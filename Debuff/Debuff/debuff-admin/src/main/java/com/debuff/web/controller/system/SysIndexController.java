// 包声明
package com.debuff.web.controller.system;

// 导入Spring MVC和配置相关依赖
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.config.RuoYiConfig;
import com.debuff.common.utils.StringUtils;

/**
 * 首页
 *
 * @author ruoyi
 */
// 标记为REST控制器
@RestController
public class SysIndexController
{
    /** 系统基础配置 */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index()
    {
        // 返回首页欢迎提示文本，包含系统名称和版本号
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }
}
