<template>
  <!-- 应用根组件，渲染路由匹配的视图 -->
  <router-view />
</template>

<script setup>
// 导入设置状态管理模块
import useSettingsStore from '@/store/modules/settings'
// 导入主题样式处理方法
import { handleThemeStyle } from '@/utils/theme'

// 组件挂载时执行初始化
onMounted(() => {
  // 等待DOM更新后执行
  nextTick(() => {
    // 初始化主题样式
    // 使用设置store中的主题配置初始化主题样式
    handleThemeStyle(useSettingsStore().theme)
  })
})
</script>
