// 定义缓动函数（二次方InOut）
Math.easeInOutQuad = function(t, b, c, d) {
  t /= d / 2
  // 前半段加速
  if (t < 1) {
    return c / 2 * t * t + b
  }
  t--
  // 后半段减速
  return -c / 2 * (t * (t - 2) - 1) + b
}

// 获取 requestAnimationFrame 兼容函数
var requestAnimFrame = (function() {
  return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || function(callback) { window.setTimeout(callback, 1000 / 60) }
})()

// 设置页面滚动位置（兼容多种浏览器）
function move(amount) {
  document.documentElement.scrollTop = amount
  document.body.parentNode.scrollTop = amount
  document.body.scrollTop = amount
}

// 获取当前滚动位置
function position() {
  return document.documentElement.scrollTop || document.body.parentNode.scrollTop || document.body.scrollTop
}

// 平滑滚动到指定位置
export function scrollTo(to, duration, callback) {
  // 获取起始位置
  const start = position()
  // 计算需要滚动的距离
  const change = to - start
  // 每次递增的时间间隔（毫秒）
  const increment = 20
  let currentTime = 0
  // 默认动画时长 500ms
  duration = (typeof (duration) === 'undefined') ? 500 : duration
  // 动画执行函数
  var animateScroll = function() {
    // 累加当前时间
    currentTime += increment
    // 使用缓动函数计算当前值
    var val = Math.easeInOutQuad(currentTime, start, change, duration)
    // 移动滚动条
    move(val)
    // 动画未结束则继续
    if (currentTime < duration) {
      requestAnimFrame(animateScroll)
    } else {
      // 动画结束执行回调
      if (callback && typeof (callback) === 'function') {
        callback()
      }
    }
  }
  animateScroll()
}
