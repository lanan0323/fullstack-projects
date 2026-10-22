// 包声明：Base64编解码工具类所在包
package com.debuff.common.utils.sign;

/**
 * Base64工具类
 * 
 * @author ruoyi
 */
public final class Base64 // Base64编解码工具类，手动实现Base64算法
{
    static private final int     BASELENGTH           = 128; // Base64字母表长度
    static private final int     LOOKUPLENGTH         = 64; // 查找表长度
    static private final int     TWENTYFOURBITGROUP   = 24; // 24位一组处理
    static private final int     EIGHTBIT             = 8; // 8位
    static private final int     SIXTEENBIT           = 16; // 16位
    static private final int     FOURBYTE             = 4; // 4字节
    static private final int     SIGN                 = -128; // 符号位掩码
    static private final char    PAD                  = '='; // 填充字符
    static final private byte[]  base64Alphabet       = new byte[BASELENGTH]; // Base64解码字母表
    static final private char[]  lookUpBase64Alphabet = new char[LOOKUPLENGTH]; // Base64编码查找表

    static // 静态初始化块，填充编码和解码查找表
    {
        for (int i = 0; i < BASELENGTH; ++i) // 初始化解码表为-1
        {
            base64Alphabet[i] = -1;
        }
        for (int i = 'Z'; i >= 'A'; i--) // 填充A-Z的解码值0-25
        {
            base64Alphabet[i] = (byte) (i - 'A');
        }
        for (int i = 'z'; i >= 'a'; i--) // 填充a-z的解码值26-51
        {
            base64Alphabet[i] = (byte) (i - 'a' + 26);
        }

        for (int i = '9'; i >= '0'; i--) // 填充0-9的解码值52-61
        {
            base64Alphabet[i] = (byte) (i - '0' + 52);
        }

        base64Alphabet['+'] = 62; // +的解码值
        base64Alphabet['/'] = 63; // /的解码值

        for (int i = 0; i <= 25; i++) // 填充编码查找表A-Z
        {
            lookUpBase64Alphabet[i] = (char) ('A' + i);
        }

        for (int i = 26, j = 0; i <= 51; i++, j++) // 填充编码查找表a-z
        {
            lookUpBase64Alphabet[i] = (char) ('a' + j);
        }

        for (int i = 52, j = 0; i <= 61; i++, j++) // 填充编码查找表0-9
        {
            lookUpBase64Alphabet[i] = (char) ('0' + j);
        }
        lookUpBase64Alphabet[62] = (char) '+'; // 编码查找表+
        lookUpBase64Alphabet[63] = (char) '/'; // 编码查找表/
    }

