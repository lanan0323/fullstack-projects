package com.milktea.user.service.impl;

import com.milktea.common.context.UserContext;
import com.milktea.common.exception.BusinessException;
import com.milktea.model.entity.PointsAccount;
import com.milktea.model.entity.PointsFlow;
import com.milktea.user.mapper.PointsMapper;
import com.milktea.user.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private PointsMapper pointsMapper;

    private org.springframework.web.client.RestTemplate restTemplate;

    @Override
    public Map<String, Object> myPoints() {
        settleCompletedOrders(UserContext.getCurrentUserId());
        PointsAccount account = getOrCreate(UserContext.getCurrentUserId(), 0);
        checkLevelGift(UserContext.getCurrentUserId(), account.getPoints());
        Map<String, Object> result = new HashMap<>();
        result.put("points", account.getPoints());
        return result;
    }

    @Override
    @Transactional
    public void earn(Long userId, long points, String remark) {
        if (userId == null || points <= 0) throw new BusinessException("积分参数不合法");
        // 建档初始值为0, 积分统一走addPoints, 避免双倍记账
        getOrCreate(userId, 0);
        pointsMapper.addPoints(userId, points);
        recordFlow(userId, 1, points, remark);
        checkLevelGift(userId, pointsMapper.findByUserId(userId).getPoints());
    }

    @Override
    public List<PointsFlow> flows(int limit) {
        if (limit <= 0 || limit > 100) limit = 50;
        settleCompletedOrders(UserContext.getCurrentUserId());
        return pointsMapper.findFlows(UserContext.getCurrentUserId(), limit);
    }

    // 只读调用订单服务(C组员代码零改动), 惰性结算已完成订单; 订单服务不可用时静默跳过
    @Override
    @Transactional
    public int settleCompletedOrders(Long userId) {
        List<com.milktea.model.vo.OrderVO> completed = fetchCompletedOrders(userId);
        if (completed == null || completed.isEmpty()) return 0;
        int earned = 0;
        for (com.milktea.model.vo.OrderVO order : completed) {
            if (pointsMapper.countSettled(order.getId()) > 0) continue;
            // 订单服务口径: totalAmount=券后应付金额(即实付), 1元=1积分向下取整
            long p = order.getTotalAmount() == null ? 0 : Math.max(0, order.getTotalAmount().longValue());
            pointsMapper.insertSettle(order.getId(), userId, p, order.getOrderNo());
            if (p > 0) {
                getOrCreate(userId, 0);
                pointsMapper.addPoints(userId, p);
                recordFlow(userId, 1, p, "订单消费 " + order.getOrderNo());
                earned += p;
            }
        }
        return earned;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<com.milktea.model.vo.OrderVO> fetchCompletedOrders(Long userId) {
        try {
            if (restTemplate == null) restTemplate = new org.springframework.web.client.RestTemplate();
            org.springframework.http.ResponseEntity<com.milktea.common.result.Result> resp = restTemplate.getForEntity(
                    "http://localhost:8083/order/user/" + userId + "?status=3&pageNum=1&pageSize=50",
                    com.milktea.common.result.Result.class);
            com.milktea.common.result.Result result = resp.getBody();
            if (result == null || result.getData() == null) return List.of();
            Object data = result.getData();
            if (data instanceof java.util.Map<?, ?> page) {
                Object list = page.get("list");
                if (list == null) return List.of();
                // 转成OrderVO列表
                com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                om.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                om.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                return om.convertValue(list, om.getTypeFactory().constructCollectionType(List.class, com.milktea.model.vo.OrderVO.class));
            }
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    private PointsAccount getOrCreate(Long userId, long initialPoints) {
        PointsAccount account = pointsMapper.findByUserId(userId);
        if (account == null) {
            account = new PointsAccount();
            account.setUserId(userId);
            account.setPoints(initialPoints);
            pointsMapper.insert(account);
        }
        return account;
    }

    private void recordFlow(Long userId, int type, long points, String remark) {
        PointsFlow flow = new PointsFlow();
        flow.setUserId(userId);
        flow.setType(type);
        flow.setPoints(points);
        flow.setRemark(remark);
        pointsMapper.insertFlow(flow);
    }

    private static final long POINTS_PER_YUAN = 100;

    /** 等级门槛: 索引即等级序号(0普通/1白银/2黄金/3钻石), 与前端展示口径一致 */
    private static final long[] LEVEL_MINS = {0, 500, 2000, 5000};
    private static final String[] LEVEL_NAMES = {"普通会员", "白银会员", "黄金会员", "钻石会员"};
    private static final String[] GIFT_COUPON_NAMES = {null, "[等级]白银礼券", "[等级]黄金礼券", "[等级]钻石礼券"};

    @Autowired
    private com.milktea.user.mapper.CouponMapper couponMapper;

    @Autowired
    private com.milktea.user.service.CouponService couponService;

    /**
     * 升级礼包检测: 首次达到白银/黄金/钻石时自动发放对应礼券(幂等, 以积分流水为凭)
     */
    private void checkLevelGift(Long userId, long points) {
        if (userId == null) return;
        for (int lv = 1; lv < LEVEL_MINS.length; lv++) {
            if (points < LEVEL_MINS[lv]) break;
            String flowMark = LEVEL_NAMES[lv] + "升级礼包";
            if (pointsMapper.countFlowByRemark(userId, flowMark) > 0) continue;
            Long couponId = couponMapper.selectIdByName(GIFT_COUPON_NAMES[lv]);
            if (couponId == null) continue; // 券模板缺失则跳过, 不阻断
            try {
                couponService.issue(userId, couponId);
                recordFlow(userId, 1, 0, flowMark);
            } catch (Exception e) {
                // 已有同款未使用券等情况: 也记流水防止每次查询反复尝试发放
                recordFlow(userId, 1, 0, flowMark);
            }
        }
    }

    @Override
    public java.math.BigDecimal getDiscount(Long userId) {
        if (userId == null) return java.math.BigDecimal.ONE;
        PointsAccount account = pointsMapper.findByUserId(userId);
        long p = account == null || account.getPoints() == null ? 0 : account.getPoints();
        // 注意: 此处为下单链路, 不触发settleCompletedOrders(避免回调订单服务造成循环依赖)
        if (p >= LEVEL_MINS[3]) return new java.math.BigDecimal("0.90");
        if (p >= LEVEL_MINS[2]) return new java.math.BigDecimal("0.95");
        if (p >= LEVEL_MINS[1]) return new java.math.BigDecimal("0.98");
        return java.math.BigDecimal.ONE;
    }

    @Override
    @Transactional
    public java.math.BigDecimal deductForOrder(Long userId, long points, java.math.BigDecimal orderAmount, String orderNo) {
        if (userId == null || points <= 0 || orderAmount == null || orderAmount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return java.math.BigDecimal.ZERO;
        }
        // 100分=1元; 只用整百分, 且抵扣额封顶到订单应付金额
        long maxByMoney = orderAmount.multiply(java.math.BigDecimal.valueOf(POINTS_PER_YUAN)).longValue();
        long usable = Math.min(points / POINTS_PER_YUAN * POINTS_PER_YUAN, maxByMoney / POINTS_PER_YUAN * POINTS_PER_YUAN);
        if (usable <= 0) return java.math.BigDecimal.ZERO;
        getOrCreate(userId, 0);
        if (pointsMapper.deductPoints(userId, usable) == 0) {
            throw new BusinessException("积分余额不足");
        }
        recordFlow(userId, 2, usable, "下单积分抵扣" + (orderNo == null ? "" : " " + orderNo));
        return java.math.BigDecimal.valueOf(usable, 2);
    }
}
