import request from './request'

// 我的钱包余额
export const getWallet = () => request.get('/user/wallet')

// 模拟支付充值 {amount}
export const rechargeWallet = amount => request.post('/user/wallet/recharge', { amount })

// 钱包流水
export const walletFlows = (limit = 50) => request.get('/user/wallet/flows', { params: { limit } })
