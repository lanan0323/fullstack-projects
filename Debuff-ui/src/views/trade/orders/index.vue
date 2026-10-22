<template>
  <!-- 订单页面根容器 -->
  <div class="orders-page">
    <!-- 页头 -->
    <!-- 页头区域容器 -->
    <div class="ord-hero">
      <!-- 英文小标题装饰文字 -->
      <div class="hero-eyebrow">MY TRADES</div>
      <!-- 页面主标题 -->
      <h1 class="hero-title">我的交易</h1>
      <!-- 副标题：显示订单总数和说明 -->
      <p class="hero-sub">共 {{ total }} 笔订单 · 买家和卖家双向交易记录</p>
    </div>

    <!-- 统计卡 -->
    <!-- 统计卡片行容器，展示各状态订单数量 -->
    <div class="stat-row">
      <!-- 全部订单统计卡片 -->
      <div class="stat-card">
        <!-- 显示全部订单数量 -->
        <span class="stat-num">{{ stats.all }}</span>
        <!-- 全部订单标签文字 -->
        <span class="stat-label">全部订单</span>
      </div>
      <!-- 待确认订单统计卡片 -->
      <div class="stat-card">
        <!-- 显示待确认订单数量，橙色高亮 -->
        <span class="stat-num" style="color: #ffb454">{{ stats.pending }}</span>
        <!-- 待确认标签文字 -->
        <span class="stat-label">待确认</span>
      </div>
      <!-- 已完成订单统计卡片 -->
      <div class="stat-card">
        <!-- 显示已完成订单数量，绿色高亮 -->
        <span class="stat-num" style="color: #3ddc97">{{ stats.done }}</span>
        <!-- 已完成标签文字 -->
        <span class="stat-label">已完成</span>
      </div>
      <!-- 已取消订单统计卡片 -->
      <div class="stat-card">
        <!-- 显示已取消订单数量，灰色高亮 -->
        <span class="stat-num" style="color: #8f9ab3">{{ stats.canceled }}</span>
        <!-- 已取消标签文字 -->
        <span class="stat-label">已取消</span>
      </div>
    </div>

    <!-- 筛选区 -->
    <!-- 筛选面板容器，用于按方向和状态过滤订单 -->
    <div class="filter-panel">
      <!-- 筛选条件行容器 -->
      <div class="filter-row">
        <!-- 方向筛选项 -->
        <div class="filter-item">
          <!-- 方向筛选标签 -->
          <span class="filter-label">方向</span>
          <!-- 方向下拉选择框，绑定查询参数的direction字段 -->
          <el-select v-model="queryParams.direction" placeholder="全部" clearable class="dark-select"
            popper-class="debuff-popper">
            <!-- 选项：我买的，值为1 -->
            <el-option label="我买的" :value="1" />
            <!-- 选项：我卖的，值为2 -->
            <el-option label="我卖的" :value="2" />
          </el-select>
        </div>
        <!-- 状态筛选项 -->
        <div class="filter-item">
          <!-- 状态筛选标签 -->
          <span class="filter-label">状态</span>
          <!-- 状态下拉选择框，绑定查询参数的status字段，选项从statusMap动态生成 -->
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable class="dark-select"
            popper-class="debuff-popper">
            <!-- 遍历statusMap生成各状态选项 -->
            <el-option v-for="(s, key) in statusMap" :key="key" :label="s.label" :value="Number(key)" />
          </el-select>
        </div>
        <!-- 筛选操作按钮区域 -->
        <div class="filter-item filter-actions">
          <!-- 搜索按钮，点击触发查询 -->
          <button class="btn-primary" @click="handleQuery">搜索</button>
          <!-- 重置按钮，点击清空筛选条件并重新查询 -->
          <button class="btn-ghost" @click="resetQuery">重置</button>
        </div>
      </div>
    </div>

    <!-- 表格 -->
    <!-- 表格容器，展示订单列表数据 -->
    <div class="table-wrap">
      <!-- Element Plus表格组件，绑定加载状态和订单数据 -->
      <el-table v-loading="loading" :data="orderList" class="debuff-table">
        <!-- 订单编号列，显示orderNo字段 -->
        <el-table-column label="订单编号" align="center" width="200" prop="orderNo" show-overflow-tooltip />
        <!-- 方向列，显示买家/卖家身份 -->
        <el-table-column label="方向" align="center" width="90">
          <!-- 自定义方向列内容模板 -->
          <template #default="scope">
            <!-- 方向标签，根据是否为买家显示不同样式 -->
            <span class="dir-chip" :class="isBuyer(scope.row) ? 'dir-buy' : 'dir-sell'">
              <!-- 显示"我买"或"我卖"文字 -->
              {{ isBuyer(scope.row) ? '我买' : '我卖' }}
            </span>
          </template>
        </el-table-column>
        <!-- 饰品ID列，显示skinId字段 -->
        <el-table-column label="饰品ID" align="center" prop="skinId" width="90" />
        <!-- 买家列，显示买家名称或ID -->
        <el-table-column label="买家" align="center" min-width="110">
          <!-- 自定义买家列内容模板 -->
          <template #default="scope">
            <!-- 买家信息单元格容器 -->
            <span class="user-cell">
              <!-- 显示买家名称，若无则显示默认"用户+ID" -->
              {{ scope.row.buyerName || '用户' + scope.row.buyerId }}
              <!-- 如果当前用户是买家，显示"我"标签 -->
              <span v-if="isBuyer(scope.row)" class="me-tag">我</span>
            </span>
          </template>
        </el-table-column>
        <!-- 卖家列，显示卖家名称或ID -->
        <el-table-column label="卖家" align="center" min-width="110">
          <!-- 自定义卖家列内容模板 -->
          <template #default="scope">
            <!-- 卖家信息单元格容器 -->
            <span class="user-cell">
              <!-- 显示卖家名称，若无则显示默认"用户+ID" -->
              {{ scope.row.sellerName || '用户' + scope.row.sellerId }}
              <!-- 如果当前用户是卖家（非买家），显示"我"标签 -->
              <span v-if="!isBuyer(scope.row)" class="me-tag">我</span>
            </span>
          </template>
        </el-table-column>
        <!-- 成交价格列，显示格式化后的价格 -->
        <el-table-column label="成交价格" align="center" width="110">
          <!-- 自定义成交价格列内容模板 -->
          <template #default="scope">
            <!-- 价格数字，带人民币符号和两位小数 -->
            <span class="price-num">¥{{ formatMoney(scope.row.price) }}</span>
          </template>
        </el-table-column>
        <!-- 平台手续费列 -->
        <el-table-column label="平台手续费" align="center" width="100">
          <!-- 自定义手续费列内容模板 -->
          <template #default="scope">
            <!-- 显示格式化后的平台手续费金额 -->
            <span class="muted-text">¥{{ formatMoney(scope.row.platformFee) }}</span>
          </template>
        </el-table-column>
        <!-- 卖家实收列 -->
        <el-table-column label="卖家实收" align="center" width="100">
          <!-- 自定义卖家实收列内容模板 -->
          <template #default="scope">
            <!-- 显示格式化后的卖家实际收入金额 -->
            <span class="muted-text">¥{{ formatMoney(scope.row.sellerIncome) }}</span>
          </template>
        </el-table-column>
        <!-- 状态列，显示订单当前状态 -->
        <el-table-column label="状态" align="center" width="90">
          <!-- 自定义状态列内容模板 -->
          <template #default="scope">
            <!-- 状态标签，根据status值动态添加样式类 -->
            <span class="status-chip" :class="'st-' + scope.row.status">
              <!-- 显示状态对应的中文标签 -->
              {{ statusMap[scope.row.status]?.label }}
            </span>
          </template>
        </el-table-column>
        <!-- 下单时间列，显示格式化后的创建时间 -->
        <el-table-column label="下单时间" align="center" width="150" prop="createTime">
          <!-- 自定义下单时间列内容模板 -->
          <template #default="scope">
            <!-- 显示格式化后的时间文本 -->
            <span class="muted-text">{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <!-- 操作列，提供确认收货、取消订单等操作按钮 -->
        <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
          <!-- 自定义操作列内容模板 -->
          <template #default="scope">
            <!-- 当订单状态为待确认(0)时显示操作按钮 -->
            <template v-if="scope.row.status === 0">
              <!-- 当当前用户是买家时显示操作按钮 -->
              <template v-if="isBuyer(scope.row)">
                <!-- 确认收货按钮，点击触发确认收货逻辑 -->
                <button class="link-btn" @click="handleConfirm(scope.row)">确认收货</button>
                <!-- 取消订单按钮，点击触发取消订单逻辑，红色危险样式 -->
                <button class="link-btn danger" @click="handleCancel(scope.row)">取消订单</button>
              </template>
              <!-- 当当前用户是卖家时，显示等待买家确认的提示文字 -->
              <span v-else class="muted-text">等待买家确认</span>
            </template>
            <!-- 当订单状态不是待确认时，显示已完成或已取消的文字 -->
            <span v-else class="muted-text">{{ scope.row.status === 1 ? '已完成' : '已取消' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页器容器 -->
    <div class="pager">
      <!-- 分页组件，总数大于0时显示，绑定页码和每页条数，切换时重新获取列表 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>
  </div>
</template>

<script setup name="Orders">
// 引入Vue组合式API：响应式引用、响应式对象、生命周期钩子
import { ref, reactive, onMounted, onActivated } from 'vue'
// 引入Element Plus的消息提示和确认弹窗组件
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入订单相关API：获取我的订单、确认订单、取消订单
import { myOrders, confirmOrder, cancelOrder } from '@/api/order'
// 引入用户状态管理模块
import useUserStore from '@/store/modules/user'

// 获取用户状态存储实例，用于判断当前登录用户身份
const userStore = useUserStore()

// 表格加载状态，控制loading动画显示
const loading = ref(false)
// 订单列表数据，存储当前页的订单数组
const orderList = ref([])
// 订单总数，用于分页器显示
const total = ref(0)
// 各状态订单统计数据：全部、待确认、已完成、已取消
const stats = reactive({ all: 0, pending: 0, done: 0, canceled: 0 })

// 订单状态映射表，定义各状态码对应的标签和颜色
const statusMap = {
  // 状态0：待确认，橙色
  0: { label: '待确认', color: '#ffb454' },
  // 状态1：已完成，绿色
  1: { label: '已完成', color: '#3ddc97' },
  // 状态2：已取消，灰色
  2: { label: '已取消', color: '#8f9ab3' }
}

// 查询参数响应式对象，包含分页和筛选条件
const queryParams = reactive({
  // 当前页码，默认第1页
  pageNum: 1,
  // 每页显示条数，默认10条
  pageSize: 10,
  // 状态筛选条件，默认不筛选
  status: undefined,
  // 方向筛选条件，默认不筛选
  direction: undefined
})

// 判断当前行是否为买家订单，通过比较买家ID和当前登录用户ID
const isBuyer = (row) => row.buyerId === userStore.id

// 格式化金额工具函数，将数值转为两位小数字符串
const formatMoney = (v) => {
  // 如果值为null或undefined返回占位符'--'，否则转为数字保留两位小数
  return v === null || v === undefined ? '--' : Number(v).toFixed(2)
}

// 获取订单列表的异步方法
const getList = async () => {
  // 开启加载状态，显示表格loading动画
  loading.value = true
  try {
    // 调用API获取我的订单列表，传入查询参数
    const res = await myOrders(queryParams)
    // 将返回的订单行数据赋值给orderList，若无数据则为空数组
    orderList.value = res.rows || []
    // 将返回的总数赋值给total，若无数据则为0
    total.value = res.total || 0
  } finally {
    // 无论请求成功或失败，都关闭加载状态
    loading.value = false
  }
}

// 统计全量订单（不传分页参数返回全部）
// 加载订单统计数据的异步方法，获取全部订单并计算各状态数量
const loadStats = async () => {
  // 调用API获取全部订单（不传分页参数）
  const res = await myOrders({})
  // 获取返回的订单行数据，若无数据则为空数组
  const rows = res.rows || []
  // 统计全部订单数量
  stats.all = rows.length
  // 筛选状态为0（待确认）的订单数量
  stats.pending = rows.filter(r => r.status === 0).length
  // 筛选状态为1（已完成）的订单数量
  stats.done = rows.filter(r => r.status === 1).length
  // 筛选状态为2（已取消）的订单数量
  stats.canceled = rows.filter(r => r.status === 2).length
}

// 处理搜索按钮点击事件
const handleQuery = () => {
  // 重置页码为第1页，避免筛选后页码越界
  queryParams.pageNum = 1
  // 调用获取列表方法执行查询
  getList()
}

// 重置筛选条件并重新查询
const resetQuery = () => {
  // 清空状态筛选条件
  queryParams.status = undefined
  // 清空方向筛选条件
  queryParams.direction = undefined
  // 重新执行查询（会重置页码并获取列表）
  handleQuery()
}

// ---------- 确认收货 ----------
// 处理确认收货操作的函数
const handleConfirm = (row) => {
  // 弹出确认对话框，提示用户确认收货及款项流向
  ElMessageBox.confirm(
    `确认收到该饰品？确认后 ¥${formatMoney(row.price)} 将转入卖家账户，商品将进入你的库存。`,
    '确认收货',
    { confirmButtonText: '确认收货', cancelButtonText: '再想想', type: 'warning' }
  // 用户点击确认后执行
  ).then(async () => {
    // 调用确认订单API，传入订单ID
    const res = await confirmOrder(row.id)
    // 判断接口返回状态码是否为200（成功）
    if (res.code === 200) {
      // 显示成功提示消息
      ElMessage.success('交易完成，饰品已放入你的库存')
      // 重新获取订单列表以刷新数据
      getList()
      // 重新加载统计数据以更新顶部统计卡片
      loadStats()
    } else {
      // 操作失败时显示错误提示消息
      ElMessage.error(res.msg || '操作失败')
      // 失败时也刷新列表保持数据同步
      getList()
    }
  // 用户点击取消时不做任何处理
  }).catch(() => {})
}

// ---------- 取消订单 ----------
// 处理取消订单操作的函数
const handleCancel = (row) => {
  // 弹出确认对话框，提示用户取消订单后款项退回和商品重新上架
  ElMessageBox.confirm(
    `取消订单后 ¥${formatMoney(row.price)} 将原路退回你的余额，商品会重新上架。确定取消？`,
    '取消订单',
    { confirmButtonText: '确定取消', cancelButtonText: '再想想', type: 'warning' }
  // 用户点击确认后执行
  ).then(async () => {
    // 调用取消订单API，传入订单ID
    const res = await cancelOrder(row.id)
    // 判断接口返回状态码是否为200（成功）
    if (res.code === 200) {
      // 显示成功提示消息
      ElMessage.success('订单已取消，款项已退回')
      // 重新获取订单列表以刷新数据
      getList()
      // 重新加载统计数据以更新顶部统计卡片
      loadStats()
    } else {
      // 操作失败时显示错误提示消息
      ElMessage.error(res.msg || '操作失败')
      // 失败时也刷新列表保持数据同步
      getList()
    }
  // 用户点击取消时不做任何处理
  }).catch(() => {})
}

// 组件挂载完成时的生命周期钩子
onMounted(() => {
  // 加载订单统计数据
  loadStats()
  // 加载订单列表数据
  getList()
})
// keep-alive缓存激活时重新加载数据
onActivated(() => {
  loadStats()
  getList()
})
</script>

<!-- scoped样式：仅作用于当前组件 -->
<style scoped>
/* 订单页面根容器样式，定义暗色主题变量和整体背景 */
.orders-page {
  /* 页面背景色变量 */
  --bg: #0d1220;
  /* 卡片背景色变量 */
  --card: #141b2d;
  /* 卡片悬停背景色变量 */
  --card-hover: #1a2338;
  /* 边框颜色变量 */
  --border: #232e45;
  /* 主文本颜色变量 */
  --text: #e9edf6;
  /* 次要/弱化文本颜色变量 */
  --muted: #8f9ab3;
  /* 最小高度为视口高度减去顶部导航高度 */
  min-height: calc(100vh - 84px);
  /* 背景使用径向渐变叠加纯色，营造微光效果 */
  background:
    radial-gradient(900px 360px at 90% -10%, rgba(255, 180, 84, 0.06), transparent 60%),
    var(--bg);
  /* 内边距：上24 左右28 下32 */
  padding: 24px 28px 32px;
  /* 覆盖Element Plus背景色变量为卡片色 */
  --el-bg-color: var(--card);
  /* 覆盖Element Plus覆盖层背景色变量 */
  --el-bg-color-overlay: var(--card-hover);
  /* 覆盖Element Plus边框颜色变量 */
  --el-border-color: var(--border);
  /* 覆盖Element Plus主文本颜色变量 */
  --el-text-color-primary: var(--text);
  /* 覆盖Element Plus常规文本颜色变量 */
  --el-text-color-regular: #c2cad9;
  /* 覆盖Element Plus次要文本颜色变量 */
  --el-text-color-secondary: var(--muted);
  /* 覆盖Element Plus空白填充色变量 */
  --el-fill-color-blank: #0d1220;
  /* 覆盖Element Plus输入框背景色变量 */
  --el-input-bg-color: #0d1220;
  /* 覆盖Element Plus输入框边框颜色变量 */
  --el-input-border-color: var(--border);
  /* 覆盖Element Plus输入框悬停边框颜色变量 */
  --el-input-hover-border-color: #35507a;
  /* 覆盖Element Plus输入框占位符颜色变量 */
  --el-input-placeholder-color: #5b6680;
  /* 设置页面文本颜色 */
  color: var(--text);
}

/* 页头区域样式 */
.ord-hero {
  /* 内边距：上8 左4 下18 */
  padding: 8px 4px 18px;
}
/* 英文小标题装饰文字样式 */
.hero-eyebrow {
  /* 字体大小12px */
  font-size: 12px;
  /* 字母间距4px，增加装饰感 */
  letter-spacing: 4px;
  /* 使用弱化颜色 */
  color: var(--muted);
  /* 转为大写显示 */
  text-transform: uppercase;
}
/* 页面主标题样式 */
.hero-title {
  /* 外边距：上6 左右0 下4 */
  margin: 6px 0 4px;
  /* 字体大小30px */
  font-size: 30px;
  /* 字重800，极粗 */
  font-weight: 800;
  /* 字母间距1px */
  letter-spacing: 1px;
}
/* 副标题样式 */
.hero-sub {
  /* 清除默认外边距 */
  margin: 0;
  /* 字体大小13px */
  font-size: 13px;
  /* 使用弱化颜色 */
  color: var(--muted);
}

/* 统计卡 */
/* 统计卡片行容器，使用网格布局 */
.stat-row {
  /* 使用网格布局 */
  display: grid;
  /* 4列等宽网格 */
  grid-template-columns: repeat(4, 1fr);
  /* 网格间距14px */
  gap: 14px;
  /* 底部外边距18px */
  margin-bottom: 18px;
}
/* 单个统计卡片样式 */
.stat-card {
  /* 卡片背景色 */
  background: var(--card);
  /* 卡片边框 */
  border: 1px solid var(--border);
  /* 圆角10px */
  border-radius: 10px;
  /* 内边距：上下16 左右20 */
  padding: 16px 20px;
  /* 使用弹性布局 */
  display: flex;
  /* 垂直方向基线对齐 */
  align-items: baseline;
  /* 子元素间距10px */
  gap: 10px;
}
/* 统计数字样式 */
.stat-num {
  /* 字体大小28px */
  font-size: 28px;
  /* 字重800，极粗 */
  font-weight: 800;
  /* 使用等宽数字字体，保证数字对齐 */
  font-variant-numeric: tabular-nums;
}
/* 统计标签样式 */
.stat-label {
  /* 字体大小13px */
  font-size: 13px;
  /* 使用弱化颜色 */
  color: var(--muted);
}

/* 筛选区 */
/* 筛选面板容器样式 */
.filter-panel {
  /* 面板背景色 */
  background: var(--card);
  /* 面板边框 */
  border: 1px solid var(--border);
  /* 圆角10px */
  border-radius: 10px;
  /* 内边距：上下14 左右18 */
  padding: 14px 18px;
  /* 底部外边距16px */
  margin-bottom: 16px;
}
/* 筛选条件行容器样式 */
.filter-row {
  /* 使用弹性布局 */
  display: flex;
  /* 允许换行 */
  flex-wrap: wrap;
  /* 间距：行14px 列26px */
  gap: 14px 26px;
  /* 垂直居中对齐 */
  align-items: center;
}
/* 单个筛选项容器样式 */
.filter-item {
  /* 使用弹性布局 */
  display: flex;
  /* 垂直居中对齐 */
  align-items: center;
  /* 子元素间距8px */
  gap: 8px;
}
/* 筛选标签文字样式 */
.filter-label {
  /* 字体大小12px */
  font-size: 12px;
  /* 使用弱化颜色 */
  color: var(--muted);
  /* 禁止文字换行 */
  white-space: nowrap;
}
/* 暗色主题下拉选择框宽度 */
.dark-select { width: 140px; }
/* 筛选操作按钮区域靠右对齐 */
.filter-actions { margin-left: auto; }
/* 主按钮和幽灵按钮的共用基础样式 */
.btn-primary,
.btn-ghost {
  /* 去除默认边框 */
  border: none;
  /* 圆角6px */
  border-radius: 6px;
  /* 内边距：上下9 左右22 */
  padding: 9px 22px;
  /* 字体大小13px */
  font-size: 13px;
  /* 鼠标指针样式 */
  cursor: pointer;
  /* 过渡动画0.18秒 */
  transition: all 0.18s;
}
/* 主按钮（搜索按钮）样式 */
.btn-primary {
  /* 蓝色渐变背景 */
  background: linear-gradient(135deg, #4d9fff, #3a7bd5);
  /* 白色文字 */
  color: #fff;
  /* 字重600，半粗 */
  font-weight: 600;
}
/* 主按钮悬停效果，提高亮度 */
.btn-primary:hover { filter: brightness(1.12); }
/* 幽灵按钮（重置按钮）样式 */
.btn-ghost {
  /* 透明背景 */
  background: transparent;
  /* 弱化颜色文字 */
  color: var(--muted);
  /* 边框 */
  border: 1px solid var(--border);
}
/* 幽灵按钮悬停效果 */
.btn-ghost:hover {
  /* 文字变为主文本颜色 */
  color: var(--text);
  /* 边框颜色加深 */
  border-color: #35507a;
}

/* 表格 */
/* 表格外层容器样式 */
.table-wrap {
  /* 容器背景色 */
  background: var(--card);
  /* 容器边框 */
  border: 1px solid var(--border);
  /* 圆角10px */
  border-radius: 10px;
  /* 内边距：上下6 左右10 */
  padding: 6px 10px;
  /* 隐藏溢出内容 */
  overflow: hidden;
}
/* 方向标签基础样式 */
.dir-chip {
  /* 字体大小12px */
  font-size: 12px;
  /* 内边距：上下2 左右12 */
  padding: 2px 12px;
  /* 圆角20px，形成胶囊形状 */
  border-radius: 20px;
  /* 边框1px */
  border: 1px solid;
}
/* 买入方向标签样式：蓝色调 */
.dir-buy { color: #4d9fff; border-color: rgba(77, 159, 255, 0.4); background: rgba(77, 159, 255, 0.08); }
/* 卖出方向标签样式：紫色调 */
.dir-sell { color: #b48aff; border-color: rgba(180, 138, 255, 0.4); background: rgba(180, 138, 255, 0.08); }
/* 用户信息单元格样式 */
.user-cell {
  /* 使用行内弹性布局 */
  display: inline-flex;
  /* 垂直居中对齐 */
  align-items: center;
  /* 子元素间距6px */
  gap: 6px;
  /* 字体大小13px */
  font-size: 13px;
}
/* "我"标签样式，标识当前登录用户 */
.me-tag {
  /* 字体大小10px */
  font-size: 10px;
  /* 蓝色文字 */
  color: #4d9fff;
  /* 蓝色半透明边框 */
  border: 1px solid rgba(77, 159, 255, 0.4);
  /* 圆角4px */
  border-radius: 4px;
  /* 内边距：上下0 左右4 */
  padding: 0 4px;
}
/* 价格数字样式 */
.price-num {
  /* 使用主文本颜色 */
  color: var(--text);
  /* 字重700，粗体 */
  font-weight: 700;
  /* 使用等宽数字字体 */
  font-variant-numeric: tabular-nums;
}
/* 弱化文本样式，用于次要信息 */
.muted-text {
  /* 使用弱化颜色 */
  color: var(--muted);
  /* 字体大小12px */
  font-size: 12px;
}
/* 状态标签基础样式 */
.status-chip {
  /* 字体大小12px */
  font-size: 12px;
  /* 内边距：上下2 左右10 */
  padding: 2px 10px;
  /* 圆角20px，形成胶囊形状 */
  border-radius: 20px;
  /* 边框1px */
  border: 1px solid;
}
/* 状态0（待确认）样式：橙色调 */
.st-0 { color: #ffb454; border-color: rgba(255, 180, 84, 0.4); background: rgba(255, 180, 84, 0.08); }
/* 状态1（已完成）样式：绿色调 */
.st-1 { color: #3ddc97; border-color: rgba(61, 220, 151, 0.4); background: rgba(61, 220, 151, 0.08); }
/* 状态2（已取消）样式：灰色调 */
.st-2 { color: #8f9ab3; border-color: rgba(143, 154, 179, 0.4); background: rgba(143, 154, 179, 0.08); }
/* 操作链接按钮基础样式 */
.link-btn {
  /* 无背景 */
  background: none;
  /* 无边框 */
  border: none;
  /* 蓝色文字 */
  color: #4d9fff;
  /* 字体大小13px */
  font-size: 13px;
  /* 鼠标指针样式 */
  cursor: pointer;
  /* 内边距：上下2 左右6 */
  padding: 2px 6px;
}
/* 操作链接按钮悬停效果：显示下划线 */
.link-btn:hover { text-decoration: underline; }
/* 危险操作链接按钮样式（取消订单）：红色 */
.link-btn.danger { color: #ff6b5e; }
/* 分页器容器样式 */
.pager {
  /* 顶部外边距16px */
  margin-top: 16px;
  /* 使用弹性布局 */
  display: flex;
  /* 内容靠右对齐 */
  justify-content: flex-end;
}
</style>

<!-- 非scoped样式：用于覆盖Element Plus组件内部样式和teleport到body的元素 -->
<style>
/* 亮色主题下表格单元格文字颜色覆盖为深灰色 */
body.user-surface .orders-page .el-table,
body.user-surface .orders-page .el-table th.el-table__cell,
body.user-surface .orders-page .el-table td.el-table__cell {
  color: #526275 !important;
}
/* 亮色主题下表格表头文字颜色覆盖 */
body.user-surface .orders-page .el-table th.el-table__cell { color: #8290a3 !important; }
/* 亮色主题下价格数字颜色覆盖为红色 */
body.user-surface .orders-page .price-num { color: #d95550 !important; }
/* 亮色主题下买入方向标签颜色覆盖为蓝色系 */
body.user-surface .orders-page .dir-buy { color: #2f80ed; border-color: #b8d3f4; background: #f1f7ff; }
/* 亮色主题下卖出方向标签颜色覆盖为紫色系 */
body.user-surface .orders-page .dir-sell { color: #7b60b2; border-color: #d8ccf0; background: #f6f2fc; }
/* 亮色主题下"我"标签颜色覆盖 */
body.user-surface .orders-page .me-tag { color: #2f80ed; border-color: #b8d3f4; }
/* 亮色主题下分页器列表项和按钮颜色覆盖 */
body.user-surface .orders-page .el-pagination .el-pager li,
body.user-surface .orders-page .el-pagination button { color: #526275 !important; background: transparent !important; }
/* 亮色主题下分页器当前激活页码颜色覆盖为蓝色 */
body.user-surface .orders-page .el-pagination .el-pager li.is-active { color: #2f80ed !important; }

/* 下拉面板(teleport到body)暗色覆盖 */
/* 下拉弹出面板暗色主题变量覆盖 */
.debuff-popper {
  /* 覆盖层背景色 */
  --el-bg-color-overlay: #1a2338;
  /* 覆盖浅边框颜色 */
  --el-border-color-light: #2a3654;
  /* 覆盖主文本颜色 */
  --el-text-color-primary: #e9edf6;
  /* 覆盖常规文本颜色 */
  --el-text-color-regular: #c2cad9;
  /* 覆盖空白填充颜色 */
  --el-fill-color-blank: #1a2338;
}
/* 下拉面板自身背景和边框覆盖 */
.debuff-popper.el-popper {
  background: #1a2338 !important;
  border: 1px solid #2a3654 !important;
}
/* 下拉面板选项文字颜色覆盖 */
.debuff-popper .el-select-dropdown__item {
  color: #c2cad9 !important;
}
/* 下拉面板选项悬停背景色覆盖 */
.debuff-popper .el-select-dropdown__item.is-hovering {
  background: #1f2b45 !important;
}
/* 下拉面板选中项样式覆盖 */
.debuff-popper .el-select-dropdown__item.is-selected {
  color: #4d9fff !important;
  font-weight: 600;
}
/* 下拉面板下拉容器背景色覆盖 */
.debuff-popper .el-select-dropdown {
  background: #1a2338 !important;
}
/* 下拉面板箭头背景色和边框覆盖 */
.debuff-popper .el-popper__arrow::before {
  background: #1a2338;
  border-color: #2a3654;
}
/* 订单页面表格暗色主题样式 */
.orders-page .el-table {
  /* 表格背景透明 */
  --el-table-bg-color: transparent;
  /* 表格行背景透明 */
  --el-table-tr-bg-color: transparent;
  /* 表头背景色 */
  --el-table-header-bg-color: #1a2338;
  /* 表头文字颜色 */
  --el-table-header-text-color: #8f9ab3;
  /* 表格边框颜色 */
  --el-table-border-color: #232e45;
  /* 表格文字颜色 */
  --el-table-text-color: #c2cad9;
  /* 表格行悬停背景色 */
  --el-table-row-hover-bg-color: #1a2338;
  /* 表格自身背景透明 */
  background: transparent;
}
/* 表格表头单元格样式覆盖 */
.orders-page .el-table th.el-table__cell {
  background-color: #1a2338 !important;
  color: #8f9ab3;
  border-bottom-color: #232e45;
}
/* 表格数据单元格底部边框颜色覆盖 */
.orders-page .el-table td.el-table__cell {
  border-bottom-color: #232e45;
}
/* 分页容器背景透明覆盖 */
.orders-page .pagination-container {
  background: transparent !important;
}
/* 分页器暗色主题变量覆盖 */
.orders-page .el-pagination {
  /* 分页器背景透明 */
  --el-pagination-bg-color: transparent;
  /* 分页器文字颜色 */
  --el-pagination-text-color: #c2cad9;
  /* 分页器按钮颜色 */
  --el-pagination-button-color: #c2cad9;
  /* 分页器悬停颜色 */
  --el-pagination-hover-color: #4d9fff;
}
/* 分页器页码列表项样式覆盖 */
.orders-page .el-pagination .el-pager li {
  background: transparent !important;
  color: #c2cad9 !important;
}
/* 分页器当前激活页码样式覆盖 */
.orders-page .el-pagination .el-pager li.is-active {
  color: #4d9fff !important;
  font-weight: 700;
}
/* 分页器按钮样式覆盖 */
.orders-page .el-pagination button {
  background: transparent !important;
  color: #c2cad9 !important;
}
/* 分页器所有子元素背景透明覆盖 */
.orders-page .el-pagination * {
  background-color: transparent !important;
}
/* 分页器中选择器和跳转输入框背景覆盖 */
.orders-page .el-pagination .el-select .el-input__wrapper,
.orders-page .el-pagination .el-pagination__jump .el-input__wrapper {
  background-color: #1a2338 !important;
  box-shadow: 0 0 0 1px #2a3654 inset !important;
}
/* 选择器输入框暗色背景覆盖 */
.orders-page .el-select .el-input__wrapper {
  background-color: #1a2338 !important;
  box-shadow: 0 0 0 1px #2a3654 inset !important;
}
/* 选择器输入框文字颜色覆盖 */
.orders-page .el-select .el-input__inner {
  color: #c2cad9 !important;
}
/* 选择器输入框悬停时边框高亮效果 */
.orders-page .el-select .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #4d9fff inset !important;
}
/* 表格加载遮罩层半透明背景 */
.orders-page .el-loading-mask {
  background-color: rgba(13, 18, 32, 0.6);
}
</style>
