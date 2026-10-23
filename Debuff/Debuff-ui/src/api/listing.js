// 导入封装的axios请求工具，用于统一发送HTTP请求
import request from '@/utils/request'

// 上架（库存 -> 在售）
// POST /api/listing
// 将库存中的饰品上架到市场，使其变为在售状态，供其他买家浏览和购买
export function createListing(data) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：上架商品接口
    url: '/api/listing',
    // HTTP方法：POST表示创建新的上架记录
    method: 'post',
    // 请求体：传递上架所需数据（如库存ID、价格、备注等）
    data: data
  })
}

// 下架（在售 -> 已下架，按库存ID）
// DELETE /api/listing/inventory/:inventoryId
// 根据库存ID将已上架的商品从市场下架，状态变为已下架
export function offShelfListing(inventoryId) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：拼接inventoryId到URL中，表示对指定库存执行下架操作
    url: '/api/listing/inventory/' + inventoryId,
    // HTTP方法：DELETE表示删除/下架操作
    method: 'delete'
  })
}
