// 注释：导入 axios 库，用于发送 HTTP 请求
import axios from 'axios'
// 注释：导入 Element Plus 组件，用于显示通知、消息框、消息和加载动画
import { ElNotification , ElMessageBox, ElMessage, ElLoading } from 'element-plus'
// 注释：导入获取 token 的工具函数
import { getToken } from '@/utils/auth'
// 注释：导入错误码映射表
import errorCode from '@/utils/errorCode'
// 注释：导入参数转换和 blob 验证工具函数
import { tansParams, blobValidate } from '@/utils/ruoyi'
// 注释：导入缓存插件
import cache from '@/plugins/cache'
// 注释：导入文件保存工具函数
import { saveAs } from 'file-saver'
// 注释：导入用户 store 模块
import useUserStore from '@/store/modules/user'

// 注释：定义下载加载实例变量
let downloadLoadingInstance;
// 注释：定义是否显示重新登录提示的标志
// 是否显示重新登录
export let isRelogin = { show: false };

// 注释：设置 axios 默认请求头的内容类型为 JSON
axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 注释：创建 axios 实例
// 创建axios实例
const service = axios.create({
  // axios中请求配置有baseURL选项，表示请求URL公共部分
  // 注释：设置请求的基础 URL，从环境变量中获取
  baseURL: import.meta.env.VITE_APP_BASE_API,
  // 注释：设置请求超时时间为 10 秒
  // 超时
  timeout: 10000
})

// 注释：配置请求拦截器
// request拦截器
service.interceptors.request.use(config => {
  // 注释：判断是否需要设置 token（默认需要）
  // 是否需要设置 token
  const isToken = (config.headers || {}).isToken === false
  // 注释：判断是否需要防止重复提交（默认需要）
  // 是否需要防止数据重复提交
  const isRepeatSubmit = (config.headers || {}).repeatSubmit === false
  // 注释：如果有 token 且需要设置 token
  if (getToken() && !isToken) {
    // 注释：在请求头中添加 Bearer token
    config.headers['Authorization'] = 'Bearer ' + getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
  }
  // 注释：如果是 GET 请求且有 params 参数
  // get请求映射params参数
  if (config.method === 'get' && config.params) {
    // 注释：将 params 转换为 URL 查询字符串格式
    let url = config.url + '?' + tansParams(config.params);
    // 注释：移除末尾的 & 符号
    url = url.slice(0, -1);
    // 注释：清空 params 对象
    config.params = {};
    // 注释：更新请求 URL
    config.url = url;
  }
  // 注释：如果不是防止重复提交且是 POST 或 PUT 请求
  if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
    // 注释：构建请求对象
    const requestObj = {
      // 注释：请求 URL
      url: config.url,
      // 注释：请求数据（如果是对象则转换为 JSON 字符串）
      data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
      // 注释：当前时间戳
      time: new Date().getTime()
    }
    // 注释：计算请求数据大小（字节数）
    const requestSize = Object.keys(JSON.stringify(requestObj)).length; // 请求数据大小
    // 注释：设置限制大小为 5MB
    const limitSize = 5 * 1024 * 1024; // 限制存放数据5M
    // 注释：如果请求数据超过 5MB
    if (requestSize >= limitSize) {
      // 注释：输出警告信息，说明无法进行防重复提交验证
      console.warn(`[${config.url}]: ` + '请求数据大小超出允许的5M限制，无法进行防重复提交验证。')
      // 注释：直接返回配置，跳过防重复提交检查
      return config;
    }
    // 注释：从 sessionStorage 获取上次请求的对象
    const sessionObj = cache.session.getJSON('sessionObj')
    // 注释：如果 sessionStorage 中没有数据
    if (sessionObj === undefined || sessionObj === null || sessionObj === '') {
      // 注释：将当前请求对象存储到 sessionStorage
      cache.session.setJSON('sessionObj', requestObj)
    } else {
      // 注释：获取上次请求的 URL
      const s_url = sessionObj.url;                // 请求地址
      // 注释：获取上次请求的数据
      const s_data = sessionObj.data;              // 请求数据
      // 注释：获取上次请求的时间
      const s_time = sessionObj.time;              // 请求时间
      // 注释：设置间隔时间为 1000 毫秒（1秒）
      const interval = 1000;                       // 间隔时间(ms)，小于此时间视为重复提交
      // 注释：如果数据相同、时间间隔小于 1 秒且 URL 相同
      if (s_data === requestObj.data && requestObj.time - s_time < interval && s_url === requestObj.url) {
        // 注释：定义重复提交提示信息
        const message = '数据正在处理，请勿重复提交';
        // 注释：输出警告信息
        console.warn(`[${s_url}]: ` + message)
        // 注释：拒绝 Promise，阻止请求发送
        return Promise.reject(new Error(message))
      } else {
        // 注释：更新 sessionStorage 中的请求对象
        cache.session.setJSON('sessionObj', requestObj)
      }
    }
  }
  // 注释：返回配置对象，继续请求
  return config
}, error => {
    // 注释：打印错误信息
    console.log(error)
    // 注释：拒绝 Promise，返回错误
    return Promise.reject(error)
})

