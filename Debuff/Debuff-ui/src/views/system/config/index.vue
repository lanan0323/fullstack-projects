<!-- 系统参数配置管理页面 -->
<template>
   <div class="app-container">
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
         <!-- 参数名称搜索 -->
         <el-form-item label="参数名称" prop="configName">
            <el-input
               v-model="queryParams.configName"
               placeholder="请输入参数名称"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <!-- 参数键名搜索 -->
         <el-form-item label="参数键名" prop="configKey">
            <el-input
               v-model="queryParams.configKey"
               placeholder="请输入参数键名"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <!-- 系统内置类型选择 -->
         <el-form-item label="系统内置" prop="configType">
            <el-select v-model="queryParams.configType" placeholder="系统内置" clearable>
               <el-option
                  v-for="dict in sys_yes_no"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
               />
            </el-select>
         </el-form-item>
         <!-- 创建时间日期范围选择 -->
         <el-form-item label="创建时间" style="width: 308px;">
            <el-date-picker
               v-model="dateRange"
               value-format="YYYY-MM-DD"
               type="daterange"
               range-separator="-"
               start-placeholder="开始日期"
               end-placeholder="结束日期"
            ></el-date-picker>
         </el-form-item>
         <!-- 搜索和重置按钮 -->
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <!-- 工具栏按钮组 -->
      <el-row :gutter="10" class="mb8">
         <!-- 新增按钮 -->
         <el-col :span="1.5">
            <el-button
               type="primary"
               plain
               icon="Plus"
               @click="handleAdd"
               v-hasPermi="['system:config:add']"
            >新增</el-button>
         </el-col>
         <!-- 修改按钮 -->
         <el-col :span="1.5">
            <el-button
               type="success"
               plain
               icon="Edit"
               :disabled="single"
               @click="handleUpdate"
               v-hasPermi="['system:config:edit']"
            >修改</el-button>
         </el-col>
         <!-- 删除按钮 -->
         <el-col :span="1.5">
            <el-button
               type="danger"
               plain
               icon="Delete"
               :disabled="multiple"
               @click="handleDelete"
               v-hasPermi="['system:config:remove']"
            >删除</el-button>
         </el-col>
         <!-- 导出按钮 -->
         <el-col :span="1.5">
            <el-button
               type="warning"
               plain
               icon="Download"
               @click="handleExport"
               v-hasPermi="['system:config:export']"
            >导出</el-button>
         </el-col>
         <!-- 刷新缓存按钮 -->
         <el-col :span="1.5">
            <el-button
               type="danger"
               plain
               icon="Refresh"
               @click="handleRefreshCache"
               v-hasPermi="['system:config:remove']"
            >刷新缓存</el-button>
         </el-col>
         <!-- 右侧工具栏 -->
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 参数配置列表 -->
      <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
         <!-- 多选列 -->
         <el-table-column type="selection" width="55" align="center" />
         <!-- 参数ID列 -->
         <el-table-column label="参数主键" align="center" prop="configId" />
         <!-- 参数名称列 -->
         <el-table-column label="参数名称" align="center" prop="configName" :show-overflow-tooltip="true" />
         <!-- 参数键名列 -->
         <el-table-column label="参数键名" align="center" prop="configKey" :show-overflow-tooltip="true" />
         <!-- 参数键值列 -->
         <el-table-column label="参数键值" align="center" prop="configValue" :show-overflow-tooltip="true" />
         <!-- 系统内置列 -->
         <el-table-column label="系统内置" align="center" prop="configType">
            <template #default="scope">
               <!-- 字典标签显示系统内置值 -->
               <dict-tag :options="sys_yes_no" :value="scope.row.configType" />
            </template>
         </el-table-column>
         <!-- 备注列 -->
         <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
         <!-- 创建时间列 -->
         <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template #default="scope">
               <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
         </el-table-column>
         <!-- 操作列 -->
         <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
            <template #default="scope">
               <!-- 行内修改按钮 -->
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:config:edit']" >修改</el-button>
               <!-- 行内删除按钮 -->
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:config:remove']">删除</el-button>
            </template>
         </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <pagination
         v-show="total > 0"
         :total="total"
         v-model:page="queryParams.pageNum"
         v-model:limit="queryParams.pageSize"
         @pagination="getList"
      />

      <!-- 添加或修改参数配置对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
         <!-- 参数表单 -->
         <el-form ref="configRef" :model="form" :rules="rules" label-width="80px">
            <!-- 参数名称输入 -->
            <el-form-item label="参数名称" prop="configName">
               <el-input v-model="form.configName" placeholder="请输入参数名称" />
            </el-form-item>
            <!-- 参数键名输入 -->
            <el-form-item label="参数键名" prop="configKey">
               <el-input v-model="form.configKey" placeholder="请输入参数键名" />
            </el-form-item>
            <!-- 参数键值输入 -->
            <el-form-item label="参数键值" prop="configValue">
               <el-input v-model="form.configValue" placeholder="请输入参数键值" />
            </el-form-item>
            <!-- 系统内置单选 -->
            <el-form-item label="系统内置" prop="configType">
               <el-radio-group v-model="form.configType">
                  <el-radio
                     v-for="dict in sys_yes_no"
                     :key="dict.value"
                     :label="dict.value"
                  >{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
            <!-- 备注输入 -->
            <el-form-item label="备注" prop="remark">
               <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
         </el-form>
         <!-- 对话框底部按钮 -->
         <template #footer>
            <div class="dialog-footer">
               <!-- 确定按钮 -->
               <el-button type="primary" @click="submitForm">确 定</el-button>
               <!-- 取消按钮 -->
               <el-button @click="cancel">取 消</el-button>
            </div>
         </template>
      </el-dialog>
   </div>
</template>

<script setup name="Config">
// 导入参数配置相关API
import { listConfig, getConfig, delConfig, addConfig, updateConfig, refreshCache } from "@/api/system/config";

// 获取组件代理
const { proxy } = getCurrentInstance();
// 获取系统是否字典
const { sys_yes_no } = proxy.useDict("sys_yes_no");

// 参数列表数据
const configList = ref([]);
// 对话框显示状态
const open = ref(false);
// 加载状态
const loading = ref(true);
// 搜索区域显示状态
const showSearch = ref(true);
// 选中行的ID数组
const ids = ref([]);
// 非单个禁用状态
const single = ref(true);
// 非多个禁用状态
const multiple = ref(true);
// 总记录数
const total = ref(0);
// 对话框标题
const title = ref("");
// 日期范围
const dateRange = ref([]);

// 表单数据和查询参数及校验规则
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    configName: undefined,
    configKey: undefined,
    configType: undefined
  },
  rules: {
    configName: [{ required: true, message: "参数名称不能为空", trigger: "blur" }],
    configKey: [{ required: true, message: "参数键名不能为空", trigger: "blur" }],
    configValue: [{ required: true, message: "参数键值不能为空", trigger: "blur" }]
  }
});

