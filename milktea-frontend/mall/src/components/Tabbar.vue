<template>
  <div class="tabbar-layout">
    <router-view />
    <van-tabbar v-model="active" route active-color="#3a9e63" inactive-color="#9ab0a3">
      <van-tabbar-item to="/menu" icon="wap-nav">点单</van-tabbar-item>
      <van-tabbar-item to="/orders" icon="orders-o">订单</van-tabbar-item>
      <van-tabbar-item to="/profile" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const active = ref(0)
</script>

<style scoped>
.tabbar-layout {
  min-height: 100vh;
}
:deep(.van-tabbar) {
  width: auto;  /* 覆盖vant的width:100%, 否则left/right内缩后右溢出 */
  height: 60px;
  left: 12px;
  right: 12px;
  bottom: 12px;
  border: none;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(20, 70, 45, 0.18);
}
:deep(.van-tabbar-item__icon) {
  font-size: 22px;
  margin-bottom: 3px;
}
:deep(.van-tabbar-item--active) {
  font-weight: 700;
}
</style>
