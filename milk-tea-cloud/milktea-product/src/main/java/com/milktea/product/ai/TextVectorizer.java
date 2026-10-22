package com.milktea.product.ai;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 轻量文本向量化：哈希词频（Hashing Trick）+ L2 归一化。
 * 中文按"单字 + 相邻二元组"切分，英文/数字按单词切分；
 * 向量归一化后，点积即为余弦相似度。
 * <p>
 * 说明：这里用词频向量替代 embedding 模型，零成本、无外部依赖，
 * 适合菜单/规则这类短文本知识库；如需语义泛化能力，可将本类替换为
 * embedding API 调用，其余流程不变。
 */
@Component
public class TextVectorizer {

    public static final int DIM = 256;
    private static final Pattern TOKEN = Pattern.compile("([a-z0-9]+)|([\\u4e00-\\u9fa5])");

    public double[] vectorize(String text) {
        double[] vec = new double[DIM];
        for (String token : tokenize(text)) {
            int idx = (token.hashCode() & 0x7fffffff) % DIM;
            vec[idx] += 1.0;
        }
        double norm = 0;
        for (double v : vec) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < DIM; i++) {
                vec[i] /= norm;
            }
        }
        return vec;
    }

    /** 归一化向量的余弦相似度 = 点积 */
    public double cosine(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return tokens;
        }
        String lower = text.toLowerCase();
        Matcher m = TOKEN.matcher(lower);
        String prevCjk = null;
        int prevEnd = -1;
        while (m.find()) {
            // 与上一个词之间有分隔符时，不组二元词
            if (m.start() != prevEnd) {
                prevCjk = null;
            }
            prevEnd = m.end();
            String ascii = m.group(1);
            if (ascii != null) {
                tokens.add(ascii);
                prevCjk = null;
            } else {
                String cjk = m.group(2);
                tokens.add(cjk);
                if (prevCjk != null) {
                    tokens.add(prevCjk + cjk);
                }
                prevCjk = cjk;
            }
        }
        return tokens;
    }
}
