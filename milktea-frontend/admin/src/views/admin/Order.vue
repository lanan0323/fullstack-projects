<template>
  <!-- 负责人: 成员C | 后端: milktea-order /order (状态对外1-based) -->
  <div>
    <el-card>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待制作" name="1" />
        <el-tab-pane label="制作中" name="2" />
        <el-tab-pane label="已完成" name="3" />
        <el-tab-pane label="已取消" name="4" />
      </el-tabs>

      <el-table :data="orders" v-loading="loading" border stripe>
        <el-table-column prop="orderNo" label="订单号" width="190" />
        <el-table-column prop="username" label="下单人" width="110" />
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="金额" width="110" align="right">
          <template #default="{ row }">￥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMeta[row.status]?.tag">{{ statusMeta[row.status]?.text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="230" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
            <el-button v-if="row.status === 1" link type="success" @click="changeStatus(row.id, 2)">接单</el-button>
            <el-button v-if="row.status === 2" link type="success" @click="changeStatus(row.id, 3)">完成</el-button>
            <el-popconfirm v-if="row.status === 1" title="确定取消该订单吗?" @confirm="changeStatus(row.id, 4)">
              <template #reference>
                <el-button link type="danger">取消</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pager"
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadOrders"
        @current-change="loadOrders"
      />
    </el-card>

    <!-- 详情抽屉 -->
    <el-drawer v-model="detailVisible" title="订单详情" size="480px">
      <template v-if="detail">
        <el-descriptions :column="2" border size="small" class="mb16">
          <el-descriptions-item label="订单号" :span="2">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下单人">{{ detail.username }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusMeta[detail.status]?.tag" size="small">{{ statusMeta[detail.status]?.text }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ detail.createTime }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ detail.completeTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="优惠" :span="2">
            {{ detail.discountAmount > 0 ? `已优惠￥${detail.discountAmount}` : '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">订单明细</div>
        <el-table :data="detail.details || []" border size="small">
          <el-table-column prop="productName" label="商品" min-width="120" />
          <el-table-column prop="specs" label="规格" min-width="110">
            <template #default="{ row }">{{ row.specs || '-' }}</template>
          </el-table-column>
          <el-table-column label="单价" width="80" align="right">
            <template #default="{ row }">￥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="60" align="center" />
          <el-table-column label="小计" width="90" align="right">
            <template #default="{ row }">￥{{ row.subtotal }}</template>
          </el-table-column>
        </el-table>

        <div class="section-title">评价信息</div>
        <template v-if="detail.evaluation">
          <el-rate :model-value="detail.evaluation.rating" disabled />
          <div class="eval-content">{{ detail.evaluation.content }}</div>
        </template>
        <el-text v-else type="info">该订单暂无评价</el-text>

        <div class="total-line">
          <span v-if="detail.discountAmount > 0" class="discount-line">已优惠￥{{ detail.discountAmount }}　</span>
          合计：<span class="total-amount">￥{{ detail.totalAmount }}</span>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { pageOrders, getOrder, updateOrderStatus } from '../../api/order'

const statusMeta = {
  1: { text: '待制作', tag: 'warning' },
  2: { text: '制作中', tag: 'primary' },
  3: { text: '已完成', tag: 'success' },
  4: { text: '已取消', tag: 'info' }
}

const activeTab = ref('all')
const loading = ref(false)
const orders = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })

const loadOrders = async () => {
  loading.value = true
  try {
    const params = { ...query }
    if (activeTab.value !== 'all') params.status = Number(activeTab.value)
    const res = await pageOrders(params)
    orders.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  query.pageNum = 1
  loadOrders()
}

const changeStatus = async (id, status) => {
  await updateOrderStatus(id, status)
  ElMessage.success('操作成功')
  loadOrders()
}

// ==================== 详情抽屉 ====================
const detailVisible = ref(false)
const detail = ref(null)

const openDetail = async id => {
  const res = await getOrder(id)
  detail.value = res.data
  detailVisible.value = true
}

onMounted(loadOrders)
</script>

<style scoped>
.pager { margin-top: 16px; justify-content: flex-end; }
.mb16 { margin-bottom: 16px; }
.section-title { font-weight: bold; margin: 16px 0 8px; }
.eval-content { color: #606266; margin-top: 6px; font-size: 13px; }
.total-line { text-align: right; margin-top: 16px; }
.total-amount { color: #f56c6c; font-size: 18px; font-weight: bold; }
.discount-line { color: #e6a23c; font-size: 13px; }
</style>
