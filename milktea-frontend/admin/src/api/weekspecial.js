import request from './request'

// ========== 每周9.9特价活动配置 API(后端: 组员A milktea-user /user/weekspecial) ==========

// 活动配置全量
export const listWeekSpecials = () => request.get('/user/weekspecial/list')

// 设置/更新活动价(同商品覆盖并启用)
export const setWeekSpecial = data => request.post('/user/weekspecial', data)

// 取消活动
export const removeWeekSpecial = productId => request.delete(`/user/weekspecial/${productId}`)
