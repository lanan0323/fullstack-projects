// 包声明：文件上传工具类所在包
package com.debuff.common.utils.file;

// 导入文件IO、路径、Spring Multipart、配置、常量、异常、日期、字符串、序列等相关类
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import com.debuff.common.config.RuoYiConfig;
import com.debuff.common.constant.Constants;
import com.debuff.common.exception.file.FileNameLengthLimitExceededException;
import com.debuff.common.exception.file.FileSizeLimitExceededException;
import com.debuff.common.exception.file.InvalidExtensionException;
import com.debuff.common.utils.DateUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.uuid.Seq;

/**
 * 文件上传工具类
 *
 * @author ruoyi
 */
public class FileUploadUtils // 文件上传工具类，处理文件上传、校验、路径生成
{
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024; // 默认最大文件大小50MB

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100; // 默认文件名最大长度100字符

    /**
     * 默认上传的地址
     */
    private static String defaultBaseDir = RuoYiConfig.getProfile(); // 默认上传根目录

    public static void setDefaultBaseDir(String defaultBaseDir) // 设置默认上传根目录
    {
        FileUploadUtils.defaultBaseDir = defaultBaseDir; // 赋值默认目录
    }

    public static String getDefaultBaseDir() // 获取默认上传根目录
    {
        return defaultBaseDir; // 返回默认目录
    }

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final String upload(MultipartFile file) throws IOException // 使用默认配置上传文件
    {
        try
        {
            return upload(getDefaultBaseDir(), file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION); // 调用完整上传方法
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e); // 包装为IOException抛出
        }
    }

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file 上传的文件
     * @return 文件名称
     * @throws IOException
     */
    public static final String upload(String baseDir, MultipartFile file) throws IOException // 指定目录上传文件
    {
        try
        {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION); // 调用完整上传方法
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e); // 包装为IOException抛出
        }
    }

    /**
     * 文件上传
     *
     * @param baseDir 相对应用的基目录
     * @param file 上传的文件
     * @param allowedExtension 上传文件类型
     * @return 返回上传成功的文件名
     * @throws FileSizeLimitExceededException 如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException 比如读写文件出错时
     * @throws InvalidExtensionException 文件校验异常
     */
    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension) // 完整文件上传：校验+编码文件名+写入磁盘
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException,
            InvalidExtensionException
    {
        int fileNamelength = Objects.requireNonNull(file.getOriginalFilename()).length(); // 获取原始文件名长度
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) // 文件名超长时
        {
            throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH); // 抛出文件名长度超限异常
        }

        assertAllowed(file, allowedExtension); // 校验文件大小和扩展名

        String fileName = extractFilename(file); // 生成编码后的文件名

        String absPath = getAbsoluteFile(baseDir, fileName).getAbsolutePath(); // 获取绝对路径
        file.transferTo(Paths.get(absPath)); // 将文件写入磁盘
        return getPathFileName(baseDir, fileName); // 返回可访问的文件路径
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file) // 生成日期目录/原始名_序列号.后缀的文件名
    {
        return StringUtils.format("{}/{}_{}.{}", DateUtils.datePath(), // 日期路径
                FilenameUtils.getBaseName(file.getOriginalFilename()), Seq.getId(Seq.uploadSeqType), getExtension(file)); // 原始名_序列号.后缀
    }

    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException // 获取绝对路径文件对象，自动创建目录
    {
        File desc = new File(uploadDir + File.separator + fileName); // 拼接完整路径

        if (!desc.exists()) // 文件不存在时
        {
            if (!desc.getParentFile().exists()) // 父目录不存在时
            {
                desc.getParentFile().mkdirs(); // 递归创建父目录
            }
        }
        return desc; // 返回文件对象
    }

    public static final String getPathFileName(String uploadDir, String fileName) throws IOException // 生成可访问的文件URL路径
    {
        int dirLastIndex = RuoYiConfig.getProfile().length() + 1; // 计算根目录后的索引位置
        String currentDir = StringUtils.substring(uploadDir, dirLastIndex); // 截取相对目录
        return Constants.RESOURCE_PREFIX + "/" + currentDir + "/" + fileName; // 拼接资源前缀+目录+文件名
    }

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     * @return
     * @throws FileSizeLimitExceededException 如果超出最大大小
     * @throws InvalidExtensionException
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension) // 校验文件大小和扩展名是否合法
            throws FileSizeLimitExceededException, InvalidExtensionException
    {
        long size = file.getSize(); // 获取文件大小
        if (size > DEFAULT_MAX_SIZE) // 超过最大限制时
        {
            throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024); // 抛出文件大小超限异常
        }

        String fileName = file.getOriginalFilename(); // 获取原始文件名
        String extension = getExtension(file); // 获取文件后缀
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) // 后缀不在允许列表中时
        {
            if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) // 图片类型
            {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension,
                        fileName); // 抛出图片扩展名异常
            }
            else if (allowedExtension == MimeTypeUtils.FLASH_EXTENSION) // Flash类型
            {
                throw new InvalidExtensionException.InvalidFlashExtensionException(allowedExtension, extension,
                        fileName); // 抛出Flash扩展名异常
            }
            else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) // 媒体类型
            {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension,
                        fileName); // 抛出媒体扩展名异常
            }
            else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) // 视频类型
            {
                throw new InvalidExtensionException.InvalidVideoExtensionException(allowedExtension, extension,
                        fileName); // 抛出视频扩展名异常
            }
            else
            {
                throw new InvalidExtensionException(allowedExtension, extension, fileName); // 抛出通用扩展名异常
            }
        }
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension
     * @param allowedExtension
     * @return
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) // 判断扩展名是否在允许列表中
    {
        for (String str : allowedExtension) // 遍历允许的后缀数组
        {
            if (str.equalsIgnoreCase(extension)) // 忽略大小写比较
            {
                return true; // 匹配成功返回true
            }
        }
        return false; // 都不匹配返回false
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) // 获取上传文件的后缀名
    {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()); // 从文件名获取后缀
        if (StringUtils.isEmpty(extension)) // 文件名为空后缀时
        {
            extension = MimeTypeUtils.getExtension(Objects.requireNonNull(file.getContentType())); // 从ContentType获取后缀
        }
        return extension; // 返回后缀名
    }
}
