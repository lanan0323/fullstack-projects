<!-- 钱包管理 -->
<template>
  <!-- 钱包管理页面主容器 -->
  <div class="app-container wallet-admin">
    <!-- 页头 -->
    <!-- 页头区域容器 -->
    <div class="admin-head">
      <!-- 页面主标题 -->
      <h2 class="admin-title">钱包管理</h2>
      <!-- 页面副标题说明 -->
      <p class="admin-sub">管理用户余额与资金统计</p>
    </div>
    <!-- 搜索筛选表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px" class="admin-filter">
      <!-- 用户名搜索表单项 -->
      <el-form-item label="用户名" prop="userName">
        <!-- 用户名输入框，支持回车搜索 -->
        <el-input
          v-model="queryParams.userName"
          placeholder="用户名/昵称"
          clearable
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
          v-hasPermi="['trade:wallet:remove']"
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
          v-hasPermi="['trade:wallet:export']"
        >导出</el-button>
      </el-col>
      <!-- 右侧工具栏，控制搜索区显隐 -->
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 钱包数据列表表格 -->
    <el-table v-loading="loading" :data="walletList" @selection-change="handleSelectionChange" class="admin-table">
      <!-- 多选列 -->
      <el-table-column type="selection" width="55" align="center" />
      <!-- 序号列，根据分页计算 -->
      <el-table-column label="序号" align="center" type="index" width="60" :index="(i) => (queryParams.pageNum - 1) * queryParams.pageSize + i + 1" />
      <!-- 用户名列 -->
      <el-table-column label="用户名" align="center" prop="userName" width="140" />
      <!-- 昵称列 -->
      <el-table-column label="昵称" align="center" prop="nickName" width="140" />
      <!-- 可用余额列 -->
      <el-table-column label="可用余额" align="center" prop="balance" width="180">
        <!-- 余额自定义渲染模板 -->
        <template #default="scope">
          <!-- 金额显示，负数标红 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.balance) < 0 }">¥{{ formatPrice(scope.row.balance) }}</span>
        </template>
      </el-table-column>
      <!-- 累计充值列 -->
      <el-table-column label="累计充值" align="center" prop="totalRecharge" width="180">
        <!-- 充值金额自定义渲染模板 -->
        <template #default="scope">
          <!-- 充值金额显示，负数标红 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.totalRecharge) < 0 }">¥{{ formatPrice(scope.row.totalRecharge) }}</span>
        </template>
      </el-table-column>
      <!-- 累计收入列 -->
      <el-table-column label="累计收入" align="center" prop="totalIncome" width="180">
        <!-- 收入金额自定义渲染模板 -->
        <template #default="scope">
          <!-- 收入金额显示，负数标红 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.totalIncome) < 0 }">¥{{ formatPrice(scope.row.totalIncome) }}</span>
        </template>
      </el-table-column>
      <!-- 累计支出列 -->
      <el-table-column label="累计支出" align="center" prop="totalExpense" width="180">
        <!-- 支出金额自定义渲染模板 -->
        <template #default="scope">
          <!-- 支出金额显示，负数标红 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.totalExpense) < 0 }">¥{{ formatPrice(scope.row.totalExpense) }}</span>
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <!-- 操作按钮自定义渲染模板 -->
        <template #default="scope">
          <!-- 修改按钮 -->
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['trade:wallet:edit']">修改</el-button>
          <!-- 删除按钮 -->
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['trade:wallet:remove']">删除</el-button>
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

    <!-- 添加或修改用户钱包对话框 -->
    <!-- 新增/编辑钱包弹窗 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <!-- 钱包表单 -->
      <el-form ref="walletRef" :model="form" :rules="rules" label-width="80px">
        <!-- 用户ID表单项 -->
        <el-form-item label="用户ID" prop="userId">
          <!-- 用户ID输入框，编辑时禁用 -->
          <el-input v-model="form.userId" placeholder="请输入用户ID" :disabled="form.id" />
        </el-form-item>
        <!-- 可用余额表单项 -->
        <el-form-item label="可用余额" prop="balance">
          <!-- 可用余额输入框 -->
          <el-input v-model="form.balance" placeholder="请输入可用余额" />
        </el-form-item>
        <!-- 冻结金额表单项 -->
        <el-form-item label="冻结金额" prop="frozenAmount">
          <!-- 冻结金额输入框 -->
          <el-input v-model="form.frozenAmount" placeholder="请输入冻结金额" />
        </el-form-item>
        <!-- 累计充值表单项 -->
        <el-form-item label="累计充值" prop="totalRecharge">
          <!-- 累计充值输入框 -->
          <el-input v-model="form.totalRecharge" placeholder="请输入累计充值" />
        </el-form-item>
        <!-- 累计提现表单项 -->
        <el-form-item label="累计提现" prop="totalWithdraw">
          <!-- 累计提现输入框 -->
          <el-input v-model="form.totalWithdraw" placeholder="请输入累计提现" />
        </el-form-item>
        <!-- 累计收入表单项 -->
        <el-form-item label="累计收入" prop="totalIncome">
          <!-- 累计收入输入框 -->
          <el-input v-model="form.totalIncome" placeholder="请输入累计收入" />
        </el-form-item>
        <!-- 累计支出表单项 -->
        <el-form-item label="累计支出" prop="totalExpense">
          <!-- 累计支出输入框 -->
          <el-input v-model="form.totalExpense" placeholder="请输入累计支出" />
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

