// 导入 js-cookie 库用于操作浏览器 Cookie
import Cookies from 'js-cookie'

// 定义 Cookie 中 token 的键名
const TokenKey = 'Admin-Token'

// 从 Cookie 中获取 token
export function getToken() {
  return Cookies.get(TokenKey)
}

// 将 token 存储到 Cookie 中
export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

// 从 Cookie 中移除 token
export function removeToken() {
  return Cookies.remove(TokenKey)
}
