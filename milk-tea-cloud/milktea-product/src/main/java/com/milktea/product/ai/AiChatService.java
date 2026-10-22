package com.milktea.product.ai;

import cn.hutool.core.util.StrUtil;
import com.milktea.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 点单助手编排（在线阶段）：
 * 问题向量化召回 Top-K 知识块 → 拼接上下文（RAG）→ 调大模型生成回答。
 * 生成失败时降级为固定话术，不影响点单主流程。
 */
@Service
@Slf4j
public class AiChatService {

    /** 角色设定 + 格式约束 + 防幻觉规则的系统提示词，{} 处拼接检索到的资料 */
    private static final String SYSTEM_PROMPT = """
            你是"茶千道"奶茶店的智能点单助手"茶仔"，语气亲切、口语化、回答简短。
            请只根据【参考资料】回答顾客问题；资料里没有的信息不要编造，
            可以回复"这个以门店当天实际情况为准哦"。
            饮品价格一律以资料标注为准，单位"元"；不要杜撰不在资料里的商品。
            回答控制在 150 字以内，可以分点，最后可以自然地引导顾客下单。

            【参考资料】
            %s""";

    /** 多轮对话最多携带的历史消息条数（省 token，也避免语境跑偏） */
    private static final int MAX_HISTORY = 4;

    /** 大模型不可用时的降级话术 */
    private static final String FALLBACK_ANSWER = "AI 助手开小差了，请稍后再试，也可以先看看菜单直接下单哦~";

    private final KnowledgeBase knowledgeBase;
    private final LlmClient llmClient;
    private final AiProperties properties;

    public AiChatService(KnowledgeBase knowledgeBase, LlmClient llmClient, AiProperties properties) {
        this.knowledgeBase = knowledgeBase;
        this.llmClient = llmClient;
        this.properties = properties;
    }

    public AiAnswerVO ask(AiChatDTO dto) {
        String question = StrUtil.trimToEmpty(dto.getQuestion());
        if (StrUtil.isBlank(question)) {
            throw new BusinessException("问题不能为空");
        }
        if (knowledgeBase.size() == 0) {
            return new AiAnswerVO("知识库还没准备好，请稍后再试~", List.of());
        }

        // 1. RAG 检索：召回最相关的 Top-K 知识块
        List<KnowledgeChunk> topK = knowledgeBase.search(question, properties.getRag().getTopK());
        String context = topK.stream()
                .map(c -> "- " + c.title() + "：" + c.content())
                .collect(Collectors.joining("\n"));

        // 2. 组装消息：系统提示词 + 最近几轮历史 + 当前问题
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", SYSTEM_PROMPT.formatted(context)));
        if (dto.getHistory() != null && !dto.getHistory().isEmpty()) {
            List<ChatMessage> recent = dto.getHistory()
                    .stream()
                    .filter(m -> m != null && StrUtil.isNotBlank(m.getContent()))
                    .toList();
            recent.subList(Math.max(0, recent.size() - MAX_HISTORY), recent.size())
                    .forEach(messages::add);
        }
        messages.add(new ChatMessage("user", question));

        // 3. 调用大模型生成；失败降级
        try {
            String answer = llmClient.chat(messages);
            List<String> references = topK.stream().map(KnowledgeChunk::title).distinct().toList();
            return new AiAnswerVO(answer, references);
        } catch (Exception e) {
            log.warn("AI 助手降级: {}", e.getMessage());
            return new AiAnswerVO(FALLBACK_ANSWER, List.of());
        }
    }

    /** 重建知识库（商品/活动变更后调用），返回知识块数量 */
    public int refresh() {
        knowledgeBase.rebuild();
        return knowledgeBase.size();
    }
}
