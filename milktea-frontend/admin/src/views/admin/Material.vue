<template>
  <!-- 负责人: 成员D | 后端: milktea-store /material + /stock/record -->
  <div>
    <el-card>
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="原料名称" clearable style="width: 180px" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增原料</el-button>
      </div>

      <el-table :data="materials" v-loading="loading" border stripe>
        <el-table-column prop="name" label="原料名" min-width="130" />
        <el-table-column prop="unit" label="单位" width="70" align="center" />
        <el-table-column label="当前库存" min-width="180">
          <template #default="{ row }">
            <el-progress
              :percentage="stockPercent(row)"
              :color="stockColor(row.stockStatus)"
              :format="() => `${row.currentStock} ${row.unit}`"
            />
          </template>
        </el-table-column>
        <el-table-column prop="minThreshold" label="最低阈值" width="100" align="center" />
        <el-table-column label="库存状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="stockColor(row.stockStatus)">{{ row.stockStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center">
          <template #default="{ row }">
            <el-button link type="success" @click="openStock(row, 1)">入库</el-button>
            <el-button link type="warning" @click="openStock(row, 2)">出库</el-button>
            <el-button link type="primary" @click="openRecords(row)">流水</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该原料吗?" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
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
        @size-change="loadMaterials"
        @current-change="loadMaterials"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑原料' : '新增原料'" width="420px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="原料名" required>
          <el-input v-model="form.name" placeholder="如 全脂牛奶" />
        </el-form-item>
        <el-form-item label="单位" required>
          <el-input v-model="form.unit" placeholder="如 L / g / 个" style="width: 160px" />
        </el-form-item>
        <el-form-item label="最低阈值">
          <el-input-number v-model="form.minThreshold" :min="0" :precision="1" style="width: 160px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 入库/出库弹窗 -->
    <el-dialog v-model="stockVisible" :title="stockForm.type === 1 ? '原料入库' : '原料出库'" width="400px">
      <el-descriptions :column="1" border size="small" class="mb16">
        <el-descriptions-item label="原料">{{ stockMaterial?.name }}（当前 {{ stockMaterial?.currentStock }} {{ stockMaterial?.unit }}）</el-descriptions-item>
      </el-descriptions>
      <el-form :model="stockForm" label-width="90px">
        <el-form-item label="类型">
          <el-radio-group v-model="stockForm.type">
            <el-radio :value="1">入库</el-radio>
            <el-radio :value="2">出库</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="数量" required>
          <el-input-number v-model="stockForm.quantity" :min="0.1" :precision="1" style="width: 160px" />
          <span class="unit-text">{{ stockMaterial?.unit }}</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stockForm.remark" placeholder="如 供应商补货" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockVisible = false">取消</el-button>
        <el-button type="primary" :loading="stockSaving" @click="handleStock">确认</el-button>
      </template>
    </el-dialog>

    <!-- 流水抽屉 -->
    <el-drawer v-model="recordVisible" :title="`出入库流水 - ${recordMaterial?.name || ''}`" size="520px">
      <el-table :data="records" v-loading="recordLoading" border size="small">
        <el-table-column label="类型" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'success' : 'warning'" size="small">{{ row.type === 1 ? '入库' : '出库' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" align="right" />
        <el-table-column label="库存变化" width="120" align="center">
          <template #default="{ row }">{{ row.beforeStock }} → {{ row.afterStock }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="100">
          <template #default="{ row }">{{ row.remark || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="160" />
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  pageMaterials, addMaterial, updateMaterial, deleteMaterial,
  stockOperation, stockRecordsByMaterial
} from '../../api/store'

const loading = ref(false)
const saving = ref(false)
const materials = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const stockColor = s => (s === '预警' ? '#f56c6c' : s === '偏低' ? '#e6a23c' : '#67c23a')
const stockPercent = row => {
  const max = Number(row.minThreshold || 0) * 3 || Number(row.currentStock || 1)
  return Math.min(Math.round(((Number(row.currentStock) || 0) / max) * 100), 100)
}

const loadMaterials = async () => {
  loading.value = true
  try {
    const res = await pageMaterials(query)
    materials.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.pageNum = 1
  loadMaterials()
}

const handleDelete = async id => {
  await deleteMaterial(id)
  ElMessage.success('删除成功')
  loadMaterials()
}

// ==================== 新增/编辑 ====================
const dialogVisible = ref(false)
const form = reactive({ id: null, name: '', unit: '', minThreshold: 0, status: 1 })

const handleAdd = () => {
  Object.assign(form, { id: null, name: '', unit: '', minThreshold: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = row => {
  Object.assign(form, {
    id: row.id, name: row.name, unit: row.unit,
    minThreshold: Number(row.minThreshold) || 0, status: row.status ?? 1
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.name || !form.unit) {
    ElMessage.warning('请填写原料名和单位')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await updateMaterial(form)
    } else {
      await addMaterial(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadMaterials()
  } finally {
    saving.value = false
  }
}

// ==================== 入库/出库 ====================
const stockVisible = ref(false)
const stockSaving = ref(false)
const stockMaterial = ref(null)
const stockForm = reactive({ materialId: null, type: 1, quantity: 1, remark: '' })

const openStock = (row, type) => {
  stockMaterial.value = row
  Object.assign(stockForm, { materialId: row.id, type, quantity: 1, remark: '' })
  stockVisible.value = true
}

const handleStock = async () => {
  if (!stockForm.quantity || stockForm.quantity <= 0) {
    ElMessage.warning('请填写正确的数量')
    return
  }
  stockSaving.value = true
  try {
    await stockOperation(stockForm)
    ElMessage.success(stockForm.type === 1 ? '入库成功' : '出库成功')
    stockVisible.value = false
    loadMaterials()
  } finally {
    stockSaving.value = false
  }
}

// ==================== 流水 ====================
const recordVisible = ref(false)
const recordLoading = ref(false)
const recordMaterial = ref(null)
const records = ref([])

const openRecords = async row => {
  recordMaterial.value = row
  recordVisible.value = true
  recordLoading.value = true
  try {
    const res = await stockRecordsByMaterial(row.id)
    records.value = res.data || []
  } finally {
    recordLoading.value = false
  }
}

onMounted(loadMaterials)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
.mb16 { margin-bottom: 16px; }
.unit-text { margin-left: 8px; color: #909399; }
</style>
