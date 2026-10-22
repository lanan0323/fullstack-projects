<!-- 字典数据管理页面 -->
<template>
   <div class="app-container">
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
         <!-- 字典类型选择 -->
         <el-form-item label="字典名称" prop="dictType">
            <el-select v-model="queryParams.dictType" style="width: 200px">
               <el-option
                  v-for="item in typeOptions"
                  :key="item.dictId"
                  :label="item.dictName"
                  :value="item.dictType"
               />
            </el-select>
         </el-form-item>
         <!-- 字典标签搜索 -->
         <el-form-item label="字典标签" prop="dictLabel">
            <el-input
               v-model="queryParams.dictLabel"
               placeholder="请输入字典标签"
               clearable
               style="width: 200px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <!-- 状态筛选 -->
         <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="数据状态" clearable style="width: 200px">
               <el-option
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
               />
            </el-select>
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
         <!-- 关闭按钮(返回字典类型页) -->
         <el-col :span="1.5">
            <el-button
               type="warning"
               plain
               icon="Close"
               @click="handleClose"
            >关闭</el-button>
         </el-col>
         <!-- 右侧工具栏 -->
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 字典数据列表 -->
      <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
         <!-- 多选列 -->
         <el-table-column type="selection" width="55" align="center" />
         <!-- 字典编码列 -->
         <el-table-column label="字典编码" align="center" prop="dictCode" />
         <!-- 字典标签列(带样式) -->
         <el-table-column label="字典标签" align="center" prop="dictLabel">
            <template #default="scope">
               <!-- 无样式时直接显示文本 -->
               <span v-if="(scope.row.listClass == '' || scope.row.listClass == 'default') && (scope.row.cssClass == '' || scope.row.cssClass == null)">{{ scope.row.dictLabel }}</span>
               <!-- 有样式时用el-tag渲染 -->
               <el-tag v-else :type="scope.row.listClass == 'primary' ? '' : scope.row.listClass" :class="scope.row.cssClass">{{ scope.row.dictLabel }}</el-tag>
            </template>
         </el-table-column>
         <!-- 字典键值列 -->
         <el-table-column label="字典键值" align="center" prop="dictValue" />
         <!-- 字典排序列 -->
         <el-table-column label="字典排序" align="center" prop="dictSort" />
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

      <!-- 添加或修改字典数据对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
         <!-- 字典数据表单 -->
         <el-form ref="dataRef" :model="form" :rules="rules" label-width="80px">
            <!-- 字典类型(只读) -->
            <el-form-item label="字典类型">
               <el-input v-model="form.dictType" :disabled="true" />
            </el-form-item>
            <!-- 数据标签输入 -->
            <el-form-item label="数据标签" prop="dictLabel">
               <el-input v-model="form.dictLabel" placeholder="请输入数据标签" />
            </el-form-item>
            <!-- 数据键值输入 -->
            <el-form-item label="数据键值" prop="dictValue">
               <el-input v-model="form.dictValue" placeholder="请输入数据键值" />
            </el-form-item>
            <!-- CSS样式属性输入 -->
            <el-form-item label="样式属性" prop="cssClass">
               <el-input v-model="form.cssClass" placeholder="请输入样式属性" />
            </el-form-item>
            <!-- 显示排序数字输入 -->
            <el-form-item label="显示排序" prop="dictSort">
               <el-input-number v-model="form.dictSort" controls-position="right" :min="0" />
            </el-form-item>
            <!-- 回显样式选择 -->
            <el-form-item label="回显样式" prop="listClass">
               <el-select v-model="form.listClass">
                  <el-option
                     v-for="item in listClassOptions"
                     :key="item.value"
                     :label="item.label + '(' + item.value + ')'"
                     :value="item.value"
                  ></el-option>
               </el-select>
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

