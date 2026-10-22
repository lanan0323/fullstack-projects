<template>
  <!-- 轮播管理页面主容器 -->
  <div class="app-container banner-admin">
    <!-- 页头区域容器 -->
    <div class="admin-head">
      <!-- 页面主标题 -->
      <h2 class="admin-title">轮播管理</h2>
      <!-- 页面副标题说明 -->
      <p class="admin-sub">配置用户端市场首页的图片、文案、跳转链接和展示顺序</p>
    </div>

    <!-- 搜索筛选表单 -->
    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
      <!-- 标题搜索表单项 -->
      <el-form-item label="标题" prop="title">
        <!-- 轮播标题输入框，支持回车搜索 -->
        <el-input v-model="queryParams.title" placeholder="搜索轮播标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <!-- 状态筛选表单项 -->
      <el-form-item label="状态" prop="status">
        <!-- 状态下拉选择框 -->
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 130px">
          <!-- 已发布选项 -->
          <el-option label="已发布" value="0" />
          <!-- 已停用选项 -->
          <el-option label="已停用" value="1" />
        </el-select>
      </el-form-item>
      <!-- 搜索与重置按钮组 -->
      <el-form-item>
        <!-- 搜索按钮 -->
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <!-- 重置按钮 -->
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮行 -->
    <el-row :gutter="10" class="mb8">
      <!-- 新增按钮列 -->
      <el-col :span="1.5">
        <!-- 新增轮播按钮 -->
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['trade:banner:add']">新增</el-button>
      </el-col>
      <!-- 修改按钮列 -->
      <el-col :span="1.5">
        <!-- 修改轮播按钮，未选中单条时禁用 -->
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['trade:banner:edit']">修改</el-button>
      </el-col>
      <!-- 删除按钮列 -->
      <el-col :span="1.5">
        <!-- 批量删除按钮，无选中时禁用 -->
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['trade:banner:remove']">删除</el-button>
      </el-col>
      <!-- 右侧工具栏，控制搜索区显隐 -->
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 轮播数据列表表格 -->
    <el-table v-loading="loading" :data="bannerList" @selection-change="handleSelectionChange">
      <!-- 多选列 -->
      <el-table-column type="selection" width="55" align="center" />
      <!-- 排序列 -->
      <el-table-column label="排序" prop="sortOrder" align="center" width="80" />
      <!-- 预览列，显示缩略图 -->
      <el-table-column label="预览" width="180" align="center">
        <!-- 预览自定义渲染模板 -->
        <template #default="scope">
          <!-- 图片预览组件，支持点击放大 -->
          <el-image :src="resolveImgUrl(scope.row.imageUrl)" fit="cover" class="banner-thumb" :preview-src-list="[resolveImgUrl(scope.row.imageUrl)]" preview-teleported />
        </template>
      </el-table-column>
      <!-- 主标题列 -->
      <el-table-column label="主标题" prop="title" min-width="160" show-overflow-tooltip />
      <!-- 副标题列 -->
      <el-table-column label="副标题" prop="subtitle" min-width="220" show-overflow-tooltip />
      <!-- 跳转链接列 -->
      <el-table-column label="跳转链接" prop="linkUrl" min-width="200" show-overflow-tooltip />
      <!-- 状态列 -->
      <el-table-column label="状态" align="center" width="100">
        <!-- 状态自定义渲染模板 -->
        <template #default="scope">
          <!-- 状态标签，已发布为绿色，已停用为灰色 -->
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'">{{ scope.row.status === '0' ? '已发布' : '已停用' }}</el-tag>
        </template>
      </el-table-column>
      <!-- 更新时间列 -->
      <el-table-column label="更新时间" prop="updateTime" align="center" width="170">
        <!-- 更新时间自定义渲染模板，优先使用更新时间，否则用创建时间 -->
        <template #default="scope">{{ parseTime(scope.row.updateTime || scope.row.createTime) }}</template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" align="center" width="150">
        <!-- 操作按钮自定义渲染模板 -->
        <template #default="scope">
          <!-- 修改按钮 -->
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['trade:banner:edit']">修改</el-button>
          <!-- 删除按钮 -->
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['trade:banner:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑轮播弹窗 -->
    <el-dialog v-model="open" :title="title" width="680px" append-to-body @closed="reset">
      <!-- 轮播表单 -->
      <el-form ref="bannerRef" :model="form" :rules="rules" label-width="92px">
        <!-- 主标题表单项 -->
        <el-form-item label="主标题" prop="title"><el-input v-model="form.title" maxlength="128" show-word-limit /></el-form-item>
        <!-- 副标题表单项 -->
        <el-form-item label="副标题" prop="subtitle"><el-input v-model="form.subtitle" maxlength="256" show-word-limit /></el-form-item>
        <!-- 图片来源表单项，支持链接输入和本地上传 -->
        <el-form-item label="图片来源" prop="imageUrl">
          <!-- 图片链接输入框 -->
          <el-input v-model="form.imageUrl" placeholder="可直接粘贴图片链接，例如 https://..." clearable class="image-url-input" />
          <!-- 分隔线，区分链接输入和上传 -->
          <div class="source-divider"><span>或上传本地图片</span></div>
          <!-- 图片上传组件，限制1张，最大8MB -->
          <image-upload v-model="form.imageUrl" :limit="1" :file-size="8" />
          <!-- 图片尺寸提示信息 -->
          <div class="form-tip">图片链接和本地上传任选一种。推荐 1600 × 420 像素，图片内容避免放置关键文字。</div>
        </el-form-item>
        <!-- 点击跳转链接表单项 -->
        <el-form-item label="点击跳转" prop="linkUrl"><el-input v-model="form.linkUrl" placeholder="可选，用户点击轮播图后打开的页面" /></el-form-item>
        <!-- 排序和状态行布局 -->
        <el-row>
          <!-- 排序列，占半宽 -->
          <el-col :span="12">
            <!-- 展示排序表单项 -->
            <el-form-item label="展示排序" prop="sortOrder"><el-input-number v-model="form.sortOrder" :min="0" :precision="0" /></el-form-item>
          </el-col>
          <!-- 状态列，占半宽 -->
          <el-col :span="12">
            <!-- 发布状态表单项 -->
            <el-form-item label="发布状态" prop="status">
              <!-- 发布状态单选组 -->
              <el-radio-group v-model="form.status"><el-radio :label="0">发布</el-radio><el-radio :label="1">停用</el-radio></el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 备注表单项 -->
        <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" :rows="2" maxlength="256" show-word-limit /></el-form-item>
      </el-form>
      <!-- 弹窗底部按钮区域 -->
      <template #footer>
        <!-- 保存按钮，提交表单 -->
        <el-button type="primary" @click="submitForm">保存</el-button>
        <!-- 取消按钮，关闭弹窗 -->
        <el-button @click="open = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Banner">
// 导入轮播相关API方法
import { addBanner, delBanner, getBanner, listBanner, updateBanner } from '@/api/trade/banner'
// 导入图片URL解析工具方法
import { resolveImgUrl } from '@/utils/img'

// 获取当前组件实例代理
const { proxy } = getCurrentInstance()
// 表格加载状态
const loading = ref(false)
// 搜索区显隐状态
const showSearch = ref(true)
// 弹窗显隐状态
const open = ref(false)
// 弹窗标题
const title = ref('')
// 轮播列表数据
const bannerList = ref([])
// 数据总条数
const total = ref(0)
// 选中的行ID数组
const ids = ref([])
// 是否单选状态
const single = ref(true)
// 是否多选状态（无选中时为true）
const multiple = ref(true)

// 响应式数据对象，包含查询参数、表单和校验规则
const data = reactive({
  // 查询参数对象
  queryParams: { pageNum: 1, pageSize: 10, title: undefined, status: undefined },
  // 表单数据对象
  form: {},
  // 表单校验规则
  rules: {
    // 主标题必填校验
    title: [{ required: true, message: '请输入轮播主标题', trigger: 'blur' }],
    // 图片链接必填校验
    imageUrl: [{ required: true, message: '请填写图片链接或上传本地图片', trigger: ['blur', 'change'] }]
  }
})
// 从响应式数据中解构出查询参数、表单和规则的引用
const { queryParams, form, rules } = toRefs(data)

// 获取轮播列表数据
function getList() {
  // 开启加载状态
  loading.value = true
  // 调用API获取轮播列表
  listBanner(queryParams.value).then(res => {
    // 赋值列表数据，无数据时为空数组
    bannerList.value = res.rows || []
    // 赋值总条数，无数据时为0
    total.value = res.total || 0
  // 无论成功失败都关闭加载状态
  }).finally(() => { loading.value = false })
}
// 重置表单数据
function reset() {
  // 将表单重置为默认值
  form.value = { id: undefined, title: '', subtitle: '', imageUrl: '', linkUrl: '', sortOrder: 0, status: 0, remark: '' }
  // 调用代理重置表单方法
  proxy.resetForm('bannerRef')
}
// 搜索按钮处理函数，重置页码并查询
function handleQuery() { queryParams.value.pageNum = 1; getList() }
// 重置查询条件函数
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
// 表格多选变化处理函数
function handleSelectionChange(selection) {
  // 提取选中行的ID数组
  ids.value = selection.map(item => item.id)
  // 判断是否为单选（仅选中一行）
  single.value = selection.length !== 1
  // 判断是否无选中项
  multiple.value = selection.length === 0
}
// 新增按钮处理函数
function handleAdd() { reset(); open.value = true; title.value = '新增首页轮播图' }
// 修改按钮处理函数
function handleUpdate(row) {
  // 重置表单
  reset()
  // 调用API获取轮播详情
  getBanner(row.id || ids.value[0]).then(res => {
    // 用Object.assign合并到现有form对象，保持响应式代理不丢失
    Object.assign(form.value, res.data)
    // 确保status为数字类型，与el-radio的:value匹配
    if (form.value.status !== undefined && form.value.status !== null) {
      form.value.status = Number(form.value.status)
    }
    // 打开弹窗
    open.value = true
    // 设置弹窗标题为修改
    title.value = '修改首页轮播图'
  })
}
// 提交表单处理函数
function submitForm() {
  // 触发表单校验
  proxy.$refs.bannerRef.validate(valid => {
    // 校验不通过直接返回
    if (!valid) return
    // 根据是否有id决定调用更新或新增API
    const request = form.value.id ? updateBanner(form.value) : addBanner(form.value)
    // 请求成功后处理
    request.then(() => {
      // 显示操作成功提示
      proxy.$modal.msgSuccess(form.value.id ? '修改成功' : '新增成功')
      // 关闭弹窗
      open.value = false
      // 刷新列表
      getList()
    })
  })
}
// 删除按钮处理函数
function handleDelete(row) {
  // 获取行ID或选中ID数组
  const targetIds = row.id || ids.value
  // 弹出确认框，确认后调用删除API
  proxy.$modal.confirm('确认删除选中的轮播图吗？删除后首页将不再展示。').then(() => delBanner(targetIds)).then(() => {
    // 显示删除成功提示
    proxy.$modal.msgSuccess('删除成功')
    // 刷新列表
    getList()
  }).catch(() => {})
}

// 页面首次挂载时查询列表
import { onMounted, onActivated } from 'vue'
onMounted(() => {
  getList()
})
// keep-alive缓存激活时重新查询列表
onActivated(() => {
  getList()
})
</script>

<style scoped>
/* 轮播缩略图样式 */
.banner-thumb { width: 150px; height: 54px; border-radius: 4px; background: #f2f5f8; }
/* 图片链接输入框宽度 */
.image-url-input { width: 100%; }
/* 分隔线容器样式 */
.source-divider { width: 100%; display: flex; align-items: center; gap: 10px; margin: 10px 0; color: #909399; font-size: 12px; }
/* 分隔线伪元素，绘制左右横线 */
.source-divider::before,
.source-divider::after { content: ''; flex: 1; height: 1px; background: var(--el-border-color-lighter); }
/* 表单提示信息样式 */
.form-tip { color: #909399; font-size: 12px; line-height: 1.5; margin-top: 6px; }
</style>
