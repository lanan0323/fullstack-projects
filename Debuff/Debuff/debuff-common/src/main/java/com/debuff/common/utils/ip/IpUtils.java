// 包声明
package com.debuff.common.utils.ip;

// 导入网络、Servlet、字符串工具类
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import com.debuff.common.utils.ServletUtils;
import com.debuff.common.utils.StringUtils;

// IP地址工具类，提供IP获取、验证、通配符匹配、网段匹配等功能
public class IpUtils
{
    // 0-255数字的正则表达式
    public final static String REGX_0_255 = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
    // 匹配完整IP地址的正则
    public final static String REGX_IP = "((" + REGX_0_255 + "\\.){3}" + REGX_0_255 + ")";
    // 匹配IP通配符的正则（如 *.*.*.* 或 192.*.*.* 等）
    public final static String REGX_IP_WILDCARD = "(((\\*\\.){3}\\*)|(" + REGX_0_255 + "(\\.\\*){3})|(" + REGX_0_255 + "\\." + REGX_0_255 + ")(\\.\\*){2}" + "|((" + REGX_0_255 + "\\.){3}\\*))";
    // 匹配IP网段的正则（如 10.10.10.1-10.10.10.99）
    public final static String REGX_IP_SEG = "(" + REGX_IP + "\\-" + REGX_IP + ")";

    // 获取当前请求的客户端IP
    public static String getIpAddr()
    {
        return getIpAddr(ServletUtils.getRequest());
    }

    // 从HttpServletRequest中获取客户端真实IP地址
    public static String getIpAddr(HttpServletRequest request)
    {
        if (request == null) // 请求为空返回unknown
        {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for"); // 从X-Forwarded-For头获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP"); // 从Proxy-Client-IP头获取
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("X-Forwarded-For"); // 从X-Forwarded-For头获取（大写）
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP"); // 从WL-Proxy-Client-IP头获取
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("X-Real-IP"); // 从X-Real-IP头获取
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr(); // 从远程地址获取
        }

        // IPv6本地地址转为IPv4格式，并处理多级代理
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    // 判断是否为内网IP地址
    public static boolean internalIp(String ip)
    {
        byte[] addr = textToNumericFormatV4(ip); // 将IP转为字节数组
        return internalIp(addr) || "127.0.0.1".equals(ip);
    }

