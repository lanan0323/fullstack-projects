// 声明当前类所属的包路径，位于trade模块的controller/admin子包下（管理端专用）
package com.debuff.trade.controller.admin;

// ===== 以下为Java标准库相关的导入 =====
// 导入Java集合框架的List接口，用于接收查询结果列表
import java.util.List;
// 导入Java集合框架的Map接口，用于接收总览统计的键值对数据
import java.util.Map;
// ===== 以下为Spring框架核心注解的导入 =====
// 导入@Autowired注解，用于自动注入依赖的Bean
import org.springframework.beans.factory.annotation.Autowired;
// 导入@PreAuthorize注解，用于方法级别的权限校验
import org.springframework.security.access.prepost.PreAuthorize;
// 导入@GetMapping注解，用于映射HTTP GET请求
import org.springframework.web.bind.annotation.GetMapping;
// 导入@RequestMapping注解，用于定义控制器的基础请求路径
import org.springframework.web.bind.annotation.RequestMapping;
// 导入@RequestParam注解，用于接收URL查询参数
import org.springframework.web.bind.annotation.RequestParam;
// 导入@RestController注解，标记该类为RESTful控制器，返回值自动序列化为JSON
import org.springframework.web.bind.annotation.RestController;
// ===== 以下为项目公共模块（common）的导入 =====
// 导入若依框架的基础控制器类，提供响应封装等通用方法
import com.debuff.common.core.controller.BaseController;
// 导入AjaxResult统一响应对象，用于封装返回结果
import com.debuff.common.core.domain.AjaxResult;
// ===== 以下为trade模块自身的Mapper导入 =====
// 导入管理端统计Mapper接口，直接调用SQL查询获取统计数据
import com.debuff.trade.mapper.AdminStatsMapper;

/**
 * 数据看板统计控制器（管理端首页 Dashboard）
 *
 * <p>路径前缀 /api/admin/stats，为管理端首页的 ECharts 图表和统计卡片提供数据接口。
 * 共4个接口：总览统计、订单趋势折线图、成交TOP柱状图、订单状态饼图。</p>
 *
 * <h3>设计说明</h3>
 * <ul>
 *   <li>直接注入 AdminStatsMapper 而非通过 Service 层，因为统计接口只有查询逻辑，
 *       没有复杂的业务处理，加一层 Service 反而增加代码量</li>
 *   <li>所有接口共用 admin:skin:list 权限（有饰品管理权限的管理员就能看看板）</li>
 *   <li>前端使用 Promise.all 并发请求4个接口，提升页面加载速度</li>
 * </ul>
 *
 * <h3>【答辩要点】为什么直接注入 Mapper 而跳过 Service 层？这样做好不好？</h3>
 * <p>统计接口只有简单的 SELECT 查询，没有事务、没有业务校验、没有多步操作，
 * 如果写 IAdminStatsService + AdminStatsServiceImpl 再 @Autowired 注入，
 * 只是多了一层"传话筒"，没有实际价值。对于纯查询/统计类接口，Controller 直接调 Mapper 是合理的简化。
 * 但如果后续统计逻辑变复杂（比如要加缓存、多步聚合计算），就应该抽 Service 层了。
 * 这是一种务实的权衡：简单场景减少代码量，复杂场景再重构。</p>
 *
 * <h3>【答辩要点】@PreAuthorize("@ss.hasPermi('admin:skin:list')") 是什么意思？</h3>
 * <p>@ss 是若依框架在 Spring 容器中注册的权限校验 Bean 的名字（Service Security 的缩写），
 * hasPermi() 方法检查当前登录用户是否拥有 'admin:skin:list' 这个权限标识。
 * 底层流程：Spring Security 过滤器拦截请求 → 从 Token 解析用户信息 → 查数据库获取该用户的权限列表
 * → 判断是否包含指定权限 → 不包含则返回403。权限数据存在 sys_role_menu 关联表中，
 * 管理员在"角色管理"页面给角色分配菜单时，就同时分配了权限标识。</p>
 *
 * @author debuff
 */
// 标记为REST控制器，Spring会自动将其注册为Bean并序列化返回值为JSON
@RestController
// 指定该控制器的基础路径，所有统计接口都以 /api/admin/stats 开头
@RequestMapping("/api/admin/stats")
// 定义控制器类并继承BaseController，获得响应封装等通用能力
public class AdminStatsController extends BaseController
{
    // 自动注入统计Mapper实例，直接调用SQL查询获取看板数据（跳过Service层）
    @Autowired
    private AdminStatsMapper adminStatsMapper;

