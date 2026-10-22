package com.milktea.product.ai;

/**
 * 知识块：标题 + 原文 + 向量
 */
public record KnowledgeChunk(String title, String content, double[] vector) {
}
