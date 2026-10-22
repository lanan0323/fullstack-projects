// 导入封装的HTTP请求工具，用于发送API请求
import request from '@/utils/request'

// 切换收藏状态，对某个上架饰品进行收藏或取消收藏
// 导出toggleFavorite函数，接收上架饰品ID参数
export function toggleFavorite(listingId) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：收藏切换接口
    url: '/api/favorite/toggle',
    // 请求方式：POST切换收藏状态
    method: 'post',
    // 将上架饰品ID作为参数传递
    params: { listingId }
  })
}

// 检查当前用户是否已收藏某个上架饰品
// 导出checkFavorite函数，接收上架饰品ID参数
export function checkFavorite(listingId) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：收藏检查接口
    url: '/api/favorite/check',
    // 请求方式：GET查询收藏状态
    method: 'get',
    // 将上架饰品ID作为参数传递
    params: { listingId }
  })
}

// 获取我的收藏列表，查看当前用户收藏的所有饰品
// 导出listMyFavorites函数，接收查询参数query
export function listMyFavorites(query) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：收藏列表接口
    url: '/api/favorite/list',
    // 请求方式：GET获取数据
    method: 'get',
    // 将查询参数拼接到URL上
    params: query
  })
}

// 取消收藏，根据收藏记录ID删除收藏
// 导出removeFavorite函数，接收要删除的收藏记录ID
export function removeFavorite(id) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接收藏记录ID到URL路径中
    url: '/api/favorite/' + id,
    // 请求方式：DELETE删除收藏记录
    method: 'delete'
  })
}
