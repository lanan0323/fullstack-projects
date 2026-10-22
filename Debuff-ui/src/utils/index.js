// 导入 parseTime 时间格式化函数
import { parseTime } from './ruoyi'

// 表格时间格式化，返回 yyyy-MM-dd HH:mm:ss 格式
export function formatDate(cellValue) {
  // 空值返回空字符串
  if (cellValue == null || cellValue == "") return "";
  // 将值转换为 Date 对象
  var date = new Date(cellValue) 
  // 获取年份
  var year = date.getFullYear()
  // 获取月份并补零
  var month = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1
  // 获取日期并补零
  var day = date.getDate() < 10 ? '0' + date.getDate() : date.getDate() 
  // 获取小时并补零
  var hours = date.getHours() < 10 ? '0' + date.getHours() : date.getHours() 
  // 获取分钟并补零
  var minutes = date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes() 
  // 获取秒数并补零
  var seconds = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
  // 拼接为完整的时间字符串
  return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + ':' + seconds
}

// 格式化时间为相对时间（如"刚刚""5分钟前""3小时前"）
export function formatTime(time, option) {
  // 10 位时间戳转为 13 位毫秒
  if (('' + time).length === 10) {
    time = parseInt(time) * 1000
  } else {
    time = +time
  }
  // 创建 Date 对象
  const d = new Date(time)
  // 获取当前时间戳
  const now = Date.now()

  // 计算时间差（秒）
  const diff = (now - d) / 1000

  // 30 秒内显示"刚刚"
  if (diff < 30) {
    return '刚刚'
  } else if (diff < 3600) {
    // 1 小时内显示"X分钟前"
    return Math.ceil(diff / 60) + '分钟前'
  } else if (diff < 3600 * 24) {
    // 24 小时内显示"X小时前"
    return Math.ceil(diff / 3600) + '小时前'
  } else if (diff < 3600 * 24 * 2) {
    // 48 小时内显示"1天前"
    return '1天前'
  }
  // 如果有格式化参数则使用 parseTime
  if (option) {
    return parseTime(time, option)
  } else {
    // 否则返回 "X月X日X时X分" 格式
    return (
      d.getMonth() +
      1 +
      '月' +
      d.getDate() +
      '日' +
      d.getHours() +
      '时' +
      d.getMinutes() +
      '分'
    )
  }
}

// 从 URL 中解析查询参数为对象
export function getQueryObject(url) {
  // 默认使用当前页面 URL
  url = url == null ? window.location.href : url
  // 截取问号后的查询字符串
  const search = url.substring(url.lastIndexOf('?') + 1)
  const obj = {}
  // 正则匹配 key=value 格式
  const reg = /([^?&=]+)=([^?&=]*)/g
  // 替换匹配结果，构建参数对象
  search.replace(reg, (rs, $1, $2) => {
    const name = decodeURIComponent($1)
    let val = decodeURIComponent($2)
    val = String(val)
    obj[name] = val
    return rs
  })
  return obj
}

// 计算 UTF-8 字符串的字节长度
export function byteLength(str) {
  // 初始长度为字符数
  let s = str.length
  // 从后往前遍历每个字符
  for (var i = str.length - 1; i >= 0; i--) {
    const code = str.charCodeAt(i)
    // 2 字节字符加 1
    if (code > 0x7f && code <= 0x7ff) s++
    // 3 字节字符加 2
    else if (code > 0x7ff && code <= 0xffff) s += 2
    // 代理对高代理位回退一步
    if (code >= 0xDC00 && code <= 0xDFFF) i--
  }
  return s
}

// 清除数组中的假值元素（null、undefined、''、0、false）
export function cleanArray(actual) {
  const newArray = []
  // 遍历数组，只保留真值元素
  for (let i = 0; i < actual.length; i++) {
    if (actual[i]) {
      newArray.push(actual[i])
    }
  }
  return newArray
}

// 将对象序列化为 URL 查询参数格式（key1=val1&key2=val2）
export function param(json) {
  // 空值返回空字符串
  if (!json) return ''
  // 遍历对象键，编码后拼接为查询参数
  return cleanArray(
    Object.keys(json).map(key => {
      if (json[key] === undefined) return ''
      return encodeURIComponent(key) + '=' + encodeURIComponent(json[key])
    })
  ).join('&')
}

// 将 URL 中的查询参数解析为对象
export function param2Obj(url) {
  // 截取问号后的部分并解码
  const search = decodeURIComponent(url.split('?')[1]).replace(/\+/g, ' ')
  if (!search) {
    return {}
  }
  const obj = {}
  // 按 & 分割为键值对数组
  const searchArr = search.split('&')
  // 遍历每个键值对
  searchArr.forEach(v => {
    // 查找等号位置
    const index = v.indexOf('=')
    if (index !== -1) {
      const name = v.substring(0, index)
      const val = v.substring(index + 1, v.length)
      obj[name] = val
    }
  })
  return obj
}

// 将 HTML 字符串转换为纯文本
export function html2Text(val) {
  // 创建临时 div 元素
  const div = document.createElement('div')
  div.innerHTML = val
  // 获取文本内容
  return div.textContent || div.innerText
}

// 深度合并两个对象，source 的优先级高于 target
export function objectMerge(target, source) {
  // target 非对象时初始化为空对象
  if (typeof target !== 'object') {
    target = {}
  }
  // source 是数组则直接返回副本
  if (Array.isArray(source)) {
    return source.slice()
  }
  // 遍历 source 的每个属性
  Object.keys(source).forEach(property => {
    const sourceProperty = source[property]
    // 属性值为对象则递归合并
    if (typeof sourceProperty === 'object') {
      target[property] = objectMerge(target[property], sourceProperty)
    } else {
      // 否则直接赋值
      target[property] = sourceProperty
    }
  })
  return target
}