    private static boolean isWhiteSpace(char octect) // 判断字符是否为空白字符
    {
        return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9); // 空格/回车/换行/制表符
    }

    private static boolean isPad(char octect) // 判断字符是否为填充字符=
    {
        return (octect == PAD); // 判断是否为=
    }

    private static boolean isData(char octect) // 判断字符是否为有效的Base64字符
    {
        return (octect < BASELENGTH && base64Alphabet[octect] != -1); // 在字母表中且值不为-1
    }

    /**
     * Encodes hex octects into Base64
     *
     * @param binaryData Array containing binaryData
     * @return Encoded Base64 array
     */
    public static String encode(byte[] binaryData) // 将字节数组编码为Base64字符串
    {
        if (binaryData == null) // 输入为空时
        {
            return null; // 返回null
        }

        int lengthDataBits = binaryData.length * EIGHTBIT; // 计算总位数
        if (lengthDataBits == 0) // 数据为空时
        {
            return ""; // 返回空字符串
        }

        int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP; // 不足24位的余数
        int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP; // 完整的24位组数
        int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1 : numberTriplets; // 输出4字节组数
        char encodedData[] = null; // 编码结果数组

        encodedData = new char[numberQuartet * 4]; // 创建结果数组，每组输出4个字符

        byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0; // 临时变量

        int encodedIndex = 0; // 结果数组索引
        int dataIndex = 0; // 输入数组索引

        for (int i = 0; i < numberTriplets; i++) // 遍历完整的3字节组
        {
            b1 = binaryData[dataIndex++]; // 取第1个字节
            b2 = binaryData[dataIndex++]; // 取第2个字节
            b3 = binaryData[dataIndex++]; // 取第3个字节

            l = (byte) (b2 & 0x0f); // b2低4位
            k = (byte) (b1 & 0x03); // b1低2位

            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0); // b1高6位
            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0); // b2高4位
            byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc); // b3高2位

            encodedData[encodedIndex++] = lookUpBase64Alphabet[val1]; // 输出第1个Base64字符
            encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)]; // 输出第2个Base64字符
            encodedData[encodedIndex++] = lookUpBase64Alphabet[(l << 2) | val3]; // 输出第3个Base64字符
            encodedData[encodedIndex++] = lookUpBase64Alphabet[b3 & 0x3f]; // 输出第4个Base64字符
        }

        // form integral number of 6-bit groups
        if (fewerThan24bits == EIGHTBIT) // 剩余8位（1字节）
        {
            b1 = binaryData[dataIndex]; // 取最后1个字节
            k = (byte) (b1 & 0x03); // 低2位
            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0); // 高6位
            encodedData[encodedIndex++] = lookUpBase64Alphabet[val1]; // 输出第1个字符
            encodedData[encodedIndex++] = lookUpBase64Alphabet[k << 4]; // 输出第2个字符
            encodedData[encodedIndex++] = PAD; // 填充=
            encodedData[encodedIndex++] = PAD; // 填充=
        }
        else if (fewerThan24bits == SIXTEENBIT) // 剩余16位（2字节）
        {
            b1 = binaryData[dataIndex]; // 取第1个字节
            b2 = binaryData[dataIndex + 1]; // 取第2个字节
            l = (byte) (b2 & 0x0f); // b2低4位
            k = (byte) (b1 & 0x03); // b1低2位

            byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0); // b1高6位
            byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0); // b2高4位

            encodedData[encodedIndex++] = lookUpBase64Alphabet[val1]; // 输出第1个字符
            encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)]; // 输出第2个字符
            encodedData[encodedIndex++] = lookUpBase64Alphabet[l << 2]; // 输出第3个字符
            encodedData[encodedIndex++] = PAD; // 填充=
        }
        return new String(encodedData); // 返回Base64编码字符串
    }

    /**
     * Decodes Base64 data into octects
     *
     * @param encoded string containing Base64 data
     * @return Array containind decoded data.
     */
    public static byte[] decode(String encoded) // 将Base64字符串解码为字节数组
    {
        if (encoded == null) // 输入为空时
        {
            return null; // 返回null
        }

        char[] base64Data = encoded.toCharArray(); // 转为字符数组
        // remove white spaces
        int len = removeWhiteSpace(base64Data); // 去除空白字符

        if (len % FOURBYTE != 0) // 长度不是4的倍数时
        {
            return null;// should be divisible by four // 返回null
        }

        int numberQuadruple = (len / FOURBYTE); // 计算4字节组数

        if (numberQuadruple == 0) // 没有数据时
        {
            return new byte[0]; // 返回空数组
        }

        byte decodedData[] = null; // 解码结果数组
        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0; // 临时字节变量
        char d1 = 0, d2 = 0, d3 = 0, d4 = 0; // 临时字符变量

        int i = 0; // 组计数器
        int encodedIndex = 0; // 结果数组索引
        int dataIndex = 0; // 输入数组索引
        decodedData = new byte[(numberQuadruple) * 3]; // 创建结果数组

        for (; i < numberQuadruple - 1; i++) // 遍历除最后一组外的所有完整4字符组
        {

            if (!isData((d1 = base64Data[dataIndex++])) || !isData((d2 = base64Data[dataIndex++])) // 检查4个字符是否都是有效Base64字符
                    || !isData((d3 = base64Data[dataIndex++])) || !isData((d4 = base64Data[dataIndex++])))
            {
                return null; // if found "no data" just return null // 无效数据返回null
            }

            b1 = base64Alphabet[d1]; // 查表获取解码值
            b2 = base64Alphabet[d2]; // 查表获取解码值
            b3 = base64Alphabet[d3]; // 查表获取解码值
            b4 = base64Alphabet[d4]; // 查表获取解码值

            decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4); // 还原第1个字节
            decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)); // 还原第2个字节
            decodedData[encodedIndex++] = (byte) (b3 << 6 | b4); // 还原第3个字节
        }

        if (!isData((d1 = base64Data[dataIndex++])) || !isData((d2 = base64Data[dataIndex++]))) // 检查最后两组字符
        {
            return null;// if found "no data" just return null // 无效数据返回null
        }

        b1 = base64Alphabet[d1]; // 查表获取解码值
        b2 = base64Alphabet[d2]; // 查表获取解码值

        d3 = base64Data[dataIndex++]; // 取第3个字符
        d4 = base64Data[dataIndex++]; // 取第4个字符
        if (!isData((d3)) || !isData((d4))) // 第3、4个字符不是有效Base64字符时
        {// Check if they are PAD characters
            if (isPad(d3) && isPad(d4)) // 最后两个都是填充符
            {
                if ((b2 & 0xf) != 0)// last 4 bits should be zero // 最后4位应为0
                {
                    return null; // 不合法返回null
                }
                byte[] tmp = new byte[i * 3 + 1]; // 创建结果数组，只有1个有效字节
                System.arraycopy(decodedData, 0, tmp, 0, i * 3); // 复制前面的数据
                tmp[encodedIndex] = (byte) (b1 << 2 | b2 >> 4); // 设置最后一个字节
                return tmp; // 返回结果
            }
            else if (!isPad(d3) && isPad(d4)) // 第3个不是填充，第4个是填充
            {
                b3 = base64Alphabet[d3]; // 查表获取解码值
                if ((b3 & 0x3) != 0)// last 2 bits should be zero // 最后2位应为0
                {
                    return null; // 不合法返回null
                }
                byte[] tmp = new byte[i * 3 + 2]; // 创建结果数组，有2个有效字节
                System.arraycopy(decodedData, 0, tmp, 0, i * 3); // 复制前面的数据
                tmp[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4); // 设置第1个字节
                tmp[encodedIndex] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)); // 设置第2个字节
                return tmp; // 返回结果
            }
            else
            {
                return null; // 其他情况返回null
            }
        }
        else
        { // No PAD e.g 3cQl // 没有填充符，完整3字节
            b3 = base64Alphabet[d3]; // 查表获取解码值
            b4 = base64Alphabet[d4]; // 查表获取解码值
            decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4); // 还原第1个字节
            decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)); // 还原第2个字节
            decodedData[encodedIndex++] = (byte) (b3 << 6 | b4); // 还原第3个字节

        }
        return decodedData; // 返回解码后的字节数组
    }

    /**
     * remove WhiteSpace from MIME containing encoded Base64 data.
     *
     * @param data the byte array of base64 data (with WS)
     * @return the new length
     */
    private static int removeWhiteSpace(char[] data) // 去除字符数组中的空白字符
    {
        if (data == null) // 输入为空时
        {
            return 0; // 返回0
        }

        // count characters that's not whitespace
        int newSize = 0; // 新数组长度
        int len = data.length; // 原数组长度
        for (int i = 0; i < len; i++) // 遍历所有字符
        {
            if (!isWhiteSpace(data[i])) // 不是空白字符时
            {
                data[newSize++] = data[i]; // 前移覆盖
            }
        }
        return newSize; // 返回新长度
    }
}
