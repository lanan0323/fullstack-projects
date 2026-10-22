import request from './request'

// ========== 商品服务 API(后端: 成员B, 网关前缀 /api) ==========

// 分类列表(白名单)
export const listCategories = () => request.get('/category/list')

// 分类详情
export const getCategory = id => request.get(`/category/${id}`)

// 新增/修改/删除分类
export const addCategory = data => request.post('/category', data)
export const updateCategory = data => request.put('/category', data)
export const deleteCategory = id => request.delete(`/category/${id}`)

// 商品分页(管理端)
export const pageProducts = params => request.get('/product/page', { params })

// 商品详情(含规格)
export const getProduct = id => request.get(`/product/${id}`)

// 顾客端在售商品列表(白名单)
export const listOnSale = () => request.get('/product/list')

// 新增/修改/删除商品
export const addProduct = data => request.post('/product', data)
export const updateProduct = data => request.put('/product', data)
export const deleteProduct = id => request.delete(`/product/${id}`)

// 上架/下架
export const toggleProductStatus = (id, status) => request.put(`/product/${id}/status`, null, { params: { status } })

// 规格类型+选项(商品编辑弹窗用)
export const listSpecTypes = () => request.get('/spec/types')

// 规格维护
export const addSpecType = data => request.post('/spec/type', data)
export const updateSpecType = data => request.put('/spec/type', data)
export const deleteSpecType = id => request.delete(`/spec/type/${id}`)
export const addSpecOption = data => request.post('/spec/option', data)
export const deleteSpecOption = id => request.delete(`/spec/option/${id}`)
