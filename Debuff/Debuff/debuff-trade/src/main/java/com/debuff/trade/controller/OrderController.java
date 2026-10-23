// 声明当前类所属的包路径，位于trade模块的controller层
package com.debuff.trade.controller;

// ===== 以下为Java标准库和Servlet API相关的导入 =====
// 导入Java集合框架的List接口，用于接收查询结果列表
import java.util.List;
// 导入HttpServletResponse对象，用于Excel导出时写入HTTP响应流
import javax.servlet.http.HttpServletResponse;
// ===== 以下为Spring Security权限控制相关的导入 =====
// 导入@PreAuthorize注解，用于方法级别的权限校验
import org.springframework.security.access.prepost.PreAuthorize;
// ===== 以下为Spring框架核心注解的导入 =====
// 导入@Autowired注解，用于自动注入依赖的Bean
import org.springframework.beans.factory.annotation.Autowired;
// 导入@GetMapping注解，用于映射HTTP GET请求
import org.springframework.web.bind.annotation.GetMapping;
// 导入@PostMapping注解，用于映射HTTP POST请求
import org.springframework.web.bind.annotation.PostMapping;
// 导入@PutMapping注解，用于映射HTTP PUT请求（修改操作）
import org.springframework.web.bind.annotation.PutMapping;
// 导入@DeleteMapping注解，用于映射HTTP DELETE请求（删除操作）
import org.springframework.web.bind.annotation.DeleteMapping;
// 导入@PathVariable注解，用于从URL路径中提取参数
import org.springframework.web.bind.annotation.PathVariable;
// 导入@RequestBody注解，用于将请求体中的JSON绑定到方法参数
import org.springframework.web.bind.annotation.RequestBody;
// 导入@RequestMapping注解，用于定义控制器的基础请求路径
import org.springframework.web.bind.annotation.RequestMapping;
// 导入@RestController注解，标记该类为RESTful控制器，返回值自动序列化为JSON
import org.springframework.web.bind.annotation.RestController;
// ===== 以下为项目公共模块（common）的导入 =====
// 导入@Log操作日志注解，用于自动记录管理员的操作行为
import com.debuff.common.annotation.Log;
// 导入若依框架的基础控制器类，提供分页、响应封装等通用方法
import com.debuff.common.core.controller.BaseController;
// 导入AjaxResult统一响应对象，用于封装单条数据的返回结果
import com.debuff.common.core.domain.AjaxResult;
// 导入业务类型枚举，标识操作类型（新增、修改、删除、导出等）
import com.debuff.common.enums.BusinessType;
// ===== 以下为trade模块自身的领域对象和服务导入 =====
// 导入订单实体类，对应数据库中的订单表
import com.debuff.trade.domain.Order;
// 导入订单Service接口，定义订单相关的业务方法
import com.debuff.trade.service.IOrderService;
// 导入若依框架的Excel工具类，用于将数据导出为Excel文件
import com.debuff.common.utils.poi.ExcelUtil;
// 导入TableDataInfo分页响应对象，包含rows和total字段，供前端表格组件使用
import com.debuff.common.core.page.TableDataInfo;

/**
 * 管理端交易订单控制器（后台管理，面向管理员）
 *
 * <p>路径前缀 /trade/order，提供管理员对订单的增删改查和导出功能。
 * 与C端的 TradeOrderController（/api/order）不同，这里的接口都需要通过 @PreAuthorize 权限校验，
 * 只有拥有 trade:order:xxx 权限的管理员角色才能访问。</p>
 *
 * <h3>权限与审计</h3>
 * <ul>
 *   <li>@PreAuthorize("@ss.hasPermi('trade:order:list')") — 调用若依的权限校验工具，
 *       检查当前用户是否拥有对应权限字符串（在 sys_menu 表中配置）</li>
 *   <li>@Log(title="交易订单", businessType=BusinessType.EXPORT) — 若依的操作日志注解，
 *       自动记录操作人、操作时间、请求参数、响应结果到 sys_oper_log 表</li>
 * </ul>
 *
 * <h3>【答辩要点】@Log 注解的实现原理？</h3>
 * <p>@Log 是若依框架自定义的注解，配合 AOP（面向切面编程）实现。
 * 框架中有一个 LogAspect 切面类，用 @Around 或 @AfterReturning 拦截所有标注了 @Log 的方法。
 * 拦截后，从方法参数、请求上下文中提取操作人、IP、请求URL、方法参数、返回值等信息，
 * 异步写入 sys_oper_log 表。这样业务代码不需要手动写日志，加上注解就自动记录。
 * 对于导出等敏感操作，审计日志可以追溯"谁在什么时候导出了什么数据"。</p>
 *
 * <h3>【答辩要点】为什么列表接口返回 TableDataInfo，而详情接口返回 AjaxResult？</h3>
 * <p>这是若依框架的约定：TableDataInfo 继承自 AjaxResult，额外包含 rows（数据列表）和 total（总条数）两个字段，
 * 专供前端 el-table + el-pagination 组件使用。AjaxResult 只有 data（单个对象），
 * 适合详情、新增、修改等不需要分页的接口。前端 request.js 拦截器对两种返回值都能正确处理。</p>
 *
 * @author debuff
 * @date 2026-08-25
 */
// 标记为REST控制器，Spring会自动将其注册为Bean并序列化返回值为JSON
@RestController
// 指定该控制器的基础路径，所有管理端订单接口都以 /trade/order 开头
@RequestMapping("/trade/order")
// 定义控制器类并继承BaseController，获得分页、响应封装等通用能力
public class OrderController extends BaseController
{
    // 自动注入订单Service实例，用于调用订单相关的业务逻辑方法
    @Autowired
    private IOrderService orderService;

