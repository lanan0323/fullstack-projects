// 包声明
package com.debuff.framework.security.filter;

// 导入所需类
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.framework.web.service.TokenService;

/**
 * token过滤器 验证token有效性
 * 
 * @author ruoyi
 */
@Component // 注册为Spring Bean
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter // 继承OncePerRequestFilter，确保每个请求只过滤一次
{
    @Autowired // 自动注入Token服务
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) // 每次请求执行一次的过滤方法
            throws ServletException, IOException
    {
        LoginUser loginUser = tokenService.getLoginUser(request); // 从请求中获取登录用户信息（解析token）
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication())) // 如果用户已登录且尚未认证
        {
            tokenService.verifyToken(loginUser); // 验证token是否过期
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities()); // 创建认证令牌
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 设置认证详情（包含IP、SessionId等）
            SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 将认证信息存入Security上下文
        }
        chain.doFilter(request, response); // 继续执行过滤链
    }
}
