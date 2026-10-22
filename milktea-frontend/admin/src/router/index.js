import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getUser } from '../utils/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { title: '登录' } },
  {
    path: '/admin',
    component: () => import('../layout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '工作台' } },
      { path: 'product', name: 'Product', component: () => import('../views/admin/Product.vue'), meta: { title: '商品管理' } },
      { path: 'category', name: 'Category', component: () => import('../views/admin/Category.vue'), meta: { title: '分类管理' } },
      { path: 'order', name: 'Order', component: () => import('../views/admin/Order.vue'), meta: { title: '订单管理' } },
      { path: 'coupon', name: 'Coupon', component: () => import('../views/admin/Coupon.vue'), meta: { title: '优惠券' } },
      { path: 'weekspecial', name: 'WeekSpecial', component: () => import('../views/admin/WeekSpecial.vue'), meta: { title: '每周9.9活动' } },
      { path: 'material', name: 'Material', component: () => import('../views/admin/Material.vue'), meta: { title: '库存管理' } },
      { path: 'statistics', name: 'Statistics', component: () => import('../views/admin/Statistics.vue'), meta: { title: '数据统计' } },
      { path: 'system', name: 'System', component: () => import('../views/admin/System.vue'), meta: { title: '系统管理' } }
    ]
  },
  { path: '/', redirect: '/login' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 奶茶管理系统` : '奶茶管理系统'
  if (to.path === '/login') {
    next()
    return
  }
  if (!getToken()) {
    next('/login')
    return
  }
  const user = getUser()
  // 顾客只能进顾客端,员工只能进管理端
  if (to.path.startsWith('/client') && user && user.userType === 1) {
    next('/admin/dashboard')
    return
  }
  next()
})

export default router
