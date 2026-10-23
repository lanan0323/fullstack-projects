// 包声明：HTML过滤器所在包
package com.debuff.common.utils.html;

// 导入集合、并发Map、正则等相关类
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML过滤器，用于去除XSS漏洞隐患。
 *
 * @author ruoyi
 */
public final class HTMLFilter // HTML过滤器，过滤XSS危险标签和属性
{
    /**
     * regex flag union representing /si modifiers in php
     **/
    private static final int REGEX_FLAGS_SI = Pattern.CASE_INSENSITIVE | Pattern.DOTALL; // 正则标志：忽略大小写+点匹配换行
    private static final Pattern P_COMMENTS = Pattern.compile("<!--(.*?)-->", Pattern.DOTALL); // 匹配HTML注释
    private static final Pattern P_COMMENT = Pattern.compile("^!--(.*)--$", REGEX_FLAGS_SI); // 匹配注释内容
    private static final Pattern P_TAGS = Pattern.compile("<(.*?)>", Pattern.DOTALL); // 匹配所有标签
    private static final Pattern P_END_TAG = Pattern.compile("^/([a-z0-9]+)", REGEX_FLAGS_SI); // 匹配结束标签
    private static final Pattern P_START_TAG = Pattern.compile("^([a-z0-9]+)(.*?)(/?)$", REGEX_FLAGS_SI); // 匹配开始标签
    private static final Pattern P_QUOTED_ATTRIBUTES = Pattern.compile("([a-z0-9]+)=([\"'])(.*?)\\2", REGEX_FLAGS_SI); // 匹配带引号的属性
    private static final Pattern P_UNQUOTED_ATTRIBUTES = Pattern.compile("([a-z0-9]+)(=)([^\"\\s']+)", REGEX_FLAGS_SI); // 匹配不带引号的属性
    private static final Pattern P_PROTOCOL = Pattern.compile("^([^:]+):", REGEX_FLAGS_SI); // 匹配协议头
    private static final Pattern P_ENTITY = Pattern.compile("&#(\\d+);?"); // 匹配十进制实体
    private static final Pattern P_ENTITY_UNICODE = Pattern.compile("&#x([0-9a-f]+);?"); // 匹配Unicode实体
    private static final Pattern P_ENCODE = Pattern.compile("%([0-9a-f]{2});?"); // 匹配百分号编码
    private static final Pattern P_VALID_ENTITIES = Pattern.compile("&([^&;]*)(?=(;|&|$))"); // 匹配有效实体
    private static final Pattern P_VALID_QUOTES = Pattern.compile("(>|^)([^<]+?)(<|$)", Pattern.DOTALL); // 匹配引号内的内容
    private static final Pattern P_END_ARROW = Pattern.compile("^>"); // 匹配结尾>
    private static final Pattern P_BODY_TO_END = Pattern.compile("<([^>]*?)(?=<|$)"); // 匹配到末尾的内容
    private static final Pattern P_XML_CONTENT = Pattern.compile("(^|>)([^<]*?)(?=>)"); // 匹配XML内容
    private static final Pattern P_STRAY_LEFT_ARROW = Pattern.compile("<([^>]*?)(?=<|$)"); // 匹配孤立的<
    private static final Pattern P_STRAY_RIGHT_ARROW = Pattern.compile("(^|>)([^<]*?)(?=>)"); // 匹配孤立的>
    private static final Pattern P_AMP = Pattern.compile("&"); // 匹配&
    private static final Pattern P_QUOTE = Pattern.compile("\""); // 匹配"
    private static final Pattern P_LEFT_ARROW = Pattern.compile("<"); // 匹配<
    private static final Pattern P_RIGHT_ARROW = Pattern.compile(">"); // 匹配>
    private static final Pattern P_BOTH_ARROWS = Pattern.compile("<>"); // 匹配<>

    // @xxx could grow large... maybe use sesat's ReferenceMap
    private static final ConcurrentMap<String, Pattern> P_REMOVE_PAIR_BLANKS = new ConcurrentHashMap<>(); // 移除空内容标签对的缓存
    private static final ConcurrentMap<String, Pattern> P_REMOVE_SELF_BLANKS = new ConcurrentHashMap<>(); // 移除空自闭合标签的缓存

