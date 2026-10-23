<template>
  <!-- 内嵌iframe容器，高度根据视口动态计算 -->
  <div :style="'height:' + height">
    <!-- iframe嵌入外部页面 -->
    <iframe
      :id="iframeId"
      style="width: 100%; height: 100%"
      :src="src"
      frameborder="no"
    ></iframe>
  </div>
</template>

<script setup>
// 定义组件属性
const props = defineProps({
  // iframe嵌入的页面URL
  src: {
    type: String,
    default: "/"
  },
  // iframe元素的唯一标识
  iframeId: {
    type: String
  }
});

// 根据视口高度动态计算iframe容器高度（减去顶部导航和标签栏高度94.5px）
const height = ref(document.documentElement.clientHeight - 94.5 + "px");
</script>
