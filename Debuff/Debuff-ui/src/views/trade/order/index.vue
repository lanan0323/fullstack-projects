<template>
  <!-- 页面主容器，包含订单管理全部内容 -->
  <div class="app-container order-admin">
    <!-- 页头 -->
    <!-- 页头区域：展示页面标题和副标题说明 -->
    <div class="admin-head">
      <!-- 页面主标题：订单管理 -->
      <h2 class="admin-title">订单管理</h2>
      <!-- 页面副标题：简要说明页面功能 -->
      <p class="admin-sub">查看平台成交订单与状态</p>
    </div>
    <!-- 搜索栏 -->
    <!-- 搜索表单：按订单号/饰品名/买家名/状态等条件筛选订单，inline模式水平排列 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px" class="admin-filter">
      <!-- 搜索条件：订单编号输入框 -->
      <el-form-item label="订单编号" prop="orderNo">
        <!-- 订单编号输入框，支持清空，回车触发搜索 -->
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <!-- 搜索条件：饰品名称输入框 -->
      <el-form-item label="饰品名" prop="skinName">
        <!-- 饰品名称输入框，支持清空，回车触发搜索 -->
        <el-input
          v-model="queryParams.skinName"
          placeholder="饰品名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <!-- 搜索条件：买家名称输入框 -->
      <el-form-item label="买家名" prop="buyerName">
        <!-- 买家名称输入框，支持清空，回车触发搜索 -->
        <el-input
          v-model="queryParams.buyerName"
          placeholder="用户名/昵称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <!-- 搜索条件：订单状态下拉选择框 -->
      <el-form-item label="状态" prop="status">
        <!-- 状态下拉框，绑定字典trade_order_status，支持清空 -->
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <!-- 遍历字典数据生成下拉选项 -->
          <el-option
            v-for="dict in trade_order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <!-- 搜索操作按钮区域 -->
      <el-form-item>
        <!-- 搜索按钮：点击执行查询 -->
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <!-- 重置按钮：点击清空搜索条件并重新查询 -->
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <!-- 工具栏行：放置批量操作按钮和右侧工具栏 -->
    <el-row :gutter="10" class="mb8">
      <!-- 批量删除按钮列 -->
      <el-col :span="1.5">
        <!-- 删除按钮：批量删除选中订单，无选中时禁用，需trade:order:remove权限 -->
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['trade:order:remove']"
        >删除</el-button>
      </el-col>
      <!-- 导出按钮列 -->
      <el-col :span="1.5">
        <!-- 导出按钮：导出订单数据为Excel，需trade:order:export权限 -->
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['trade:order:export']"
        >导出</el-button>
      </el-col>
      <!-- 右侧工具栏：控制搜索栏显示/隐藏，触发表格刷新 -->
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <!-- 订单数据表格：展示订单列表，支持多选，加载中显示loading动画 -->
    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange" class="admin-table">
      <!-- 多选列：用于批量选择订单 -->
      <el-table-column type="selection" width="55" align="center" />
      <!-- 订单编号列 -->
      <el-table-column label="订单编号" align="center" prop="orderNo" />
      <!-- 饰品名称列，最小宽度140，超出显示tooltip -->
      <el-table-column label="饰品" align="center" prop="skinName" min-width="140" show-overflow-tooltip />
      <!-- 买家名称列 -->
      <el-table-column label="买家" align="center" prop="buyerName" width="120" />
      <!-- 卖家名称列 -->
      <el-table-column label="卖家" align="center" prop="sellerName" width="120" />
      <!-- 成交价格列：自定义渲染，格式化金额并处理负数样式 -->
      <el-table-column label="成交价格" align="center" prop="price" width="110">
        <template #default="scope">
          <!-- 价格文本，负数时添加红色样式类 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.price) < 0 }">¥{{ formatPrice(scope.row.price) }}</span>
        </template>
      </el-table-column>
      <!-- 平台手续费列：自定义渲染，格式化金额并处理负数样式 -->
      <el-table-column label="平台手续费" align="center" prop="platformFee" width="110">
        <template #default="scope">
          <!-- 手续费金额文本，负数时添加红色样式类 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.platformFee) < 0 }">¥{{ formatPrice(scope.row.platformFee) }}</span>
        </template>
      </el-table-column>
      <!-- 卖家实收列：自定义渲染，格式化金额并处理负数样式 -->
      <el-table-column label="卖家实收" align="center" prop="sellerIncome" width="110">
        <template #default="scope">
          <!-- 实收金额文本，负数时添加红色样式类 -->
          <span class="price-text" :class="{ 'price-negative': Number(scope.row.sellerIncome) < 0 }">¥{{ formatPrice(scope.row.sellerIncome) }}</span>
        </template>
      </el-table-column>
      <!-- 订单状态列：根据状态值显示不同颜色的状态标签 -->
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <!-- 状态标签：根据status值(0/1/2)显示待确认/已完成/已取消，并应用对应样式类 -->
          <span class="status-chip" :class="{ warn: scope.row.status === '0', on: scope.row.status === '1', off: scope.row.status === '2' }">
            {{ scope.row.status === '0' ? '待确认' : scope.row.status === '1' ? '已完成' : '已取消' }}
          </span>
        </template>
      </el-table-column>
      <!-- 下单时间列：格式化时间为 yyyy-MM-dd HH:mm:ss -->
      <el-table-column label="下单时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <!-- 格式化后的下单时间文本 -->
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <!-- 操作列：放置行级操作按钮，固定宽度 -->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <!-- 修改按钮：打开编辑对话框，需trade:order:edit权限 -->
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['trade:order:edit']">修改</el-button>
          <!-- 删除按钮：删除当前行订单，需trade:order:remove权限 -->
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['trade:order:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <!-- 分页组件：显示总条数，绑定页码和每页条数，切换时重新查询 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改交易订单对话框 -->
    <!-- 对话框：用于新增或编辑订单，宽度500px，标题动态绑定 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <!-- 对话框内表单：用于填写/编辑订单信息，含表单校验 -->
      <el-form ref="orderRef" :model="form" :rules="rules" label-width="80px">
        <!-- 表单项：订单编号，新增时可手动生成，编辑时只读 -->
        <el-form-item label="订单编号" prop="orderNo">
          <!-- 订单编号输入框，编辑模式下只读 -->
          <el-input v-model="form.orderNo" placeholder="系统自动生成" readonly>
            <!-- 新增模式下显示生成按钮 -->
            <template #append v-if="!form.id">
              <!-- 点击生成唯一订单编号 -->
              <el-button @click="generateOrderNo">生成</el-button>
            </template>
          </el-input>
        </el-form-item>
        <!-- 表单项：饰品ID，编辑时不可修改 -->
        <el-form-item label="饰品ID" prop="skinId">
          <!-- 饰品ID输入框，编辑模式下禁用 -->
          <el-input v-model="form.skinId" placeholder="请输入饰品ID" :disabled="form.id" />
        </el-form-item>
        <!-- 表单项：买家ID，编辑时不可修改 -->
        <el-form-item label="买家ID" prop="buyerId">
          <!-- 买家ID输入框，编辑模式下禁用 -->
          <el-input v-model="form.buyerId" placeholder="请输入买家ID" :disabled="form.id" />
        </el-form-item>
        <!-- 表单项：卖家ID，编辑时不可修改 -->
        <el-form-item label="卖家ID" prop="sellerId">
          <!-- 卖家ID输入框，编辑模式下禁用 -->
          <el-input v-model="form.sellerId" placeholder="请输入卖家ID" :disabled="form.id" />
        </el-form-item>
        <!-- 表单项：成交价格，输入时自动计算手续费和实收 -->
        <el-form-item label="成交价格" prop="price">
          <!-- 成交价格输入框，输入时触发onPriceChange计算 -->
          <el-input v-model="form.price" placeholder="请输入成交价格" @input="onPriceChange" />
        </el-form-item>
        <!-- 表单项：平台手续费，只读展示自动计算结果 -->
        <el-form-item label="平台手续费">
          <!-- 手续费只读输入框，显示计算后的金额 -->
          <el-input v-model="platformFeeDisplay" readonly />
        </el-form-item>
        <!-- 表单项：卖家实收，只读展示自动计算结果 -->
        <el-form-item label="卖家实收">
          <!-- 实收只读输入框，显示计算后的金额 -->
          <el-input v-model="sellerIncomeDisplay" readonly />
        </el-form-item>
        <!-- 表单项：订单状态，单选按钮组绑定字典值 -->
        <el-form-item label="状态" prop="status">
          <!-- 状态单选组：遍历字典生成选项 -->
          <el-radio-group v-model="form.status">
            <!-- 遍历字典数据生成单选按钮 -->
            <el-radio v-for="dict in trade_order_status" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮区域 -->
      <template #footer>
        <!-- 底部按钮容器 -->
        <div class="dialog-footer">
          <!-- 确定按钮：提交表单数据 -->
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <!-- 取消按钮：关闭对话框并重置表单 -->
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Order">
// 导入订单相关API接口：列表查询、详情、新增、修改、删除
import { listOrder, getOrder, addOrder, updateOrder, delOrder } from "@/api/trade/order";

// 获取当前组件代理实例，用于调用全局方法
const { proxy } = getCurrentInstance();
// 获取订单状态字典数据，用于下拉框和单选组渲染
const { trade_order_status } = proxy.useDict('trade_order_status');

// 平台手续费率（5%）
const FEE_RATE = 0.05;

// 订单列表数据数组
const orderList = ref([]);
// 控制对话框显示/隐藏的布尔值
const open = ref(false);
// 表格加载状态标识
const loading = ref(true);
// 搜索栏显示/隐藏标识
const showSearch = ref(true);
// 选中行的ID数组，用于批量操作
const ids = ref([]);
// 是否非单选标识（非恰好选中一条时为true）
const single = ref(true);
// 是否未选中任何项标识（未选中时为true，禁用批量删除按钮）
const multiple = ref(true);
// 数据总条数，用于分页显示
const total = ref(0);
// 对话框标题，动态显示"添加"或"修改"
const title = ref("");
// 平台手续费展示值（格式化后的字符串）
const platformFeeDisplay = ref("0.00");
// 卖家实收展示值（格式化后的字符串）
const sellerIncomeDisplay = ref("0.00");

/** 金额格式化:与饰品管理一致(¥ + 两位小数) */
// 定义金额格式化函数：null/undefined显示'--'，否则保留两位小数
const formatPrice = (v) => {
  // 判断值是否为空，空返回'--'，否则转为数字并保留两位小数
  return v === null || v === undefined ? '--' : Number(v).toFixed(2)
}

// 定义响应式数据对象，包含表单、查询参数和校验规则
const data = reactive({
  // 表单数据对象，用于新增/编辑订单
  form: {},
  // 查询参数对象，包含分页和筛选条件
  queryParams: {
    // 当前页码，默认第1页
    pageNum: 1,
    // 每页条数，默认10条
    pageSize: 10,
    // 按订单编号筛选
    orderNo: null,
    // 按饰品名称筛选
    skinName: null,
    // 按买家名称筛选
    buyerName: null,
    // 按订单状态筛选
    status: null
  },
  // 表单校验规则
  rules: {
    // 饰品ID校验规则：必填
    skinId: [
      { required: true, message: "饰品ID不能为空", trigger: "blur" }
    ],
    // 买家ID校验规则：必填
    buyerId: [
      { required: true, message: "买家ID不能为空", trigger: "blur" }
    ],
    // 卖家ID校验规则：必填
    sellerId: [
      { required: true, message: "卖家ID不能为空", trigger: "blur" }
    ],
    // 成交价格校验规则：必填
    price: [
      { required: true, message: "成交价格不能为空", trigger: "blur" }
    ],
    // 订单编号校验规则：必填
    orderNo: [
      { required: true, message: "请先生成订单编号", trigger: "blur" }
    ]
  }
});

// 从响应式数据中解构出查询参数、表单和校验规则的ref引用
const { queryParams, form, rules } = toRefs(data);

/** 生成订单编号 */
// 生成订单编号函数：格式为 ORD + 年月日时分秒 + 4位随机数
function generateOrderNo() {
  // 获取当前时间对象
  const now = new Date();
  // 获取四位年份
  const y = now.getFullYear();
  // 获取月份并补零至两位（01-12）
  const m = String(now.getMonth() + 1).padStart(2, "0");
  // 获取日期并补零至两位（01-31）
  const d = String(now.getDate()).padStart(2, "0");
  // 获取小时并补零至两位（00-23）
  const h = String(now.getHours()).padStart(2, "0");
  // 获取分钟并补零至两位（00-59）
  const min = String(now.getMinutes()).padStart(2, "0");
  // 获取秒数并补零至两位（00-59）
  const s = String(now.getSeconds()).padStart(2, "0");
  // 生成4位随机数字字符串（0000-9999）
  const rand = String(Math.floor(Math.random() * 10000)).padStart(4, "0");
  // 拼接完整订单编号并赋值给表单
  form.value.orderNo = "ORD" + y + m + d + h + min + s + rand;
}

/** 价格变化时自动计算手续费和实收 */
// 价格变化处理函数：根据输入价格自动计算平台手续费和卖家实收
function onPriceChange(val) {
  // 将输入值转换为浮点数
  const price = parseFloat(val);
  // 判断价格是否有效（大于0）
  if (price && price > 0) {
    // 计算平台手续费 = 价格 × 费率，保留两位小数
    const fee = Number((price * FEE_RATE).toFixed(2));
    // 计算卖家实收 = 价格 - 手续费，保留两位小数
    const income = Number((price - fee).toFixed(2));
    // 将手续费写入表单数据
    form.value.platformFee = fee;
    // 将卖家实收写入表单数据
    form.value.sellerIncome = income;
    // 更新手续费展示值（格式化为两位小数字符串）
    platformFeeDisplay.value = fee.toFixed(2);
    // 更新卖家实收展示值（格式化为两位小数字符串）
    sellerIncomeDisplay.value = income.toFixed(2);
  } else {
    // 价格无效时，将表单中手续费设为0
    form.value.platformFee = 0;
    // 价格无效时，将表单中实收设为0
    form.value.sellerIncome = 0;
    // 手续费展示值重置为"0.00"
    platformFeeDisplay.value = "0.00";
    // 实收展示值重置为"0.00"
    sellerIncomeDisplay.value = "0.00";
  }
}

/** 查询订单列表 */
// 查询订单列表函数：调用API获取订单数据并更新表格
function getList() {
  // 开启表格加载动画
  loading.value = true;
  // 调用列表查询API，传入查询参数
  listOrder(queryParams.value).then(response => {
    // 将返回的订单行数据赋值给列表
    orderList.value = response.rows;
    // 将返回的总条数赋值给分页
    total.value = response.total;
    // 关闭表格加载动画
    loading.value = false;
  });
}

// 取消按钮
// 取消按钮处理函数：关闭对话框并重置表单
function cancel() {
  // 关闭对话框
  open.value = false;
  // 调用重置函数清空表单
  reset();
}

// 表单重置
// 表单重置函数：将所有表单字段恢复为初始值
function reset() {
  // 重置表单数据对象的所有字段为null
  form.value = {
    id: null,
    orderNo: null,
    skinId: null,
    buyerId: null,
    sellerId: null,
    price: null,
    platformFee: null,
    sellerIncome: null,
    status: null,
    payTime: null,
    cancelTime: null
  };
  // 重置手续费展示值为默认值
  platformFeeDisplay.value = "0.00";
  // 重置实收展示值为默认值
  sellerIncomeDisplay.value = "0.00";
  // 调用全局方法重置表单校验状态
  proxy.resetForm("orderRef");
}

/** 搜索按钮操作 */
// 搜索按钮处理函数：重置页码为1并执行查询
function handleQuery() {
  // 将页码重置为1，避免筛选后停留在无数据的页
  queryParams.value.pageNum = 1;
  // 执行列表查询
  getList();
}

/** 重置按钮操作 */
// 重置按钮处理函数：清空搜索条件并重新查询
function resetQuery() {
  // 调用全局方法重置搜索表单
  proxy.resetForm("queryRef");
  // 重置后执行查询以刷新列表
  handleQuery();
}

// 多选框选中数据
// 表格多选变化处理函数：更新选中状态和按钮可用性
function handleSelectionChange(selection) {
  // 提取选中行的ID数组
  ids.value = selection.map(item => item.id);
  // 判断是否非恰好选中一条（用于控制修改按钮）
  single.value = selection.length != 1;
  // 判断是否未选中任何项（用于控制删除按钮）
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
// 新增按钮处理函数：重置表单、生成编号、打开对话框
function handleAdd() {
  // 重置表单数据和校验状态
  reset();
  // 自动生成新的订单编号
  generateOrderNo();
  // 打开对话框
  open.value = true;
  // 设置对话框标题为"添加交易订单"
  title.value = "添加交易订单";
}

/** 修改按钮操作 */
// 修改按钮处理函数：获取订单详情并填充到表单
function handleUpdate(row) {
  // 重置表单数据和校验状态
  reset();
  // 获取订单ID，优先使用行数据ID，否则使用选中ID数组
  const _id = row.id || ids.value
  // 调用API获取订单详情
  getOrder(_id).then(response => {
    // 将返回的订单数据赋值给表单
    form.value = response.data;
    // 确保status类型与字典值一致
    // 确保status字段为字符串类型以匹配字典值
    if (form.value.status !== null && form.value.status !== undefined) {
      // 将status转换为字符串
      form.value.status = String(form.value.status);
    }
    // 回显手续费和实收
    // 格式化手续费并更新展示值
    platformFeeDisplay.value = form.value.platformFee ? Number(form.value.platformFee).toFixed(2) : "0.00";
    // 格式化实收并更新展示值
    sellerIncomeDisplay.value = form.value.sellerIncome ? Number(form.value.sellerIncome).toFixed(2) : "0.00";
    // 打开对话框
    open.value = true;
    // 设置对话框标题为"修改交易订单"
    title.value = "修改交易订单";
  });
}

/** 提交按钮 */
// 表单提交处理函数：校验通过后调用新增或修改API
function submitForm() {
  // 触发表单校验
  proxy.$refs["orderRef"].validate(valid => {
    // 校验通过后执行提交逻辑
    if (valid) {
      // 判断表单是否有ID（有ID为修改，无ID为新增）
      if (form.value.id != null) {
        // 调用修改API提交更新数据
        updateOrder(form.value).then(response => {
          // 显示修改成功提示
          proxy.$modal.msgSuccess("修改成功");
          // 关闭对话框
          open.value = false;
          // 刷新列表数据
          getList();
        });
      } else {
        // 调用新增API提交新订单数据
        addOrder(form.value).then(response => {
          // 显示新增成功提示
          proxy.$modal.msgSuccess("新增成功");
          // 关闭对话框
          open.value = false;
          // 刷新列表数据
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
// 删除按钮处理函数：弹出确认框后调用删除API
function handleDelete(row) {
  // 获取要删除的ID，优先使用行ID，否则使用选中ID数组
  const _ids = row.id || ids.value;
  // 弹出确认对话框，确认后执行删除操作
  proxy.$modal.confirm('是否确认删除交易订单编号为"' + _ids + '"的数据项？').then(function() {
    // 调用删除API
    return delOrder(_ids);
  }).then(() => {
    // 删除成功后刷新列表
    getList();
    // 显示删除成功提示
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
// 导出按钮处理函数：调用下载接口导出Excel文件
function handleExport() {
  // 调用全局下载方法，传入导出路径、查询参数和文件名
  proxy.download('trade/order/export', {
    // 展开查询参数作为导出条件
    ...queryParams.value
  }, `order_${new Date().getTime()}.xlsx`)
}

// 页面首次挂载时查询订单列表
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
/* 价格文本样式：加粗绿色，使用等宽数字对齐 */
.price-text {
  /* 设置字体加粗 */
  font-weight: 600;
  /* 设置默认绿色（正数金额） */
  color: #67c23a;
  /* 使用等宽数字变体，保证金额列数字对齐 */
  font-variant-numeric: tabular-nums;
}
/* 负数价格样式：红色显示异常金额 */
.price-text.price-negative {
  /* 设置负数金额为红色 */
  color: #f56c6c;
}
</style>
