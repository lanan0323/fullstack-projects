<template>
  <!-- 负责人: 成员B | 后端: milktea-product /category -->
  <div>
    <el-card>
      <el-button type="success" class="mb16" @click="handleAdd">新增分类</el-button>

      <el-table :data="categories" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="categoryName" label="分类名称" min-width="160" />
        <el-table-column prop="sort" label="排序" width="90" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="删除前请确保分类下无商品，确定删除吗?" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="form.categoryName" placeholder="如 经典奶茶" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 160px" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listCategories, addCategory, updateCategory, deleteCategory } from '../../api/product'

const loading = ref(false)
const saving = ref(false)
const categories = ref([])

const load = async () => {
  loading.value = true
  try {
    const res = await listCategories()
    categories.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleDelete = async id => {
  await deleteCategory(id)
  ElMessage.success('删除成功')
  load()
}

// ==================== 弹窗 ====================
const dialogVisible = ref(false)
const form = reactive({ id: null, categoryName: '', sort: 0, status: 1 })

const handleAdd = () => {
  Object.assign(form, { id: null, categoryName: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

const handleEdit = row => {
  Object.assign(form, {
    id: row.id, categoryName: row.categoryName,
    sort: row.sort || 0, status: row.status ?? 1
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.categoryName) {
    ElMessage.warning('请填写分类名称')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await updateCategory(form)
    } else {
      await addCategory(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
</style>
