<template>
  <!-- 钱包页面根容器 -->
  <div class="wallet-page">
    <!-- 页头区域 -->
    <div class="wallet-hero">
      <!-- 英文标识 -->
      <div class="hero-eyebrow">WALLET</div>
      <!-- 页面主标题 -->
      <h1 class="hero-title">我的钱包</h1>
      <!-- 页面副标题说明 -->
      <p class="hero-sub">余额充值 · 提现 · 交易资金明细</p>
    </div>

    <!-- 余额卡片区域 -->
    <div class="balance-card">
      <!-- 余额信息主体 -->
      <div class="balance-main">
        <!-- 余额标签 -->
        <div class="balance-label">可用余额（元）</div>
        <!-- 余额数字展示 -->
        <div class="balance-num">
          <!-- 货币符号 -->
          <span class="cur">¥</span>
          <!-- 格式化后的余额数值 -->
          <span class="num">{{ formatMoney(wallet.balance) }}</span>
        </div>
        <!-- 冻结金额信息 -->
        <div class="balance-frozen">
          <!-- 冻结标签 -->
          <span class="frozen-label">冻结金额</span>
          <!-- 冻结金额数值 -->
          <span class="frozen-num">¥{{ formatMoney(wallet.frozenAmount) }}</span>
          <!-- 冻结说明提示 -->
          <span class="frozen-tip">下单未确认时资金冻结，确认或取消后解冻</span>
        </div>
      </div>
      <!-- 操作按钮区 -->
      <div class="balance-actions">
        <!-- 充值按钮 -->
        <button class="btn-primary" @click="openRecharge">充值</button>
        <!-- 提现按钮 -->
        <button class="btn-ghost" @click="openWithdraw">提现</button>
      </div>
    </div>

    <!-- 统计卡片行 -->
    <div class="stats-row">
      <!-- 累计充值统计卡片 -->
      <div class="stat-card">
        <!-- 统计标签 -->
        <div class="stat-label">累计充值</div>
        <!-- 统计数值，绿色表示正向 -->
        <div class="stat-value stat-green">¥{{ formatMoney(wallet.totalRecharge) }}</div>
      </div>
      <!-- 累计提现统计卡片 -->
      <div class="stat-card">
        <!-- 统计标签 -->
        <div class="stat-label">累计提现</div>
        <!-- 统计数值 -->
        <div class="stat-value">¥{{ formatMoney(wallet.totalWithdraw) }}</div>
      </div>
      <!-- 累计收入统计卡片 -->
      <div class="stat-card">
        <!-- 统计标签 -->
        <div class="stat-label">累计收入</div>
        <!-- 统计数值，绿色表示正向 -->
        <div class="stat-value stat-green">¥{{ formatMoney(wallet.totalIncome) }}</div>
      </div>
      <!-- 累计支出统计卡片 -->
      <div class="stat-card">
        <!-- 统计标签 -->
        <div class="stat-label">累计支出</div>
        <!-- 统计数值，红色表示支出 -->
        <div class="stat-value stat-red">¥{{ formatMoney(wallet.totalExpense) }}</div>
      </div>
    </div>

    <!-- 资金流水面板 -->
    <div class="logs-panel">
      <!-- 流水头部：标题+筛选 -->
      <div class="logs-head">
        <!-- 流水标题 -->
        <span class="logs-title">资金流水</span>
        <!-- 类型筛选下拉框 -->
        <el-select v-model="queryParams.type" placeholder="全部类型" clearable class="logs-filter"
          popper-class="debuff-popper">
          <!-- 遍历类型映射生成选项 -->
          <el-option v-for="(label, key) in typeMap" :key="key" :label="label" :value="Number(key)" />
        </el-select>
        <!-- 搜索按钮 -->
        <!-- <el-button type="primary" @click="getList" class="logs-search-btn">搜索</el-button> -->
      </div>
      <!-- 流水数据表格 -->
      <el-table v-loading="loading" :data="logList" empty-text="暂无流水记录" class="debuff-table">
        <!-- 时间列 -->
        <el-table-column label="时间" prop="createTime" align="center" width="170" />
        <!-- 类型列 -->
        <el-table-column label="类型" align="center" width="100">
          <!-- 自定义类型列渲染 -->
          <template #default="scope">
            <!-- 类型标签，根据类型动态设置样式类 -->
            <span class="type-chip" :class="'type-' + scope.row.type">{{ typeMap[scope.row.type] || '未知' }}</span>
          </template>
        </el-table-column>
        <!-- 金额列 -->
        <el-table-column label="金额" align="right" width="140">
          <!-- 自定义金额列渲染 -->
          <template #default="scope">
            <!-- 金额显示，正数绿色负数红色 -->
            <span class="amount" :class="scope.row.amount >= 0 ? 'amount-in' : 'amount-out'">
              <!-- 正数显示+号，然后显示格式化金额 -->
              {{ scope.row.amount >= 0 ? '+' : '' }}{{ formatMoney(scope.row.amount) }}
            </span>
          </template>
        </el-table-column>
        <!-- 变动后余额列 -->
        <el-table-column label="变动后余额" align="right" width="120">
          <!-- 自定义余额列渲染 -->
          <template #default="scope">
            <!-- 格式化后的变动后余额 -->
            <span class="balance-after">{{ formatMoney(scope.row.balanceAfter) }}</span>
          </template>
        </el-table-column>
        <!-- 关联订单号列 -->
        <el-table-column label="关联订单" prop="relatedOrderNo" align="center" width="170">
          <!-- 自定义订单号列渲染 -->
          <template #default="scope">
            <!-- 订单号显示，无则显示破折号 -->
            <span class="order-no">{{ scope.row.relatedOrderNo || '—' }}</span>
          </template>
        </el-table-column>
        <!-- 备注列 -->
        <el-table-column label="备注" prop="remark" align="center" min-width="140" />
      </el-table>
      <!-- 分页组件区域 -->
      <div class="pager">
        <!-- 分页组件，显示总数并绑定翻页事件 -->
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getLogs" />
      </div>
    </div>

    <!-- 充值对话框 -->
    <el-dialog v-model="rechargeOpen" title="充值" width="420px" append-to-body
      class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 充值提示信息 -->
      <div class="dialog-tip">模拟支付，输入金额后立即到账</div>
      <!-- 充值表单 -->
      <el-form label-width="70px">
        <!-- 金额表单项 -->
        <el-form-item label="金额">
          <!-- 金额数字输入框 -->
          <el-input-number v-model="rechargeForm.amount" :min="0.01" :max="100000" :precision="2"
            :controls="false" placeholder="请输入充值金额" class="amount-input" />
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮 -->
      <template #footer>
        <!-- 取消按钮 -->
        <el-button @click="rechargeOpen = false">取消</el-button>
        <!-- 确认充值按钮，带加载状态 -->
        <el-button type="primary" :loading="submitting" @click="submitRecharge">确认充值</el-button>
      </template>
    </el-dialog>

    <!-- 提现对话框 -->
    <el-dialog v-model="withdrawOpen" title="提现" width="420px" append-to-body
      class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 提现提示信息，显示可提现余额 -->
      <div class="dialog-tip">可提现余额 ¥{{ formatMoney(wallet.balance) }}</div>
      <!-- 提现表单 -->
      <el-form label-width="70px">
        <!-- 金额表单项 -->
        <el-form-item label="金额">
          <!-- 金额数字输入框 -->
          <el-input-number v-model="withdrawForm.amount" :min="0.01" :max="50000" :precision="2"
            :controls="false" placeholder="请输入提现金额" class="amount-input" />
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮 -->
      <template #footer>
        <!-- 取消按钮 -->
        <el-button @click="withdrawOpen = false">取消</el-button>
        <!-- 确认提现按钮，带加载状态 -->
        <el-button type="primary" :loading="submitting" @click="submitWithdraw">确认提现</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Wallet">