<script setup name="Data">
// 导入字典Store用于清除缓存
import useDictStore from '@/store/modules/dict'
// 导入字典类型选项和类型详情API
import { optionselect as getDictOptionselect, getType } from "@/api/system/dict/type";
// 导入字典数据CRUD API
import { listData, getData, delData, addData, updateData } from "@/api/system/dict/data";

// 获取组件代理
const { proxy } = getCurrentInstance();
// 获取正常/禁用字典
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

// 字典数据列表
const dataList = ref([]);
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
// 默认字典类型(从路由参数获取)
const defaultDictType = ref("");
// 字典类型下拉选项
const typeOptions = ref([]);
// 当前路由信息
const route = useRoute();
// 数据标签回显样式选项
const listClassOptions = ref([
  { value: "default", label: "默认" }, 
  { value: "primary", label: "主要" }, 
  { value: "success", label: "成功" },
  { value: "info", label: "信息" },
  { value: "warning", label: "警告" },
  { value: "danger", label: "危险" }
]);

// 表单数据和查询参数及校验规则
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    dictType: undefined,
    dictLabel: undefined,
    status: undefined
  },
  rules: {
    dictLabel: [{ required: true, message: "数据标签不能为空", trigger: "blur" }],
    dictValue: [{ required: true, message: "数据键值不能为空", trigger: "blur" }],
    dictSort: [{ required: true, message: "数据顺序不能为空", trigger: "blur" }]
  }
});

// 解构为ref
const { queryParams, form, rules } = toRefs(data);

/** 根据字典ID查询字典类型并加载数据 */
function getTypes(dictId) {
  getType(dictId).then(response => {
    queryParams.value.dictType = response.data.dictType;
    defaultDictType.value = response.data.dictType;
    getList();
  });
}

/** 查询字典类型列表(用于下拉选择) */
function getTypeList() {
  getDictOptionselect().then(response => {
    typeOptions.value = response.data;
  });
}
/** 查询字典数据列表 */
function getList() {
  loading.value = true;
  listData(queryParams.value).then(response => {
    dataList.value = response.rows;
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
    dictCode: undefined,
    dictLabel: undefined,
    dictValue: undefined,
    cssClass: undefined,
    listClass: "default",
    dictSort: 0,
    status: "0",
    remark: undefined
  };
  proxy.resetForm("dataRef");
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 返回按钮操作(关闭当前页返回字典类型列表) */
function handleClose() {
  const obj = { path: "/system/dict" };
  proxy.$tab.closeOpenPage(obj);
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  // 重置后恢复默认字典类型
  queryParams.value.dictType = defaultDictType.value;
  handleQuery();
}
/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加字典数据";
  // 将当前字典类型带入表单
  form.value.dictType = queryParams.value.dictType;
}
/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.dictCode);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const dictCode = row.dictCode || ids.value;
  // 获取字典数据详情
  getData(dictCode).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改字典数据";
  });
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["dataRef"].validate(valid => {
    if (valid) {
      if (form.value.dictCode != undefined) {
        // 修改字典数据
        updateData(form.value).then(response => {
          // 清除该字典类型的缓存
          useDictStore().removeDict(queryParams.value.dictType);
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        // 新增字典数据
        addData(form.value).then(response => {
          // 清除该字典类型的缓存
          useDictStore().removeDict(queryParams.value.dictType);
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
  const dictCodes = row.dictCode || ids.value;
  proxy.$modal.confirm('是否确认删除字典编码为"' + dictCodes + '"的数据项？').then(function() {
    return delData(dictCodes);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
    // 删除后清除字典缓存
    useDictStore().removeDict(queryParams.value.dictType);
  }).catch(() => {});
}
/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/dict/data/export", {
    ...queryParams.value
  }, `dict_data_${new Date().getTime()}.xlsx`);
}

// 页面加载时根据路由参数获取字典类型并查询数据
getTypes(route.params && route.params.dictId);
// 加载字典类型下拉选项
getTypeList();
</script>
