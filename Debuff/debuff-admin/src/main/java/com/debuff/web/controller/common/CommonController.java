// 包声明
package com.debuff.web.controller.common;

// 导入集合、Servlet、日志、Spring MVC和文件处理相关依赖
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.debuff.common.config.RuoYiConfig;
import com.debuff.common.constant.Constants;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.file.FileUploadUtils;
import com.debuff.common.utils.file.FileUtils;
import com.debuff.framework.config.ServerConfig;

/**
 * 通用请求处理
 * 
 * @author ruoyi
 */
// 标记为REST控制器，统一映射路径为/common
@RestController
@RequestMapping("/common")
public class CommonController
{
    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    // 注入服务器配置，用于获取URL前缀
    @Autowired
    private ServerConfig serverConfig;

    // 多文件上传时文件名之间的分隔符
    private static final String FILE_DELIMETER = ",";

    /**
     * 通用下载请求
     * 
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            // 检查文件名是否合法，防止路径穿越攻击
            if (!FileUtils.checkAllowDownload(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            // 去掉前缀生成真实文件名
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            // 拼接文件完整路径
            String filePath = RuoYiConfig.getDownloadPath() + fileName;

            // 设置响应类型为二进制流
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            // 设置下载文件的响应头
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            // 将文件内容写入响应输出流
            FileUtils.writeBytes(filePath, response.getOutputStream());
            // 如果需要下载后删除文件
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
            // 记录下载失败日志
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            // 拼接完整访问URL
            String url = serverConfig.getUrl() + fileName;
            // 构建成功返回结果
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url); // 文件访问地址
            ajax.put("fileName", fileName); // 服务器存储文件名
            ajax.put("newFileName", FileUtils.getName(fileName)); // 不含路径的文件名
            ajax.put("originalFilename", file.getOriginalFilename()); // 原始文件名
            return ajax;
        }
        catch (Exception e)
        {
            // 返回错误信息
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 存储上传后的URL列表
            List<String> urls = new ArrayList<String>();
            // 存储文件名列表
            List<String> fileNames = new ArrayList<String>();
            // 存储新文件名列表
            List<String> newFileNames = new ArrayList<String>();
            // 存储原始文件名列表
            List<String> originalFilenames = new ArrayList<String>();
            // 遍历所有上传文件
            for (MultipartFile file : files)
            {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                // 拼接完整访问URL
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            // 构建成功返回结果
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER)); // 逗号拼接的URL
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER)); // 逗号拼接的文件名
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER)); // 逗号拼接的新文件名
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER)); // 逗号拼接的原始文件名
            return ajax;
        }
        catch (Exception e)
        {
            // 返回错误信息
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        try
        {
            // 检查资源文件是否允许下载
            if (!FileUtils.checkAllowDownload(resource))
            {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = RuoYiConfig.getProfile();
            // 数据库资源地址：拼接本地路径和去掉前缀后的资源路径
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称：提取文件名部分
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            // 设置响应类型为二进制流
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            // 设置下载文件响应头
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            // 将文件内容写入响应输出流
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            // 记录下载失败日志
            log.error("下载文件失败", e);
        }
    }
}
