// 声明当前类所属的包路径，位于trade模块的controller层
package com.debuff.trade.controller;

// ===== 以下为Java标准库相关的导入 =====
// 导入BigDecimal类，用于精确表示价格（避免浮点数精度问题）
import java.math.BigDecimal;
// ===== 以下为Spring框架核心注解的导入 =====
// 导入@Autowired注解，用于自动注入依赖的Bean
import org.springframework.beans.factory.annotation.Autowired;
// 导入@DeleteMapping注解，用于映射HTTP DELETE请求（下架操作）
import org.springframework.web.bind.annotation.DeleteMapping;
// 导入@PathVariable注解，用于从URL路径中提取参数
import org.springframework.web.bind.annotation.PathVariable;
// 导入@PostMapping注解，用于映射HTTP POST请求（上架操作）
import org.springframework.web.bind.annotation.PostMapping;
// 导入@RequestBody注解，用于将请求体中的JSON绑定到方法参数
import org.springframework.web.bind.annotation.RequestBody;
// 导入@RequestMapping注解，用于定义控制器的基础请求路径
import org.springframework.web.bind.annotation.RequestMapping;
// 导入@RestController注解，标记该类为RESTful控制器，返回值自动序列化为JSON
import org.springframework.web.bind.annotation.RestController;
// ===== 以下为项目公共模块（common）的导入 =====
// 导入若依框架的基础控制器类，提供响应封装等通用方法
import com.debuff.common.core.controller.BaseController;
// 导入AjaxResult统一响应对象，用于封装返回结果
import com.debuff.common.core.domain.AjaxResult;
// 导入Spring Security工具类，用于获取当前登录用户的信息
import com.debuff.common.utils.SecurityUtils;
// ===== 以下为trade模块自身的服务导入 =====
// 导入上架Service接口，定义上架/下架相关的业务方法
import com.debuff.trade.service.IListingService;

/**
 * C端上架/下架控制器（前台接口，面向普通用户）
 *
 * <p>路径前缀 /api/listing，提供用户端的"上架"和"下架"功能。
 * 上架：从库存中选择饰品 → 设置价格 → 挂到市场出售。
 * 下架：将市场在售的饰品撤回库存。</p>
 *
 * <p>与 OrderController 不同，上架/下架不需要 @PreAuthorize 权限注解，
 * 因为所有登录用户都可以上架自己的饰品。权限控制通过 SecurityUtils.getUserId() 限定只能操作自己的库存。</p>
 *
 * @author debuff
 */
// 标记为REST控制器，Spring会自动将其注册为Bean并序列化返回值为JSON
@RestController
// 指定该控制器的基础路径，所有C端上架接口都以 /api/listing 开头
@RequestMapping("/api/listing")
// 定义控制器类并继承BaseController，获得响应封装等通用能力
public class UserListingController extends BaseController
{
    // 自动注入上架Service实例，用于调用上架/下架相关的业务逻辑
    @Autowired
    private IListingService listingService;

    /**
     * 上架（库存 0在库 → 1在售）
     *
     * <p>前端在"我的库存"页面选择饰品，输入价格后点击"上架"调用此接口。
     * 请求体为 JSON：{ inventoryId: 库存ID, price: 售价 }。</p>
     *
     * @param body 上架请求参数（库存ID + 价格）
     * @return 创建的上架记录
     */
    // 映射POST请求到 /api/listing，用于创建新的上架记录
    @PostMapping
    // 定义create方法，接收JSON格式的请求体，返回创建的上架记录
    public AjaxResult create(@RequestBody ListingCreateBody body)
    {
        // 卖家ID从登录态获取（安全），库存ID和价格从前端传入
        // 调用Service层创建上架记录，传入卖家ID、库存ID和售价
        return success(listingService.createListing(SecurityUtils.getUserId(), body.getInventoryId(), body.getPrice()));
    }

    /**
     * 下架（在售 → 已下架，库存回在库）
     *
     * <p>按库存ID操作：每个库存最多一条在售记录，所以用库存ID就能唯一定位要下架的记录。
     * 前端在"我的上架"页面点击"下架"按钮时调用此接口。</p>
     *
     * @param inventoryId 要下架的库存ID
     * @return 操作结果
     */
    // 映射DELETE请求到 /api/listing/inventory/{inventoryId}，用于下架指定库存的饰品
    @DeleteMapping("/inventory/{inventoryId}")
    // 定义offShelf方法，接收路径中的库存ID，返回操作结果
    public AjaxResult offShelf(@PathVariable Long inventoryId)
    {
        // 调用Service层执行下架操作，传入当前用户ID和库存ID（确保只能下架自己的饰品）
        listingService.offShelf(SecurityUtils.getUserId(), inventoryId);
        // 下架成功后返回成功响应（无数据体）
        return success();
    }

    /**
     * 上架请求体（内部类）
     *
     * <p>封装前端 POST /api/listing 的 JSON 请求参数。
     * 使用内部类而不是独立 DTO，是因为这个接口参数简单（只有两个字段），
     * 放在 Controller 内部更紧凑，减少文件数量。</p>
     */
    // 定义静态内部类，用于接收上架请求的JSON参数（inventoryId + price）
    public static class ListingCreateBody
    {
        /** 要上架的库存ID */
        // 声明库存ID字段，对应前端传入的inventoryId
        private Long inventoryId;

        /** 卖家设定的售价 */
        // 声明价格字段，使用BigDecimal保证金额精度
        private BigDecimal price;

        // 获取库存ID的getter方法
        public Long getInventoryId()
        {
            // 返回库存ID字段的值
            return inventoryId;
        }

        // 设置库存ID的setter方法
        public void setInventoryId(Long inventoryId)
        {
            // 将传入的库存ID赋值给成员变量
            this.inventoryId = inventoryId;
        }

        // 获取价格的getter方法
        public BigDecimal getPrice()
        {
            // 返回价格字段的值
            return price;
        }

        // 设置价格的setter方法
        public void setPrice(BigDecimal price)
        {
            // 将传入的价格赋值给成员变量
            this.price = price;
        }
    }
}
