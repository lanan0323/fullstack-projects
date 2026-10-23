<template>
  <!-- 页面主内容区域容器 -->
  <section class="app-main">
    <!-- 路由视图，使用fade-transform过渡动画实现页面切换效果 -->
    <router-view v-slot="{ Component, route }">
      <transition name="fade-transform" mode="out-in">
        <!-- 动态组件渲染，使用fullPath作为key确保参数变化时也重新渲染 -->
        <component :is="Component" :key="route.fullPath"/>
      </transition>
    </router-view>
    <!-- iframe内嵌页面切换组件 -->
    <iframe-toggle />
  </section>
</template>

<script setup>
// 引入iframe切换组件
import iframeToggle from "./IframeToggle/index"
// 引入标签视图store（当前未使用但保留引用）
import useTagsViewStore from '@/store/modules/tagsView'

// 获取标签视图store实例
const tagsViewStore = useTagsViewStore()
</script>

<style lang="scss" scoped>
.app-main {
  /* 50= navbar  50  */
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  /* 为固定在底部的分页条留出空间 */
  padding-bottom: 60px;
}

.fixed-header + .app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    /* 84 = navbar + tags-view = 50 + 34 */
    min-height: calc(100vh - 84px);
  }

  .fixed-header + .app-main {
    padding-top: 84px;
  }
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 6px;
  }
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: #0d1220;
}

::-webkit-scrollbar-thumb {
  background-color: #2a3550;
  border-radius: 3px;
}
</style>