    /**
     * 查询交易订单列表（管理端订单管理页面）
     *
     * <p>支持按订单号、饰品名称、买家昵称、状态等条件筛选（通过 Order 实体接收参数）。
     * startPage() 从请求参数取 pageNum/pageSize，PageHelper 自动拼接 LIMIT。</p>
     */
    // 权限校验：当前用户必须拥有 trade:order:list 权限才能访问此接口
    @PreAuthorize("@ss.hasPermi('trade:order:list')")
    // 映射GET请求到 /trade/order/list，用于查询订单列表
    @GetMapping("/list")
    // 定义list方法，接收Order对象作为查询条件，返回分页数据
    public TableDataInfo list(Order order)
    {
        // startPage() 是若依 BaseController 提供的分页初始化方法
        // 它读取请求中的 pageNum（页码）和 pageSize（每页条数），调用 PageHelper.startPage()
        // 之后紧接着的第一条 SELECT 查询会被自动加上 LIMIT 子句
        startPage();
        // 调用Service层根据条件查询订单列表
        List<Order> list = orderService.selectOrderList(order);
        // getDataTable() 将 List 包装为 TableDataInfo（包含 rows 和 total），
        // 前端 el-table 组件需要的数据格式
        return getDataTable(list);
    }

    /**
     * 导出交易订单列表为 Excel
     *
     * <p>使用若依内置的 ExcelUtil，通过 Order 实体上的 @Excel 注解定义列映射。
     * @Log 注解记录导出操作到操作日志。</p>
     */
    // 权限校验：当前用户必须拥有 trade:order:export 权限才能导出
    @PreAuthorize("@ss.hasPermi('trade:order:export')")
    // 操作日志注解：记录本次导出操作，标题为"交易订单"，类型为EXPORT
    @Log(title = "交易订单", businessType = BusinessType.EXPORT)
    // 映射POST请求到 /trade/order/export，用于导出Excel
    @PostMapping("/export")
    // 定义export方法，接收HttpServletResponse用于写出文件流，Order作为查询条件
    public void export(HttpServletResponse response, Order order)
    {
        // 先查询出所有符合条件的订单数据（不分页，导出全部）
        List<Order> list = orderService.selectOrderList(order);
        // ExcelUtil 通过反射读取 Order 类上的 @Excel 注解，自动生成 Excel 文件
        ExcelUtil<Order> util = new ExcelUtil<Order>(Order.class);
        // 调用工具类方法将订单列表写入Excel并通过HTTP响应返回给前端下载
        util.exportExcel(response, list, "交易订单数据");
    }

    /**
     * 获取交易订单详细信息（管理端点击某条订单查看详情）
     */
    // 权限校验：当前用户必须拥有 trade:order:query 权限才能查看详情
    @PreAuthorize("@ss.hasPermi('trade:order:query')")
    // 映射GET请求到 /trade/order/{id}，根据ID获取订单详情
    @GetMapping(value = "/{id}")
    // 定义getInfo方法，接收路径中的订单ID，返回订单详细信息
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        // 调用Service层根据ID查询订单，将结果包装为AjaxResult返回
        return success(orderService.selectOrderById(id));
    }

    /**
     * 新增交易订单（管理端手动创建订单，一般不常用，保留框架完整性）
     */
    // 权限校验：当前用户必须拥有 trade:order:add 权限才能新增
    @PreAuthorize("@ss.hasPermi('trade:order:add')")
    // 操作日志注解：记录本次新增操作，标题为"交易订单"，类型为INSERT
    @Log(title = "交易订单", businessType = BusinessType.INSERT)
    // 映射POST请求到 /trade/order，用于创建新订单
    @PostMapping
    // 定义add方法，接收请求体中的Order对象，返回操作是否成功
    public AjaxResult add(@RequestBody Order order)
    {
        // 调用Service层插入订单记录，并将影响行数转为AjaxResult返回
        return toAjax(orderService.insertOrder(order));
    }

    /**
     * 修改交易订单（管理端编辑订单信息）
     */
    // 权限校验：当前用户必须拥有 trade:order:edit 权限才能修改
    @PreAuthorize("@ss.hasPermi('trade:order:edit')")
    // 操作日志注解：记录本次修改操作，标题为"交易订单"，类型为UPDATE
    @Log(title = "交易订单", businessType = BusinessType.UPDATE)
    // 映射PUT请求到 /trade/order，用于更新订单信息
    @PutMapping
    // 定义edit方法，接收请求体中的Order对象，返回操作是否成功
    public AjaxResult edit(@RequestBody Order order)
    {
        // 调用Service层更新订单记录，并将影响行数转为AjaxResult返回
        return toAjax(orderService.updateOrder(order));
    }

    /**
     * 删除交易订单（支持批量删除，传入ID数组）
     *
     * <p>@PathVariable Long[] ids 接收路径中的多个ID，如 /trade/order/1,2,3</p>
     */
    // 权限校验：当前用户必须拥有 trade:order:remove 权限才能删除
    @PreAuthorize("@ss.hasPermi('trade:order:remove')")
    // 操作日志注解：记录本次删除操作，标题为"交易订单"，类型为DELETE
    @Log(title = "交易订单", businessType = BusinessType.DELETE)
    // 映射DELETE请求到 /trade/order/{ids}，支持批量删除（多个ID用逗号分隔）
	@DeleteMapping("/{ids}")
    // 定义remove方法，接收路径中的ID数组，返回操作是否成功
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        // 调用Service层批量删除订单记录，并将影响行数转为AjaxResult返回
        return toAjax(orderService.deleteOrderByIds(ids));
    }
}
