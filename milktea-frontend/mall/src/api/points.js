import request from './request'

// 我的积分
export const getPoints = () => request.get('/user/points')

// 积分明细
export const pointsFlows = (limit = 50) => request.get('/user/points/flows', { params: { limit } })
