<template>
  <div class="menu-page">
    <!-- 顶部导航 -->
    <van-sticky>
      <div class="top-head">
        <!-- 背景茶叶装饰 -->
        <i class="deco-leaf l1"></i>
        <i class="deco-leaf l2"></i>
        <i class="deco-leaf l3"></i>
        <div class="top-bar">
          <div class="pickup-toggle">
            <span :class="{ active: pickupType === 1 }" @click="setPickup(1)">到店取</span>
            <span :class="{ active: pickupType === 2 }" @click="setPickup(2)">外送</span>
          </div>
          <!-- 品牌logo: 茶千道(茶杯+涟漪+茶叶) -->
          <div class="brand-logo">
            <svg viewBox="0 0 40 40" class="brand-mark" aria-hidden="true">
              <circle cx="20" cy="20" r="19" fill="rgba(255,255,255,.2)" />
              <circle cx="20" cy="20" r="15.5" fill="#fff" />
              <!-- 茶叶 -->
              <path d="M20.2 8.6 Q23.6 9.2 23.9 12.4 Q20.5 12 20.2 8.6 Z" fill="#4c9e50" />
              <!-- 碗状茶杯 -->
              <path d="M11 16.8 H29 Q28.6 27.8 20 27.8 Q11.4 27.8 11 16.8 Z"
                fill="#e8f6ee" stroke="#3a9e63" stroke-width="1.5" stroke-linejoin="round" />
              <!-- 杯沿 -->
              <rect x="10.3" y="14.8" width="19.4" height="2.8" rx="1.4" fill="#3a9e63" />
              <!-- 茶汤涟漪 -->
              <path d="M13.6 20.8 Q16.8 19.5 20 20.8 T26.4 20.8" stroke="#8cc9ab" stroke-width="1.3" fill="none" stroke-linecap="round" />
              <path d="M15.2 23.6 Q17.6 22.5 20 23.6 T24.8 23.6" stroke="#8cc9ab" stroke-width="1.3" fill="none" stroke-linecap="round" opacity=".75" />
            </svg>
            <div class="brand-text">
              <span class="brand-name">茶千道</span>
              <span class="brand-sub">CHA QIAN DAO</span>
            </div>
          </div>
          <van-icon name="search" size="20" @click="showSearch = true" />
        </div>
        <!-- 到店取: 显示门店地址; 外送: 显示收货地址条(点击去选择/更换) -->
        <div class="addr-strip" v-if="pickupType === 1">
          <van-icon name="shop-o" size="14" />
          <span class="addr-text">门店地址：{{ SHOP_ADDRESS }}</span>
        </div>
        <div class="addr-strip" v-else @click="goChooseAddress">
          <van-icon name="location-o" size="14" />
          <span class="addr-text" v-if="deliveryAddress">{{ deliveryAddress.address }}（{{ deliveryAddress.receiver }}）</span>
          <span class="addr-text" v-else>请选择收货地址</span>
          <van-icon name="arrow" size="12" />
        </div>
      </div>
      <!-- 分类Tab与头部同一吸顶层: 永远贴在棕色栏下面, 不会盖住地址条 -->
      <van-tabs v-model:active="tabActive" @change="onTabChange">
        <van-tab title="经典菜单" />
        <van-tab title="每周9.9起" />
      </van-tabs>
    </van-sticky>

    <!-- 优惠券横滚条(只展示未领取; 超过3张收进"更多优惠"弹窗; 9.9活动Tab下不显示) -->
    <div class="coupon-bar" v-if="visibleCoupons.length && tabActive === 0">
      <div class="coupon-item" v-for="c in visibleCoupons" :key="c.id" @click="onReceive(c)">
        <div class="coupon-left">
          <span class="coupon-value">{{ couponValue(c) }}</span>
        </div>
        <div class="coupon-right">
          <span class="coupon-label">{{ c.name }}</span>
          <span class="coupon-action">点击领取</span>
        </div>
      </div>
      <button class="coupon-more" v-if="hasMoreCoupons" @click="showCouponSheet = true">
        更多优惠<van-icon name="arrow" size="11" />
      </button>
    </div>

    <!-- 更多优惠弹窗(全部券: 未领可领/已领置灰) -->
    <van-popup v-model:show="showCouponSheet" position="bottom" round>
      <div class="coupon-sheet">
        <div class="coupon-sheet-title">更多优惠</div>
        <div class="coupon-sheet-list">
          <div class="coupon-item" :class="{ received: c.received }" v-for="c in coupons" :key="c.id" @click="onReceive(c)">
            <div class="coupon-left">
              <span class="coupon-value">{{ couponValue(c) }}</span>
            </div>
            <div class="coupon-right">
              <span class="coupon-label">{{ c.name }}</span>
              <span class="coupon-action">{{ c.received ? '已领取' : '点击领取' }}</span>
            </div>
          </div>
          <van-empty v-if="!coupons.length" description="暂无优惠券" />
        </div>
      </div>
    </van-popup>

    <!-- 左分类 + 右商品(按分类分组连成一列, 滚动联动左侧高亮; 每周9.9Tab走下方专属活动页) -->
    <div class="content" v-if="tabActive === 0 && categories.length" :style="{ '--sticky-h': stickyH + 'px' }">
      <van-sidebar v-model="activeCategory" class="sidebar">
        <van-sidebar-item v-for="(cat, idx) in visibleGroups" :key="cat.id" @click="onSidebarClick(idx)">
          <template #title>
            <div class="cat-item">
              <span>{{ cat.categoryName }}</span>
            </div>
          </template>
        </van-sidebar-item>
      </van-sidebar>

      <div class="product-area">
        <template v-for="cat in visibleGroups" :key="cat.id">
          <div class="cat-group" :id="'cat-group-' + cat.id">
            <div class="cat-header">
              <span class="cat-name">{{ cat.categoryName }}</span>
              <span class="cat-desc" v-if="cat.description">{{ cat.description }}</span>
            </div>
            <div class="product-card" v-for="p in productsOf(cat)" :key="p.id">
              <img :src="p.imageUrl || FALLBACK_IMG" class="product-img" />
              <div class="product-info">
                <div class="product-name">{{ p.productName }}</div>
                <div class="product-desc" v-if="p.description">{{ p.description }}</div>
                <div class="product-bottom">
                  <div class="price-box">
                    <span class="price-now"><i class="price-cur">￥</i>{{ p.price }}</span>
                    <span class="price-old" v-if="p.originalPrice">￥{{ p.originalPrice }}</span>
                  </div>
                  <van-button
                    type="primary"
                    size="mini"
                    round
                    icon="plus"
                    class="add-btn"
                    @click="openSpecPicker(p)"
                  />
                </div>
              </div>
            </div>
          </div>
        </template>
        <van-empty v-if="displayProducts.length === 0" description="暂无商品" />
      </div>
    </div>

    <!-- 每周9.9专属活动页: 热销banner(倒计时) + 促销卡片(立省/折扣/已售) + 活动规则 -->
    <div class="sp-page" v-if="tabActive === 1">
      <template v-if="displayProducts.length">
        <div class="sp-banner">
          <div class="sp-banner-left">
            <span class="sp-hot"><van-icon name="fire-o" />本周特惠 · 每周一更新</span>
            <div class="sp-banner-title">每周9.9起</div>
            <div class="sp-banner-sub">品牌经典 · 好茶低价 · 售完即止</div>
            <span class="sp-countdown">距结束 <b>{{ spCountdown }}</b></span>
          </div>
          <div class="sp-price-bubble">
            <span class="sp-price-num">9.9</span>
            <span class="sp-price-unit">元起</span>
          </div>
        </div>

        <template v-for="cat in visibleGroups" :key="cat.id">
          <div class="sp-group-head">
            <span class="sp-group-bar"></span>{{ cat.categoryName }}
            <span class="sp-group-sub">本周特价</span>
          </div>
          <div class="sp-card" v-for="p in productsOf(cat)" :key="p.id">
            <div class="sp-img-wrap">
              <img :src="p.imageUrl || FALLBACK_IMG" class="sp-img" />
              <span class="sp-corner">活动价</span>
            </div>
            <div class="sp-info">
              <div class="sp-name-row">
                <span class="sp-name">{{ p.productName }}</span>
                <span class="sp-sold">已售 {{ spSold(p) }}+</span>
              </div>
              <div class="sp-desc" v-if="p.description">{{ p.description }}</div>
              <div class="sp-tags">
                <span class="sp-save-tag">立省￥{{ spSave(p) }}</span>
                <span class="sp-disc-tag">{{ spDiscount(p) }}</span>
              </div>
              <div class="sp-bottom">
                <div class="sp-price">
                  <i>￥</i><b>{{ p.price }}</b><s>￥{{ p.originalPrice }}</s>
                </div>
                <van-button round icon="plus" class="sp-add" @click="openSpecPicker(p)" />
              </div>
            </div>
          </div>
        </template>

        <div class="sp-rules">
          <div class="sp-rules-title">活动规则</div>
          <p>1. 活动价每周一更新，数量有限，售完即止；</p>
          <p>2. 活动商品按活动价直接结算，不与优惠券同享；</p>
          <p>3. 本活动仅限线上点单参与，到店/外送同价。</p>
        </div>
      </template>
      <van-empty v-else description="本周活动暂未开始，周一再来看看" />
    </div>

    <!-- 底部购物车栏 -->
    <div class="cart-bar" v-if="cartTotalCount > 0">
      <van-badge :content="cartTotalCount" class="cart-badge" @click="goCart">
        <van-icon name="shopping-cart-o" size="28" color="#fff" />
      </van-badge>
      <div class="cart-info" @click="goCart">
        <span class="total-price">￥{{ cartTotalPrice.toFixed(2) }}</span>
        <span class="delivery-fee" v-if="pickupType === 2">含配送费￥5</span>
      </div>
      <van-button round size="small" class="checkout-btn" @click="goCart">去结算</van-button>
    </div>

    <!-- 规格挑选弹窗 -->
    <van-popup v-model:show="specPopup" position="bottom" round :style="{ maxHeight: '75%' }">
      <div class="spec-popup" v-if="specProduct">
        <div class="spec-head">
          <img :src="specProduct.imageUrl || FALLBACK_IMG" class="spec-img" />
          <div class="spec-head-info">
            <div class="spec-name">{{ specProduct.productName }}</div>
            <div class="spec-price"><i class="price-cur">￥</i>{{ unitPrice.toFixed(2) }}</div>
            <div class="spec-chosen" v-if="specsText">已选：{{ specsText }}</div>
          </div>
        </div>

        <div class="spec-group" v-for="g in specGroups" :key="g.typeId">
          <div class="spec-group-title">
            {{ g.typeName }}
            <span class="multi-hint" v-if="isMultiGroup(g)">(可多选)</span>
          </div>
          <div class="spec-options">
            <span
              v-for="o in g.options"
              :key="o.specOptionId"
              class="spec-option"
              :class="{ active: selectedSpecs.some(s => s.optionId === o.specOptionId) }"
              @click="pickSpec(g, o)"
            >
              {{ o.specOptionName }}
              <em v-if="Number(o.extraPrice) > 0">+{{ o.extraPrice }}</em>
            </span>
          </div>
        </div>

        <div class="spec-qty-row">
          <span class="spec-group-title">数量</span>
          <van-stepper v-model="specQty" min="1" max="20" integer />
        </div>

        <div class="spec-footer">
          <div class="spec-footer-total">
            <span class="total-label">合计</span>
            <span class="total-price">￥{{ specTotal.toFixed(2) }}</span>
          </div>
          <van-button round type="primary" class="spec-footer-btn" @click="confirmSpecs">加入购物车</van-button>
        </div>
      </div>
    </van-popup>

    <!-- 搜索弹层 -->
    <van-popup v-model:show="showSearch" position="top" :style="{ height: '100%' }">
      <van-search v-model="keyword" placeholder="搜索商品" shape="round" show-action @cancel="showSearch = false" @search="onSearch" />
      <!-- 热门搜索: 无关键词时展示, 点击直达 -->
      <div class="hot-search" v-if="!keyword">
        <div class="hot-title">
          <van-icon name="fire-o" />
          <span>热门搜索</span>
        </div>
        <div class="hot-tags">
          <span class="hot-tag" v-for="w in hotKeywords" :key="w" @click="keyword = w; onSearch()">{{ w }}</span>
        </div>
      </div>
      <div class="search-results">
        <div class="product-card" v-for="p in searchResults" :key="p.id">
          <img :src="p.imageUrl || FALLBACK_IMG" class="product-img" />
          <div class="product-info">
            <div class="product-name">{{ p.productName }}</div>
            <div class="product-bottom">
              <span class="price-now"><i class="price-cur">￥</i>{{ p.price }}</span>
              <van-button type="primary" size="mini" round icon="plus" @click="openSpecPicker(p)" />
            </div>
          </div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { listCategories, listOnSale, getProduct } from '../api/product'
