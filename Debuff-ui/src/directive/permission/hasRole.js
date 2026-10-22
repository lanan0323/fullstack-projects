// v-hasRole 角色权限处理指令
// Copyright (c) 2019 ruoyi

// 导入用户 store 获取角色信息
import useUserStore from '@/store/modules/user'

export default {
  // 指令挂载时检查角色
  mounted(el, binding, vnode) {
    // 获取指令绑定的值
    const { value } = binding
    // 定义超级管理员角色标识
    const super_admin = "admin";
    // 从 store 获取当前用户的角色数组
    const roles = useUserStore().roles

    // 检查绑定值是否为非空数组
    if (value && value instanceof Array && value.length > 0) {
      // 获取需要校验的角色标识数组
      const roleFlag = value

      // 检查用户角色中是否包含任一指定角色
      const hasRole = roles.some(role => {
        // 是超级管理员或匹配指定角色则返回 true
        return super_admin === role || roleFlag.includes(role)
      })

      // 没有角色则移除 DOM 元素
      if (!hasRole) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      // 未设置角色值时抛出错误提示
      throw new Error(`请设置角色权限标签值`)
    }
  }
}