    /**
     * set of allowed html elements, along with allowed attributes for each element
     **/
    private final Map<String, List<String>> vAllowed; // 允许的HTML标签及其属性映射
    /**
     * counts of open tags for each (allowable) html element
     **/
    private final Map<String, Integer> vTagCounts = new HashMap<>(); // 各标签的开标签计数

    /**
     * html elements which must always be self-closing (e.g. "<img />")
     **/
    private final String[] vSelfClosingTags; // 必须自闭合的标签
    /**
     * html elements which must always have separate opening and closing tags (e.g. "<b></b>")
     **/
    private final String[] vNeedClosingTags; // 必须有闭合标签的标签
    /**
     * set of disallowed html elements
     **/
    private final String[] vDisallowed; // 禁止的标签
    /**
     * attributes which should be checked for valid protocols
     **/
    private final String[] vProtocolAtts; // 需要检查协议的属性
    /**
     * allowed protocols
     **/
    private final String[] vAllowedProtocols; // 允许的协议
    /**
     * tags which should be removed if they contain no content (e.g. "<b></b>" or "<b />")
     **/
    private final String[] vRemoveBlanks; // 无内容时需移除的标签
    /**
     * entities allowed within html markup
     **/
    private final String[] vAllowedEntities; // 允许的实体
    /**
     * flag determining whether comments are allowed in input String.
     */
    private final boolean stripComment; // 是否去除注释
    private final boolean encodeQuotes; // 是否编码引号
    /**
     * flag determining whether to try to make tags when presented with "unbalanced" angle brackets (e.g. "<b text </b>"
     * becomes "<b> text </b>"). If set to false, unbalanced angle brackets will be html escaped.
     */
    private final boolean alwaysMakeTags; // 是否总是尝试生成标签

    /**
     * Default constructor.
     */
    public HTMLFilter() // 默认构造，初始化允许的标签和规则
    {
        vAllowed = new HashMap<>(); // 创建允许标签映射

        final ArrayList<String> a_atts = new ArrayList<>(); // a标签的属性列表
        a_atts.add("href"); // 添加href属性
        a_atts.add("target"); // 添加target属性
        vAllowed.put("a", a_atts); // 允许a标签

        final ArrayList<String> img_atts = new ArrayList<>(); // img标签的属性列表
        img_atts.add("src"); // 添加src属性
        img_atts.add("width"); // 添加width属性
        img_atts.add("height"); // 添加height属性
        img_atts.add("alt"); // 添加alt属性
        vAllowed.put("img", img_atts); // 允许img标签

        final ArrayList<String> no_atts = new ArrayList<>(); // 无属性的标签
        vAllowed.put("b", no_atts); // 允许b标签
        vAllowed.put("strong", no_atts); // 允许strong标签
        vAllowed.put("i", no_atts); // 允许i标签
        vAllowed.put("em", no_atts); // 允许em标签

        vSelfClosingTags = new String[] { "img" }; // 自闭合标签列表
        vNeedClosingTags = new String[] { "a", "b", "strong", "i", "em" }; // 需闭合标签列表
        vDisallowed = new String[] {}; // 禁止标签列表（空）
        vAllowedProtocols = new String[] { "http", "mailto", "https" }; // no ftp. // 允许协议
        vProtocolAtts = new String[] { "src", "href" }; // 需检查协议的属性
        vRemoveBlanks = new String[] { "a", "b", "strong", "i", "em" }; // 无内容时移除的标签
        vAllowedEntities = new String[] { "amp", "gt", "lt", "quot" }; // 允许的实体
        stripComment = true; // 去除注释
        encodeQuotes = true; // 编码引号
        alwaysMakeTags = false; // 不强制生成标签
    }

