// 导入封装的HTTP请求工具，用于发送API请求
import request from '@/utils/request'

// 获取我的库存列表，查看当前用户拥有的饰品
// 导出listInventory函数，接收查询参数query
export function listInventory(query) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：库存列表接口
    url: '/api/inventory/list',
    // 请求方式：GET获取数据
    method: 'get',
    // 将查询参数拼接到URL上
    params: query
  })
}

// 获取库存饰品详情，查看某个饰品的详细信息
// 导出getInventory函数，接收库存饰品ID参数
export function getInventory(id) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接饰品ID到库存详情接口路径中
    url: '/api/inventory/detail/' + id,
    // 请求方式：GET获取数据
    method: 'get'
  })
}

// 更新库存饰品信息，包括贴纸、检视链接、磨损度等
// 导出updateInventory函数，接收更新数据对象（需包含id字段）
export function updateInventory(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接饰品ID到库存更新接口路径中
    url: '/api/inventory/update/' + data.id,
    // 请求方式：PUT更新数据
    method: 'put',
    // 将更新数据放在请求体中
    data: data
  })
}
