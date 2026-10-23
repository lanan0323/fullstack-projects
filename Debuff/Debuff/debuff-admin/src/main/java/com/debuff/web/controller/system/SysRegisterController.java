// 包声明
package com.debuff.web.controller.system;

// 导入所需类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.domain.model.RegisterBody;
import com.debuff.common.utils.StringUtils;
import com.debuff.framework.web.service.SysRegisterService;
import com.debuff.system.service.ISysConfigService;

/**
 * 注册验证
 * 
 * @author ruoyi
 */
@RestController // 标记为REST控制器
public class SysRegisterController extends BaseController
{
    @Autowired // 自动注入注册服务
    private SysRegisterService registerService;

    @Autowired // 自动注入配置服务
    private ISysConfigService configService;

    @PostMapping("/register") // POST注册请求
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) // 检查系统是否开启注册功能
        {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user); // 调用注册服务
        return StringUtils.isEmpty(msg) ? success() : error(msg); // 注册成功返回success，失败返回错误信息
    }
}