    /**
     * Map-parameter configurable constructor.
     *
     * @param conf map containing configuration. keys match field names.
     */
    @SuppressWarnings("unchecked")
    public HTMLFilter(final Map<String, Object> conf) // 通过Map配置构造
    {

        assert conf.containsKey("vAllowed") : "configuration requires vAllowed"; // 断言必须包含vAllowed
        assert conf.containsKey("vSelfClosingTags") : "configuration requires vSelfClosingTags"; // 断言必须包含vSelfClosingTags
        assert conf.containsKey("vNeedClosingTags") : "configuration requires vNeedClosingTags"; // 断言必须包含vNeedClosingTags
        assert conf.containsKey("vDisallowed") : "configuration requires vDisallowed"; // 断言必须包含vDisallowed
        assert conf.containsKey("vAllowedProtocols") : "configuration requires vAllowedProtocols"; // 断言必须包含vAllowedProtocols
        assert conf.containsKey("vProtocolAtts") : "configuration requires vProtocolAtts"; // 断言必须包含vProtocolAtts
        assert conf.containsKey("vRemoveBlanks") : "configuration requires vRemoveBlanks"; // 断言必须包含vRemoveBlanks
        assert conf.containsKey("vAllowedEntities") : "configuration requires vAllowedEntities"; // 断言必须包含vAllowedEntities

        vAllowed = Collections.unmodifiableMap((HashMap<String, List<String>>) conf.get("vAllowed")); // 设置允许标签（不可变）
        vSelfClosingTags = (String[]) conf.get("vSelfClosingTags"); // 设置自闭合标签
        vNeedClosingTags = (String[]) conf.get("vNeedClosingTags"); // 设置需闭合标签
        vDisallowed = (String[]) conf.get("vDisallowed"); // 设置禁止标签
        vAllowedProtocols = (String[]) conf.get("vAllowedProtocols"); // 设置允许协议
        vProtocolAtts = (String[]) conf.get("vProtocolAtts"); // 设置协议检查属性
        vRemoveBlanks = (String[]) conf.get("vRemoveBlanks"); // 设置移除空白标签
        vAllowedEntities = (String[]) conf.get("vAllowedEntities"); // 设置允许实体
        stripComment = conf.containsKey("stripComment") ? (Boolean) conf.get("stripComment") : true; // 设置是否去除注释
        encodeQuotes = conf.containsKey("encodeQuotes") ? (Boolean) conf.get("encodeQuotes") : true; // 设置是否编码引号
        alwaysMakeTags = conf.containsKey("alwaysMakeTags") ? (Boolean) conf.get("alwaysMakeTags") : true; // 设置是否总是生成标签
    }

    private void reset() // 重置标签计数
    {
        vTagCounts.clear(); // 清空标签计数
    }

    // ---------------------------------------------------------------
    // my versions of some PHP library functions
    public static String chr(final int decimal) // 将十进制转为字符
    {
        return String.valueOf((char) decimal); // 转为字符
    }

    public static String htmlSpecialChars(final String s) // 转义HTML特殊字符
    {
        String result = s; // 初始化结果
        result = regexReplace(P_AMP, "&amp;", result); // &转义为&amp;
        result = regexReplace(P_QUOTE, "&quot;", result); // "转义为&quot;
        result = regexReplace(P_LEFT_ARROW, "&lt;", result); // <转义为&lt;
        result = regexReplace(P_RIGHT_ARROW, "&gt;", result); // >转义为&gt;
        return result; // 返回转义后的字符串
    }

    // ---------------------------------------------------------------

    /**
     * given a user submitted input String, filter out any invalid or restricted html.
     *
     * @param input text (i.e. submitted by a user) than may contain html
     * @return "clean" version of input, with only valid, whitelisted html elements allowed
     */
    public String filter(final String input) // 过滤HTML，去除XSS危险内容
    {
        reset(); // 重置标签计数
        String s = input; // 初始化处理字符串

        s = escapeComments(s); // 转义注释

        s = balanceHTML(s); // 平衡HTML标签

        s = checkTags(s); // 检查标签

        s = processRemoveBlanks(s); // 移除空白标签

        // s = validateEntities(s);

        return s; // 返回过滤后的字符串
    }

    public boolean isAlwaysMakeTags() // 是否总是生成标签
    {
        return alwaysMakeTags; // 返回配置
    }

    public boolean isStripComments() // 是否去除注释
    {
        return stripComment; // 返回配置
    }

    private String escapeComments(final String s) // 转义HTML注释中的内容
    {
        final Matcher m = P_COMMENTS.matcher(s); // 匹配注释
        final StringBuffer buf = new StringBuffer(); // 创建缓冲区
        if (m.find()) // 找到注释时
        {
            final String match = m.group(1); // (.*?) // 获取注释内容
            m.appendReplacement(buf, Matcher.quoteReplacement("<!--" + htmlSpecialChars(match) + "-->")); // 转义注释内容
        }
        m.appendTail(buf); // 追加剩余内容

        return buf.toString(); // 返回结果
    }

