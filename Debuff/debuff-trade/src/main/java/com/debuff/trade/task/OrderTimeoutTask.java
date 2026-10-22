// 声明当前类所在的包路径，属于 trade 模块下的 task 包
package com.debuff.trade.task;

// 导入Java集合框架中的List接口，用于接收订单列表
import java.util.List;

// 导入SLF4J日志框架的Logger接口
import org.slf4j.Logger;
// 导入SLF4J日志工厂类，用于创建Logger实例
import org.slf4j.LoggerFactory;
// 导入Spring的@Autowired注解，用于自动注入依赖
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的@Value注解，用于从配置文件中读取属性值
import org.springframework.beans.factory.annotation.Value;
// 导入Spring的@Scheduled注解，用于定义定时任务
import org.springframework.scheduling.annotation.Scheduled;
// 导入Spring的@Component注解，用于将类注册为Spring Bean
import org.springframework.stereotype.Component;

// 导入项目自定义的业务异常类
import com.debuff.common.exception.ServiceException;
// 导入订单实体类，对应数据库中的订单表
import com.debuff.trade.domain.Order;
// 导入订单服务接口，提供订单相关的业务方法
import com.debuff.trade.service.IOrderService;
// 导入Spring的事务注解（本文件中未直接使用，但cancelOrder内部依赖事务）
import org.springframework.transaction.annotation.Transactional;

// 导入Swing工具包（可能是IDE自动生成的无用导入）
import javax.swing.*;

/**
 * 订单超时自动取消定时任务
 *
 * <p>业务场景：买家下单后如果一直不确认收货，商品和资金会一直处于冻结状态。
 * 此定时任务定期扫描"待确认"订单，将超过配置时间未确认的订单自动取消，
 * 释放冻结的资金和库存，保证平台数据不会永远卡在中间态。</p>
 *
 * <h3>设计要点</h3>
 * <ul>
 *   <li>复用 cancelOrder 的完整回滚链路：买家退款、库存解冻恢复在售、上架恢复在售。
 *       不单独写取消逻辑，避免代码重复和逻辑不一致。</li>
 *   <li>每单独立 try-catch：一个订单取消失败（如并发冲突）不影响其他订单的处理。
 *       外层 try-catch 兜底数据库连接异常等意外错误，保证定时任务线程不会因异常终止。</li>
 *   <li>条件更新兜底：cancelOrder 内部用 WHERE status=0 条件更新，
 *       如果用户刚好在定时任务执行前手动确认/取消了订单，updateStatus 返回0行，
 *       cancelOrder 抛 ServiceException，被内层 catch 捕获后跳过，不影响其他订单。</li>
 * </ul>
 *
 * <h3>【答辩要点】如果一次任务执行时间超过60秒（比如有1000个超时订单要取消），会怎样？</h3>
 * <p>fixedDelay 是"上一次执行<b>完成后</b>间隔60秒再执行"，所以不会出现任务重叠。
 * 如果本次执行了120秒，下次会在120秒+60秒=180秒后才开始。
 * 如果用 fixedRate 就会有问题：fixedRate=60000 表示每60秒触发一次，
 * 如果上一次还没执行完，下一次会被排队等待（Spring 默认单线程调度），
 * 可能导致任务堆积。所以这里用 fixedDelay 更安全。</p>
 *
 * <h3>【答辩要点】为什么不用 @Scheduled(cron = "0 */
