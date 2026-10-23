<template>
  <!-- 侧边栏Logo容器，根据主题切换背景色 -->
  <div class="sidebar-logo-container" :class="{ 'collapse': collapse }" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <!-- Logo区域带淡入淡出过渡动画 -->
    <transition name="sidebarLogoFade">
      <!-- 折叠状态下的Logo（仅显示图标） -->
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img :src="logo" class="sidebar-logo" />
      </router-link>
      <!-- 展开状态下的Logo -->
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img :src="logo" class="sidebar-logo" />
      </router-link>
    </transition>
  </div>
</template>

<script setup>
// 引入SCSS变量（菜单背景色等）
import variables from '@/assets/styles/variables.module.scss'
// 引入Logo图片资源
import logo from '@/assets/logo/logo.png'
// 引入设置store（获取侧边栏主题）
import useSettingsStore from '@/store/modules/settings'

// 接收折叠状态属性
defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

// 获取设置store实例
const settingsStore = useSettingsStore();
// 计算侧边栏主题（dark/light）
const sideTheme = computed(() => settingsStore.sideTheme);
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  background: #2b2f3a;
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      width: 110px;
      height: 32px;
      object-fit: contain;
      vertical-align: middle;
      margin-right: 0;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }
}
</style>
