<template>
  <!-- 注册页面根容器 -->
  <div class="register">
    <!-- 注册表单，绑定表单数据、校验规则和引用 -->
    <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
      <!-- 平台标题 -->
      <h3 class="title">Debuff 饰品交易平台</h3>
      <!-- 用户名表单项，绑定username字段 -->
      <el-form-item prop="username">
        <!-- 用户名输入框 -->
        <el-input 
          v-model="registerForm.username" 
          type="text" 
          size="large" 
          auto-complete="off" 
          placeholder="账号"
        >
          <!-- 用户名输入框前缀图标 -->
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <!-- 密码表单项，绑定password字段 -->
      <el-form-item prop="password">
        <!-- 密码输入框，支持回车触发注册 -->
        <el-input
          v-model="registerForm.password"
          type="password"
          size="large" 
          auto-complete="off"
          placeholder="密码"
          @keyup.enter="handleRegister"
        >
          <!-- 密码输入框前缀图标 -->
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <!-- 确认密码表单项，绑定confirmPassword字段 -->
      <el-form-item prop="confirmPassword">
        <!-- 确认密码输入框，支持回车触发注册 -->
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          size="large" 
          auto-complete="off"
          placeholder="确认密码"
          @keyup.enter="handleRegister"
        >
          <!-- 确认密码输入框前缀图标 -->
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <!-- 验证码表单项，根据captchaEnabled控制是否显示 -->
      <el-form-item prop="code" v-if="captchaEnabled">
        <!-- 验证码输入框，宽度占63% -->
        <el-input
          size="large" 
          v-model="registerForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter="handleRegister"
        >
          <!-- 验证码输入框前缀图标 -->
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <!-- 验证码图片容器 -->
        <div class="register-code">
          <!-- 验证码图片，点击刷新验证码 -->
          <img :src="codeUrl" @click="getCode" class="register-code-img"/>
        </div>
      </el-form-item>
      <!-- 注册按钮区域 -->
      <el-form-item style="width:100%;">
        <!-- 注册按钮，loading状态由loading变量控制 -->
        <el-button
          :loading="loading"
          size="large" 
          type="primary"
          style="width:100%;"
          @click.prevent="handleRegister"
        >
          <!-- 非loading状态显示"注 册" -->
          <span v-if="!loading">注 册</span>
          <!-- loading状态显示"注 册 中..." -->
          <span v-else>注 册 中...</span>
        </el-button>
        <!-- 跳转登录链接容器 -->
        <div style="float: right;">
          <!-- 跳转到登录页面的链接 -->
          <router-link class="link-type" :to="'/login'">使用已有账户登录</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <!-- 底部版权信息区域 -->
    <div class="el-register-footer">
      <!-- 版权声明文字 -->
      <span>Copyright © 2026 Debuff All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup>
// 导入Element Plus消息弹框组件
import { ElMessageBox } from "element-plus";
// 导入获取验证码图片和注册API方法
import { getCodeImg, register } from "@/api/login";

// 获取路由实例，用于页面跳转
const router = useRouter();
// 获取当前组件实例的代理对象
const { proxy } = getCurrentInstance();

// 定义注册表单的响应式数据
const registerForm = ref({
  // 用户名，默认为空
  username: "",
  // 密码，默认为空
  password: "",
  // 确认密码，默认为空
  confirmPassword: "",
  // 验证码，默认为空
  code: "",
  // 验证码唯一标识，默认为空
  uuid: ""
});

// 确认密码校验器，检查两次密码是否一致
const equalToPassword = (rule, value, callback) => {
  // 如果确认密码与密码不一致
  if (registerForm.value.password !== value) {
    // 返回错误提示
    callback(new Error("两次输入的密码不一致"));
  } else {
    // 校验通过
    callback();
  }
};

