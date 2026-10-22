// 包声明
package com.debuff.web.controller.monitor;

// 导入所需类
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.framework.web.domain.Server;

/**
 * 服务器监控
 * 
 * @author ruoyi
 */
@RestController // 标记为REST控制器
@RequestMapping("/monitor/server") // 请求路径前缀
public class ServerController
{
    @PreAuthorize("@ss.hasPermi('monitor:server:list')") // 权限校验：服务器监控
    @GetMapping() // GET请求
    public AjaxResult getInfo() throws Exception
    {
        Server server = new Server(); // 创建服务器信息对象
        server.copyTo(); // 收集服务器信息（CPU、内存、磁盘等）
        return AjaxResult.success(server); // 返回服务器监控数据
    }
}
