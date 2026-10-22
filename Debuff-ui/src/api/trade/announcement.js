// 导入封装的HTTP请求工具，用于发送API请求
import request from '@/utils/request'

// 查询系统公告列表，支持分页和条件筛选
// 导出listAnnouncement函数，接收查询参数query
export function listAnnouncement(query) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：系统公告列表接口
    url: '/trade/announcement/list',
    // 请求方式：GET获取数据
    method: 'get',
    // 将查询参数拼接到URL上
    params: query
  })
}

// 查询系统公告详细信息，根据公告ID获取详情
// 导出getAnnouncement函数，接收公告ID参数
export function getAnnouncement(id) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接公告ID到URL路径中
    url: '/trade/announcement/' + id,
    // 请求方式：GET获取数据
    method: 'get'
  })
}

// 新增系统公告，提交公告数据到后端
// 导出addAnnouncement函数，接收公告数据对象
export function addAnnouncement(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：系统公告接口
    url: '/trade/announcement',
    // 请求方式：POST提交新数据
    method: 'post',
    // 将公告数据放在请求体中
    data: data
  })
}

// 修改系统公告，更新已有公告信息
// 导出updateAnnouncement函数，接收更新后的公告数据对象
export function updateAnnouncement(data) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：系统公告接口
    url: '/trade/announcement',
    // 请求方式：PUT更新数据
    method: 'put',
    // 将更新数据放在请求体中
    data: data
  })
}

// 删除系统公告，根据公告ID删除指定公告
// 导出delAnnouncement函数，接收要删除的公告ID
export function delAnnouncement(id) {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：拼接公告ID到URL路径中
    url: '/trade/announcement/' + id,
    // 请求方式：DELETE删除数据
    method: 'delete'
  })
}

// 查询用户端最新公告，获取面向前台用户展示的公告列表
// 导出getPublicAnnouncements函数，无需参数
export function getPublicAnnouncements() {
  // 返回一个HTTP请求Promise
  return request({
    // 请求地址：用户端公开公告列表接口
    url: '/trade/announcement/publicList',
    // 请求方式：GET获取数据
    method: 'get'
  })
}
