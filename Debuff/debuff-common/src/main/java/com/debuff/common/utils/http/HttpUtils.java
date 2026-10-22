// 包声明
package com.debuff.common.utils.http;

// 导入IO流、网络、SSL、日志等相关类
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.debuff.common.constant.Constants;
import com.debuff.common.utils.StringUtils;

// 通用HTTP请求工具类，提供GET/POST/SSL请求方法
public class HttpUtils
{
    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    // 无参GET请求，调用带参数的重载方法
    public static String sendGet(String url)
    {
        return sendGet(url, StringUtils.EMPTY);
    }

    // 带参数的GET请求，默认UTF-8编码
    public static String sendGet(String url, String param)
    {
        return sendGet(url, param, Constants.UTF8);
    }

    // 向指定URL发送GET请求的核心方法
    public static String sendGet(String url, String param, String contentType)
    {
        StringBuilder result = new StringBuilder(); // 拼接响应结果
        BufferedReader in = null; // 输入流读取器
        try
        {
            // 拼接完整请求URL（有参数则加?拼接）
            String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
            log.info("sendGet - {}", urlNameString); // 记录请求URL
            URL realUrl = new URL(urlNameString); // 创建URL对象
            URLConnection connection = realUrl.openConnection(); // 打开连接
            connection.setRequestProperty("accept", "*/*"); // 设置接受任意类型
            connection.setRequestProperty("connection", "Keep-Alive"); // 保持长连接
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); // 设置用户代理
            connection.connect(); // 建立连接
            // 创建带指定编码的输入流读取器
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), contentType));
            String line;
            // 逐行读取响应内容
            while ((line = in.readLine()) != null)
            {
                result.append(line); // 追加到结果中
            }
            log.info("recv - {}", result); // 记录响应内容
        }
        catch (ConnectException e) // 连接异常
        {
            log.error("调用HttpUtils.sendGet ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e) // 超时异常
        {
            log.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e) // IO异常
        {
            log.error("调用HttpUtils.sendGet IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e) // 其他异常
        {
            log.error("调用HttpsUtil.sendGet Exception, url=" + url + ",param=" + param, e);
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close(); // 关闭输入流
                }
            }
            catch (Exception ex)
            {
                log.error("调用in.close Exception, url=" + url + ",param=" + param, ex);
            }
        }
        return result.toString(); // 返回响应字符串
    }

    // 向指定URL发送POST请求
    public static String sendPost(String url, String param)
    {
        PrintWriter out = null; // 输出流
        BufferedReader in = null; // 输入流
        StringBuilder result = new StringBuilder(); // 响应结果
        try
        {
            log.info("sendPost - {}", url); // 记录请求URL
            URL realUrl = new URL(url); // 创建URL对象
            URLConnection conn = realUrl.openConnection(); // 打开连接
            conn.setRequestProperty("accept", "*/*"); // 设置接受类型
            conn.setRequestProperty("connection", "Keep-Alive"); // 保持长连接
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); // 用户代理
            conn.setRequestProperty("Accept-Charset", "utf-8"); // 设置请求编码
            conn.setRequestProperty("contentType", "utf-8"); // 设置内容类型编码
            conn.setDoOutput(true); // 允许输出（发送请求体）
            conn.setDoInput(true); // 允许输入（读取响应）
            out = new PrintWriter(conn.getOutputStream()); // 获取输出流
            out.print(param); // 写入请求参数
            out.flush(); // 刷新输出缓冲区
            // 创建UTF-8编码的输入流读取器
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            // 逐行读取响应
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            log.info("recv - {}", result); // 记录响应
        }
        catch (ConnectException e) // 连接异常
        {
            log.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e) // 超时异常
        {
            log.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e) // IO异常
        {
            log.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e) // 其他异常
        {
            log.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + param, e);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close(); // 关闭输出流
                }
                if (in != null)
                {
                    in.close(); // 关闭输入流
                }
            }
            catch (IOException ex)
            {
                log.error("调用in.close Exception, url=" + url + ",param=" + param, ex);
            }
        }
        return result.toString(); // 返回响应字符串
    }

    // 发送HTTPS的POST请求（信任所有证书）
    public static String sendSSLPost(String url, String param)
    {
        StringBuilder result = new StringBuilder(); // 响应结果
        String urlNameString = url + "?" + param; // 拼接完整URL
        try
        {
            log.info("sendSSLPost - {}", urlNameString); // 记录请求URL
            SSLContext sc = SSLContext.getInstance("SSL"); // 获取SSL上下文
            // 初始化SSL上下文，使用信任所有证书的管理器
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
            URL console = new URL(urlNameString); // 创建URL对象
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection(); // 打开HTTPS连接
            conn.setRequestProperty("accept", "*/*"); // 设置接受类型
            conn.setRequestProperty("connection", "Keep-Alive"); // 保持长连接
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); // 用户代理
            conn.setRequestProperty("Accept-Charset", "utf-8"); // 请求编码
            conn.setRequestProperty("contentType", "utf-8"); // 内容编码
            conn.setDoOutput(true); // 允许输出
            conn.setDoInput(true); // 允许输入

            conn.setSSLSocketFactory(sc.getSocketFactory()); // 设置SSL套接字工厂
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier()); // 设置主机名验证器（信任所有）
            conn.connect(); // 建立连接
            InputStream is = conn.getInputStream(); // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(is)); // 创建读取器
            String ret = "";
            // 逐行读取响应
            while ((ret = br.readLine()) != null)
            {
                if (ret != null && !"".equals(ret.trim()))
                {
                    // 将ISO-8859-1编码转为UTF-8
                    result.append(new String(ret.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                }
            }
            log.info("recv - {}", result); // 记录响应
            conn.disconnect(); // 断开连接
            br.close(); // 关闭读取器
        }
        catch (ConnectException e) // 连接异常
        {
            log.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e) // 超时异常
        {
            log.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e) // IO异常
        {
            log.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e) // 其他异常
        {
            log.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, e);
        }
        return result.toString(); // 返回响应字符串
    }

    // 信任所有证书的SSL信任管理器（内部类）
    private static class TrustAnyTrustManager implements X509TrustManager
    {
        // 检查客户端证书（不做验证）
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
        {
        }

        // 检查服务端证书（不做验证）
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
        {
        }

        // 返回接受的证书颁发者（返回空数组）
        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[] {};
        }
    }

    // 信任所有主机名的验证器（内部类）
    private static class TrustAnyHostnameVerifier implements HostnameVerifier
    {
        // 验证主机名（始终返回true）
        @Override
        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    }
}