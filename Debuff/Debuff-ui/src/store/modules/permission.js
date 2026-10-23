// 注释：导入权限验证插件
import auth from '@/plugins/auth'
// 注释：导入路由器实例、公共路由和动态路由
import router, { constantRoutes, dynamicRoutes } from '@/router'
// 注释：导入获取后端路由数据的 API 函数
import { getRouters } from '@/api/menu'
// 注释：导入后台管理布局组件
import Layout from '@/layout/index'
// 注释：导入父级视图组件（用于多级菜单）
import ParentView from '@/components/ParentView'
// 注释：导入内嵌链接组件
import InnerLink from '@/layout/components/InnerLink'

// 注释：使用 import.meta.glob 动态导入 views 目录下所有的 .vue 文件
// 匹配views里面所有的.vue文件
const modules = import.meta.glob('./../../views/**/*.vue')

// 注释：定义权限 store，使用 Pinia 的 defineStore 方法
const usePermissionStore = defineStore(
  // 注释：store 的名称标识为 'permission'
  'permission',
  {
    // 注释：定义 store 的状态
    state: () => ({
      // 注释：所有路由数组（包括公共路由和动态路由）
      routes: [],
      // 注释：动态添加的路由数组
      addRoutes: [],
      // 注释：默认路由数组（公共路由 + 动态路由）
      defaultRoutes: [],
      // 注释：顶部导航栏路由数组
      topbarRouters: [],
      // 注释：侧边栏路由数组
      sidebarRouters: []
    }),
    // 注释：定义 store 的操作方法
    actions: {
      // 注释：设置路由方法
      setRoutes(routes) {
        // 注释：将动态路由存储到 addRoutes 状态
        this.addRoutes = routes
        // 注释：将公共路由和动态路由合并后存储到 routes 状态
        this.routes = constantRoutes.concat(routes)
      },
      // 注释：设置默认路由方法
      setDefaultRoutes(routes) {
        // 注释：将公共路由和动态路由合并后存储到 defaultRoutes 状态
        this.defaultRoutes = constantRoutes.concat(routes)
      },
      // 注释：设置顶部导航栏路由方法
      setTopbarRoutes(routes) {
        // 注释：将路由存储到 topbarRouters 状态
        this.topbarRouters = routes
      },
      // 注释：设置侧边栏路由方法
      setSidebarRouters(routes) {
        // 注释：将路由存储到 sidebarRouters 状态
        this.sidebarRouters = routes
      },
      // 注释：生成路由方法
      generateRoutes(roles) {
        // 注释：返回 Promise 对象
        return new Promise(resolve => {
          // 注释：向后端请求路由数据
          // 向后端请求路由数据
          getRouters().then(res => {
            // 注释：深拷贝响应数据，用于侧边栏路由
            const sdata = JSON.parse(JSON.stringify(res.data))
            // 注释：深拷贝响应数据，用于重写路由
            const rdata = JSON.parse(JSON.stringify(res.data))
            // 注释：深拷贝响应数据，用于默认路由
            const defaultData = JSON.parse(JSON.stringify(res.data))
            // 注释：过滤并转换侧边栏路由
            const sidebarRoutes = filterAsyncRouter(sdata)
            // 注释：过滤并转换重写路由（用于动态添加）
            const rewriteRoutes = filterAsyncRouter(rdata, false, true)
            // 注释：过滤并转换默认路由
            const defaultRoutes = filterAsyncRouter(defaultData)
            // 注释：过滤动态路由（基于权限）
            const asyncRoutes = filterDynamicRoutes(dynamicRoutes)
            // 注释：遍历动态路由并添加到路由器
            asyncRoutes.forEach(route => { router.addRoute(route) })
            // 注释：设置重写路由到 store 状态
            this.setRoutes(rewriteRoutes)
            // 注释：设置侧边栏路由到 store 状态
            this.setSidebarRouters(constantRoutes.concat(sidebarRoutes))
            // 注释：设置默认路由到 store 状态
            this.setDefaultRoutes(sidebarRoutes)
            // 注释：设置顶部导航栏路由到 store 状态
            this.setTopbarRoutes(defaultRoutes)
            // 注释：解析 Promise，返回重写路由
            resolve(rewriteRoutes)
          })
        })
      }
    }
  })

