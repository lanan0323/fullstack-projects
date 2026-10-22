<template>
  <div class="cart-page">
    <van-nav-bar title="确认订单" left-arrow @click-left="$router.back()" />

    <!-- 取餐方式 -->
    <van-cell-group inset title="取餐方式">
      <van-radio-group v-model="orderForm.pickupType" direction="horizontal">
        <van-radio name="1">到店自取</van-radio>
        <van-radio name="2">配送</van-radio>
      </van-radio-group>
    </van-cell-group>

    <!-- 收货地址（配送才显示, 从地址簿选择） -->
    <div class="addr-card" v-if="orderForm.pickupType === '2'" @click="goChooseAddress">
      <van-icon name="location-o" class="addr-icon" size="20" />
      <div class="addr-info" v-if="chosenAddress">
        <div class="addr-line">
          <span class="receiver">{{ chosenAddress.receiver }}</span>
          <span class="phone">{{ chosenAddress.phone }}</span>
        </div>
        <div class="addr-text">{{ chosenAddress.address }}</div>
      </div>
      <div class="addr-info" v-else>
        <div class="addr-line">
          <span class="receiver">请选择收货地址</span>
        </div>
        <div class="addr-text">{{ addressCount > 0 ? '点击从地址簿选择' : '还没有地址，点击去添加' }}</div>
      </div>
      <van-icon name="arrow" color="#b09884" />
    </div>

    <!-- 商品清单 -->
    <van-cell-group inset title="商品清单" v-if="cart.length">
      <van-swipe-cell v-for="item in cart" :key="item.productId + (item.specs || '')">
        <van-card
          :price="item.price"
          :title="item.productName"
          :desc="item.specs || ''"
          :thumb="item.image"
        >
          <template #footer>
            <van-stepper v-model="item.quantity" :min="1" :max="20" integer @change="persist" />
          </template>
        </van-card>
        <template #right>
          <van-button square type="danger" text="删除" class="delete-btn" @click="removeItem(item)" />
        </template>
      </van-swipe-cell>
    </van-cell-group>
    <van-empty v-else description="购物车是空的">
      <van-button round type="primary" @click="$router.push('/menu')">去点单</van-button>
    </van-empty>

    <!-- 备注 -->
    <van-cell-group inset title="备注">
      <van-field v-model="orderForm.remark" placeholder="口味偏好、过敏提醒等" type="textarea" rows="2" autosize />
    </van-cell-group>

    <!-- 金额 -->
    <van-cell-group inset title="费用明细">
      <van-cell title="商品金额" :value="`￥${totalPrice.toFixed(2)}`" />
      <van-cell v-if="levelRate < 1" :title="`${levelName}折扣`" :value="`-${levelRate * 10}折  -￥${levelOff.toFixed(2)}`" />
      <!-- 含9.9活动商品时不可用券 -->
      <van-cell v-if="hasSpecialItem" title="优惠券" value="含9.9活动价商品, 不可用券" />
      <van-cell v-else title="优惠券" is-link :value="couponLabel" @click="showCoupon = true" />
      <van-cell v-if="pointsAvail >= 100" title="积分抵扣" :label="`${pointsAvail}分可用, 100分=1元`">
        <template #value>
          <span v-if="usePoints" style="color: var(--price); font-weight: bold; margin-right: 8px;">-￥{{ ptDeduct.toFixed(2) }}</span>
          <van-switch v-model="usePoints" size="20px" />
        </template>
      </van-cell>
      <van-cell title="配送费" :value="orderForm.pickupType === '2' ? '￥5.00' : '￥0.00'" />
      <van-cell title="合计">
        <template #value>
          <span style="color: var(--price); font-weight: bold; font-size: 18px;">￥{{ finalPrice.toFixed(2) }}</span>
        </template>
      </van-cell>
    </van-cell-group>

    <!-- 优惠券选择弹层 -->
    <van-popup v-model:show="showCoupon" round position="bottom">
      <div class="coupon-popup">
        <div class="coupon-popup-title">选择优惠券</div>
        <div class="coupon-tip" v-if="hasSpecialItem">购物车含每周9.9活动价商品, 不可使用优惠券</div>
        <div class="coupon-item plain" :class="{ active: selectedCouponId === 0 }" @click="selectedCouponId = 0; showCoupon = false">
          <span>不使用优惠券</span>
          <van-icon v-if="selectedCouponId === 0" name="success" color="#3a9e63" />
        </div>
        <div class="coupon-item" v-for="c in usableCoupons" :key="c.id"
             :class="{ active: selectedCouponId === c.couponId }"
             @click="selectedCouponId = c.couponId; showCoupon = false">
          <div class="coupon-left">
            <template v-if="c.type === 2">
              <span class="coupon-value">{{ (c.amount * 10).toFixed(1) }}</span><span class="coupon-unit">折</span>
            </template>
            <template v-else>
              <span class="coupon-unit">￥</span><span class="coupon-value">{{ c.amount }}</span>
            </template>
          </div>
          <div class="coupon-info">
            <div class="coupon-name">{{ c.name }}</div>
            <div class="coupon-cond">{{ c.minAmount > 0 ? `满${c.minAmount}元可用` : '无门槛' }}</div>
            <div class="coupon-expire">{{ (c.expireTime || '').slice(0, 10) }} 到期</div>
          </div>
          <van-icon v-if="selectedCouponId === c.couponId" name="success" color="#3a9e63" />
        </div>
        <van-empty v-if="!coupons.length" description="暂无可用优惠券" image-size="60" />
      </div>
    </van-popup>

    <div style="margin: 20px 16px 8px;">
      <van-button round block type="primary" :loading="loading" @click="submitOrder">提交订单</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { createOrder } from '../api/order'
