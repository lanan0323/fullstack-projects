// 导入封装的HTTP请求工具，用于发送API请求
import request from '@/utils/request'

// 查询轮播图列表，支持分页和条件筛选
// 导出listBanner函数，接收查询参数query
export function listBanner(query) {
  // 返回一个HTTP请求Promise，请求地址为轮播图列表接口，GET方式，参数拼接到URL上
  return request({ url: '/trade/banner/list', method: 'get', params: query })
}

// 查询轮播图详细信息，根据轮播图ID获取详情
// 导出getBanner函数，接收轮播图ID参数
export function getBanner(id) {
  // 返回一个HTTP请求Promise，拼接ID到URL路径中，GET方式获取数据
  return request({ url: '/trade/banner/' + id, method: 'get' })
}

// 新增轮播图，提交轮播图数据到后端
// 导出addBanner函数，接收轮播图数据对象
export function addBanner(data) {
  // 返回一个HTTP请求Promise，POST方式提交新数据
  return request({ url: '/trade/banner', method: 'post', data })
}

// 修改轮播图，更新已有轮播图信息
// 导出updateBanner函数，接收更新后的轮播图数据对象
export function updateBanner(data) {
  // 返回一个HTTP请求Promise，PUT方式更新数据
  return request({ url: '/trade/banner', method: 'put', data })
}

// 删除轮播图，根据轮播图ID删除指定轮播图
// 导出delBanner函数，接收要删除的轮播图ID
export function delBanner(id) {
  // 返回一个HTTP请求Promise，拼接ID到URL路径中，DELETE方式删除数据
  return request({ url: '/trade/banner/' + id, method: 'delete' })
}

// 查询用户端公开轮播图列表，获取前台展示的轮播图
// 导出getPublicBanners函数，无需参数
export function getPublicBanners() {
  // 返回一个HTTP请求Promise，请求用户端公开轮播图列表，GET方式
  return request({ url: '/trade/banner/publicList', method: 'get' })
}
