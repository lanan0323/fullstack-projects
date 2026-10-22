// 包声明
package com.debuff.trade.controller;

// 导入所需类
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.trade.domain.query.SkinMarketQuery;
import com.debuff.trade.domain.vo.SkinDetailVO;
import com.debuff.trade.domain.vo.SkinMarketVO;
import com.debuff.trade.service.ISkinService;

/**
 * 饰品市场 前台接口
 * 
 * @author B同学
 */
@RestController // 标记为REST控制器
@RequestMapping("/api/market") // 设置饰品市场请求路径前缀
public class SkinMarketController extends BaseController // 继承基础控制器
{
    @Autowired // 自动注入饰品服务
    private ISkinService skinService;

    /**
     * 饰品市场列表（分页 + 筛选 + 排序）
     */
    @GetMapping("/list") // GET请求映射到市场列表接口
    public TableDataInfo list(SkinMarketQuery query) // 接收查询条件对象，分页查询市场饰品
    {
        startPage(); // 启动PageHelper分页
        List<SkinMarketVO> list = skinService.selectMarketList(query); // 调用服务层查询市场饰品列表
        return getDataTable(list); // 封装为分页表格数据返回
    }

    /**
     * 关键词搜索饰品
     */
    @GetMapping("/search") // GET请求映射到搜索接口
    public AjaxResult search(@RequestParam(required = false) String keyword) // 接收可选的搜索关键词
    {
        SkinMarketQuery query = new SkinMarketQuery(); // 创建查询条件对象
        query.setKeyword(keyword); // 设置搜索关键词
        return success(skinService.selectMarketList(query)); // 返回搜索结果列表
    }

    /**
     * 热门饰品推荐（在售数量前10）
     */
    @GetMapping("/hot") // GET请求映射到热门饰品接口
    public AjaxResult hot() // 获取热门饰品推荐
    {
        return success(skinService.selectHotSkins(10)); // 返回在售数量最多的前10个饰品
    }

    /**
     * 获取分类数据（武器分类 + 品质列表）
     */
    @GetMapping("/categories") // GET请求映射到分类数据接口
    public AjaxResult categories() // 获取市场分类数据
    {
        Map<String, Object> data = skinService.selectCategories(); // 调用服务层获取武器分类和品质列表
        return success(data); // 返回分类数据
    }

    /**
     * 饰品详情（基本信息 + 在售列表 + 价格历史）
     */
    @GetMapping("/detail/{skinId}") // GET请求，路径参数为饰品定义ID
    public AjaxResult detail(@PathVariable Long skinId) // 获取指定饰品的详细信息
    {
        SkinDetailVO detail = skinService.selectSkinDetail(skinId); // 调用服务层查询饰品详情
        return detail == null ? error("饰品不存在") : success(detail); // 不存在返回错误，否则返回详情
    }

    /**
     * 饰品价格历史（ECharts数据）
     */
    @GetMapping("/priceHistory/{skinId}") // GET请求，路径参数为饰品ID
    public AjaxResult priceHistory(@PathVariable Long skinId) // 获取饰品历史价格数据
    {
        return success(skinService.selectPriceHistory(skinId)); // 返回价格历史数据供ECharts图表展示
    }
}
