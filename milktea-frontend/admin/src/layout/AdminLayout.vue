<template>
  <el-container class="admin-layout">
    <el-aside width="210px" class="aside">
      <div class="logo">
        <svg viewBox="0 0 40 40" class="logo-badge" aria-hidden="true">
          <circle cx="20" cy="20" r="19" fill="rgba(255,255,255,.15)" />
          <circle cx="20" cy="20" r="15.5" fill="#fff" />
          <path d="M20.2 8.6 Q23.6 9.2 23.9 12.4 Q20.5 12 20.2 8.6 Z" fill="#4c9e50" />
          <path d="M11 16.8 H29 Q28.6 27.8 20 27.8 Q11.4 27.8 11 16.8 Z"
            fill="#e8f6ee" stroke="#3a9e63" stroke-width="1.5" stroke-linejoin="round" />
          <rect x="10.3" y="14.8" width="19.4" height="2.8" rx="1.4" fill="#3a9e63" />
          <path d="M13.6 20.8 Q16.8 19.5 20 20.8 T26.4 20.8" stroke="#8cc9ab" stroke-width="1.3" fill="none" stroke-linecap="round" />
          <path d="M15.2 23.6 Q17.6 22.5 20 23.6 T24.8 23.6" stroke="#8cc9ab" stroke-width="1.3" fill="none" stroke-linecap="round" opacity=".75" />
        </svg>
        <div class="logo-text">
          <span class="logo-name">茶千道</span>
          <span class="logo-sub">管理系统</span>
        </div>
      </div>
      <el-menu :default-active="$route.path" router background-color="transparent" text-color="#c3d9cb" active-text-color="#fff">
        <el-menu-item index="/admin/dashboard"><el-icon><Odometer /></el-icon>工作台</el-menu-item>
        <el-menu-item index="/admin/product"><el-icon><Goods /></el-icon>商品管理</el-menu-item>
        <el-menu-item index="/admin/category"><el-icon><Menu /></el-icon>分类管理</el-menu-item>
        <el-menu-item index="/admin/order"><el-icon><List /></el-icon>订单管理</el-menu-item>
        <el-menu-item index="/admin/coupon"><el-icon><Ticket /></el-icon>优惠券</el-menu-item>
        <el-menu-item index="/admin/weekspecial"><el-icon><Discount /></el-icon>每周9.9活动</el-menu-item>
        <el-menu-item index="/admin/material"><el-icon><Box /></el-icon>库存管理</el-menu-item>
        <el-menu-item index="/admin/statistics"><el-icon><TrendCharts /></el-icon>数据统计</el-menu-item>
        <el-menu-item index="/admin/system"><el-icon><Setting /></el-icon>系统管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="header-right">
          <span class="welcome">{{ user?.nickname || user?.username || '员工' }}</span>
          <el-button type="danger" link @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { getUser, removeToken } from '../utils/auth'

const router = useRouter()
const user = computed(() => getUser())

const logout = () => {
  removeToken()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { height: 100vh; }
.aside { background: linear-gradient(180deg, #14532f, #1f6b45); }
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,.08);
}
.logo-badge { width: 36px; height: 36px; flex-shrink: 0; }
.logo-text { display: flex; flex-direction: column; line-height: 1.2; }
.logo-name { color: #fff; font-size: 17px; font-weight: bold; letter-spacing: 3px; }
.logo-sub { color: rgba(255,255,255,.55); font-size: 10px; letter-spacing: 2px; }
.aside :deep(.el-menu) { border-right: none; background: transparent; }
.aside :deep(.el-menu-item.is-active) { background: rgba(255,255,255,.12); }
.header { background: #fff; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #e8e8e8; }
.header-right { display: flex; align-items: center; gap: 12px; }
.welcome { color: #606266; font-size: 14px; }
.main { background: #f4f9f6; }
</style>
