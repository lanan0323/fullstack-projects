// 导入用户 store 用于获取权限和角色
import useUserStore from '@/store/modules/user'

// 检查用户是否拥有指定权限的内部方法
function authPermission(permission) {
  // 定义全部权限标识
  const all_permission = "*:*:*";
  // 从 store 获取当前用户权限数组
  const permissions = useUserStore().permissions
  // 权限值存在且非空时进行校验
  if (permission && permission.length > 0) {
    // 检查是否拥有全部权限或匹配指定权限
    return permissions.some(v => {
      return all_permission === v || v === permission
    })
  } else {
    // 未传权限值返回 false
    return false
  }
}

// 检查用户是否拥有指定角色的内部方法
function authRole(role) {
  // 定义超级管理员角色标识
  const super_admin = "admin";
  // 从 store 获取当前用户角色数组
  const roles = useUserStore().roles
  // 角色值存在且非空时进行校验
  if (role && role.length > 0) {
    // 检查是否为超级管理员或匹配指定角色
    return roles.some(v => {
      return super_admin === v || v === role
    })
  } else {
    // 未传角色值返回 false
    return false
  }
}

// 导出权限认证对象
export default {
  // 验证用户是否具备某权限
  hasPermi(permission) {
    return authPermission(permission);
  },
  // 验证用户是否含有指定权限之一（或关系）
  hasPermiOr(permissions) {
    return permissions.some(item => {
      return authPermission(item)
    })
  },
  // 验证用户是否含有全部指定权限（与关系）
  hasPermiAnd(permissions) {
    return permissions.every(item => {
      return authPermission(item)
    })
  },
  // 验证用户是否具备某角色
  hasRole(role) {
    return authRole(role);
  },
  // 验证用户是否含有指定角色之一（或关系）
  hasRoleOr(roles) {
    return roles.some(item => {
      return authRole(item)
    })
  },
  // 验证用户是否含有全部指定角色（与关系）
  hasRoleAnd(roles) {
    return roles.every(item => {
      return authRole(item)
    })
  }
}