// 定义注册表单的校验规则
const registerRules = {
  // 用户名校验：必填，长度2-20
  username: [
    { required: true, trigger: "blur", message: "请输入您的账号" },
    { min: 2, max: 20, message: "用户账号长度必须介于 2 和 20 之间", trigger: "blur" }
  ],
  // 密码校验：必填，长度5-20
  password: [
    { required: true, trigger: "blur", message: "请输入您的密码" },
    { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" }
  ],
  // 确认密码校验：必填，且需与密码一致
  confirmPassword: [
    { required: true, trigger: "blur", message: "请再次输入您的密码" },
    { required: true, validator: equalToPassword, trigger: "blur" }
  ],
  // 验证码校验：必填
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
};

// 验证码图片的URL地址
const codeUrl = ref("");
// 注册按钮的加载状态
const loading = ref(false);
// 验证码开关状态
const captchaEnabled = ref(true);

// 处理注册逻辑
function handleRegister() {
  // 触发表单校验
  proxy.$refs.registerRef.validate(valid => {
    // 校验通过后执行注册
    if (valid) {
      // 设置loading状态为true
      loading.value = true;
      // 调用注册API
      register(registerForm.value).then(res => {
        // 获取注册的用户名
        const username = registerForm.value.username;
        // 弹出注册成功提示框
        ElMessageBox.alert("<font color='red'>恭喜你，您的账号 " + username + " 注册成功！</font>", "系统提示", {
          // 允许使用HTML字符串
          dangerouslyUseHTMLString: true,
          // 提示类型为成功
          type: "success",
        }).then(() => {
          // 注册成功后跳转到登录页面
          router.push("/login");
        }).catch(() => {});
      }).catch(() => {
        // 注册失败，关闭loading状态
        loading.value = false;
        // 如果验证码开启则刷新验证码
        if (captchaEnabled) {
          // 调用获取验证码方法
          getCode();
        }
      });
    }
  });
}

// 获取验证码图片
function getCode() {
  // 调用获取验证码图片API
  getCodeImg().then(res => {
    // 根据接口返回值设置验证码开关状态
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled;
    // 如果验证码功能开启
    if (captchaEnabled.value) {
      // 拼接base64图片数据设置验证码图片URL
      codeUrl.value = "data:image/gif;base64," + res.img;
      // 保存验证码唯一标识用于后续校验
      registerForm.value.uuid = res.uuid;
    }
  });
}

// 页面初始化时获取验证码
getCode();
</script>

<style lang='scss' scoped>
/* 注册页面根容器样式 */
.register {
  /* 使用弹性布局居中内容 */
  display: flex;
  /* 水平居中 */
  justify-content: center;
  /* 垂直居中 */
  align-items: center;
  /* 占满整个高度 */
  height: 100%;
  /* 设置背景渐变叠加背景图片 */
  background-image: linear-gradient(rgba(10, 14, 24, 0.72), rgba(10, 14, 24, 0.85)), url("../assets/images/login-background.jpg");
  /* 背景图片覆盖整个容器 */
  background-size: cover;
}
/* 平台标题样式 */
.title {
  /* 外边距：左右自动居中，底部30px */
  margin: 0px auto 30px auto;
  /* 文字居中 */
  text-align: center;
  /* 文字颜色为浅灰白 */
  color: #e9edf6;
  /* 字体加粗 */
  font-weight: 700;
}

/* 注册表单容器样式 */
.register-form {
  /* 圆角12px */
  border-radius: 12px;
  /* 深色背景 */
  background: #141b2d;
  /* 深色边框 */
  border: 1px solid #232e45;
  /* 添加阴影效果 */
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.5);
  /* 固定宽度400px */
  width: 400px;
  /* 内边距 */
  padding: 25px 25px 5px 25px;
  /* 表单内输入框样式 */
  .el-input {
    /* 输入框高度40px */
    height: 40px;
    /* 内部input元素高度 */
    input {
      height: 40px;
    }
  }
  /* 输入框前缀图标样式 */
  .input-icon {
    /* 图标高度 */
    height: 39px;
    /* 图标宽度 */
    width: 14px;
    /* 左侧无间距 */
    margin-left: 0px;
  }
}
/* 注册提示文字样式 */
.register-tip {
  /* 字体大小13px */
  font-size: 13px;
  /* 文字居中 */
  text-align: center;
  /* 灰色文字 */
  color: #bfbfbf;
}
/* 验证码图片区域样式 */
.register-code {
  /* 宽度占33% */
  width: 33%;
  /* 高度40px */
  height: 40px;
  /* 右浮动 */
  float: right;
  /* 验证码图片样式 */
  img {
    /* 鼠标悬浮时显示手型 */
    cursor: pointer;
    /* 垂直居中对齐 */
    vertical-align: middle;
  }
}
/* 底部版权信息样式 */
.el-register-footer {
  /* 高度40px */
  height: 40px;
  /* 行高40px实现垂直居中 */
  line-height: 40px;
  /* 固定在页面底部 */
  position: fixed;
  /* 距底部0 */
  bottom: 0;
  /* 占满整个宽度 */
  width: 100%;
  /* 文字居中 */
  text-align: center;
  /* 白色文字 */
  color: #fff;
  /* 使用Arial字体 */
  font-family: Arial;
  /* 字体大小12px */
  font-size: 12px;
  /* 字间距1px */
  letter-spacing: 1px;
}
/* 验证码图片样式 */
.register-code-img {
  /* 高度40px */
  height: 40px;
  /* 左内边距12px */
  padding-left: 12px;
}
</style>
