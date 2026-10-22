import request from './request'

// ========== 订单服务 API(后端: 成员C, 网关前缀 /api) ==========

// 下单
export const createOrder = data => request.post('/order', data)

// 订单分页(管理端)
export const pageOrders = params => request.get('/order/page', { params })

// 订单详情(含明细+评价)
export const getOrder = id => request.get(`/order/${id}`)

// 顾客订单分页
export const pageMyOrders = (userId, params) => request.get(`/order/user/${userId}`, { params })

// 更新订单状态(接单2/完成3/取消4)
export const updateOrderStatus = (id, status) => request.put(`/order/${id}/status`, null, { params: { status } })

// 顾客取消订单
export const cancelOrder = id => request.delete(`/order/${id}`)

// 评价订单
export const evaluateOrder = data => request.post('/order/evaluation', data)
