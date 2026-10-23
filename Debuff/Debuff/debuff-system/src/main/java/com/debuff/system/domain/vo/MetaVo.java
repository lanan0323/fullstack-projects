// 包声明：系统模块的VO（视图对象）包
package com.debuff.system.domain.vo;

// 导入RuoYi的字符串工具类
import com.debuff.common.utils.StringUtils;

/**
 * 路由显示信息
 * 
 * @author ruoyi
 */
public class MetaVo // 路由元信息VO，用于前端侧边栏和面包屑展示
{
    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;

    /**
     * 设置为true，则不会被 <keep-alive>缓存
     */
    private boolean noCache; // 是否不缓存页面组件

    /**
     * 内链地址（http(s)://开头）
     */
    private String link; // 外链/内链地址

    public MetaVo() // 无参构造方法
    {
    }

    public MetaVo(String title, String icon) // 通过标题和图标创建路由元信息
    {
        this.title = title; // 设置标题
        this.icon = icon; // 设置图标
    }

    public MetaVo(String title, String icon, boolean noCache) // 通过标题、图标和缓存设置创建路由元信息
    {
        this.title = title; // 设置标题
        this.icon = icon; // 设置图标
        this.noCache = noCache; // 设置是否缓存
    }

    public MetaVo(String title, String icon, String link) // 通过标题、图标和链接创建路由元信息
    {
        this.title = title; // 设置标题
        this.icon = icon; // 设置图标
        this.link = link; // 设置链接地址
    }

    public MetaVo(String title, String icon, boolean noCache, String link) // 完整参数构造方法
    {
        this.title = title; // 设置标题
        this.icon = icon; // 设置图标
        this.noCache = noCache; // 设置是否缓存
        if (StringUtils.ishttp(link)) // 判断链接是否为http/https开头
        {
            this.link = link; // 是合法外链则赋值
        }
    }

    public boolean isNoCache() // 获取是否不缓存
    {
        return noCache;
    }

    public void setNoCache(boolean noCache) // 设置是否不缓存
    {
        this.noCache = noCache;
    }

    public String getTitle() // 获取路由标题
    {
        return title;
    }

    public void setTitle(String title) // 设置路由标题
    {
        this.title = title;
    }

    public String getIcon() // 获取路由图标
    {
        return icon;
    }

    public void setIcon(String icon) // 设置路由图标
    {
        this.icon = icon;
    }

    public String getLink() // 获取内链地址
    {
        return link;
    }

    public void setLink(String link) // 设置内链地址
    {
        this.link = link;
    }
}
