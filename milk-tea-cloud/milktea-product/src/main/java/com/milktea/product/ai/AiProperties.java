package com.milktea.product.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 点单助手配置项（ai.llm.* / ai.rag.*）
 */
@Component
@ConfigurationProperties(prefix = "ai")
@Data
public class AiProperties {

    private Llm llm = new Llm();
    private Rag rag = new Rag();

    @Data
    public static class Llm {
        /** OpenAI 兼容接口地址（DeepSeek / 通义 / Kimi 等均可） */
        private String baseUrl = "https://api.deepseek.com";
        /** API Key，建议通过环境变量 DEEPSEEK_API_KEY 注入，避免提交到仓库 */
        private String apiKey = "";
        private String model = "deepseek-chat";
        /** 调用超时时间（秒） */
        private int timeoutSeconds = 30;
    }

    @Data
    public static class Rag {
        /** 每次提问召回的知识块数量 Top-K */
        private int topK = 3;
    }
}
