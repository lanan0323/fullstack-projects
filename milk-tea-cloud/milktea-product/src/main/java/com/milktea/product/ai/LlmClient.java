package com.milktea.product.ai;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 大模型客户端：调用 OpenAI 兼容的 chat/completions 接口（默认 DeepSeek）。
 * 仅封装 HTTP 调用与解析，失败直接抛异常，由上层服务决定降级策略。
 */
@Component
@Slf4j
public class LlmClient {

    private final AiProperties properties;

    public LlmClient(AiProperties properties) {
        this.properties = properties;
    }

    public String chat(List<ChatMessage> messages) {
        AiProperties.Llm cfg = properties.getLlm();
        if (StrUtil.isBlank(cfg.getApiKey())) {
            throw new IllegalStateException("未配置大模型 API Key（ai.llm.api-key / 环境变量 DEEPSEEK_API_KEY）");
        }

        JSONArray msgArr = new JSONArray();
        for (ChatMessage msg : messages) {
            msgArr.add(new JSONObject()
                    .set("role", msg.getRole())
                    .set("content", msg.getContent()));
        }
        JSONObject body = new JSONObject()
                .set("model", cfg.getModel())
                .set("messages", msgArr)
                .set("temperature", 0.7)
                .set("max_tokens", 512)
                .set("stream", false);

        String url = StrUtil.removeSuffix(cfg.getBaseUrl(), "/") + "/chat/completions";
        try (HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + cfg.getApiKey())
                .header("Content-Type", "application/json")
                .body(body.toString())
                .timeout(cfg.getTimeoutSeconds() * 1000)
                .execute()) {

            String respBody = response.body();
            if (!response.isOk()) {
                log.warn("大模型调用失败 HTTP {}: {}", response.getStatus(), respBody);
                throw new IllegalStateException("大模型调用失败 HTTP " + response.getStatus());
            }
            JSONObject json = JSONUtil.parseObj(respBody);
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content", "");
        }
    }
}