import { listCoupons, receiveCoupon, listWeekSpecials } from '../api/user'
import { listAddresses } from '../api/address'

const router = useRouter()
const keyword = ref('')
const showSearch = ref(false)

// 热门搜索词(与在售商品匹配, 点击直接搜)
const hotKeywords = ['生椰拿铁', '芋泥波波', '珍珠奶茶', '杨枝甘露', '冰鲜柠檬水', '轻乳茶']

// 门店地址(到店取模式展示在顶部, 改成你们门店的真实地址即可)
const SHOP_ADDRESS = '幸福路1号奶茶大厦1层'
const tabActive = ref(0)
const activeCategory = ref(0)
// 取餐方式持久化: 去地址页选完回来保持"外送"状态
const pickupType = ref(Number(sessionStorage.getItem('pickupType')) || 1)
const setPickup = t => {
  pickupType.value = t
  sessionStorage.setItem('pickupType', String(t))
}
const categories = ref([])
const products = ref([])
const cart = ref(JSON.parse(sessionStorage.getItem('cart') || '[]'))

// 无图商品占位图(内置SVG奶茶杯, 不依赖外网CDN)
const FALLBACK_IMG =
  "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Crect width='100' height='100' fill='%23f6ede3'/%3E%3Crect x='26' y='22' width='48' height='8' rx='4' fill='%239a6435'/%3E%3Cpath d='M30 30h40l-6 44a8 8 0 0 1-8 7h-12a8 8 0 0 1-8-7z' fill='%23c68e5e'/%3E%3Ccircle cx='42' cy='62' r='4' fill='%239a6435'/%3E%3Ccircle cx='56' cy='68' r='4' fill='%239a6435'/%3E%3Ccircle cx='50' cy='55' r='4' fill='%239a6435'/%3E%3C/svg%3E"

