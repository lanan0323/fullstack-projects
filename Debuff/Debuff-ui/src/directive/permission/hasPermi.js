// v-hasPermi 操作权限处理指令
// Copyright (c) 2019 ruoyi

// 导入用户 store 获取权限信息
import useUserStore from '@/store/modules/user'

export default {
  // 指令挂载时检查权限
  mounted(el, binding, vnode) {
    // 获取指令绑定的值
    const { value } = binding
    // 定义全部权限标识
    const all_permission = "*:*:*";
    // 从 store 获取当前用户的权限数组
    const permissions = useUserStore().permissions

    // 检查绑定值是否为非空数组
    if (value && value instanceof Array && value.length > 0) {
      // 获取需要校验的权限标识数组
      const permissionFlag = value

      // 检查用户权限中是否包含任一指定权限
      const hasPermissions = permissions.some(permission => {
        // 拥有全部权限或匹配指定权限则返回 true
        return all_permission === permission || permissionFlag.includes(permission)
      })

      // 没有权限则移除 DOM 元素
      if (!hasPermissions) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      // 未设置权限值时抛出错误提示
      throw new Error(`请设置操作权限标签值`)
    }
  }
}