import { myCoupons, listWeekSpecials, myPoints } from '../api/user'
import { listAddresses } from '../api/address'
import { getUser } from '../utils/auth'

const router = useRouter()
const loading = ref(false)

const cart = ref(JSON.parse(sessionStorage.getItem('cart') || '[]'))
const orderForm = ref({
  pickupType: '1',
  remark: ''
})

// ========== 收货地址(地址簿) ==========
const chosenAddress = ref(null)   // 已选地址: 选择页回传优先, 否则取默认地址
const addressCount = ref(0)

const loadAddress = async () => {
  try {
    // 地址选择页(select模式)回传的地址优先
    const stored = sessionStorage.getItem('chosenAddress')
    if (stored) {
      chosenAddress.value = JSON.parse(stored)
      return
    }
    const res = await listAddresses()
    const list = res.data || []
    addressCount.value = list.length
    chosenAddress.value = list.find(a => a.isDefault === 1) || list[0] || null
  } catch (e) {
    chosenAddress.value = null
  }
}

const goChooseAddress = () => {
  sessionStorage.removeItem('chosenAddress')  // 避免残留旧选择
  router.push('/address?select=1')
}

const totalPrice = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// ========== 优惠券 ==========
const coupons = ref([])
const selectedCouponId = ref(0) // 0=不使用
const showCoupon = ref(false)

// ========== 积分抵现(100分=1元, 与券可叠加, 后端封顶到应付) ==========
const pointsAvail = ref(0)
const usePoints = ref(false)

onMounted(async () => {
  try {
    const res = await myCoupons()
    coupons.value = res.data || []
  } catch (e) { /* 静默, 不影响下单 */ }
  loadAddress()
  // 9.9活动价映射: 商品ID -> 活动价(购物车按成交价命中才禁券)
  try {
    const res = await listWeekSpecials()
    specialPriceMap.value = Object.fromEntries((res.data || []).map(s => [s.productId, s.specialPrice]))
  } catch (e) { /* 加载失败不拦券 */ }
  // 我的积分(加载失败不挡结算)
  try {
    const res = await myPoints()
    pointsAvail.value = Number(res.data?.points || 0)
  } catch (e) { pointsAvail.value = 0 }
})

