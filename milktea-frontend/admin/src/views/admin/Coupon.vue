<template>
  <!-- 负责人: 组员A | 后端: milktea-user /coupon (网关 /api/coupon/**) -->
  <div>
    <el-card>
      <div class="toolbar">
        <el-input v-model="query.name" placeholder="券名搜索" clearable style="width: 200px" @clear="load" @keyup.enter="load" />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px" @change="load">
          <el-option label="上架中" :value="1" />
          <el-option label="已下架" :value="0" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <div class="spacer" />
        <el-button type="warning" @click="openIssue()">发放优惠券</el-button>
        <el-button type="primary" @click="openEdit()">新增优惠券</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="name" label="券名" min-width="140" />
        <el-table-column label="优惠内容" width="130" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.type === 1" type="danger">减￥{{ row.amount }}</el-tag>
            <el-tag v-else type="success">{{ formatDiscount(row.amount) }}折</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="门槛" width="100" align="center">
          <template #default="{ row }">{{ row.minAmount > 0 ? `满￥${row.minAmount}` : '无门槛' }}</template>
        </el-table-column>
        <el-table-column prop="remain" label="剩余数量" width="90" align="center" />
        <el-table-column label="领取后有效" width="100" align="center">
          <template #default="{ row }">{{ row.validDays }}天</template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架中' : '已下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="250" align="center">
          <template #default="{ row }">
            <el-button link type="warning" :disabled="row.status !== 1 || row.remain <= 0" @click="openIssue(row)">发放</el-button>
            <el-button link type="primary" @click="openRecords(row.id)">记录</el-button>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" link type="info" @click="toggleStatus(row, 0)">下架</el-button>
            <el-button v-else link type="success" @click="toggleStatus(row, 1)">上架</el-button>
            <el-popconfirm title="确定删除该券吗?" @confirm="remove(row.id)">
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
        @size-change="load"
        @current-change="load"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="editVisible" :title="form.id ? '编辑优惠券' : '新增优惠券'" width="480px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="券名" required>
          <el-input v-model="form.name" placeholder="如: 新客立减券" maxlength="30" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-radio-group v-model="form.type">
            <el-radio :value="1">满减券</el-radio>
            <el-radio :value="2">折扣券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="form.type === 2 ? '折扣值' : '减免金额'" required>
          <el-input-number v-model="form.amount" :min="0.01" :max="form.type === 2 ? 0.99 : 999" :step="form.type === 2 ? 0.01 : 1" :precision="2" style="width: 200px" />
          <span class="tip">{{ form.type === 2 ? '(0.83 = 8.3折)' : '(元)' }}</span>
        </el-form-item>
        <el-form-item label="使用门槛">
          <el-input-number v-model="form.minAmount" :min="0" :max="9999" :step="1" :precision="2" style="width: 200px" />
          <span class="tip">(0 = 无门槛, 元)</span>
        </el-form-item>
        <el-form-item label="发放数量" required>
          <el-input-number v-model="form.remain" :min="1" :max="99999" :step="10" style="width: 200px" />
        </el-form-item>
        <el-form-item label="领取后有效" required>
          <el-input-number v-model="form.validDays" :min="1" :max="365" :step="1" style="width: 200px" />
          <span class="tip">天</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <!-- 发放弹窗 -->
    <el-dialog v-model="issueVisible" title="定向发放优惠券" width="440px">
      <el-form label-width="100px">
        <el-form-item label="选择优惠券" required>
          <el-select v-model="issueForm.couponId" placeholder="仅上架中的券" style="width: 260px" :disabled="!!issueLockId">
            <el-option v-for="c in onSaleCoupons" :key="c.id" :value="c.id"
              :label="`${c.name} (剩${c.remain}张)`" :disabled="c.remain <= 0" />
          </el-select>
        </el-form-item>
        <el-form-item label="发放对象" required>
          <el-radio-group v-model="issueForm.target">
            <el-radio value="phone">按手机号</el-radio>
            <el-radio value="userId">按用户ID</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="issueForm.target === 'phone'" label="用户手机号" required>
          <el-input v-model="issueForm.phone" placeholder="顾客注册手机号" maxlength="11" style="width: 260px" />
        </el-form-item>
        <el-form-item v-else label="用户ID" required>
          <el-input-number v-model="issueForm.userId" :min="1" style="width: 260px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="issueVisible = false">取消</el-button>
        <el-button type="warning" @click="doIssue">确认发放</el-button>
      </template>
    </el-dialog>

    <!-- 领取记录抽屉 -->
    <el-drawer v-model="recordsVisible" title="领取/发放记录" size="560px">
      <el-table :data="records" v-loading="recordsLoading" border size="small">
        <el-table-column prop="nickname" label="用户" width="110">
          <template #default="{ row }">{{ row.nickname || '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="name" label="券名" min-width="110" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="recordStatusMeta[row.status]?.tag">{{ recordStatusMeta[row.status]?.text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiveTime" label="领取时间" width="160" />
        <el-table-column prop="expireTime" label="到期时间" width="160" />
      </el-table>
      <el-pagination
        class="pager"
        v-model:current-page="recordsQuery.pageNum"
        v-model:page-size="recordsQuery.pageSize"
        :total="recordsTotal"
        layout="total, prev, pager, next"
        @current-change="loadRecords"
      />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  pageCoupons, addCoupon, updateCoupon, updateCouponStatus, deleteCoupon, issueCoupon, couponRecords
} from '../../api/coupon'

// 记录状态: 0未使用 1已使用 2已过期
const recordStatusMeta = {
  0: { text: '未使用', tag: 'warning' },
  1: { text: '已使用', tag: 'success' },
  2: { text: '已过期', tag: 'info' }
}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ name: '', status: null, pageNum: 1, pageSize: 10 })

const load = async () => {
  loading.value = true
  try {
    const params = { ...query }
    if (!params.name) delete params.name
    if (params.status === null || params.status === '') delete params.status
    const res = await pageCoupons(params)
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const formatDiscount = amount => {
  const v = Number(amount) * 10
  return Number.isInteger(v) ? v : v.toFixed(1)
}

// ==================== 新增/编辑 ====================
const editVisible = ref(false)
const form = reactive({ id: null, name: '', type: 1, amount: 5, minAmount: 0, remain: 100, validDays: 7 })

const openEdit = row => {
  if (row) {
    Object.assign(form, { id: row.id, name: row.name, type: row.type, amount: Number(row.amount), minAmount: Number(row.minAmount), remain: row.remain, validDays: row.validDays })
  } else {
    Object.assign(form, { id: null, name: '', type: 1, amount: 5, minAmount: 0, remain: 100, validDays: 7 })
  }
  editVisible.value = true
}

const save = async () => {
  if (!form.name.trim()) return ElMessage.warning('请填写券名')
  if (form.type === 2 && form.amount >= 1) return ElMessage.warning('折扣值须小于1, 如0.83表示8.3折')
  if (form.id) {
    await updateCoupon(form.id, form)
  } else {
    await addCoupon(form)
  }
  ElMessage.success('保存成功')
  editVisible.value = false
  load()
}

const toggleStatus = async (row, status) => {
  await updateCouponStatus(row.id, status)
  ElMessage.success(status === 1 ? '已上架' : '已下架')
  load()
}

const remove = async id => {
  await deleteCoupon(id)
  ElMessage.success('已删除')
  load()
}

// ==================== 定向发放 ====================
const issueVisible = ref(false)
const issueLockId = ref(null)
const onSaleCoupons = ref([])
const issueForm = reactive({ couponId: null, target: 'phone', phone: '', userId: 1 })

const openIssue = async row => {
  issueLockId.value = row ? row.id : null
  Object.assign(issueForm, { couponId: row ? row.id : null, target: 'phone', phone: '', userId: 1 })
  const res = await pageCoupons({ status: 1, pageNum: 1, pageSize: 100 })
  onSaleCoupons.value = res.data.list || []
  issueVisible.value = true
}

const doIssue = async () => {
  if (!issueForm.couponId) return ElMessage.warning('请选择优惠券')
  const params = { couponId: issueForm.couponId }
  if (issueForm.target === 'phone') {
    if (!issueForm.phone.trim()) return ElMessage.warning('请填写用户手机号')
    params.phone = issueForm.phone.trim()
  } else {
    if (!issueForm.userId) return ElMessage.warning('请填写用户ID')
    params.userId = issueForm.userId
  }
  await issueCoupon(params)
  ElMessage.success('发放成功')
  issueVisible.value = false
  load()
}

// ==================== 领取记录 ====================
const recordsVisible = ref(false)
const recordsLoading = ref(false)
const records = ref([])
const recordsTotal = ref(0)
const recordsQuery = reactive({ couponId: null, pageNum: 1, pageSize: 10 })

const openRecords = couponId => {
  recordsQuery.couponId = couponId
  recordsQuery.pageNum = 1
  recordsVisible.value = true
  loadRecords()
}

const loadRecords = async () => {
  recordsLoading.value = true
  try {
    const params = { ...recordsQuery }
    if (!params.couponId) delete params.couponId
    const res = await couponRecords(params)
    records.value = res.data.list || []
    recordsTotal.value = res.data.total || 0
  } finally {
    recordsLoading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; align-items: center; }
.spacer { flex: 1; }
.pager { margin-top: 16px; justify-content: flex-end; }
.tip { color: #909399; font-size: 12px; margin-left: 8px; }
</style>
