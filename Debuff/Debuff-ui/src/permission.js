// 注释：导入路由器实例
import router from './router'
// 注释：导入 Element Plus 消息组件
import { ElMessage } from 'element-plus'
// 注释：导入 NProgress 进度条库
import NProgress from 'nprogress'
// 注释：导入 NProgress 样式文件
import 'nprogress/nprogress.css'
// 注释：导入获取 token 的工具函数
import { getToken } from '@/utils/auth'
// 注释：导入 HTTP URL 验证函数
import { isHttp } from '@/utils/validate'
// 注释：导入重新登录标志
import { isRelogin } from '@/utils/request'
// 注释：导入用户 store 模块
import useUserStore from '@/store/modules/user'
// 注释：导入设置 store 模块
import useSettingsStore from '@/store/modules/settings'
// 注释：导入权限 store 模块
import usePermissionStore from '@/store/modules/permission'

// 注释：配置 NProgress，隐藏加载指示器
NProgress.configure({ showSpinner: false });

// 注释：定义免登录白名单路径
const whiteList = ['/login', '/register'];

// 注释：定义后台管理路径前缀（普通用户禁止访问）
// 后台管理路径前缀（普通用户禁止访问）
const ADMIN_PATHS = ['/system', '/monitor', '/tool', '/admin', '/user'];

// 注释：定义判断路径是否为后台管理路径的函数
const isAdminPath = (path) => ADMIN_PATHS.some(p => path.startsWith(p));

// 注释：配置路由前置守卫
router.beforeEach((to, from, next) => {
  // 注释：启动进度条
  NProgress.start()
  // 注释：如果有 token
  if (getToken()) {
    // 注释：如果路由有标题，设置页面标题
    to.meta.title && useSettingsStore().setTitle(to.meta.title)
    // 注释：已登录状态
    /* has token*/
    if (to.path === '/login') {
      // 注释：如果已登录且访问登录页，重定向到首页
      next({ path: '/' })
      // 注释：完成进度条
      NProgress.done()
    } else if (whiteList.indexOf(to.path) !== -1) {
      // 注释：如果在白名单中，直接放行
      next()
    } else {
      // 注释：如果不在白名单中
      if (useUserStore().roles.length === 0) {
        // 注释：如果用户角色为空（首次登录或刷新页面）
        // 注释：设置显示重新登录提示标志
        isRelogin.show = true
        // 注释：获取用户信息
        // 判断当前用户是否已拉取完user_info信息
        useUserStore().getInfo().then(() => {
          // 注释：重置显示重新登录提示标志
          isRelogin.show = false
          // 注释：判断用户是否为管理员
          const isAdmin = useUserStore().roles.includes('admin')
          if (isAdmin) {
            // 注释：如果是管理员，加载后台动态菜单
            // 管理员：加载后端动态菜单（后台界面）
            usePermissionStore().generateRoutes().then(accessRoutes => {
              // 注释：遍历可访问的路由
              // 根据roles权限生成可访问的路由表
              accessRoutes.forEach(route => {
                // 注释：如果路由路径不是 HTTP URL
                if (!isHttp(route.path)) {
                  // 注释：动态添加路由
                  router.addRoute(route) // 动态添加可访问路由表
                }
              })
              // 注释：使用 next({ ...to, replace: true }) 确保路由添加完成后再导航
              next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
            })
          } else {
            // 注释：如果是普通用户
            // 普通用户：仅允许访问用户端界面
            if (to.path === '/' || to.path === '/index') {
              // 注释：如果访问首页或根路径，重定向到市场页面
              next({ path: '/market', replace: true })
            } else if (isAdminPath(to.path)) {
              // 注释：如果访问后台管理路径，重定向到 401 页面
              next({ path: '/401', replace: true })
            } else {
              // 注释：其他情况正常放行
              next({ ...to, replace: true })
            }
          }
        }).catch(err => {
          // 注释：如果获取用户信息失败
          // 注释：调用退出登录方法
          useUserStore().logOut().then(() => {
            // 注释：显示错误消息
            ElMessage.error(err)
            // 注释：重定向到登录页
            next({ path: '/' })
          })
        })
      } else {
        // 注释：如果用户角色已加载（二次导航或刷新后）
        // roles 已加载：二次导航/刷新后按角色拦截
        // 注释：判断用户是否为管理员
        const isAdmin = useUserStore().roles.includes('admin')
        if (!isAdmin && (to.path === '/' || to.path === '/index')) {
          // 注释：如果是普通用户且访问首页，重定向到市场页面
          next({ path: '/market', replace: true })
        } else if (!isAdmin && isAdminPath(to.path)) {
          // 注释：如果是普通用户且访问后台管理路径，重定向到 401 页面
          next({ path: '/401', replace: true })
        } else {
          // 注释：其他情况正常放行
          next()
        }
      }
    }
  } else {
    // 注释：如果没有 token（未登录）
    // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      // 注释：如果在免登录白名单中
      // 在免登录白名单，直接进入
      next()
    } else {
      // 注释：如果不在白名单中
      // 注释：重定向到登录页，并携带当前路径作为 redirect 参数
      next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
      // 注释：完成进度条
      NProgress.done()
    }
  }
})

// 注释：配置路由后置守卫
router.afterEach(() => {
  // 注释：完成进度条
  NProgress.done()
})