// 优惠券(真实接口: 可领列表, 点击领取)
const coupons = ref([])
const showCouponSheet = ref(false)
// 横条只展示未领取的券, 最多3张; 其余进"更多优惠"弹窗
const unreceivedCoupons = computed(() => coupons.value.filter(c => !c.received))
const visibleCoupons = computed(() => unreceivedCoupons.value.slice(0, 3))
const hasMoreCoupons = computed(() => unreceivedCoupons.value.length > 3)

// ==================== 外送收货地址(顶部地址条展示, 与结算页共用选择逻辑) ====================
const deliveryAddress = ref(null)

const loadDeliveryAddress = async () => {
  try {
    // 地址选择页(select模式)回传的地址优先, 否则取默认地址
    const stored = sessionStorage.getItem('chosenAddress')
    if (stored) {
      deliveryAddress.value = JSON.parse(stored)
      return
    }
    const res = await listAddresses()
    const list = res.data || []
    deliveryAddress.value = list.find(a => a.isDefault === 1) || list[0] || null
  } catch {
    deliveryAddress.value = null
  }
}

const goChooseAddress = () => {
  sessionStorage.removeItem('chosenAddress')  // 避免残留旧选择
  router.push('/address?select=1')
}

// 展示值: 满减券"x元", 折扣券"x.x折"
const couponValue = c => (c.type === 2 ? `${(Number(c.amount) * 10).toFixed(1).replace(/\.0$/, '')}折` : `${Number(c.amount)}元`)

const loadCoupons = async () => {
  try {
    const res = await listCoupons()
    coupons.value = res.data || []
  } catch (e) {
    coupons.value = []
  }
}

const onReceive = async c => {
  if (c.received) {
    showToast('已领取过该券，可在"我的-优惠券"查看')
    return
  }
  await receiveCoupon(c.id)
  c.received = true
  showToast('领取成功')
}

// ==================== 分类分组 + 滚动联动(仿瑞幸: 商品连成一列, 滚动自动高亮左侧) ====================
const productsOf = cat => displayProducts.value.filter(p => p.categoryId === cat.id)

// 只渲染有商品的分类组, 侧栏与分组严格同序对齐
const visibleGroups = computed(() => displayCategories.value.filter(c => productsOf(c).length > 0))

// 吸顶层高度: 顶栏84 + 地址条(到店46/外送52) + 分类Tab(46)
const stickyH = computed(() => (pickupType.value === 2 ? 182 : 176))

