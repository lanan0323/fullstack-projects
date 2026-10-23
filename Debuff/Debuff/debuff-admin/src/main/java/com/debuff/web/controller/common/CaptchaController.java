// 包声明
package com.debuff.web.controller.common;

// 导入图像处理、IO、缓存、验证码生成等相关依赖
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.code.kaptcha.Producer;
import com.debuff.common.config.RuoYiConfig;
import com.debuff.common.constant.CacheConstants;
import com.debuff.common.constant.Constants;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.redis.RedisCache;
import com.debuff.common.utils.sign.Base64;
import com.debuff.common.utils.uuid.IdUtils;
import com.debuff.system.service.ISysConfigService;

/**
 * 验证码操作处理
 * 
 * @author ruoyi
 */
// 标记为REST控制器
@RestController
public class CaptchaController
{
    // 注入字符验证码生成器
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    // 注入数学验证码生成器
    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    // 注入Redis缓存操作对象
    @Autowired
    private RedisCache redisCache;
    
    // 注入系统配置服务，用于判断验证码是否开启
    @Autowired
    private ISysConfigService configService;
    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode(HttpServletResponse response) throws IOException
    {
        // 构建成功返回结果
        AjaxResult ajax = AjaxResult.success();
        // 查询验证码是否开启
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        // 返回验证码开关状态
        ajax.put("captchaEnabled", captchaEnabled);
        // 如果验证码未开启，直接返回
        if (!captchaEnabled)
        {
            return ajax;
        }

        // 保存验证码信息
        // 生成唯一UUID作为验证码标识
        String uuid = IdUtils.simpleUUID();
        // 拼接Redis缓存key
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        // 验证码文本、答案和图片对象
        String capStr = null, code = null;
        BufferedImage image = null;

        // 生成验证码
        // 获取验证码类型（math数学/char字符）
        String captchaType = RuoYiConfig.getCaptchaType();
        // 数学验证码类型
        if ("math".equals(captchaType))
        {
            // 生成数学表达式文本（如"3+5@8"）
            String capText = captchaProducerMath.createText();
            // 截取表达式部分作为显示文本
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            // 截取@后的答案部分
            code = capText.substring(capText.lastIndexOf("@") + 1);
            // 生成验证码图片
            image = captchaProducerMath.createImage(capStr);
        }
        // 字符验证码类型
        else if ("char".equals(captchaType))
        {
            // 生成字符验证码，文本和答案相同
            capStr = code = captchaProducer.createText();
            // 生成验证码图片
            image = captchaProducer.createImage(capStr);
        }

        // 将验证码答案存入Redis，设置过期时间
        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        // 创建字节数组输出流用于存放图片数据
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try
        {
            // 将图片以JPG格式写入输出流
            ImageIO.write(image, "jpg", os);
        }
        catch (IOException e)
        {
            // 图片生成失败返回错误
            return AjaxResult.error(e.getMessage());
        }

        // 返回UUID标识
        ajax.put("uuid", uuid);
        // 返回Base64编码的图片数据
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }
}
