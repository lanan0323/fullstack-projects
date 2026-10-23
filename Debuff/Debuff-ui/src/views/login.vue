<template>
  <!-- 登录页面根容器 -->
  <div class="login">
    <!-- 登录表单，绑定表单数据、校验规则和引用 -->
    <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
      <!-- 平台标题 -->
      <h3 class="title">Debuff 饰品交易平台</h3>
      <!-- 用户名表单项，绑定username字段 -->
      <el-form-item prop="username">
        <!-- 用户名输入框，绑定loginForm.username -->
        <el-input
          v-model="loginForm.username"
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
        <!-- 密码输入框，支持回车触发登录 -->
        <el-input
          v-model="loginForm.password"
          type="password"
          size="large"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter="handleLogin"
        >
          <!-- 密码输入框前缀图标 -->
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <!-- 验证码表单项，根据captchaEnabled控制是否显示 -->
      <el-form-item prop="code" v-if="captchaEnabled">
        <!-- 验证码输入框，宽度占63% -->
        <el-input
          v-model="loginForm.code"
          size="large"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter="handleLogin"
        >
          <!-- 验证码输入框前缀图标 -->
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <!-- 验证码图片容器 -->
        <div class="login-code">
          <!-- 验证码图片，点击刷新验证码 -->
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
        </div>
      </el-form-item>
      <!-- 记住密码复选框 -->
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
      <!-- 登录按钮区域 -->
      <el-form-item style="width:100%;">
        <!-- 登录按钮，loading状态由loading变量控制 -->
        <el-button
          :loading="loading"
          size="large"
          type="primary"
          style="width:100%;"
          @click.prevent="handleLogin"
        >
          <!-- 非loading状态显示"登 录" -->
          <span v-if="!loading">登 录</span>
          <!-- loading状态显示"登 录 中..." -->
          <span v-else>登 录 中...</span>
        </el-button>
        <!-- 注册链接容器，register为true时显示 -->
        <div style="float: right;" v-if="register">
          <!-- 跳转到注册页面的链接 -->
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <!-- 底部版权信息区域 -->
    <div class="el-login-footer">
      <!-- 版权声明文字 -->
      <span>Copyright © 2026 Debuff All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup>
// 导入获取验证码图片的API方法
import { getCodeImg } from "@/api/login";
// 导入js-cookie库，用于读写浏览器Cookie
import Cookies from "js-cookie";
// 导入加密和解密工具方法
import { encrypt, decrypt } from "@/utils/jsencrypt";
// 导入用户状态管理模块
import useUserStore from '@/store/modules/user'

// 获取用户状态管理实例
const userStore = useUserStore()
// 获取当前路由对象
const route = useRoute();
// 获取路由实例，用于页面跳转
const router = useRouter();
// 获取当前组件实例的代理对象
const { proxy } = getCurrentInstance();

// 定义登录表单的响应式数据
const loginForm = ref({
  // 默认用户名
  username: "admin",
  // 默认密码
  password: "admin123",
  // 是否记住密码，默认不记住
  rememberMe: false,
  // 验证码字段，默认为空
  code: "",
  // 验证码唯一标识，默认为空
  uuid: ""
});

// 定义登录表单的校验规则
const loginRules = {
  // 用户名校验：必填，失焦时触发
  username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
  // 密码校验：必填，失焦时触发
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
  // 验证码校验：必填，值变化时触发
  code: [{ required: true, trigger: "change", message: "请输入验证码" }]
};

// 验证码图片的URL地址
const codeUrl = ref("");
// 登录按钮的加载状态
const loading = ref(false);
// 验证码开关
const captchaEnabled = ref(true);
// 注册开关
const register = ref(true);
// 登录成功后重定向的地址
const redirect = ref(undefined);

// 监听路由变化，获取重定向地址
watch(route, (newRoute) => {
    // 从路由查询参数中提取redirect字段
    redirect.value = newRoute.query && newRoute.query.redirect;
}, { immediate: true });

// 处理登录逻辑
function handleLogin() {
  // 触发表单校验
  proxy.$refs.loginRef.validate(valid => {
    // 校验通过后执行登录
    if (valid) {
      // 设置loading状态为true，显示加载中
      loading.value = true;
      // 勾选了需要记住密码设置在 cookie 中设置记住用户名和密码
      if (loginForm.value.rememberMe) {
        // 将用户名存入Cookie，有效期30天
        Cookies.set("username", loginForm.value.username, { expires: 30 });
        // 将加密后的密码存入Cookie，有效期30天
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 });
        // 将记住密码标记存入Cookie，有效期30天
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 });
      } else {
        // 否则移除
        // 移除Cookie中的用户名
        Cookies.remove("username");
        // 移除Cookie中的密码
        Cookies.remove("password");
        // 移除Cookie中的记住密码标记
        Cookies.remove("rememberMe");
      }
      // 调用action的登录方法
      userStore.login(loginForm.value).then(() => {
        // 获取当前路由的查询参数
        const query = route.query;
        // 过滤掉redirect参数，保留其他查询参数
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          // 跳过redirect参数
          if (cur !== "redirect") {
            // 将其他参数存入累加器
            acc[cur] = query[cur];
          }
          // 返回累加结果
          return acc;
        }, {});
        // 登录成功后跳转到目标页面或首页
        router.push({ path: redirect.value || "/", query: otherQueryParams });
      }).catch(() => {
        // 登录失败，关闭loading状态
        loading.value = false;
        // 重新获取验证码
        if (captchaEnabled.value) {
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
      loginForm.value.uuid = res.uuid;
    }
  });
}

// 从Cookie中读取记住的登录信息
function getCookie() {
  // 从Cookie中读取用户名
  const username = Cookies.get("username");
  // 从Cookie中读取加密密码
  const password = Cookies.get("password");
  // 从Cookie中读取记住密码标记
  const rememberMe = Cookies.get("rememberMe");
  // 用Cookie中的值回填登录表单
  loginForm.value = {
    // 如果Cookie中没有用户名则保留默认值
    username: username === undefined ? loginForm.value.username : username,
    // 如果Cookie中没有密码则保留默认值，否则解密
    password: password === undefined ? loginForm.value.password : decrypt(password),
    // 如果Cookie中没有记住标记则设为false，否则转为布尔值
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
  };
}

// 页面初始化时获取验证码
getCode();
// 页面初始化时读取Cookie中的登录信息
getCookie();
</script>

<style lang='scss' scoped>
/* 登录页面根容器样式 */
.login {
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

/* 登录表单容器样式 */
.login-form {
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
/* 登录提示文字样式 */
.login-tip {
  /* 字体大小13px */
  font-size: 13px;
  /* 文字居中 */
  text-align: center;
  /* 灰色文字 */
  color: #bfbfbf;
}
/* 验证码图片区域样式 */
.login-code {
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
.el-login-footer {
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
.login-code-img {
  /* 高度40px */
  height: 40px;
  /* 左内边距12px */
  padding-left: 12px;
}
</style>