// 点击侧栏: 滚动到对应分组(滚动期间锁定监听, 避免高亮回跳)
let lockSpy = false
const onSidebarClick = idx => {
  const cat = visibleGroups.value[idx]
  if (!cat) return
  const el = document.getElementById('cat-group-' + cat.id)
  if (!el) return
  lockSpy = true
  el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  setTimeout(() => { lockSpy = false }, 700)
}

// 页面滚动: 组头越过吸顶层时切换左侧高亮; 滚到底部选中最后一组
const onScrollSpy = () => {
  if (lockSpy) return
  const groups = visibleGroups.value
  if (!groups.length) return
  const limit = stickyH.value + 6
  let cur = 0
  for (let i = 0; i < groups.length; i++) {
    const el = document.getElementById('cat-group-' + groups[i].id)
    if (el && el.getBoundingClientRect().top - limit <= 0) cur = i
  }
  if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 10) {
    cur = groups.length - 1
  }
  if (activeCategory.value !== cur) activeCategory.value = cur
}

onMounted(() => window.addEventListener('scroll', onScrollSpy, { passive: true }))
onBeforeUnmount(() => window.removeEventListener('scroll', onScrollSpy))

// ==================== 每周9.9特价(管理端在商品管理页配置, 后端 milktea-user /user/weekspecial) ====================
const specialMap = ref(new Map())   // productId -> 活动价, 进页面时从接口加载

const loadSpecials = async () => {
  try {
    const res = await listWeekSpecials()
    const map = new Map()
    for (const s of res.data || []) map.set(s.productId, Number(s.specialPrice))
    specialMap.value = map
  } catch {
    specialMap.value = new Map()
  }
}

// 展示商品: 特价Tab只显示活动商品, 价格替换为活动价+划线原价
const displayProducts = computed(() => {
  if (tabActive.value === 0) return products.value
  return products.value
    .filter(p => specialMap.value.has(p.id))
    .map(p => ({ ...p, price: specialMap.value.get(p.id), originalPrice: p.price }))
})

// 特价Tab下左侧只显示含活动商品的分类
const displayCategories = computed(() => {
  if (tabActive.value === 0) return categories.value
  return categories.value.filter(cat => displayProducts.value.some(p => p.categoryId === cat.id))
})

// ---- 活动页氛围元素: 倒计时(至下周一0点)/立省/折扣/已售 ----
const spNow = ref(Date.now())
let spTimer = null
onMounted(() => { spTimer = setInterval(() => { spNow.value = Date.now() }, 1000) })
onBeforeUnmount(() => clearInterval(spTimer))

const spCountdown = computed(() => {
  const d = new Date(); d.setHours(0, 0, 0, 0)
  const diff = ((8 - d.getDay()) % 7) || 7   // 距下一个周一的天数
  let s = Math.max(0, Math.floor((d.getTime() + diff * 864e5 - spNow.value) / 1000))
  const dd = Math.floor(s / 86400); s %= 86400
  const hh = String(Math.floor(s / 3600)).padStart(2, '0'); s %= 3600
  const mm = String(Math.floor(s / 60)).padStart(2, '0')
  const ss = String(s % 60).padStart(2, '0')
  return (dd ? dd + '天' : '') + `${hh}:${mm}:${ss}`
})

// 立省金额(原价-活动价, 整数不带小数位)
const spSave = p => {
  const v = +(Number(p.originalPrice) - Number(p.price)).toFixed(2)
  return Number.isInteger(v) ? v : v.toFixed(2)
}
// 折扣(几折)
const spDiscount = p => {
  const r = (Number(p.price) / Number(p.originalPrice)) * 10
  return r.toFixed(1).replace(/\.0$/, '') + '折'
}
// 已售杯数(按商品id固定, 页面氛围展示)
const spSold = p => 186 + ((p.id * 97) % 560)

const onTabChange = () => {
  activeCategory.value = 0
  window.scrollTo({ top: 0 })  // 切Tab回顶部, 避免停在半截
}

const searchResults = computed(() => {
  if (!keyword.value) return []
  return products.value.filter(p => p.productName.includes(keyword.value))
})

const cartTotalCount = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.quantity, 0)
})

const cartTotalPrice = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const saveCart = () => {
  sessionStorage.setItem('cart', JSON.stringify(cart.value))
}

// ==================== 规格挑选弹窗(仿瑞幸) ====================
const specPopup = ref(false)
const specProduct = ref(null)   // 完整商品(含specs)
const selectedSpecs = ref([])   // 已选选项: {typeId, typeName, optionId, optionName, extraPrice}
const specQty = ref(1)          // 弹窗内购买数量

// 按规格类型分组
const specGroups = computed(() => {
  const groups = []
  const map = {}
  for (const s of specProduct.value?.specs || []) {
    if (!map[s.specTypeId]) {
      map[s.specTypeId] = { typeId: s.specTypeId, typeName: s.specTypeName, options: [] }
      groups.push(map[s.specTypeId])
    }
    map[s.specTypeId].options.push(s)
  }
  return groups
})

// 瑞幸规则: 加料/小料可多选(默认不选), 其余组必选其一
const isMultiGroup = g => /加料|小料|topping/i.test(g.typeName)

// 当前单价 = 基础价 + 已选规格加价
const unitPrice = computed(() => {
  const base = Number(specProduct.value?.price) || 0
  return base + selectedSpecs.value.reduce((s, o) => s + (Number(o.extraPrice) || 0), 0)
})

// 合计 = 单价 x 数量
const specTotal = computed(() => unitPrice.value * specQty.value)

const specsText = computed(() => selectedSpecs.value.map(o => o.optionName).join('/'))

