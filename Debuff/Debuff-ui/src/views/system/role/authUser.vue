<!-- 角色已授权用户列表页面，展示该角色下的所有用户 -->
<template>
   <div class="app-container">
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" v-show="showSearch" :inline="true">
         <!-- 用户名称搜索 -->
         <el-form-item label="用户名称" prop="userName">
            <el-input
               v-model="queryParams.userName"
               placeholder="请输入用户名称"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <!-- 手机号码搜索 -->
         <el-form-item label="手机号码" prop="phonenumber">
            <el-input
               v-model="queryParams.phonenumber"
               placeholder="请输入手机号码"
               clearable
               style="width: 240px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <!-- 搜索/重置按钮 -->
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <!-- 工具栏按钮行 -->
      <el-row :gutter="10" class="mb8">
         <!-- 添加用户按钮 -->
         <el-col :span="1.5">
            <el-button
               type="primary"
               plain
               icon="Plus"
               @click="openSelectUser"
               v-hasPermi="['system:role:add']"
            >添加用户</el-button>
         </el-col>
         <!-- 批量取消授权按钮 -->
         <el-col :span="1.5">
            <el-button
               type="danger"
               plain
               icon="CircleClose"
               :disabled="multiple"
               @click="cancelAuthUserAll"
               v-hasPermi="['system:role:remove']"
            >批量取消授权</el-button>
         </el-col>
         <!-- 关闭按钮 -->
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

      <!-- 已授权用户数据表格 -->
      <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
         <!-- 多选列 -->
         <el-table-column type="selection" width="55" align="center" />
         <!-- 用户名称列 -->
         <el-table-column label="用户名称" prop="userName" :show-overflow-tooltip="true" />
         <!-- 用户昵称列 -->
         <el-table-column label="用户昵称" prop="nickName" :show-overflow-tooltip="true" />
         <!-- 邮箱列 -->
         <el-table-column label="邮箱" prop="email" :show-overflow-tooltip="true" />
         <!-- 手机号列 -->
         <el-table-column label="手机" prop="phonenumber" :show-overflow-tooltip="true" />
         <!-- 状态列，使用字典标签展示 -->
         <el-table-column label="状态" align="center" prop="status">
            <template #default="scope">
               <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
            </template>
         </el-table-column>
         <!-- 创建时间列 -->
         <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template #default="scope">
               <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
         </el-table-column>
         <!-- 操作列：取消授权 -->
         <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-button link type="primary" icon="CircleClose" @click="cancelAuthUser(scope.row)" v-hasPermi="['system:role:remove']">取消授权</el-button>
            </template>
         </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <pagination
         :fixed="false"
         v-show="total > 0"
         :total="total"
         v-model:page="queryParams.pageNum"
         v-model:limit="queryParams.pageSize"
         @pagination="getList"
      />
      <!-- 选择用户弹窗组件 -->
      <select-user ref="selectRef" :roleId="queryParams.roleId" @ok="handleQuery" />
   </div>
</template>

<script setup name="AuthUser">
// 导入选择用户弹窗子组件
import selectUser from "./selectUser";
// 导入角色相关API：已分配用户列表、取消授权、批量取消授权
import { allocatedUserList, authUserCancel, authUserCancelAll } from "@/api/system/role";

// 获取路由信息
const route = useRoute();
// 获取组件代理实例
const { proxy } = getCurrentInstance();
// 获取系统启停字典
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

// 用户列表数据
const userList = ref([]);
// 加载状态
const loading = ref(true);
// 搜索区域显示状态
const showSearch = ref(true);
// 是否禁用批量按钮(未选中时为true)
const multiple = ref(true);
// 数据总数
const total = ref(0);
// 选中的用户ID数组
const userIds = ref([]);

// 查询参数
const queryParams = reactive({
  pageNum: 1,           // 当前页码
  pageSize: 10,         // 每页条数
  roleId: route.params.roleId,  // 角色ID(从路由参数获取)
  userName: undefined,   // 用户名搜索条件
  phonenumber: undefined, // 手机号搜索条件
});

/** 查询已授权用户列表 */
function getList() {
  loading.value = true;
  allocatedUserList(queryParams).then(response => {
    userList.value = response.rows;   // 用户列表数据
    total.value = response.total;     // 数据总数
    loading.value = false;            // 关闭加载状态
  });
}

// 返回按钮，关闭当前标签并跳转到角色管理页
function handleClose() {
  const obj = { path: "/system/role" };
  proxy.$tab.closeOpenPage(obj);
}

/** 搜索按钮操作，重置页码后查询 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作，清空搜索条件后查询 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据回调，收集选中的用户ID
function handleSelectionChange(selection) {
  userIds.value = selection.map(item => item.userId);
  multiple.value = !selection.length;  // 未选中时禁用批量按钮
}

/** 打开选择用户弹窗 */
function openSelectUser() {
  proxy.$refs["selectRef"].show();
}

/** 取消单个用户授权 */
function cancelAuthUser(row) {
  proxy.$modal.confirm('确认要取消该用户"' + row.userName + '"角色吗？').then(function () {
    return authUserCancel({ userId: row.userId, roleId: queryParams.roleId });
  }).then(() => {
    getList();                        // 刷新列表
    proxy.$modal.msgSuccess("取消授权成功");
  }).catch(() => {});
}

/** 批量取消授权 */
function cancelAuthUserAll(row) {
  const roleId = queryParams.roleId;
  const uIds = userIds.value.join(",");  // 将选中用户ID拼接为逗号分隔字符串
  proxy.$modal.confirm("是否取消选中用户授权数据项?").then(function () {
    return authUserCancelAll({ roleId: roleId, userIds: uIds });
  }).then(() => {
    getList();                        // 刷新列表
    proxy.$modal.msgSuccess("取消授权成功");
  }).catch(() => {});
}

// 页面加载时查询列表
getList();
</script>
