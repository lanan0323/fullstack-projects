<template>
  <!-- 负责人: 成员D | 后端: milktea-store /statistics -->
  <div>
    <el-card>
      <div class="toolbar">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :clearable="false"
        />
        <el-button type="primary" @click="handleQuery">查询</el-button>
      </div>
    </el-card>

    <el-card class="mt16">
      <template #header>营业额趋势（近 {{ days }} 天）</template>
      <div class="bar-chart" v-if="trend.length" v-loading="loading">
        <div class="bar-item" v-for="t in trend" :key="t.date">
          <div class="bar-value">{{ t.revenue }}</div>
          <div class="bar" :style="{ height: barHeight(t.revenue) }"></div>
          <div class="bar-date">{{ t.date.slice(5) }}</div>
        </div>
      </div>
      <el-empty v-else description="该时间段暂无营业数据" :image-size="80" />
    </el-card>

    <el-card class="mt16">
      <template #header>商品销量 TOP5</template>
      <div v-if="rank.length" class="rank-list">
        <div class="rank-item" v-for="(r, i) in rank" :key="r.productName">
          <span class="rank-no" :class="'top' + (i + 1)">{{ i + 1 }}</span>
          <span class="rank-name">{{ r.productName }}</span>
          <div class="rank-bar-wrap">
            <div class="rank-bar" :style="{ width: rankWidth(r.salesCount) }"></div>
          </div>
          <span class="rank-count">{{ r.salesCount }} 份</span>
        </div>
      </div>
      <el-empty v-else description="暂无销量数据" :image-size="80" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { revenueTrend, salesRank } from '../../api/store'

const loading = ref(false)
const dateRange = ref([new Date(Date.now() - 6 * 86400000), new Date()])
const trend = ref([])
const rank = ref([])

const days = computed(() => {
  if (!dateRange.value || dateRange.value.length !== 2) return 7
  const diff = new Date(dateRange.value[1]) - new Date(dateRange.value[0])
  return Math.min(Math.max(Math.round(diff / 86400000) + 1, 1), 90)
})

const maxRevenue = computed(() => Math.max(...trend.value.map(t => Number(t.revenue) || 0), 1))
const barHeight = v => `${Math.max(((Number(v) || 0) / maxRevenue.value) * 160, 4)}px`

const maxSales = computed(() => Math.max(...rank.value.map(r => r.salesCount || 0), 1))
const rankWidth = c => `${Math.max(((c || 0) / maxSales.value) * 100, 4)}%`

const load = async () => {
  loading.value = true
  try {
    const [t, r] = await Promise.all([revenueTrend({ days: days.value }), salesRank({ limit: 5 })])
    trend.value = t.data || []
    rank.value = r.data || []
  } finally {
    loading.value = false
  }
}

const handleQuery = load

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; }
.mt16 { margin-top: 16px; }
.bar-chart { display: flex; align-items: flex-end; gap: 16px; height: 220px; padding: 0 10px; }
.bar-item { display: flex; flex-direction: column; align-items: center; gap: 6px; flex: 1; }
.bar { width: 30px; background: linear-gradient(180deg, #6cb98a, #3a9e63); border-radius: 4px 4px 0 0; }
.bar-value { font-size: 12px; color: #606266; }
.bar-date { font-size: 12px; color: #909399; }
.rank-list { display: flex; flex-direction: column; gap: 14px; }
.rank-item { display: flex; align-items: center; gap: 12px; }
.rank-no { display: inline-flex; align-items: center; justify-content: center; width: 22px; height: 22px; border-radius: 50%; background: #c0c4cc; color: #fff; font-size: 12px; }
.rank-no.top1 { background: #f56c6c; }
.rank-no.top2 { background: #e6a23c; }
.rank-no.top3 { background: #3a9e63; }
.rank-name { width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.rank-bar-wrap { flex: 1; height: 14px; background: #f5f7fa; border-radius: 7px; overflow: hidden; }
.rank-bar { height: 100%; background: linear-gradient(90deg, #95d475, #67c23a); border-radius: 7px; }
.rank-count { width: 70px; text-align: right; color: #606266; font-size: 13px; }
</style>
