// 导入默认配置
import defaultSettings from '@/settings'
// 导入动态标题工具函数
import { useDynamicTitle } from '@/utils/dynamicTitle'

// 从默认配置中解构布局相关设置
const { sideTheme, showSettings, topNav, tagsView, fixedHeader, sidebarLogo, dynamicTitle } = defaultSettings

// 从 localStorage 读取用户保存的布局配置
const storageSetting = JSON.parse(localStorage.getItem('layout-setting')) || ''

// 定义布局设置 store
const useSettingsStore = defineStore(
  'settings',
  {
    // 定义 store 的状态
    state: () => ({
      // 页面标题
      title: '',
      // 主题颜色，优先读取本地缓存
      theme: storageSetting.theme || '#4d9fff',
      // 侧边栏主题，优先读取本地缓存
      sideTheme: storageSetting.sideTheme || sideTheme,
      // 是否显示设置面板
      showSettings: showSettings,
      // 是否启用顶部导航，优先读取本地缓存
      topNav: storageSetting.topNav === undefined ? topNav : storageSetting.topNav,
      // 是否启用标签页视图，优先读取本地缓存
      tagsView: storageSetting.tagsView === undefined ? tagsView : storageSetting.tagsView,
      // 是否固定头部，优先读取本地缓存
      fixedHeader: storageSetting.fixedHeader === undefined ? fixedHeader : storageSetting.fixedHeader,
      // 是否显示侧边栏 Logo，优先读取本地缓存
      sidebarLogo: storageSetting.sidebarLogo === undefined ? sidebarLogo : storageSetting.sidebarLogo,
      // 是否启用动态标题，优先读取本地缓存
      dynamicTitle: storageSetting.dynamicTitle === undefined ? dynamicTitle : storageSetting.dynamicTitle
    }),
    // 定义 store 的操作方法
    actions: {
      // 修改布局设置项
      changeSetting(data) {
        // 解构获取键和值
        const { key, value } = data
        // 如果存在该属性则更新
        if (this.hasOwnProperty(key)) {
          this[key] = value
        }
      },
      // 设置网页标题
      setTitle(title) {
        this.title = title
        // 调用动态标题工具更新页面标题
        useDynamicTitle();
      }
    }
  })

// 导出设置 store
export default useSettingsStore