// 引入Vue组合式API
import { ref, reactive, onMounted, onActivated } from 'vue'
// 引入Element Plus消息提示
import { ElMessage } from 'element-plus'
// 引入钱包相关API方法
import { getMyWallet, getMyWalletLogs, rechargeWallet, withdrawWallet } from '@/api/wallet'

// 流水类型映射表
const typeMap = {
  // 充值类型
  1: '充值',
  // 提现类型
  2: '提现',
  // 买入支出类型
  3: '买入支出',
  // 卖出收入类型
  4: '卖出收入',
  // 退款类型
  5: '退款',
  // 手续费类型
  6: '手续费'
}

// 钱包数据响应式对象
const wallet = ref({
  // 可用余额
  balance: 0,
  // 冻结金额
  frozenAmount: 0,
  // 累计充值
  totalRecharge: 0,
  // 累计提现
  totalWithdraw: 0,
  // 累计收入
  totalIncome: 0,
  // 累计支出
  totalExpense: 0
})

// 表格加载状态
const loading = ref(false)
// 流水列表数据
const logList = ref([])
// 流水总条数
const total = ref(0)
// 查询参数（分页+类型筛选）
const queryParams = reactive({
  // 当前页码
  pageNum: 1,
  // 每页条数
  pageSize: 10,
  // 流水类型筛选
  type: undefined
})

