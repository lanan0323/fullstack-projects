<template>
  <div class="profile-page">
    <!-- 头部用户信息 -->
    <div class="user-header">
      <div class="avatar-wrap" @click="fileInput && fileInput.click()">
        <van-image round width="64" height="64" :src="user?.avatar || FALLBACK_AVATAR" />
        <span class="avatar-edit"><van-icon name="photograph" /></span>
      </div>
      <input ref="fileInput" type="file" accept="image/*" hidden @change="onAvatarChange" />
      <div class="user-info">
        <span class="nickname" @click="openEditInfo">{{ user?.nickname || '奶茶爱好者' }} <van-icon name="arrow" /></span>
        <div class="member-badge" @click="router.push('/level')">
          <van-icon name="gem-o" />
          <span>{{ memberLevel }}</span>
          <van-icon name="arrow" class="badge-arrow" />
        </div>
      </div>
    </div>

    <!-- 统计数据 -->
    <div class="stats-row">
      <div class="stat-item" @click="router.push('/orders')">
        <span class="stat-num">{{ orderTotal }}</span>
        <span class="stat-label">历史订单</span>
      </div>
      <div class="stat-item" @click="openMyCoupons">
        <span class="stat-num">{{ myCouponList.length }}</span>
        <span class="stat-label">优惠券</span>
      </div>
      <div class="stat-item" @click="router.push('/points')">
        <span class="stat-num">{{ memberPoints }}</span>
        <span class="stat-label">积分</span>
      </div>
      <div class="stat-item" @click="router.push('/wallet')">
        <span class="stat-num">{{ Number(walletBalance).toFixed(2) }}</span>
        <span class="stat-label">余额(元)</span>
      </div>
    </div>

    <!-- 最近订单 -->
    <div class="section-card" v-if="recentOrder">
      <div class="card-title-row">
        <span class="card-title">最近订单</span>
        <span class="card-more" @click="router.push('/orders')">全部订单 ></span>
      </div>
      <div class="order-item" @click="router.push('/orders')">
        <div class="order-icon"><van-icon name="orders-o" size="22" /></div>
        <div class="order-info">
          <span class="order-name">{{ recentOrder.productName }}</span>
          <span class="order-time">{{ (recentOrder.createTime || '').replace('T', ' ') }}　订单号 {{ recentOrder.orderNo }}</span>
        </div>
        <div class="order-right">
          <span class="order-status" :class="'st' + recentOrder.status">{{ statusText(recentOrder.status) }}</span>
          <span class="order-amount">实付￥{{ Number(recentOrder.totalAmount || 0).toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- 功能网格 -->
    <div class="section-card">
      <div class="card-title">我的功能</div>
      <div class="func-grid">
        <div class="func-item" @click="router.push('/address')">
          <span class="func-icon brand"><van-icon name="location-o" size="20" /></span>
          <span>收货地址</span>
        </div>
        <div class="func-item" @click="showEditPwd = true">
          <span class="func-icon blue"><van-icon name="lock" size="20" /></span>
          <span>修改密码</span>
        </div>
        <div class="func-item" @click="openEditInfo">
          <span class="func-icon blue"><van-icon name="edit" size="20" /></span>
          <span>修改昵称</span>
        </div>
        <div class="func-item" @click="router.push('/points')">
          <span class="func-icon gold"><van-icon name="gem-o" size="20" /></span>
          <span>会员中心</span>
        </div>
      </div>
    </div>

    <!-- 退出按钮 -->
    <div style="margin: 24px 16px;">
      <van-button round block plain type="danger" @click="onLogout">退出登录</van-button>
    </div>

    <!-- 我的优惠券弹窗 -->
    <van-popup v-model:show="showCoupons" position="bottom" round :style="{ maxHeight: '70%' }">
      <div class="coupon-popup">
        <div class="card-title">我的优惠券</div>
        <div class="my-coupon" v-for="c in myCouponList" :key="c.id">
          <div class="mc-left">
            <span class="mc-value">{{ couponValue(c) }}</span>
            <span class="mc-min">{{ Number(c.minAmount) > 0 ? `满${c.minAmount}可用` : '无门槛' }}</span>
          </div>
          <div class="mc-right">
            <span class="mc-name">{{ c.name }}</span>
            <span class="mc-expire">有效期至 {{ (c.expireTime || '').slice(0, 10) }}</span>
          </div>
        </div>
        <van-empty v-if="!myCouponList.length" description="还没有优惠券，去点单页领取" :image-size="60" />
      </div>
    </van-popup>

    <!-- 修改昵称弹窗 -->
    <van-dialog v-model:show="showEditInfo" title="修改昵称" show-cancel-button @confirm="onSaveInfo">
      <van-field v-model="editForm.nickname" placeholder="请输入新昵称" style="margin: 12px;" />
    </van-dialog>

    <!-- 修改密码弹窗 -->
    <van-dialog v-model:show="showEditPwd" title="修改密码" show-cancel-button @confirm="onSavePwd">
      <div style="margin: 12px;">
        <van-field v-model="pwdForm.oldPassword" type="password" label="原密码" placeholder="请输入原密码" />
        <van-field v-model="pwdForm.newPassword" type="password" label="新密码" placeholder="请输入新密码" />
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { getUser, removeToken, setUser } from '../utils/auth'
import { updatePassword, updateUserInfo, myCoupons, uploadAvatar } from '../api/user'
import { pageMyOrders } from '../api/order'
import { getWallet } from '../api/wallet'
import { getPoints } from '../api/points'

const router = useRouter()
const user = ref(getUser())   // ref保证保存昵称后头部实时刷新

// 无头像占位图(内置SVG, 不依赖外网CDN)
const FALLBACK_AVATAR =
  "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Crect width='100' height='100' fill='%23f6ede3'/%3E%3Ccircle cx='50' cy='38' r='16' fill='%23c68e5e'/%3E%3Cpath d='M20 88a30 30 0 0 1 60 0z' fill='%23c68e5e'/%3E%3C/svg%3E"

const showEditInfo = ref(false)
const showEditPwd = ref(false)
const editForm = ref({ nickname: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const recentOrder = ref(null)
const orderTotal = ref(0)
const walletBalance = ref(0)
const memberPoints = ref(0)

const memberLevel = computed(() => {
  if (user.value?.userType === 1) return '员工账号'
  const p = memberPoints.value
  if (p >= 5000) return '钻石会员'
  if (p >= 2000) return '黄金会员'
  if (p >= 500) return '白银会员'
  return '普通会员'
})

// ==================== 头像上传 ====================
const fileInput = ref(null)

const onAvatarChange = async e => {
  const file = e.target.files[0]
  e.target.value = '' // 允许重复选择同一张
  if (!file) return
  if (!file.type.startsWith('image/')) return showToast('请选择图片文件')
  // canvas压缩到256px JPEG, 控制上传体积
  const blob = await new Promise(resolve => {
    const img = new Image()
    const url = URL.createObjectURL(file)
    img.onload = () => {
      const canvas = document.createElement('canvas')
      const side = Math.min(img.width, img.height)
      canvas.width = canvas.height = 256
      const ctx = canvas.getContext('2d')
      // 居中裁方后缩放
      ctx.drawImage(img, (img.width - side) / 2, (img.height - side) / 2, side, side, 0, 0, 256, 256)
      URL.revokeObjectURL(url)
      canvas.toBlob(resolve, 'image/jpeg', 0.85)
    }
    img.onerror = () => { URL.revokeObjectURL(url); resolve(null) }
    img.src = url
  })
  if (!blob) return showToast('图片读取失败')
  const fd = new FormData()
  fd.append('file', blob, 'avatar.jpg')
  try {
    const res = await uploadAvatar(fd)
    const u = getUser()
    u.avatar = res.data
    setUser(u)
    user.value = { ...user.value, avatar: res.data }
    showToast('头像已更新')
  } catch (err) { /* 拦截器已toast */ }
}

const STATUS = { 1: '待制作', 2: '制作中', 3: '已完成', 4: '已取消' }
const statusText = s => STATUS[s] || '未知'

// ==================== 我的优惠券 ====================
const showCoupons = ref(false)
const myCouponList = ref([])

const couponValue = c => (c.type === 2 ? `${(Number(c.amount) * 10).toFixed(1).replace(/\.0$/, '')}折` : `${Number(c.amount)}元`)

const openMyCoupons = async () => {
  showCoupons.value = true
  try {
    const res = await myCoupons()
    myCouponList.value = res.data || []
  } catch (e) {
    myCouponList.value = []
  }
}

const onMountedLoad = onMounted(async () => {
  try {
    const u = getUser()
    if (!u) return
    // 统计 + 最近一单(字段: PageResult.list / OrderVO.details)
    const res = await pageMyOrders(u.id, { pageNum: 1, pageSize: 1 })
    orderTotal.value = res.data?.total || 0
    const order = res.data?.list?.[0]
    if (order) {
      recentOrder.value = {
        orderNo: order.orderNo,
        status: order.status,
        totalAmount: order.totalAmount,
        discountAmount: order.discountAmount,
        createTime: order.createTime,
        productName: order.details?.[0]?.productName || '奶茶'
      }
    }
    // 优惠券数量
    const cRes = await myCoupons()
    myCouponList.value = cRes.data || []
    // 钱包余额
    try {
      const wRes = await getWallet()
      walletBalance.value = wRes.data?.balance || 0
    } catch (e2) { /* 钱包接口异常时显示0 */ }
    // 会员积分
    try {
      const pRes = await getPoints()
      memberPoints.value = pRes.data?.points || 0
    } catch (e3) { /* 积分接口异常时显示0 */ }
  } catch (e) {
    // 接口未就绪时静默处理
  }
})

const openEditInfo = () => {
  editForm.value.nickname = user.value?.nickname || ''
  showEditInfo.value = true
}

const onSaveInfo = async () => {
  if (!editForm.value.nickname) {
    showToast('请输入昵称')
    return
  }
  try {
    await updateUserInfo({ nickname: editForm.value.nickname, avatar: user.value?.avatar })
  } catch (e) {
    return // 失败原因已由拦截器toast, 弹窗保持打开可重试
  }
  user.value = { ...user.value, nickname: editForm.value.nickname }
  setUser(user.value)
  showToast('修改成功')
  showEditInfo.value = false
}

const onSavePwd = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    showToast('请填写完整')
    return
  }
  if (pwdForm.value.newPassword.length < 6) {
    showToast('新密码至少6位')
    return
  }
  try {
    await updatePassword(pwdForm.value)
  } catch (e) {
    return // 如"原密码错误"已由拦截器toast, 弹窗保持打开可重试
  }
  showToast('密码已修改')
  showEditPwd.value = false
  pwdForm.value = { oldPassword: '', newPassword: '' }
  showConfirmDialog({
    title: '提示',
    message: '密码已修改，需要重新登录吗？'
  }).then(() => {
    removeToken()
    router.replace('/login')
  }).catch(() => {})
}

const onLogout = () => {
  showConfirmDialog({
    title: '提示',
    message: '确认退出登录？'
  }).then(() => {
    removeToken()
    router.replace('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--page-bg);
  padding-bottom: 100px;
}
/* ===== 渐变头部 ===== */
.user-header {
  position: relative;
  display: flex;
  align-items: center;
  padding: 44px 20px 46px;
  background: linear-gradient(160deg, #7cc9a0 0%, #3a9e63 60%, #14532f 100%);
  color: #fff;
  overflow: hidden;
}
.avatar-wrap {
  position: relative;
  margin-right: 14px;
  cursor: pointer;
  flex-shrink: 0;
}
.avatar-edit {
  position: absolute;
  right: -2px;
  bottom: -2px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #fff;
  color: #3a9e63;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.2);
}
.user-header::before,
.user-header::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  pointer-events: none;
}
.user-header::before {
  width: 150px;
  height: 150px;
  top: -50px;
  right: -40px;
}
.user-header::after {
  width: 70px;
  height: 70px;
  bottom: -20px;
  left: 40px;
}
.user-info {
  margin-left: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.nickname {
  font-size: 19px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}
.member-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  padding: 3px 10px;
  width: fit-content;
  cursor: pointer;
}
.badge-arrow {
  font-size: 11px;
  opacity: 0.7;
}
/* 统计卡上浮叠压头部 */
.stats-row {
  display: flex;
  justify-content: space-around;
  background: #fff;
  padding: 16px 8px;
  margin: -28px 12px 10px;
  position: relative;
  z-index: 1;
  border-radius: 16px;
  box-shadow: 0 6px 18px rgba(30, 90, 55, 0.1);
}
.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}
.stat-num {
  font-size: 20px;
  font-weight: 700;
  color: #2b2320;
}
.stat-label {
  font-size: 12px;
  color: #a08e7e;
}
.section-card {
  background: #fff;
  margin: 10px 12px;
  border-radius: 16px;
  padding: 14px;
  box-shadow: 0 2px 8px rgba(30, 90, 55, 0.05);
}
.card-title {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #2b2320;
}
.card-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.card-title-row .card-title {
  margin-bottom: 0;
}
.card-more {
  font-size: 12px;
  color: var(--brand);
}
.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.order-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--brand-light);
  color: var(--brand);
}
.order-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}
.order-name {
  font-size: 14px;
  color: #2b2320;
  font-weight: 500;
}
.order-time {
  font-size: 11px;
  color: #b09884;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.order-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}
.order-status {
  font-size: 11px;
  padding: 1px 8px;
  border-radius: 10px;
}
.order-status.st1 { color: #e8890c; background: #fff4e5; }
.order-status.st2 { color: #2e6be6; background: #eaf1ff; }
.order-status.st3 { color: #22a35c; background: #e8f7ee; }
.order-status.st4 { color: #999; background: #f2f2f2; }
.order-amount {
  font-size: 15px;
  font-weight: 700;
  color: #2b2320;
}
/* ===== 优惠券弹窗(票券风) ===== */
.coupon-popup {
  padding: 20px 16px 16px;
  max-height: 70vh;
  overflow-y: auto;
}
.coupon-popup .card-title {
  text-align: center;
}
.my-coupon {
  display: flex;
  align-items: center;
  border: 1px solid #f3e3d3;
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 10px;
  box-shadow: 0 2px 6px rgba(30, 90, 55, 0.04);
}
.mc-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-right: 12px;
  border-right: 1px dashed #e8d5c0;
  min-width: 80px;
  color: var(--price);
}
.mc-value {
  font-size: 20px;
  font-weight: 700;
}
.mc-min {
  font-size: 10px;
  color: #ff6a3d;
  margin-top: 2px;
}
.mc-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-left: 12px;
}
.mc-name {
  font-size: 14px;
  font-weight: 700;
  color: #2b2320;
}
.mc-expire {
  font-size: 11px;
  color: #b09884;
}
/* ===== 功能网格 ===== */
.func-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px 8px;
}
.func-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.func-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
}
.func-icon.blue {
  background: #eaf1ff;
  color: #2e6be6;
}
.func-icon.gold {
  background: linear-gradient(135deg, #d9f0e3, #b3decb);
  color: #1f6b45;
}
.func-icon.brand {
  background: var(--brand-light);
  color: var(--brand);
}
.func-icon.orange {
  background: #fff0e8;
  color: #ff6a3d;
}
.func-item > span:last-child {
  font-size: 12px;
  color: #6b5d52;
}
</style>
