<!-- 流水管理 -->
<template>
  <!-- 流水管理页面主容器 -->
  <div class="app-container walletlog-admin">
    <!-- 页头 -->
    <!-- 页头区域容器 -->
    <div class="admin-head">
      <!-- 页面主标题 -->
      <h2 class="admin-title">流水管理</h2>
      <!-- 页面副标题说明 -->
      <p class="admin-sub">查看钱包资金变动明细</p>
    </div>
    <!-- 搜索筛选表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px" class="admin-filter">
      <!-- 用户名搜索表单项 -->
      <el-form-item label="用户名" prop="userName">
        <!-- 用户名输入框，限制宽度，支持回车搜索 -->
        <el-input
          v-model="queryParams.userName"
          placeholder="用户名/昵称"
          clearable
          style="width: 130px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <!-- 类型筛选表单项 -->
      <el-form-item label="类型" prop="type">
        <!-- 类型下拉选择框，绑定字典数据 -->
        <el-select v-model="queryParams.type" placeholder="请选择类型" clearable style="width: 130px">
          <!-- 遍历字典选项生成下拉项 -->
          <el-option
            v-for="dict in wallet_log_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <!-- 订单编号搜索表单项 -->
      <el-form-item label="订单编号" prop="relatedOrderNo">
        <!-- 订单编号输入框，支持回车搜索 -->
        <el-input
          v-model="queryParams.relatedOrderNo"
          placeholder="请输入订单编号"
          clearable
          style="width: 130px"
          @keyup.enter="handleQuery"
        />
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
      <!-- 删除按钮列 -->
      <el-col :span="1.5">
        <!-- 批量删除按钮，无选中时禁用 -->
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['trade:walletLog:remove']"
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
          v-hasPermi="['trade:walletLog:export']"
        >导出</el-button>
      </el-col>
      <!-- 右侧工具栏，控制搜索区显隐 -->
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 流水数据列表表格 -->
    <el-table v-loading="loading" :data="walletLogList" @selection-change="handleSelectionChange" class="admin-table">
      <!-- 多选列 -->
      <el-table-column type="selection" width="55" align="center" />
      <!-- 序号列，根据分页计算 -->
      <el-table-column label="序号" align="center" type="index" width="60" :index="(i) => (queryParams.pageNum - 1) * queryParams.pageSize + i + 1" />      
      <!-- 用户名列 -->
      <el-table-column label="用户名" align="center" prop="userName" width="140" />
      <!-- 昵称列 -->
      <el-table-column label="昵称" align="center" prop="nickName" width="140" />
      <!-- 类型列，使用字典标签渲染 -->
      <el-table-column label="类型" align="center" prop="type">
        <!-- 类型自定义渲染模板 -->
        <template #default="scope">
          <!-- 字典标签，根据类型值显示对应名称 -->
          <dict-tag :options="wallet_log_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <!-- 金额列 -->
      <el-table-column label="金额" align="center" prop="amount" width="120">
        <!-- 金额自定义渲染模板 -->
        <template #default="scope">
          <!-- 金额显示，负数标红 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.amount) < 0 }">¥{{ formatPrice(scope.row.amount) }}</span>
        </template>
      </el-table-column>
      <!-- 变动前余额列 -->
      <el-table-column label="变动前余额" align="center" prop="balanceBefore" width="130">
        <!-- 变动前余额自定义渲染模板 -->
        <template #default="scope">
          <!-- 变动前余额显示，负数标红 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.balanceBefore) < 0 }">¥{{ formatPrice(scope.row.balanceBefore) }}</span>
        </template>
      </el-table-column>
      <!-- 变动后余额列 -->
      <el-table-column label="变动后余额" align="center" prop="balanceAfter" width="130">
        <!-- 变动后余额自定义渲染模板 -->
        <template #default="scope">
          <!-- 变动后余额显示，负数标红 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.balanceAfter) < 0 }">¥{{ formatPrice(scope.row.balanceAfter) }}</span>
        </template>
      </el-table-column>
      <!-- 订单编号列 -->
      <el-table-column label="订单编号" align="center" prop="relatedOrderNo" />
      <!-- 备注列 -->
      <el-table-column label="备注" align="center" prop="remark" />
      <!-- 创建时间列 -->
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <!-- 创建时间自定义渲染模板 -->
        <template #default="scope">
          <!-- 格式化时间显示 -->
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <!-- 操作按钮自定义渲染模板 -->
        <template #default="scope">
          <!-- 修改按钮 -->
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['trade:walletLog:edit']">修改</el-button>
          <!-- 删除按钮 -->
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['trade:walletLog:remove']">删除</el-button>
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

    <!-- 添加或修改钱包流水对话框 -->
    <!-- 新增/编辑流水弹窗 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <!-- 流水表单 -->
      <el-form ref="walletLogRef" :model="form" :rules="rules" label-width="80px">
        <!-- 用户ID表单项 -->
        <el-form-item label="用户ID" prop="userId">
          <!-- 用户ID输入框，编辑时禁用 -->
          <el-input v-model="form.userId" placeholder="请输入用户ID" :disabled="form.id" />
        </el-form-item>
        <!-- 钱包ID表单项 -->
        <el-form-item label="钱包ID" prop="walletId">
          <!-- 钱包ID输入框，编辑时禁用 -->
          <el-input v-model="form.walletId" placeholder="请输入钱包ID" :disabled="form.id" />
        </el-form-item>
        <!-- 类型表单项 -->
        <el-form-item label="类型" prop="type">
          <!-- 类型下拉选择框 -->
          <el-select v-model="form.type" placeholder="请选择类型">
            <!-- 遍历字典选项，值转为整数 -->
            <el-option
              v-for="dict in wallet_log_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <!-- 金额表单项 -->
        <el-form-item label="金额" prop="amount">
          <!-- 金额输入框 -->
          <el-input v-model="form.amount" placeholder="请输入金额" />
        </el-form-item>
        <!-- 变动前余额表单项 -->
        <el-form-item label="变动前余额" prop="balanceBefore">
          <!-- 变动前余额输入框 -->
          <el-input v-model="form.balanceBefore" placeholder="请输入变动前余额" />
        </el-form-item>
        <!-- 变动后余额表单项 -->
        <el-form-item label="变动后余额" prop="balanceAfter">
          <!-- 变动后余额输入框 -->
          <el-input v-model="form.balanceAfter" placeholder="请输入变动后余额" />
        </el-form-item>
        <!-- 关联订单号表单项 -->
        <el-form-item label="关联订单号" prop="relatedOrderNo">
          <!-- 关联订单号输入框 -->
          <el-input v-model="form.relatedOrderNo" placeholder="请输入关联订单号" />
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

