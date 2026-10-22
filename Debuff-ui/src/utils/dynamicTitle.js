// 导入 store 实例
import store from '@/store'
// 导入默认配置
import defaultSettings from '@/settings'
// 导入设置 store
import useSettingsStore from '@/store/modules/settings'

/**
 * 动态修改页面标题
 */
export function useDynamicTitle() {
  // 获取设置 store 实例
  const settingsStore = useSettingsStore();
  // 如果启用了动态标题
  if (settingsStore.dynamicTitle) {
    // 设置为 "页面标题 - 应用标题" 格式
    document.title = settingsStore.title + ' - ' + defaultSettings.title;
  } else {
    // 否则只显示固定的应用标题
    document.title = defaultSettings.title;
  }
}
