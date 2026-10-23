<template>
  <!-- 链接组件：根据链接类型动态渲染为a标签或router-link -->
  <component :is="type" v-bind="linkProps()">
    <slot />
  </component>
</template>

<script setup>
// 引入外部链接判断工具函数
import { isExternal } from '@/utils/validate'

// 接收目标路径参数（可以是字符串路径或路由对象）
const props = defineProps({
  to: {
    type: [String, Object],
    required: true
  }
})

// 计算目标是否为外部链接（http/https开头）
const isExt = computed(() => {
  return isExternal(props.to)
})

// 根据是否外部链接决定渲染a标签还是router-link
const type = computed(() => {
  if (isExt.value) {
    return 'a'
  }
  return 'router-link'
})

// 根据链接类型返回对应的属性对象
function linkProps() {
  // 外部链接：使用href+新窗口打开+安全属性
  if (isExt.value) {
    return {
      href: props.to,
      target: '_blank',
      rel: 'noopener'
    }
  }
  // 内部链接：使用router-link的to属性
  return {
    to: props.to
  }
}
</script>