// 9.9活动价商品不可用优惠券: 优先看加购时打的special标记, 再按成交价兜底比对
const specialPriceMap = ref({})
const hasSpecialItem = computed(() => cart.value.some(i => {
  if (i.special === true) return true
  const sp = specialPriceMap.value[i.productId]
  return sp != null && Math.abs(Number(i.price) - Number(sp)) < 0.001
}))

// ========== 等级折扣(与后端口径一致: 白银98/黄金95/钻石90, 作用于商品总额) ==========
const levelRate = computed(() => {
  const p = pointsAvail.value
  if (p >= 5000) return 0.90
  if (p >= 2000) return 0.95
  if (p >= 500) return 0.98
  return 1
})
const levelName = computed(() => {
  const p = pointsAvail.value
  if (p >= 5000) return '钻石会员'
  if (p >= 2000) return '黄金会员'
  if (p >= 500) return '白银会员'
  return ''
})
// 折后商品金额(券门槛/券抵扣/积分封顶均以此为基准)
const levelBase = computed(() => Math.round(totalPrice.value * levelRate.value * 100) / 100)
const levelOff = computed(() => Math.round((totalPrice.value - levelBase.value) * 100) / 100)

// 满足门槛的券才可选用; 含9.9活动商品时全部禁用 (门槛按会员折后价判断)
const usableCoupons = computed(() => {
  if (hasSpecialItem.value) return []
  return coupons.value.filter(c => !c.minAmount || c.minAmount <= 0 || levelBase.value >= c.minAmount)
})

const selectedCoupon = computed(() =>
  usableCoupons.value.find(c => c.couponId === selectedCouponId.value)
)

const discount = computed(() => {
  const c = selectedCoupon.value
  if (!c) return 0
  return c.type === 2
    ? Math.round(levelBase.value * (1 - c.amount) * 100) / 100
    : Math.min(Number(c.amount), levelBase.value)
})

const couponLabel = computed(() => {
  const c = selectedCoupon.value
  if (!c) return usableCoupons.value.length ? `${usableCoupons.value.length}张可用` : '暂无可用'
  return `-￥${discount.value.toFixed(2)}`
})

// 积分抵扣额: 整百积分, 100分=1元, 封顶到券后应付(与后端口径一致)
const ptDeduct = computed(() => {
  if (!usePoints.value || pointsAvail.value < 100) return 0
  const usable = Math.floor(pointsAvail.value / 100) * 100
  const payable = Math.max(levelBase.value - discount.value, 0) + (orderForm.value.pickupType === '2' ? 5 : 0)
  return Math.min(usable / 100, payable)
})

const finalPrice = computed(() => {
  return Math.max(levelBase.value - discount.value - ptDeduct.value, 0) + (orderForm.value.pickupType === '2' ? 5 : 0)
})

const persist = () => {
  sessionStorage.setItem('cart', JSON.stringify(cart.value))
}

const removeItem = item => {
  // 匹配价格: 同商品可能同时存在原价行与9.9活动价行
  cart.value = cart.value.filter(c => !(c.productId === item.productId && c.specs === item.specs && Number(c.price) === Number(item.price)))
  persist()
  showToast('已删除')
}

