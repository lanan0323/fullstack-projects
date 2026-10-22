package com.milktea.product.ai;

import lombok.Data;

import java.util.List;

/**
 * AI 点单助手提问请求
 */
@Data
public class AiChatDTO {
    /** 当前用户问题 */
    private String question;
    /** 最近几轮对话（可选，用于多轮语境），按时间正序传入 */
    private List<ChatMessage> history;
}