    private String balanceHTML(String s) // 平衡HTML标签
    {
        if (alwaysMakeTags) // 总是生成标签模式
        {
            //
            // try and form html
            //
            s = regexReplace(P_END_ARROW, "", s); // 移除末尾>
            // 不追加结束标签
            s = regexReplace(P_BODY_TO_END, "<$1>", s); // 补充结束标签
            s = regexReplace(P_XML_CONTENT, "$1<$2", s); // 处理XML内容

        }
        else
        {
            //
            // escape stray brackets
            //
            s = regexReplace(P_STRAY_LEFT_ARROW, "&lt;$1", s); // 转义孤立的<
            s = regexReplace(P_STRAY_RIGHT_ARROW, "$1$2&gt;<", s); // 转义孤立的>

            //
            // the last regexp causes '<>' entities to appear
            // (we need to do a lookahead assertion so that the last bracket can
            // be used in the next pass of the regexp)
            //
            s = regexReplace(P_BOTH_ARROWS, "", s); // 移除<>
        }

        return s; // 返回处理后的字符串
    }

    private String checkTags(String s) // 检查并处理所有标签
    {
        Matcher m = P_TAGS.matcher(s); // 匹配所有标签

        final StringBuffer buf = new StringBuffer(); // 创建缓冲区
        while (m.find()) // 遍历所有标签
        {
            String replaceStr = m.group(1); // 获取标签内容
            replaceStr = processTag(replaceStr); // 处理单个标签
            m.appendReplacement(buf, Matcher.quoteReplacement(replaceStr)); // 替换
        }
        m.appendTail(buf); // 追加剩余内容

        // these get tallied in processTag
        // (remember to reset before subsequent calls to filter method)
        final StringBuilder sBuilder = new StringBuilder(buf.toString()); // 构建结果
        for (String key : vTagCounts.keySet()) // 遍历未闭合的标签
        {
            for (int ii = 0; ii < vTagCounts.get(key); ii++) // 补充闭合标签
            {
                sBuilder.append("</").append(key).append(">"); // 追加闭合标签
            }
        }
        s = sBuilder.toString(); // 转为字符串

        return s; // 返回结果
    }

    private String processRemoveBlanks(final String s) // 移除无内容的标签
    {
        String result = s; // 初始化结果
        for (String tag : vRemoveBlanks) // 遍历需移除空白内容的标签
        {
            if (!P_REMOVE_PAIR_BLANKS.containsKey(tag)) // 缓存中没有该标签的正则时
            {
                P_REMOVE_PAIR_BLANKS.putIfAbsent(tag, Pattern.compile("<" + tag + "(\\s[^>]*)?></" + tag + ">")); // 编译匹配空标签对的正则
            }
            result = regexReplace(P_REMOVE_PAIR_BLANKS.get(tag), "", result); // 移除空标签对
            if (!P_REMOVE_SELF_BLANKS.containsKey(tag)) // 缓存中没有自闭合正则时
            {
                P_REMOVE_SELF_BLANKS.putIfAbsent(tag, Pattern.compile("<" + tag + "(\\s[^>]*)?/>")); // 编译匹配自闭合标签的正则
            }
            result = regexReplace(P_REMOVE_SELF_BLANKS.get(tag), "", result); // 移除空自闭合标签
        }

        return result; // 返回结果
    }

    private static String regexReplace(final Pattern regex_pattern, final String replacement, final String s) // 正则替换
    {
        Matcher m = regex_pattern.matcher(s); // 创建匹配器
        return m.replaceAll(replacement); // 替换所有匹配
    }

