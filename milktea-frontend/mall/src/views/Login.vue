<template>
  <div class="login-page">
    <div class="bubble b1"></div>
    <div class="bubble b2"></div>
    <div class="bubble b3"></div>

    <div class="logo">
      <svg viewBox="0 0 40 40" class="logo-badge" aria-hidden="true">
        <circle cx="20" cy="20" r="19" fill="rgba(255,255,255,.2)" />
        <circle cx="20" cy="20" r="15.5" fill="#fff" />
        <path d="M20.2 8.6 Q23.6 9.2 23.9 12.4 Q20.5 12 20.2 8.6 Z" fill="#4c9e50" />
        <path d="M11 16.8 H29 Q28.6 27.8 20 27.8 Q11.4 27.8 11 16.8 Z"
          fill="#e8f6ee" stroke="#3a9e63" stroke-width="1.5" stroke-linejoin="round" />
        <rect x="10.3" y="14.8" width="19.4" height="2.8" rx="1.4" fill="#3a9e63" />
        <path d="M13.6 20.8 Q16.8 19.5 20 20.8 T26.4 20.8" stroke="#8cc9ab" stroke-width="1.3" fill="none" stroke-linecap="round" />
        <path d="M15.2 23.6 Q17.6 22.5 20 23.6 T24.8 23.6" stroke="#8cc9ab" stroke-width="1.3" fill="none" stroke-linecap="round" opacity=".75" />
      </svg>
      <h1>茶千道</h1>
      <p class="brand-en">CHA QIAN DAO</p>
      <p class="slogan"><i></i>新鲜现制 · 即刻送达<i></i></p>
    </div>

    <div class="login-card">
      <van-tabs v-model:active="tabActive" shrink>
        <van-tab title="登录">
          <van-form @submit="onLogin">
            <van-cell-group inset>
              <van-field v-model="loginForm.username" label="用户名" placeholder="手机号或用户名" :rules="[{ required: true, message: '请输入用户名' }]" />
              <van-field v-model="loginForm.password" type="password" label="密码" placeholder="请输入密码" :rules="[{ required: true, message: '请输入密码' }]" />
            </van-cell-group>
            <div style="margin: 20px 16px 8px;">
              <van-button round block type="primary" native-type="submit" :loading="loading">登录</van-button>
            </div>
          </van-form>
        </van-tab>
        <van-tab title="注册">
          <van-form @submit="onRegister">
            <van-cell-group inset>
              <van-field v-model="registerForm.phone" label="手机号" placeholder="请输入手机号" :rules="[{ required: true, message: '请输入手机号' }]" />
              <van-field v-model="registerForm.password" type="password" label="密码" placeholder="请输入密码" :rules="[{ required: true, message: '请输入密码' }]" />
              <van-field v-model="registerForm.nickname" label="昵称" placeholder="请输入昵称" />
            </van-cell-group>
            <div style="margin: 20px 16px 8px;">
              <van-button round block type="primary" native-type="submit" :loading="loading">注册</van-button>
            </div>
          </van-form>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { login, register } from '../api/user'
import { setToken, setUser } from '../utils/auth'

const router = useRouter()
const tabActive = ref(0)
const loading = ref(false)

const loginForm = ref({ username: '', password: '' })
const registerForm = ref({ phone: '', password: '', nickname: '' })

const onLogin = async () => {
  loading.value = true
  try {
    const res = await login(loginForm.value)
    setToken(res.data.token)
    setUser(res.data)
    showToast('登录成功')
    router.push('/menu')
  } finally {
    loading.value = false
  }
}

const onRegister = async () => {
  loading.value = true
  try {
    await register(registerForm.value)
    showToast('注册成功，请登录')
    tabActive.value = 0
    loginForm.value.username = registerForm.value.phone
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(160deg, #7cc9a0 0%, #3a9e63 55%, #14532f 100%);
  overflow: hidden;
}
/* 漂浮气泡装饰 */
.bubble {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  pointer-events: none;
}
.bubble.b1 {
  width: 180px;
  height: 180px;
  top: -60px;
  right: -50px;
}
.bubble.b2 {
  width: 90px;
  height: 90px;
  top: 170px;
  left: -30px;
}
.bubble.b3 {
  width: 56px;
  height: 56px;
  top: 90px;
  right: 70px;
  background: rgba(255, 255, 255, 0.12);
}
.logo {
  position: relative;
  text-align: center;
  padding: 58px 0 34px;
  color: #fff;
}
/* 茶千道徽标(与点单页同款SVG) */
.logo-badge {
  width: 78px;
  height: 78px;
  filter: drop-shadow(0 12px 26px rgba(0, 0, 0, 0.2));
  animation: badge-float 3s ease-in-out infinite;
}
@keyframes badge-float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}
.logo h1 {
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 8px;
  text-indent: 8px;  /* 平衡字距造成的偏移 */
  margin: 14px 0 4px;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}
.brand-en {
  font-size: 9px;
  letter-spacing: 4.5px;
  text-indent: 4.5px;
  opacity: 0.75;
  margin-bottom: 10px;
}
.slogan {
  font-size: 13px;
  opacity: 0.92;
  letter-spacing: 2px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}
.slogan i {
  display: inline-block;
  width: 22px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.7));
}
.slogan i:last-child {
  transform: scaleX(-1);
}
/* 白色圆角卡片 */
.login-card {
  position: relative;
  margin: 0 16px;
  background: #fff;
  border-radius: 24px;
  padding: 8px 0 18px;
  box-shadow: 0 20px 48px rgba(20, 70, 45, 0.28);
}
.login-card :deep(.van-tabs__nav) {
  background: transparent;
}
.login-card :deep(.van-tab--active) {
  color: var(--brand);
  font-weight: 700;
}
.login-card :deep(.van-tabs__line) {
  width: 24px !important;  /* 覆盖vant内联宽度 */
  height: 3px;
  border-radius: 3px;
  background: var(--brand);
}
.login-card :deep(.van-cell) {
  padding-top: 14px;
  padding-bottom: 14px;
}
.login-card :deep(.van-field__label) {
  color: #5c8070;
}
.login-card :deep(.van-cell::after) {
  border-color: #dfeee6;
}
.login-card :deep(.van-field__control) {
  font-size: 14px;
}
/* 登录/注册按钮: 焦糖渐变+投影(替换vant默认蓝) */
.login-card :deep(.van-button--primary) {
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 6px;
  text-indent: 6px;
  background: linear-gradient(135deg, #7cc9a0 0%, #3a9e63 100%);
  border: none;
  box-shadow: 0 10px 22px rgba(58, 158, 99, 0.35);
}
</style>