<script setup name="wallet">
// 导入钱包相关API方法
import { listWallet, getWallet, delWallet, addWallet, updateWallet } from "@/api/trade/wallet";

// 获取当前组件实例代理
const { proxy } = getCurrentInstance();

// 钱包列表数据
const walletList = ref([]);

/** 金额格式化:与饰品管理一致(¥ + 两位小数) */
// 金额格式化函数，空值显示--，否则保留两位小数
const formatPrice = (v) => {
  // 返回格式化后的金额字符串
  return v === null || v === undefined ? '--' : Number(v).toFixed(2)
}
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
    // 用户名筛选
    userName: null,
    // 余额筛选
    balance: null,
    // 冻结金额筛选
    frozenAmount: null,
    // 累计充值筛选
    totalRecharge: null,
    // 累计提现筛选
    totalWithdraw: null,
    // 累计收入筛选
    totalIncome: null,
    // 累计支出筛选
    totalExpense: null,
  },
  // 表单校验规则
  rules: {
    // 用户ID必填校验
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
  }
});

// 从响应式数据中解构出查询参数、表单和规则的引用
const { queryParams, form, rules } = toRefs(data);

/** 查询用户钱包列表 */
// 获取钱包列表数据
function getList() {
  // 开启加载状态
  loading.value = true;
  // 调用API获取钱包列表
  listWallet(queryParams.value).then(response => {
    // 赋值列表数据
    walletList.value = response.rows;
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
    balance: null,
    frozenAmount: null,
    totalRecharge: null,
    totalWithdraw: null,
    totalIncome: null,
    totalExpense: null,
    createTime: null,
    updateTime: null
  };
  // 调用代理重置表单方法
  proxy.resetForm("walletRef");
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
  title.value = "添加用户钱包";
}

/** 修改按钮操作 */
// 修改按钮处理函数
function handleUpdate(row) {
  // 重置表单
  reset();
  // 获取行ID或选中ID数组的第一个
  const _id = row.id || ids.value
  // 调用API获取钱包详情
  getWallet(_id).then(response => {
    // 将返回数据赋值给表单
    form.value = response.data;
    // 打开弹窗
    open.value = true;
    // 设置弹窗标题为修改
    title.value = "修改用户钱包";
  });
}

/** 提交按钮 */
// 提交表单处理函数
function submitForm() {
  // 触发表单校验
  proxy.$refs["walletRef"].validate(valid => {
    // 校验通过时执行
    if (valid) {
      // 判断是编辑还是新增（有id为编辑）
      if (form.value.id != null) {
        // 调用更新API
        updateWallet(form.value).then(response => {
          // 显示修改成功提示
          proxy.$modal.msgSuccess("修改成功");
          // 关闭弹窗
          open.value = false;
          // 刷新列表
          getList();
        });
      } else {
        // 调用新增API
        addWallet(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除用户钱包编号为"' + _ids + '"的数据项？').then(function() {
    // 执行删除操作
    return delWallet(_ids);
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
  proxy.download('trade/wallet/export', {
    // 展开查询参数
    ...queryParams.value
  }, `wallet_${new Date().getTime()}.xlsx`)
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
