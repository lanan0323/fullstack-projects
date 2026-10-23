// 包声明
package com.debuff.web.controller.monitor;

// 导入所需类
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.annotation.Log;
import com.debuff.common.constant.CacheConstants;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.core.redis.RedisCache;
import com.debuff.common.enums.BusinessType; // 导入业务类型枚举（用于操作日志）
import com.debuff.common.utils.StringUtils;
import com.debuff.system.domain.SysUserOnline;
import com.debuff.system.service.ISysUserOnlineService;

/**
 * 在线用户监控
 * 
 * @author ruoyi
 */
@RestController // 标记为REST控制器
@RequestMapping("/monitor/online") // 请求路径前缀
public class SysUserOnlineController extends BaseController
{
    @Autowired // 自动注入在线用户服务
    private ISysUserOnlineService userOnlineService;

    @Autowired // 自动注入Redis缓存
    private RedisCache redisCache;

    @PreAuthorize("@ss.hasPermi('monitor:online:list')") // 权限校验：在线用户列表
    @GetMapping("/list") // GET请求
    public TableDataInfo list(String ipaddr, String userName)
    {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*"); // 获取所有登录Token的key
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>(); // 在线用户列表
        for (String key : keys) // 遍历所有Token
        {
            LoginUser user = redisCache.getCacheObject(key); // 从Redis获取登录用户信息
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) // 同时按IP和用户名筛选
            {
                userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
            }
            else if (StringUtils.isNotEmpty(ipaddr)) // 按IP地址筛选
            {
                userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
            }
            else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser())) // 按用户名筛选
            {
                userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
            }
            else // 不筛选，返回所有在线用户
            {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList); // 倒序排列（最新的在前）
        userOnlineList.removeAll(Collections.singleton(null)); // 移除null元素（不匹配的）
        return getDataTable(userOnlineList); // 返回分页数据
    }

    /**
     * 强退用户
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')") // 权限校验：强退用户
    @Log(title = "在线用户", businessType = BusinessType.FORCE) // 记录操作日志
    @DeleteMapping("/{tokenId}") // DELETE请求
    public AjaxResult forceLogout(@PathVariable String tokenId)
    {
        redisCache.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenId); // 删除用户Token，强制下线
        return success();
    }
}
