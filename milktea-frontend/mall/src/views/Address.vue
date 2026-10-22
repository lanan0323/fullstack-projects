<template>
  <div class="address-page">
    <van-nav-bar :title="isSelect ? '选择收货地址' : '收货地址'" left-arrow @click-left="$router.back()" />

    <van-empty v-if="!loading && list.length === 0" description="还没有收货地址，添加一个吧" />

    <!-- 地址卡片 -->
    <div class="addr-card" v-for="a in list" :key="a.id" @click="onPick(a)">
      <div class="addr-main">
        <div class="addr-line1">
          <span class="receiver">{{ a.receiver }}</span>
          <span class="phone">{{ a.phone }}</span>
          <span class="default-tag" v-if="a.isDefault === 1">默认</span>
        </div>
        <div class="addr-text">{{ a.address }}</div>
      </div>
      <div class="addr-actions" @click.stop>
        <van-icon v-if="a.isDefault !== 1" name="location-o" size="18" @click="onSetDefault(a)" />
        <van-icon name="edit" size="18" @click="openEdit(a)" />
        <van-icon name="delete-o" size="18" @click="onDelete(a)" />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <van-popup v-model:show="showForm" position="bottom" round :style="{ maxHeight: '85%' }">
      <div class="addr-form">
        <div class="form-title">{{ form.id ? '编辑地址' : '新增地址' }}</div>
        <van-form @submit="onSave">
          <van-cell-group inset>
            <van-field v-model="form.receiver" label="收货人" placeholder="请输入收货人姓名"
                       :rules="[{ required: true, message: '请输入收货人' }]" />
            <van-field v-model="form.phone" label="手机号" type="tel" maxlength="11" placeholder="请输入11位手机号"
                       :rules="[{ pattern: /^1\d{10}$/, message: '手机号格式不正确' }]" />
            <!-- 高德地图POI联想: 填入AMAP_KEY后显示, 选中回填完整地址 -->
            <template v-if="AMAP_KEY">
              <van-field v-model="poiQuery" label="地图定位" placeholder="输入小区/大厦/学校名快速带出地址"
                         @update:model-value="onPoiInput" clearable />
              <div class="poi-list" v-if="poiTips.length">
                <div class="poi-item" v-for="(t, i) in poiTips" :key="i" @click="pickTip(t)">
                  <div class="poi-name">{{ t.name }}</div>
                  <div class="poi-district">{{ t.district }}<template v-if="t.address"> · {{ t.address }}</template></div>
                </div>
              </div>
            </template>
            <van-field v-model="form.address" label="地址" rows="2" type="textarea" autosize placeholder="楼栋/门牌号等详细地址"
                       :rules="[{ required: true, message: '请输入收货地址' }]" />
          </van-cell-group>
          <div style="margin: 18px 16px 10px;">
            <van-button round block type="primary" native-type="submit">保存</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>

    <!-- 底部新增按钮 -->
    <div class="add-bar">
      <van-button round block type="primary" icon="plus" @click="openAdd">新增收货地址</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { listAddresses, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '../api/address'

const route = useRoute()
const router = useRouter()

// 选择模式: 结算页跳来选地址(select=1), 选中后写sessionStorage并返回
const isSelect = computed(() => route.query.select === '1')

const loading = ref(true)
const list = ref([])
const showForm = ref(false)
const form = ref({ id: null, receiver: '', phone: '', address: '' })

// ===== 高德地图POI联想 =====
// 去 lbs.amap.com 免费申请"Web服务"类型key填 here; 留空则隐藏该输入框(纯手输)
const AMAP_KEY = 'dd3041434b3e57073d5862981b6fa83b'
const poiQuery = ref('')
const poiTips = ref([])
let poiTimer = null
const onPoiInput = v => {
  clearTimeout(poiTimer)
  if (!AMAP_KEY || !v || v.length < 2) { poiTips.value = []; return }
  poiTimer = setTimeout(async () => {
    try {
      const r = await fetch(`https://restapi.amap.com/v3/assistant/inputtips?key=${AMAP_KEY}&keywords=${encodeURIComponent(v)}`)
      const d = await r.json()
      poiTips.value = (d.tips || []).filter(t => t.name)
    } catch { poiTips.value = [] }
  }, 300)
}
const pickTip = t => {
  const dist = (t.district || '').replace(/市辖区/, '')
  form.value.address = dist + (t.name || '')
  poiQuery.value = ''
  poiTips.value = []
}

const load = async () => {
  loading.value = true
  try {
    const res = await listAddresses()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

// 点击卡片: 选择模式下回传选中地址, 否则无动作
const onPick = a => {
  if (!isSelect.value) return
  sessionStorage.setItem('chosenAddress', JSON.stringify(a))
  router.back()
}

const openAdd = () => {
  form.value = { id: null, receiver: '', phone: '', address: '' }
  poiQuery.value = ''
  poiTips.value = []
  showForm.value = true
}

const openEdit = a => {
  form.value = { id: a.id, receiver: a.receiver, phone: a.phone, address: a.address }
  poiQuery.value = ''
  poiTips.value = []
  showForm.value = true
}

const onSave = async () => {
  if (form.value.id) {
    await updateAddress(form.value)
  } else {
    await addAddress(form.value)
  }
  showToast('已保存')
  showForm.value = false
  load()
}

const onDelete = a => {
  showConfirmDialog({ title: '提示', message: `删除「${a.receiver}」的这条地址？` }).then(async () => {
    await deleteAddress(a.id)
    showToast('已删除')
    load()
  }).catch(() => {})
}

const onSetDefault = async a => {
  await setDefaultAddress(a.id)
  showToast('已设为默认')
  load()
}

onMounted(load)
</script>

<style scoped>
.address-page {
  min-height: 100vh;
  background: var(--page-bg);
  padding-bottom: 90px;
}
.address-page :deep(.van-nav-bar) {
  background: var(--brand-grad);
}
.address-page :deep(.van-nav-bar__title) {
  color: #fff;
}
.address-page :deep(.van-nav-bar .van-icon) {
  color: #fff;
}
.addr-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 14px;
  margin: 10px 12px 0;
  padding: 14px;
  box-shadow: 0 2px 8px rgba(30, 90, 55, 0.05);
  cursor: pointer;
}
.addr-main {
  flex: 1;
  min-width: 0;
}
.addr-line1 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
}
.receiver {
  font-size: 15px;
  font-weight: 700;
  color: #2b2320;
}
.phone {
  font-size: 13px;
  color: #8a7a6c;
}
.default-tag {
  font-size: 10px;
  color: var(--brand-deep);
  background: var(--brand-light);
  border-radius: 8px;
  padding: 1px 7px;
}
.addr-text {
  font-size: 13px;
  color: #6b5d52;
  line-height: 1.4;
}
.addr-actions {
  display: flex;
  gap: 14px;
  padding-left: 12px;
  color: #b09884;
  flex-shrink: 0;
}
.addr-form {
  padding: 18px 0 10px;
  max-height: 85vh;
  overflow-y: auto;
}
.form-title {
  text-align: center;
  font-size: 15px;
  font-weight: 700;
  color: #2b2320;
  margin-bottom: 14px;
}
.add-bar {
  position: fixed;
  bottom: 16px;
  left: 16px;
  right: 16px;
}
/* 高德POI联想下拉 */
.poi-list {
  margin: 4px 16px 8px;
  background: #fff;
  border: 1px solid var(--brand-light);
  border-radius: 10px;
  overflow: hidden;
}
.poi-item {
  padding: 9px 13px;
  cursor: pointer;
}
.poi-item + .poi-item {
  border-top: 1px solid #eef4f0;
}
.poi-item:active {
  background: var(--brand-light);
}
.poi-name {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}
.poi-district {
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}
</style>
