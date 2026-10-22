<template>
  <!-- 负责人: 成员D | 后端: milktea-store /statistics -->
  <div v-loading="loading">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card><div class="stat-num">{{ stat.todayOrderCount ?? 0 }}</div><div class="stat-label">今日订单</div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card><div class="stat-num">￥{{ stat.todayRevenue ?? '0.00' }}</div><div class="stat-label">今日营业额(元)</div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card><div class="stat-num">{{ stat.productCount ?? 0 }}</div><div class="stat-label">在售商品</div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-num" :class="{ danger: (stat.stockWarningCount ?? 0) > 0 }">{{ stat.stockWarningCount ?? 0 }}</div>
          <div class="stat-label">库存预警</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="mt16">
      <template #header>近7日营业额趋势</template>
      <div class="bar-chart" v-if="trend.length">
        <div class="bar-item" v-for="t in trend" :key="t.date">
          <div class="bar-value">{{ t.revenue }}</div>
          <div class="bar" :style="{ height: barHeight(t.revenue) }"></div>
          <div class="bar-date">{{ t.date.slice(5) }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无营业数据" :image-size="60" />
    </el-card>

    <el-card class="mt16">
      <template #header>库存预警</template>
      <el-table :data="warnings" border stripe size="small">
        <el-table-column prop="name" label="原料名" min-width="140" />
        <el-table-column prop="currentStock" label="当前库存" width="110" align="center" />
        <el-table-column prop="unit" label="单位" width="80" align="center" />
        <el-table-column prop="minThreshold" label="最低阈值" width="110" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.stockStatus || '预警' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!warnings.length" description="库存状态良好，无预警" :image-size="60" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { dashboardData, revenueTrend, warningList } from '../../api/store'

const loading = ref(false)
const stat = ref({})
const trend = ref([])
const warnings = ref([])

const maxRevenue = computed(() => Math.max(...trend.value.map(t => Number(t.revenue) || 0), 1))
const barHeight = v => `${Math.max(((Number(v) || 0) / maxRevenue.value) * 160, 4)}px`

const load = async () => {
  loading.value = true
  try {
    const [d, t, w] = await Promise.all([dashboardData(), revenueTrend({ days: 7 }), warningList()])
    stat.value = d.data || {}
    trend.value = t.data || []
    warnings.value = w.data || []
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.stat-num { font-size: 28px; font-weight: bold; color: #3a9e63; }
.stat-num.danger { color: #f56c6c; }
.stat-label { color: #909399; font-size: 13px; margin-top: 6px; }
.mt16 { margin-top: 16px; }
.bar-chart { display: flex; align-items: flex-end; gap: 24px; height: 220px; padding: 0 20px; }
.bar-item { display: flex; flex-direction: column; align-items: center; gap: 6px; flex: 1; }
.bar { width: 36px; background: linear-gradient(180deg, #6cb98a, #3a9e63); border-radius: 4px 4px 0 0; }
.bar-value { font-size: 12px; color: #606266; }
.bar-date { font-size: 12px; color: #909399; }
</style>
