<template>
  <div class="level-page">
    <van-nav-bar title="会员等级" left-arrow @click-left="router.back()" />

    <!-- 当前等级卡 -->
    <div class="hero-card">
      <div class="hero-badge">
        <van-icon :name="current.icon" />
        <span>{{ current.name }}</span>
      </div>
      <div class="hero-points">当前积分 <b>{{ points }}</b></div>
      <div class="hero-next" v-if="nextLevel">
        再积 {{ nextLevel.min - points }} 分升级 {{ nextLevel.name }}
      </div>
      <div class="hero-next" v-else>已是最高等级, 尊享全部权益</div>
    </div>

    <!-- 会员权益 -->
    <div class="section-card">
      <div class="card-title">会员权益</div>
      <div class="benefit-item">
        <van-icon name="discount" class="benefit-icon" />
        <div class="benefit-info">
          <span class="benefit-name">等级折扣</span>
          <span class="benefit-desc">白银98折 / 黄金95折 / 钻石9折, 结算自动生效</span>
        </div>
      </div>
      <div class="benefit-item">
        <van-icon name="gift-o" class="benefit-icon" />
        <div class="benefit-info">
          <span class="benefit-name">升级礼包</span>
          <span class="benefit-desc">达标自动送券: 白银¥5 / 黄金¥10 / 钻石¥20</span>
        </div>
      </div>
      <div class="benefit-item">
        <van-icon name="gold-coin-o" class="benefit-icon" />
        <div class="benefit-info">
          <span class="benefit-name">积分抵现</span>
          <span class="benefit-desc">结算页可用积分抵钱, 100积分=1元</span>
        </div>
      </div>
    </div>

    <!-- 等级划分 -->
    <div class="section-card">
      <div class="card-title">等级划分</div>
      <div class="level-item" v-for="lv in levels" :key="lv.name"
           :class="{ reached: points >= lv.min, current: lv.name === current.name }">
        <span class="lv-icon"><van-icon :name="lv.icon" /></span>
        <div class="lv-info">
          <span class="lv-name">{{ lv.name }}</span>
          <span class="lv-desc">累计积分 ≥ {{ lv.min }}</span>
        </div>
        <span class="lv-tag" v-if="lv.name === current.name">当前等级</span>
        <van-icon v-else-if="points >= lv.min" name="passed" class="lv-done" />
      </div>
      <div class="level-tip">等级由累计积分自动判定, 升级实时生效; 下单消费 / 充值 / 注册均可获得积分</div>
    </div>

    <div style="margin: 20px 16px;">
      <van-button round block plain type="primary" @click="router.push('/points')">
        去赚积分
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPoints } from '../api/points'

const router = useRouter()
const points = ref(0)

const levels = [
  { name: '普通会员', min: 0, icon: 'user-o' },
  { name: '白银会员', min: 500, icon: 'medal-o' },
  { name: '黄金会员', min: 2000, icon: 'medal' },
  { name: '钻石会员', min: 5000, icon: 'gem' }
]

const current = computed(() => [...levels].reverse().find(lv => points.value >= lv.min) || levels[0])
const nextLevel = computed(() => levels.find(lv => lv.min > points.value) || null)

onMounted(async () => {
  try {
    const res = await getPoints()
    points.value = Number(res.data?.points || 0)
  } catch (e) { /* 静默 */ }
})
</script>

<style scoped>
.level-page {
  min-height: 100vh;
  background: var(--page-bg);
  padding-bottom: 30px;
}
.level-page :deep(.van-nav-bar) {
  background: var(--brand);
}
.level-page :deep(.van-nav-bar__title) { color: #fff; }
.level-page :deep(.van-nav-bar .van-icon) { color: #fff; }

.hero-card {
  margin: 14px 16px;
  padding: 26px 20px;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--brand-light), var(--brand));
  color: #fff;
  text-align: center;
}
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255,255,255,.18);
  border: 1px solid rgba(255,255,255,.45);
  border-radius: 999px;
  padding: 8px 20px;
  font-size: 18px;
  font-weight: bold;
}
.hero-badge .van-icon { font-size: 20px; }
.hero-points { margin-top: 14px; font-size: 14px; opacity: .92; }
.hero-points b { font-size: 20px; margin: 0 3px; }
.hero-next { margin-top: 6px; font-size: 12px; opacity: .8; }

.section-card {
  margin: 14px 16px;
  background: #fff;
  border-radius: 18px;
  padding: 16px;
}
.card-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}
.level-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 10px;
  border-radius: 14px;
  margin-bottom: 8px;
  background: var(--brand-light);
  opacity: .55;
}
.level-item.reached { opacity: 1; }
.level-item.current {
  background: var(--brand-light);
  border: 1px solid var(--brand);
}
.lv-icon {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: var(--brand);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.lv-info { display: flex; flex-direction: column; gap: 2px; flex: 1; }
.lv-name { font-size: 15px; font-weight: bold; color: #333; }
.lv-desc { font-size: 12px; color: #888; }
.lv-tag {
  font-size: 11px;
  color: var(--brand);
  border: 1px solid var(--brand);
  border-radius: 999px;
  padding: 2px 10px;
}
.lv-done { color: var(--brand); font-size: 18px; }
.level-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #999;
  line-height: 1.6;
}
.benefit-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 6px;
  border-bottom: 1px solid #f2f0ec;
}
.benefit-item:last-child { border-bottom: none; }
.benefit-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--brand-light);
  color: var(--brand);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}
.benefit-info { display: flex; flex-direction: column; gap: 2px; }
.benefit-name { font-size: 14px; font-weight: bold; color: #333; }
.benefit-desc { font-size: 12px; color: #888; }
</style>
