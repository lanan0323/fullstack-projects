// 导入标签页视图 store
import useTagsViewStore from '@/store/modules/tagsView'
// 导入路由实例
import router from '@/router'

// 导出页签操作工具对象
export default {
  // 刷新当前 tab 页签
  refreshPage(obj) {
    // 获取当前路由信息
    const { path, query, matched } = router.currentRoute.value;
    // 如果未传入参数，从路由匹配中查找组件名称
    if (obj === undefined) {
      matched.forEach((m) => {
        // 遍历匹配的路由，找到非 Layout/ParentView 的组件
        if (m.components && m.components.default && m.components.default.name) {
          if (!['Layout', 'ParentView'].includes(m.components.default.name)) {
            obj = { name: m.components.default.name, path: path, query: query };
          }
        }
      });
    }
    // 先删除缓存，再通过重定向实现刷新
    return useTagsViewStore().delCachedView(obj).then(() => {
      const { path, query } = obj
      // 跳转到重定向路由实现页面刷新
      router.replace({
        path: '/redirect' + path,
        query: query
      })
    })
  },
  // 关闭当前 tab 并打开新页签
  closeOpenPage(obj) {
    // 删除当前路由的标签视图
    useTagsViewStore().delView(router.currentRoute.value);
    // 如果指定了新页签则跳转
    if (obj !== undefined) {
      return router.push(obj);
    }
  },
  // 关闭指定 tab 页签
  closePage(obj) {
    // 未指定时关闭当前页签并跳转到最后一个页签
    if (obj === undefined) {
      return useTagsViewStore().delView(router.currentRoute.value).then(({ visitedViews }) => {
        // 获取最后访问的页签
        const latestView = visitedViews.slice(-1)[0]
        if (latestView) {
          // 跳转到最后访问的页签
          return router.push(latestView.fullPath)
        }
        // 没有页签则跳转首页
        return router.push('/');
      });
    }
    // 关闭指定的页签
    return useTagsViewStore().delView(obj);
  },
  // 关闭所有 tab 页签
  closeAllPage() {
    return useTagsViewStore().delAllViews();
  },
  // 关闭左侧 tab 页签
  closeLeftPage(obj) {
    return useTagsViewStore().delLeftTags(obj || router.currentRoute.value);
  },
  // 关闭右侧 tab 页签
  closeRightPage(obj) {
    return useTagsViewStore().delRightTags(obj || router.currentRoute.value);
  },
  // 关闭其他 tab 页签
  closeOtherPage(obj) {
    return useTagsViewStore().delOthersViews(obj || router.currentRoute.value);
  },
  // 打开指定 tab 页签
  openPage(url) {
    return router.push(url);
  },
  // 更新 tab 页签信息
  updatePage(obj) {
    return useTagsViewStore().updateVisitedView(obj);
  }
}
