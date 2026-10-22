<template>
  <!-- 菜单项容器，隐藏的路由不渲染 -->
  <div v-if="!item.hidden">
    <!-- 情况1：只有一个可见子路由或没有子路由时，渲染为普通菜单项（不展开子菜单） -->
    <template v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren) && !item.alwaysShow">
      <!-- 使用AppLink包裹以支持外链和内部路由 -->
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path, onlyOneChild.query)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)" :class="{ 'submenu-title-noDropdown': !isNest }">
          <!-- 菜单图标 -->
          <svg-icon :icon-class="onlyOneChild.meta.icon || (item.meta && item.meta.icon)"/>
          <!-- 菜单标题（超过5个字符时显示title属性用于tooltip） -->
          <template #title><span class="menu-title" :title="hasTitle(onlyOneChild.meta.title)">{{ onlyOneChild.meta.title }}</span></template>
        </el-menu-item>
      </app-link>
    </template>

    <!-- 情况2：有多个可见子路由时，渲染为可展开的子菜单 -->
    <el-sub-menu v-else ref="subMenu" :index="resolvePath(item.path)" teleported>
      <!-- 父菜单标题模板 -->
      <template v-if="item.meta" #title>
        <!-- 父菜单图标 -->
        <svg-icon :icon-class="item.meta && item.meta.icon" />
        <!-- 父菜单文字 -->
        <span class="menu-title" :title="hasTitle(item.meta.title)">{{ item.meta.title }}</span>
      </template>

      <!-- 递归渲染子菜单项 -->
      <sidebar-item
        v-for="(child, index) in item.children"
        :key="child.path + index"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
// 引入外部链接判断工具
import { isExternal } from '@/utils/validate'
// 引入链接组件（处理外链/内部路由）
import AppLink from './Link'
// 引入路径拼接工具函数
import { getNormalPath } from '@/utils/ruoyi'

// 定义组件属性
const props = defineProps({
  // 路由对象（包含path/meta/children等）
  item: {
    type: Object,
    required: true
  },
  // 是否为嵌套菜单（子菜单项传true）
  isNest: {
    type: Boolean,
    default: false
  },
  // 父级路径（用于拼接完整路径）
  basePath: {
    type: String,
    default: ''
  }
})

// 存储唯一可见子路由的引用
const onlyOneChild = ref({});

// 判断是否只有一个可见的子路由
function hasOneShowingChild(children = [], parent) {
  if (!children) {
    children = [];
  }
  // 过滤出未隐藏的子路由
  const showingChildren = children.filter(item => {
    if (item.hidden) {
      return false
    } else {
      // 临时保存可见子路由（当只有一个时使用）
      onlyOneChild.value = item
      return true
    }
  })

  // 只有一个可见子路由时，直接展示该子路由
  if (showingChildren.length === 1) {
    return true
  }

  // 没有可见子路由时，展示父路由本身
  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }

  // 有多个可见子路由，需要展开子菜单
  return false
};

// 解析完整路由路径（处理外链、嵌套路径、查询参数）
function resolvePath(routePath, routeQuery) {
  // 外部链接直接返回
  if (isExternal(routePath)) {
    return routePath
  }
  // 父路径为外部链接也直接返回
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  // 有查询参数时拼接为路由对象
  if (routeQuery) {
    let query = JSON.parse(routeQuery);
    return { path: getNormalPath(props.basePath + '/' + routePath), query: query }
  }
  // 普通路径拼接并规范化
  return getNormalPath(props.basePath + '/' + routePath)
}

// 标题超过5个字符时返回标题（用于tooltip显示），否则返回空字符串
function hasTitle(title){
  if (title.length > 5) {
    return title;
  } else {
    return "";
  }
}
</script>
