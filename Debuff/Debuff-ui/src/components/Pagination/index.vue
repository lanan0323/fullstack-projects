<template>
  <!-- 分页容器，支持隐藏、固定到视口底部、侧边栏折叠响应 -->
  <div
    :class="{ 'hidden': hidden, 'pagination-fixed': fixed, 'pagination-collapsed': fixed && !isUserSide && !appStore.sidebar.opened }"
    class="pagination-container"
  >
    <!-- Element Plus 分页组件 -->
    <el-pagination
      :background="background"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :layout="layout"
      :page-sizes="pageSizes"
      :pager-count="pagerCount"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
// 导入平滑滚动工具
import { scrollTo } from '@/utils/scroll-to'
// 导入路由
import { useRoute } from 'vue-router'
// 导入应用 store
import useAppStore from '@/store/modules/app'

const route = useRoute()
const appStore = useAppStore()
// C 端(UserLayout)固定路由，不走管理端折叠状态
const userSidePaths = ['/market', '/inventory', '/trade', '/wallet', '/profile']
// 判断当前是否在 C 端页面
const isUserSide = computed(() => userSidePaths.some(p => route.path.startsWith(p)))

const props = defineProps({
  // 总条数
  total: {
    required: true,
    type: Number
  },
  // 当前页码
  page: {
    type: Number,
    default: 1
  },
  // 每页条数
  limit: {
    type: Number,
    default: 20
  },
  // 每页条数选项
  pageSizes: {
    type: Array,
    default() {
      return [10, 20, 30, 50]
    }
  },
  // 页码按钮数量（移动端默认 5 个）
  pagerCount: {
    type: Number,
    default: document.body.clientWidth < 992 ? 5 : 7
  },
  // 分页布局
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  // 是否显示背景色
  background: {
    type: Boolean,
    default: true
  },
  // 翻页后是否自动滚动到顶部
  autoScroll: {
    type: Boolean,
    default: true
  },
  // 是否隐藏
  hidden: {
    type: Boolean,
    default: false
  },
  // 是否固定到视口底部（弹窗内的分页传 false）
  fixed: {
    type: Boolean,
    default: true
  }
})

// 定义事件
const emit = defineEmits();
// 双向绑定当前页码
const currentPage = computed({
  get() {
    return props.page
  },
  set(val) {
    emit('update:page', val)
  }
})
// 双向绑定每页条数
const pageSize = computed({
  get() {
    return props.limit
  },
  set(val){
    emit('update:limit', val)
  }
})
// 每页条数变化处理
function handleSizeChange(val) {
  // 切换后超出总页数则回到第一页
  if (currentPage.value * val > props.total) {
    currentPage.value = 1
  }
  // 触发分页事件
  emit('pagination', { page: currentPage.value, limit: val })
  // 自动滚动到顶部
  if (props.autoScroll) {
    scrollTo(0, 800)
  }
}
// 页码变化处理
function handleCurrentChange(val) {
  // 触发分页事件
  emit('pagination', { page: val, limit: pageSize.value })
  // 自动滚动到顶部
  if (props.autoScroll) {
    scrollTo(0, 800)
  }
}

</script>

<style scoped>
.pagination-container {
  background: var(--el-bg-color);
  padding: 12px 16px;
}
.pagination-container.hidden {
  display: none;
}
/* 固定到视口底部：管理端默认左 200px（折叠 54px）；C 端通过 --pagination-left 变量控制 */
.pagination-container.pagination-fixed {
  position: fixed;
  bottom: 20px;
  left: var(--pagination-left, 200px);
  right: 16px;
  z-index: 999;
  background: transparent;
  box-shadow: none;
}
.pagination-container.pagination-fixed.pagination-collapsed {
  left: 54px;
}
</style>
