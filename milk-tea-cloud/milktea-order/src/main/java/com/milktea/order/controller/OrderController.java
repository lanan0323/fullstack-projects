package com.milktea.order.controller;

import com.milktea.common.exception.BusinessException;
import com.milktea.common.result.PageResult;
import com.milktea.common.result.Result;
import com.milktea.model.dto.OrderCreateDTO;
import com.milktea.model.vo.OrderVO;
import com.milktea.model.dto.OrderEvaluationDTO;
import com.milktea.order.mapper.OrderDetailMapper;
import com.milktea.order.mapper.OrderMapper;
import com.milktea.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单服务接口(成员C)
 * 状态约定: 对外 API 使用 1-based(1待制作/2制作中/3已完成/4已取消),
 * 数据库存储 0-based(0/1/2/3)。入参在边界处转 db: db = api - 1
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private int toDb(int apiStatus) {
        return apiStatus - 1;
    }

    /** 内部统计接口(store服务Feign调用): 今日概况 + 营业额趋势 + 销量排行 */
    @GetMapping("/stats/internal")
    public Result<java.util.Map<String, Object>> statsInternal(@RequestParam(defaultValue = "7") Integer days,
                                                               @RequestParam(defaultValue = "5") Integer rankLimit) {
        java.util.Map<String, Object> m = new java.util.HashMap<>();
        m.put("todayOrderCount", orderMapper.countToday());
        m.put("todayRevenue", orderMapper.sumTodayRevenue());
        m.put("trend", orderMapper.revenueTrend(days));
        m.put("rank", orderDetailMapper.salesRank(rankLimit));
        return Result.success(m);
    }

    /** 下单 */
    @PostMapping
    public Result<Long> create(@RequestBody OrderCreateDTO dto) {
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("订单明细不能为空");
        }
        Long id = orderService.create(dto);
        return Result.success(id);
    }

    /** 管理端分页(status 为对外 1-based) */
    @GetMapping("/page")
    public Result<PageResult<OrderVO>> page(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Integer dbStatus = (status != null) ? toDb(status) : null;
        return Result.success(orderService.page(dbStatus, keyword, pageNum, pageSize));
    }

    /** 订单详情 */
    @GetMapping("/{id}")
    public Result<OrderVO> get(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    /** 顾客"我的订单"分页 */
    @GetMapping("/user/{userId}")
    public Result<PageResult<OrderVO>> myOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(orderService.pageByUser(userId, status != null ? toDb(status) : null, pageNum, pageSize));
    }

    /** 状态流转: 接单(2)/完成(3)/取消(4), 对外 1-based */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        orderService.updateStatus(id, toDb(status));
        return Result.success();
    }

    /** 顾客取消(仅待制作, 对外 1-based=4) */
    @DeleteMapping("/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return Result.success();
    }

    /** 评价订单 */
    @PostMapping("/evaluation")
    public Result<Void> evaluate(@RequestBody OrderEvaluationDTO dto) {
        orderService.evaluate(dto);
        return Result.success();
    }
}
