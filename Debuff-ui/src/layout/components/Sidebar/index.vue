<template>
  <!-- 侧边栏容器，根据主题切换背景色 -->
  <div :class="{ 'has-logo': showLogo }" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <!-- Logo组件，根据设置决定是否显示 -->
    <logo v-if="showLogo" :collapse="isCollapse" />
    <!-- 可滚动区域包裹菜单，防止菜单过长溢出 -->
    <el-scrollbar :class="sideTheme" wrap-class="scrollbar-wrapper">
      <!-- Element Plus垂直菜单组件 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground"
        :text-color="sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
        :unique-opened="true"
        :active-text-color="theme"
        :collapse-transition="false"
        mode="vertical"
      >
        <!-- 遍历路由配置渲染侧边栏菜单项 -->
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path + index"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
// 引入Logo组件
import Logo from './Logo'
// 引入侧边栏菜单项递归组件
import SidebarItem from './SidebarItem'
// 引入SCSS变量（菜单颜色/背景色等）
import variables from '@/assets/styles/variables.module.scss'
// 引入应用store（侧边栏展开状态）
import useAppStore from '@/store/modules/app'
// 引入设置store（侧边栏主题/Logo显示）
import useSettingsStore from '@/store/modules/settings'
// 引入权限store（路由配置）
import usePermissionStore from '@/store/modules/permission'

// 获取当前路由对象
const route = useRoute();
// 获取应用store实例
const appStore = useAppStore()
// 获取设置store实例
const settingsStore = useSettingsStore()
// 获取权限store实例
const permissionStore = usePermissionStore()

// 侧边栏路由列表（从权限store获取）
const sidebarRouters =  computed(() => permissionStore.sidebarRouters);
// 是否显示侧边栏Logo
const showLogo = computed(() => settingsStore.sidebarLogo);
// 侧边栏主题（dark/light）
const sideTheme = computed(() => settingsStore.sideTheme);
// 当前主题颜色（用于菜单激活态文字颜色）
const theme = computed(() => settingsStore.theme);
// 侧边栏是否处于折叠状态
const isCollapse = computed(() => !appStore.sidebar.opened);

// 计算当前激活的菜单项路径（支持通过meta.activeMenu自定义高亮）
const activeMenu = computed(() => {
  const { meta, path } = route;
  // 如果路由meta中设置了activeMenu则使用自定义高亮路径
  if (meta.activeMenu) {
    return meta.activeMenu;
  }
  // 否则使用当前路由路径作为高亮项
  return path;
})

</script>
