// 导入封装的axios请求工具，用于统一发送HTTP请求
import request from '@/utils/request'

// 获取路由
export const getRouters = () => {
  return request({
    url: '/getRouters',
    method: 'get'
  })
}