<script setup name="walletLog">
// 导入钱包流水相关API方法
import { listWalletLog, getWalletLog, delWalletLog, addWalletLog, updateWalletLog } from "@/api/trade/walletLog";

// 获取当前组件实例代理
const { proxy } = getCurrentInstance();
// 获取钱包日志类型字典数据
const { wallet_log_type } = proxy.useDict('wallet_log_type');

// 流水列表数据
const walletLogList = ref([]);
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

// 金额格式化：空值显示 --，其余保留两位小数
// 金额格式化函数定义
function formatPrice(v) {
  // 空值返回占位符，否则保留两位小数
  return (v === null || v === undefined) ? '--' : Number(v).toFixed(2);
}

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
    // 用户名筛选
    userName: null,
    // 类型筛选
    type: null,
    // 金额筛选
    amount: null,
    // 变动前余额筛选
    balanceBefore: null,
    // 变动后余额筛选
    balanceAfter: null,
    // 订单编号筛选
    relatedOrderNo: null,
    // 创建时间筛选
    createTime: null
  },
  // 表单校验规则
  rules: {
    // 用户ID必填校验
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    // 钱包ID必填校验
    walletId: [
      { required: true, message: "钱包ID不能为空", trigger: "blur" }
    ],
    // 类型必填校验
    type: [
      { required: true, message: "类型不能为空", trigger: "change" }
    ],
    // 金额必填校验
    amount: [
      { required: true, message: "金额不能为空", trigger: "blur" }
    ],
  }
});

// 从响应式数据中解构出查询参数、表单和规则的引用
const { queryParams, form, rules } = toRefs(data);

/** 查询钱包流水列表 */
// 获取流水列表数据
function getList() {
  // 开启加载状态
  loading.value = true;
  // 调用API获取流水列表
  listWalletLog(queryParams.value).then(response => {
    // 赋值列表数据
    walletLogList.value = response.rows;
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
    userId: null,
    walletId: null,
    type: null,
    amount: null,
    balanceBefore: null,
    balanceAfter: null,
    relatedOrderNo: null,
    remark: null,
    createTime: null
  };
  // 调用代理重置表单方法
  proxy.resetForm("walletLogRef");
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
  title.value = "添加钱包流水";
}

/** 修改按钮操作 */
// 修改按钮处理函数
function handleUpdate(row) {
  // 重置表单
  reset();
  // 获取行ID或选中ID数组的第一个
  const _id = row.id || ids.value
  // 调用API获取流水详情
  getWalletLog(_id).then(response => {
    // 将返回数据赋值给表单
    form.value = response.data;
    // 打开弹窗
    open.value = true;
    // 设置弹窗标题为修改
    title.value = "修改钱包流水";
  });
}

/** 提交按钮 */
// 提交表单处理函数
function submitForm() {
  // 触发表单校验
  proxy.$refs["walletLogRef"].validate(valid => {
    // 校验通过时执行
    if (valid) {
      // 判断是编辑还是新增（有id为编辑）
      if (form.value.id != null) {
        // 调用更新API
        updateWalletLog(form.value).then(response => {
          // 显示修改成功提示
          proxy.$modal.msgSuccess("修改成功");
          // 关闭弹窗
          open.value = false;
          // 刷新列表
          getList();
        });
      } else {
        // 调用新增API
        addWalletLog(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除钱包流水编号为"' + _ids + '"的数据项？').then(function() {
    // 执行删除操作
    return delWalletLog(_ids);
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
  proxy.download('trade/walletLog/export', {
    // 展开查询参数
    ...queryParams.value
  }, `walletLog_${new Date().getTime()}.xlsx`)
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

<style scoped>
/* 金额文本基础样式 */
.price-text {
  /* 加粗字体 */
  font-weight: 600;
  /* 默认绿色表示正数 */
  color: #67c23a;
  /* 使用等宽数字对齐 */
  font-variant-numeric: tabular-nums;
}
/* 负数金额样式 */
.price-text.price-negative {
  /* 红色表示负数 */
  color: #f56c6c;
}
</style>