// 注释：配置响应拦截器
// 响应拦截器
service.interceptors.response.use(res => {
    // 注释：获取响应状态码，默认为 200
    // 未设置状态码则默认成功状态
    const code = res.data.code || 200;
    // 注释：获取错误信息，优先使用错误码映射，其次使用响应消息，最后使用默认错误信息
    // 获取错误信息
    const msg = errorCode[code] || res.data.msg || errorCode['default']
    // 注释：如果是二进制数据（blob 或 arraybuffer）
    // 二进制数据则直接返回
    if (res.request.responseType ===  'blob' || res.request.responseType ===  'arraybuffer') {
      // 注释：直接返回响应数据
      return res.data
    }
    // 注释：如果状态码是 401（未授权）
    if (code === 401) {
      // 注释：如果没有显示重新登录提示
      if (!isRelogin.show) {
        // 注释：设置显示重新登录提示标志
        isRelogin.show = true;
        // 注释：显示确认框，提示用户登录状态已过期
        ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', { confirmButtonText: '重新登录', cancelButtonText: '取消', type: 'warning' }).then(() => {
          // 注释：用户点击确认后，重置显示标志
          isRelogin.show = false;
          // 注释：调用用户 store 的退出登录方法
          useUserStore().logOut().then(() => {
            // 注释：重定向到首页
            location.href = '/index';
          })
      }).catch(() => {
        // 注释：用户点击取消后，重置显示标志
        isRelogin.show = false;
      });
    }
      // 注释：拒绝 Promise，返回错误信息
      return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
    } else if (code === 500) {
      // 注释：如果状态码是 500（服务器错误）
      // 注释：显示错误消息提示
      ElMessage({ message: msg, type: 'error' })
      // 注释：拒绝 Promise，返回错误
      return Promise.reject(new Error(msg))
    } else if (code === 601) {
      // 注释：如果状态码是 601（警告）
      // 注释：显示警告消息提示
      ElMessage({ message: msg, type: 'warning' })
      // 注释：拒绝 Promise，返回错误
      return Promise.reject(new Error(msg))
    } else if (code !== 200) {
      // 注释：如果状态码不是 200（其他错误）
      // 注释：显示错误通知
      ElNotification.error({ title: msg })
      // 注释：拒绝 Promise，返回错误
      return Promise.reject('error')
    } else {
      // 注释：如果状态码是 200（成功）
      // 注释：解析 Promise，返回响应数据
      return  Promise.resolve(res.data)
    }
  },
  error => {
    // 注释：打印错误信息
    console.log('err' + error)
    // 注释：定义错误消息变量
    let { message } = error;
    // 注释：如果是网络错误
    if (message == "Network Error") {
      // 注释：设置错误消息为"后端接口连接异常"
      message = "后端接口连接异常";
    } else if (message.includes("timeout")) {
      // 注释：如果是超时错误
      // 注释：设置错误消息为"系统接口请求超时"
      message = "系统接口请求超时";
    } else if (message.includes("Request failed with status code")) {
      // 注释：如果是状态码错误
      // 注释：设置错误消息为"系统接口 + 状态码 + 异常"
      message = "系统接口" + message.substr(message.length - 3) + "异常";
    }
    // 注释：显示错误消息，持续 5 秒
    ElMessage({ message: message, type: 'error', duration: 5 * 1000 })
    // 注释：拒绝 Promise，返回错误
    return Promise.reject(error)
  }
)

// 注释：定义通用下载方法
// 通用下载方法
export function download(url, params, filename, config) {
  // 注释：显示加载动画，提示用户正在下载数据
  downloadLoadingInstance = ElLoading.service({ text: "正在下载数据，请稍候", background: "rgba(0, 0, 0, 0.7)", })
  // 注释：发送 POST 请求下载文件
  return service.post(url, params, {
    // 注释：转换请求参数为 URL 查询字符串格式
    transformRequest: [(params) => { return tansParams(params) }],
    // 注释：设置请求头内容类型为表单格式
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    // 注释：设置响应类型为 blob
    responseType: 'blob',
    // 注释：合并额外配置
    ...config
  }).then(async (data) => {
    // 注释：验证返回的数据是否为有效的 blob
    const isBlob = blobValidate(data);
    // 注释：如果是有效的 blob 数据
    if (isBlob) {
      // 注释：创建 Blob 对象
      const blob = new Blob([data])
      // 注释：保存文件到本地
      saveAs(blob, filename)
    } else {
      // 注释：如果是错误响应
      // 注释：读取 blob 文本内容
      const resText = await data.text();
      // 注释：解析 JSON 数据
      const rspObj = JSON.parse(resText);
      // 注释：获取错误信息
      const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
      // 注释：显示错误消息
      ElMessage.error(errMsg);
    }
    // 注释：关闭加载动画
    downloadLoadingInstance.close();
  }).catch((r) => {
    // 注释：如果请求失败
    // 注释：打印错误信息
    console.error(r)
    // 注释：显示错误消息
    ElMessage.error('下载文件出现错误，请联系管理员！')
    // 注释：关闭加载动画
    downloadLoadingInstance.close();
  })
}

// 注释：导出 axios 实例作为默认导出
export default service
