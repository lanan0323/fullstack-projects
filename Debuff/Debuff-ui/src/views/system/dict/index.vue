<!-- 字典类型管理页面 -->
<template>
   <div class="app-container">
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
         <!-- 字典名称搜索 -->
         <el-form-item label="字典名称" prop="dictName">
            <el-input
               v-model="queryParams.dictName"
               placeholder="请输入字典名称"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <!-- 字典类型搜索 -->
         <el-form-item label="字典类型" prop="dictType">
            <el-input
               v-model="queryParams.dictType"
               placeholder="请输入字典类型"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <!-- 状态筛选 -->
         <el-form-item label="状态" prop="status">
            <el-select
               v-model="queryParams.status"
               placeholder="字典状态"
               clearable
               style="width: 240px"
            >
               <el-option
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
               />
            </el-select>
         </el-form-item>
         <!-- 创建时间日期范围选择 -->
         <el-form-item label="创建时间" style="width: 308px">
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
               v-hasPermi="['system:dict:add']"
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
               v-hasPermi="['system:dict:edit']"
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
               v-hasPermi="['system:dict:remove']"
            >删除</el-button>
         </el-col>
         <!-- 导出按钮 -->
         <el-col :span="1.5">
            <el-button
               type="warning"
               plain
               icon="Download"
               @click="handleExport"
               v-hasPermi="['system:dict:export']"
            >导出</el-button>
         </el-col>
         <!-- 刷新缓存按钮 -->
         <el-col :span="1.5">
            <el-button
               type="danger"
               plain
               icon="Refresh"
               @click="handleRefreshCache"
               v-hasPermi="['system:dict:remove']"
            >刷新缓存</el-button>
         </el-col>
         <!-- 右侧工具栏 -->
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 字典类型列表 -->
      <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
         <!-- 多选列 -->
         <el-table-column type="selection" width="55" align="center" />
         <!-- 字典ID列 -->
         <el-table-column label="字典编号" align="center" prop="dictId" />
         <!-- 字典名称列 -->
         <el-table-column label="字典名称" align="center" prop="dictName" :show-overflow-tooltip="true"/>
         <!-- 字典类型列(可点击跳转到字典数据页) -->
         <el-table-column label="字典类型" align="center" :show-overflow-tooltip="true">
            <template #default="scope">
               <!-- 路由链接跳转到字典数据管理 -->
               <router-link :to="'/system/dict-data/index/' + scope.row.dictId" class="link-type">
                  <span>{{ scope.row.dictType }}</span>
               </router-link>
            </template>
         </el-table-column>
         <!-- 状态列 -->
         <el-table-column label="状态" align="center" prop="status">
            <template #default="scope">
               <!-- 字典标签显示状态 -->
               <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
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
         <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
            <template #default="scope">
               <!-- 行内修改按钮 -->
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:dict:edit']">修改</el-button>
               <!-- 行内删除按钮 -->
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:dict:remove']">删除</el-button>
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

      <!-- 添加或修改字典类型对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
         <!-- 字典类型表单 -->
         <el-form ref="dictRef" :model="form" :rules="rules" label-width="80px">
            <!-- 字典名称输入 -->
            <el-form-item label="字典名称" prop="dictName">
               <el-input v-model="form.dictName" placeholder="请输入字典名称" />
            </el-form-item>
            <!-- 字典类型输入 -->
            <el-form-item label="字典类型" prop="dictType">
               <el-input v-model="form.dictType" placeholder="请输入字典类型" />
            </el-form-item>
            <!-- 状态单选 -->
            <el-form-item label="状态" prop="status">
               <el-radio-group v-model="form.status">
                  <el-radio
                     v-for="dict in sys_normal_disable"
                     :key="dict.value"
                     :label="dict.value"
                  >{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
            <!-- 备注输入 -->
            <el-form-item label="备注" prop="remark">
               <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
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

<script setup name="Dict">
// 导入字典Store用于清除缓存
import useDictStore from '@/store/modules/dict'
// 导入字典类型CRUD API
import { listType, getType, delType, addType, updateType, refreshCache } from "@/api/system/dict/type";

// 获取组件代理
const { proxy } = getCurrentInstance();
// 获取正常/禁用字典
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

// 字典类型列表数据
const typeList = ref([]);
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
    dictName: undefined,
    dictType: undefined,
    status: undefined
  },
  rules: {
    dictName: [{ required: true, message: "字典名称不能为空", trigger: "blur" }],
    dictType: [{ required: true, message: "字典类型不能为空", trigger: "blur" }]
  },
});

// 解构为ref
const { queryParams, form, rules } = toRefs(data);

/** 查询字典类型列表 */
function getList() {
  loading.value = true;
  // 调用列表API并附加日期范围
  listType(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    typeList.value = response.rows;
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
    dictId: undefined,
    dictName: undefined,
    dictType: undefined,
    status: "0",
    remark: undefined
  };
  proxy.resetForm("dictRef");
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
/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加字典类型";
}
/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.dictId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const dictId = row.dictId || ids.value;
  // 获取字典类型详情
  getType(dictId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改字典类型";
  });
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["dictRef"].validate(valid => {
    if (valid) {
      if (form.value.dictId != undefined) {
        // 修改字典类型
        updateType(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        // 新增字典类型
        addType(form.value).then(response => {
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
  const dictIds = row.dictId || ids.value;
  proxy.$modal.confirm('是否确认删除字典编号为"' + dictIds + '"的数据项？').then(function() {
    return delType(dictIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}
/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/dict/type/export", {
    ...queryParams.value
  }, `dict_${new Date().getTime()}.xlsx`);
}
/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  refreshCache().then(() => {
    proxy.$modal.msgSuccess("刷新成功");
    // 清除前端字典缓存
    useDictStore().cleanDict();
  });
}

// 页面加载时获取列表
getList();
</script>
