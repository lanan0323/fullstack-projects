package com.milktea.product.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 点单助手回答
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnswerVO {
    /** 回答内容 */
    private String answer;
    /** 引用到的知识块标题（体现 RAG 检索来源） */
    private List<String> references;
}
