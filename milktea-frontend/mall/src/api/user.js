import request from './request'

// 登录
export const login = data => request.post('/user/login', data)

// 注册
export const register = data => request.post('/user/register', data)

// 查用户信息
export const getUserInfo = id => request.get(`/user/${id}`)

// 修改密码
export const updatePassword = data => request.put('/user/password', data)

// 修改个人信息
export const updateUserInfo = data => request.put('/user/info', data)

// 头像上传(multipart), 返回图片URL; 后端同时更新当前用户avatar
export const uploadAvatar = formData => request.post('/user/avatar', formData)

// 可领券列表
export const listCoupons = () => request.get('/user/coupon/list')

// 领券
export const receiveCoupon = couponId => request.post(`/user/coupon/receive/${couponId}`)

// 我的未使用券
export const myCoupons = () => request.get('/user/coupon/my')

// 每周9.9生效中的特价活动
export const listWeekSpecials = () => request.get('/user/weekspecial/active')

// 我的积分(会员中心)
export const myPoints = () => request.get('/user/points')
