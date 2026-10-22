package com.milktea.order.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.milktea.api.client.CouponClient;
import com.milktea.api.client.PointsClient;
import com.milktea.api.client.ProductClient;
import com.milktea.common.exception.BusinessException;
import com.milktea.common.result.PageResult;
import com.milktea.common.result.Result;
import com.milktea.model.dto.OrderCreateDTO;
import com.milktea.model.entity.OrderDetail;
import com.milktea.model.entity.OrderEvaluation;
import com.milktea.model.entity.Orders;
import com.milktea.model.entity.Product;
import com.milktea.model.vo.OrderVO;
import com.milktea.model.dto.OrderEvaluationDTO;
import com.milktea.order.mapper.OrderDetailMapper;
import com.milktea.order.mapper.OrderEvaluationMapper;
import com.milktea.order.mapper.OrderMapper;
import com.milktea.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    // 订单状态(数据库存储为 0-based):
    // 0=待制作  1=制作中  2=已完成  3=已取消
    // 对外 API 使用 1-based(1/2/3/4), 在 Controller 边界做转换
    private static final int DB_PENDING = 0;
    private static final int DB_MAKING = 1;
    private static final int DB_DONE = 2;
    private static final int DB_CANCELLED = 3;

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderEvaluationMapper orderEvaluationMapper;
    private final ProductClient productClient;
    private final CouponClient couponClient;
    private final PointsClient pointsClient;
    private final com.milktea.api.client.StoreClient storeClient;

    public OrderServiceImpl(OrderMapper orderMapper, OrderDetailMapper orderDetailMapper,
                            OrderEvaluationMapper orderEvaluationMapper, ProductClient productClient,
                            CouponClient couponClient, PointsClient pointsClient,
                            com.milktea.api.client.StoreClient storeClient) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.orderEvaluationMapper = orderEvaluationMapper;
        this.productClient = productClient;
        this.couponClient = couponClient;
        this.pointsClient = pointsClient;
        this.storeClient = storeClient;
    }

    @Override
    @Transactional
    public Long create(OrderCreateDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("订单明细不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetail> details = new ArrayList<>();

        for (OrderCreateDTO.OrderItemDTO item : dto.getItems()) {
            // 加分项: Feign 反查商品, 下架则拦截; 服务不可用时降级放行
            checkProductOnShelf(item.getProductId());

            BigDecimal subtotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);

            OrderDetail d = new OrderDetail();
            d.setProductId(item.getProductId());
            d.setProductName(item.getProductName());
            d.setSpecs(item.getSpecs());
            d.setPrice(item.getPrice());
            d.setQuantity(item.getQuantity());
            d.setSubtotal(subtotal);
            details.add(d);
        }

        // 等级折扣: 白银98/黄金95/钻石90, 作用于商品总额; 查询失败降级不打折(不阻断下单)
        BigDecimal levelRate = BigDecimal.ONE;
        try {
            Result<BigDecimal> dr = pointsClient.getDiscount(dto.getUserId());
            if (dr != null && dr.getCode() != null && dr.getCode() == 200 && dr.getData() != null) {
                levelRate = dr.getData();
            }
        } catch (Exception e) {
            log.warn("等级折扣查询失败, 降级不打折: userId={}", dto.getUserId(), e);
        }
        if (levelRate.compareTo(BigDecimal.ONE) < 0) {
            BigDecimal levelOff = total.multiply(BigDecimal.ONE.subtract(levelRate)).setScale(2, java.math.RoundingMode.HALF_UP);
            total = total.subtract(levelOff);
        }

        // 优惠券抵扣: Feign 调用户服务核销, 以服务端校验结果为准
        BigDecimal discount = BigDecimal.ZERO;
        if (dto.getCouponId() != null) {
            Result<BigDecimal> couponResult = couponClient.use(dto.getUserId(), dto.getCouponId(), total);
            if (couponResult == null || couponResult.getCode() == null || couponResult.getCode() != 200) {
                throw new BusinessException(couponResult == null ? "优惠券服务不可用" : couponResult.getMessage());
            }
            discount = couponResult.getData() == null ? BigDecimal.ZERO : couponResult.getData();
        }

        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(dto.getUserId());
        order.setStatus(DB_PENDING);
        order.setRemark(dto.getRemark());
        order.setCouponId(dto.getCouponId());
        order.setDiscountAmount(discount);
        order.setCreateTime(now);
        order.setUpdateTime(now);

        // 积分抵现: remark 协议 |PT:分数 (不动model的DTO), Feign调用户服务按需扣分, 抵扣额封顶到券后应付
        BigDecimal pointsDiscount = BigDecimal.ZERO;
        String remark = dto.getRemark();
        long pt = 0;
        if (remark != null) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\|PT:(\\d+)").matcher(remark);
            if (m.find()) {
                pt = Long.parseLong(m.group(1));
                order.setRemark(remark.replaceAll("\\|PT:\\d+", "")); // 存库剔除协议标记
            }
        }
        if (pt > 0) {
            BigDecimal payable = total.subtract(discount).max(BigDecimal.ZERO);
            if (payable.compareTo(BigDecimal.ZERO) > 0) {
                Result<BigDecimal> pr = pointsClient.deduct(dto.getUserId(), pt, payable, order.getOrderNo());
                if (pr == null || pr.getCode() == null || pr.getCode() != 200) {
                    throw new BusinessException(pr == null ? "积分服务不可用" : pr.getMessage());
                }
                pointsDiscount = pr.getData() == null ? BigDecimal.ZERO : pr.getData();
            }
        }

        order.setTotalAmount(total.subtract(discount).subtract(pointsDiscount).max(BigDecimal.ZERO)); // 服务端算钱, 不信前端

        // 原料自动扣减: 按配方扣store服务原料, 不足则拦截下单(整体回滚)
        try {
            java.util.List<java.util.Map<String, Object>> deductItems = new java.util.ArrayList<>();
            for (OrderDetail d : details) {
                java.util.Map<String, Object> it = new java.util.HashMap<>();
                it.put("productId", d.getProductId());
                it.put("quantity", java.math.BigDecimal.valueOf(d.getQuantity()));
                deductItems.add(it);
            }
            java.util.Map<String, Object> deductReq = new java.util.HashMap<>();
            deductReq.put("orderNo", order.getOrderNo());
            deductReq.put("items", deductItems);
            Result<java.util.List<String>> dr = storeClient.deduct(deductReq);
            if (dr == null || dr.getCode() == null || dr.getCode() != 200) {
                throw new BusinessException(dr == null ? "库存服务不可用" : dr.getMessage());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("原料扣减异常, 拦截下单以保证库存一致", e);
            throw new BusinessException("库存服务异常, 请稍后再试");
        }

        orderMapper.insert(order);             // useGeneratedKeys 回填 id

        for (OrderDetail d : details) {
            d.setOrderId(order.getId());
            orderDetailMapper.insert(d);        // 存快照
        }
        return order.getId();
    }

    /**
     * 反查商品上下架状态(分布式 Feign 调用, 加分项)
     * 商品不存在 / 已下架(1=上架) -> 抛异常; 商品服务不可用(降级返回 null) -> 放行并记录
     */
    private void checkProductOnShelf(Long productId) {
        Result<Product> result;
        try {
            result = productClient.queryById(productId);
        } catch (Exception e) {
            log.warn("反查商品失败, 降级放行: productId={}", productId, e);
            return;
        }
        // 商品服务不可用(降级)或返回空 -> 放行, 不阻断下单
        if (result == null || result.getData() == null) {
            log.warn("商品服务不可用或商品不存在, 降级放行: productId={}", productId);
            return;
        }
        Product product = result.getData();
        if (product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException("商品已下架");
        }
    }

    @Override
    public PageResult<OrderVO> page(Integer status, String keyword, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = orderMapper.selectPage(status, keyword);
        PageInfo<Orders> pageInfo = new PageInfo<>(list);
        List<OrderVO> vos = list.stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(vos, pageInfo.getTotal(), pageInfo.getPageNum(),
                pageInfo.getPageSize(), pageInfo.getPages());
    }

    @Override
    public OrderVO getById(Long id) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        OrderVO vo = toVo(order);
        List<OrderDetail> details = orderDetailMapper.selectByOrderId(id);
        vo.setDetails(details.stream().map(this::toDetailVo).collect(Collectors.toList()));
        OrderEvaluation evaluation = orderEvaluationMapper.selectByOrderId(id);
        if (evaluation != null) {
            OrderVO.OrderEvaluationVO evo = new OrderVO.OrderEvaluationVO();
            evo.setRating(evaluation.getRating());
            evo.setContent(evaluation.getContent());
            evo.setCreateTime(evaluation.getCreateTime());
            vo.setEvaluation(evo);
        }
        return vo;
    }

    @Override
    public PageResult<OrderVO> pageByUser(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = status == null
                ? orderMapper.selectByUserId(userId)
                : orderMapper.selectByUserIdAndStatus(userId, status);
        PageInfo<Orders> pageInfo = new PageInfo<>(list);
        List<OrderVO> vos = list.stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(vos, pageInfo.getTotal(), pageInfo.getPageNum(),
                pageInfo.getPageSize(), pageInfo.getPages());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer target) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        validateTransition(order.getStatus(), target);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime finishTime = (target == DB_DONE) ? now : null;
        orderMapper.updateStatus(id, target, now, finishTime);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != DB_PENDING) {
            throw new BusinessException("仅待制作状态的订单可以取消");
        }
        orderMapper.updateStatus(id, DB_CANCELLED, LocalDateTime.now(), null);
        // 取消订单回退优惠券
        if (order.getCouponId() != null) {
            couponClient.restore(order.getUserId(), order.getCouponId());
        }
    }

    @Override
    @Transactional
    public void evaluate(OrderEvaluationDTO dto) {
        Orders order = orderMapper.selectById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != DB_DONE) {
            throw new BusinessException("仅已完成订单可以评价");
        }
        if (orderEvaluationMapper.selectByOrderId(dto.getOrderId()) != null) {
            throw new BusinessException("该订单已评价");
        }
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BusinessException("评分需在 1-5 星之间");
        }
        OrderEvaluation e = new OrderEvaluation();
        e.setOrderId(dto.getOrderId());
        e.setUserId(dto.getUserId());
        e.setRating(dto.getRating());
        e.setContent(dto.getContent());
        e.setCreateTime(LocalDateTime.now());
        orderEvaluationMapper.insert(e);
    }

    // ===== 状态机校验(数据库 0-based) =====
    // 0(待制作) -> 1(制作中) / 3(已取消)
    // 1(制作中) -> 2(已完成)
    // 2(已完成) / 3(已取消) 为终态
    private void validateTransition(int current, int target) {
        if (current == target) {
            return; // 幂等, 放行
        }
        switch (current) {
            case DB_PENDING:
                if (target == DB_MAKING || target == DB_CANCELLED) return;
                break;
            case DB_MAKING:
                if (target == DB_DONE) return;
                break;
            default:
                break;
        }
        throw new BusinessException("非法的状态变更");
    }

    // ===== 组装 VO(数据库 0-based -> 对外 1-based) =====
    private OrderVO toVo(Orders o) {
        OrderVO vo = new OrderVO();
        vo.setId(o.getId());
        vo.setOrderNo(o.getOrderNo());
        vo.setUserId(o.getUserId());
        vo.setTotalAmount(o.getTotalAmount());
        vo.setDiscountAmount(o.getDiscountAmount());
        vo.setStatus(o.getStatus() == null ? null : o.getStatus() + 1);
        vo.setRemark(o.getRemark());
        vo.setCreateTime(o.getCreateTime());
        vo.setCompleteTime(o.getCompleteTime());
        return vo;
    }

    private OrderVO.OrderDetailVO toDetailVo(OrderDetail d) {
        OrderVO.OrderDetailVO v = new OrderVO.OrderDetailVO();
        v.setId(d.getId());
        v.setProductId(d.getProductId());
        v.setProductName(d.getProductName());
        v.setSpecs(d.getSpecs());
        v.setPrice(d.getPrice());
        v.setQuantity(d.getQuantity());
        v.setSubtotal(d.getSubtotal());
        return v;
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String rand = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return time + rand;
    }
}