const submitOrder = async () => {
  if (cart.value.length === 0) {
    showToast('购物车为空')
    return
  }
  if (orderForm.value.pickupType === '2' && !chosenAddress.value) {
    showToast('请先选择收货地址')
    return
  }
  loading.value = true
  try {
    const user = getUser()
    const addr = chosenAddress.value
    const remarkParts = [
      orderForm.value.pickupType === '2'
        ? `外送: ${addr.address}（${addr.receiver} ${addr.phone}）`
        : '到店自取',
      orderForm.value.remark
    ].filter(Boolean)
    // 积分抵现协议: |PT:分数 (订单服务解析扣分, 存库时剔除)
    if (ptDeduct.value > 0) remarkParts.push(`|PT:${Math.floor(pointsAvail.value / 100) * 100}`)
    const payload = {
      userId: user.id,
      remark: remarkParts.join('；'),
      items: cart.value.map(item => ({
        productId: item.productId,
        productName: item.productName,
        specs: item.specs || '',
        price: item.price,
        quantity: item.quantity
      }))
    }
    if (selectedCoupon.value) payload.couponId = selectedCoupon.value.couponId
    const res = await createOrder(payload)
    sessionStorage.removeItem('cart')
    sessionStorage.removeItem('chosenAddress')
    showToast('下单成功')
    router.replace('/orders')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: var(--page-bg);
  /* 悬浮Tabbar高约72px, 留白需盖过它否则提交订单按钮被挡 */
  padding-bottom: 100px;
}
.cart-page :deep(.van-nav-bar) {
  background: var(--brand-grad);
}
.cart-page :deep(.van-nav-bar__title) {
  color: #fff;
}
.cart-page :deep(.van-nav-bar .van-icon) {
  color: #fff;
}
.cart-page :deep(.van-cell-group--inset) {
  border-radius: 14px;
  margin-bottom: 10px;
  box-shadow: 0 2px 8px rgba(30, 90, 55, 0.05);
}
.cart-page :deep(.van-card__thumb) {
  border-radius: 10px;
  overflow: hidden;
}
.cart-page :deep(.van-card__price) {
  color: var(--price);
  font-weight: 700;
}
.delete-btn {
  height: 100%;
}
/* 收货地址卡片 */
.addr-card {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  border-radius: 14px;
  margin: 0 16px 10px;
  padding: 14px;
  box-shadow: 0 2px 8px rgba(30, 90, 55, 0.05);
  cursor: pointer;
}
.addr-icon {
  color: var(--brand);
  flex-shrink: 0;
}
.addr-info {
  flex: 1;
  min-width: 0;
}
.addr-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 3px;
}
.addr-line .receiver {
  font-size: 15px;
  font-weight: 700;
  color: #2b2320;
}
.addr-line .phone {
  font-size: 13px;
  color: #8a7a6c;
}
.addr-text {
  font-size: 13px;
  color: #6b5d52;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.coupon-popup {
  padding: 20px 16px 16px;
  max-height: 60vh;
  overflow-y: auto;
}
.coupon-popup-title {
  font-size: 15px;
  color: #2b2320;
  text-align: center;
  font-weight: 700;
  margin-bottom: 14px;
}
.coupon-tip {
  margin: 0 16px 10px;
  padding: 8px 12px;
  background: #fff4ed;
  border-radius: 8px;
  font-size: 12px;
  color: #d4622a;
  text-align: center;
}
.coupon-item {
  position: relative;
  display: flex;
  align-items: center;
  background: #fff;
  border: 1px solid #f3e3d3;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 10px;
  box-shadow: 0 2px 6px rgba(30, 90, 55, 0.04);
  transition: all 0.15s;
}
.coupon-item.plain {
  background: #faf7f4;
  border-color: #eee5dc;
  color: #8a7a6c;
}
.coupon-item.active {
  border-color: var(--brand);
  box-shadow: 0 4px 10px rgba(58, 158, 99, 0.18);
}
.coupon-left {
  min-width: 78px;
  text-align: center;
  color: var(--price);
  padding-right: 12px;
  border-right: 1px dashed #e8d5c0;
}
.coupon-value {
  font-size: 26px;
  font-weight: 700;
}
.coupon-unit {
  font-size: 14px;
}
.coupon-info {
  flex: 1;
  margin-left: 12px;
}
.coupon-name {
  font-size: 14px;
  font-weight: 700;
  color: #2b2320;
}
.coupon-cond {
  font-size: 12px;
  color: #ff6a3d;
  margin-top: 2px;
}
.coupon-expire {
  font-size: 11px;
  color: #b09884;
  margin-top: 2px;
}
</style>
