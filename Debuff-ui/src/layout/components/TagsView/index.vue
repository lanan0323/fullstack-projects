<template>
  <!-- 标签页导航容器 -->
  <div id="tags-view-container" class="tags-view-container">
    <!-- 可滚动面板，包裹标签页列表支持横向滚动 -->
    <scroll-pane ref="scrollPaneRef" class="tags-view-wrapper" @scroll="handleScroll">
      <!-- 遍历已访问的视图渲染标签页 -->
      <router-link
        v-for="tag in visitedViews"
        :key="tag.path"
        :data-path="tag.path"
        :class="isActive(tag) ? 'active' : ''"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        class="tags-view-item"
        :style="activeStyle(tag)"
        @click.middle="!isAffix(tag) ? closeSelectedTag(tag) : ''"
        @contextmenu.prevent="openMenu(tag, $event)"
      >
        <!-- 标签页标题 -->
        {{ tag.title }}
        <!-- 非固定标签显示关闭按钮 -->
        <span v-if="!isAffix(tag)" @click.prevent.stop="closeSelectedTag(tag)">
          <close class="el-icon-close" style="width: 1em; height: 1em;vertical-align: middle;" />
        </span>
      </router-link>
    </scroll-pane>
    <!-- 右键菜单（动态定位显示） -->
    <ul v-show="visible" :style="{ left: left + 'px', top: top + 'px' }" class="contextmenu">
      <!-- 刷新当前标签页 -->
      <li @click="refreshSelectedTag(selectedTag)">
        <refresh-right style="width: 1em; height: 1em;" /> 刷新页面
      </li>
      <!-- 关闭当前标签页（固定标签不可关闭） -->
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">
        <close style="width: 1em; height: 1em;" /> 关闭当前
      </li>
      <!-- 关闭其他标签页 -->
      <li @click="closeOthersTags">
        <circle-close style="width: 1em; height: 1em;" /> 关闭其他
      </li>
      <!-- 关闭左侧标签页（非第一个标签时可用） -->
      <li v-if="!isFirstView()" @click="closeLeftTags">
        <back style="width: 1em; height: 1em;" /> 关闭左侧
      </li>
      <!-- 关闭右侧标签页（非最后一个标签时可用） -->
      <li v-if="!isLastView()" @click="closeRightTags">
        <right style="width: 1em; height: 1em;" /> 关闭右侧
      </li>
      <!-- 关闭所有标签页 -->
      <li @click="closeAllTags(selectedTag)">
        <circle-close style="width: 1em; height: 1em;" /> 全部关闭
      </li>
    </ul>
  </div>
</template>

<script setup>
// 引入横向滚动面板组件
import ScrollPane from './ScrollPane'
// 引入路径规范化工具
import { getNormalPath } from '@/utils/ruoyi'
// 引入标签视图store
import useTagsViewStore from '@/store/modules/tagsView'
// 引入布局设置store（获取主题颜色）
import useSettingsStore from '@/store/modules/settings'
// 引入权限store（获取路由配置用于初始化固定标签）
import usePermissionStore from '@/store/modules/permission'

// 右键菜单是否可见
const visible = ref(false);
// 右键菜单定位：距顶部距离
const top = ref(0);
// 右键菜单定位：距左侧距离
const left = ref(0);
// 当前右键选中的标签
const selectedTag = ref({});
// 固定标签列表（meta.affix为true的路由）
const affixTags = ref([]);
// 滚动面板组件引用
const scrollPaneRef = ref(null);

// 获取组件实例代理（用于调用$tab全局方法）
const { proxy } = getCurrentInstance();
// 获取当前路由对象
const route = useRoute();
// 获取路由实例
const router = useRouter();

// 已访问的视图列表（从store获取）
const visitedViews = computed(() => useTagsViewStore().visitedViews);
// 路由配置列表（用于初始化固定标签）
const routes = computed(() => usePermissionStore().routes);
// 当前主题颜色（用于激活态标签样式）
const theme = computed(() => useSettingsStore().theme);

