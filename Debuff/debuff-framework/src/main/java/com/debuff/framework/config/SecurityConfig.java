// 包声明
package com.debuff.framework.config;

// 导入所需类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;
import com.debuff.framework.config.properties.PermitAllUrlProperties;
import com.debuff.framework.security.filter.JwtAuthenticationTokenFilter;
import com.debuff.framework.security.handle.AuthenticationEntryPointImpl;
import com.debuff.framework.security.handle.LogoutSuccessHandlerImpl;

/**
 * spring security配置
 * 
 * @author ruoyi
 */
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 开启方法级别安全注解支持
public class SecurityConfig extends WebSecurityConfigurerAdapter // 继承Security配置适配器
{
    /**
     * 自定义用户认证逻辑
     */
    @Autowired // 自动注入自定义用户认证服务
    private UserDetailsService userDetailsService;
    
    /**
     * 认证失败处理类
     */
    @Autowired // 自动注入认证失败处理器
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /**
     * 退出处理类
     */
    @Autowired // 自动注入登出成功处理器
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * token认证过滤器
     */
    @Autowired // 自动注入JWT认证过滤器
    private JwtAuthenticationTokenFilter authenticationTokenFilter;
    
    /**
     * 跨域过滤器
     */
    @Autowired // 自动注入跨域过滤器
    private CorsFilter corsFilter;

    /**
     * 允许匿名访问的地址
     */
    @Autowired // 自动注入允许匿名访问的URL配置
    private PermitAllUrlProperties permitAllUrl;

    /**
     * 解决 无法直接注入 AuthenticationManager
     *
     * @return
     * @throws Exception
     */
    @Bean // 注册AuthenticationManager Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean(); // 暴露AuthenticationManager供外部使用
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception // 配置HTTP安全规则
    {
        // 注解标记允许匿名访问的url
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity.authorizeRequests(); // 获取URL授权配置注册器
        permitAllUrl.getUrls().forEach(url -> registry.antMatchers(url).permitAll()); // 将@Anonymous注解标记的URL设为允许匿名访问

        httpSecurity
                // CSRF禁用，因为不使用session
                .csrf().disable() // 禁用CSRF防护（基于token不需要）
                // 禁用HTTP响应标头
                .headers().cacheControl().disable().and() // 禁用缓存控制响应头
                // 认证失败处理类
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and() // 设置认证失败处理器
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // 设置会话策略为无状态
                // 过滤请求
                .authorizeRequests() // 开始配置URL授权规则
                // 对于登录login 注册register 验证码captchaImage 允许匿名访问
                .antMatchers("/login", "/register", "/captchaImage").permitAll() // 登录、注册、验证码接口允许匿名访问
                // 静态资源，可匿名访问
                .antMatchers(HttpMethod.GET, "/", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/profile/**").permitAll() // GET请求的静态资源允许匿名访问
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/*/api-docs", "/druid/**").permitAll() // Swagger和Druid监控页面允许匿名访问
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated() // 其他所有请求都需要认证
                .and()
                .headers().frameOptions().disable(); // 禁用frameOptions，允许iframe嵌套
        // 添加Logout filter
        httpSecurity.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler); // 配置登出URL和登出成功处理器
        // 添加JWT filter
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class); // 在用户名密码过滤器之前添加JWT过滤器
        // 添加CORS filter
        httpSecurity.addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class); // 在JWT过滤器之前添加跨域过滤器
        httpSecurity.addFilterBefore(corsFilter, LogoutFilter.class); // 在登出过滤器之前添加跨域过滤器
    }

    /**
     * 强散列哈希加密实现
     */
    @Bean // 注册密码编码器Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder(); // 返回BCrypt密码编码器，用于密码加密和验证
    }

    /**
     * 身份认证接口
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception // 配置认证管理器
    {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder()); // 设置用户详情服务和密码编码器
    }
}
