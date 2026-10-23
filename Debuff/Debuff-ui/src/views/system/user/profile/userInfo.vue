<!-- 系统用户基本信息编辑页面 -->
<template>
   <!-- 用户信息编辑表单 -->
   <el-form ref="userRef" :model="form" :rules="rules" label-width="80px">
      <!-- 用户昵称输入项 -->
      <el-form-item label="用户昵称" prop="nickName">
         <el-input v-model="form.nickName" maxlength="30" />
      </el-form-item>
      <!-- 手机号码输入项 -->
      <el-form-item label="手机号码" prop="phonenumber">
         <el-input v-model="form.phonenumber" maxlength="11" />
      </el-form-item>
      <!-- 邮箱输入项 -->
      <el-form-item label="邮箱" prop="email">
         <el-input v-model="form.email" maxlength="50" />
      </el-form-item>
      <!-- 性别选择 -->
      <el-form-item label="性别">
         <el-radio-group v-model="form.sex">
            <el-radio label="0">男</el-radio>
            <el-radio label="1">女</el-radio>
         </el-radio-group>
      </el-form-item>
      <!-- 操作按钮区域 -->
      <el-form-item>
      <el-button type="primary" @click="submit">保存</el-button>
      <el-button type="danger" @click="close">关闭</el-button>
      </el-form-item>
   </el-form>
</template>

<script setup>
// 导入更新用户资料API
import { updateUserProfile } from "@/api/system/user";

// 接收父组件传入的用户对象
const props = defineProps({
  user: {
    type: Object
  }
});

// 获取组件代理实例
const { proxy } = getCurrentInstance();

// 表单数据
const form = ref({});

// 表单校验规则
const rules = ref({
  nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],  // 昵称必填
  email: [{ required: true, message: "邮箱地址不能为空", trigger: "blur" }, { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],  // 邮箱必填+格式校验
  phonenumber: [{ required: true, message: "手机号码不能为空", trigger: "blur" }, { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }],  // 手机号必填+正则校验
});

/** 提交按钮，校验通过后调用更新资料接口 */
function submit() {
  proxy.$refs.userRef.validate(valid => {
    if (valid) {
      updateUserProfile(form.value).then(response => {
        proxy.$modal.msgSuccess("修改成功");
        // 同步更新父组件中的手机号和邮箱
        props.user.phonenumber = form.value.phonenumber;
        props.user.email = form.value.email;
      });
    }
  });
};

/** 关闭按钮，关闭当前标签页 */
function close() {
  proxy.$tab.closePage();
};

// 监听props.user变化，回显当前登录用户信息到表单
watch(() => props.user, user => {
  if (user) {
    form.value = { nickName: user.nickName, phonenumber: user.phonenumber, email: user.email, sex: user.sex };
  }
},{ immediate: true });
</script>
