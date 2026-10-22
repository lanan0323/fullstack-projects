<template>
  <div class="orders-page">
    <van-nav-bar title="我的订单" />

    <!-- 状态 Tab -->
    <van-tabs v-model:active="activeStatus" sticky offset-top="46" @change="onTabChange">
      <van-tab title="全部" name="" />
      <van-tab title="待制作" name="1" />
      <van-tab title="制作中" name="2" />
      <van-tab title="已完成" name="3" />
      <van-tab title="已取消" name="4" />
    </van-tabs>

    <!-- 订单列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="loadOrders">
        <div class="order-card" v-for="order in orders" :key="order.id">
          <div class="order-header">
            <span class="order-no">订单号: {{ order.orderNo }}</span>
            <span class="order-status" :class="statusClass(order.status)">{{ statusText(order.status) }}</span>
          </div>
          <div class="order-items">
            <div class="item" v-for="item in order.details || []" :key="item.id">
              <div class="item-info">
                <span class="item-name">{{ item.productName }}</span>
                <span class="item-specs" v-if="item.specs">{{ item.specs }}</span>
                <span class="item-price">￥{{ item.price }} x{{ item.quantity }}</span>
              </div>
            </div>
          </div>
          <div class="order-footer">
            <span class="order-time">{{ (order.createTime || '').replace('T', ' ') }}</span>
            <span class="order-total">
              <em v-if="order.discountAmount > 0" class="order-discount">券已抵￥{{ order.discountAmount }}</em>
              实付￥{{ Number(order.totalAmount || 0).toFixed(2) }}
            </span>
          </div>
          <div class="order-actions" v-if="order.status === 1">
            <van-button size="small" plain type="danger" @click="onCancel(order.id)">取消订单</van-button>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { showToast, showConfirmDialog } from 'vant'
import { pageMyOrders, cancelOrder } from '../api/order'
import { getUser } from '../utils/auth'

const activeStatus = ref('')
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const orders = ref([])
const pageNum = ref(1)
const total = ref(0)

const statusText = (status) => {
  const map = { 1: '待制作', 2: '制作中', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const statusClass = (status) => {
  const map = { 1: 'pending', 2: 'cooking', 3: 'done', 4: 'cancel' }
  return map[status] || ''
}

const loadOrders = async () => {
  try {
    const user = getUser()
    const params = { pageNum: pageNum.value, pageSize: 10 }
    if (activeStatus.value) params.status = activeStatus.value
    const res = await pageMyOrders(user.id, params)
    const list = res.data.list || []
    total.value = res.data.total || 0
    if (pageNum.value === 1) {
      orders.value = list
    } else {
      orders.value.push(...list)
    }
    pageNum.value += 1
    finished.value = orders.value.length >= total.value
  } catch (e) {
    finished.value = true
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const onTabChange = () => {
  pageNum.value = 1
  total.value = 0
  orders.value = []
  finished.value = false
  loading.value = true
  loadOrders()
}

const onRefresh = () => {
  pageNum.value = 1
  total.value = 0
  finished.value = false
  loadOrders()
}

const onCancel = (id) => {
  showConfirmDialog({
    title: '提示',
    message: '确认取消该订单吗？'
  }).then(async () => {
    await cancelOrder(id)
    showToast('已取消')
    onRefresh()
  }).catch(() => {})
}
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background: var(--page-bg);
  padding-bottom: 100px;
}
.orders-page :deep(.van-nav-bar) {
  background: var(--brand-grad);
}
.orders-page :deep(.van-nav-bar__title) {
  color: #fff;
}
/* 状态Tab品牌色(vant组件级变量,:root覆盖不了) */
.orders-page :deep(.van-tabs) {
  --van-tabs-bottom-bar-color: var(--brand);
}
.orders-page :deep(.van-tabs__line) {
  width: 24px !important;
  height: 3px;
  border-radius: 3px;
  bottom: 8px;
  background: var(--brand);
}
.orders-page :deep(.van-tab--active .van-tab__text) {
  font-weight: 800;
  color: #2b2320;
}
.order-card {
  background: #fff;
  margin: 10px;
  border-radius: 14px;
  padding: 14px;
  box-shadow: 0 2px 8px rgba(30, 90, 55, 0.05);
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.order-no {
  font-size: 12px;
  color: #b09884;
}
.order-status {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 10px;
}
.order-status.pending { color: #e8890c; background: #fff4e5; }
.order-status.cooking { color: #2e6be6; background: #eaf1ff; }
.order-status.done { color: #22a35c; background: #e8f7ee; }
.order-status.cancel { color: #999; background: #f2f2f2; }
.order-items {
  padding: 4px 0 8px;
}
.item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #2b2320;
}
.item-price {
  font-size: 12px;
  color: #8a7a6c;
}
.item-specs {
  font-size: 11px;
  color: #b09884;
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #f5efe8;
  padding-top: 10px;
}
.order-time {
  font-size: 12px;
  color: #b09884;
}
.order-total {
  font-size: 16px;
  font-weight: 700;
  color: var(--price);
}
.order-discount {
  font-style: normal;
  font-size: 12px;
  font-weight: normal;
  color: #ff6a3d;
  margin-right: 6px;
}
.order-actions {
  margin-top: 10px;
  text-align: right;
}
</style>
