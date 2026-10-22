<template>
  <!-- 面包屑导航容器，使用 / 作为分隔符 -->
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <!-- 过渡动画组 -->
    <transition-group name="breadcrumb">
      <!-- 遍历路由匹配项生成面包屑 -->
      <el-breadcrumb-item v-for="(item,index) in levelList" :key="item.path">
        <!-- 不可跳转的项（首页或最后一项）显示为文本 -->
        <span v-if="item.redirect === 'noRedirect' || index == levelList.length - 1" class="no-redirect">{{ item.meta.title }}</span>
        <!-- 可跳转的项显示为链接 -->
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup>
// 获取当前路由对象
const route = useRoute();
// 获取路由实例
const router = useRouter();
// 存储面包屑列表
const levelList = ref([])

// 获取面包屑数据
function getBreadcrumb() {
  // 只保留有 meta.title 的路由
  let matched = route.matched.filter(item => item.meta && item.meta.title);
  const first = matched[0]
  // 判断是否为首页
  if (!isDashboard(first)) {
    // 非首页则在前面拼接首页
    matched = [{ path: '/index', meta: { title: '首页' } }].concat(matched)
  }

  // 过滤掉 breadcrumb 设为 false 的路由
  levelList.value = matched.filter(item => item.meta && item.meta.title && item.meta.breadcrumb !== false)
}
// 判断是否为首页
function isDashboard(route) {
  const name = route && route.name
  if (!name) {
    return false
  }
  return name.trim() === 'Index'
}
// 处理面包屑点击跳转
function handleLink(item) {
  const { redirect, path } = item
  // 有 redirect 则跳转重定向地址
  if (redirect) {
    router.push(redirect)
    return
  }
  // 否则跳转路径
  router.push(path)
}

// 监听路由变化自动更新面包屑
watchEffect(() => {
  // 重定向页面不更新面包屑
  if (route.path.startsWith('/redirect/')) {
    return
  }
  getBreadcrumb()
})
// 初始化获取面包屑
getBreadcrumb();
</script>

<style lang='scss' scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 8px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}
</style>
