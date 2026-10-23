// 导入封装的axios请求工具，用于统一发送HTTP请求
import request from '@/utils/request'

// 发布求购（丢求购）
// POST /api/buyOrder
// 买家发布一条求购信息，告知市场自己想要购买某款饰品及愿意支付的价格
export function createBuyOrder(data) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：创建求购信息接口
    url: '/api/buyOrder',
    // HTTP方法：POST表示创建新的求购记录
    method: 'post',
    // 请求体：传递求购所需数据（如饰品ID、求购价格等）
    data: data
  })
}

// 查询某饰品的有效求购（按价格降序）
// GET /api/buyOrder/list
// 根据饰品ID查询该饰品的所有有效求购记录，按价格从高到低排序
export function listSkinBuyOrders(skinId, limit) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：查询求购列表接口
    url: '/api/buyOrder/list',
    // HTTP方法：GET表示查询操作
    method: 'get',
    // 查询参数：skinId指定饰品ID，limit限制返回条数，通过URL传递
    params: { skinId: skinId, limit: limit }
  })
}

// 我的求购列表
// GET /api/buyOrder/my
// 查询当前用户发布的所有求购记录，支持分页和筛选
export function listMyBuyOrders(query) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：获取当前用户的求购列表接口
    url: '/api/buyOrder/my',
    // HTTP方法：GET表示查询操作
    method: 'get',
    // 查询参数：通过URL传递筛选条件（如分页参数、状态等）
    params: query
  })
}

// 修改求购价格
// PUT /api/buyOrder/:id
// 根据求购ID修改求购价格，更新买家愿意支付的金额
export function updateBuyOrderPrice(id, price) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：拼接求购ID到URL中，表示更新指定求购记录
    url: '/api/buyOrder/' + id,
    // HTTP方法：PUT表示全量更新操作
    method: 'put',
    // 请求体：传递新的求购价格
    data: { price: price }
  })
}

// 取消求购
// DELETE /api/buyOrder/:id
// 根据求购ID取消/删除指定的求购记录
export function cancelBuyOrder(id) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：拼接求购ID到URL中，表示删除指定求购记录
    url: '/api/buyOrder/' + id,
    // HTTP方法：DELETE表示删除/取消操作
    method: 'delete'
  })
}

// 卖家以选定库存匹配并接受求购
// POST /api/buyOrder/:id/accept
// 卖家选择自己的库存来匹配买家的求购，接受该求购并生成订单
export function acceptBuyOrder(id, inventoryId) {
  // 调用request发起HTTP请求
  return request({
    // 请求路径：拼接求购ID和accept动作，表示接受指定求购
    url: '/api/buyOrder/' + id + '/accept',
    // HTTP方法：POST表示执行接受操作
    method: 'post',
    // 请求体：传递卖家选定的库存ID，用于匹配该求购
    data: { inventoryId }
  })
}
