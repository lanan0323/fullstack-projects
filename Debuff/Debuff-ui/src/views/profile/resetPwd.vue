<template>
   <!-- 修改密码表单，绑定表单数据、校验规则和引用 -->
   <el-form ref="pwdRef" :model="user" :rules="rules" label-width="80px">
      <!-- 旧密码输入项 -->
      <el-form-item label="旧密码" prop="oldPassword">
         <!-- 旧密码输入框，支持显示/隐藏密码 -->
         <el-input v-model="user.oldPassword" placeholder="请输入旧密码" type="password" show-password />
      </el-form-item>
      <!-- 新密码输入项 -->
      <el-form-item label="新密码" prop="newPassword">
         <!-- 新密码输入框，支持显示/隐藏密码 -->
         <el-input v-model="user.newPassword" placeholder="请输入新密码" type="password" show-password />
      </el-form-item>
      <!-- 确认密码输入项 -->
      <el-form-item label="确认密码" prop="confirmPassword">
         <!-- 确认密码输入框，支持显示/隐藏密码 -->
         <el-input v-model="user.confirmPassword" placeholder="请确认新密码" type="password" show-password/>
      </el-form-item>
      <!-- 操作按钮区域 -->
      <el-form-item>
      <!-- 保存按钮，点击触发submit方法 -->
      <el-button type="primary" @click="submit">保存</el-button>
      </el-form-item>
   </el-form>
</template>

<script setup>
// 导入修改用户密码的API方法
import { updateUserPwd } from "@/api/system/user";

// 获取当前组件实例的代理对象
const { proxy } = getCurrentInstance();

// 定义密码表单的响应式数据
const user = reactive({
  // 旧密码，初始为undefined
  oldPassword: undefined,
  // 新密码，初始为undefined
  newPassword: undefined,
  // 确认密码，初始为undefined
  confirmPassword: undefined
});

// 确认密码校验器，检查两次密码是否一致
const equalToPassword = (rule, value, callback) => {
  // 如果确认密码与新密码不一致
  if (user.newPassword !== value) {
    // 返回错误提示
    callback(new Error("两次输入的密码不一致"));
  } else {
    // 校验通过
    callback();
  }
};
// 定义表单校验规则
const rules = ref({
  // 旧密码校验：必填
  oldPassword: [{ required: true, message: "旧密码不能为空", trigger: "blur" }],
  // 新密码校验：必填，长度6-20
  newPassword: [{ required: true, message: "新密码不能为空", trigger: "blur" }, { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" }],
  // 确认密码校验：必填，且需与新密码一致
  confirmPassword: [{ required: true, message: "确认密码不能为空", trigger: "blur" }, { required: true, validator: equalToPassword, trigger: "blur" }]
});

/** 提交按钮 */
// 提交修改密码
function submit() {
  // 触发表单校验
  proxy.$refs.pwdRef.validate(valid => {
    // 校验通过后执行修改
    if (valid) {
      // 调用修改密码API
      updateUserPwd(user.oldPassword, user.newPassword).then(response => {
        // 显示修改成功提示
        proxy.$modal.msgSuccess("修改成功");
        // 清空旧密码输入
        user.oldPassword = undefined;
        // 清空新密码输入
        user.newPassword = undefined;
        // 清空确认密码输入
        user.confirmPassword = undefined;
      });
    }
  });
};
</script>
