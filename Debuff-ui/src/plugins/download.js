// 导入 axios 用于发送 HTTP 请求
import axios from 'axios'
// 导入 Element Plus 的加载和消息组件
import { ElLoading, ElMessage } from 'element-plus'
// 导入 file-saver 用于保存文件
import { saveAs } from 'file-saver'
// 导入获取 token 的工具函数
import { getToken } from '@/utils/auth'
// 导入错误码映射
import errorCode from '@/utils/errorCode'
// 导入 blob 校验函数
import { blobValidate } from '@/utils/ruoyi'

// 获取 API 基础路径
const baseURL = import.meta.env.VITE_APP_BASE_API
// 下载加载实例变量
let downloadLoadingInstance;

// 导出文件下载工具对象
export default {
  // 按文件名下载（支持下载后删除）
  name(name, isDelete = true) {
    // 拼接下载接口 URL，对文件名进行编码
    var url = baseURL + "/common/download?fileName=" + encodeURIComponent(name) + "&delete=" + isDelete
    // 发送 GET 请求，响应类型为 blob
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { 'Authorization': 'Bearer ' + getToken() }
    }).then((res) => {
      // 校验返回的 blob 数据是否有效
      const isBlob = blobValidate(res.data);
      if (isBlob) {
        // 有效则创建 Blob 对象并保存
        const blob = new Blob([res.data])
        this.saveAs(blob, decodeURIComponent(res.headers['download-filename']))
      } else {
        // 无效则打印错误信息
        this.printErrMsg(res.data);
      }
    })
  },
  // 按资源路径下载文件
  resource(resource) {
    // 拼接资源下载接口 URL
    var url = baseURL + "/common/download/resource?resource=" + encodeURIComponent(resource);
    // 发送 GET 请求，响应类型为 blob
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { 'Authorization': 'Bearer ' + getToken() }
    }).then((res) => {
      // 校验返回的 blob 数据是否有效
      const isBlob = blobValidate(res.data);
      if (isBlob) {
        // 有效则创建 Blob 对象并保存
        const blob = new Blob([res.data])
        this.saveAs(blob, decodeURIComponent(res.headers['download-filename']))
      } else {
        // 无效则打印错误信息
        this.printErrMsg(res.data);
      }
    })
  },
  // 下载 zip 压缩包（带加载遮罩）
  zip(url, name) {
    // 拼接完整的下载 URL
    var url = baseURL + url
    // 显示下载加载遮罩层
    downloadLoadingInstance = ElLoading.service({ text: "正在下载数据，请稍候", background: "rgba(0, 0, 0, 0.7)", })
    // 发送 GET 请求，响应类型为 blob
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { 'Authorization': 'Bearer ' + getToken() }
    }).then((res) => {
      // 校验返回的 blob 数据是否有效
      const isBlob = blobValidate(res.data);
      if (isBlob) {
        // 有效则创建 zip 类型的 Blob 对象并保存
        const blob = new Blob([res.data], { type: 'application/zip' })
        this.saveAs(blob, name)
      } else {
        // 无效则打印错误信息
        this.printErrMsg(res.data);
      }
      // 关闭加载遮罩
      downloadLoadingInstance.close();
    }).catch((r) => {
      // 请求失败时输出错误日志
      console.error(r)
      // 显示错误提示
      ElMessage.error('下载文件出现错误，请联系管理员！')
      // 关闭加载遮罩
      downloadLoadingInstance.close();
    })
  },
  // 调用 file-saver 保存文件
  saveAs(text, name, opts) {
    saveAs(text, name, opts);
  },
  // 解析并显示错误消息
  async printErrMsg(data) {
    // 读取 blob 数据的文本内容
    const resText = await data.text();
    // 解析 JSON 获取错误对象
    const rspObj = JSON.parse(resText);
    // 根据错误码获取对应的错误消息
    const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
    // 显示错误消息提示
    ElMessage.error(errMsg);
  }
}