    // 通过字节数组判断是否为内网IP
    private static boolean internalIp(byte[] addr)
    {
        if (StringUtils.isNull(addr) || addr.length < 2)
        {
            return true;
        }
        final byte b0 = addr[0]; // 第一段
        final byte b1 = addr[1]; // 第二段
        // 10.x.x.x/8 网段标识
        final byte SECTION_1 = 0x0A;
        // 172.16.x.x/12 网段标识
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        // 192.168.x.x/16 网段标识
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0)
        {
            case SECTION_1: // 10.x.x.x
                return true;
            case SECTION_2: // 172.16-31.x.x
                if (b1 >= SECTION_3 && b1 <= SECTION_4)
                {
                    return true;
                }
            case SECTION_5: // 192.168.x.x
                switch (b1)
                {
                    case SECTION_6:
                        return true;
                }
            default:
                return false; // 非内网IP
        }
    }

    // 将IPv4地址字符串转换为4字节数组
    public static byte[] textToNumericFormatV4(String text)
    {
        if (text.length() == 0) // 空字符串返回null
        {
            return null;
        }

        byte[] bytes = new byte[4]; // 4字节数组存储IP
        String[] elements = text.split("\\.", -1); // 按.分割IP
        try
        {
            long l;
            int i;
            switch (elements.length) // 根据分割段数处理不同格式
            {
                case 1: // 单段格式（如 2130706999）
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L)) // 超出范围
                    {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF); // 取最高8位
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF); // 取次高8位
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF); // 取次低8位
                    bytes[3] = (byte) (int) (l & 0xFF); // 取最低8位
                    break;
                case 2: // 两段格式（如 10.12345）
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L))
                    {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L))
                    {
                        return null;
                    }
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3: // 三段格式（如 10.10.1234）
                    for (i = 0; i < 2; ++i)
                    {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L))
                        {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L))
                    {
                        return null;
                    }
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4: // 标准四段格式（如 10.10.10.1）
                    for (i = 0; i < 4; ++i)
                    {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L))
                        {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default: // 其他格式返回null
                    return null;
            }
        }
        catch (NumberFormatException e) // 数字格式异常
        {
            return null;
        }
        return bytes;
    }

    // 获取本机IP地址
    public static String getHostIp()
    {
        try
        {
            return InetAddress.getLocalHost().getHostAddress(); // 获取本机IP
        }
        catch (UnknownHostException e)
        {
        }
        return "127.0.0.1"; // 获取失败返回本地回环地址
    }

    // 获取本机主机名
    public static String getHostName()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName(); // 获取主机名
        }
        catch (UnknownHostException e)
        {
        }
        return "未知"; // 获取失败返回"未知"
    }

    // 从多级反向代理中提取第一个非unknown的IP
    public static String getMultistageReverseProxyIp(String ip)
    {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0) // 包含逗号说明有多级代理
        {
            final String[] ips = ip.trim().split(","); // 按逗号分割
            for (String subIp : ips)
            {
                if (false == isUnknown(subIp)) // 找到第一个非unknown的IP
                {
                    ip = subIp;
                    break;
                }
            }
        }
        return StringUtils.substring(ip, 0, 255); // 截取最多255个字符
    }

    // 检测字符串是否为"unknown"
    public static boolean isUnknown(String checkString)
    {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    // 判断字符串是否为合法IP地址
    public static boolean isIP(String ip)
    {
        return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP);
    }

    // 判断是否为IP通配符格式（如 192.*.*.*）
    public static boolean isIpWildCard(String ip)
    {
        return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP_WILDCARD);
    }

    // 检测IP是否匹配通配符规则（不校验参数合法性）
    public static boolean ipIsInWildCardNoCheck(String ipWildCard, String ip)
    {
        String[] s1 = ipWildCard.split("\\."); // 分割通配符IP
        String[] s2 = ip.split("\\."); // 分割目标IP
        boolean isMatchedSeg = true; // 是否匹配
        // 逐段比较，遇到*号停止
        for (int i = 0; i < s1.length && !s1[i].equals("*"); i++)
        {
            if (!s1[i].equals(s2[i])) // 某段不匹配
            {
                isMatchedSeg = false;
                break;
            }
        }
        return isMatchedSeg;
    }

    // 判断是否为IP网段格式（如 10.10.10.1-10.10.10.99）
    public static boolean isIPSegment(String ipSeg)
    {
        return StringUtils.isNotBlank(ipSeg) && ipSeg.matches(REGX_IP_SEG);
    }

    // 判断IP是否在指定网段内（不校验参数合法性）
    public static boolean ipIsInNetNoCheck(String iparea, String ip)
    {
        int idx = iparea.indexOf('-'); // 找到分隔符位置
        String[] sips = iparea.substring(0, idx).split("\\."); // 起始IP各段
        String[] sipe = iparea.substring(idx + 1).split("\\."); // 结束IP各段
        String[] sipt = ip.split("\\."); // 目标IP各段
        long ips = 0L, ipe = 0L, ipt = 0L; // 起始/结束/目标的long值
        // 将4段IP拼接为long型数值
        for (int i = 0; i < 4; ++i)
        {
            ips = ips << 8 | Integer.parseInt(sips[i]); // 起始IP
            ipe = ipe << 8 | Integer.parseInt(sipe[i]); // 结束IP
            ipt = ipt << 8 | Integer.parseInt(sipt[i]); // 目标IP
        }
        if (ips > ipe) // 起始大于结束则交换
        {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe; // 判断是否在范围内
    }

    // 校验IP是否符合过滤规则（支持精确匹配、通配符、网段）
    public static boolean isMatchedIp(String filter, String ip)
    {
        if (StringUtils.isEmpty(filter) || StringUtils.isEmpty(ip)) // 参数为空
        {
            return false;
        }
        String[] ips = filter.split(";"); // 按分号分割多个规则
        for (String iStr : ips)
        {
            if (isIP(iStr) && iStr.equals(ip)) // 精确匹配
            {
                return true;
            }
            else if (isIpWildCard(iStr) && ipIsInWildCardNoCheck(iStr, ip)) // 通配符匹配
            {
                return true;
            }
            else if (isIPSegment(iStr) && ipIsInNetNoCheck(iStr, ip)) // 网段匹配
            {
                return true;
            }
        }
        return false; // 都不匹配
    }
}