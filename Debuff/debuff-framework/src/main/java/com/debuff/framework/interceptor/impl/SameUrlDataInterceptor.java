// 包声明
package com.debuff.framework.interceptor.impl;

// 导入所需类
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.debuff.common.annotation.RepeatSubmit;
import com.debuff.common.constant.CacheConstants;
import com.debuff.common.core.redis.RedisCache;
import com.debuff.common.filter.RepeatedlyRequestWrapper;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.http.HttpHelper;
import com.debuff.framework.interceptor.RepeatSubmitInterceptor;

/**
 * 判断请求url和数据是否和上一次相同，
 * 如果和上次相同，则是重复提交表单。 有效时间为10秒内。
 * 
 * @author ruoyi
 */
@Component // 注册为Spring Bean
public class SameUrlDataInterceptor extends RepeatSubmitInterceptor // 继承防重复提交拦截器，基于URL+数据判断是否重复
{
    public final String REPEAT_PARAMS = "repeatParams"; // 重复参数key

    public final String REPEAT_TIME = "repeatTime"; // 重复时间key

    // 令牌自定义标识
    @Value("${token.header}") // 从配置文件读取token请求头名称
    private String header;

    @Autowired // 自动注入Redis缓存
    private RedisCache redisCache;

    @SuppressWarnings("unchecked")
    @Override
    public boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) // 判断是否重复提交
    {
        String nowParams = ""; // 当前请求参数
        if (request instanceof RepeatedlyRequestWrapper) // 如果是可重复读请求包装器
        {
            RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper) request; // 转换为可重复读请求
            nowParams = HttpHelper.getBodyString(repeatedlyRequest); // 获取请求体内容
        }

        // body参数为空，获取Parameter的数据
        if (StringUtils.isEmpty(nowParams)) // 如果请求体为空
        {
            nowParams = JSON.toJSONString(request.getParameterMap()); // 获取URL参数
        }
        Map<String, Object> nowDataMap = new HashMap<String, Object>(); // 当前请求数据Map
        nowDataMap.put(REPEAT_PARAMS, nowParams); // 存储请求参数
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis()); // 存储当前时间戳

        // 请求地址（作为存放cache的key值）
        String url = request.getRequestURI(); // 获取请求URI

        // 唯一值（没有消息头则使用请求地址）
        String submitKey = StringUtils.trimToEmpty(request.getHeader(header)); // 获取token作为唯一标识

        // 唯一标识（指定key + url + 消息头）
        String cacheRepeatKey = CacheConstants.REPEAT_SUBMIT_KEY + url + submitKey; // 拼接缓存key

        Object sessionObj = redisCache.getCacheObject(cacheRepeatKey); // 从Redis获取缓存数据
        if (sessionObj != null) // 如果缓存存在
        {
            Map<String, Object> sessionMap = (Map<String, Object>) sessionObj; // 转换为Map
            if (sessionMap.containsKey(url)) // 如果包含当前URL的数据
            {
                Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url); // 获取上次的请求数据
                if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap, annotation.interval())) // 如果参数相同且在时间间隔内
                {
                    return true; // 判定为重复提交
                }
            }
        }
        Map<String, Object> cacheMap = new HashMap<String, Object>(); // 创建新的缓存数据
        cacheMap.put(url, nowDataMap); // 存储当前请求数据
        redisCache.setCacheObject(cacheRepeatKey, cacheMap, annotation.interval(), TimeUnit.MILLISECONDS); // 存入Redis，过期时间为注解配置的间隔
        return false; // 不是重复提交
    }

    /**
     * 判断参数是否相同
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) // 比较两次请求的参数是否一致
    {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS); // 获取当前参数
        String preParams = (String) preMap.get(REPEAT_PARAMS); // 获取上次参数
        return nowParams.equals(preParams); // 比较是否相同
    }

    /**
     * 判断两次间隔时间
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int interval) // 比较两次请求的时间间隔是否在阈值内
    {
        long time1 = (Long) nowMap.get(REPEAT_TIME); // 获取当前时间
        long time2 = (Long) preMap.get(REPEAT_TIME); // 获取上次时间
        if ((time1 - time2) < interval) // 如果时间间隔小于配置的阈值
        {
            return true; // 判定为重复提交
        }
        return false; // 不是重复提交
    }
}
