import request from './request'

// 下单
export const createOrder = data => request.post('/order', data)

// 我的订单
export const pageMyOrders = (userId, params) => request.get(`/order/user/${userId}`, { params })

// 订单详情
export const getOrder = id => request.get(`/order/${id}`)

// 取消订单
export const cancelOrder = id => request.delete(`/order/${id}`)