// 金额格式化函数，保留两位小数
const formatMoney = (v) => {
  // 空值返回0.00，否则格式化为两位小数
  return v === null || v === undefined ? '0.00' : Number(v).toFixed(2)
}

// 加载钱包数据
const loadWallet = async () => {
  // 调用获取钱包接口
  const res = await getMyWallet()
  // 更新钱包数据
  wallet.value = res.data || wallet.value
}

// 获取流水列表
const getLogs = async () => {
  // 开启加载状态
  loading.value = true
  try {
    // 调用获取流水接口，传入查询参数
    const res = await getMyWalletLogs(queryParams)
    // 更新流水列表
    logList.value = res.rows || []
    // 更新总条数
    total.value = res.total || 0
  } finally {
    // 关闭加载状态
    loading.value = false
  }
}

// ---------- 充值 / 提现 ----------
// 充值对话框显示状态
const rechargeOpen = ref(false)
// 提现对话框显示状态
const withdrawOpen = ref(false)
// 提交中状态（防重复点击）
const submitting = ref(false)
// 充值表单数据
const rechargeForm = reactive({ amount: undefined })
// 提现表单数据
const withdrawForm = reactive({ amount: undefined })

// 打开充值对话框
const openRecharge = () => {
  // 重置充值金额
  rechargeForm.amount = undefined
  // 显示充值对话框
  rechargeOpen.value = true
}

// 打开提现对话框
const openWithdraw = () => {
  // 重置提现金额
  withdrawForm.amount = undefined
  // 显示提现对话框
  withdrawOpen.value = true
}

// 提交充值
const submitRecharge = async () => {
  // 校验金额是否有效
  if (!rechargeForm.amount || rechargeForm.amount <= 0) {
    // 提示输入金额
    ElMessage.warning('请输入充值金额')
    return
  }
  // 开启提交状态
  submitting.value = true
  try {
    // 调用充值接口
    await rechargeWallet({ amount: rechargeForm.amount })
    // 提示充值成功
    ElMessage.success('充值成功')
    // 关闭充值对话框
    rechargeOpen.value = false
    // 重新加载钱包数据
    loadWallet()
    // 重新加载流水
    getLogs()
  } finally {
    // 关闭提交状态
    submitting.value = false
  }
}