const openSpecPicker = async product => {
  try {
    const res = await getProduct(product.id)
    const full = res.data
    // 9.9活动Tab的卡片按活动价快照加购(与列表展示一致); 经典菜单/搜索按原价
    const isSpecial = product.originalPrice != null
    const basePrice = isSpecial ? Number(product.price) : Number(full.price)
    if (!full.specs || !full.specs.length) {
      // 无规格商品直接加购
      addToCart({ productId: full.id, productName: full.productName, price: basePrice, image: full.imageUrl, specs: '', special: isSpecial })
      return
    }
    specProduct.value = { ...full, price: basePrice, special: isSpecial }
    specQty.value = 1
    // 默认: 单选组选第一项, 多选组不选
    selectedSpecs.value = specGroups.value
      .filter(g => !isMultiGroup(g))
      .map(g => {
        const o = g.options[0]
        return {
          typeId: o.specTypeId, typeName: o.specTypeName,
          optionId: o.specOptionId, optionName: o.specOptionName,
          extraPrice: Number(o.extraPrice) || 0
        }
      })
    specPopup.value = true
  } catch {
    showToast('获取规格失败')
  }
}

const pickSpec = (group, option) => {
  const item = {
    typeId: option.specTypeId, typeName: option.specTypeName,
    optionId: option.specOptionId, optionName: option.specOptionName,
    extraPrice: Number(option.extraPrice) || 0
  }
  const idx = selectedSpecs.value.findIndex(o => o.optionId === option.specOptionId)
  if (isMultiGroup(group)) {
    // 多选组: 点击切换选中/取消
    if (idx > -1) {
      selectedSpecs.value.splice(idx, 1)
    } else {
      selectedSpecs.value.push(item)
    }
  } else {
    // 单选组: 同组替换
    const sameGroupIdx = selectedSpecs.value.findIndex(o => o.typeId === group.typeId)
    if (sameGroupIdx > -1) {
      selectedSpecs.value.splice(sameGroupIdx, 1, item)
    } else {
      selectedSpecs.value.push(item)
    }
  }
}

const confirmSpecs = () => {
  addToCart({
    productId: specProduct.value.id,
    productName: specProduct.value.productName,
    price: unitPrice.value,
    image: specProduct.value.imageUrl,
    specs: specsText.value,
    special: specProduct.value.special === true
  }, specQty.value)
  specPopup.value = false
}

const addToCart = (item, qty = 1) => {
  // 合并键含价格: 同一商品9.9活动价与原价分开成行, 互不串价
  const exist = cart.value.find(c => c.productId === item.productId && c.specs === item.specs && Number(c.price) === Number(item.price))
  if (exist) {
    exist.quantity += qty
  } else {
    cart.value.push({ ...item, quantity: qty })
  }
  saveCart()
  showToast('已加入购物车')
}

const goCart = () => {
  router.push('/cart')
}

const onSearch = () => {
  if (!keyword.value) return
  const matchCat = categories.value.findIndex(cat =>
    products.value.some(p => p.categoryId === cat.id && p.name.includes(keyword.value))
  )
  if (matchCat > -1) {
    activeCategory.value = matchCat
    showSearch.value = false
  }
}

onMounted(async () => {
  try {
    const [catRes, prodRes] = await Promise.all([listCategories(), listOnSale()])
    categories.value = catRes.data || []
    products.value = prodRes.data || []
  } catch (e) {
    showToast('加载失败')
  }
  loadCoupons()
  loadSpecials()
  loadDeliveryAddress()
})
</script>

