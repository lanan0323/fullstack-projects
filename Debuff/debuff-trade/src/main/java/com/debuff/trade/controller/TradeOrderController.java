// 声明当前类所属的包路径，位于trade模块的controller层
package com.debuff.trade.controller;

// ===== 以下为Java标准库和Spring框架相关的导入 =====
// 导入Java集合框架的List接口，用于接收查询结果列表
import java.util.List;
// ===== 以下为Spring框架核心注解的导入 =====
// 导入@Autowired注解，用于自动注入依赖的Bean
import org.springframework.beans.factory.annotation.Autowired;
// 导入@GetMapping注解，用于映射HTTP GET请求
import org.springframework.web.bind.annotation.GetMapping;
// 导入@PathVariable注解，用于从URL路径中提取参数
import org.springframework.web.bind.annotation.PathVariable;
// 导入@PostMapping注解，用于映射HTTP POST请求
import org.springframework.web.bind.annotation.PostMapping;
// 导入@RequestMapping注解，用于定义控制器的基础请求路径
import org.springframework.web.bind.annotation.RequestMapping;
// 导入@RequestParam注解，用于接收URL查询参数
import org.springframework.web.bind.annotation.RequestParam;
// 导入@RestController注解，标记该类为RESTful控制器，返回值自动序列化为JSON
import org.springframework.web.bind.annotation.RestController;
// ===== 以下为项目公共模块（common）的导入 =====
// 导入若依框架的基础控制器类，提供分页、响应封装等通用方法
import com.debuff.common.core.controller.BaseController;
// 导入AjaxResult统一响应对象，用于封装单条数据的返回结果
import com.debuff.common.core.domain.AjaxResult;
// 导入TableDataInfo分页响应对象，包含rows和total字段，供前端表格组件使用
import com.debuff.common.core.page.TableDataInfo;
// 导入业务异常类，用于在业务逻辑出错时抛出异常
import com.debuff.common.exception.ServiceException;
// 导入Spring Security工具类，用于获取当前登录用户的信息
import com.debuff.common.utils.SecurityUtils;
// ===== 以下为trade模块自身的领域对象和服务导入 =====
// 导入订单实体类，对应数据库中的订单表
import com.debuff.trade.domain.Order;
// 导入订单Service接口，定义订单相关的业务方法
import com.debuff.trade.service.IOrderService;


// 标记为REST控制器，Spring会自动将其注册为Bean并序列化返回值为JSON
@RestController
// 指定该控制器的基础路径，所有C端订单接口都以 /api/order 开头
@RequestMapping("/api/order")
// 定义控制器类并继承BaseController，获得分页、响应封装等通用能力
public class TradeOrderController extends BaseController
{
    // 自动注入订单Service实例，用于调用订单相关的业务逻辑方法
    @Autowired
    private IOrderService orderService;

    /**
     * 用户下单购买
     *
     * <p>前端在市场详情页点击"购买"按钮时调用，传入上架记录ID（listingId）。
     * 买家ID从当前登录用户获取，不接收前端传参（安全考虑）。</p>
     *
     * @param listingId 上架ID（市场详情页的在售列表项）
     * @return 生成的订单信息
     */
    // 映射POST请求到 /api/order/buy/{listingId}，用于用户下单购买
    @PostMapping("/buy/{listingId}")
    // 定义buy方法，接收路径中的listingId参数，返回封装后的AjaxResult
    public AjaxResult buy(@PathVariable Long listingId)
    {
        // SecurityUtils.getUserId() 从 Spring Security 的认证信息中获取当前用户ID
        // 这个值是服务端从 Token 中解析的，不可被前端伪造
        // 调用Service层创建订单，传入上架ID和当前登录用户ID作为买家
        Order order = orderService.createOrder(listingId, SecurityUtils.getUserId());
        // 将创建成功的订单对象包装为AjaxResult返回给前端
        return success(order);
    }

