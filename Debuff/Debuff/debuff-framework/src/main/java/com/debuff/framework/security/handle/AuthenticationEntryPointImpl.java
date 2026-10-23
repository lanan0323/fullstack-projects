// 包声明
package com.debuff.framework.security.handle;

// 导入所需类
import java.io.IOException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.debuff.common.constant.HttpStatus;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.utils.ServletUtils;
import com.debuff.common.utils.StringUtils;

/**
 * 认证失败处理类 返回未授权
 * 
 * @author ruoyi
 */
@Component // 注册为Spring Bean
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable // 实现认证失败入口点接口
{
    private static final long serialVersionUID = -8970718410437077606L; // 序列化版本号

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) // 认证失败时调用此方法
            throws IOException
    {
        int code = HttpStatus.UNAUTHORIZED; // 设置HTTP状态码为401未授权
        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI()); // 构建错误提示信息
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(code, msg))); // 将错误信息以JSON格式返回给前端
    }
}
