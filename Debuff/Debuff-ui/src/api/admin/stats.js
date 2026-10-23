// 导入封装的axios请求工具，用于统一发送HTTP请求
import request from '@/utils/request'

// 数据看板总览统计
// GET /api/admin/stats/overview
// 获取管理端数据看板的总览统计数据，包括用户数、订单数、交易额等关键指标
export function getOverview() {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：管理端统计总览接口
    url: '/api/admin/stats/overview',
    // HTTP方法：GET表示查询操作
    method: 'get'
  })
}

// 近 N 天订单趋势
// GET /api/admin/stats/orderTrend
// 获取最近N天的订单数量/金额趋势数据，用于绘制折线图展示变化趋势
export function getOrderTrend(days) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：管理端订单趋势接口
    url: '/api/admin/stats/orderTrend',
    // HTTP方法：GET表示查询操作
    method: 'get',
    // 查询参数：days指定查询的天数范围，通过URL传递
    params: { days }
  })
}

// 成交金额 TOP N 饰品
// GET /api/admin/stats/topSkins
// 获取成交金额排名前N的饰品数据，用于展示热门/高价值饰品排行
export function getTopSkins(limit) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：管理端饰品成交排行接口
    url: '/api/admin/stats/topSkins',
    // HTTP方法：GET表示查询操作
    method: 'get',
    // 查询参数：limit指定返回的排行数量（TOP N），通过URL传递
    params: { limit }
  })
}

// 订单状态分布
// GET /api/admin/stats/statusDist
// 获取各状态订单的数量分布，用于绘制饼图展示订单状态占比
export function getStatusDist() {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：管理端订单状态分布接口
    url: '/api/admin/stats/statusDist',
    // HTTP方法：GET表示查询操作
    method: 'get'
  })
}
