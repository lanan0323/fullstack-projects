<template>
  <router-view />
  <AiAssistant />
</template>

<script setup>
import AiAssistant from './components/AiAssistant.vue'
</script>

<style>
:root {
  /* ===== 品牌主题(清新绿) ===== */
  --brand: #3a9e63;
  --brand-deep: #1f6b45;
  --brand-light: #e8f6ee;
  --brand-grad: linear-gradient(135deg, #7cc9a0 0%, #3a9e63 100%);
  --price: #e25822;
  --page-bg: #f5faf7;

  /* Vant 主题变量统一换色 */
  --van-primary-color: var(--brand);
  --van-tabs-bottom-bar-color: var(--brand);
  --van-nav-bar-icon-color: #333;
  --van-nav-bar-text-color: #333;
}
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  -webkit-tap-highlight-color: transparent;
}
body {
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', 'Segoe UI', Roboto, sans-serif;
  background: var(--page-bg);
  color: #2b2320;
  -webkit-font-smoothing: antialiased;
}
/* 页面默认暖灰底 */
.van-nav-bar,
.van-cell-group,
.van-card {
  background: #fff;
}
</style>
