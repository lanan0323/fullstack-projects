import request from './request'

// 我的收货地址列表
export const listAddresses = () => request.get('/user/address/list')

// 新增地址
export const addAddress = data => request.post('/user/address', data)

// 修改地址
export const updateAddress = data => request.put('/user/address', data)

// 删除地址
export const deleteAddress = id => request.delete(`/user/address/${id}`)

// 设为默认地址
export function setDefaultAddress(id) {
  return request.put(`/user/address/default/${id}`)
}
