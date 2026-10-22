// 导入封装的HTTP请求工具，用于发送API请求
import request from '@/utils/request'

// 获取当前用户的钱包信息，如果用户没有钱包则自动创建
// 导出getMyWallet函数，无需参数
export function getMyWallet() {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：获取个人钱包接口
    url: '/api/wallet/my',
    // 请求方式：GET获取数据
    method: 'get'
  })
}

// 获取当前用户的钱包流水记录，支持分页查询
// 导出getMyWalletLogs函数，接收查询参数query
export function getMyWalletLogs(query) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：个人钱包流水接口
    url: '/api/wallet/logs',
    // 请求方式：GET获取数据
    method: 'get',
    // 将分页等查询参数拼接到URL上
    params: query
  })
}

// 钱包充值，模拟支付流程向钱包添加余额
// 导出rechargeWallet函数，接收充值数据对象
export function rechargeWallet(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：钱包充值接口
    url: '/api/wallet/recharge',
    // 请求方式：POST提交充值请求
    method: 'post',
    // 将充值数据放在请求体中
    data: data
  })
}

// 钱包提现，从钱包中提取余额
// 导出withdrawWallet函数，接收提现数据对象
export function withdrawWallet(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：钱包提现接口
    url: '/api/wallet/withdraw',
    // 请求方式：POST提交提现请求
    method: 'post',
    // 将提现数据放在请求体中
    data: data
  })
}