// 提交提现
const submitWithdraw = async () => {
  // 校验金额是否有效
  if (!withdrawForm.amount || withdrawForm.amount <= 0) {
    // 提示输入金额
    ElMessage.warning('请输入提现金额')
    return
  }
  // 开启提交状态
  submitting.value = true
  try {
    // 调用提现接口
    await withdrawWallet({ amount: withdrawForm.amount })
    // 提示提现成功
    ElMessage.success('提现成功')
    // 关闭提现对话框
    withdrawOpen.value = false
    // 重新加载钱包数据
    loadWallet()
    // 重新加载流水
    getLogs()
  } finally {
    // 关闭提交状态
    submitting.value = false
  }
}

// 组件挂载时初始化数据
onMounted(() => {
  // 加载钱包余额数据
  loadWallet()
  // 加载流水列表
  getLogs()
})
// keep-alive缓存激活时重新加载数据
onActivated(() => {
  loadWallet()
  getLogs()
})
</script>

<style scoped>
/* 钱包页面根样式 */
.wallet-page {
  /* 背景色变量 */
  --bg: #0d1220;
  /* 卡片背景变量 */
  --card: #141b2d;
  /* 卡片悬停背景变量 */
  --card-hover: #1a2338;
  /* 边框颜色变量 */
  --border: #232e45;
  /* 主文本颜色变量 */
  --text: #e9edf6;
  /* 次要文本颜色变量 */
  --muted: #8f9ab3;
  /* 绿色（收入/正向）变量 */
  --green: #3ddc97;
  /* 红色（支出/负向）变量 */
  --red: #ff6b5e;
  /* 最小高度撑满视口 */
  min-height: calc(100vh - 84px);
  /* 多层渐变背景 */
  background:
    radial-gradient(1000px 400px at 80% -10%, rgba(61, 220, 151, 0.07), transparent 60%),
    radial-gradient(800px 300px at 10% 0%, rgba(77, 159, 255, 0.06), transparent 60%),
    var(--bg);
  /* 内边距 */
  padding: 24px 28px 32px;
  /* Element Plus 暗色变量覆盖 */
  --el-bg-color: var(--card);
  --el-bg-color-overlay: var(--card-hover);
  --el-border-color: var(--border);
  --el-border-color-light: var(--border);
  --el-text-color-primary: var(--text);
  --el-text-color-regular: #c2cad9;
  --el-text-color-secondary: var(--muted);
  --el-fill-color-blank: #0d1220;
  --el-input-bg-color: #0d1220;
  --el-input-border-color: var(--border);
  --el-input-hover-border-color: #35507a;
  --el-input-placeholder-color: #5b6680;
  /* 主文本颜色 */
  color: var(--text);
}

/* ---------- 页头 ---------- */
/* 页头区域样式 */
.wallet-hero {
  /* 内边距 */
  padding: 8px 4px 18px;
}
/* 英文标识样式 */
.hero-eyebrow {
  /* 字号 */
  font-size: 12px;
  /* 字间距 */
  letter-spacing: 4px;
  /* 次要文本颜色 */
  color: var(--muted);
  /* 大写转换 */
  text-transform: uppercase;
}
/* 主标题样式 */
.hero-title {
  /* 外边距 */
  margin: 6px 0 4px;
  /* 字号 */
  font-size: 30px;
  /* 字重 */
  font-weight: 800;
  /* 字间距 */
  letter-spacing: 1px;
  /* 主文本颜色 */
  color: var(--text);
}
/* 副标题样式 */
.hero-sub {
  /* 无外边距 */
  margin: 0;
  /* 字号 */
  font-size: 13px;
  /* 次要文本颜色 */
  color: var(--muted);
}

