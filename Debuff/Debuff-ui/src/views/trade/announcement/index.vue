<template>
  <!-- 公告管理页面主容器 -->
  <div class="app-container announcement-admin">
    <!-- 页头 -->
    <!-- 页头区域容器 -->
    <div class="admin-head">
      <!-- 页面主标题 -->
      <h2 class="admin-title">公告管理</h2>
      <!-- 页面副标题说明 -->
      <p class="admin-sub">维护公告发布与展示状态</p>
    </div>
    <!-- 搜索筛选表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px" class="admin-filter">
      <!-- 公告标题搜索表单项 -->
      <el-form-item label="公告标题" prop="title">
        <!-- 公告标题输入框，支持回车搜索 -->
        <el-input
          v-model="queryParams.title"
          placeholder="请输入公告标题"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <!-- 类型筛选表单项 -->
      <el-form-item label="类型" prop="announcementType">
        <!-- 类型下拉选择框，绑定字典数据 -->
        <el-select v-model="queryParams.announcementType" placeholder="请选择类型" clearable>
          <!-- 遍历字典选项生成下拉项 -->
          <el-option
            v-for="dict in announcement_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
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
        <!-- 新增公告按钮 -->
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['trade:announcement:add']"
        >新增</el-button>
      </el-col>
      <!-- 修改按钮列 -->
      <el-col :span="1.5">
        <!-- 修改公告按钮，未选中单条时禁用 -->
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['trade:announcement:edit']"
        >修改</el-button>
      </el-col>
      <!-- 删除按钮列 -->
      <el-col :span="1.5">
        <!-- 批量删除按钮，无选中时禁用 -->
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['trade:announcement:remove']"
        >删除</el-button>
      </el-col>
      <!-- 导出按钮列 -->
      <el-col :span="1.5">
        <!-- 导出数据按钮 -->
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['trade:announcement:export']"
        >导出</el-button>
      </el-col>
      <!-- 右侧工具栏，控制搜索区显隐 -->
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 公告数据列表表格 -->
    <el-table v-loading="loading" :data="announcementList" @selection-change="handleSelectionChange" class="admin-table">
      <!-- 多选列 -->
      <el-table-column type="selection" width="55" align="center" />
      <!-- 序号列，根据分页计算 -->
      <el-table-column label="序号" align="center" type="index" width="60"
        :index="(i) => (queryParams.pageNum - 1) * queryParams.pageSize + i + 1" />
      <!-- 公告标题列，超出显示tooltip -->
      <el-table-column label="公告标题" align="center" prop="title" width="200" show-overflow-tooltip />
      <!-- 公告内容列，使用stripHtml去除HTML标签，最小宽度260 -->
      <el-table-column label="公告内容" align="center" prop="content" min-width="260" show-overflow-tooltip :formatter="stripHtml" />
      <!-- 类型列，使用字典标签渲染 -->
      <el-table-column label="类型" align="center" prop="announcementType" width="100">
        <!-- 类型自定义渲染模板 -->
        <template #default="scope">
          <!-- 字典标签，根据类型值显示对应名称 -->
          <dict-tag :options="announcement_type" :value="scope.row.announcementType"/>
        </template>
      </el-table-column>
      <!-- 状态列 -->
      <el-table-column label="状态" align="center" prop="status" width="90">
        <!-- 状态自定义渲染模板 -->
        <template #default="scope">
          <!-- 状态标签，发布为绿色，停用为灰色 -->
          <span class="status-chip" :class="scope.row.status === '0' ? 'on' : 'off'">
            <!-- 根据状态值显示对应文字 -->
            {{ scope.row.status === '0' ? '发布' : '停用' }}
          </span>
        </template>
      </el-table-column>
      <!-- 备注列 -->
      <el-table-column label="备注" align="center" prop="remark" width="160" show-overflow-tooltip />
      <!-- 操作列 -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <!-- 操作按钮自定义渲染模板 -->
        <template #default="scope">
          <!-- 修改按钮 -->
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['trade:announcement:edit']">修改</el-button>
          <!-- 删除按钮 -->
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['trade:announcement:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改系统公告对话框 -->
    <!-- 新增/编辑公告弹窗 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <!-- 公告表单 -->
      <el-form ref="announcementRef" :model="form" :rules="rules" label-width="80px">
        <!-- 公告标题表单项 -->
        <el-form-item label="公告标题" prop="title">
          <!-- 公告标题输入框 -->
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <!-- 公告内容表单项，使用富文本编辑器 -->
        <el-form-item label="公告内容">
          <!-- 富文本编辑器组件，最小高度192px -->
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <!-- 类型表单项 -->
        <el-form-item label="类型" prop="announcementType">
          <!-- 类型下拉选择框 -->
          <el-select v-model="form.announcementType" placeholder="请选择类型">
            <!-- 遍历字典选项生成下拉项 -->
            <el-option
              v-for="dict in announcement_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <!-- 备注表单项 -->
        <el-form-item label="备注" prop="remark">
          <!-- 备注输入框 -->
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <!-- 弹窗底部按钮区域 -->
      <template #footer>
        <!-- 底部按钮容器 -->
        <div class="dialog-footer">
          <!-- 确定按钮，提交表单 -->
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <!-- 取消按钮，关闭弹窗 -->
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="announcement">
// 导入公告相关API方法
import { listAnnouncement, getAnnouncement, delAnnouncement, addAnnouncement, updateAnnouncement } from "@/api/trade/announcement";

// 获取当前组件实例代理
const { proxy } = getCurrentInstance();
// 获取公告类型字典数据
const { announcement_type } = proxy.useDict('announcement_type');

// 公告列表数据
const announcementList = ref([]);
// 弹窗显隐状态
const open = ref(false);
// 表格加载状态
const loading = ref(true);
// 搜索区显隐状态
const showSearch = ref(true);
// 选中的行ID数组
const ids = ref([]);
// 是否单选状态
const single = ref(true);
// 是否多选状态（无选中时为true）
const multiple = ref(true);
// 数据总条数
const total = ref(0);
// 弹窗标题
const title = ref("");

// 响应式数据对象，包含表单、查询参数和校验规则
const data = reactive({
  // 表单数据对象
  form: {},
  // 查询参数对象
  queryParams: {
    // 当前页码
    pageNum: 1,
    // 每页条数
    pageSize: 10,
    // 标题筛选
    title: null,
    // 内容筛选
    content: null,
    // 类型筛选
    announcementType: null,
    // 状态筛选
    status: null,
  },
  // 表单校验规则
  rules: {
    // 标题必填校验
    title: [
      { required: true, message: "公告标题不能为空", trigger: "blur" }
    ],
  }
});

// 从响应式数据中解构出查询参数、表单和规则的引用
const { queryParams, form, rules } = toRefs(data);

/** 去掉HTML标签,列表只显示纯文本 */
// 去除HTML标签的格式化函数，用于表格内容列显示纯文本
function stripHtml(row, column) {
  // 获取当前列对应的原始HTML内容
  const html = row[column.property]
  // 空值返回空字符串
  if (!html) return ''
  // 去除HTML标签，合并多余空白
  return String(html).replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

/** 查询系统公告列表 */
// 获取公告列表数据
function getList() {
  // 开启加载状态
  loading.value = true;
  // 调用API获取公告列表
  listAnnouncement(queryParams.value).then(response => {
    // 赋值列表数据
    announcementList.value = response.rows;
    // 赋值总条数
    total.value = response.total;
    // 关闭加载状态
    loading.value = false;
  });
}

// 取消按钮
// 取消按钮处理函数
function cancel() {
  // 关闭弹窗
  open.value = false;
  // 重置表单
  reset();
}

// 表单重置
// 重置表单数据函数
function reset() {
  // 将表单所有字段重置为null
  form.value = {
    id: null,
    title: null,
    content: null,
    announcementType: null,
    status: null,
    createBy: null,
    createTime: null,
    updateTime: null,
    remark: null
  };
  // 调用代理重置表单方法
  proxy.resetForm("announcementRef");
}

/** 搜索按钮操作 */
// 搜索按钮处理函数
function handleQuery() {
  // 重置页码为1
  queryParams.value.pageNum = 1;
  // 执行查询
  getList();
}

/** 重置按钮操作 */
// 重置查询条件函数
function resetQuery() {
  // 重置搜索表单
  proxy.resetForm("queryRef");
  // 重新查询
  handleQuery();
}

// 多选框选中数据
// 表格多选变化处理函数
function handleSelectionChange(selection) {
  // 提取选中行的ID数组
  ids.value = selection.map(item => item.id);
  // 判断是否为单选（仅选中一行）
  single.value = selection.length != 1;
  // 判断是否无选中项
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
// 新增按钮处理函数
function handleAdd() {
  // 重置表单
  reset();
  // 打开弹窗
  open.value = true;
  // 设置弹窗标题
  title.value = "添加系统公告";
}

/** 修改按钮操作 */
// 修改按钮处理函数
function handleUpdate(row) {
  // 重置表单
  reset();
  // 获取行ID或选中ID数组的第一个
  const _id = row.id || ids.value
  // 调用API获取公告详情
  getAnnouncement(_id).then(response => {
    // 将返回数据赋值给表单
    form.value = response.data;
    // 打开弹窗
    open.value = true;
    // 设置弹窗标题为修改
    title.value = "修改系统公告";
  });
}

/** 提交按钮 */
// 提交表单处理函数
function submitForm() {
  // 触发表单校验
  proxy.$refs["announcementRef"].validate(valid => {
    // 校验通过时执行
    if (valid) {
      // 判断是编辑还是新增（有id为编辑）
      if (form.value.id != null) {
        // 调用更新API
        updateAnnouncement(form.value).then(response => {
          // 显示修改成功提示
          proxy.$modal.msgSuccess("修改成功");
          // 关闭弹窗
          open.value = false;
          // 刷新列表
          getList();
        });
      } else {
        // 调用新增API
        addAnnouncement(form.value).then(response => {
          // 显示新增成功提示
          proxy.$modal.msgSuccess("新增成功");
          // 关闭弹窗
          open.value = false;
          // 刷新列表
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
// 删除按钮处理函数
function handleDelete(row) {
  // 获取行ID或选中ID数组
  const _ids = row.id || ids.value;
  // 弹出确认框，确认后调用删除API
  proxy.$modal.confirm('是否确认删除系统公告编号为"' + _ids + '"的数据项？').then(function() {
    // 执行删除操作
    return delAnnouncement(_ids);
  }).then(() => {
    // 删除成功后刷新列表
    getList();
    // 显示删除成功提示
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
// 导出按钮处理函数
function handleExport() {
  // 调用代理下载方法，导出Excel文件
  proxy.download('trade/announcement/export', {
    // 展开查询参数
    ...queryParams.value
  }, `announcement_${new Date().getTime()}.xlsx`)
}

// 页面首次挂载时查询列表
import { onMounted, onActivated } from 'vue'
onMounted(() => {
  getList();
})
// keep-alive缓存激活时重新查询列表
onActivated(() => {
  getList();
})
</script>
