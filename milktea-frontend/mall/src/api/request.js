import axios from 'axios'
import { showToast } from 'vant'
import router from '../router'
import { getToken, removeToken } from '../utils/auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      showToast(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response && error.response.status === 401) {
      removeToken()
      showToast('登录已过期，请重新登录')
      router.push('/login')
    } else {
      showToast(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request
