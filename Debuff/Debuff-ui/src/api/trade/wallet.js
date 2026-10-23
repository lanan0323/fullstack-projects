// 导入封装的HTTP请求工具，用于发送API请求
import request from '@/utils/request'

// 查询用户钱包列表，支持分页和条件筛选
// 导出listWallet函数，接收查询参数query
export function listWallet(query) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：用户钱包列表接口
    url: '/trade/wallet/list',
    // 请求方式：GET获取数据
    method: 'get',
    // 将查询参数拼接到URL上
    params: query
  })
}

// 查询用户钱包详细信息，根据钱包ID获取详情
// 导出getWallet函数，接收钱包ID参数
export function getWallet(id) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接钱包ID到URL路径中
    url: '/trade/wallet/' + id,
    // 请求方式：GET获取数据
    method: 'get'
  })
}

// 新增用户钱包，提交钱包数据到后端
// 导出addWallet函数，接收钱包数据对象
export function addWallet(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：用户钱包接口
    url: '/trade/wallet',
    // 请求方式：POST提交新数据
    method: 'post',
    // 将钱包数据放在请求体中
    data: data
  })
}

// 修改用户钱包，更新已有钱包信息
// 导出updateWallet函数，接收更新后的钱包数据对象
export function updateWallet(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：用户钱包接口
    url: '/trade/wallet',
    // 请求方式：PUT更新数据
    method: 'put',
    // 将更新数据放在请求体中
    data: data
  })
}

// 删除用户钱包，根据钱包ID删除指定钱包
// 导出delWallet函数，接收要删除的钱包ID
export function delWallet(id) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接钱包ID到URL路径中
    url: '/trade/wallet/' + id,
    // 请求方式：DELETE删除数据
    method: 'delete'
  })
}