    private String processTag(final String s) // 处理单个标签
    {
        // ending tags
        Matcher m = P_END_TAG.matcher(s); // 匹配结束标签
        if (m.find()) // 找到结束标签时
        {
            final String name = m.group(1).toLowerCase(); // 获取标签名（小写）
            if (allowed(name)) // 标签被允许时
            {
                if (!inArray(name, vSelfClosingTags)) // 不是自闭合标签时
                {
                    if (vTagCounts.containsKey(name)) // 有对应的开标签时
                    {
                        vTagCounts.put(name, vTagCounts.get(name) - 1); // 计数-1
                        return "</" + name + ">"; // 返回闭合标签
                    }
                }
            }
        }

        // starting tags
        m = P_START_TAG.matcher(s); // 匹配开始标签
        if (m.find()) // 找到开始标签时
        {
            final String name = m.group(1).toLowerCase(); // 获取标签名（小写）
            final String body = m.group(2); // 获取属性部分
            String ending = m.group(3); // 获取结尾部分

            // debug( "in a starting tag, name='" + name + "'; body='" + body + "'; ending='" + ending + "'" );
            if (allowed(name)) // 标签被允许时
            {
                final StringBuilder params = new StringBuilder(); // 构建属性字符串

                final Matcher m2 = P_QUOTED_ATTRIBUTES.matcher(body); // 匹配带引号属性
                final Matcher m3 = P_UNQUOTED_ATTRIBUTES.matcher(body); // 匹配不带引号属性
                final List<String> paramNames = new ArrayList<>(); // 属性名列表
                final List<String> paramValues = new ArrayList<>(); // 属性值列表
                while (m2.find()) // 遍历带引号属性
                {
                    paramNames.add(m2.group(1)); // ([a-z0-9]+) // 添加属性名
                    paramValues.add(m2.group(3)); // (.*?) // 添加属性值
                }
                while (m3.find()) // 遍历不带引号属性
                {
                    paramNames.add(m3.group(1)); // ([a-z0-9]+) // 添加属性名
                    paramValues.add(m3.group(3)); // ([^\"\\s']+) // 添加属性值
                }

                String paramName, paramValue; // 属性名和值
                for (int ii = 0; ii < paramNames.size(); ii++) // 遍历所有属性
                {
                    paramName = paramNames.get(ii).toLowerCase(); // 属性名转小写
                    paramValue = paramValues.get(ii); // 获取属性值

                    // debug( "paramName='" + paramName + "'" );
                    // debug( "paramValue='" + paramValue + "'" );
                    // debug( "allowed? " + vAllowed.get( name ).contains( paramName ) );

                    if (allowedAttribute(name, paramName)) // 属性被允许时
                    {
                        if (inArray(paramName, vProtocolAtts)) // 需要检查协议的属性
                        {
                            paramValue = processParamProtocol(paramValue); // 处理协议
                        }
                        params.append(' ').append(paramName).append("=\\\"").append(paramValue).append("\\\""); // 拼接属性
                    }
                }

                if (inArray(name, vSelfClosingTags)) // 自闭合标签
                {
                    ending = " /"; // 设置自闭合结尾
                }

                if (inArray(name, vNeedClosingTags)) // 需要闭合的标签
                {
                    ending = ""; // 不设置自闭合
                }

                if (ending == null || ending.length() < 1) // 非自闭合标签
                {
                    if (vTagCounts.containsKey(name)) // 已有开标签计数
                    {
                        vTagCounts.put(name, vTagCounts.get(name) + 1); // 计数+1
                    }
                    else
                    {
                        vTagCounts.put(name, 1); // 初始计数为1
                    }
                }
                else
                {
                    ending = " /"; // 自闭合结尾
                }
                return "<" + name + params + ending + ">"; // 返回处理后的标签
            }
            else
            {
                return ""; // 不允许的标签返回空
            }
        }

        // comments
        m = P_COMMENT.matcher(s); // 匹配注释
        if (!stripComment && m.find()) // 不去除注释且找到注释时
        {
            return "<" + m.group() + ">"; // 保留注释
        }

        return ""; // 其他情况返回空
    }

    private String processParamProtocol(String s) // 处理属性中的协议，过滤非法协议
    {
        s = decodeEntities(s); // 解码实体
        final Matcher m = P_PROTOCOL.matcher(s); // 匹配协议
        if (m.find()) // 找到协议时
        {
            final String protocol = m.group(1); // 获取协议名
            if (!inArray(protocol, vAllowedProtocols)) // 协议不在允许列表中
            {
                // bad protocol, turn into local anchor link instead
                s = "#" + s.substring(protocol.length() + 1); // 转为锚点链接
                if (s.startsWith("#//")) // 去除开头的//
                {
                    s = "#" + s.substring(3); // 去掉#//
                }
            }
        }

        return s; // 返回处理后的值
    }