/* ---------- 余额卡片 ---------- */
/* 余额卡片容器 */
.balance-card {
  /* 弹性布局 */
  display: flex;
  /* 两端对齐 */
  justify-content: space-between;
  /* 垂直居中 */
  align-items: center;
  /* 渐变背景 */
  background: linear-gradient(135deg, #16233c, #141b2d 60%);
  /* 边框 */
  border: 1px solid var(--border);
  /* 圆角 */
  border-radius: 12px;
  /* 内边距 */
  padding: 26px 30px;
  /* 下外边距 */
  margin-bottom: 16px;
}
/* 余额标签样式 */
.balance-label {
  /* 字号 */
  font-size: 12px;
  /* 次要文本颜色 */
  color: var(--muted);
  /* 字间距 */
  letter-spacing: 1px;
}
/* 余额数字容器 */
.balance-num {
  /* 上外边距 */
  margin-top: 8px;
  /* 弹性布局 */
  display: flex;
  /* 基线对齐 */
  align-items: baseline;
}
/* 货币符号样式 */
.balance-num .cur {
  /* 绿色 */
  color: var(--green);
  /* 字号 */
  font-size: 18px;
  /* 右外边距 */
  margin-right: 4px;
}
/* 余额数字样式 */
.balance-num .num {
  /* 绿色 */
  color: var(--green);
  /* 大字号 */
  font-size: 42px;
  /* 粗体 */
  font-weight: 800;
  /* 等宽数字 */
  font-variant-numeric: tabular-nums;
  /* 字间距微调 */
  letter-spacing: -0.5px;
}
/* 冻结金额信息区 */
.balance-frozen {
  /* 上外边距 */
  margin-top: 12px;
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 间距 */
  gap: 10px;
  /* 字号 */
  font-size: 12px;
  /* 黄色 */
  color: #e6a23c;
}
/* 冻结金额数字 */
.frozen-num {
  /* 橙黄色 */
  color: #ffb84d;
  /* 半粗体 */
  font-weight: 600;
  /* 等宽数字 */
  font-variant-numeric: tabular-nums;
}
/* 冻结提示文字 */
.frozen-tip {
  /* 暗灰色 */
  color: #4a5570;
}
/* 操作按钮容器 */
.balance-actions {
  /* 弹性布局 */
  display: flex;
  /* 按钮间距 */
  gap: 12px;
}
/* 通用按钮样式 */
.btn-primary,
.btn-ghost {
  /* 无边框 */
  border: none;
  /* 圆角 */
  border-radius: 8px;
  /* 内边距 */
  padding: 11px 34px;
  /* 字号 */
  font-size: 14px;
  /* 手型光标 */
  cursor: pointer;
  /* 过渡动画 */
  transition: all 0.18s;
}
/* 主要按钮（充值） */
.btn-primary {
  /* 绿色渐变背景 */
  background: linear-gradient(135deg, #3ddc97, #23b87a);
  /* 深色文字 */
  color: #08131a;
  /* 粗体 */
  font-weight: 700;
}
/* 主要按钮悬停 */
.btn-primary:hover {
  /* 提高亮度 */
  filter: brightness(1.1);
}
/* 幽灵按钮（提现） */
.btn-ghost {
  /* 透明背景 */
  background: transparent;
  /* 主文本颜色 */
  color: var(--text);
  /* 边框 */
  border: 1px solid var(--border);
}
/* 幽灵按钮悬停 */
.btn-ghost:hover {
  /* 边框高亮 */
  border-color: #35507a;
  /* 保持文本颜色 */
  color: var(--text);
}

/* ---------- 统计 ---------- */
/* 统计卡片行 */
.stats-row {
  /* 四列网格 */
  display: grid;
  /* 四等分列 */
  grid-template-columns: repeat(4, 1fr);
  /* 间距 */
  gap: 14px;
  /* 下外边距 */
  margin-bottom: 18px;
}
/* 单个统计卡片 */
.stat-card {
  /* 卡片背景 */
  background: var(--card);
  /* 边框 */
  border: 1px solid var(--border);
  /* 圆角 */
  border-radius: 10px;
  /* 内边距 */
  padding: 16px 18px;
}
/* 统计标签 */
.stat-label {
  /* 字号 */
  font-size: 12px;
  /* 次要文本颜色 */
  color: var(--muted);
}
/* 统计数值 */
.stat-value {
  /* 上外边距 */
  margin-top: 8px;
  /* 字号 */
  font-size: 20px;
  /* 粗体 */
  font-weight: 700;
  /* 等宽数字 */
  font-variant-numeric: tabular-nums;
  /* 主文本颜色 */
  color: var(--text);
}
/* 绿色统计数值 */
.stat-green {
  /* 绿色 */
  color: var(--green);
}
/* 红色统计数值 */
.stat-red {
  /* 红色 */
  color: var(--red);
}

/* ---------- 流水 ---------- */
/* 流水面板容器 */
.logs-panel {
  /* 卡片背景 */
  background: var(--card);
  /* 边框 */
  border: 1px solid var(--border);
  /* 圆角 */
  border-radius: 10px;
  /* 内边距 */
  padding: 16px 18px;
}
/* 流水头部区域 */
.logs-head {
  /* 弹性布局 */
  display: flex;
  /* 两端对齐 */
  justify-content: space-between;
  /* 垂直居中 */
  align-items: center;
  /* 下外边距 */
  margin-bottom: 14px;
}
/* 流水标题 */
.logs-title {
  /* 字号 */
  font-size: 15px;
  /* 粗体 */
  font-weight: 700;
}
/* 流水筛选下拉框 */
.logs-filter {
  /* 固定宽度 */
  width: 140px;
}
/* 类型标签芯片 */
.type-chip {
  /* 行内块 */
  display: inline-block;
  /* 字号 */
  font-size: 12px;
  /* 内边距 */
  padding: 3px 10px;
  /* 圆角 */
  border-radius: 20px;
  /* 边框 */
  border: 1px solid;
}
/* 充值类型样式 */
.type-1 {
  /* 绿色文字 */
  color: #3ddc97;
  /* 绿色半透明边框 */
  border-color: rgba(61, 220, 151, 0.35);
  /* 绿色半透明背景 */
  background: rgba(61, 220, 151, 0.08);
}
/* 提现类型样式 */
.type-2 {
  /* 橙黄色文字 */
  color: #ffb84d;
  /* 橙黄色半透明边框 */
  border-color: rgba(255, 184, 77, 0.35);
  /* 橙黄色半透明背景 */
  background: rgba(255, 184, 77, 0.08);
}
/* 买入支出类型样式 */
.type-3 {
  /* 红色文字 */
  color: #ff6b5e;
  /* 红色半透明边框 */
  border-color: rgba(255, 107, 94, 0.35);
  /* 红色半透明背景 */
  background: rgba(255, 107, 94, 0.08);
}
/* 卖出收入类型样式 */
.type-4 {
  /* 绿色文字 */
  color: #3ddc97;
  /* 绿色半透明边框 */
  border-color: rgba(61, 220, 151, 0.35);
  /* 绿色半透明背景 */
  background: rgba(61, 220, 151, 0.08);
}
/* 退款类型样式 */
.type-5 {
  /* 蓝色文字 */
  color: #4d9fff;
  /* 蓝色半透明边框 */
  border-color: rgba(77, 159, 255, 0.35);
  /* 蓝色半透明背景 */
  background: rgba(77, 159, 255, 0.08);
}
/* 手续费类型样式 */
.type-6 {
  /* 灰色文字 */
  color: #8f9ab3;
  /* 灰色半透明边框 */
  border-color: rgba(143, 154, 179, 0.35);
  /* 灰色半透明背景 */
  background: rgba(143, 154, 179, 0.08);
}
/* 金额通用样式 */
.amount {
  /* 粗体 */
  font-weight: 700;
  /* 等宽数字 */
  font-variant-numeric: tabular-nums;
}
/* 收入金额（正数） */
.amount-in {
  /* 绿色 */
  color: var(--green);
}
/* 支出金额（负数） */
.amount-out {
  /* 红色 */
  color: var(--red);
}
/* 变动后余额样式 */
.balance-after {
  /* 次要文本颜色 */
  color: var(--muted);
  /* 等宽数字 */
  font-variant-numeric: tabular-nums;
}
/* 关联订单号样式 */
.order-no {
  /* 暗灰色 */
  color: #4a5570;
  /* 小字号 */
  font-size: 12px;
}

/* 分页容器 */
.pager {
  /* 上外边距 */
  margin-top: 16px;
  /* 弹性布局 */
  display: flex;
  /* 右对齐 */
  justify-content: flex-end;
}

/* ---------- 对话框 ---------- */
/* 对话框提示文字 */
.dialog-tip {
  /* 字号 */
  font-size: 12px;
  /* 次要文本颜色 */
  color: var(--muted);
  /* 下外边距 */
  margin-bottom: 14px;
}
/* 金额输入框宽度撑满 */
.amount-input {
  /* 宽度100% */
  width: 100%;
}
</style>

<style>
/* 亮色主题下余额卡片覆盖 */
body.user-surface .wallet-page .balance-card {
  /* 白色背景 */
  background: #ffffff !important;
  /* 浅蓝边框 */
  border: 1px solid #d9e9fb;
  /* 小圆角 */
  border-radius: 4px;
  /* 轻微阴影 */
  box-shadow: 0 2px 8px rgba(31, 45, 61, 0.04);
}
/* 亮色主题下标签和提示文字颜色覆盖 */
body.user-surface .wallet-page .balance-label,
body.user-surface .wallet-page .frozen-label,
body.user-surface .wallet-page .frozen-tip { color: #e6a23c !important; }
/* 亮色主题下冻结金额数字颜色覆盖 */
body.user-surface .wallet-page .frozen-num { color: #e6a23c !important; }
/* 亮色主题下余额数字绿色覆盖 */
body.user-surface .wallet-page .balance-num .cur,
body.user-surface .wallet-page .balance-num .num { color: #2d9a6a !important; }
/* 亮色主题下充值按钮样式覆盖 */
body.user-surface .wallet-page .btn-primary { background: #2d9a6a !important; color: #fff !important; }
/* 亮色主题下提现按钮样式覆盖 */
body.user-surface .wallet-page .btn-ghost { color: #526275 !important; border-color: #d6dee8 !important; }
/* 亮色主题下统计数值颜色 */
body.user-surface .wallet-page .stat-value { color: #526275; }
/* 亮色主题下绿色统计值 */
body.user-surface .wallet-page .stat-green { color: #2d9a6a; }
/* 亮色主题下红色统计值 */
body.user-surface .wallet-page .stat-red { color: #d95550; }
/* 亮色主题下流水标题颜色 */
body.user-surface .wallet-page .logs-title { color: #1f2d3d; }
/* 亮色主题下弹出层背景覆盖 */
body.user-surface .wallet-page .el-popper,
body.user-surface .wallet-page .el-select-dropdown { background: #fff !important; border-color: #e3e8ef !important; }
/* 亮色主题下拉选项文字颜色 */
body.user-surface .wallet-page .el-select-dropdown__item { color: #526275 !important; }
/* 亮色主题下拉选项悬停背景 */
body.user-surface .wallet-page .el-select-dropdown__item.is-hovering { background: #f0f5fb !important; }
/* 亮色主题下拉选中项颜色 */
body.user-surface .wallet-page .el-select-dropdown__item.is-selected { color: #2f80ed !important; }

/* 暗色分页（非scoped覆盖组件样式） */
/* 分页容器背景透明 */
.wallet-page .pager .pagination-container {
  /* 透明背景 */
  background: transparent !important;
}
</style>
