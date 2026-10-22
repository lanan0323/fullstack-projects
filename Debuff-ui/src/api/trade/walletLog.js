// 导入封装的HTTP请求工具，用于发送API请求
import request from '@/utils/request'

// 查询钱包流水列表，支持分页和条件筛选
// 导出listWalletLog函数，接收查询参数query
export function listWalletLog(query) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：钱包流水列表接口
    url: '/trade/walletLog/list',
    // 请求方式：GET获取数据
    method: 'get',
    // 将查询参数拼接到URL上
    params: query
  })
}

// 查询钱包流水详细信息，根据流水ID获取详情
// 导出getWalletLog函数，接收流水ID参数
export function getWalletLog(id) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接流水ID到URL路径中
    url: '/trade/walletLog/' + id,
    // 请求方式：GET获取数据
    method: 'get'
  })
}

// 新增钱包流水，提交流水数据到后端
// 导出addWalletLog函数，接收流水数据对象
export function addWalletLog(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：钱包流水接口
    url: '/trade/walletLog',
    // 请求方式：POST提交新数据
    method: 'post',
    // 将流水数据放在请求体中
    data: data
  })
}

// 修改钱包流水，更新已有流水信息
// 导出updateWalletLog函数，接收更新后的流水数据对象
export function updateWalletLog(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：钱包流水接口
    url: '/trade/walletLog',
    // 请求方式：PUT更新数据
    method: 'put',
    // 将更新数据放在请求体中
    data: data
  })
}

// 删除钱包流水，根据流水ID删除指定流水记录
// 导出delWalletLog函数，接收要删除的流水ID
export function delWalletLog(id) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接流水ID到URL路径中
    url: '/trade/walletLog/' + id,
    // 请求方式：DELETE删除数据
    method: 'delete'
  })
}