    private String decodeEntities(String s) // 解码HTML实体和百分号编码
    {
        StringBuffer buf = new StringBuffer(); // 创建缓冲区

        Matcher m = P_ENTITY.matcher(s); // 匹配十进制实体
        while (m.find()) // 遍历所有实体
        {
            final String match = m.group(1); // 获取数字
            final int decimal = Integer.decode(match).intValue(); // 转为整数
            m.appendReplacement(buf, Matcher.quoteReplacement(chr(decimal))); // 替换为字符
        }
        m.appendTail(buf); // 追加剩余
        s = buf.toString(); // 转为字符串

        buf = new StringBuffer(); // 新建缓冲区
        m = P_ENTITY_UNICODE.matcher(s); // 匹配Unicode实体
        while (m.find()) // 遍历所有Unicode实体
        {
            final String match = m.group(1); // 获取十六进制值
            final int decimal = Integer.valueOf(match, 16).intValue(); // 转为整数
            m.appendReplacement(buf, Matcher.quoteReplacement(chr(decimal))); // 替换为字符
        }
        m.appendTail(buf); // 追加剩余
        s = buf.toString(); // 转为字符串

        buf = new StringBuffer(); // 新建缓冲区
        m = P_ENCODE.matcher(s); // 匹配百分号编码
        while (m.find()) // 遍历所有编码
        {
            final String match = m.group(1); // 获取十六进制值
            final int decimal = Integer.valueOf(match, 16).intValue(); // 转为整数
            m.appendReplacement(buf, Matcher.quoteReplacement(chr(decimal))); // 替换为字符
        }
        m.appendTail(buf); // 追加剩余
        s = buf.toString(); // 转为字符串

        s = validateEntities(s); // 验证实体
        return s; // 返回解码后的字符串
    }

    private String validateEntities(final String s) // 验证并过滤实体
    {
        StringBuffer buf = new StringBuffer(); // 创建缓冲区

        // validate entities throughout the string
        Matcher m = P_VALID_ENTITIES.matcher(s); // 匹配有效实体
        while (m.find()) // 遍历所有实体
        {
            final String one = m.group(1); // ([^&;]*) // 实体名
            final String two = m.group(2); // (?=(;|&|$)) // 终止符
            m.appendReplacement(buf, Matcher.quoteReplacement(checkEntity(one, two))); // 检查并替换
        }
        m.appendTail(buf); // 追加剩余

        return encodeQuotes(buf.toString()); // 编码引号后返回
    }

    private String encodeQuotes(final String s) // 编码引号
    {
        if (encodeQuotes) // 需要编码引号时
        {
            StringBuffer buf = new StringBuffer(); // 创建缓冲区
            Matcher m = P_VALID_QUOTES.matcher(s); // 匹配引号
            while (m.find()) // 遍历所有引号
            {
                final String one = m.group(1); // (>|^) // 前缀
                final String two = m.group(2); // ([^<]+?) // 内容
                final String three = m.group(3); // (<|$) // 后缀
                // 不替换双引号为&quot;，防止json格式无效 regexReplace(P_QUOTE, "&quot;", two)
                m.appendReplacement(buf, Matcher.quoteReplacement(one + two + three)); // 保留原样
            }
            m.appendTail(buf); // 追加剩余
            return buf.toString(); // 返回结果
        }
        else
        {
            return s; // 不需要编码直接返回
        }
    }

    private String checkEntity(final String preamble, final String term) // 检查实体是否合法
    {

        return ";".equals(term) && isValidEntity(preamble) ? '&' + preamble : "&amp;" + preamble; // 合法实体保留，否则转义
    }

    private boolean isValidEntity(final String entity) // 判断实体是否在允许列表中
    {
        return inArray(entity, vAllowedEntities); // 在允许列表中返回true
    }

    private static boolean inArray(final String s, final String[] array) // 判断字符串是否在数组中
    {
        for (String item : array) // 遍历数组
        {
            if (item != null && item.equals(s)) // 匹配成功
            {
                return true; // 返回true
            }
        }
        return false; // 未找到返回false
    }

    private boolean allowed(final String name) // 判断标签是否被允许
    {
        return (vAllowed.isEmpty() || vAllowed.containsKey(name)) && !inArray(name, vDisallowed); // 在允许列表且不在禁止列表
    }

    private boolean allowedAttribute(final String name, final String paramName) // 判断标签属性是否被允许
    {
        return allowed(name) && (vAllowed.isEmpty() || vAllowed.get(name).contains(paramName)); // 标签允许且属性在允许列表
    }
}
