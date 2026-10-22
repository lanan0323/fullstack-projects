// 导入封装的HTTP请求工具，用于发送API请求
import request from '@/utils/request'

// 饰品市场列表，支持分页、筛选和排序
// 导出listMarket函数，接收查询参数query
export function listMarket(query) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：市场饰品列表接口
    url: '/api/market/list',
    // 请求方式：GET获取数据
    method: 'get',
    // 将查询参数（分页、筛选、排序条件）拼接到URL上
    params: query
  })
}

// 关键词搜索饰品，根据用户输入的关键词搜索匹配饰品
// 导出searchMarket函数，接收搜索查询参数
export function searchMarket(query) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：市场饰品搜索接口
    url: '/api/market/search',
    // 请求方式：GET获取数据
    method: 'get',
    // 将搜索关键词等参数拼接到URL上
    params: query
  })
}

// 获取热门饰品推荐列表，展示热门/推荐饰品
// 导出getHotSkins函数，无需参数
export function getHotSkins() {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：热门饰品推荐接口
    url: '/api/market/hot',
    // 请求方式：GET获取数据
    method: 'get'
  })
}

// 获取市场分类信息，包括武器分类和品质分类
// 导出getCategories函数，无需参数
export function getCategories() {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：市场分类接口
    url: '/api/market/categories',
    // 请求方式：GET获取数据
    method: 'get'
  })
}

// 获取饰品详情信息，包含基本信息、在售列表和价格历史
// 导出getMarketDetail函数，接收饰品ID参数
export function getMarketDetail(skinId) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接饰品ID到详情接口路径中
    url: '/api/market/detail/' + skinId,
    // 请求方式：GET获取数据
    method: 'get'
  })
}

// 获取饰品价格历史数据，用于ECharts图表展示
// 导出getPriceHistory函数，接收饰品ID参数
export function getPriceHistory(skinId) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接饰品ID到价格历史接口路径中
    url: '/api/market/priceHistory/' + skinId,
    // 请求方式：GET获取数据
    method: 'get'
  })
}