// 路由变化时添加新标签并滚动到当前标签
watch(route, () => {
  addTags()
  moveToCurrentTag()
})
// 右键菜单显示时监听全局点击关闭，隐藏时移除监听
watch(visible, (value) => {
  if (value) {
    document.body.addEventListener('click', closeMenu)
  } else {
    document.body.removeEventListener('click', closeMenu)
  }
})
// 组件挂载时初始化固定标签并添加当前路由标签
onMounted(() => {
  initTags()
  addTags()
})

// 判断标签是否为当前激活的路由
function isActive(r) {
  return r.path === route.path
}
// 激活态标签的背景色和边框色跟随主题颜色
function activeStyle(tag) {
  if (!isActive(tag)) return {};
  return {
    "background-color": theme.value,
    "border-color": theme.value
  };
}
// 判断标签是否为固定标签（不可关闭）
function isAffix(tag) {
  return tag.meta && tag.meta.affix
}
// 判断当前选中标签是否为第一个视图
function isFirstView() {
  try {
    return selectedTag.value.fullPath === '/index' || selectedTag.value.fullPath === visitedViews.value[1].fullPath
  } catch (err) {
    return false
  }
}
// 判断当前选中标签是否为最后一个视图
function isLastView() {
  try {
    return selectedTag.value.fullPath === visitedViews.value[visitedViews.value.length - 1].fullPath
  } catch (err) {
    return false
  }
}
// 递归过滤出所有固定标签（meta.affix为true的路由）
function filterAffixTags(routes, basePath = '') {
  let tags = []
  routes.forEach(route => {
    if (route.meta && route.meta.affix) {
      const tagPath = getNormalPath(basePath + '/' + route.path)
      tags.push({
        fullPath: tagPath,
        path: tagPath,
        name: route.name,
        meta: { ...route.meta }
      })
    }
    if (route.children) {
      const tempTags = filterAffixTags(route.children, route.path)
      if (tempTags.length >= 1) {
        tags = [...tags, ...tempTags]
      }
    }
  })
  return tags
}
// 初始化固定标签：将affix路由加入已访问视图
function initTags() {
  const res = filterAffixTags(routes.value);
  affixTags.value = res;
  for (const tag of res) {
    // 必须有name属性才加入标签视图
    if (tag.name) {
       useTagsViewStore().addVisitedView(tag)
    }
  }
}
// 将当前路由添加到标签视图（含iframe视图）
function addTags() {
  const { name } = route
  if (name) {
    useTagsViewStore().addView(route)
    // 如果路由有link属性（iframe嵌入页面），同时添加iframe视图
    if (route.meta.link) {
      useTagsViewStore().addIframeView(route);
    }
  }
  return false
}
// 滚动到当前激活的标签位置
function moveToCurrentTag() {
  nextTick(() => {
    for (const r of visitedViews.value) {
      if (r.path === route.path) {
        scrollPaneRef.value.moveToTarget(r);
        // 当query参数变化时更新标签视图
        if (r.fullPath !== route.fullPath) {
          useTagsViewStore().updateVisitedView(route)
        }
      }
    }
  })
}
// 刷新选中的标签页
function refreshSelectedTag(view) {
  proxy.$tab.refreshPage(view);
  // 如果是iframe页面，同时刷新iframe视图
  if (route.meta.link) {
    useTagsViewStore().delIframeView(route);
  }
}
// 关闭选中的标签页，关闭后跳转到最后一个标签
function closeSelectedTag(view) {
  proxy.$tab.closePage(view).then(({ visitedViews }) => {
    if (isActive(view)) {
      toLastView(visitedViews, view)
    }
  })
}
// 关闭右侧所有标签页
function closeRightTags() {
  proxy.$tab.closeRightPage(selectedTag.value).then(visitedViews => {
    // 如果当前路由不在剩余标签中，跳转到最后一个标签
    if (!visitedViews.find(i => i.fullPath === route.fullPath)) {
      toLastView(visitedViews)
    }
  })
}
// 关闭左侧所有标签页
function closeLeftTags() {
  proxy.$tab.closeLeftPage(selectedTag.value).then(visitedViews => {
    // 如果当前路由不在剩余标签中，跳转到最后一个标签
    if (!visitedViews.find(i => i.fullPath === route.fullPath)) {
      toLastView(visitedViews)
    }
  })
}
// 关闭除选中标签外的所有标签页
function closeOthersTags() {
  router.push(selectedTag.value).catch(() => { });
  proxy.$tab.closeOtherPage(selectedTag.value).then(() => {
    moveToCurrentTag()
  })
}
// 关闭所有标签页
function closeAllTags(view) {
  proxy.$tab.closeAllPage().then(({ visitedViews }) => {
    // 如果当前路由是固定标签则不跳转
    if (affixTags.value.some(tag => tag.path === route.path)) {
      return
    }
    toLastView(visitedViews, view)
  })
}
// 跳转到最后一个标签视图（无标签时默认跳转首页）
function toLastView(visitedViews, view) {
  const latestView = visitedViews.slice(-1)[0]
  if (latestView) {
    router.push(latestView.fullPath)
  } else {
    // 无标签时根据情况跳转：Dashboard页面使用redirect重载，其他跳转首页
    if (view.name === 'Dashboard') {
      router.replace({ path: '/redirect' + view.fullPath })
    } else {
      router.push('/')
    }
  }
}
// 打开右键菜单并计算显示位置（防止超出右边界）
function openMenu(tag, e) {
  const menuMinWidth = 105
  const offsetLeft = proxy.$el.getBoundingClientRect().left
  const offsetWidth = proxy.$el.offsetWidth
  const maxLeft = offsetWidth - menuMinWidth
  const l = e.clientX - offsetLeft + 15

  // 菜单超出右边界时靠左显示
  if (l > maxLeft) {
    left.value = maxLeft
  } else {
    left.value = l
  }

  top.value = e.clientY
  visible.value = true
  selectedTag.value = tag
}
// 关闭右键菜单
function closeMenu() {
  visible.value = false
}
// 滚动时关闭右键菜单
function handleScroll() {
  closeMenu()
}
</script>

