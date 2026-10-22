package com.milktea.order.service;

import com.milktea.common.result.PageResult;
import com.milktea.model.dto.OrderCreateDTO;
import com.milktea.model.vo.OrderVO;
import com.milktea.model.dto.OrderEvaluationDTO;

public interface OrderService {

    /** 下单核心(服务端算钱 + 存快照 + 状态机) */
    Long create(OrderCreateDTO dto);

    /** 管理端分页: status 为数据库 0-based 值 */
    PageResult<OrderVO> page(Integer status, String keyword, Integer pageNum, Integer pageSize);

    /** 订单详情(含明细 + 评价) */
    OrderVO getById(Long id);

    /** 顾客"我的订单"分页, status 为数据库 0-based 值, 可为空(查全部) */
    PageResult<OrderVO> pageByUser(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /** 员工状态流转: target 为数据库 0-based 值 */
    void updateStatus(Long id, Integer target);

    /** 顾客取消: 仅待制作可取消 */
    void cancel(Long id);

    /** 评价: 仅已完成可评价 */
    void evaluate(OrderEvaluationDTO dto);
}
