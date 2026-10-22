// 导入封装的HTTP请求工具，用于发送API请求
import request from '@/utils/request'

// 查询交易订单列表，支持分页和条件筛选
// 导出listOrder函数，接收查询参数query
export function listOrder(query) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：交易订单列表接口
    url: '/trade/order/list',
    // 请求方式：GET获取数据
    method: 'get',
    // 将查询参数拼接到URL上
    params: query
  })
}

// 查询交易订单详细信息，根据订单ID获取详情
// 导出getOrder函数，接收订单ID参数
export function getOrder(id) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接订单ID到URL路径中
    url: '/trade/order/' + id,
    // 请求方式：GET获取数据
    method: 'get'
  })
}

// 新增交易订单，提交订单数据到后端
// 导出addOrder函数，接收订单数据对象
export function addOrder(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：交易订单接口
    url: '/trade/order',
    // 请求方式：POST提交新数据
    method: 'post',
    // 将订单数据放在请求体中
    data: data
  })
}

// 修改交易订单，更新已有订单信息
// 导出updateOrder函数，接收更新后的订单数据对象
export function updateOrder(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：交易订单接口
    url: '/trade/order',
    // 请求方式：PUT更新数据
    method: 'put',
    // 将更新数据放在请求体中
    data: data
  })
}

// 删除交易订单，根据订单ID删除指定订单
// 导出delOrder函数，接收要删除的订单ID
export function delOrder(id) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接订单ID到URL路径中
    url: '/trade/order/' + id,
    // 请求方式：DELETE删除数据
    method: 'delete'
  })
}
