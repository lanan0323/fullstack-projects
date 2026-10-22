// 注释：导入登录、退出登录、获取用户信息的 API 函数
import { login, logout, getInfo } from '@/api/login'
// 注释：导入获取 token、设置 token、移除 token 的工具函数
import { getToken, setToken, removeToken } from '@/utils/auth'
// 注释：导入默认头像图片
import defAva from '@/assets/images/profile.jpg'

// 注释：定义用户 store，使用 Pinia 的 defineStore 方法
const useUserStore = defineStore(
  // 注释：store 的名称标识为 'user'
  'user',
  {
    // 注释：定义 store 的状态
    state: () => ({
      // 注释：用户的 token，从 Cookie 中获取初始值
      token: getToken(),
      // 注释：用户 ID
      id: '',
      // 注释：用户名称
      name: '',
      // 注释：用户头像 URL
      avatar: '',
      // 注释：用户角色数组
      roles: [],
      // 注释：用户权限数组
      permissions: []
    }),
    // 注释：定义 store 的操作方法
    actions: {
      // 注释：登录方法
      // 登录
      login(userInfo) {
        // 注释：获取用户名并去除两端空格
        const username = userInfo.username.trim()
        // 注释：获取密码
        const password = userInfo.password
        // 注释：获取验证码
        const code = userInfo.code
        // 注释：获取验证码的唯一标识
        const uuid = userInfo.uuid
        // 注释：返回 Promise 对象
        return new Promise((resolve, reject) => {
          // 注释：调用登录 API
          login(username, password, code, uuid).then(res => {
            // 注释：将 token 存储到 Cookie
            setToken(res.token)
            // 注释：将 token 存储到 store 状态中
            this.token = res.token
            // 注释：解析 Promise，表示登录成功
            resolve()
          }).catch(error => {
            // 注释：如果登录失败，拒绝 Promise
            reject(error)
          })
        })
      },
      // 注释：获取用户信息方法
      // 获取用户信息
      getInfo() {
        // 注释：返回 Promise 对象
        return new Promise((resolve, reject) => {
          // 注释：调用获取用户信息 API
          getInfo().then(res => {
            // 注释：获取用户对象
            const user = res.user
            // 注释：处理用户头像，如果为空则使用默认头像，否则拼接完整 URL
            const avatar = (user.avatar == "" || user.avatar == null) ? defAva : import.meta.env.VITE_APP_BASE_API + user.avatar;

            // 注释：验证返回的角色是否为非空数组
            if (res.roles && res.roles.length > 0) { // 验证返回的roles是否是一个非空数组
              // 注释：将角色数组存储到 store 状态中
              this.roles = res.roles
              // 注释：将权限数组存储到 store 状态中
              this.permissions = res.permissions
            } else {
              // 注释：如果没有角色，设置默认角色
              this.roles = ['ROLE_DEFAULT']
            }
            // 注释：将用户 ID 存储到 store 状态中
            this.id = user.userId
            // 注释：将用户名称存储到 store 状态中（优先使用昵称，其次使用用户名）
            this.name = user.nickName || user.userName
            // 注释：将用户头像存储到 store 状态中
            this.avatar = avatar
            // 注释：解析 Promise，返回完整的响应数据
            resolve(res)
          }).catch(error => {
            // 注释：如果获取用户信息失败，拒绝 Promise
            reject(error)
          })
        })
      },
      // 注释：退出登录方法
      // 退出系统
      logOut() {
        // 注释：返回 Promise 对象
        return new Promise((resolve, reject) => {
          // 注释：调用退出登录 API，传入 token
          logout(this.token).then(() => {
            // 注释：清空 store 中的 token
            this.token = ''
            // 注释：清空 store 中的角色数组
            this.roles = []
            // 注释：清空 store 中的权限数组
            this.permissions = []
            // 注释：从 Cookie 中移除 token
            removeToken()
            // 注释：解析 Promise，表示退出成功
            resolve()
          }).catch(error => {
            // 注释：如果退出登录失败，拒绝 Promise
            reject(error)
          })
        })
      }
    }
  })

// 注释：导出用户 store 供其他模块使用
export default useUserStore
