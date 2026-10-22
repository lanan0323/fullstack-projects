// 包声明
package com.debuff.trade.controller;

// 导入所需类
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.trade.domain.Favorite;
import com.debuff.trade.domain.vo.FavoriteVO;
import com.debuff.trade.service.IFavoriteService;

/**
 * 收藏 前台接口
 * 
 * @author debuff
 */
@RestController // 标记为REST控制器
@RequestMapping("/api/favorite") // 收藏API路径前缀
public class UserFavoriteController extends BaseController // 用户收藏控制器
{
    @Autowired // 自动注入收藏服务
    private IFavoriteService favoriteService;

    /**
     * 切换收藏状态（收藏/取消收藏）
     */
    @PostMapping("/toggle") // POST /api/favorite/toggle
    public AjaxResult toggle(@RequestParam Long listingId) // 接收挂单ID
    {
        Long userId = SecurityUtils.getUserId(); // 获取当前用户ID
        // 查询是否已收藏
        Favorite query = new Favorite(); // 创建查询条件
        query.setUserId(userId); // 设置用户ID
        query.setListingId(listingId); // 设置挂单ID
        List<Favorite> list = favoriteService.selectFavoriteList(query); // 查询收藏列表
        if (list != null && !list.isEmpty()) // 已收藏
        {
            // 已收藏，取消收藏
            favoriteService.deleteFavoriteById(list.get(0).getId()); // 删除收藏记录
            return success("已取消收藏"); // 返回成功提示
        }
        else // 未收藏
        {
            // 未收藏，添加收藏
            Favorite favorite = new Favorite(); // 创建收藏对象
            favorite.setUserId(userId); // 设置用户ID
            favorite.setListingId(listingId); // 设置挂单ID
            favoriteService.insertFavorite(favorite); // 插入收藏记录
            return success("已收藏"); // 返回成功提示
        }
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check") // GET /api/favorite/check
    public AjaxResult check(@RequestParam Long listingId) // 接收挂单ID
    {
        Long userId = SecurityUtils.getUserId(); // 获取当前用户ID
        Favorite query = new Favorite(); // 创建查询条件
        query.setUserId(userId); // 设置用户ID
        query.setListingId(listingId); // 设置挂单ID
        List<Favorite> list = favoriteService.selectFavoriteList(query); // 查询收藏列表
        boolean favorited = list != null && !list.isEmpty(); // 判断是否已收藏
        return success(favorited); // 返回布尔结果
    }

    /**
     * 我的收藏列表（含饰品详情）
     */
    @GetMapping("/list") // GET /api/favorite/list
    public TableDataInfo list()
    {
        Long userId = SecurityUtils.getUserId(); // 获取当前用户ID
        startPage(); // 启动PageHelper分页
        List<FavoriteVO> list = favoriteService.selectUserFavoriteList(userId); // 查询收藏列表
        return getDataTable(list); // 返回分页数据
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{id}") // DELETE /api/favorite/{id}
    public AjaxResult remove(@PathVariable Long id) // 接收收藏ID
    {
        return toAjax(favoriteService.deleteFavoriteById(id)); // 删除收藏并返回结果
    }
}
