<template>
  <!-- C端用户界面布局容器 -->
  <div class="user-layout">
    <!-- 顶部导航栏 -->
    <header class="u-header">
      <div class="u-header-inner">
        <!-- 品牌Logo区域，点击跳转市场页 -->
        <button class="u-brand" type="button" aria-label="返回市场" @click="router.push('/market')">
          <img class="u-brand-mark" src="/favicon.png" alt="DEBUFF" />
          <span class="u-brand-copy">
            <strong>DEBUFF</strong>
            <small>CS2 饰品交易</small>
          </span>
        </button>

        <!-- C端主导航菜单 -->
        <nav class="u-nav" aria-label="用户端导航">
          <!-- 遍历导航项生成导航按钮 -->
          <button
            v-for="item in navItems"
            :key="item.path"
            type="button"
            class="u-nav-item"
            :class="{ active: isActive(item.path) }"
            @click="router.push(item.path)"
          >
            <!-- 导航图标 -->
            <el-icon><component :is="item.icon" /></el-icon>
            <!-- 导航文字 -->
            <span>{{ item.title }}</span>
          </button>
        </nav>

        <!-- 右侧操作区：钱包和用户下拉菜单 -->
        <div class="u-header-actions">
          <!-- 钱包快捷入口 -->
          <button class="u-balance" type="button" @click="router.push('/wallet')">
            <el-icon><Wallet /></el-icon>
            <span>钱包</span>
          </button>
          <!-- 用户头像下拉菜单 -->
          <el-dropdown trigger="click" @command="handleCommand" popper-class="u-dropdown">
            <button class="u-user-info" type="button">
              <!-- 用户头像 -->
              <el-avatar :size="30" :src="userStore.avatar" class="u-avatar" />
              <!-- 用户名 -->
              <span class="u-name">{{ userStore.name || '我的账号' }}</span>
              <!-- 下拉箭头图标 -->
              <el-icon class="u-caret"><ArrowDown /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <!-- 管理员可见的后台管理入口 -->
                <el-dropdown-item v-if="isAdmin" command="admin">
                  <el-icon><Setting /></el-icon>后台管理
                </el-dropdown-item>
                <!-- 个人中心入口 -->
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <!-- 退出登录（带分割线） -->
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 页面主内容区，由子路由渲染 -->
    <main class="u-main">
      <router-view />
    </main>

    <!-- 页脚信息 -->
    <footer class="u-footer">
      <span>DEBUFF 饰品交易平台</span>
      <span>模拟交易环境 · 仅供毕业设计演示</span>
    </footer>
  </div>
</template>

<script setup name="UserLayout">
// 引入Vue生命周期钩子
import { computed, onBeforeUnmount, onMounted } from 'vue'
// 引入路由相关API
import { useRoute, useRouter } from 'vue-router'
// 引入Element Plus消息弹框（退出确认）
import { ElMessageBox } from 'element-plus'
// 引入导航所需的Element Plus图标
import {
  ArrowDown, Box, Setting, Shop, Star, SwitchButton, Tickets, User, Wallet
} from '@element-plus/icons-vue'
// 引入用户状态store
import useUserStore from '@/store/modules/user'

// 获取当前路由对象
const route = useRoute()
// 获取路由实例用于编程式导航
const router = useRouter()
// 获取用户store实例
const userStore = useUserStore()

// 判断当前用户是否为管理员角色
const isAdmin = computed(() => userStore.roles.includes('admin'))

// C端导航菜单配置项
const navItems = [
  { path: '/market', title: '市场', icon: Shop },
  { path: '/inventory', title: '我的库存', icon: Box },
  { path: '/trade', title: '我的交易', icon: Tickets },
  { path: '/favorites', title: '收藏', icon: Star },
  { path: '/wallet', title: '我的钱包', icon: Wallet },
  { path: '/profile', title: '个人中心', icon: User }
]

// 判断导航项是否为当前激活状态（精确匹配或子路径匹配）
const isActive = (path) => route.path === path || route.path.startsWith(path + '/')

// 组件挂载时给body添加user-surface类以切换为C端浅色主题
onMounted(() => document.body.classList.add('user-surface'))
// 组件卸载时移除body的user-surface类
onBeforeUnmount(() => document.body.classList.remove('user-surface'))

// 处理下拉菜单命令的回调函数
const handleCommand = (cmd) => {
  // 跳转到后台管理首页
  if (cmd === 'admin') {
    router.push('/index')
  // 跳转到个人中心
  } else if (cmd === 'profile') {
    router.push('/profile')
  // 退出登录，弹出确认框
  } else if (cmd === 'logout') {
    ElMessageBox.confirm('确定退出登录吗？', '提示', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 确认后执行登出并跳转到登录页
      userStore.logOut().then(() => {
        router.push('/login')
      })
    }).catch(() => {})
  }
}
</script>