<style scoped>
.menu-page {
  min-height: 100vh;
  background: var(--page-bg);
  padding-bottom: 160px;
}
/* ===== 顶部品牌渐变头 ===== */
.top-head {
  position: relative;
  overflow: hidden;
  background: var(--brand-grad);
}
/* 背景飘浮茶叶(叶形=对角圆角, 带叶脉, 缓慢摇曳) */
.deco-leaf {
  position: absolute;
  pointer-events: none;
  background: rgba(255, 255, 255, 0.13);
  border-radius: 0 100% 0 100%;
}
.deco-leaf::after {
  content: '';
  position: absolute;
  left: 12%;
  top: 12%;
  width: 76%;
  height: 1px;
  background: rgba(255, 255, 255, 0.35);
  transform: rotate(45deg);
  transform-origin: left top;
}
.deco-leaf.l1 {
  width: 88px;
  height: 88px;
  right: -22px;
  top: -26px;
  transform: rotate(24deg);
  animation: leaf-sway 6s ease-in-out infinite;
}
.deco-leaf.l2 {
  width: 44px;
  height: 44px;
  right: 96px;
  top: 16px;
  transform: rotate(-32deg);
  background: rgba(255, 255, 255, 0.1);
  animation: leaf-sway 7s ease-in-out infinite reverse;
}
.deco-leaf.l3 {
  width: 58px;
  height: 58px;
  left: -18px;
  bottom: -30px;
  transform: rotate(118deg);
  background: rgba(255, 255, 255, 0.09);
  animation: leaf-sway 8s ease-in-out infinite;
}
@keyframes leaf-sway {
  0%, 100% { translate: 0 0; rotate: 0deg; }
  50% { translate: 3px 4px; rotate: 7deg; }
}
.top-bar {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
  height: 84px;
  color: #fff;
}
/* 外送模式地址条 */
.addr-strip {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 16px 14px;
  color: #fff;
  cursor: pointer;
}
.addr-text {
  flex: 1;
  min-width: 0;
  font-size: 12px;
  opacity: 0.95;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.addr-strip .van-icon-arrow {
  opacity: 0.8;
}
/* 分类Tab(瑞幸式: 白底/黑粗体激活/短圆角下划线) */
.menu-page :deep(.van-tabs) {
  --van-tabs-bottom-bar-color: var(--brand);
}
.menu-page :deep(.van-tabs__wrap) {
  height: 46px;
  border-bottom: 1px solid #e3efe8;
}
.menu-page :deep(.van-tabs__nav) {
  background: #fff;
}
.menu-page :deep(.van-tab) {
  font-size: 15px;
  color: #8a7a6c;
}
.menu-page :deep(.van-tab--active .van-tab__text) {
  font-size: 16px;
  font-weight: 800;
  color: #2b2320;
}
.menu-page :deep(.van-tabs__line) {
  width: 24px !important;  /* 覆盖vant内联宽度 */
  height: 3px;
  border-radius: 3px;
  bottom: 8px;
  background: var(--brand);
}
.pickup-toggle {
  display: flex;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 16px;
  padding: 3px;
}
/* ===== 品牌logo(顶栏中央) ===== */
.brand-logo {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 7px;
}
.brand-mark {
  width: 46px;
  height: 46px;
  filter: drop-shadow(0 1px 3px rgba(0, 0, 0, 0.15));
  animation: logo-bob 3s ease-in-out infinite;
}
@keyframes logo-bob {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-2px); }
}
.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}
.brand-name {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 2px;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.12);
}
.brand-sub {
  font-size: 7px;
  letter-spacing: 1.6px;
  color: rgba(255, 255, 255, 0.85);
  transform: scale(0.9);
  transform-origin: left;
}
.pickup-toggle span {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.85);
  padding: 3px 11px;
  border-radius: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.pickup-toggle span.active {
  background: #fff;
  color: var(--brand-deep);
  font-weight: 600;
}
/* ===== 票券式优惠券 ===== */
.coupon-bar {
  display: flex;
  gap: 12px;
  padding: 12px 16px 8px;
  overflow-x: auto;
  background: #fff;
  scrollbar-width: none;  /* 隐藏横向滚动条 */
}
.coupon-bar::-webkit-scrollbar {
  display: none;
}
.coupon-item {
  position: relative;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #ff9a56 0%, #ff6a3d 100%);
  border-radius: 10px;
  padding: 8px 12px 8px 4px;
  min-width: 132px;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(255, 106, 61, 0.25);
}
.coupon-item::before,
.coupon-item::after {
  content: '';
  position: absolute;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #fff;
  top: 50%;
  transform: translateY(-50%);
}
.coupon-item::before { left: -5px; }
.coupon-item::after { right: -5px; }
.coupon-left {
  padding: 2px 10px 2px 8px;
  border-right: 1px dashed rgba(255, 255, 255, 0.6);
}
.coupon-value {
  font-size: 20px;
  font-weight: 700;
}
.coupon-right {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  padding-left: 10px;
}
.coupon-label {
  font-size: 11px;
  opacity: 0.95;
  max-width: 90px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.coupon-action {
  font-size: 10px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.22);
  border-radius: 10px;
  padding: 1px 8px;
}
.coupon-item.received {
  opacity: 0.5;
  filter: grayscale(0.3);
}
.coupon-item.received .coupon-action {
  background: transparent;
  border-color: rgba(255, 255, 255, 0.5);
}
/* "更多优惠"胶囊按钮 */
.coupon-more {
  flex-shrink: 0;
  align-self: center;
  display: inline-flex;
  align-items: center;
  gap: 2px;
  border: none;
  background: linear-gradient(135deg, #ff9a56 0%, #ff6a3d 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border-radius: 16px;
  padding: 7px 12px;
  box-shadow: 0 4px 10px rgba(255, 106, 61, 0.25);
  white-space: nowrap;
  cursor: pointer;
}
/* 更多优惠弹窗 */
.coupon-sheet {
  padding: 18px 16px calc(20px + env(safe-area-inset-bottom));
}
.coupon-sheet-title {
  text-align: center;
  font-size: 16px;
  font-weight: 700;
  color: #2b2320;
  margin-bottom: 14px;
}
.coupon-sheet-list {
  max-height: 56vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.coupon-sheet-list .coupon-item {
  min-width: 0;
}
/* ===== 左侧分类栏 ===== */
.content {
  display: flex;
  margin-top: 8px;
}
.sidebar {
  flex-shrink: 0;
  width: 88px;
  --van-sidebar-selected-border-color: var(--brand);
  /* 侧栏固定: 贴在吸顶层下方, 自身可滚动, 右侧商品滚动时高亮联动 */
  position: sticky;
  top: var(--sticky-h, 74px);
  align-self: flex-start;
  max-height: calc(100vh - var(--sticky-h, 74px) - 60px);
  overflow-y: auto;
  scrollbar-width: none;
}
.sidebar::-webkit-scrollbar {
  display: none;
}
.sidebar :deep(.van-sidebar-item) {
  background: transparent;
  padding: 17px 6px 17px 16px;
  font-size: 13px;
  color: #8a7a6c;
  border-radius: 16px 0 0 16px;
}
.sidebar :deep(.van-sidebar-item--selected) {
  background: #fff;
  color: var(--brand-deep);
  font-weight: 700;
  border-radius: 16px 0 0 16px;
  box-shadow: 0 4px 12px rgba(30, 90, 55, 0.07);
}
.sidebar :deep(.van-sidebar-item--selected::before) {
  background-color: var(--brand);
  border-radius: 2px;
}
.cat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  line-height: 1.3;
}
.product-area {
  flex: 1;
  min-width: 0;
  padding: 10px 10px 8px 4px;
}
.cat-group {
  /* 点击侧栏定位时, 组头停在吸顶层下方 */
  scroll-margin-top: calc(var(--sticky-h, 74px) + 4px);
}
.cat-header {
  padding: 6px 8px 12px;
  background: var(--page-bg);
}
.cat-name {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 18px;
  font-weight: 800;
  color: #2b2320;
  letter-spacing: 0.5px;
}
.cat-name::before {
  content: '';
  width: 4px;
  height: 15px;
  border-radius: 2px;
  background: var(--brand-grad);
}
.cat-desc {
  font-size: 12px;
  color: #b09884;
  margin-left: 11px;
}
.empty {
  background: #fff;
  border-radius: 16px;
  margin-left: 4px;
}
/* ===== 商品卡片(瑞幸式圆图) ===== */
.product-card {
  background: #fff;
  border-radius: 18px;
  padding: 14px;
  margin-bottom: 12px;
  margin-left: 4px;
  display: flex;
  gap: 13px;
  box-shadow: 0 4px 16px rgba(30, 90, 55, 0.06);
  transition: transform 0.15s;
}
.product-card:active {
  transform: scale(0.985);
}
.product-img {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 4px solid #fff;
  box-shadow: 0 4px 14px rgba(30, 90, 55, 0.14), 0 0 0 1px rgba(58, 158, 99, 0.08);
  background: var(--brand-light);
}
.product-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.product-name {
  font-size: 16px;
  font-weight: 700;
  color: #2b2320;
  margin-bottom: 4px;
}
.product-desc {
  font-size: 12px;
  color: #a08e7e;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}
.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.price-box {
  display: flex;
  align-items: baseline;
  gap: 6px;
}
.price-now {
  display: flex;
  align-items: baseline;
  font-size: 22px;
  font-weight: 800;
  color: var(--price);
  letter-spacing: -0.5px;
  font-style: normal;
}
.price-cur {
  font-size: 13px;
  font-weight: 700;
  font-style: normal;
  margin-right: 1px;
}
.price-old {
  font-size: 12px;
  color: #bfae9e;
  text-decoration: line-through;
}
/* 渐变圆形加购按钮 */
.add-btn {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  padding: 0;
  border: none;
  border-radius: 50%;
  background: var(--brand-grad);
  box-shadow: 0 4px 10px rgba(58, 158, 99, 0.35);
  transition: transform 0.15s;
}
.add-btn:active {
  transform: scale(0.85);
}
/* ===== 底部购物车条(瑞幸式白胶囊+右侧大按钮) ===== */
.cart-bar {
  position: fixed;
  bottom: 84px;
  left: 12px;
  right: 12px;
  height: 62px;
  background: #fff;
  border-radius: 31px;
  display: flex;
  align-items: center;
  padding: 0 10px 0 10px;
  z-index: 100;
  box-shadow: 0 10px 30px rgba(30, 90, 55, 0.18);
}
.cart-badge {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: var(--brand-grad);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(58, 158, 99, 0.35);
}
.cart-badge :deep(.van-badge__content) {
  background: #ff6a3d;
}
.cart-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 1px;
  padding-left: 12px;
}
.cart-bar .total-price {
  font-size: 20px;
  font-weight: 800;
  color: #2b2320;
  letter-spacing: -0.5px;
}
.delivery-fee {
  font-size: 10px;
  color: #b09884;
}
.cart-bar .checkout-btn.van-button {
  flex-shrink: 0;
  height: 44px;
  padding: 0 24px;
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  border: none;
  border-radius: 24px;
  background: var(--brand-grad);
  box-shadow: 0 6px 14px rgba(58, 158, 99, 0.3);
}
/* ===== 规格挑选弹窗 ===== */
.spec-popup {
  padding: 20px 16px 12px;
  max-height: 75vh;
  overflow-y: auto;
}
.spec-head {
  display: flex;
  gap: 14px;
  padding-bottom: 14px;
  border-bottom: 1px solid #f3ece4;
}
.spec-img {
  width: 76px;
  height: 76px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 3px solid #fff;
  box-shadow: 0 2px 10px rgba(30, 90, 55, 0.12);
  background: var(--brand-light);
}
.spec-head-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 5px;
}
.spec-name {
  font-size: 16px;
  font-weight: 700;
  color: #2b2320;
}
.spec-price {
  display: flex;
  align-items: baseline;
  font-size: 22px;
  font-weight: 800;
  color: var(--price);
  letter-spacing: -0.5px;
}
.spec-chosen {
  font-size: 11px;
  color: #b09884;
}
.spec-group {
  margin-top: 16px;
}
.spec-group-title {
  font-size: 14px;
  font-weight: 700;
  color: #2b2320;
  margin-bottom: 10px;
}
.multi-hint {
  font-size: 11px;
  font-weight: normal;
  color: #b09884;
}
.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.spec-option {
  padding: 7px 16px;
  border-radius: 18px;
  background: #f5efe8;
  font-size: 13px;
  color: #6b5d52;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.15s;
}
.spec-option em {
  font-style: normal;
  font-size: 11px;
  color: #ff6a3d;
}
.spec-option.active {
  background: var(--brand-light);
  border-color: var(--brand);
  color: var(--brand-deep);
  font-weight: 600;
}
.spec-option.active em {
  color: var(--brand);
}
.spec-qty-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20px;
}
.spec-qty-row .spec-group-title {
  margin-bottom: 0;
}
.spec-footer {
  display: flex;
  align-items: center;
  margin-top: 20px;
  padding-bottom: 8px;
}
.spec-footer-total {
  display: flex;
  align-items: baseline;
  gap: 4px;
  flex-shrink: 0;
}
.total-label {
  font-size: 12px;
  color: #b09884;
}
.spec-footer .total-price {
  font-size: 22px;
  font-weight: 700;
  color: var(--price);
}
.spec-footer-btn {
  flex: 1;
  margin-left: 16px;
  height: 42px;
  font-weight: 600;
  border: none;
  background: var(--brand-grad);
  box-shadow: 0 6px 14px rgba(58, 158, 99, 0.3);
}
/* ===== 搜索弹层 ===== */
/* 热门搜索 */
.hot-search {
  padding: 14px 16px 4px;
}
.hot-title {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 700;
  color: #4d5c53;
  margin-bottom: 10px;
}
.hot-title .van-icon {
  color: var(--price);
}
.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.hot-tag {
  font-size: 12px;
  color: var(--brand);
  background: var(--brand-light);
  border-radius: 14px;
  padding: 5px 13px;
  cursor: pointer;
  transition: all 0.15s;
}
.hot-tag:active {
  transform: scale(0.94);
  opacity: 0.8;
}
.search-results {
  padding: 8px 10px 10px;
}

