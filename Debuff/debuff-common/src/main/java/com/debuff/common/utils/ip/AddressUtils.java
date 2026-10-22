// 包声明
package com.debuff.common.utils.ip;

// 导入日志、JSON、配置、HTTP工具等类
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.debuff.common.config.RuoYiConfig;
import com.debuff.common.constant.Constants;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.http.HttpUtils;

// 根据IP获取地址信息的工具类
public class AddressUtils
{
    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // IP地址查询接口URL
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址标识
    public static final String UNKNOWN = "XX XX";

    // 根据IP获取真实地址（调用第三方接口）
    public static String getRealAddressByIP(String ip)
    {
        // 内网不查询
        if (IpUtils.internalIp(ip))
        {
            return "内网IP";
        }
        if (RuoYiConfig.isAddressEnabled()) // 地址查询开关开启
        {
            try
            {
                // 发送GET请求查询IP归属地
                String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", Constants.GBK);
                if (StringUtils.isEmpty(rspStr)) // 响应为空
                {
                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                JSONObject obj = JSON.parseObject(rspStr); // 解析JSON响应
                String region = obj.getString("pro"); // 获取省份
                String city = obj.getString("city"); // 获取城市
                return String.format("%s %s", region, city); // 拼接返回
            }
            catch (Exception e)
            {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return UNKNOWN; // 返回未知地址
    }
}