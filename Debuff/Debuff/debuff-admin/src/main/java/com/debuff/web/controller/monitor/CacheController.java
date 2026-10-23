// 包声明
package com.debuff.web.controller.monitor;

// 导入所需类
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.constant.CacheConstants;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.utils.StringUtils;
import com.debuff.system.domain.SysCache;

/**
 * 缓存监控
 * 
 * @author ruoyi
 */
@RestController // 标记为REST控制器
@RequestMapping("/monitor/cache") // 请求路径前缀
public class CacheController
{
    @Autowired // 自动注入Redis模板
    private RedisTemplate<String, String> redisTemplate;

    private final static List<SysCache> caches = new ArrayList<SysCache>(); // 缓存名称列表
    {
        caches.add(new SysCache(CacheConstants.LOGIN_TOKEN_KEY, "用户信息")); // 用户Token缓存
        caches.add(new SysCache(CacheConstants.SYS_CONFIG_KEY, "配置信息")); // 系统配置缓存
        caches.add(new SysCache(CacheConstants.SYS_DICT_KEY, "数据字典")); // 数据字典缓存
        caches.add(new SysCache(CacheConstants.CAPTCHA_CODE_KEY, "验证码")); // 验证码缓存
        caches.add(new SysCache(CacheConstants.REPEAT_SUBMIT_KEY, "防重提交")); // 防重复提交缓存
        caches.add(new SysCache(CacheConstants.RATE_LIMIT_KEY, "限流处理")); // 限流缓存
        caches.add(new SysCache(CacheConstants.PWD_ERR_CNT_KEY, "密码错误次数")); // 密码错误计数缓存
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')") // 权限校验：缓存监控
    @GetMapping() // GET请求
    public AjaxResult getInfo() throws Exception
    {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info()); // 获取Redis服务器信息
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats")); // 获取命令统计信息
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) connection -> connection.dbSize()); // 获取数据库key数量

        Map<String, Object> result = new HashMap<>(3); // 封装返回结果
        result.put("info", info); // Redis服务器信息
        result.put("dbSize", dbSize); // 数据库key数量

        List<Map<String, String>> pieList = new ArrayList<>(); // 命令统计饼图数据
        commandStats.stringPropertyNames().forEach(key -> { // 遍历每条命令统计
            Map<String, String> data = new HashMap<>(2);
            String property = commandStats.getProperty(key);
            data.put("name", StringUtils.removeStart(key, "cmdstat_")); // 命令名称
            data.put("value", StringUtils.substringBetween(property, "calls=", ",usec")); // 调用次数
            pieList.add(data);
        });
        result.put("commandStats", pieList); // 命令统计图表数据
        return AjaxResult.success(result);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')") // 权限校验：缓存监控
    @GetMapping("/getNames") // 获取缓存名称列表
    public AjaxResult cache()
    {
        return AjaxResult.success(caches); // 返回缓存名称列表
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')") // 权限校验：缓存监控
    @GetMapping("/getKeys/{cacheName}") // 获取指定缓存的key列表
    public AjaxResult getCacheKeys(@PathVariable String cacheName)
    {
        Set<String> cacheKeys = redisTemplate.keys(cacheName + "*"); // 模糊匹配缓存key
        return AjaxResult.success(cacheKeys); // 返回key列表
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')") // 权限校验：缓存监控
    @GetMapping("/getValue/{cacheName}/{cacheKey}") // 获取指定缓存的值
    public AjaxResult getCacheValue(@PathVariable String cacheName, @PathVariable String cacheKey)
    {
        String cacheValue = redisTemplate.opsForValue().get(cacheKey); // 从Redis获取缓存值
        SysCache sysCache = new SysCache(cacheName, cacheKey, cacheValue); // 封装缓存对象
        return AjaxResult.success(sysCache); // 返回缓存信息
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')") // 权限校验：缓存监控
    @DeleteMapping("/clearCacheName/{cacheName}") // 清除指定名称的缓存
    public AjaxResult clearCacheName(@PathVariable String cacheName)
    {
        Collection<String> cacheKeys = redisTemplate.keys(cacheName + "*"); // 匹配该名称下所有key
        redisTemplate.delete(cacheKeys); // 批量删除
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')") // 权限校验：缓存监控
    @DeleteMapping("/clearCacheKey/{cacheKey}") // 清除指定key的缓存
    public AjaxResult clearCacheKey(@PathVariable String cacheKey)
    {
        redisTemplate.delete(cacheKey); // 删除指定key
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')") // 权限校验：缓存监控
    @DeleteMapping("/clearCacheAll") // 清除所有缓存
    public AjaxResult clearCacheAll()
    {
        Collection<String> cacheKeys = redisTemplate.keys("*"); // 匹配所有key
        redisTemplate.delete(cacheKeys); // 批量删除所有缓存
        return AjaxResult.success();
    }
}
