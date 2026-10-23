<!-- 系统用户修改密码页面 -->
<template>
   <!-- 修改密码表单 -->
   <el-form ref="pwdRef" :model="user" :rules="rules" label-width="80px">
      <!-- 旧密码输入项 -->
      <el-form-item label="旧密码" prop="oldPassword">
         <el-input v-model="user.oldPassword" placeholder="请输入旧密码" type="password" show-password />
      </el-form-item>
      <!-- 新密码输入项 -->
      <el-form-item label="新密码" prop="newPassword">
         <el-input v-model="user.newPassword" placeholder="请输入新密码" type="password" show-password />
      </el-form-item>
      <!-- 确认密码输入项 -->
      <el-form-item label="确认密码" prop="confirmPassword">
         <el-input v-model="user.confirmPassword" placeholder="请确认新密码" type="password" show-password/>
      </el-form-item>
      <!-- 操作按钮区域 -->
      <el-form-item>
      <el-button type="primary" @click="submit">保存</el-button>
      <el-button type="danger" @click="close">关闭</el-button>
      </el-form-item>
   </el-form>
</template>

<script setup>
// 导入修改密码API
import { updateUserPwd } from "@/api/system/user";

// 获取组件代理实例
const { proxy } = getCurrentInstance();

// 密码表单数据
const user = reactive({
  oldPassword: undefined,  // 旧密码
  newPassword: undefined,  // 新密码
  confirmPassword: undefined  // 确认密码
});

// 确认密码自定义校验器，检查两次密码是否一致
const equalToPassword = (rule, value, callback) => {
  if (user.newPassword !== value) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

// 表单校验规则
const rules = ref({
  oldPassword: [{ required: true, message: "旧密码不能为空", trigger: "blur" }],  // 旧密码必填
  newPassword: [{ required: true, message: "新密码不能为空", trigger: "blur" }, { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" }],  // 新密码必填+长度限制
  confirmPassword: [{ required: true, message: "确认密码不能为空", trigger: "blur" }, { required: true, validator: equalToPassword, trigger: "blur" }]  // 确认密码必填+一致性校验
});

/** 提交按钮，校验通过后调用修改密码接口 */
function submit() {
  proxy.$refs.pwdRef.validate(valid => {
    if (valid) {
      updateUserPwd(user.oldPassword, user.newPassword).then(response => {
        proxy.$modal.msgSuccess("修改成功");
      });
    }
  });
};

/** 关闭按钮，关闭当前标签页 */
function close() {
  proxy.$tab.closePage();
};
</script>
