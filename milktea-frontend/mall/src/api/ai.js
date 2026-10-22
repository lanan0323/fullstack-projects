import request from './request'

// AI 点单助手提问（大模型生成较慢，单独放宽超时）
export const askAssistant = data =>
  request.post('/product/ai/chat', data, { timeout: 60000 })