    /**
     * 买家确认收货
     *
     * <p>买家收到饰品后点击"确认收货"，触发资金结算和饰品转移。
     * 调用前会通过 checkBuyer() 校验当前用户是否为该订单的买家。</p>
     *
     * @param orderId 订单ID
     * @return 操作结果
     */
    // 映射POST请求到 /api/order/confirm/{orderId}，用于买家确认收货
    @PostMapping("/confirm/{orderId}")
    // 定义confirm方法，接收路径中的订单ID，返回操作是否成功
    public AjaxResult confirm(@PathVariable Long orderId)
    {
        // 先校验订单归属，确认是当前用户的订单才允许操作
        checkBuyer(orderId);
        // 调用Service层执行确认收货逻辑（结算资金、转移饰品），并将结果转为AjaxResult返回
        return toAjax(orderService.confirmOrder(orderId));
    }

    /**
     * 取消订单（买家手动取消 / 定时任务超时自动取消 共用同一 Service 方法）
     *
     * <p>取消后触发逆向操作：退款、库存恢复、上架恢复。
     * 定时任务内部也调用 orderService.cancelOrder()，不经过此 Controller。</p>
     *
     * @param orderId 订单ID
     * @return 操作结果
     */
    // 映射POST请求到 /api/order/cancel/{orderId}，用于取消订单
    @PostMapping("/cancel/{orderId}")
    // 定义cancel方法，接收路径中的订单ID，返回操作是否成功
    public AjaxResult cancel(@PathVariable Long orderId)
    {
        // 先校验订单归属，防止用户取消不属于自己的订单
        checkBuyer(orderId);
        // 调用Service层执行取消订单逻辑（退款、恢复库存、恢复上架），并将结果转为AjaxResult返回
        return toAjax(orderService.cancelOrder(orderId));
    }

    /**
     * 我的交易列表（C端"我的订单"页面）
     *
     * <p>支持按状态和方向筛选：
     * status=0/1/2 分别对应待确认/已完成/已取消；
     * direction=1 查我买的，direction=2 查我卖的，不传查全部。</p>
     *
     * @param status    订单状态（可选）
     * @param direction 方向（可选：1我买的 2我卖的）
     * @return 分页订单列表
     */
    // 映射GET请求到 /api/order/my，用于查询当前用户的订单列表
    @GetMapping("/my")
    // 定义my方法，接收可选的状态和方向筛选参数，返回分页数据
    public TableDataInfo my(@RequestParam(required = false) Integer status, @RequestParam(required = false) Integer direction)
    {
        //分页
        startPage();
        // 调用Service层查询当前用户的订单列表，传入用户ID、状态和方向作为筛选条件
        List<Order> list = orderService.selectMyOrders(SecurityUtils.getUserId(), status, direction);
        // 将查询结果列表包装为TableDataInfo返回，包含rows和total供前端表格使用
        return getDataTable(list);
    }

    /**
     * 校验订单买家是当前登录用户（防止越权操作他人订单）
     *
     * <p>场景：用户A通过构造请求传入用户B的 orderId，试图确认/取消B的订单。
     * 此方法查出订单后比对 buyerId 与当前登录用户ID，不一致则抛异常拦截。
     * 这是接口安全的基本保障——"谁下的单谁才能操作"。</p>
     *
     * @param orderId 订单ID
     * @throws ServiceException 订单不存在或当前用户不是买家时抛出
     */
    // 定义私有方法checkBuyer，用于校验当前用户是否为订单的买家
    private void checkBuyer(Long orderId)
    {
        // 根据订单ID从数据库查询订单记录
        Order order = orderService.selectOrderById(orderId);
        // 判断订单是否存在，不存在则直接抛出异常
        if (order == null)
        {
            // 抛出业务异常，提示"订单不存在"，前端会收到错误信息
            throw new ServiceException("订单不存在");
        }
        // 核心校验：订单的买家ID必须等于当前登录用户ID
        // 比对订单的buyerId和当前登录用户ID，不一致说明是越权操作
        if (!order.getBuyerId().equals(SecurityUtils.getUserId()))
        {
            // 抛出业务异常，提示"无权操作该订单"，阻止越权访问
            throw new ServiceException("无权操作该订单");
        }
    }
}