/* ===== 每周9.9专属活动页 ===== */
.sp-page {
  padding: 12px 12px 0;
}
/* 热销banner: 暖橙渐变+装饰光斑+价格气泡 */
.sp-banner {
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: 20px;
  padding: 18px 16px;
  background: linear-gradient(135deg, #ff9a56, #e25822);
  color: #fff;
  box-shadow: 0 8px 20px rgba(226, 88, 34, 0.28);
}
.sp-banner::before {
  content: '';
  position: absolute;
  right: 70px;
  top: -34px;
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.14);
}
.sp-banner::after {
  content: '';
  position: absolute;
  left: -22px;
  bottom: -38px;
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}
.sp-banner-left {
  position: relative;
  z-index: 1;
}
.sp-hot {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  background: rgba(255, 255, 255, 0.22);
  border-radius: 999px;
  padding: 3px 10px;
}
.sp-banner-title {
  font-size: 26px;
  font-weight: 900;
  letter-spacing: 1px;
  margin-top: 8px;
  text-shadow: 0 2px 6px rgba(160, 50, 10, 0.25);
}
.sp-banner-sub {
  font-size: 12px;
  opacity: 0.94;
  margin-top: 4px;
  letter-spacing: 0.5px;
}
.sp-countdown {
  display: inline-block;
  margin-top: 10px;
  font-size: 11px;
  background: rgba(0, 0, 0, 0.18);
  border-radius: 8px;
  padding: 3px 9px;
}
.sp-countdown b {
  font-weight: 800;
  font-variant-numeric: tabular-nums;
}
.sp-price-bubble {
  position: relative;
  z-index: 1;
  flex-shrink: 0;
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: #fff;
  color: #e25822;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 14px rgba(140, 40, 5, 0.3);
}
.sp-price-num {
  font-size: 30px;
  font-weight: 900;
  line-height: 1;
}
.sp-price-unit {
  font-size: 12px;
  font-weight: 800;
  margin-top: 3px;
}
/* 分类组头(橙色装饰条, 区别于经典菜单的绿色) */
.sp-group-head {
  display: flex;
  align-items: baseline;
  gap: 7px;
  padding: 16px 6px 10px;
  font-size: 18px;
  font-weight: 800;
  color: #2b2320;
}
.sp-group-bar {
  width: 4px;
  height: 15px;
  border-radius: 2px;
  background: linear-gradient(180deg, #ff9a56, #e25822);
  align-self: center;
}
.sp-group-sub {
  font-size: 10px;
  font-weight: 700;
  color: #e25822;
  background: #fff1e8;
  border-radius: 999px;
  padding: 2px 8px;
}
/* 促销卡片 */
.sp-card {
  display: flex;
  gap: 12px;
  background: #fff;
  border-radius: 18px;
  padding: 12px;
  margin-bottom: 12px;
  box-shadow: 0 4px 16px rgba(226, 88, 34, 0.07);
  transition: transform 0.15s;
}
.sp-card:active {
  transform: scale(0.985);
}
.sp-img-wrap {
  position: relative;
  flex-shrink: 0;
}
.sp-img {
  width: 92px;
  height: 92px;
  border-radius: 50%;
  object-fit: cover;
  background: #f6ede3;
}
.sp-corner {
  position: absolute;
  top: -3px;
  left: -6px;
  font-size: 10px;
  font-weight: 800;
  color: #fff;
  background: linear-gradient(135deg, #ff7a3c, #e25822);
  border-radius: 10px 10px 10px 2px;
  padding: 2px 8px;
  box-shadow: 0 2px 6px rgba(226, 88, 34, 0.35);
}
.sp-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.sp-name-row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px;
}
.sp-name {
  font-size: 16px;
  font-weight: 800;
  color: #2b2320;
}
.sp-sold {
  flex-shrink: 0;
  font-size: 10px;
  color: #c0b0a5;
}
.sp-desc {
  font-size: 11px;
  color: #9c8d84;
  margin-top: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.sp-tags {
  display: flex;
  gap: 6px;
  margin-top: 7px;
}
.sp-save-tag {
  font-size: 10px;
  font-weight: 800;
  color: #e25822;
  background: #fff1e8;
  border-radius: 999px;
  padding: 2px 9px;
}
.sp-disc-tag {
  font-size: 10px;
  font-weight: 700;
  color: #e25822;
  border: 1px solid #f3c1a6;
  border-radius: 999px;
  padding: 1px 8px;
}
.sp-bottom {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 8px;
}
.sp-price {
  display: flex;
  align-items: baseline;
  color: #e25822;
}
.sp-price i {
  font-size: 13px;
  font-weight: 800;
  font-style: normal;
}
.sp-price b {
  font-size: 26px;
  font-weight: 900;
  letter-spacing: -0.5px;
}
.sp-price s {
  font-size: 11px;
  color: #c4b6ac;
  margin-left: 7px;
}
.sp-add {
  flex-shrink: 0;
  width: 34px;
  height: 34px;
  padding: 0;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff8a4c, #e25822);
  box-shadow: 0 4px 10px rgba(226, 88, 34, 0.35);
  transition: transform 0.15s;
}
.sp-add:active {
  transform: scale(0.9);
}
/* 活动规则 */
.sp-rules {
  background: #fff;
  border-radius: 14px;
  padding: 12px 14px;
  margin-top: 2px;
  font-size: 11px;
  line-height: 1.9;
  color: #a2938a;
  box-shadow: 0 4px 16px rgba(30, 90, 55, 0.05);
}
.sp-rules-title {
  font-size: 12px;
  font-weight: 800;
  color: #7c6f66;
  margin-bottom: 2px;
}
</style>