// @Component 注解：将此类的实例注册到Spring容器中，由Spring容器管理生命周期
@Component
// 定义订单超时自动取消定时任务类
public class OrderTimeoutTask
{
    // 声明一个静态常量Logger，用于记录本类的日志信息
    // LoggerFactory.getLogger 传入当前类，日志输出时会带上类名
    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutTask.class);

    /**
     * 订单超时分钟数（创建后未确认即自动取消）
     * 可通过配置文件 application.yml 中的 debuff.order.timeout-minutes 修改
     * 冒号后面的 15 是默认值，如果配置文件中没设置就用15分钟
     */
    // @Value 注解：从配置文件中读取 debuff.order.timeout-minutes 的值，默认15
    @Value("${debuff.order.timeout-minutes:15}")
    // 声明私有成员变量 timeoutMinutes，存储订单超时时间（单位：分钟）
    private int timeoutMinutes;

    // @Autowired 注解：自动注入IOrderService的实现类实例
    @Autowired
    // 声明私有成员变量 orderService，用于调用订单相关的业务方法
    private IOrderService orderService;

    /**
     * 每 60 秒扫描一次超时未确认的订单并自动取消
     *
     * <p>@Scheduled(fixedDelay = 60000) 说明：
     * fixedDelay = 上一次执行【完成后】间隔60秒再执行下一次（保证不会重叠执行）
     * 对比 fixedRate = 上一次执行【开始后】间隔N秒（可能重叠）
     * 对比 cron = 按 Cron 表达式定时（如 "0 0/5 * * * ?" 每5分钟）
     * 这里用 fixedDelay 是因为取消操作可能耗时，用 delay 模式更安全。</p>
     */
    // @Scheduled 注解：定义定时任务，fixedDelay=60000 表示上次执行完成后间隔60秒再执行
    @Scheduled(fixedDelay = 60000)
    // 定义自动取消超时订单的方法，无返回值，由Spring定时调度器自动调用
    public void autoCancelTimeoutOrders()
    {
        // 外层 try-catch：兜底意外异常（如数据库连接断开）
        // 如果不 catch，Spring 的调度线程池会因未捕获异常而停止后续调度
        try
        {
            // 查询所有超时的待确认订单（status=0 且 create_time 早于 当前时间-timeoutMinutes）
            // 调用 orderService 的 selectTimeoutOrders 方法，传入超时分钟数
            List<Order> timeoutOrders = orderService.selectTimeoutOrders(timeoutMinutes);
            // 判断查询结果是否为null或空集合，即没有超时的订单
            if (timeoutOrders == null || timeoutOrders.isEmpty())
            {
                return; // 没有超时订单，直接返回，不打日志（避免日志刷屏）
            }
            // 声明计数器变量 cancelled，记录本次成功取消的订单数量，初始值为0
            int cancelled = 0;
            // 遍历所有超时订单，逐个尝试取消
            for (Order order : timeoutOrders)
            {
                // 内层 try-catch：每个订单独立捕获异常，一个失败不影响其他订单
                try
                {
                    // 复用 cancelOrder 的完整取消逻辑（退款+库存恢复+上架恢复）
                    // 传入订单ID，cancelOrder 内部会执行退款、库存恢复、上架恢复等操作
                    orderService.cancelOrder(order.getId());
                    // 取消成功，计数器加1
                    cancelled++;
                    // 记录info级别日志，输出被自动取消的订单详细信息（订单ID、订单号、买家ID、卖家ID）
                    log.info("[订单超时] 自动取消订单 orderId={} orderNo={} buyerId={} sellerId={}",
                            order.getId(), order.getOrderNo(), order.getBuyerId(), order.getSellerId());
                }
                // 内层 catch：只捕获 ServiceException（业务异常）
                catch (ServiceException e)
                {
                    // 内层 catch：只捕获 ServiceException（业务异常）
                    // 场景：订单已被用户手动确认/取消，cancelOrder 中条件更新返回0行抛异常
                    // 这是正常的并发情况，不需要告警，只记录 info 日志
                    // 记录info日志，说明该订单已不是待确认状态（可能被用户手动操作），跳过处理
                    log.info("[订单超时] 订单 orderId={} 已不是待确认状态，跳过自动取消：{}", order.getId(), e.getMessage());
                }
            }
            // 判断是否有成功取消的订单（cancelled > 0 表示至少取消了一个）
            if (cancelled > 0)
            {
                // 记录info日志，输出本次扫描成功取消的订单总数
                log.info("[订单超时] 本次扫描共自动取消 {} 个超时订单", cancelled);
            }
        }
        // 外层 catch：捕获所有非 ServiceException 的异常（如数据库连接异常、空指针等）
        catch (Exception e)
        {
            // 外层 catch：捕获所有非 ServiceException 的异常（如数据库连接异常、空指针等）
            // 记录 error 日志（含堆栈），方便排查问题
            // 不抛出，保证定时任务线程继续运行，下次调度仍能正常执行
            // 记录error级别日志，输出异常信息和完整堆栈，便于排查问题
            log.error("[订单超时] 自动取消任务执行异常", e);
        }
    }
}
