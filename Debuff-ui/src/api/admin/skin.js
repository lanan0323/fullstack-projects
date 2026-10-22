// 导入封装的HTTP请求工具，用于发送API请求
import request from '@/utils/request'

// 获取后台管理的饰品列表，支持分页和条件筛选
// 导出listSkin函数，接收查询参数query
export function listSkin(query) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：后台管理饰品列表接口
    url: '/api/admin/skin/list',
    // 请求方式：GET获取数据
    method: 'get',
    // 将查询参数拼接到URL上
    params: query
  })
}

// 获取后台管理的饰品详情，根据饰品ID查看详细信息
// 导出getSkin函数，接收饰品ID参数
export function getSkin(skinId) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接饰品ID到后台管理详情路径中
    url: '/api/admin/skin/' + skinId,
    // 请求方式：GET获取数据
    method: 'get'
  })
}

// 新增饰品定义，在后台添加新的饰品信息
// 导出addSkin函数，接收饰品数据对象
export function addSkin(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：后台管理饰品接口
    url: '/api/admin/skin',
    // 请求方式：POST提交新数据
    method: 'post',
    // 将饰品数据放在请求体中
    data: data
  })
}

// 编辑饰品定义，更新后台已有的饰品信息
// 导出updateSkin函数，接收更新后的饰品数据对象
export function updateSkin(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：后台管理饰品接口
    url: '/api/admin/skin',
    // 请求方式：PUT更新数据
    method: 'put',
    // 将更新数据放在请求体中
    data: data
  })
}

// 删除饰品定义，根据饰品ID（支持批量）删除饰品
// 导出delSkin函数，接收要删除的饰品ID（可以是多个ID）
export function delSkin(skinIds) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接饰品ID到URL路径中
    url: '/api/admin/skin/' + skinIds,
    // 请求方式：DELETE删除数据
    method: 'delete'
  })
}

// 切换饰品上架/下架状态
// 导出changeSkinStatus函数，接收状态变更数据对象
export function changeSkinStatus(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：饰品状态变更接口
    url: '/api/admin/skin/changeStatus',
    // 请求方式：PUT更新状态
    method: 'put',
    // 将状态变更数据放在请求体中
    data: data
  })
}
