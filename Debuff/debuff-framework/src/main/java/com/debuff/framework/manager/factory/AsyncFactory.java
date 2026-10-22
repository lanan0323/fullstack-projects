// 包声明
package com.debuff.framework.manager.factory;

// 导入所需类
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.debuff.common.constant.Constants;
import com.debuff.common.utils.LogUtils;
import com.debuff.common.utils.ServletUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.ip.AddressUtils;
import com.debuff.common.utils.ip.IpUtils;
import com.debuff.common.utils.spring.SpringUtils;
import com.debuff.system.domain.SysLogininfor;
import com.debuff.system.domain.SysOperLog;
import com.debuff.system.service.ISysLogininforService;
import com.debuff.system.service.ISysOperLogService;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * 异步工厂（产生任务用）
 * 
 * @author ruoyi
 */
public class AsyncFactory
{
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user"); // 用户操作日志记录器

    /**
     * 记录登录信息
     * 
     * @param username 用户名
     * @param status 状态
     * @param message 消息
     * @param args 列表
     * @return 任务task
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message, // 创建记录登录信息的异步任务
            final Object... args)
    {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent")); // 解析客户端UserAgent
        final String ip = IpUtils.getIpAddr(); // 获取客户端IP
        return new TimerTask() // 创建定时任务
        {
            @Override
            public void run()
            {
                String address = AddressUtils.getRealAddressByIP(ip); // 根据IP查询归属地址
                StringBuilder s = new StringBuilder(); // 构建日志信息
                s.append(LogUtils.getBlock(ip)); // 添加IP信息
                s.append(address); // 添加地址信息
                s.append(LogUtils.getBlock(username)); // 添加用户名
                s.append(LogUtils.getBlock(status)); // 添加状态
                s.append(LogUtils.getBlock(message)); // 添加消息
                // 打印信息到日志
                sys_user_logger.info(s.toString(), args); // 输出到日志文件
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName(); // 获取操作系统名称
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName(); // 获取浏览器名称
                // 封装对象
                SysLogininfor logininfor = new SysLogininfor(); // 创建登录日志对象
                logininfor.setUserName(username); // 设置用户名
                logininfor.setIpaddr(ip); // 设置IP地址
                logininfor.setLoginLocation(address); // 设置登录地点
                logininfor.setBrowser(browser); // 设置浏览器
                logininfor.setOs(os); // 设置操作系统
                logininfor.setMsg(message); // 设置消息
                // 日志状态
                if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) // 如果状态为登录成功、登出或注册
                {
                    logininfor.setStatus(Constants.SUCCESS); // 设置状态为成功
                }
                else if (Constants.LOGIN_FAIL.equals(status)) // 如果状态为登录失败
                {
                    logininfor.setStatus(Constants.FAIL); // 设置状态为失败
                }
                // 插入数据
                SpringUtils.getBean(ISysLogininforService.class).insertLogininfor(logininfor); // 异步保存登录日志到数据库
            }
        };
    }

    /**
     * 操作日志记录
     * 
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog operLog) // 创建记录操作日志的异步任务
    {
        return new TimerTask() // 创建定时任务
        {
            @Override
            public void run()
            {
                // 远程查询操作地点
                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp())); // 根据IP查询操作地点
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog); // 异步保存操作日志到数据库
            }
        };
    }
}
