// 判断 URL 是否为 http 或 https 协议
export function isHttp(url) {
  return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1
}

// 判断路径是否为外链（http/https/mailto/tel）
export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

// 验证用户名是否为合法值（admin 或 editor）
export function validUsername(str) {
  // 合法用户名列表
  const valid_map = ['admin', 'editor']
  return valid_map.indexOf(str.trim()) >= 0
}

// 验证是否为合法 URL
export function validURL(url) {
  // URL 格式正则
  const reg = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
  return reg.test(url)
}

// 验证字符串是否全为小写字母
export function validLowerCase(str) {
  const reg = /^[a-z]+$/
  return reg.test(str)
}

// 验证字符串是否全为大写字母
export function validUpperCase(str) {
  const reg = /^[A-Z]+$/
  return reg.test(str)
}

// 验证字符串是否全为字母（不区分大小写）
export function validAlphabets(str) {
  const reg = /^[A-Za-z]+$/
  return reg.test(str)
}

// 验证邮箱格式是否正确
export function validEmail(email) {
  const reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  return reg.test(email)
}

// 判断值是否为字符串类型
export function isString(str) {
  if (typeof str === 'string' || str instanceof String) {
    return true
  }
  return false
}

// 判断值是否为数组类型
export function isArray(arg) {
  // 兼容不支持 Array.isArray 的环境
  if (typeof Array.isArray === 'undefined') {
    return Object.prototype.toString.call(arg) === '[object Array]'
  }
  return Array.isArray(arg)
}
