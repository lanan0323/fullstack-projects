<template>
  <!-- 负责人: 成员B | 后端: milktea-product /product -->
  <div>
    <el-card>
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="商品名称" clearable style="width: 180px" @keyup.enter="handleSearch" />
        <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 140px">
          <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
        </el-select>
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 120px">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="handleAdd">新增商品</el-button>
      </div>

      <el-table :data="products" v-loading="loading" border stripe>
        <el-table-column label="商品图" width="90" align="center">
          <template #default="{ row }">
            <el-image :src="row.imageUrl" fit="cover" style="width: 50px; height: 50px; border-radius: 6px">
              <template #error><div class="img-fallback">无图</div></template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="名称" min-width="140" />
        <el-table-column prop="categoryName" label="分类" width="110" />
        <el-table-column label="价格" width="100" align="right">
          <template #default="{ row }">￥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="规格" min-width="160">
          <template #default="{ row }">
            <el-tag v-for="s in row.specs || []" :key="s.specOptionId" size="small" type="info" class="spec-tag">
              {{ s.specOptionName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column label="操作" width="250" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="openRecipe(row)">配方</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="handleToggle(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-popconfirm title="确定删除该商品吗?" @confirm="handleDelete(row.id)">
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
        @size-change="loadProducts"
        @current-change="loadProducts"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品' : '新增商品'" width="560px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="form.productName" placeholder="如 珍珠奶茶" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" style="width: 160px" />
        </el-form-item>
        <el-form-item label="图片地址">
          <el-input v-model="form.imageUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 160px" />
        </el-form-item>
        <el-form-item label="规格" v-if="specTypes.length">
          <div class="spec-group" v-for="t in specTypes" :key="t.id">
            <div class="spec-title">{{ t.specName }}</div>
            <el-checkbox-group v-model="form.specOptionIds">
              <el-checkbox v-for="o in (optionsByType[t.id] || [])" :key="o.id" :value="o.id">
                {{ o.optionName }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 配方弹窗: 每杯消耗原料(下单自动扣减) -->
    <el-dialog v-model="recipeVisible" :title="`配方 - ${recipeProduct?.productName || ''}`" width="560px">
      <el-alert type="info" :closable="false" show-icon
                title="配置每杯消耗的原料与用量, 用户下单后系统按配方自动扣减库存; 留空表示不自动扣减" class="recipe-tip" />
      <div class="recipe-row" v-for="(r, i) in recipeRows" :key="i">
        <el-select v-model="r.materialId" placeholder="选择原料" style="width: 240px" size="default">
          <el-option v-for="m in materialOptions" :key="m.id" :label="`${m.name} (${m.unit})`" :value="m.id"
                     :disabled="recipeRows.some((x, j) => j !== i && x.materialId === m.id)" />
        </el-select>
        <span class="recipe-x">× 每杯</span>
        <el-input-number v-model="r.quantity" :min="0.001" :precision="3" :step="0.01" style="width: 140px" />
        <el-button link type="danger" @click="recipeRows.splice(i, 1)">删除</el-button>
      </div>
      <el-button type="primary" plain size="small" @click="recipeRows.push({ materialId: null, quantity: 0.01 })"
                 :disabled="!materialOptions.length || recipeRows.length >= materialOptions.length">
        + 添加原料
      </el-button>
      <template #footer>
        <el-button @click="recipeVisible = false">取消</el-button>
        <el-button type="primary" :loading="recipeSaving" @click="saveRecipeRows">保存配方</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  pageProducts, addProduct, updateProduct, deleteProduct,
  toggleProductStatus, listCategories, listSpecTypes, getProduct
} from '../../api/product'
import request from '../../api/request'
import { getRecipe, saveRecipe } from '../../api/store'
import { listMaterials } from '../../api/store'

// ===== 配方管理 =====
const recipeVisible = ref(false)
const recipeSaving = ref(false)
const recipeProduct = ref(null)
const recipeRows = ref([])
const materialOptions = ref([])

const openRecipe = async row => {
  recipeProduct.value = row
  recipeRows.value = []
  recipeVisible.value = true
  if (!materialOptions.value.length) {
    try {
      const res = await listMaterials()
      materialOptions.value = res.data || []
    } catch (e) { /* 静默 */ }
  }
  try {
    const res = await getRecipe(row.id)
    recipeRows.value = (res.data || []).map(r => ({
      materialId: Number(r.materialId),
      quantity: Number(r.quantity)
    }))
  } catch (e) {
    recipeRows.value = []
  }
}

const saveRecipeRows = async () => {
  const rows = recipeRows.value.filter(r => r.materialId && r.quantity > 0)
  recipeSaving.value = true
  try {
    await saveRecipe(recipeProduct.value.id, rows)
    ElMessage.success('配方已保存, 下单将按此自动扣减原料')
    recipeVisible.value = false
  } finally {
    recipeSaving.value = false
  }
}

const loading = ref(false)
const saving = ref(false)
const products = ref([])
const total = ref(0)
const categories = ref([])
const specTypes = ref([])
const optionsByType = ref({})
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', categoryId: null, status: null })

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await pageProducts(query)
    products.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.pageNum = 1
  loadProducts()
}

const handleToggle = async row => {
  await toggleProductStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  loadProducts()
}

const handleDelete = async id => {
  await deleteProduct(id)
  ElMessage.success('删除成功')
  loadProducts()
}

// ==================== 弹窗 ====================
const dialogVisible = ref(false)
const form = reactive({
  id: null, productName: '', categoryId: null, price: 0,
  imageUrl: '', description: '', sort: 0, specOptionIds: []
})

// 加载全部规格类型及各类型下的选项
const loadSpecs = async () => {
  const res = await listSpecTypes()
  specTypes.value = res.data || []
  await Promise.all(
    specTypes.value.map(async t => {
      const r = await request.get(`/spec/options/${t.id}`)
      optionsByType.value[t.id] = r.data || []
    })
  )
}

const handleAdd = () => {
  Object.assign(form, {
    id: null, productName: '', categoryId: null, price: 0,
    imageUrl: '', description: '', sort: 0, specOptionIds: []
  })
  dialogVisible.value = true
}

const handleEdit = async row => {
  const res = await getProduct(row.id)
  const p = res.data || {}
  Object.assign(form, {
    id: p.id, productName: p.productName, categoryId: p.categoryId,
    price: Number(p.price) || 0, imageUrl: p.imageUrl, description: p.description,
    sort: p.sort || 0, specOptionIds: (p.specs || []).map(s => s.specOptionId)
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.productName || !form.categoryId) {
    ElMessage.warning('请填写名称和分类')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await updateProduct(form)
    } else {
      await addProduct(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadProducts()
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadProducts()
  listCategories().then(res => (categories.value = res.data || []))
  loadSpecs()
})
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
.img-fallback { display: flex; align-items: center; justify-content: center; width: 50px; height: 50px; color: #c0c4cc; font-size: 12px; background: #f5f7fa; border-radius: 6px; }
.spec-tag { margin: 2px; }
.spec-group { margin-bottom: 8px; }
.spec-title { font-size: 13px; color: #909399; margin-bottom: 4px; }
.recipe-tip { margin-bottom: 14px; }
.recipe-row { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.recipe-x { color: #909399; font-size: 13px; }
</style>
