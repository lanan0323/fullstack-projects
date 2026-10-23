// 从 vue-router 导入创建 Web 历史记录和路由器的方法
import { createWebHistory, createRouter } from 'vue-router'
// 注释：导入布局组件
/* Layout */
// 从 @/layout 导入后台管理布局组件
import Layout from '@/layout'
// 注释：导入用户端布局组件
/* 用户端布局 */
// 从 @/layout/UserLayout 导入用户端布局组件
import UserLayout from '@/layout/UserLayout'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 注释：定义公共路由（不需要权限即可访问的路由）
// 公共路由
export const constantRoutes = [
  // 注释：重定向路由配置
  {
    // 路由路径为 /redirect
    path: '/redirect',
    // 使用后台管理布局组件
    component: Layout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 子路由配置
    children: [
      {
        // 匹配任意路径参数
        path: '/redirect/:path(.*)',
        // 懒加载重定向页面组件
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  // 注释：登录页面路由
  {
    // 路由路径为 /login
    path: '/login',
    // 懒加载登录页面组件
    component: () => import('@/views/login'),
    // 隐藏该路由，不在侧边栏显示
    hidden: true
  },
  // 注释：注册页面路由
  {
    // 路由路径为 /register
    path: '/register',
    // 懒加载注册页面组件
    component: () => import('@/views/register'),
    // 隐藏该路由，不在侧边栏显示
    hidden: true
  },
  // 注释：市场页面路由（用户端）
  {
    // 路由路径为 /market
    path: '/market',
    // 使用用户端布局组件
    component: UserLayout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 子路由配置
    children: [
      {
        // 空路径，匹配 /market
        path: '',
        // 懒加载市场页面组件
        component: () => import('@/views/market/index'),
        // 路由名称为 UserMarket
        name: 'UserMarket',
        // 路由元信息，设置标题为"市场"
        meta: { title: '市场' }
      }
    ]
  },
  // 注释：库存页面路由（用户端）
  {
    // 路由路径为 /inventory
    path: '/inventory',
    // 使用用户端布局组件
    component: UserLayout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 子路由配置
    children: [
      {
        // 空路径，匹配 /inventory
        path: '',
        // 懒加载库存页面组件
        component: () => import('@/views/inventory/index'),
        // 路由名称为 UserInventory
        name: 'UserInventory',
        // 路由元信息，设置标题为"我的库存"
        meta: { title: '我的库存' }
      }
    ]
  },
  // 注释：交易页面路由（用户端）
  {
    // 路由路径为 /trade
    path: '/trade',
    // 使用用户端布局组件
    component: UserLayout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 子路由配置
    children: [
      {
        // 空路径，匹配 /trade
        path: '',
        // 懒加载交易订单页面组件
        component: () => import('@/views/trade/orders/index'),
        // 路由名称为 UserTrade
        name: 'UserTrade',
        // 路由元信息，设置标题为"我的交易"
        meta: { title: '我的交易' }
      }
    ]
  },
  // 注释：收藏页面路由（用户端）
  {
    // 路由路径为 /favorites
    path: '/favorites',
    // 使用用户端布局组件
    component: UserLayout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 子路由配置
    children: [
      {
        // 空路径，匹配 /favorites
        path: '',
        // 懒加载收藏页面组件
        component: () => import('@/views/favorites/index'),
        // 路由名称为 UserFavorites
        name: 'UserFavorites',
        // 路由元信息，设置标题为"我的收藏"
        meta: { title: '我的收藏' }
      }
    ]
  },
  // 注释：钱包页面路由（用户端）
  {
    // 路由路径为 /wallet
    path: '/wallet',
    // 使用用户端布局组件
    component: UserLayout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 子路由配置
    children: [
      {
        // 空路径，匹配 /wallet
        path: '',
        // 懒加载钱包页面组件
        component: () => import('@/views/wallet/index'),
        // 路由名称为 UserWallet
        name: 'UserWallet',
        // 路由元信息，设置标题为"我的钱包"
        meta: { title: '我的钱包' }
      }
    ]
  },
  // 注释：个人中心页面路由（用户端）
  {
    // 路由路径为 /profile
    path: '/profile',
    // 使用用户端布局组件
    component: UserLayout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 子路由配置
    children: [
      {
        // 空路径，匹配 /profile
        path: '',
        // 懒加载个人中心页面组件
        component: () => import('@/views/profile/index'),
        // 路由名称为 UserProfile
        name: 'UserProfile',
        // 路由元信息，设置标题为"个人中心"
        meta: { title: '个人中心' }
      }
    ]
  },
  // 注释：404 页面路由（匹配所有未定义的路径）
  {
    // 匹配所有未定义的路径
    path: "/:pathMatch(.*)*",
    // 懒加载 404 错误页面组件
    component: () => import('@/views/error/404'),
    // 隐藏该路由，不在侧边栏显示
    hidden: true
  },
  // 注释：401 未授权页面路由
  {
    // 路由路径为 /401
    path: '/401',
    // 懒加载 401 错误页面组件
    component: () => import('@/views/error/401'),
    // 隐藏该路由，不在侧边栏显示
    hidden: true
  },
  // 注释：首页路由（根路径重定向到首页）
  {
    // 路由路径为空（根路径）
    path: '',
    // 使用后台管理布局组件
    component: Layout,
    // 重定向到 /index
    redirect: '/index',
    // 子路由配置
    children: [
      {
        // 路由路径为 /index
        path: '/index',
        // 懒加载首页组件
        component: () => import('@/views/index'),
        // 路由名称为 Index
        name: 'Index',
        // 路由元信息，设置标题为"首页"，图标为 dashboard，固定标签页
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  // 注释：用户个人资料路由（后台管理）
  {
    // 路由路径为 /user
    path: '/user',
    // 使用后台管理布局组件
    component: Layout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 不重定向
    redirect: 'noredirect',
    // 子路由配置
    children: [
      {
        // 路由路径为 profile
        path: 'profile',
        // 懒加载用户个人资料页面组件
        component: () => import('@/views/system/user/profile/index'),
        // 路由名称为 Profile
        name: 'Profile',
        // 路由元信息，设置标题为"个人中心"，图标为 user
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

// 注释：定义动态路由（基于用户权限动态加载的路由）
// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
  // 注释：用户角色分配路由
  {
    // 路由路径为 /system/user-auth
    path: '/system/user-auth',
    // 使用后台管理布局组件
    component: Layout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 需要 system:user:edit 权限才能访问
    permissions: ['system:user:edit'],
    // 子路由配置
    children: [
      {
        // 路由路径为 role/:userId，userId 必须是数字
        path: 'role/:userId(\\d+)',
        // 懒加载用户角色分配页面组件
        component: () => import('@/views/system/user/authRole'),
        // 路由名称为 AuthRole
        name: 'AuthRole',
        // 路由元信息，设置标题为"分配角色"，高亮 /system/user 菜单
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  // 注释：角色用户分配路由
  {
    // 路由路径为 /system/role-auth
    path: '/system/role-auth',
    // 使用后台管理布局组件
    component: Layout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 需要 system:role:edit 权限才能访问
    permissions: ['system:role:edit'],
    // 子路由配置
    children: [
      {
        // 路由路径为 user/:roleId，roleId 必须是数字
        path: 'user/:roleId(\\d+)',
        // 懒加载角色用户分配页面组件
        component: () => import('@/views/system/role/authUser'),
        // 路由名称为 AuthUser
        name: 'AuthUser',
        // 路由元信息，设置标题为"分配用户"，高亮 /system/role 菜单
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  // 注释：字典数据路由
  {
    // 路由路径为 /system/dict-data
    path: '/system/dict-data',
    // 使用后台管理布局组件
    component: Layout,
    // 隐藏该路由，不在侧边栏显示
    hidden: true,
    // 需要 system:dict:list 权限才能访问
    permissions: ['system:dict:list'],
    // 子路由配置
    children: [
      {
        // 路由路径为 index/:dictId，dictId 必须是数字
        path: 'index/:dictId(\\d+)',
        // 懒加载字典数据页面组件
        component: () => import('@/views/system/dict/data'),
        // 路由名称为 Data
        name: 'Data',
        // 路由元信息，设置标题为"字典数据"，高亮 /system/dict 菜单
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  }
]

// 注释：创建路由器实例
const router = createRouter({
  // 使用 Web 历史记录模式（HTML5 History API）
  history: createWebHistory(),
  // 使用公共路由配置
  routes: constantRoutes,
  // 滚动行为配置
  scrollBehavior(to, from, savedPosition) {
    // 如果有保存的滚动位置
    if (savedPosition) {
      // 返回保存的滚动位置
      return savedPosition
    } else {
      // 否则滚动到顶部
      return { top: 0 }
    }
  },
});

// 注释：导出路由器实例供其他模块使用
export default router;
