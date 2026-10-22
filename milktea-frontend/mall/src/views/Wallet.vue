<template>
  <div class="wallet-page">
    <div class="page-head">
      <van-icon name="arrow-left" size="20" @click="$router.back()" />
      <span class="head-title">我的钱包</span>
      <span style="width: 20px" />
    </div>

    <!-- 余额卡 -->
    <div class="balance-card">
      <div class="balance-label">钱包余额 (元)</div>
      <div class="balance-num">{{ Number(balance).toFixed(2) }}</div>
      <div class="balance-tip">充值后可在下单时抵扣 · 模拟支付环境</div>
    </div>

    <!-- 充值 -->
    <div class="section-card">
      <div class="card-title">选择充值金额</div>
      <div class="amount-grid">
        <div v-for="a in presets" :key="a" class="amount-item" :class="{ active: amount === a, custom: a === 'custom' }" @click="pick(a)">
          <template v-if="a === 'custom'">
            <input v-model="customAmount" class="custom-input" type="number" min="0.01" max="5000" step="0.01" placeholder="自定义" @input="onCustomInput" />
          </template>
          <template v-else>{{ a }}元</template>
        </div>
      </div>
      <van-button round block type="primary" class="recharge-btn" :loading="loading" @click="doRecharge">
        确认充值<span v-if="payAmount"> ￥{{ payAmount }}</span>
      </van-button>
      <div class="pay-note"><van-icon name="shield-o" /> 模拟支付环境, 不产生真实扣款</div>
    </div>

    <!-- 明细 -->
    <div class="section-card">
      <div class="card-title">收支明细</div>
      <div class="flow-item" v-for="f in flows" :key="f.id">
        <span class="flow-icon" :class="f.type === 1 ? 'in' : f.type === 2 ? 'out' : 'refund'">
          <van-icon :name="f.type === 1 ? 'plus' : f.type === 2 ? 'cart-o' : 'revoke'" />
        </span>
        <div class="flow-info">
          <span class="flow-name">{{ flowName(f) }}</span>
          <span class="flow-time">{{ (f.createTime || '').replace('T', ' ').slice(0, 19) }}</span>
        </div>
        <div class="flow-right">
          <span class="flow-amount" :class="f.type === 2 ? 'minus' : 'plus-c'">{{ f.type === 2 ? '-' : '+' }}￥{{ Number(f.amount).toFixed(2) }}</span>
          <span class="flow-after">余额 ￥{{ Number(f.balanceAfter).toFixed(2) }}</span>
        </div>
      </div>
      <van-empty v-if="!flows.length" description="暂无收支记录" image-size="70" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast } from 'vant'
import { getWallet, rechargeWallet, walletFlows } from '../api/wallet'

const presets = [10, 20, 50, 100, 'custom']
const amount = ref(null)
const customAmount = ref('')
const balance = ref(0)
const flows = ref([])
const loading = ref(false)

const payAmount = computed(() => {
  const v = amount.value === 'custom' ? parseFloat(customAmount.value) : amount.value
  return v > 0 ? v : null
})

const pick = a => {
  amount.value = a
  if (a !== 'custom') customAmount.value = ''
}

const onCustomInput = () => { amount.value = 'custom' }

const flowName = f => ({ 1: '在线充值', 2: f.orderNo ? `订单消费 ${f.orderNo}` : '订单消费', 3: '订单退款' }[f.type] || '其他')

const doRecharge = async () => {
  const v = payAmount.value
  if (!v) return showToast('请输入或选择充值金额')
  if (v < 0.01) return showToast('充值金额至少0.01元')
  if (v > 5000) return showToast('单笔充值不能超过5000元')
  loading.value = true
  try {
    const res = await rechargeWallet(v)
    balance.value = res.data.balance
    showToast('充值成功(模拟支付)')
    const f = await walletFlows(50)
    flows.value = f.data || []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  const [w, f] = await Promise.all([getWallet(), walletFlows(50)])
  balance.value = w.data.balance
  flows.value = f.data || []
})
</script>

<style scoped>
.wallet-page {
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
.balance-card {
  margin: 16px;
  padding: 26px 22px;
  border-radius: 18px;
  background: linear-gradient(135deg, #7cc9a0 0%, #3a9e63 70%);
  color: #fff;
  box-shadow: 0 8px 20px rgba(31, 107, 69, 0.25);
}
.balance-label {
  font-size: 13px;
  opacity: 0.85;
}
.balance-num {
  font-size: 40px;
  font-weight: 800;
  letter-spacing: -1px;
  margin: 8px 0 6px;
}
.balance-tip {
  font-size: 11px;
  opacity: 0.75;
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
.amount-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 14px;
}
.amount-item {
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1.5px solid #f0e4d7;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #1f6b45;
  background: #fffaf5;
  cursor: pointer;
  transition: all 0.15s;
}
.amount-item.active {
  border-color: #3a9e63;
  background: #eaf7f0;
  box-shadow: 0 2px 8px rgba(58, 158, 99, 0.15);
}
.custom-input {
  width: 80%;
  border: none;
  outline: none;
  background: transparent;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #1f6b45;
}
.recharge-btn {
  height: 44px;
  font-size: 15px;
  font-weight: 700;
}
.pay-note {
  margin-top: 10px;
  font-size: 11px;
  color: #b8a99a;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
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
  font-size: 16px;
  flex-shrink: 0;
}
.flow-icon.in { background: #e8f5ee; color: #2e9e6b; }
.flow-icon.out { background: #fdeee6; color: #d4622a; }
.flow-icon.refund { background: #eef2fb; color: #4a6fd4; }
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
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.flow-time {
  font-size: 11px;
  color: #b8a99a;
}
.flow-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 3px;
}
.flow-amount {
  font-size: 15px;
  font-weight: 700;
}
.flow-amount.plus-c { color: #2e9e6b; }
.flow-amount.minus { color: #2b2320; }
.flow-after {
  font-size: 11px;
  color: #b8a99a;
}
</style>
