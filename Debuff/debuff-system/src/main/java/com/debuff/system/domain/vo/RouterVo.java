// 包声明：系统模块的VO（视图对象）包
package com.debuff.system.domain.vo;

// 导入Jackson注解，控制JSON序列化时忽略空值字段
import com.fasterxml.jackson.annotation.JsonInclude;
// 导入Java集合类的List
import java.util.List;

/**
 * 路由配置信息
 * 
 * @author ruoyi
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY) // JSON序列化时忽略空值字段，减少传输数据量
public class RouterVo // 前端路由配置VO，用于构建动态菜单
{
    /**
     * 路由名字
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     */
    private boolean hidden;

    /**
     * 重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     */
    private String redirect;

    /**
     * 组件地址
     */
    private String component; // 前端组件路径

    /**
     * 路由参数：如 {"id": 1, "name": "ry"}
     */
    private String query; // 路由传递的参数

    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
     */
    private Boolean alwaysShow; // 是否总是显示路由，即使只有一个子路由

    /**
     * 其他元素
     */
    private MetaVo meta; // 路由元信息（标题、图标等）

    /**
     * 子路由
     */
    private List<RouterVo> children; // 子路由列表

    public String getName() // 获取路由名称
    {
        return name;
    }

    public void setName(String name) // 设置路由名称
    {
        this.name = name;
    }

    public String getPath() // 获取路由地址
    {
        return path;
    }

    public void setPath(String path) // 设置路由地址
    {
        this.path = path;
    }

    public boolean getHidden() // 获取是否隐藏
    {
        return hidden;
    }

    public void setHidden(boolean hidden) // 设置是否隐藏
    {
        this.hidden = hidden;
    }

    public String getRedirect() // 获取重定向地址
    {
        return redirect;
    }

    public void setRedirect(String redirect) // 设置重定向地址
    {
        this.redirect = redirect;
    }

    public String getComponent() // 获取组件路径
    {
        return component;
    }

    public void setComponent(String component) // 设置组件路径
    {
        this.component = component;
    }

    public String getQuery() // 获取路由参数
    {
        return query;
    }

    public void setQuery(String query) // 设置路由参数
    {
        this.query = query;
    }

    public Boolean getAlwaysShow() // 获取是否总是显示
    {
        return alwaysShow;
    }

    public void setAlwaysShow(Boolean alwaysShow) // 设置是否总是显示
    {
        this.alwaysShow = alwaysShow;
    }

    public MetaVo getMeta() // 获取路由元信息
    {
        return meta;
    }

    public void setMeta(MetaVo meta) // 设置路由元信息
    {
        this.meta = meta;
    }

    public List<RouterVo> getChildren() // 获取子路由列表
    {
        return children;
    }

    public void setChildren(List<RouterVo> children) // 设置子路由列表
    {
        this.children = children;
    }
}