// 注释：定义过滤异步路由的函数，将后台返回的路由字符串转换为组件对象
// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  // 注释：过滤路由数组
  return asyncRouterMap.filter(route => {
    // 注释：如果是重写模式且有子路由
    if (type && route.children) {
      // 注释：递归过滤子路由
      route.children = filterChildren(route.children)
    }
    // 注释：如果路由有组件属性
    if (route.component) {
      // 注释：特殊处理 Layout、ParentView、InnerLink 组件
      // Layout ParentView 组件特殊处理
      if (route.component === 'Layout') {
        // 注释：将字符串 'Layout' 替换为实际的 Layout 组件
        route.component = Layout
      } else if (route.component === 'ParentView') {
        // 注释：将字符串 'ParentView' 替换为实际的 ParentView 组件
        route.component = ParentView
      } else if (route.component === 'InnerLink') {
        // 注释：将字符串 'InnerLink' 替换为实际的 InnerLink 组件
        route.component = InnerLink
      } else {
        // 注释：加载其他视图组件
        route.component = loadView(route.component)
      }
    }
    // 注释：如果路由有子路由且子路由数组不为空
    if (route.children != null && route.children && route.children.length) {
      // 注释：递归过滤子路由
      route.children = filterAsyncRouter(route.children, route, type)
    } else {
      // 注释：如果没有子路由
      // 顶级页面菜单（顶层无子路由、非 Layout/ParentView/InnerLink 组件）：自动包裹 Layout，避免全屏渲染丢失侧边栏
      if (!lastRouter && route.component && route.component !== Layout && route.component !== ParentView && route.component !== InnerLink) {
        // 注释：保存当前组件
        const pageComponent = route.component
        // 注释：保存当前元信息
        const pageMeta = route.meta
        // 注释：保存当前查询参数
        const pageQuery = route.query
        // 注释：确保路径以 / 开头
        route.path = route.path.startsWith('/') ? route.path : '/' + route.path
        // 注释：将当前路由的组件设置为 Layout
        route.component = Layout
        // 注释：将原来的组件作为子路由
        route.children = [
          {
            // 注释：子路由路径为空
            path: '',
            // 注释：子路由组件为原来的页面组件
            component: pageComponent,
            // 注释：子路由元信息
            meta: pageMeta,
            // 注释：子路由查询参数
            query: pageQuery
          }
        ]
        // 注释：删除 redirect 属性
        delete route['redirect']
      } else {
        // 注释：删除 children 和 redirect 属性
        delete route['children']
        delete route['redirect']
      }
    }
    // 修复：父路由与唯一子路由路径相同时，将父路由路径设为 /，避免 route.matched 中出现重复路径导致面包屑 duplicate key 和组件不渲染
    if (route.children && route.children.length === 1 && route.component === Layout) {
      const childPath = route.children[0].path;
      const parentAbsPath = route.path.startsWith('/') ? route.path : '/' + route.path;
      if (childPath && parentAbsPath !== '/' && (childPath === parentAbsPath || childPath === route.path)) {
        route.path = '/';
      }
    }
    // 注释：返回 true，保留该路由
    return true
  })
}

// 注释：定义过滤子路由的函数
function filterChildren(childrenMap, lastRouter = false) {
  // 注释：初始化子路由数组
  var children = []
  // 注释：遍历子路由映射
  childrenMap.forEach((el, index) => {
    // 注释：如果子路由还有子路由
    if (el.children && el.children.length) {
      // 注释：如果是 ParentView 且不是最后一个路由
      if (el.component === 'ParentView' && !lastRouter) {
        // 注释：遍历子路由的子路由
        el.children.forEach(c => {
          // 注释：拼接路径
          c.path = el.path + '/' + c.path
          // 注释：如果还有子路由
          if (c.children && c.children.length) {
            // 注释：递归过滤子路由
            children = children.concat(filterChildren(c.children, c))
            return
          }
          // 注释：将子路由添加到数组
          children.push(c)
        })
        return
      }
    }
    // 注释：如果是最后一个路由
    if (lastRouter) {
      // 注释：拼接路径
      el.path = lastRouter.path + '/' + el.path
      // 注释：如果还有子路由
      if (el.children && el.children.length) {
        // 注释：递归过滤子路由
        children = children.concat(filterChildren(el.children, el))
        return
      }
    }
    // 注释：将子路由添加到数组
    children = children.concat(el)
  })
  // 注释：返回过滤后的子路由数组
  return children
}

// 注释：定义过滤动态路由的函数，验证是否具备权限
// 动态路由遍历，验证是否具备权限
export function filterDynamicRoutes(routes) {
  // 注释：初始化结果数组
  const res = []
  // 注释：遍历路由数组
  routes.forEach(route => {
    // 注释：如果路由有权限要求
    if (route.permissions) {
      // 注释：验证用户是否具备任一权限
      if (auth.hasPermiOr(route.permissions)) {
        // 注释：如果具备权限，添加到结果数组
        res.push(route)
      }
    } else if (route.roles) {
      // 注释：如果路由有角色要求
      // 注释：验证用户是否具备任一角色
      if (auth.hasRoleOr(route.roles)) {
        // 注释：如果具备角色，添加到结果数组
        res.push(route)
      }
    }
  })
  // 注释：返回过滤后的路由数组
  return res
}

// 注释：定义加载视图组件的函数
export const loadView = (view) => {
  // 注释：初始化结果变量
  let res;
  // 注释：遍历所有模块路径
  for (const path in modules) {
    // 注释：提取视图路径（去掉 'views/' 前缀和 '.vue' 后缀）
    const dir = path.split('views/')[1].split('.vue')[0];
    // 注释：如果路径匹配
    if (dir === view) {
      // 注释：设置懒加载函数
      res = () => modules[path]();
    }
  }
  // 注释：返回结果
  return res;
}

// 注释：导出权限 store 作为默认导出
export default usePermissionStore
