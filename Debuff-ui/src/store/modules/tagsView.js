// 定义标签页视图 store
const useTagsViewStore = defineStore(
  'tags-view',
  {
    // 定义 store 的状态
    state: () => ({
      // 已访问的视图列表
      visitedViews: [],
      // 缓存的视图名称列表（用于 keep-alive）
      cachedViews: [],
      // iframe 视图列表
      iframeViews: []
    }),
    // 定义 store 的操作方法
    actions: {
      // 添加视图（同时添加到已访问和缓存）
      addView(view) {
        this.addVisitedView(view)
        this.addCachedView(view)
      },
      // 添加 iframe 视图（去重）
      addIframeView(view) {
        // 如果已存在相同路径则不重复添加
        if (this.iframeViews.some(v => v.path === view.path)) return
        this.iframeViews.push(
          Object.assign({}, view, {
            title: view.meta.title || 'no-name'
          })
        )
      },
      // 添加已访问视图（去重）
      addVisitedView(view) {
        // 如果已存在相同路径则不重复添加
        if (this.visitedViews.some(v => v.path === view.path)) return
        this.visitedViews.push(
          Object.assign({}, view, {
            title: view.meta.title || 'no-name'
          })
        )
      },
      // 添加缓存视图（去重，noCache 的页面不缓存）
      addCachedView(view) {
        // 如果缓存中已包含该名称则跳过
        if (this.cachedViews.includes(view.name)) return
        // 没有设置 noCache 的页面才加入缓存
        if (!view.meta.noCache) {
          this.cachedViews.push(view.name)
        }
      },
      // 删除视图（同时从已访问和缓存中删除）
      delView(view) {
        return new Promise(resolve => {
          this.delVisitedView(view)
          this.delCachedView(view)
          resolve({
            visitedViews: [...this.visitedViews],
            cachedViews: [...this.cachedViews]
          })
        })
      },
      // 从已访问列表中删除视图
      delVisitedView(view) {
        return new Promise(resolve => {
          // 遍历找到匹配路径的视图并移除
          for (const [i, v] of this.visitedViews.entries()) {
            if (v.path === view.path) {
              this.visitedViews.splice(i, 1)
              break
            }
          }
          // 同时从 iframe 视图中移除匹配路径的项
          this.iframeViews = this.iframeViews.filter(item => item.path !== view.path)
          resolve([...this.visitedViews])
        })
      },
      // 从 iframe 视图中删除指定视图
      delIframeView(view) {
        return new Promise(resolve => {
          // 过滤掉匹配路径的 iframe 视图
          this.iframeViews = this.iframeViews.filter(item => item.path !== view.path)
          resolve([...this.iframeViews])
        })
      },
      // 从缓存列表中删除视图
      delCachedView(view) {
        return new Promise(resolve => {
          // 查找缓存名称的索引
          const index = this.cachedViews.indexOf(view.name)
          // 找到则移除
          index > -1 && this.cachedViews.splice(index, 1)
          resolve([...this.cachedViews])
        })
      },
      // 删除其他视图（保留当前和 affix 固定的）
      delOthersViews(view) {
        return new Promise(resolve => {
          this.delOthersVisitedViews(view)
          this.delOthersCachedViews(view)
          resolve({
            visitedViews: [...this.visitedViews],
            cachedViews: [...this.cachedViews]
          })
        })
      },
      // 删除其他已访问视图（保留 affix 和当前路径）
      delOthersVisitedViews(view) {
        return new Promise(resolve => {
          // 只保留 affix 固定的标签和当前路径的标签
          this.visitedViews = this.visitedViews.filter(v => {
            return v.meta.affix || v.path === view.path
          })
          // 只保留当前路径的 iframe 视图
          this.iframeViews = this.iframeViews.filter(item => item.path === view.path)
          resolve([...this.visitedViews])
        })
      },
      // 删除其他缓存视图（只保留当前视图的缓存）
      delOthersCachedViews(view) {
        return new Promise(resolve => {
          // 查找当前视图在缓存中的索引
          const index = this.cachedViews.indexOf(view.name)
          if (index > -1) {
            // 只保留当前视图的缓存
            this.cachedViews = this.cachedViews.slice(index, index + 1)
          } else {
            // 未找到则清空缓存
            this.cachedViews = []
          }
          resolve([...this.cachedViews])
        })
      },
      // 删除所有视图
      delAllViews(view) {
        return new Promise(resolve => {
          this.delAllVisitedViews(view)
          this.delAllCachedViews(view)
          resolve({
            visitedViews: [...this.visitedViews],
            cachedViews: [...this.cachedViews]
          })
        })
      },
      // 删除所有已访问视图（保留 affix 固定的）
      delAllVisitedViews(view) {
        return new Promise(resolve => {
          // 只保留 affix 固定的标签
          const affixTags = this.visitedViews.filter(tag => tag.meta.affix)
          this.visitedViews = affixTags
          // 清空 iframe 视图
          this.iframeViews = []
          resolve([...this.visitedViews])
        })
      },
      // 删除所有缓存视图
      delAllCachedViews(view) {
        return new Promise(resolve => {
          // 清空缓存数组
          this.cachedViews = []
          resolve([...this.cachedViews])
        })
      },
      // 更新已访问视图的信息
      updateVisitedView(view) {
        // 遍历找到匹配路径的视图并更新
        for (let v of this.visitedViews) {
          if (v.path === view.path) {
            v = Object.assign(v, view)
            break
          }
        }
      },
      // 删除当前标签右侧的所有标签
      delRightTags(view) {
        return new Promise(resolve => {
          // 查找当前标签的索引
          const index = this.visitedViews.findIndex(v => v.path === view.path)
          if (index === -1) {
            return
          }
          // 过滤掉右侧的标签，保留左侧和 affix 固定的
          this.visitedViews = this.visitedViews.filter((item, idx) => {
            // 索引小于等于当前或是 affix 固定的保留
            if (idx <= index || (item.meta && item.meta.affix)) {
              return true
            }
            // 同时从缓存中移除被删除的标签
            const i = this.cachedViews.indexOf(item.name)
            if (i > -1) {
              this.cachedViews.splice(i, 1)
            }
            // 如果有 iframe 链接也一并移除
            if(item.meta.link) {
              const fi = this.iframeViews.findIndex(v => v.path === item.path)
              this.iframeViews.splice(fi, 1)
            }
            return false
          })
          resolve([...this.visitedViews])
        })
      },
      // 删除当前标签左侧的所有标签
      delLeftTags(view) {
        return new Promise(resolve => {
          // 查找当前标签的索引
          const index = this.visitedViews.findIndex(v => v.path === view.path)
          if (index === -1) {
            return
          }
          // 过滤掉左侧的标签，保留右侧和 affix 固定的
          this.visitedViews = this.visitedViews.filter((item, idx) => {
            // 索引大于等于当前或是 affix 固定的保留
            if (idx >= index || (item.meta && item.meta.affix)) {
              return true
            }
            // 同时从缓存中移除被删除的标签
            const i = this.cachedViews.indexOf(item.name)
            if (i > -1) {
              this.cachedViews.splice(i, 1)
            }
            // 如果有 iframe 链接也一并移除
            if(item.meta.link) {
              const fi = this.iframeViews.findIndex(v => v.path === item.path)
              this.iframeViews.splice(fi, 1)
            }
            return false
          })
          resolve([...this.visitedViews])
        })
      }
    }
  })

// 导出标签页视图 store
export default useTagsViewStore
