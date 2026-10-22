// 包声明
package com.debuff.framework.security.handle;

// 导入所需类
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.alibaba.fastjson2.JSON;
import com.debuff.common.constant.Constants;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.utils.ServletUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.framework.manager.AsyncManager;
import com.debuff.framework.manager.factory.AsyncFactory;
import com.debuff.framework.web.service.TokenService;

/**
 * 自定义退出处理类 返回成功
 * 
 * @author ruoyi
 */
@Configuration // 标记为配置类
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler // 实现登出成功处理器接口
{
    @Autowired // 自动注入Token服务
    private TokenService tokenService;

    /**
     * 退出处理
     * 
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) // 登出成功时调用此方法
            throws IOException, ServletException
    {
        LoginUser loginUser = tokenService.getLoginUser(request); // 从请求中获取登录用户信息
        if (StringUtils.isNotNull(loginUser)) // 如果用户已登录
        {
            String userName = loginUser.getUsername(); // 获取用户名
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken()); // 删除Redis中的用户token缓存
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, "退出成功")); // 异步记录退出日志
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success("退出成功"))); // 返回退出成功的JSON响应
    }
}
