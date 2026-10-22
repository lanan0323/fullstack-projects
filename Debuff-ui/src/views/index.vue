<template>
  <!-- 页面主容器 -->
  <div class="app-container dash-page">
    <!-- 欢迎区 -->
    <!-- 顶部欢迎区域，包含标题和进入前台按钮 -->
    <div class="dash-head">
      <!-- 标题文字容器 -->
      <div>
        <!-- 管理平台主标题 -->
        <div class="dash-title">Debuff 管理平台</div>
        <!-- 管理平台副标题 -->
        <div class="dash-sub">饰品交易后台 · 运营数据总览</div>
      </div>
      <!-- 进入前台界面按钮 -->
      <el-button type="primary" icon="ShoppingBag" size="large" @click="goFront">进入前台界面</el-button>
    </div>

    <!-- 统计卡 -->
    <!-- 统计卡片行，使用栅格布局 -->
    <el-row :gutter="16">
      <!-- 遍历cards数组渲染统计卡片，响应式列宽 -->
      <el-col :xs="12" :sm="12" :md="6" v-for="card in cards" :key="card.label">
        <!-- 单个统计卡片容器 -->
        <div class="stat-card">
          <!-- 统计图标区域，动态设置背景色和文字颜色 -->
          <div class="stat-icon" :style="{ background: card.bg, color: card.color }">
            <!-- 动态图标组件 -->
            <el-icon><component :is="card.icon" /></el-icon>
          </div>
          <!-- 统计信息区域 -->
          <div class="stat-info">
            <!-- 统计数值 -->
            <div class="stat-num">{{ card.value }}</div>
            <!-- 统计标签 -->
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <!-- 快捷入口区域标题 -->
    <div class="sec-title">快捷入口</div>
    <!-- 快捷入口栅格布局 -->
    <el-row :gutter="16">
      <!-- 遍历quickLinks数组渲染快捷入口卡片 -->
      <el-col :xs="12" :sm="8" :md="4" v-for="q in quickLinks" :key="q.path">
        <!-- 快捷入口卡片，点击跳转到对应路径 -->
        <div class="quick-card" @click="router.push(q.path)">
          <!-- 快捷入口图标，动态设置颜色 -->
          <div class="quick-icon" :style="{ color: q.color }">
            <!-- 动态图标组件 -->
            <el-icon><component :is="q.icon" /></el-icon>
          </div>
          <!-- 快捷入口名称 -->
          <div class="quick-name">{{ q.name }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 最近订单 -->
    <!-- 最近订单区域标题 -->
    <div class="sec-title">最近订单</div>
    <!-- 最近订单卡片容器 -->
    <el-card shadow="never" class="recent-card">
      <!-- 订单数据表格，支持加载状态和空数据提示 -->
      <el-table v-loading="orderLoading" :data="recentOrders" empty-text="暂无订单" stripe>
        <!-- 订单号列 -->
        <el-table-column label="订单号" prop="orderNo" min-width="180" show-overflow-tooltip />
        <!-- 买家名称列 -->
        <el-table-column label="买家" prop="buyerName" width="110" />
        <!-- 卖家名称列 -->
        <el-table-column label="卖家" prop="sellerName" width="110" />
        <!-- 金额列，右对齐 -->
        <el-table-column label="金额" align="right" width="120">
          <!-- 自定义金额显示模板 -->
          <template #default="scope">
            <!-- 格式化显示金额，带人民币符号 -->
            <span class="price">¥{{ formatMoney(scope.row.price) }}</span>
          </template>
        </el-table-column>
        <!-- 订单状态列，居中对齐 -->
        <el-table-column label="状态" align="center" width="100">
          <!-- 自定义状态显示模板 -->
          <template #default="scope">
            <!-- 使用标签组件显示订单状态，动态设置类型 -->
            <el-tag :type="orderStatusMap[scope.row.status]?.type || 'info'" size="small">
              <!-- 显示状态标签文字 -->
              {{ orderStatusMap[scope.row.status]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 成交时间列，居中对齐 -->
        <el-table-column label="成交时间" prop="payTime" width="170" align="center" />
      </el-table>
      <!-- 查看全部订单链接，订单总数超过5条时显示 -->
      <div class="more-link" v-if="orderTotal > 5">
        <!-- 跳转到订单管理页面的链接 -->
        <el-link type="primary" @click="router.push('/admin/order')">查看全部订单</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
// 导入Vue组合式API：响应式引用、响应式对象、生命周期钩子
import { ref, reactive, onMounted } from 'vue'
// 导入vue-router的路由跳转方法
import { useRouter } from 'vue-router'
// 导入Element Plus图标组件
import { User, Box, Goods, Tickets, Bell, Wallet, Money } from '@element-plus/icons-vue'
// 导入用户列表API方法
import { listUser } from '@/api/system/user'
// 导入饰品列表API方法
import { listSkin } from '@/api/admin/skin'
// 导入市场列表API方法
import { listMarket } from '@/api/market'
// 导入订单列表API方法
import { listOrder } from '@/api/trade/order'

// 获取路由实例
const router = useRouter()

// 定义订单状态映射表，数字对应状态标签和类型
const orderStatusMap = {
  // 状态0：待确认，warning类型标签
  0: { label: '待确认', type: 'warning' },
  // 状态1：已完成，success类型标签
  1: { label: '已完成', type: 'success' },
  // 状态2：已取消，info类型标签
  2: { label: '已取消', type: 'info' }
}

// 金额格式化函数
const formatMoney = (v) => {
  // 如果值为null或undefined，返回'0.00'
  if (v === null || v === undefined) return '0.00'
  // 将数值转为保留两位小数的字符串
  return Number(v).toFixed(2)
}

// 定义统计卡片数据，使用reactive保持响应式
const cards = reactive([
  // 注册用户统计卡片
  { label: '注册用户', value: 0, icon: User, color: '#4d9fff', bg: 'rgba(77,159,255,0.12)' },
  // 饰品库统计卡片
  { label: '饰品库', value: 0, icon: Box, color: '#3ddc97', bg: 'rgba(61,220,151,0.12)' },
  // 在售饰品统计卡片
  { label: '在售饰品', value: 0, icon: Goods, color: '#ffb454', bg: 'rgba(255,180,84,0.12)' },
  // 交易订单统计卡片
  { label: '交易订单', value: 0, icon: Tickets, color: '#ff6b5e', bg: 'rgba(255,107,94,0.12)' }
])

// 定义快捷入口数据数组
const quickLinks = [
  // 订单管理入口
  { name: '订单管理', path: '/admin/order', icon: Tickets, color: '#ff6b5e' },
  // 饰品管理入口
  { name: '饰品管理', path: '/admin/skin', icon: Goods, color: '#4d9fff' },
  // 用户管理入口
  { name: '用户管理', path: '/system/user', icon: User, color: '#3ddc97' },
  // 钱包管理入口
  { name: '钱包管理', path: '/admin/wallet', icon: Wallet, color: '#ffb454' },
  // 公告管理入口
  { name: '公告管理', path: '/admin/notice', icon: Bell, color: '#b98cff' },
  // 数据看板入口
  { name: '数据看板', path: '/admin/dashboard', icon: Money, color: '#4dd0e1' }
]

// 订单表格加载状态
const orderLoading = ref(false)
// 最近订单数据数组
const recentOrders = ref([])
// 订单总数
const orderTotal = ref(0)

// 加载统计数据的方法
const loadStats = async () => {
  try {
    // 并发请求用户、饰品、市场、订单数据
    const [users, skins, market, orders] = await Promise.all([
      // 获取用户总数（只取第1页1条以获取total）
      listUser({ pageNum: 1, pageSize: 1 }),
      // 获取饰品总数
      listSkin({ pageNum: 1, pageSize: 1 }),
      // 获取市场在售总数
      listMarket({ pageNum: 1, pageSize: 1 }),
      // 获取最近5条订单
      listOrder({ pageNum: 1, pageSize: 5 })
    ])
    // 将用户总数赋值到统计卡片
    cards[0].value = users.total || 0
    // 将饰品总数赋值到统计卡片
    cards[1].value = skins.total || 0
    // 将市场在售总数赋值到统计卡片
    cards[2].value = market.total || 0
    // 将订单总数赋值到统计卡片
    cards[3].value = orders.total || 0
    // 将订单列表赋值到最近订单数据
    recentOrders.value = orders.rows || []
    // 将订单总数赋值到orderTotal
    orderTotal.value = orders.total || 0
  } catch (e) {
    // 统计加载失败不阻塞页面
  }
}

// 跳转到前台市场页面
const goFront = () => {
  // 使用路由跳转到市场页面
  router.push('/market')
}

// 组件挂载时加载统计数据
onMounted(loadStats)
</script>

<style scoped lang="scss">
/* 仪表盘页面样式 */
/* 页面主容器，限制最大宽度 */
.dash-page {
  /* 最大宽度100% */
  max-width: 100%;
}
/* 顶部欢迎区域样式 */
.dash-head {
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 两端对齐 */
  justify-content: space-between;
  /* 底部外边距20px */
  margin-bottom: 20px;
}
/* 主标题样式 */
.dash-title {
  /* 字体大小22px */
  font-size: 22px;
  /* 字体加粗 */
  font-weight: 700;
  /* 使用主题主要文字颜色 */
  color: var(--el-text-color-primary);
}
/* 副标题样式 */
.dash-sub {
  /* 顶部外边距4px */
  margin-top: 4px;
  /* 字体大小13px */
  font-size: 13px;
  /* 使用主题次要文字颜色 */
  color: var(--el-text-color-secondary);
}
/* 统计卡片样式 */
.stat-card {
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 子元素间距14px */
  gap: 14px;
  /* 使用主题背景色 */
  background: var(--el-bg-color);
  /* 使用主题浅色边框 */
  border: 1px solid var(--el-border-color-light);
  /* 圆角10px */
  border-radius: 10px;
  /* 内边距18px */
  padding: 18px;
  /* 底部外边距16px */
  margin-bottom: 16px;
}
/* 统计图标样式 */
.stat-icon {
  /* 宽度46px */
  width: 46px;
  /* 高度46px */
  height: 46px;
  /* 圆角10px */
  border-radius: 10px;
  /* 弹性布局居中图标 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 水平居中 */
  justify-content: center;
  /* 图标字体大小22px */
  font-size: 22px;
  /* 不允许收缩 */
  flex-shrink: 0;
}
/* 统计数值样式 */
.stat-num {
  /* 字体大小22px */
  font-size: 22px;
  /* 字体加粗 */
  font-weight: 700;
  /* 使用主题主要文字颜色 */
  color: var(--el-text-color-primary);
  /* 行高1.2 */
  line-height: 1.2;
  /* 使用等宽数字格式 */
  font-variant-numeric: tabular-nums;
}
/* 统计标签样式 */
.stat-label {
  /* 字体大小12px */
  font-size: 12px;
  /* 使用主题次要文字颜色 */
  color: var(--el-text-color-secondary);
  /* 顶部外边距2px */
  margin-top: 2px;
}
/* 区块标题样式 */
.sec-title {
  /* 字体大小15px */
  font-size: 15px;
  /* 字体加粗 */
  font-weight: 700;
  /* 使用主题主要文字颜色 */
  color: var(--el-text-color-primary);
  /* 外边距 */
  margin: 8px 0 14px;
}
/* 快捷入口卡片样式 */
.quick-card {
  /* 使用主题背景色 */
  background: var(--el-bg-color);
  /* 使用主题浅色边框 */
  border: 1px solid var(--el-border-color-light);
  /* 圆角10px */
  border-radius: 10px;
  /* 内边距 */
  padding: 18px 12px;
  /* 文字居中 */
  text-align: center;
  /* 鼠标手型 */
  cursor: pointer;
  /* 底部外边距16px */
  margin-bottom: 16px;
  /* 过渡动画 */
  transition: all 0.18s;
  /* 悬浮状态样式 */
  &:hover {
    /* 边框变为主题主色 */
    border-color: var(--el-color-primary);
    /* 向上偏移2px */
    transform: translateY(-2px);
  }
}
/* 快捷入口图标样式 */
.quick-icon {
  /* 字体大小24px */
  font-size: 24px;
}
/* 快捷入口名称样式 */
.quick-name {
  /* 顶部外边距8px */
  margin-top: 8px;
  /* 字体大小13px */
  font-size: 13px;
  /* 使用主题主要文字颜色 */
  color: var(--el-text-color-primary);
}
/* 最近订单卡片样式 */
.recent-card {
  /* 覆盖卡片背景色为主题背景色 */
  --el-card-bg-color: var(--el-bg-color);
  /* 覆盖卡片边框色为主题边框色 */
  --el-card-border-color: var(--el-border-color-light);
}
/* 金额文字样式 */
.price {
  /* 使用主题主要文字颜色 */
  color: var(--el-text-color-primary);
  /* 字体加粗 */
  font-weight: 600;
  /* 使用等宽数字格式 */
  font-variant-numeric: tabular-nums;
}
/* 查看更多链接样式 */
.more-link {
  /* 顶部外边距12px */
  margin-top: 12px;
  /* 右对齐 */
  text-align: right;
}
</style>
