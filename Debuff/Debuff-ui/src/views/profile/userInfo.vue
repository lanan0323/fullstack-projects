<template>
   <!-- 用户信息编辑表单，绑定表单数据、校验规则和引用 -->
   <el-form ref="userRef" :model="form" :rules="rules" label-width="80px">
      <!-- 用户昵称输入项 -->
      <el-form-item label="用户昵称" prop="nickName">
         <!-- 用户昵称输入框，最大长度30 -->
         <el-input v-model="form.nickName" maxlength="30" />
      </el-form-item>
      <!-- 手机号码输入项 -->
      <el-form-item label="手机号码" prop="phonenumber">
         <!-- 手机号码输入框，最大长度11 -->
         <el-input v-model="form.phonenumber" maxlength="11" />
      </el-form-item>
      <!-- 邮箱输入项 -->
      <el-form-item label="邮箱" prop="email">
         <!-- 邮箱输入框，最大长度50 -->
         <el-input v-model="form.email" maxlength="50" />
      </el-form-item>
      <!-- 性别选择项 -->
      <el-form-item label="性别">
         <!-- 性别单选框组，绑定form.sex -->
         <el-radio-group v-model="form.sex">
            <!-- 男选项，值为'0' -->
            <el-radio :label="'0'">男</el-radio>
            <!-- 女选项，值为'1' -->
            <el-radio :label="'1'">女</el-radio>
         </el-radio-group>
      </el-form-item>
      <!-- 操作按钮区域 -->
      <el-form-item>
      <!-- 保存按钮，点击触发submit方法 -->
      <el-button type="primary" @click="submit">保存</el-button>
      </el-form-item>
   </el-form>
</template>

<script setup>
// 导入修改用户资料的API方法
import { updateUserProfile } from "@/api/system/user";
// 导入用户状态管理模块
import useUserStore from "@/store/modules/user";

// 定义组件接收的props属性
const props = defineProps({
  // 用户信息对象prop
  user: {
    // 类型为Object
    type: Object
  }
});

// 获取当前组件实例的代理对象
const { proxy } = getCurrentInstance();
// 获取用户状态管理实例
const userStore = useUserStore();

// 定义表单响应式数据
const form = ref({});
// 定义表单校验规则
const rules = ref({
  // 用户昵称校验：必填
  nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
  // 邮箱校验：格式验证
  email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],
  // 手机号码校验：正则匹配手机号格式
  phonenumber: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }],
});

/** 提交按钮 */
// 提交修改用户资料
function submit() {
  // 触发表单校验
  proxy.$refs.userRef.validate(valid => {
    // 校验通过后执行修改
    if (valid) {
      // 调用修改用户资料API
      updateUserProfile(form.value).then(response => {
        // 显示修改成功提示
        proxy.$modal.msgSuccess("修改成功");
        // 同步手机号到父组件的user对象
        props.user.phonenumber = form.value.phonenumber;
        // 同步邮箱到父组件的user对象
        props.user.email = form.value.email;
        // 同步昵称到父组件的user对象
        props.user.nickName = form.value.nickName;
        // 重新获取用户信息更新全局状态
        userStore.getInfo();
      });
    }
  });
};

// 回显当前登录用户信息
// 监听props.user变化，回显用户信息到表单
watch(() => props.user, user => {
  // 如果user对象存在
  if (user) {
    // 将用户信息填充到表单中
    form.value = { nickName: user.nickName, phonenumber: user.phonenumber, email: user.email, sex: user.sex };
  }
},{ immediate: true });
</script>
