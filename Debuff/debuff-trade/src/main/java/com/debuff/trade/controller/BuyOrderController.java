// 包声明
package com.debuff.trade.controller;

// 导入所需类
import java.math.BigDecimal;
import java.util.List;
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
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.trade.domain.Order;
import com.debuff.trade.domain.vo.BuyOrderVO;
import com.debuff.trade.service.IBuyOrderService;

/**
 * 求购单 前台接口（丢求购 / 我的求购 / 市场求购列表）
 * 
 * @author B同学
 */
@RestController // 标记为REST控制器
@RequestMapping("/api/buyOrder") // 求购单API路径前缀
public class BuyOrderController extends BaseController // 求购单控制器
{
    @Autowired // 自动注入求购服务
    private IBuyOrderService buyOrderService;

    /**
     * 发布求购（丢求购）
     */
    @PostMapping // POST /api/buyOrder
    public AjaxResult create(@RequestBody BuyOrderCreateBody body) // 接收发布求购请求体
    {
        return toAjax(buyOrderService.createBuyOrder(SecurityUtils.getUserId(), body.getSkinId(), body.getPrice())); // 创建求购
    }

    /**
     * 查询某饰品的有效求购（市场详情展示，按价格降序）
     */
    @GetMapping("/list") // GET /api/buyOrder/list
    public AjaxResult listBySkin(@RequestParam Long skinId, // 饰品ID
            @RequestParam(required = false) Integer limit) // 条数限制（可选）
    {
        return success(buyOrderService.selectBySkin(skinId, limit)); // 返回求购列表
    }

    /**
     * 我的求购列表
     */
    @GetMapping("/my") // GET /api/buyOrder/my
    public TableDataInfo myList()
    {
        startPage(); // 启动PageHelper分页
        List<BuyOrderVO> list = buyOrderService.selectMyBuyOrders(SecurityUtils.getUserId()); // 查询我的求购
        return getDataTable(list); // 返回分页数据
    }

    /**
     * 修改求购价格
     */
    @PutMapping("/{id}") // PUT /api/buyOrder/{id}
    public AjaxResult updatePrice(@PathVariable Long id, @RequestBody BuyOrderPriceBody body) // 接收路径ID和新价格
    {
        return toAjax(buyOrderService.updatePrice(id, SecurityUtils.getUserId(), body.getPrice())); // 修改价格
    }

    /**
     * 取消求购
     */
    @DeleteMapping("/{id}") // DELETE /api/buyOrder/{id}
    public AjaxResult cancel(@PathVariable Long id) // 接收求购ID
    {
        return toAjax(buyOrderService.cancelBuyOrder(id, SecurityUtils.getUserId())); // 取消求购
    }

    /**
     * 卖家接受求购（以求购价格出售自己的饰品）
     * 仅饰品主人可见此按钮，点击后生成交易订单
     */
    @PostMapping("/{id}/accept") // POST /api/buyOrder/{id}/accept
    public AjaxResult accept(@PathVariable Long id, @RequestBody BuyOrderAcceptBody body) // 接收求购ID和库存ID
    {
        Order order = buyOrderService.acceptBuyOrder(id, SecurityUtils.getUserId(), body.getInventoryId()); // 接受求购生成订单
        return success(order); // 返回生成的订单
    }

    /**
     * 发布求购请求体
     */
    public static class BuyOrderCreateBody // 内部类：发布求购请求体
    {
        private Long skinId; // 饰品ID

        private BigDecimal price; // 求购价格

        /** 获取饰品ID */
        public Long getSkinId()
        {
            return skinId;
        }

        /** 设置饰品ID */
        public void setSkinId(Long skinId)
        {
            this.skinId = skinId;
        }

        /** 获取价格 */
        public BigDecimal getPrice()
        {
            return price;
        }

        /** 设置价格 */
        public void setPrice(BigDecimal price)
        {
            this.price = price;
        }
    }

    /**
     * 修改价格请求体
     */
    public static class BuyOrderPriceBody // 内部类：修改价格请求体
    {
        private BigDecimal price; // 新价格

        /** 获取价格 */
        public BigDecimal getPrice()
        {
            return price;
        }

        /** 设置价格 */
        public void setPrice(BigDecimal price)
        {
            this.price = price;
        }
    }

    /**
     * 接受求购请求体
     */
    public static class BuyOrderAcceptBody // 内部类：接受求购请求体
    {
        private Long inventoryId; // 库存ID

        /** 获取库存ID */
        public Long getInventoryId()
        {
            return inventoryId;
        }

        /** 设置库存ID */
        public void setInventoryId(Long inventoryId)
        {
            this.inventoryId = inventoryId;
        }
    }
}
