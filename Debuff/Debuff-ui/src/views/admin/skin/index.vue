<template>
  <!-- 饰品管理页面主容器 -->
  <div class="app-container skin-admin">
    <!-- 页头 -->
    <!-- 页头区域容器 -->
    <div class="admin-head">
      <!-- 页头内容包裹层 -->
      <div>
        <!-- 页面主标题 -->
        <h2 class="admin-title">饰品管理</h2>
        <!-- 页面副标题说明 -->
        <p class="admin-sub">维护皮肤模板定义、图片与上下架状态</p>
      </div>
    </div>

    <!-- 筛选区 -->
    <!-- 搜索筛选表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="76px" class="admin-filter">
      <!-- 饰品名称搜索表单项 -->
      <el-form-item label="饰品名称">
        <!-- 饰品名称输入框，支持回车搜索 -->
        <el-input v-model="queryParams.skinName" placeholder="请输入饰品名称" clearable style="width: 180px"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <!-- 武器类型筛选表单项 -->
      <el-form-item label="武器">
        <!-- 武器类型下拉选择框 -->
        <el-select v-model="queryParams.weaponTypeId" placeholder="全部武器" clearable style="width: 150px">
          <!-- 遍历所有武器类型生成下拉选项 -->
          <el-option v-for="wt in allWeaponTypes" :key="wt.id" :label="wt.weaponName" :value="wt.id" />
        </el-select>
      </el-form-item>
      <!-- 品质筛选表单项 -->
      <el-form-item label="品质">
        <!-- 品质下拉选择框，带颜色标识 -->
        <el-select v-model="queryParams.qualityId" placeholder="全部品质" clearable style="width: 140px">
          <!-- 遍历品质列表生成下拉选项 -->
          <el-option v-for="q in qualities" :key="q.id" :label="q.qualityName" :value="q.id">
            <!-- 品质名称带颜色圆点显示 -->
            <span :style="{ color: q.qualityColor }">● {{ q.qualityName }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <!-- 状态筛选表单项 -->
      <el-form-item label="状态">
        <!-- 状态下拉选择框 -->
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 110px">
          <!-- 上架选项 -->
          <el-option label="上架" value="0" />
          <!-- 下架选项 -->
          <el-option label="下架" value="1" />
        </el-select>
      </el-form-item>
      <!-- 在售筛选开关表单项 -->
      <el-form-item>
        <!-- 仅显示在售开关，切换时触发查询 -->
        <el-switch v-model="queryParams.inMarket" active-text="仅显示在售" @change="handleQuery" />
      </el-form-item>
      <!-- 搜索与重置按钮组 -->
      <el-form-item>
        <!-- 搜索按钮 -->
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <!-- 重置按钮 -->
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作栏 -->
    <!-- 操作按钮行 -->
    <el-row :gutter="10" class="mb8">
      <!-- 新增按钮列 -->
      <el-col :span="1.5">
        <!-- 新增饰品按钮 -->
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['admin:skin:add']">
          新增饰品
        </el-button>
      </el-col>
      <!-- 删除按钮列 -->
      <el-col :span="1.5">
        <!-- 批量删除按钮，无选中时禁用 -->
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['admin:skin:remove']">删除</el-button>
      </el-col>
      <!-- 右侧工具栏，控制搜索区显隐 -->
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 表格 -->
    <!-- 饰品数据列表表格 -->
    <el-table v-loading="loading" :data="skinList" @selection-change="handleSelectionChange" class="admin-table">
      <!-- 多选列 -->
      <el-table-column type="selection" width="50" align="center" />
      <!-- 饰品信息列，包含图片和名称 -->
      <el-table-column label="饰品" align="left" min-width="180">
        <!-- 饰品信息自定义渲染模板 -->
        <template #default="scope">
          <!-- 饰品单元格容器 -->
          <div class="skin-cell">
            <!-- 饰品图片容器 -->
            <div class="cell-img">
              <!-- 饰品图片组件，加载失败时显示占位文字 -->
              <el-image :src="resolveImgUrl(scope.row.imageUrl)" fit="cover"
                style="width: 100%; height: 100%">
                <!-- 图片加载失败时的占位内容 -->
                <template #error><span class="cell-img-empty">SKIN</span></template>
              </el-image>
            </div>
            <!-- 饰品名称文字 -->
            <span class="cell-name">{{ scope.row.skinName }}</span>
          </div>
        </template>
      </el-table-column>
      <!-- 磨损度列 -->
      <el-table-column label="磨损" align="center" width="100">
        <!-- 磨损度自定义渲染模板 -->
        <template #default="scope">
          <!-- 磨损名称文字，无数据时显示占位符 -->
          <span class="wear-text">{{ scope.row.wearName || '--' }}</span>
        </template>
      </el-table-column>
      <!-- 武器名称列 -->
      <el-table-column label="武器" align="center" prop="weaponName" width="110" />
      <!-- 品质列，带颜色标签 -->
      <el-table-column label="品质" align="center" width="110">
        <!-- 品质自定义渲染模板 -->
        <template #default="scope">
          <!-- 品质标签，使用品质颜色作为文字色、边框和背景 -->
          <span class="quality-chip" :style="{ color: scope.row.qualityColor, borderColor: scope.row.qualityColor + '55', background: scope.row.qualityColor + '14' }">
            <!-- 品质名称文字 -->
            {{ scope.row.qualityName }}
          </span>
        </template>
      </el-table-column>
      <!-- 参考价列 -->
      <el-table-column label="参考价" align="center" width="110">
        <!-- 参考价自定义渲染模板 -->
        <template #default="scope">
          <!-- 价格文字，负数标红 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.minPrice) < 0 }">¥{{ formatPrice(scope.row.minPrice) }}</span>
        </template>
      </el-table-column>
      <!-- 状态列 -->
      <el-table-column label="状态" align="center" width="90">
        <!-- 状态自定义渲染模板 -->
        <template #default="scope">
          <!-- 状态标签，上架为绿色，下架为灰色 -->
          <span class="status-chip" :class="scope.row.status === '0' ? 'on' : 'off'">
            <!-- 根据状态值显示对应文字 -->
            {{ scope.row.status === '0' ? '上架中' : '已下架' }}
          </span>
        </template>
      </el-table-column>
      <!-- 创建时间列 -->
      <el-table-column label="创建时间" align="center" width="160" prop="createTime">
        <!-- 创建时间自定义渲染模板 -->
        <template #default="scope">
          <!-- 格式化时间显示，使用次要文字样式 -->
          <span class="muted-text">{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <!-- 操作按钮自定义渲染模板 -->
        <template #default="scope">
          <!-- 编辑按钮 -->
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['admin:skin:edit']">编辑</el-button>
          <!-- 删除按钮 -->
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['admin:skin:remove']">删除</el-button>
          <!-- 上架/下架切换按钮，根据当前状态显示不同文字和颜色 -->
          <el-button link :type="scope.row.status === '0' ? 'warning' : 'success'"
            @click="handleChangeStatus(scope.row)" v-hasPermi="['admin:skin:edit']">
            <!-- 根据状态值显示上架或下架 -->
            {{ scope.row.status === '0' ? '下架' : '上架' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑弹窗 -->
    <!-- 新增/编辑饰品弹窗 -->
    <el-dialog v-model="open" :title="title" width="620px" append-to-body @closed="reset">
      <!-- 饰品表单 -->
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <!-- 表单两列布局行 -->
        <el-row :gutter="16">
          <!-- 饰品名称列，占半宽 -->
          <el-col :span="12">
            <!-- 饰品名称表单项 -->
            <el-form-item label="饰品名称" prop="skinName">
              <!-- 饰品名称输入框 -->
              <el-input v-model="form.skinName" placeholder="如：火蛇" />
            </el-form-item>
          </el-col>
          <!-- 参考价列，占半宽 -->
          <el-col :span="12">
            <!-- 参考价表单项 -->
            <el-form-item label="参考价" prop="minPrice">
              <!-- 参考价数字输入框，最小0，精度2位小数 -->
              <el-input-number v-model="form.minPrice" :min="0" :precision="2" :step="10"
                style="width: 100%" />
            </el-form-item>
          </el-col>
          <!-- 武器选择列，占半宽 -->
          <el-col :span="12">
            <!-- 武器表单项 -->
            <el-form-item label="武器" prop="weaponTypeId">
              <!-- 武器下拉选择框 -->
              <el-select v-model="form.weaponTypeId" placeholder="请选择武器" style="width: 100%">
                <!-- 遍历所有武器类型生成下拉选项 -->
                <el-option v-for="wt in allWeaponTypes" :key="wt.id" :label="wt.weaponName" :value="wt.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <!-- 品质选择列，占半宽 -->
          <el-col :span="12">
            <!-- 品质表单项 -->
            <el-form-item label="品质" prop="qualityId">
              <!-- 品质下拉选择框，带颜色标识 -->
              <el-select v-model="form.qualityId" placeholder="请选择品质" style="width: 100%">
                <!-- 遍历品质列表生成下拉选项 -->
                <el-option v-for="q in qualities" :key="q.id" :label="q.qualityName" :value="q.id">
                  <!-- 品质名称带颜色圆点显示 -->
                  <span :style="{ color: q.qualityColor }">● {{ q.qualityName }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <!-- 磨损度列，占半宽 -->
          <el-col :span="12">
            <!-- 磨损度表单项 -->
            <el-form-item label="磨损度" prop="floatValue">
              <!-- 磨损度数字输入框，范围0-1，精度6位小数 -->
              <el-input-number v-model="form.floatValue" :min="0" :max="1" :precision="6" :step="0.01"
                :controls="false" style="width: 100%" />
              <!-- 磨损度对应的磨损名称提示 -->
              <div class="wear-tip">{{ getWearName(form.floatValue) }}</div>
            </el-form-item>
          </el-col>
          <!-- 状态列，占半宽 -->
          <el-col :span="12">
            <!-- 状态表单项 -->
            <el-form-item label="状态" prop="status">
              <!-- 状态单选组 -->
              <el-radio-group v-model="form.status">
                <!-- 上架选项 -->
                <el-radio :label="'0'">上架</el-radio>
                <!-- 下架选项 -->
                <el-radio :label="'1'">下架</el-radio>
              </el-radio-group>
              <!-- 状态说明提示文字 -->
              <div class="status-tip">上架将自动按参考价挂单，用户端立即可购买</div>
            </el-form-item>
          </el-col>
          <!-- 描述列，占满宽 -->
          <el-col :span="24">
            <!-- 描述表单项 -->
            <el-form-item label="描述" prop="description">
              <!-- 描述多行文本输入框 -->
              <el-input v-model="form.description" type="textarea" :rows="2" placeholder="饰品描述" />
            </el-form-item>
          </el-col>
          <!-- 图片列，占满宽 -->
          <el-col :span="24">
            <!-- 饰品图片表单项 -->
            <el-form-item label="饰品图片">
              <!-- 图片URL输入框 -->
              <el-input v-model="form.imageUrl" placeholder="请输入图片URL，如Steam CDN链接" />
              <!-- 图片预览，有URL时显示 -->
              <el-image v-if="form.imageUrl" :src="form.imageUrl" fit="cover"
                style="width: 72px; height: 72px; border-radius: 6px; margin-top: 8px" />
            </el-form-item>
          </el-col>
        </el-row>
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

<script setup name="AdminSkin">
// 导入Vue组合式API
import { ref, reactive, computed, onMounted, onActivated } from 'vue'
// 导入Element Plus消息提示和确认框组件
import { ElMessage, ElMessageBox } from 'element-plus'
// 导入饰品相关API方法
import { listSkin, addSkin, updateSkin, delSkin, changeSkinStatus } from '@/api/admin/skin'
// 导入分类数据API方法
import { getCategories } from '@/api/market'
// 导入图片URL解析工具方法
import { resolveImgUrl } from '@/utils/img'

// 表格加载状态
const loading = ref(false)
// 饰品列表数据
const skinList = ref([])
// 数据总条数
const total = ref(0)
// 搜索区显隐状态
const showSearch = ref(true)
// 选中的行ID数组
const ids = ref([])
// 是否多选状态（无选中时为true）
const multiple = ref(true)
// 武器分类数据
const categories = ref([])
// 品质列表数据
const qualities = ref([])

// 计算属性：将所有分类下的武器类型合并为扁平数组
const allWeaponTypes = computed(() => {
  // 遍历所有分类，提取武器类型并展平
  return (categories.value || []).flatMap(c => c.weaponTypes || [])
})

// 查询参数响应式对象
const queryParams = reactive({
  // 当前页码
  pageNum: 1,
  // 每页条数
  pageSize: 10,
  // 饰品名称筛选
  skinName: undefined,
  // 武器类型ID筛选
  weaponTypeId: undefined,
  // 品质ID筛选
  qualityId: undefined,
  // 状态筛选
  status: undefined,
  // 是否仅显示在售（默认true）
  inMarket: true
})

// 获取饰品列表数据（异步函数）
const getList = async () => {
  // 开启加载状态
  loading.value = true
  try {
    // 调用API获取饰品列表
    const res = await listSkin(queryParams)
    // 赋值列表数据，无数据时为空数组
    skinList.value = res.rows || []
    // 赋值总条数，无数据时为0
    total.value = res.total || 0
  } finally {
    // 无论成功失败都关闭加载状态
    loading.value = false
  }
}

// 加载武器分类和品质数据（异步函数）
const loadCategories = async () => {
  // 调用API获取分类数据
  const res = await getCategories()
  // 赋值武器分类数据
  categories.value = res.data?.weaponCategories || []
  // 赋值品质数据
  qualities.value = res.data?.qualities || []
}

// 搜索按钮处理函数
const handleQuery = () => {
  // 重置页码为1
  queryParams.pageNum = 1
  // 执行查询
  getList()
}

// 重置查询条件函数
const resetQuery = () => {
  // 重置饰品名称筛选
  queryParams.skinName = undefined
  // 重置武器类型筛选
  queryParams.weaponTypeId = undefined
  // 重置品质筛选
  queryParams.qualityId = undefined
  // 重置状态筛选
  queryParams.status = undefined
  // 重置在售开关为默认值
  queryParams.inMarket = true
  // 重新执行查询
  handleQuery()
}

// 表格多选变化处理函数
const handleSelectionChange = (selection) => {
  // 提取选中行的ID数组
  ids.value = selection.map(item => item.id)
  // 判断是否无选中项
  multiple.value = !selection.length
}

// 金额格式化函数，空值显示--，否则保留两位小数
const formatPrice = (v) => {
  // 返回格式化后的金额字符串
  return v === null || v === undefined ? '--' : Number(v).toFixed(2)
}

// 根据磨损度获取磨损名称
// 根据磨损度数值返回对应的磨损等级名称
const getWearName = (floatValue) => {
  // 空值返回空字符串
  if (floatValue === null || floatValue === undefined) return ''
  // 转换为数字
  const val = Number(floatValue)
  // 0-0.07：崭新出厂
  if (val <= 0.07) return '崭新出厂'
  // 0.07-0.15：略有磨损
  if (val <= 0.15) return '略有磨损'
  // 0.15-0.38：久经沙场
  if (val <= 0.38) return '久经沙场'
  // 0.38-0.45：破损不堪
  if (val <= 0.45) return '破损不堪'
  // 大于0.45：战痕累累
  return '战痕累累'
}

// ---------- 新增/修改 ----------
// 弹窗显隐状态
const open = ref(false)
// 弹窗标题
const title = ref('')
// 表单引用
const formRef = ref(null)
// 表单响应式数据对象
const form = reactive({})
// 表单校验规则
const rules = {
  // 饰品名称必填校验
  skinName: [{ required: true, message: '饰品名称不能为空', trigger: 'blur' }],
  // 武器必选校验
  weaponTypeId: [{ required: true, message: '请选择武器', trigger: 'change' }],
  // 品质必选校验
  qualityId: [{ required: true, message: '请选择品质', trigger: 'change' }],
  // 参考价自定义校验：上架状态时必须大于0
  minPrice: [{
    // 自定义校验器函数
    validator: (rule, value, callback) => {
      // 上架状态且价格无效时报错
      if (form.status === '0' && (value === null || value === undefined || value <= 0)) {
        callback(new Error('上架状态需设置参考价（大于0）'))
      } else {
        // 校验通过
        callback()
      }
    },
    // 触发时机：失焦和值变化时
    trigger: ['blur', 'change']
  }]
}

// 重置表单数据函数
const reset = () => {
  // 清除表单所有已有字段
  Object.keys(form).forEach(k => delete form[k])
  // 默认状态为上架
  form.status = '0'
  // 默认磨损度为0.15（略有磨损）
  form.floatValue = 0.15 // 默认略有磨损
}

// 新增按钮处理函数
const handleAdd = () => {
  // 重置表单
  reset()
  // 设置弹窗标题
  title.value = '新增饰品'
  // 打开弹窗
  open.value = true
}

// 修改按钮处理函数
const handleUpdate = (row) => {
  // 重置表单
  reset()
  // 设置弹窗标题
  title.value = '修改饰品'
  // 将行数据赋值到表单对象
  Object.assign(form, {
    // 饰品ID
    id: row.id,
    // 饰品名称
    skinName: row.skinName,
    // 武器类型ID
    weaponTypeId: row.weaponTypeId,
    // 品质ID
    qualityId: row.qualityId,
    // 图片URL，无则为空字符串
    imageUrl: row.imageUrl || '',
    // 描述，无则为空字符串
    description: row.description || '',
    // 参考价，空值转为0
    minPrice: row.minPrice === null || row.minPrice === undefined ? 0 : Number(row.minPrice),
    // 状态，默认为上架
    status: row.status || '0',
    // 磨损度，空值默认为0.15
    floatValue: row.floatValue === null || row.floatValue === undefined ? 0.15 : Number(row.floatValue)
  })
  // 打开弹窗
  open.value = true
}

// 提交表单处理函数
const submitForm = () => {
  // 触发表单校验
  formRef.value.validate(async (valid) => {
    // 校验不通过直接返回
    if (!valid) return
    // 有id表示编辑模式
    if (form.id) {
      // 调用更新API
      const res = await updateSkin(form)
      // 根据返回码显示成功或失败提示
      res.code === 200 ? ElMessage.success('修改成功') : ElMessage.error(res.msg || '修改失败')
    } else {
      // 调用新增API
      const res = await addSkin(form)
      // 根据返回码显示成功或失败提示
      res.code === 200 ? ElMessage.success('新增成功') : ElMessage.error(res.msg || '新增失败')
    }
    // 关闭弹窗
    open.value = false
    // 刷新列表
    getList()
  })
}

// ---------- 删除 ----------
// 删除按钮处理函数
const handleDelete = (row) => {
  // 获取行ID或选中ID数组
  const skinIds = row.id || ids.value
  // 弹出确认框
  ElMessageBox.confirm('是否确认删除选中的饰品？将同时清理该饰品下的全部库存与挂单数据（有订单/求购记录的饰品无法删除）。', '系统提示', {
    // 确认按钮文字
    confirmButtonText: '确定',
    // 取消按钮文字
    cancelButtonText: '取消',
    // 对话框类型
    type: 'warning'
  }).then(async () => {
    // 确认后调用删除API
    const res = await delSkin(skinIds)
    // 根据返回码处理结果
    if (res.code === 200) {
      // 删除成功提示
      ElMessage.success('删除成功')
      // 刷新列表
      getList()
    } else {
      // 删除失败提示
      ElMessage.error(res.msg || '删除失败')
    }
  }).catch(() => {});
}

// ---------- 上架/下架 ----------
// 上架/下架切换处理函数
const handleChangeStatus = (row) => {
  // 计算目标状态（取反）
  const target = row.status === '0' ? '1' : '0'
  // 计算操作名称
  const action = target === '0' ? '上架' : '下架'
  // 根据操作类型生成不同的确认提示
  const tip = target === '0'
    ? '上架后将以最低价 ¥' + (row.minPrice ?? 0) + ' 自动挂单出售，用户端即可购买。确定吗？'
    : '下架将撤回该饰品全部在售挂单，库存退回持有者。确定吗？'
  // 弹出确认框
  ElMessageBox.confirm('确定要' + action + '饰品「' + row.skinName + '」吗？' + tip, '系统提示', {
    // 确认按钮文字
    confirmButtonText: '确定',
    // 取消按钮文字
    cancelButtonText: '取消',
    // 对话框类型
    type: 'warning'
  }).then(async () => {
    // 确认后调用状态切换API
    const res = await changeSkinStatus({ id: row.id, status: target })
    // 根据返回码处理结果
    if (res.code === 200) {
      // 操作成功提示
      ElMessage.success(action + '成功')
      // 刷新列表
      getList()
    } else {
      // 操作失败提示
      ElMessage.error(res.msg || action + '失败')
    }
  }).catch(() => {});
}

// 组件挂载时执行初始化操作
onMounted(() => {
  // 加载武器分类和品质数据
  loadCategories()
  // 加载饰品列表数据
  getList()
})
// keep-alive缓存激活时重新加载数据
onActivated(() => {
  loadCategories()
  getList()
})
</script>

<style scoped>
/* 饰品管理页面顶部内边距 */
.skin-admin {
  padding-top: 4px;
}
/* 状态说明提示文字样式 */
.status-tip {
  margin-top: 2px;
  font-size: 12px;
  line-height: 1.6;
  color: var(--el-text-color-secondary);
}
/* 磨损度提示文字样式 */
.wear-tip {
  margin-top: 2px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
/* 页头区域样式 */
.admin-head {
  margin-bottom: 16px;
}
/* 页面主标题样式 */
.admin-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}
/* 页面副标题样式 */
.admin-sub {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
/* 筛选区域容器样式 */
.admin-filter {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  padding: 14px 12px 2px;
  margin-bottom: 12px;
}
/* 饰品单元格布局（图片+名称） */
.admin-table .skin-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
/* 饰品图片容器样式 */
.cell-img {
  width: 42px;
  height: 42px;
  border-radius: 6px;
  overflow: hidden;
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-lighter);
  flex-shrink: 0;
}
/* 图片加载失败占位文字样式 */
.cell-img-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-size: 9px;
  letter-spacing: 2px;
  color: #c0c4cc;
}
/* 饰品名称文字样式 */
.cell-name {
  font-weight: 600;
  color: var(--el-text-color-primary);
}
/* 品质标签样式 */
.quality-chip {
  display: inline-block;
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 20px;
  border: 1px solid;
  font-weight: 500;
}
/* 金额文本基础样式 */
.price-text {
  font-weight: 600;
  color: #67c23a;
  font-variant-numeric: tabular-nums;
}
/* 负数金额样式 */
.price-text.price-negative {
  color: #f56c6c;
}
/* 状态标签基础样式 */
.status-chip {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 20px;
}
/* 上架状态样式（绿色） */
.status-chip.on {
  color: #67c23a;
  background: rgba(103, 194, 58, 0.1);
  border: 1px solid rgba(103, 194, 58, 0.35);
}
/* 下架状态样式（灰色） */
.status-chip.off {
  color: var(--el-text-color-secondary);
  background: rgba(144, 147, 153, 0.12);
  border: 1px solid rgba(144, 147, 153, 0.35);
}
/* 次要文字样式（如时间） */
.muted-text {
  color: var(--el-text-color-secondary);
}
/* 上传行布局样式 */
.upload-row {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
}
/* 上传预览区域样式 */
.upload-preview {
  display: flex;
  align-items: center;
  gap: 8px;
}
/* 预览提示文字样式 */
.preview-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
/* 磨损度文字样式 */
.wear-text {
  font-size: 13px;
  color: var(--el-text-color-primary);
}
</style>
