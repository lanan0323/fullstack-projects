<template>
   <!-- 个人中心页面根容器 -->
   <div class="app-container profile-page">
      <!-- 行布局，左右两栏 -->
      <el-row :gutter="20">
         <!-- 左侧栏：个人信息卡片 -->
         <el-col :span="6" :xs="24">
            <!-- 个人信息卡片 -->
            <el-card class="box-card">
               <!-- 卡片头部 -->
               <template v-slot:header>
                 <!-- 头部内容 -->
                 <div class="clearfix">
                   <!-- 标题文字 -->
                   <span>个人信息</span>
                 </div>
               </template>
               <!-- 卡片主体内容 -->
               <div>
                  <!-- 头像区域，居中显示 -->
                  <div class="text-center">
                     <!-- 用户头像组件 -->
                     <userAvatar />
                  </div>
                  <!-- 用户信息列表 -->
                  <ul class="list-group list-group-striped">
                     <!-- 用户名称 -->
                     <li class="list-group-item">
                        <!-- 图标 -->
                        <svg-icon icon-class="user" />用户名称
                        <!-- 用户名数据 -->
                        <div class="pull-right">{{ state.user.userName }}</div>
                     </li>
                     <!-- 手机号码 -->
                     <li class="list-group-item">
                        <!-- 图标 -->
                        <svg-icon icon-class="phone" />手机号码
                        <!-- 手机号数据，空值显示破折号 -->
                        <div class="pull-right">{{ state.user.phonenumber || '—' }}</div>
                     </li>
                     <!-- 用户邮箱 -->
                     <li class="list-group-item">
                        <!-- 图标 -->
                        <svg-icon icon-class="email" />用户邮箱
                        <!-- 邮箱数据，空值显示破折号 -->
                        <div class="pull-right">{{ state.user.email || '—' }}</div>
                     </li>
                     <!-- 所属角色 -->
                     <li class="list-group-item">
                        <!-- 图标 -->
                        <svg-icon icon-class="peoples" />所属角色
                        <!-- 角色组数据 -->
                        <div class="pull-right">{{ state.roleGroup }}</div>
                     </li>
                     <!-- 创建日期 -->
                     <li class="list-group-item">
                        <!-- 图标 -->
                        <svg-icon icon-class="date" />创建日期
                        <!-- 创建时间数据 -->
                        <div class="pull-right">{{ state.user.createTime }}</div>
                     </li>
                  </ul>
               </div>
            </el-card>
         </el-col>
         <!-- 右侧栏：基本资料+修改密码 -->
         <el-col :span="18" :xs="24">
            <!-- 资料卡片 -->
            <el-card>
               <!-- 卡片头部 -->
               <template v-slot:header>
                 <!-- 头部内容 -->
                 <div class="clearfix">
                   <!-- 标题文字 -->
                   <span>基本资料</span>
                 </div>
               </template>
               <!-- 标签页切换 -->
               <el-tabs v-model="activeTab">
                  <!-- 基本资料标签页 -->
                  <el-tab-pane label="基本资料" name="userinfo">
                     <!-- 用户信息编辑组件 -->
                     <userInfo :user="state.user" />
                  </el-tab-pane>
                  <!-- 修改密码标签页 -->
                  <el-tab-pane label="修改密码" name="resetPwd">
                     <!-- 重置密码组件 -->
                     <resetPwd />
                  </el-tab-pane>
               </el-tabs>
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="Profile">
// 引入用户头像子组件
import userAvatar from "./userAvatar";
// 引入用户信息编辑子组件
import userInfo from "./userInfo";
// 引入修改密码子组件
import resetPwd from "./resetPwd";
// 引入获取用户资料API
import { getUserProfile } from "@/api/system/user";

// 当前激活的标签页，默认基本资料
const activeTab = ref("userinfo");
// 页面状态数据
const state = reactive({
  // 用户信息对象
  user: {},
  // 角色组
  roleGroup: {},
  // 岗位组
  postGroup: {}
});

// 获取用户资料
function getUser() {
  // 调用获取用户资料接口
  getUserProfile().then(response => {
    // 更新用户信息
    state.user = response.data;
    // 更新角色组
    state.roleGroup = response.roleGroup;
    // 更新岗位组
    state.postGroup = response.postGroup;
  });
};

// 页面首次挂载时获取用户资料
import { onMounted, onActivated } from 'vue'
onMounted(() => {
  getUser();
})
// keep-alive缓存激活时重新获取用户资料
onActivated(() => {
  getUser();
})
</script>

<style scoped>
/* 个人中心页面根样式 */
.profile-page {
  /* 最小高度撑满视口 */
  min-height: calc(100vh - 116px);
  /* 内边距 */
  padding: 28px max(20px, 4vw) 40px;
  /* 浅灰背景 */
  background: #f4f6f8;
}
/* 卡片样式覆盖 */
.profile-page :deep(.el-card) {
  /* 边框 */
  border: 1px solid #e3e8ef;
  /* 小圆角 */
  border-radius: 4px;
  /* 轻微阴影 */
  box-shadow: 0 2px 8px rgba(31, 45, 61, 0.04);
  /* 白色背景 */
  background: #fff;
}
/* 卡片头部样式 */
.profile-page :deep(.el-card__header) {
  /* 底部边框颜色 */
  border-bottom-color: #edf0f4;
  /* 深色文字 */
  color: #1f2d3d;
  /* 粗体 */
  font-weight: 600;
}
/* 信息列表项样式 */
.profile-page :deep(.list-group-item) {
  /* 底部边框颜色 */
  border-bottom-color: #edf0f4;
  /* 文字颜色 */
  color: #526275;
}
/* 右侧数据文字颜色 */
.profile-page :deep(.pull-right) { color: #8290a3; }
/* 标签页未激活颜色 */
.profile-page :deep(.el-tabs__item) { color: #8290a3; }
/* 标签页激活颜色 */
.profile-page :deep(.el-tabs__item.is-active) { color: #2f80ed; }
/* 标签页激活指示条 */
.profile-page :deep(.el-tabs__active-bar) { background: #2f80ed; }
/* 小屏适配 */
@media (max-width: 900px) {
  /* 缩小内边距 */
  .profile-page { padding: 18px 14px 32px; }
}
</style>