// 解构为ref
const { queryParams, form, rules } = toRefs(data);

/** 查询参数列表 */
function getList() {
  loading.value = true;
  // 调用列表API并附加日期范围
  listConfig(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    configList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}
/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}
/** 表单重置 */
function reset() {
  form.value = {
    configId: undefined,
    configName: undefined,
    configKey: undefined,
    configValue: undefined,
    configType: "Y",
    remark: undefined
  };
  proxy.resetForm("configRef");
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}
/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.configId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加参数";
}
/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const configId = row.configId || ids.value;
  // 获取参数详情
  getConfig(configId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改参数";
  });
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["configRef"].validate(valid => {
    if (valid) {
      if (form.value.configId != undefined) {
        // 修改参数
        updateConfig(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        // 新增参数
        addConfig(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}
/** 删除按钮操作 */
function handleDelete(row) {
  const configIds = row.configId || ids.value;
  proxy.$modal.confirm('是否确认删除参数编号为"' + configIds + '"的数据项？').then(function () {
    return delConfig(configIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}
/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/config/export", {
    ...queryParams.value
  }, `config_${new Date().getTime()}.xlsx`);
}
/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  refreshCache().then(() => {
    proxy.$modal.msgSuccess("刷新缓存成功");
  });
}

// 页面加载时获取列表
getList();
</script>
