// 包声明
package com.debuff.common.config;

// 导入Spring Boot配置属性绑定和组件注解
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// 读取项目相关配置（对应yml中ruoyi前缀的属性）
@Component
@ConfigurationProperties(prefix = "ruoyi") // 绑定yml中ruoyi前缀的配置
public class RuoYiConfig
{
    /** 项目名称 */
    private String name;

    /** 版本 */
    private String version;

    /** 版权年份 */
    private String copyrightYear;

    /** 上传路径 */
    private static String profile;

    /** 获取地址开关 */
    private static boolean addressEnabled;

    /** 验证码类型 */
    private static String captchaType;

    // 获取项目名称
    public String getName()
    {
        return name;
    }

    // 设置项目名称
    public void setName(String name)
    {
        this.name = name;
    }

    // 获取版本号
    public String getVersion()
    {
        return version;
    }

    // 设置版本号
    public void setVersion(String version)
    {
        this.version = version;
    }

    // 获取版权年份
    public String getCopyrightYear()
    {
        return copyrightYear;
    }

    // 设置版权年份
    public void setCopyrightYear(String copyrightYear)
    {
        this.copyrightYear = copyrightYear;
    }

    // 获取上传路径
    public static String getProfile()
    {
        return profile;
    }

    // 设置上传路径
    public void setProfile(String profile)
    {
        RuoYiConfig.profile = profile;
    }

    // 获取地址开关状态
    public static boolean isAddressEnabled()
    {
        return addressEnabled;
    }

    // 设置地址开关
    public void setAddressEnabled(boolean addressEnabled)
    {
        RuoYiConfig.addressEnabled = addressEnabled;
    }

    // 获取验证码类型
    public static String getCaptchaType() {
        return captchaType;
    }

    // 设置验证码类型
    public void setCaptchaType(String captchaType) {
        RuoYiConfig.captchaType = captchaType;
    }

    // 获取导入文件上传路径
    public static String getImportPath()
    {
        return getProfile() + "/import";
    }

    // 获取头像上传路径
    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }

    // 获取文件下载路径
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    // 获取通用上传路径
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }
}