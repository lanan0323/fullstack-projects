package com.milktea.product.ai;

import cn.hutool.core.util.StrUtil;
import com.milktea.model.vo.ProductVO;
import com.milktea.product.service.ProductService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 点单助手知识库（离线阶段）：
 * 1. 商品菜单：从商品服务实时查询在售商品，每个商品生成一个知识块；
 * 2. 活动与规则：解析 classpath:ai/knowledge.md，按 "## " 标题切分。
 * 所有知识块统一向量化后常驻内存，商品/规则变更后可调用 refresh 重建。
 */
@Component
@Slf4j
public class KnowledgeBase {

    private static final String KNOWLEDGE_FILE = "ai/knowledge.md";

    private final ProductService productService;
    private final TextVectorizer vectorizer;

    private volatile List<KnowledgeChunk> chunks = List.of();

    public KnowledgeBase(ProductService productService, TextVectorizer vectorizer) {
        this.productService = productService;
        this.vectorizer = vectorizer;
    }

    /** 服务启动时构建；失败不阻断启动，可在就绪后调 refresh 重试 */
    @PostConstruct
    public void init() {
        try {
            rebuild();
        } catch (Exception e) {
            log.warn("AI 知识库初始化失败，可稍后调用 POST /product/ai/refresh 重建: {}", e.getMessage());
        }
    }

    /** 重建知识库：商品 + 规则文档 → 切块 → 向量化 */
    public synchronized void rebuild() {
        List<KnowledgeChunk> list = new ArrayList<>();

        // 1. 在售商品 → 每个商品一个知识块
        for (ProductVO p : productService.listOnSale()) {
            StringBuilder sb = new StringBuilder();
            sb.append(p.getProductName())
              .append("，分类：").append(p.getCategoryName())
              .append("，价格：").append(p.getPrice()).append("元");
            if (StrUtil.isNotBlank(p.getDescription())) {
                sb.append("。").append(p.getDescription());
            }
            if (p.getSpecs() != null && !p.getSpecs().isEmpty()) {
                String specs = p.getSpecs().stream()
                        .map(ProductVO.SpecOptionVO::getSpecOptionName)
                        .distinct()
                        .collect(Collectors.joining("/"));
                sb.append("。可选规格：").append(specs);
            }
            String content = sb.toString();
            list.add(new KnowledgeChunk(p.getProductName(),
                    content, vectorizer.vectorize(p.getProductName() + " " + content)));
        }

        // 2. 活动与规则文档，按 "## " 标题切块
        for (String[] section : parseKnowledgeFile()) {
            String title = section[0];
            String content = section[1];
            list.add(new KnowledgeChunk(title, content, vectorizer.vectorize(title + " " + content)));
        }

        this.chunks = List.copyOf(list);
        log.info("AI 知识库构建完成，共 {} 个知识块", list.size());
    }

    /** 在线阶段：问题向量化 → 余弦相似度召回 Top-K */
    public List<KnowledgeChunk> search(String query, int topK) {
        double[] queryVec = vectorizer.vectorize(query);
        return chunks.stream()
                .map(c -> Map.entry(c, vectorizer.cosine(queryVec, c.vector())))
                .sorted(Map.Entry.<KnowledgeChunk, Double>comparingByValue().reversed())
                .limit(Math.max(topK, 1))
                .map(Map.Entry::getKey)
                .toList();
    }

    public int size() {
        return chunks.size();
    }

    /** 解析 ai/knowledge.md：返回 [标题, 正文] 列表 */
    private List<String[]> parseKnowledgeFile() {
        List<String[]> sections = new ArrayList<>();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(KNOWLEDGE_FILE)) {
            if (in == null) {
                log.warn("未找到知识文档 {}", KNOWLEDGE_FILE);
                return sections;
            }
            String text = StrUtil.str(in, StandardCharsets.UTF_8);
            String title = null;
            StringBuilder body = new StringBuilder();
            for (String line : text.split("\r?\n")) {
                if (line.startsWith("## ")) {
                    if (title != null && !body.isEmpty()) {
                        sections.add(new String[]{title, body.toString().trim()});
                    }
                    title = line.substring(3).trim();
                    body.setLength(0);
                } else if (line.startsWith("# ")) {
                    continue; // 跳过文档大标题
                } else if (title != null) {
                    body.append(line).append('\n');
                }
            }
            if (title != null && !body.isEmpty()) {
                sections.add(new String[]{title, body.toString().trim()});
            }
        } catch (Exception e) {
            log.warn("解析知识文档失败: {}", e.getMessage());
        }
        return sections;
    }
}