<style lang='scss' scoped>
.tags-view-container {
  height: 34px;
  width: 100%;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color);
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.3), 0 0 3px 0 rgba(0, 0, 0, 0.15);
  .tags-view-wrapper {
    .tags-view-item {
      display: inline-block;
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid var(--el-border-color);
      color: var(--el-text-color-regular);
      background: var(--el-bg-color);
      padding: 0 8px;
      font-size: 12px;
      margin-left: 5px;
      margin-top: 4px;
      &:first-of-type {
        margin-left: 15px;
      }
      &:last-of-type {
        margin-right: 15px;
      }
      &.active {
        background-color: var(--el-color-primary);
        color: #fff;
        border-color: var(--el-color-primary);
        &::before {
          content: "";
          background: #fff;
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          position: relative;
          margin-right: 5px;
        }
      }
    }
  }
  .contextmenu {
    margin: 0;
    background: var(--el-bg-color-overlay);
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: var(--el-text-color-primary);
    border: 1px solid var(--el-border-color);
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.5);
    li {
      margin: 0;
      padding: 7px 16px;
      cursor: pointer;
      &:hover {
        background: var(--el-fill-color-light);
      }
    }
  }
}
</style>

<style lang="scss">
//reset element css of el-icon-close
.tags-view-wrapper {
  .tags-view-item {
    .el-icon-close {
      width: 16px;
      height: 16px;
      vertical-align: 2px;
      border-radius: 50%;
      text-align: center;
      transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
      transform-origin: 100% 50%;
      &:before {
        transform: scale(0.6);
        display: inline-block;
        vertical-align: -3px;
      }
      &:hover {
        background-color: var(--el-fill-color);
        color: var(--el-text-color-primary);
        width: 12px !important;
        height: 12px !important;
      }
    }
  }
}
</style>