<style scoped>
.user-layout {
  --user-ink: #1f2d3d;
  --user-muted: #8290a3;
  --user-border: #e3e8ef;
  --user-primary: #2f80ed;
  min-height: 100vh;
  background: #f4f6f8;
  color: var(--user-ink);
}
.u-header {
  position: sticky;
  top: 0;
  z-index: 90;
  height: 68px;
  background: #172232;
  color: #fff;
  box-shadow: 0 1px 0 rgba(16, 31, 48, 0.22);
}
.u-header-inner {
  max-width: 1360px;
  height: 100%;
  margin: 0 auto;
  padding: 0 28px;
  display: flex;
  align-items: center;
  gap: 34px;
}
.u-brand,
.u-user-info,
.u-balance,
.u-nav-item {
  border: 0;
  background: transparent;
  cursor: pointer;
  font: inherit;
}
.u-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
  padding: 0;
  flex-shrink: 0;
}
.u-brand-mark {
  width: 120px;
  height: 36px;
  object-fit: contain;
  border-radius: 4px;
}
.u-brand-copy {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  line-height: 1.1;
}
.u-brand-copy strong {
  font-size: 17px;
  letter-spacing: 1.5px;
}
.u-brand-copy small {
  margin-top: 4px;
  color: #9eacbc;
  font-size: 10px;
}
.u-nav {
  height: 100%;
  display: flex;
  align-items: stretch;
  gap: 8px;
}
.u-nav-item {
  position: relative;
  min-width: 76px;
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  color: #aeb9c7;
  font-size: 14px;
  transition: color 0.18s, background 0.18s;
}
.u-nav-item:hover,
.u-nav-item.active {
  color: #fff;
  background: rgba(255, 255, 255, 0.06);
}
.u-nav-item.active::after {
  content: '';
  position: absolute;
  right: 18px;
  bottom: 0;
  left: 18px;
  height: 3px;
  background: #4d9fff;
}
.u-header-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 14px;
}
.u-balance {
  padding: 9px 12px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #d8e2ed;
  font-size: 13px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 5px;
}
.u-balance:hover { border-color: #4d9fff; color: #fff; }
.u-user-info {
  padding: 5px 0 5px 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
}
.u-avatar { background: #32445a; }
.u-name { max-width: 110px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 13px; }
.u-caret { color: #9eacbc; font-size: 12px; }
.u-main { min-height: calc(100vh - 116px); padding-bottom: 36px; }
.u-footer {
  max-width: 1360px;
  margin: 0 auto;
  padding: 18px 28px 24px;
  display: flex;
  justify-content: space-between;
  color: #9aa6b5;
  font-size: 12px;
}
@media (max-width: 860px) {
  .u-header-inner { padding: 0 16px; gap: 14px; }
  .u-brand-copy, .u-name { display: none; }
  .u-brand-mark { width: 100px; height: 30px; }
  .u-nav { flex: 1; gap: 0; }
  .u-nav-item { min-width: 0; flex: 1; padding: 0 5px; font-size: 12px; }
  .u-nav-item span { display: none; }
  .u-header-actions { gap: 4px; }
  .u-balance span { display: none; }
  .u-footer { padding: 16px; }
}
</style>

<style>
body.user-surface {
  background: #f4f6f8 !important;
  color: #1f2d3d !important;
}
body.user-surface #app { background: #f4f6f8; }
.u-dropdown {
  --el-bg-color-overlay: #ffffff;
  --el-border-color-light: #e3e8ef;
  --el-text-color-primary: #1f2d3d;
  --el-text-color-regular: #5d6b7c;
  background: #ffffff !important;
  border: 1px solid #e3e8ef !important;
}
.u-dropdown .el-dropdown-menu__item { color: #5d6b7c !important; }
.u-dropdown .el-dropdown-menu__item:not(.is-disabled):hover {
  background: #f0f5fb !important;
  color: #2f80ed !important;
}
.u-dropdown .el-dropdown-menu__item .el-icon { margin-right: 6px; }
.u-dropdown .el-dropdown-menu__item--divided { border-top: 1px solid #e3e8ef; }

body.user-surface .market-page,
body.user-surface .inventory-page,
body.user-surface .orders-page,
body.user-surface .wallet-page {
  background: #f4f6f8 !important;
  color: #1f2d3d !important;
  --bg: #f4f6f8;
  --card: #ffffff;
  --card-hover: #f7faff;
  --border: #e3e8ef;
  --text: #1f2d3d;
  --muted: #8290a3;
  --price: #e0554f;
  --green: #2d9a6a;
  --red: #d95550;
  --el-bg-color: #ffffff;
  --el-bg-color-overlay: #ffffff;
  --el-fill-color-blank: #ffffff;
  --el-border-color: #e3e8ef;
  --el-border-color-light: #edf0f4;
  --el-text-color-primary: #1f2d3d;
  --el-text-color-regular: #526275;
  --el-text-color-secondary: #8290a3;
  --el-text-color-placeholder: #a3afbd;
}
body.user-surface .market-page .filter-panel,
body.user-surface .inventory-page .filter-panel,
body.user-surface .orders-page .filter-panel,
body.user-surface .wallet-page .logs-panel,
body.user-surface .inventory-page .table-wrap,
body.user-surface .inventory-page .my-buy-wrap,
body.user-surface .orders-page .table-wrap,
body.user-surface .wallet-page .stat-card {
  background: #fff !important;
  border-color: #e3e8ef !important;
  box-shadow: 0 1px 2px rgba(31, 45, 61, 0.03);
}
body.user-surface .market-page .skin-card {
  background: #fff !important;
  border-color: #e3e8ef !important;
  box-shadow: 0 2px 8px rgba(31, 45, 61, 0.04);
}
body.user-surface .market-page .skin-card:hover {
  border-color: #a9c9f4 !important;
  box-shadow: 0 8px 22px rgba(31, 45, 61, 0.10) !important;
}
body.user-surface .market-page .card-img,
body.user-surface .inventory-page .cell-img,
body.user-surface .market-page .detail-img {
  background: #f1f4f7 !important;
}
body.user-surface .market-page .btn-primary,
body.user-surface .inventory-page .btn-primary,
body.user-surface .orders-page .btn-primary {
  background: #2f80ed !important;
  color: #fff !important;
}
body.user-surface .market-page .btn-ghost,
body.user-surface .inventory-page .btn-ghost,
body.user-surface .orders-page .btn-ghost {
  color: #526275 !important;
  border-color: #d6dee8 !important;
}
body.user-surface .market-page .el-input__wrapper,
body.user-surface .market-page .el-select .el-input__wrapper,
body.user-surface .market-page .el-input-number .el-input__wrapper,
body.user-surface .inventory-page .el-input__wrapper,
body.user-surface .inventory-page .el-select .el-input__wrapper,
body.user-surface .orders-page .el-select .el-input__wrapper,
body.user-surface .wallet-page .el-input__wrapper {
  background: #fff !important;
  box-shadow: 0 0 0 1px #dce4ed inset !important;
}
body.user-surface .market-page .el-input__inner,
body.user-surface .market-page .el-select__selected-item,
body.user-surface .inventory-page .el-input__inner,
body.user-surface .inventory-page .el-select__selected-item,
body.user-surface .orders-page .el-select__selected-item,
body.user-surface .wallet-page .el-input__inner {
  color: #1f2d3d !important;
}
body.user-surface .market-page .el-table,
body.user-surface .inventory-page .el-table,
body.user-surface .orders-page .el-table,
body.user-surface .wallet-page .el-table {
  --el-table-bg-color: #fff;
  --el-table-tr-bg-color: #fff;
  --el-table-header-bg-color: #f7f9fb;
  --el-table-header-text-color: #8290a3;
  --el-table-border-color: #edf0f4;
  --el-table-text-color: #526275;
  --el-table-row-hover-bg-color: #f7faff;
  background: #fff !important;
}
body.user-surface .market-page .el-table th.el-table__cell,
body.user-surface .inventory-page .el-table th.el-table__cell,
body.user-surface .orders-page .el-table th.el-table__cell,
body.user-surface .wallet-page .el-table th.el-table__cell {
  background: #f7f9fb !important;
  color: #8290a3 !important;
  border-bottom-color: #edf0f4 !important;
}
body.user-surface .market-page .el-table td.el-table__cell,
body.user-surface .inventory-page .el-table td.el-table__cell,
body.user-surface .orders-page .el-table td.el-table__cell,
body.user-surface .wallet-page .el-table td.el-table__cell {
  border-bottom-color: #edf0f4 !important;
}
body.user-surface .debuff-dialog {
  background: #fff !important;
  border-color: #e3e8ef !important;
}
body.user-surface .debuff-dialog .el-dialog__title,
body.user-surface .debuff-dialog .el-descriptions__content,
body.user-surface .debuff-dialog .el-form-item__label {
  color: #1f2d3d !important;
}
body.user-surface .debuff-dialog .el-input__wrapper,
body.user-surface .debuff-dialog .el-textarea__inner {
  background: #fff !important;
  box-shadow: 0 0 0 1px #dce4ed inset !important;
  color: #1f2d3d !important;
}
body.user-surface .debuff-overlay { background: rgba(31, 45, 61, 0.34) !important; }
</style>
