import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../utils/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { title: '登录' } },
  { path: '/address', name: 'Address', component: () => import('../views/Address.vue'), meta: { title: '收货地址' } },
  { path: '/wallet', name: 'Wallet', component: () => import('../views/Wallet.vue'), meta: { title: '我的钱包' } },
  { path: '/points', name: 'Points', component: () => import('../views/Points.vue'), meta: { title: '会员中心' } },
  { path: '/level', name: 'Level', component: () => import('../views/Level.vue'), meta: { title: '会员等级' } },
  {
    path: '/',
    component: () => import('../components/Tabbar.vue'),
    children: [
      { path: '', redirect: '/menu' },
      { path: 'menu', name: 'Menu', component: () => import('../views/Menu.vue'), meta: { title: '点单' } },
      { path: 'cart', name: 'Cart', component: () => import('../views/Cart.vue'), meta: { title: '结算' } },
      { path: 'orders', name: 'Orders', component: () => import('../views/Orders.vue'), meta: { title: '订单' } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { title: '我的' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  // 切页时回到顶部(浏览器前进/后退保留原位置), 否则上个页面滑过的距离会带到点单页
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 茶千道` : '茶千道'
  if (to.path === '/login') {
    next()
    return
  }
  if (!getToken()) {
    next('/login')
    return
  }
  next()
})

export default router
