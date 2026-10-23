<template>
  <!-- 路由重定向中转页面，无可见内容，仅用于路由跳转 -->
  <div></div>
</template>

<script setup>
// 导入vue-router的路由和导航方法
import { useRoute, useRouter } from 'vue-router'

// 获取当前路由对象
const route = useRoute();
// 获取路由导航实例
const router = useRouter();
// 解构获取路由参数和查询参数
const { params, query } = route
// 从参数中提取目标路径
const { path } = params

// 立即重定向到目标路径，携带原始查询参数
router.replace({ path: '/' + path, query })
</script>
