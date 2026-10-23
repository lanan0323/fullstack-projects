// 注释：导出默认配置对象
export default {
  /**
   * 网页标题
   */
  // 注释：从环境变量中获取应用标题
  title: import.meta.env.VITE_APP_TITLE,
  /**
   * 侧边栏主题 深色主题theme-dark，浅色主题theme-light
   */
  // 注释：设置侧边栏主题为深色主题
  sideTheme: 'theme-dark',
  /**
   * 是否显示系统布局配置
   */
  // 注释：设置是否显示系统布局配置按钮
  showSettings: true,

  /**
   * 是否显示顶部导航
   */
  // 注释：设置是否显示顶部导航栏
  topNav: false,

  /**
   * 是否显示 tagsView（标签页视图）
   */
  // 注释：设置是否显示标签页视图
  tagsView: true,

  /**
   * 是否固定头部
   */
  // 注释：设置是否固定页面头部
  fixedHeader: false,

  /**
   * 是否显示侧边栏 logo
   */
  // 注释：设置是否显示侧边栏的 logo
  sidebarLogo: true,

  /**
   * 是否显示动态标题
   */
  // 注释：设置是否显示动态标题
  dynamicTitle: false,

  /**
   * @type {string | array} 'production' | ['production', 'development']
   * @description Need show err logs component.
   * The default is only used in the production env
   * If you want to also use it in dev, you can pass ['production', 'development']
   */
  // 注释：设置错误日志显示环境，默认只在生产环境显示
  errorLog: 'production'
}
