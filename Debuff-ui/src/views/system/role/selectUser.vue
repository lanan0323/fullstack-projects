<!-- 选择用户弹窗组件，用于给角色分配用户 -->
<template>
   <!-- 授权用户选择弹窗 -->
   <el-dialog title="选择用户" v-model="visible" width="800px" top="5vh" append-to-body>
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true">
         <!-- 用户名称搜索 -->
         <el-form-item label="用户名称" prop="userName">
            <el-input
               v-model="queryParams.userName"
               placeholder="请输入用户名称"
               clearable
               style="width: 200px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <!-- 手机号码搜索 -->
         <el-form-item label="手机号码" prop="phonenumber">
            <el-input
               v-model="queryParams.phonenumber"
               placeholder="请输入手机号码"
               clearable
               style="width: 200px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <!-- 搜索/重置按钮 -->
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>
      <el-row>
         <!-- 未分配用户列表表格，支持行点击选中 -->
         <el-table @row-click="clickRow" ref="refTable" :data="userList" @selection-change="handleSelectionChange" height="260px">
            <!-- 多选列 -->
            <el-table-column type="selection" width="55"></el-table-column>
            <!-- 用户名称列 -->
            <el-table-column label="用户名称" prop="userName" :show-overflow-tooltip="true" />
            <!-- 用户昵称列 -->
            <el-table-column label="用户昵称" prop="nickName" :show-overflow-tooltip="true" />
            <!-- 邮箱列 -->
            <el-table-column label="邮箱" prop="email" :show-overflow-tooltip="true" />
            <!-- 手机号列 -->
            <el-table-column label="手机" prop="phonenumber" :show-overflow-tooltip="true" />
            <!-- 状态列 -->
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
      </el-row>
      <!-- 弹窗底部按钮 -->
      <template #footer>
         <div class="dialog-footer">
            <el-button type="primary" @click="handleSelectUser">确 定</el-button>
            <el-button @click="visible = false">取 消</el-button>
         </div>
      </template>
   </el-dialog>
</template>

<script setup name="SelectUser">
// 导入批量授权和未分配用户列表API
import { authUserSelectAll, unallocatedUserList } from "@/api/system/role";

// 接收父组件传入的角色ID
const props = defineProps({
  roleId: {
    type: [Number, String]
  }
});

// 获取组件代理实例
const { proxy } = getCurrentInstance();
// 获取系统启停字典
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

// 用户列表数据
const userList = ref([]);
// 弹窗显示状态
const visible = ref(false);
// 数据总数
const total = ref(0);
// 选中的用户ID数组
const userIds = ref([]);

// 查询参数
const queryParams = reactive({
  pageNum: 1,           // 当前页码
  pageSize: 10,         // 每页条数
  roleId: undefined,    // 角色ID
  userName: undefined,  // 用户名搜索
  phonenumber: undefined // 手机号搜索
});

// 显示弹窗，设置角色ID并加载数据
function show() {
  queryParams.roleId = props.roleId;
  getList();
  visible.value = true;
}

/** 点击行切换选中状态 */
function clickRow(row) {
  proxy.$refs["refTable"].toggleRowSelection(row);
}

// 多选框选中数据回调，收集用户ID
function handleSelectionChange(selection) {
  userIds.value = selection.map(item => item.userId);
}

// 查询未分配用户列表
function getList() {
  unallocatedUserList(queryParams).then(res => {
    userList.value = res.rows;   // 用户列表
    total.value = res.total;     // 总数
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 定义emit事件
const emit = defineEmits(["ok"]);

/** 选择授权用户操作，批量分配选中用户到角色 */
function handleSelectUser() {
  const roleId = queryParams.roleId;
  const uIds = userIds.value.join(",");  // 拼接用户ID
  // 校验是否选择了用户
  if (uIds == "") {
    proxy.$modal.msgError("请选择要分配的用户");
    return;
  }
  // 调用批量授权接口
  authUserSelectAll({ roleId: roleId, userIds: uIds }).then(res => {
    proxy.$modal.msgSuccess(res.msg);
    if (res.code === 200) {
      visible.value = false;  // 关闭弹窗
      emit("ok");             // 通知父组件刷新
    }
  });
}

// 暴露show方法供父组件调用
defineExpose({
  show,
});
</script>
