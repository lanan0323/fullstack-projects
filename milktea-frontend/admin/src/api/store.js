import request from './request'

// ========== 门店服务 API(后端: 成员D, 网关前缀 /api) ==========

// 原料分页
export const pageMaterials = params => request.get('/material/page', { params })

// 原料列表(下拉)
export const listMaterials = () => request.get('/material/list')

// 原料详情
export const getMaterial = id => request.get(`/material/${id}`)

// 新增/修改/删除原料
export const addMaterial = data => request.post('/material', data)
export const updateMaterial = data => request.put('/material', data)
export const deleteMaterial = id => request.delete(`/material/${id}`)

// 出入库
export const stockOperation = data => request.post('/material/stock', data)

// 预警列表
export const warningList = () => request.get('/material/warning')

// 出入库流水分页
export const pageStockRecords = params => request.get('/stock/record/page', { params })

// 某原料流水
export const stockRecordsByMaterial = materialId => request.get(`/stock/record/material/${materialId}`)

// 工作台聚合数据
export const dashboardData = () => request.get('/statistics/dashboard')

// 营业额趋势
export const revenueTrend = params => request.get('/statistics/revenue', { params })

// 销量排行
export const salesRank = params => request.get('/statistics/sales-rank', { params })

// ========== 商品配方(下单自动扣原料) ==========
export const getRecipe = productId => request.get(`/recipe/${productId}`)
export const saveRecipe = (productId, rows) => request.post(`/recipe/${productId}`, rows)
export const clearRecipe = productId => request.delete(`/recipe/${productId}`)
