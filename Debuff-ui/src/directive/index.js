// 导入角色权限指令
import hasRole from './permission/hasRole'
// 导入操作权限指令
import hasPermi from './permission/hasPermi'
// 导入复制文本指令
import copyText from './common/copyText'

// 导出指令注册函数，将自定义指令注册到 Vue 应用
export default function directive(app){
  // 注册 v-hasRole 角色权限指令
  app.directive('hasRole', hasRole)
  // 注册 v-hasPermi 操作权限指令
  app.directive('hasPermi', hasPermi)
  // 注册 v-copyText 复制文本指令
  app.directive('copyText', copyText)
}
