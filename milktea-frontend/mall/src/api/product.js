import request from './request'

// 分类列表
export const listCategories = () => request.get('/category/list')

// 在售商品列表
export const listOnSale = () => request.get('/product/list')

// 商品详情
export const getProduct = id => request.get(`/product/${id}`)
