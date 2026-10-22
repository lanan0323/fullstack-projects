<template>
  <!-- 管理端主布局容器，根据侧边栏和设备类型动态切换CSS类 -->
  <div :class="classObj" class="app-wrapper" :style="{ '--current-color': theme }">
    <!-- 移动端侧边栏打开时的半透明遮罩，点击关闭侧边栏 -->
    <div v-if="device === 'mobile' && sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
    <!-- 侧边栏组件，可通过配置隐藏 -->
    <sidebar v-if="!sidebar.hide" class="sidebar-container" />
    <!-- 主内容区域，包含顶部导航栏、标签页和页面主体 -->
    <div :class="{ hasTagsView: needTagsView, sidebarHide: sidebar.hide }" class="main-container">
      <!-- 固定头部区域，包含导航栏和标签视图 -->
      <div :class="{ 'fixed-header': fixedHeader }">
        <!-- 顶部导航栏，包含面包屑和头像下拉菜单 -->
        <navbar @setLayout="setLayout" />
        <!-- 标签页导航，可通过设置开关 -->
        <tags-view v-if="needTagsView" />
      </div>
      <!-- 页面主内容区 -->
      <app-main />
      <!-- 右侧设置抽屉（主题/布局配置） -->
      <settings ref="settingRef" />
    </div>
  </div>
</template>

<script setup>
// 引入窗口尺寸监听工具，用于响应式布局判断
import { useWindowSize } from '@vueuse/core'
// 引入侧边栏组件
import Sidebar from './components/Sidebar/index.vue'
// 引入布局子组件：主内容区、导航栏、设置抽屉、标签视图
import { AppMain, Navbar, Settings, TagsView } from './components'
// 引入默认配置（从settings.js）
import defaultSettings from '@/settings'
// 引入应用状态store（侧边栏/设备状态）
import useAppStore from '@/store/modules/app'
// 引入布局设置store（主题/标签页等）
import useSettingsStore from '@/store/modules/settings'

// 获取布局设置store实例
const settingsStore = useSettingsStore()
// 当前主题颜色（响应式）
const theme = computed(() => settingsStore.theme);
// 侧边栏主题（dark/light）
const sideTheme = computed(() => settingsStore.sideTheme);
// 侧边栏状态（展开/收起/隐藏）
const sidebar = computed(() => useAppStore().sidebar);
// 当前设备类型（mobile/desktop）
const device = computed(() => useAppStore().device);
// 是否显示标签页导航
const needTagsView = computed(() => settingsStore.tagsView);
// 是否固定顶部导航栏
const fixedHeader = computed(() => settingsStore.fixedHeader);

// 动态计算布局CSS类对象
const classObj = computed(() => ({
  // 侧边栏收起时添加此类
  hideSidebar: !sidebar.value.opened,
  // 侧边栏展开时添加此类
  openSidebar: sidebar.value.opened,
  // 侧边栏无动画切换时添加此类
  withoutAnimation: sidebar.value.withoutAnimation,
  // 移动端设备时添加此类
  mobile: device.value === 'mobile'
}))

// 监听窗口尺寸变化，用于响应式断点判断
const { width, height } = useWindowSize();
// 响应式断点宽度，参考Bootstrap设计
const WIDTH = 992;

// 窗口尺寸变化时自动切换设备类型和侧边栏状态
watchEffect(() => {
  // 移动端且侧边栏打开时，自动关闭侧边栏
  if (device.value === 'mobile' && sidebar.value.opened) {
    useAppStore().closeSideBar({ withoutAnimation: false })
  }
  // 窗口宽度小于断点时切换为移动端模式并收起侧边栏
  if (width.value - 1 < WIDTH) {
    useAppStore().toggleDevice('mobile')
    useAppStore().closeSideBar({ withoutAnimation: true })
  } else {
    // 窗口宽度大于断点时切换为桌面端模式
    useAppStore().toggleDevice('desktop')
  }
})

// 点击遮罩层关闭侧边栏
function handleClickOutside() {
  useAppStore().closeSideBar({ withoutAnimation: false })
}

// 设置抽屉组件的引用
const settingRef = ref(null);
// 打开布局设置抽屉
function setLayout() {
  settingRef.value.openSetting();
}
</script>

<style lang="scss" scoped>
  @import "@/assets/styles/mixin.scss";
  @import "@/assets/styles/variables.module.scss";

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  width: calc(100% - #{$base-sidebar-width});
  transition: width 0.28s;
}

.hideSidebar .fixed-header {
  width: calc(100% - 54px);
}

.sidebarHide .fixed-header {
  width: 100%;
}

.mobile .fixed-header {
  width: 100%;
}
</style>
