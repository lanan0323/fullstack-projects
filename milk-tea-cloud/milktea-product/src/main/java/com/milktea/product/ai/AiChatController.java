package com.milktea.product.ai;

import com.milktea.common.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 点单助手接口：/product/ai/**（网关路由 /api/product/** 已放行，游客可问）
 */
@RestController
@RequestMapping("/product/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /** 提问：RAG 检索 + 大模型生成 */
    @PostMapping("/chat")
    public Result<AiAnswerVO> chat(@RequestBody AiChatDTO dto) {
        return Result.success(aiChatService.ask(dto));
    }

    /** 重建知识库：商品或活动规则变更后调用 */
    @PostMapping("/refresh")
    public Result<Integer> refresh() {
        return Result.success(aiChatService.refresh());
    }
}
