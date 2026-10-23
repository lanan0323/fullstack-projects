<!-- 系统用户个人中心页面，展示个人信息和编辑资料 -->
<template>
   <div class="app-container">
      <el-row :gutter="20">
         <!-- 左侧：个人信息卡片 -->
         <el-col :span="6" :xs="24">
            <el-card class="box-card">
               <!-- 卡片头部标题 -->
               <template v-slot:header>
                 <div class="clearfix">
                   <span>个人信息</span>
                 </div>
               </template>
               <div>
                  <!-- 用户头像组件 -->
                  <div class="text-center">
                     <userAvatar />
                  </div>
                  <!-- 用户信息列表 -->
                  <ul class="list-group list-group-striped">
                     <!-- 用户名 -->
                     <li class="list-group-item">
                        <svg-icon icon-class="user" />用户名称
                        <div class="pull-right">{{ state.user.userName }}</div>
                     </li>
                     <!-- 手机号 -->
                     <li class="list-group-item">
                        <svg-icon icon-class="phone" />手机号码
                        <div class="pull-right">{{ state.user.phonenumber }}</div>
                     </li>
                     <!-- 邮箱 -->
                     <li class="list-group-item">
                        <svg-icon icon-class="email" />用户邮箱
                        <div class="pull-right">{{ state.user.email }}</div>
                     </li>
                     <!-- 所属部门 -->
                     <li class="list-group-item">
                        <svg-icon icon-class="tree" />所属部门
                        <div class="pull-right" v-if="state.user.dept">{{ state.user.dept.deptName }} / {{ state.postGroup }}</div>
                     </li>
                     <!-- 所属角色 -->
                     <li class="list-group-item">
                        <svg-icon icon-class="peoples" />所属角色
                        <div class="pull-right">{{ state.roleGroup }}</div>
                     </li>
                     <!-- 创建日期 -->
                     <li class="list-group-item">
                        <svg-icon icon-class="date" />创建日期
                        <div class="pull-right">{{ state.user.createTime }}</div>
                     </li>
                  </ul>
               </div>
            </el-card>
         </el-col>
         <!-- 右侧：基本资料编辑区 -->
         <el-col :span="18" :xs="24">
            <el-card>
               <!-- 卡片头部标题 -->
               <template v-slot:header>
                 <div class="clearfix">
                   <span>基本资料</span>
                 </div>
               </template>
               <!-- 标签页切换：基本资料/修改密码 -->
               <el-tabs v-model="activeTab">
                  <el-tab-pane label="基本资料" name="userinfo">
                     <!-- 用户信息编辑子组件 -->
                     <userInfo :user="state.user" />
                  </el-tab-pane>
                  <el-tab-pane label="修改密码" name="resetPwd">
                     <!-- 修改密码子组件 -->
                     <resetPwd />
                  </el-tab-pane>
               </el-tabs>
            </el-card>
         </el-col>
      </el-row>
   </div>
</template>

<script setup name="Profile">
// 导入子组件
import userAvatar from "./userAvatar";   // 头像组件
import userInfo from "./userInfo";       // 用户信息编辑组件
import resetPwd from "./resetPwd";       // 修改密码组件
// 导入获取用户资料API
import { getUserProfile } from "@/api/system/user";

// 当前激活的标签页，默认显示基本资料
const activeTab = ref("userinfo");

// 页面数据状态
const state = reactive({
  user: {},       // 用户信息
  roleGroup: {},  // 角色名称
  postGroup: {}   // 岗位名称
});

/** 获取当前登录用户信息 */
function getUser() {
  getUserProfile().then(response => {
    state.user = response.data;          // 用户基本信息
    state.roleGroup = response.roleGroup; // 所属角色
    state.postGroup = response.postGroup; // 所属岗位
  });
};

// 页面加载时获取用户信息
getUser();
</script>
