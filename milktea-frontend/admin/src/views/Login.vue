<template>
  <div class="login-page">
    <!-- 背景装饰: 漂浮茶叶与气泡 -->
    <svg class="bg-leaf l1" viewBox="0 0 24 24"><path d="M12 2C6 8 4 14 12 22C20 14 18 8 12 2Z" fill="rgba(255,255,255,.10)"/></svg>
    <svg class="bg-leaf l2" viewBox="0 0 24 24"><path d="M12 2C6 8 4 14 12 22C20 14 18 8 12 2Z" fill="rgba(255,255,255,.08)"/></svg>
    <div class="bg-bubble b1"></div>
    <div class="bg-bubble b2"></div>
    <div class="bg-bubble b3"></div>

    <div class="login-card">
      <!-- 品牌logo: 与用户端完全同款 -->
      <div class="brand">
        <svg viewBox="0 0 40 40" class="brand-logo" aria-hidden="true">
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
        <span class="brand-en">CHA QIAN DAO · 管理系统</span>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="账号" prop="account">
          <el-input v-model="form.account" placeholder="手机号 / 用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">登 录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/user'
import { setToken, setUser } from '../utils/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({ account: '', password: '' })
const rules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  formRef.value.validate(async valid => {
    if (!valid) return
    loading.value = true
    try {
      const { data } = await login({ username: form.account, phone: form.account, password: form.password })
      setToken(data.token)
      setUser(data)
      ElMessage.success('登录成功')
      // userType: 0顾客 → 顾客端, 1员工 → 管理端
      router.push(data.userType === 1 ? '/admin/dashboard' : '/client/menu')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #7cc9a0, #3a9e63 55%, #1f6b45);
  position: relative;
  overflow: hidden;
}

/* 漂浮茶叶 */
.bg-leaf { position: absolute; opacity: 1; animation: leafFloat 7s ease-in-out infinite; }
.l1 { width: 180px; top: -40px; right: 8%; transform: rotate(35deg); }
.l2 { width: 130px; bottom: -20px; left: 10%; transform: rotate(-140deg); animation-delay: -3.5s; }
@keyframes leafFloat {
  0%, 100% { translate: 0 0; }
  50% { translate: 0 18px; }
}

/* 漂浮气泡 */
.bg-bubble { position: absolute; border-radius: 50%; background: rgba(255,255,255,.08); }
.b1 { width: 260px; height: 260px; top: 12%; left: -80px; }
.b2 { width: 160px; height: 160px; bottom: 16%; right: -40px; }
.b3 { width: 90px; height: 90px; top: 60%; left: 22%; animation: bubbleUp 9s ease-in-out infinite; }
@keyframes bubbleUp {
  0%, 100% { translate: 0 0; }
  50% { translate: 0 -26px; }
}

.login-card {
  width: 400px;
  background: #fff;
  border-radius: 20px;
  padding: 38px 34px 32px;
  box-shadow: 0 20px 60px rgba(20, 83, 47, .35);
  position: relative;
  z-index: 1;
}

.brand {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 10px;
  margin-bottom: 26px;
}
.brand-logo { width: 78px; height: 78px; filter: drop-shadow(0 4px 12px rgba(58,158,99,.30)); animation: bob 3s ease-in-out infinite; }
@keyframes bob {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}
.brand h1 {
  margin: 0;
  font-size: 24px;
  letter-spacing: 10px;
  text-indent: 10px;
  color: #1f6b45;
}
.brand-en { margin: 0; font-size: 11px; color: #9bb8a8; letter-spacing: 2px; }

.login-btn { width: 100%; margin-top: 8px; height: 42px; letter-spacing: 6px; text-indent: 6px; }
.login-page :deep(.el-button--primary) {
  background: linear-gradient(135deg, #5cb88b, #3a9e63);
  border: none;
  box-shadow: 0 6px 16px rgba(58, 158, 99, .35);
}
.login-page :deep(.el-button--primary:hover) { background: linear-gradient(135deg, #4dab7d, #2f8f57); }
.login-page :deep(.el-input__wrapper) { border-radius: 10px; }
</style>
