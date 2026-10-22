<template>
  <!-- 负责人: 组员A | 后端: milktea-user /user/weekspecial (配置存mt_user库) -->
  <div>
    <el-card>
      <div class="toolbar">
        <el-text type="info" size="small">配置后的商品在用户端"每周9.9起"Tab以活动价展示, 保存即时生效</el-text>
        <div class="spacer" />
        <el-button type="primary" @click="openAdd">添加活动商品</el-button>
      </div>

      <el-table :data="rows" v-loading="loading" border stripe>
        <el-table-column label="商品图" width="90" align="center">
          <template #default="{ row }">
            <el-image :src="row.imageUrl" fit="cover" style="width: 50px; height: 50px; border-radius: 6px">
              <template #error><div class="img-fallback">无图</div></template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="商品" min-width="150">
          <template #default="{ row }">{{ row.productName || '(商品已删除)' }}</template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="110" />
        <el-table-column label="原价" width="100" align="right">
          <template #default="{ row }">{{ row.price != null ? `￥${row.price}` : '-' }}</template>
        </el-table-column>
        <el-table-column label="活动价" width="110" align="right">
          <template #default="{ row }"><span class="special-price">￥{{ row.specialPrice }}</span></template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="170" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">改活动价</el-button>
            <el-popconfirm title="取消后用户端恢复原价, 确定吗?" @confirm="handleRemove(row.productId)">
              <template #reference>
                <el-button link type="danger">取消活动</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && rows.length === 0" description="暂无活动商品, 点击右上角添加" />
    </el-card>

    <!-- 添加/改价弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.productId ? '改活动价' : '添加活动商品'" width="420px">
      <el-form label-width="80px">
        <el-form-item label="商品" required>
          <el-select v-model="form.productId" placeholder="选择商品" style="width: 100%" :disabled="!!editing">
            <el-option v-for="p in selectableProducts" :key="p.id" :value="p.id"
              :label="`${p.productName} (￥${p.price})`" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动价" required>
          <el-input-number v-model="form.specialPrice" :min="0.01" :max="9999" :precision="2" :step="0.1" style="width: 180px" />
          <span class="tip">用户端将以此价展示并下单</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listWeekSpecials, setWeekSpecial, removeWeekSpecial } from '../../api/weekspecial'
import { pageProducts } from '../../api/product'

const loading = ref(false)
const rows = ref([])          // 活动配置 + 商品信息合并
const products = ref([])      // 全量商品(用于弹窗选择与信息补全)

const load = async () => {
  loading.value = true
  try {
    const [specRes, prodRes] = await Promise.all([
      listWeekSpecials(),
      pageProducts({ pageNum: 1, pageSize: 100 })
    ])
    products.value = prodRes.data.list || []
    const pMap = new Map(products.value.map(p => [p.id, p]))
    rows.value = (specRes.data || []).map(s => ({
      ...s,
      ...(pMap.get(s.productId) || {})
    }))
  } finally {
    loading.value = false
  }
}

// 弹窗可选商品: 排除已在活动中的(编辑时自身除外)
const editing = ref(false)
const selectableProducts = computed(() =>
  products.value.filter(p => !rows.value.some(r => r.productId === p.id) || p.id === form.productId)
)

// ==================== 添加/改价弹窗 ====================
const dialogVisible = ref(false)
const form = reactive({ productId: null, specialPrice: 9.9 })

const openAdd = () => {
  editing.value = false
  Object.assign(form, { productId: null, specialPrice: 9.9 })
  dialogVisible.value = true
}

const openEdit = row => {
  editing.value = true
  Object.assign(form, { productId: row.productId, specialPrice: Number(row.specialPrice) })
  dialogVisible.value = true
}

const save = async () => {
  if (!form.productId) return ElMessage.warning('请选择商品')
  if (!form.specialPrice || form.specialPrice <= 0) return ElMessage.warning('请填写活动价')
  await setWeekSpecial({ productId: form.productId, specialPrice: form.specialPrice })
  ElMessage.success('活动已生效, 用户端即时可见')
  dialogVisible.value = false
  load()
}

const handleRemove = async productId => {
  await removeWeekSpecial(productId)
  ElMessage.success('已取消活动')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; align-items: center; margin-bottom: 16px; }
.spacer { flex: 1; }
.special-price { color: #f56c6c; font-weight: bold; font-size: 15px; }
.tip { color: #909399; font-size: 12px; margin-left: 8px; }
.img-fallback { display: flex; align-items: center; justify-content: center; width: 50px; height: 50px; color: #c0c4cc; font-size: 12px; background: #f5f7fa; border-radius: 6px; }
</style>
