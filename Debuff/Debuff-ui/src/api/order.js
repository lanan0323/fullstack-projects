// 导入封装的axios请求工具，用于统一发送HTTP请求
import request from '@/utils/request'

// 下单购买（listingId 为市场在售记录ID）
// POST /api/order/buy/:listingId
// 买家对某个在售商品发起购买请求，生成订单
export function buyOrder(listingId) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：拼接listingId到URL中，表示购买指定的在售商品
    url: '/api/order/buy/' + listingId,
    // HTTP方法：POST表示创建新订单操作
    method: 'post'
  })
}

// 买家确认收货
// POST /api/order/confirm/:orderId
// 买家对已发货的订单进行确认收货操作，完成交易流程
export function confirmOrder(orderId) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：拼接orderId到URL中，表示确认指定订单的收货
    url: '/api/order/confirm/' + orderId,
    // HTTP方法：POST表示执行确认收货动作
    method: 'post'
  })
}

// 取消订单
// POST /api/order/cancel/:orderId
// 买家或卖家取消指定订单，终止交易流程
export function cancelOrder(orderId) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：拼接orderId到URL中，表示取消指定订单
    url: '/api/order/cancel/' + orderId,
    // HTTP方法：POST表示执行取消订单动作
    method: 'post'
  })
}

// 我的交易列表（我买的或我卖的）
// GET /api/order/my
// 查询当前用户的交易记录，支持分页和筛选（买/卖）
export function myOrders(query) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：获取当前用户的订单列表
    url: '/api/order/my',
    // HTTP方法：GET表示查询操作
    method: 'get',
    // 查询参数：通过URL传递筛选条件（如订单状态、买卖类型、分页参数等）
    params: query
  })
}