    /**
     * 总览统计（Dashboard 顶部的数字卡片）
     *
     * <p>返回一个 HashMap，包含7个统计指标：
     * user_count(注册用户数)、skin_count(饰品总数)、market_count(在售商品数)、
     * order_count(订单总数)、order_amount(成交总金额)、today_order_count(今日订单数)、
     * wallet_balance(钱包总余额)。</p>
     *
     * <p>SQL 使用7个子查询分别统计，一次请求返回所有指标，减少网络往返。</p>
     */
    // 权限校验：当前用户必须拥有 admin:skin:list 权限才能访问看板
    @PreAuthorize("@ss.hasPermi('admin:skin:list')")
    // 映射GET请求到 /api/admin/stats/overview，用于获取总览统计数据
    @GetMapping("/overview")
    // 定义overview方法，无参数，返回包含7个统计指标的AjaxResult
    public AjaxResult overview()
    {
        // 调用Mapper层的selectOverview方法查询总览数据，包装为AjaxResult返回
        return success(adminStatsMapper.selectOverview());
    }

    /**
     * 近 N 天订单趋势（Dashboard 折线图）
     *
     * <p>返回每天的订单数量和成交金额，用于绘制双Y轴折图。
     * 默认查近7天，前端可传 ?days=30 查看近30天。
     * SQL 按 date(create_time) 分组，SUM(CASE WHEN status=1) 只统计已完成的订单金额。</p>
     *
     * @param days 统计天数，默认7
     * @return 每天的 {day, order_count, amount} 列表
     */
    // 权限校验：当前用户必须拥有 admin:skin:list 权限才能访问
    @PreAuthorize("@ss.hasPermi('admin:skin:list')")
    // 映射GET请求到 /api/admin/stats/orderTrend，用于获取订单趋势数据
    @GetMapping("/orderTrend")
    // 定义orderTrend方法，接收天数参数（默认7天），返回趋势数据列表
    public AjaxResult orderTrend(@RequestParam(defaultValue = "7") int days)
    {
        // 调用Mapper层根据天数查询订单趋势，包装为AjaxResult返回给前端绘制折线图
        return success(adminStatsMapper.selectOrderTrend(days));
    }

    /**
     * 成交金额 TOP N（Dashboard 柱状图）
     *
     * <p>统计已完成订单中，哪些饰品的总成交金额最高。
     * SQL 通过 LEFT JOIN debuff_skin 获取饰品名称，
     * GROUP BY skin_id 聚合，ORDER BY amount DESC 取前N名。
     * 默认 TOP 5，前端可传 ?limit=10 查看 TOP 10。</p>
     *
     * @param limit 返回条数，默认5
     * @return 每件饰品的 {skin_name, amount, order_count} 列表
     */
    // 权限校验：当前用户必须拥有 admin:skin:list 权限才能访问
    @PreAuthorize("@ss.hasPermi('admin:skin:list')")
    // 映射GET请求到 /api/admin/stats/topSkins，用于获取成交TOP饰品数据
    @GetMapping("/topSkins")
    // 定义topSkins方法，接收limit参数（默认5），返回TOP N饰品成交数据
    public AjaxResult topSkins(@RequestParam(defaultValue = "5") int limit)
    {
        // 调用Mapper层根据limit查询成交金额TOP N的饰品，包装为AjaxResult返回给前端绘制柱状图
        return success(adminStatsMapper.selectTopSkins(limit));
    }

    /**
     * 订单状态分布（Dashboard 饼图）
     *
     * <p>统计各状态（0待确认/1已完成/2已取消）的订单数量，用于绘制饼图。
     * SQL 简单：SELECT status, COUNT(*) GROUP BY status。</p>
     *
     * @return 各状态的 {status, count} 列表
     */
    // 权限校验：当前用户必须拥有 admin:skin:list 权限才能访问
    @PreAuthorize("@ss.hasPermi('admin:skin:list')")
    // 映射GET请求到 /api/admin/stats/statusDist，用于获取订单状态分布数据
    @GetMapping("/statusDist")
    // 定义statusDist方法，无参数，返回各状态的订单数量
    public AjaxResult statusDist()
    {
        // 调用Mapper层查询订单状态分布，包装为AjaxResult返回给前端绘制饼图
        return success(adminStatsMapper.selectStatusDist());
    }
}