// 切换元素的 CSS 类名（有则移除，无则添加）
export function toggleClass(element, className) {
  if (!element || !className) {
    return
  }
  let classString = element.className
  // 查找类名在字符串中的位置
  const nameIndex = classString.indexOf(className)
  if (nameIndex === -1) {
    // 不存在则追加
    classString += '' + className
  } else {
    // 存在则移除
    classString =
      classString.substr(0, nameIndex) +
      classString.substr(nameIndex + className.length)
  }
  element.className = classString
}

// 获取时间范围（start 返回 90 天前，end 返回今天零点）
export function getTime(type) {
  if (type === 'start') {
    // 返回 90 天前的时间戳
    return new Date().getTime() - 3600 * 1000 * 24 * 90
  } else {
    // 返回今天零点的时间戳
    return new Date(new Date().toDateString())
  }
}

// 防抖函数，控制函数执行频率
export function debounce(func, wait, immediate) {
  let timeout, args, context, timestamp, result

  // 延迟执行函数
  const later = function() {
    // 计算距上一次触发的时间间隔
    const last = +new Date() - timestamp

    // 间隔小于设定值则重新定时
    if (last < wait && last > 0) {
      timeout = setTimeout(later, wait - last)
    } else {
      timeout = null
      // 非立即模式才执行函数
      if (!immediate) {
        result = func.apply(context, args)
        if (!timeout) context = args = null
      }
    }
  }

  // 返回包装后的函数
  return function(...args) {
    context = this
    // 记录当前时间戳
    timestamp = +new Date()
    // 是否立即执行
    const callNow = immediate && !timeout
    // 没有定时器则重新设定
    if (!timeout) timeout = setTimeout(later, wait)
    // 立即模式则马上执行
    if (callNow) {
      result = func.apply(context, args)
      context = args = null
    }

    return result
  }
}

// 简易深拷贝（不支持循环引用等复杂场景）
export function deepClone(source) {
  if (!source && typeof source !== 'object') {
    throw new Error('error arguments', 'deepClone')
  }
  // 根据构造函数判断是数组还是对象
  const targetObj = source.constructor === Array ? [] : {}
  // 遍历每个属性
  Object.keys(source).forEach(keys => {
    // 属性值为对象则递归拷贝
    if (source[keys] && typeof source[keys] === 'object') {
      targetObj[keys] = deepClone(source[keys])
    } else {
      targetObj[keys] = source[keys]
    }
  })
  return targetObj
}

// 数组去重
export function uniqueArr(arr) {
  // 利用 Set 去重后转回数组
  return Array.from(new Set(arr))
}

// 生成唯一字符串（时间戳+随机数组合）
export function createUniqueString() {
  // 获取当前时间戳字符串
  const timestamp = +new Date() + ''
  // 生成随机数
  const randomNum = parseInt((1 + Math.random()) * 65536) + ''
  // 拼接后转为 32 进制字符串
  return (+(randomNum + timestamp)).toString(32)
}

// 判断元素是否包含指定 CSS 类名
export function hasClass(ele, cls) {
  return !!ele.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'))
}

// 给元素添加 CSS 类名
export function addClass(ele, cls) {
  if (!hasClass(ele, cls)) ele.className += ' ' + cls
}

// 移除元素的 CSS 类名
export function removeClass(ele, cls) {
  if (hasClass(ele, cls)) {
    const reg = new RegExp('(\\s|^)' + cls + '(\\s|$)')
    ele.className = ele.className.replace(reg, ' ')
  }
}

// 创建字符串映射查找函数（用于快速判断字符串是否在列表中）
export function makeMap(str, expectsLowerCase) {
  // 创建空对象作为映射表
  const map = Object.create(null)
  // 按逗号分割为列表
  const list = str.split(',')
  // 遍历列表建立映射
  for (let i = 0; i < list.length; i++) {
    map[list[i]] = true
  }
  // 根据是否忽略大小写返回查找函数
  return expectsLowerCase
    ? val => map[val.toLowerCase()]
    : val => map[val]
}

// 导出 export default 字符串常量
export const exportDefault = 'export default '

// 代码美化配置（HTML 和 JS 格式化选项）
export const beautifierConf = {
  html: {
    indent_size: '2',
    indent_char: ' ',
    max_preserve_newlines: '-1',
    preserve_newlines: false,
    keep_array_indentation: false,
    break_chained_methods: false,
    indent_scripts: 'separate',
    brace_style: 'end-expand',
    space_before_conditional: true,
    unescape_strings: false,
    jslint_happy: false,
    end_with_newline: true,
    wrap_line_length: '110',
    indent_inner_html: true,
    comma_first: false,
    e4x: true,
    indent_empty_lines: true
  },
  js: {
    indent_size: '2',
    indent_char: ' ',
    max_preserve_newlines: '-1',
    preserve_newlines: false,
    keep_array_indentation: false,
    break_chained_methods: false,
    indent_scripts: 'normal',
    brace_style: 'end-expand',
    space_before_conditional: true,
    unescape_strings: false,
    jslint_happy: true,
    end_with_newline: true,
    wrap_line_length: '110',
    indent_inner_html: true,
    comma_first: false,
    e4x: true,
    indent_empty_lines: true
  }
}

// 字符串首字母转大写
export function titleCase(str) {
  return str.replace(/( |^)[a-z]/g, L => L.toUpperCase())
}

// 下划线命名转驼峰命名
export function camelCase(str) {
  return str.replace(/_[a-z]/g, str1 => str1.substr(-1).toUpperCase())
}

// 判断字符串是否为数字（含小数和正负数）
export function isNumberStr(str) {
  return /^[+-]?(0|([1-9]\d*))(\.\d+)?$/g.test(str)
}
