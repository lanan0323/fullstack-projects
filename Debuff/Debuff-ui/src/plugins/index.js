// 导入页签操作插件
import tab from './tab'
// 导入权限认证插件
import auth from './auth'
// 导入缓存插件
import cache from './cache'
// 导入模态框插件
import modal from './modal'
// 导入文件下载插件
import download from './download'

// 导出插件安装函数，将工具挂载到 Vue 全局属性上
export default function installPlugins(app){
  // 挂载页签操作对象到 this.$tab
  app.config.globalProperties.$tab = tab
  // 挂载权限认证对象到 this.$auth
  app.config.globalProperties.$auth = auth
  // 挂载缓存对象到 this.$cache
  app.config.globalProperties.$cache = cache
  // 挂载模态框对象到 this.$modal
  app.config.globalProperties.$modal = modal
  // 挂载文件下载对象到 this.$download
  app.config.globalProperties.$download = download
}
