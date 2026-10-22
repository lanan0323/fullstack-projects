<template>
  <div class="points-page">
    <div class="page-head">
      <van-icon name="arrow-left" size="20" @click="$router.back()" />
      <span class="head-title">会员中心</span>
      <span style="width: 20px" />
    </div>

    <!-- 会员卡 -->
    <div class="member-card">
      <div class="member-top">
        <span class="level-badge"><van-icon name="gem-o" /> {{ levelName }}</span>
        <span class="member-phone">{{ phoneMask }}</span>
      </div>
      <div class="points-label">我的积分</div>
      <div class="points-num">{{ points }}</div>
    </div>

    <!-- 赚积分 -->
    <div class="section-card">
      <div class="card-title">怎么赚积分</div>
      <div class="rule-item">
        <span class="rule-icon"><van-icon name="user-o" /></span>
        <div class="rule-info">
          <span class="rule-name">注册礼包</span>
          <span class="rule-desc">新用户注册即送 100 积分</span>
        </div>
        <span class="rule-award">+100</span>
      </div>
      <div class="rule-item">
        <span class="rule-icon"><van-icon name="balance-o" /></span>
        <div class="rule-info">
          <span class="rule-name">钱包充值</span>
          <span class="rule-desc">充值 1 元 = 1 积分, 上不封顶</span>
        </div>
        <span class="rule-award">1元=1分</span>
      </div>
      <div class="rule-item">
        <span class="rule-icon"><van-icon name="orders-o" /></span>
        <div class="rule-info">
          <span class="rule-name">下单消费</span>
          <span class="rule-desc">订单完成后按实付金额自动到账</span>
        </div>
        <span class="rule-award">1元=1分</span>
      </div>
      <div class="rule-item">
        <span class="rule-icon"><van-icon name="gold-coin-o" /></span>
        <div class="rule-info">
          <span class="rule-name">下单抵现</span>
          <span class="rule-desc">结算页开启积分抵扣, 100分抵1元</span>
        </div>
        <span class="rule-award">100分=1元</span>
      </div>
    </div>

    <!-- 明细 -->
    <div class="section-card">
      <div class="card-title">积分明细</div>
      <div class="flow-item" v-for="f in flows" :key="f.id">
        <span class="flow-icon" :class="f.type === 1 ? 'in' : 'out'">
          <van-icon :name="f.type === 1 ? 'plus' : 'minus'" />
        </span>
        <div class="flow-info">
          <span class="flow-name">{{ f.remark || (f.type === 1 ? '积分获得' : '积分消耗') }}</span>
          <span class="flow-time">{{ (f.createTime || '').replace('T', ' ').slice(0, 19) }}</span>
        </div>
        <span class="flow-amount" :class="f.type === 1 ? 'plus-c' : 'minus'">{{ f.type === 1 ? '+' : '-' }}{{ f.points }}</span>
      </div>
      <van-empty v-if="!flows.length" description="暂无积分记录, 充值即可获得" image-size="70" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getPoints, pointsFlows } from '../api/points'
import { getUser } from '../utils/auth'

const points = ref(0)
const flows = ref([])

const levelName = computed(() => {
  const p = points.value
  if (p >= 5000) return '钻石会员'
  if (p >= 2000) return '黄金会员'
  if (p >= 500) return '白银会员'
  return '普通会员'
})

const phoneMask = computed(() => {
  const phone = getUser()?.phone || ''
  return phone.length === 11 ? phone.slice(0, 3) + '****' + phone.slice(7) : '奶茶会员'
})

onMounted(async () => {
  const [p, f] = await Promise.all([getPoints(), pointsFlows(50)])
  points.value = p.data?.points || 0
  flows.value = f.data || []
})
</script>

<style scoped>
.points-page {
  min-height: 100vh;
  background: var(--page-bg);
  padding-bottom: 40px;
}
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: linear-gradient(160deg, #7cc9a0 0%, #3a9e63 60%, #14532f 100%);
  color: #fff;
}
.head-title {
  font-size: 16px;
  font-weight: 700;
}
.member-card {
  margin: 16px;
  padding: 20px 22px;
  border-radius: 18px;
  background: linear-gradient(135deg, #1d5c3d 0%, #14532f 100%);
  color: #fff;
  box-shadow: 0 8px 20px rgba(31, 107, 69, 0.3);
}
.member-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.level-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  font-weight: 700;
  background: linear-gradient(135deg, #b3decb, #7cc9a0);
  color: #14532f;
  border-radius: 20px;
  padding: 4px 12px;
}
.member-phone {
  font-size: 12px;
  opacity: 0.7;
}
.points-label {
  margin-top: 18px;
  font-size: 13px;
  opacity: 0.8;
}
.points-num {
  font-size: 42px;
  font-weight: 800;
  letter-spacing: -1px;
}
.section-card {
  margin: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 16px;
}
.card-title {
  font-size: 14px;
  font-weight: 700;
  color: #2b2320;
  margin-bottom: 12px;
}
.rule-item {
  display: flex;
  align-items: center;
  padding: 11px 0;
  border-bottom: 1px solid #f6efe7;
}
.rule-item:last-child {
  border-bottom: none;
}
.rule-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #eaf7f0;
  color: #3a9e63;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 17px;
  margin-right: 12px;
  flex-shrink: 0;
}
.rule-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.rule-name {
  font-size: 14px;
  font-weight: 600;
  color: #2b2320;
}
.rule-desc {
  font-size: 11px;
  color: #b8a99a;
}
.rule-award {
  font-size: 13px;
  font-weight: 700;
  color: #3a9e63;
}
.rule-award.soon {
  font-size: 11px;
  color: #b8a99a;
  font-weight: 400;
}
.flow-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f6efe7;
}
.flow-item:last-child {
  border-bottom: none;
}
.flow-icon {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 15px;
  flex-shrink: 0;
}
.flow-icon.in { background: #e8f5ee; color: #2e9e6b; }
.flow-icon.out { background: #fdeee6; color: #d4622a; }
.flow-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}
.flow-name {
  font-size: 14px;
  color: #2b2320;
  font-weight: 500;
}
.flow-time {
  font-size: 11px;
  color: #b8a99a;
}
.flow-amount {
  font-size: 15px;
  font-weight: 700;
}
.flow-amount.plus-c { color: #2e9e6b; }
.flow-amount.minus { color: #d4622a; }
</style>
