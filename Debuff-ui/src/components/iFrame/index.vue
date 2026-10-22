<template>
  <!-- iframe 容器，带加载动画 -->
  <div v-loading="loading" :style="'height:' + height">
    <!-- 内嵌 iframe 页面 -->
    <iframe 
      :src="url" 
      frameborder="no" 
      style="width: 100%; height: 100%" 
      scrolling="auto" />
  </div>
</template>

<script setup>
const props = defineProps({
  // iframe 加载的 URL 地址
  src: {
    type: String,
    required: true
  }
})

// 计算 iframe 高度（视口高度减去导航栏高度）
const height = ref(document.documentElement.clientHeight - 94.5 + "px;")
// 加载状态
const loading = ref(true)
// iframe 地址
const url = computed(() => props.src)

onMounted(() => {
  // 300ms 后关闭加载动画
  setTimeout(() => {
    loading.value = false;
  }, 300);
  // 监听窗口大小变化自适应高度
  window.onresize = function temp() {
    height.value = document.documentElement.clientHeight - 94.5 + "px;";
  };
})
</script>
