import request from './request'

// ========== 优惠券管理 API(后端: 组员A milktea-user /coupon, 网关 /api/coupon/**) ==========

// 券模板分页
export const pageCoupons = params => request.get('/coupon/page', { params })

// 新增券模板
export const addCoupon = data => request.post('/coupon', data)

// 编辑券模板
export const updateCoupon = (id, data) => request.put(`/coupon/${id}`, data)

// 上下架(1上架/0下架)
export const updateCouponStatus = (id, status) => request.put(`/coupon/${id}/status/${status}`)

// 删除券模板
export const deleteCoupon = id => request.delete(`/coupon/${id}`)

// 定向发放(params: { couponId, phone?, userId? })
export const issueCoupon = params => request.post('/coupon/issue', null, { params })

// 领取/发放记录
export const couponRecords = params => request.get('/coupon/records', { params })
