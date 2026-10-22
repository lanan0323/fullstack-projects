// 注释：导入 js-cookie 库，用于操作浏览器 Cookie
import Cookies from 'js-cookie'

// 注释：定义应用 store，使用 Pinia 的 defineStore 方法
const useAppStore = defineStore(
  // 注释：store 的名称标识为 'app'
  'app',
  {
    // 注释：定义 store 的状态
    state: () => ({
      // 注释：侧边栏状态配置
      sidebar: {
        // 注释：侧边栏是否打开，从 Cookie 中读取状态，默认为 true（打开）
        opened: Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : true,
        // 注释：是否不使用动画
        withoutAnimation: false,
        // 注释：是否隐藏侧边栏
        hide: false
      },
      // 注释：设备类型，默认为 'desktop'（桌面端）
      device: 'desktop',
      // 注释：组件大小，从 Cookie 中读取或使用默认值 'default'
      size: Cookies.get('size') || 'default'
    }),
    // 注释：定义 store 的操作方法
    actions: {
      // 注释：切换侧边栏开关状态的方法
      toggleSideBar(withoutAnimation) {
        // 注释：如果侧边栏被隐藏，直接返回 false
        if (this.sidebar.hide) {
          return false;
        }
        // 注释：切换侧边栏的打开/关闭状态
        this.sidebar.opened = !this.sidebar.opened
        // 注释：设置是否使用动画
        this.sidebar.withoutAnimation = withoutAnimation
        // 注释：如果侧边栏打开
        if (this.sidebar.opened) {
          // 注释：将侧边栏状态保存到 Cookie，值为 1（打开）
          Cookies.set('sidebarStatus', 1)
        } else {
          // 注释：将侧边栏状态保存到 Cookie，值为 0（关闭）
          Cookies.set('sidebarStatus', 0)
        }
      },
      // 注释：关闭侧边栏的方法
      closeSideBar({ withoutAnimation }) {
        // 注释：将侧边栏状态保存到 Cookie，值为 0（关闭）
        Cookies.set('sidebarStatus', 0)
        // 注释：设置侧边栏为关闭状态
        this.sidebar.opened = false
        // 注释：设置是否使用动画
        this.sidebar.withoutAnimation = withoutAnimation
      },
      // 注释：切换设备类型的方法
      toggleDevice(device) {
        // 注释：设置设备类型
        this.device = device
      },
      // 注释：设置组件大小的方法
      setSize(size) {
        // 注释：设置组件大小到 store 状态
        this.size = size;
        // 注释：将组件大小保存到 Cookie
        Cookies.set('size', size)
      },
      // 注释：切换侧边栏隐藏状态的方法
      toggleSideBarHide(status) {
        // 注释：设置侧边栏隐藏状态
        this.sidebar.hide = status
      }
    }
  })

// 注释：导出应用 store 供其他模块使用
export default useAppStore
