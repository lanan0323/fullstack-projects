<template>
  <!-- 标签页横向滚动容器，禁用纵向滚动 -->
  <el-scrollbar
    ref="scrollContainer"
    :vertical="false"
    class="scroll-container"
    @wheel.prevent="handleScroll"
  >
    <slot />
  </el-scrollbar>
</template>

<script setup>
// 引入标签视图store
import useTagsViewStore from '@/store/modules/tagsView'

// 标签之间的间距（像素）
const tagAndTagSpacing = ref(4);
// 获取组件实例代理
const { proxy } = getCurrentInstance();

// 获取滚动容器的wrap元素引用
const scrollWrapper = computed(() => proxy.$refs.scrollContainer.$refs.wrapRef);

// 组件挂载时监听滚动事件并向父组件发射scroll事件
onMounted(() => {
  scrollWrapper.value.addEventListener('scroll', emitScroll, true)
})
// 组件卸载时移除滚动监听
onBeforeUnmount(() => {
  scrollWrapper.value.removeEventListener('scroll', emitScroll)
})

// 处理鼠标滚轮事件：将纵向滚动转换为横向滚动
function handleScroll(e) {
  const eventDelta = e.wheelDelta || -e.deltaY * 40
  const $scrollWrapper = scrollWrapper.value;
  $scrollWrapper.scrollLeft = $scrollWrapper.scrollLeft + eventDelta / 4
}
// 定义发射事件
const emits = defineEmits()
// 发射scroll事件通知父组件（用于关闭右键菜单）
const emitScroll = () => {
  emits('scroll')
}

// 获取标签视图store实例
const tagsViewStore = useTagsViewStore()
// 已访问的视图列表
const visitedViews = computed(() => tagsViewStore.visitedViews);

// 滚动到目标标签位置（确保标签在可视区域内）
function moveToTarget(currentTag) {
  const $container = proxy.$refs.scrollContainer.$el
  const $containerWidth = $container.offsetWidth
  const $scrollWrapper = scrollWrapper.value;

  let firstTag = null
  let lastTag = null

  // 获取第一个和最后一个标签
  if (visitedViews.value.length > 0) {
    firstTag = visitedViews.value[0]
    lastTag = visitedViews.value[visitedViews.value.length - 1]
  }

  // 如果是第一个标签，滚动到最左边
  if (firstTag === currentTag) {
    $scrollWrapper.scrollLeft = 0
  // 如果是最后一个标签，滚动到最右边
  } else if (lastTag === currentTag) {
    $scrollWrapper.scrollLeft = $scrollWrapper.scrollWidth - $containerWidth
  } else {
    // 中间标签：确保标签在可视区域内显示
    const tagListDom = document.getElementsByClassName('tags-view-item');
    const currentIndex = visitedViews.value.findIndex(item => item === currentTag)
    let prevTag = null
    let nextTag = null
    // 查找前后标签的DOM元素
    for (const k in tagListDom) {
      if (k !== 'length' && Object.hasOwnProperty.call(tagListDom, k)) {
        if (tagListDom[k].dataset.path === visitedViews.value[currentIndex - 1].path) {
          prevTag = tagListDom[k];
        }
        if (tagListDom[k].dataset.path === visitedViews.value[currentIndex + 1].path) {
          nextTag = tagListDom[k];
        }
      }
    }

    // 计算下一个标签右侧的偏移量
    const afterNextTagOffsetLeft = nextTag.offsetLeft + nextTag.offsetWidth + tagAndTagSpacing.value

    // 计算上一个标签左侧的偏移量
    const beforePrevTagOffsetLeft = prevTag.offsetLeft - tagAndTagSpacing.value
    // 如果标签超出右侧可视区域，向右滚动
    if (afterNextTagOffsetLeft > $scrollWrapper.scrollLeft + $containerWidth) {
      $scrollWrapper.scrollLeft = afterNextTagOffsetLeft - $containerWidth
    // 如果标签超出左侧可视区域，向左滚动
    } else if (beforePrevTagOffsetLeft < $scrollWrapper.scrollLeft) {
      $scrollWrapper.scrollLeft = beforePrevTagOffsetLeft
    }
  }
}

// 暴露moveToTarget方法给父组件调用
defineExpose({
  moveToTarget,
})
</script>

<style lang='scss' scoped>
.scroll-container {
  white-space: nowrap;
  position: relative;
  overflow: hidden;
  width: 100%;
  :deep(.el-scrollbar__bar) {
    bottom: 0px;
  }
  :deep(.el-scrollbar__wrap) {
    height: 39px;
  }
}
</style>
