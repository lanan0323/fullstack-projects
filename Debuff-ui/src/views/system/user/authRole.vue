<!-- 用户授权角色页面，给用户分配角色 -->
<template>
   <div class="app-container">
      <!-- 基本信息展示区 -->
      <h4 class="form-header h4">基本信息</h4>
      <el-form :model="form" label-width="80px">
         <el-row>
            <!-- 用户昵称(只读) -->
            <el-col :span="8" :offset="2">
               <el-form-item label="用户昵称" prop="nickName">
                  <el-input v-model="form.nickName" disabled />
               </el-form-item>
            </el-col>
            <!-- 登录账号(只读) -->
            <el-col :span="8" :offset="2">
               <el-form-item label="登录账号" prop="userName">
                  <el-input v-model="form.userName" disabled />
               </el-form-item>
            </el-col>
         </el-row>
      </el-form>

      <!-- 角色信息表格 -->
      <h4 class="form-header h4">角色信息</h4>
      <el-table v-loading="loading" :row-key="getRowKey" @row-click="clickRow" ref="roleRef" @selection-change="handleSelectionChange" :data="roles.slice((pageNum - 1) * pageSize, pageNum * pageSize)">
         <!-- 序号列(跨页连续) -->
         <el-table-column label="序号" width="55" type="index" align="center">
            <template #default="scope">
               <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
            </template>
         </el-table-column>
         <!-- 多选列，支持跨页保持选中 -->
         <el-table-column type="selection" :reserve-selection="true" width="55"></el-table-column>
         <!-- 角色编号列 -->
         <el-table-column label="角色编号" align="center" prop="roleId" />
         <!-- 角色名称列 -->
         <el-table-column label="角色名称" align="center" prop="roleName" />
         <!-- 权限字符列 -->
         <el-table-column label="权限字符" align="center" prop="roleKey" />
         <!-- 创建时间列 -->
         <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template #default="scope">
               <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
         </el-table-column>
      </el-table>

      <!-- 分页组件(前端分页) -->
      <pagination :fixed="false" v-show="total > 0" :total="total" v-model:page="pageNum" v-model:limit="pageSize" />

      <!-- 操作按钮 -->
      <el-form label-width="100px">
         <div style="text-align: center;margin-left:-120px;margin-top:30px;">
            <el-button type="primary" @click="submitForm()">提交</el-button>
            <el-button @click="close()">返回</el-button>
         </div>
      </el-form>
   </div>
</template>

<script setup name="AuthRole">
// 导入获取用户角色和更新用户角色API
import { getAuthRole, updateAuthRole } from "@/api/system/user";

// 获取路由信息
const route = useRoute();
// 获取组件代理实例
const { proxy } = getCurrentInstance();

// 加载状态
const loading = ref(true);
// 角色数据总数
const total = ref(0);
// 当前页码
const pageNum = ref(1);
// 每页条数
const pageSize = ref(10);
// 选中的角色ID数组
const roleIds = ref([]);
// 所有角色列表
const roles = ref([]);
// 用户基本信息表单
const form = ref({
  nickName: undefined,
  userName: undefined,
  userId: undefined
});

/** 单击表格行切换选中状态 */
function clickRow(row) {
  proxy.$refs["roleRef"].toggleRowSelection(row);
};

/** 多选框选中数据回调，收集角色ID */
function handleSelectionChange(selection) {
  roleIds.value = selection.map(item => item.roleId);
};

/** 获取行的唯一key(用于跨页保持选中) */
function getRowKey(row) {
  return row.roleId;
};

/** 关闭按钮，返回用户管理页 */
function close() {
  const obj = { path: "/system/user" };
  proxy.$tab.closeOpenPage(obj);
};

/** 提交按钮，保存用户角色授权关系 */
function submitForm() {
  const userId = form.value.userId;
  const rIds = roleIds.value.join(",");  // 拼接角色ID
  updateAuthRole({ userId: userId, roleIds: rIds }).then(response => {
    proxy.$modal.msgSuccess("授权成功");
    close();  // 授权成功后返回
  });
};

// 立即执行：根据路由参数userId加载用户角色数据
(() => {
  const userId = route.params && route.params.userId;
  if (userId) {
    loading.value = true;
    getAuthRole(userId).then(response => {
      form.value = response.user;     // 用户信息
      roles.value = response.roles;   // 角色列表
      total.value = roles.value.length;
      // 回显已授权的角色(flag为true的默认选中)
      nextTick(() => {
        roles.value.forEach(row => {
          if (row.flag) {
            proxy.$refs["roleRef"].toggleRowSelection(row);
          }
        });
      });
      loading.value = false;
    });
  }
})();
</script>
