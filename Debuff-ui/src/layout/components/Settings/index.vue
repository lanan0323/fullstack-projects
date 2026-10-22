<template>
  <!-- 布局设置抽屉面板，从右侧滑出 -->
  <el-drawer v-model="showSettings" :withHeader="false" direction="rtl" size="300px">
    <!-- 抽屉标题区域 -->
    <div class="setting-drawer-title">
      <h3 class="drawer-title">主题风格设置</h3>
    </div>
    <!-- 侧边栏主题切换（深色/浅色） -->
    <div class="setting-drawer-block-checbox">
      <!-- 深色主题选项 -->
      <div class="setting-drawer-block-checbox-item" @click="handleTheme('theme-dark')">
        <img src="@/assets/images/dark.svg" alt="dark" />
        <!-- 选中时显示勾选图标 -->
        <div v-if="sideTheme === 'theme-dark'" class="setting-drawer-block-checbox-selectIcon" style="display: block;">
          <i aria-label="图标: check" class="anticon anticon-check">
            <svg viewBox="64 64 896 896" data-icon="check" width="1em" height="1em" :fill="theme" aria-hidden="true" focusable="false" class>
              <path d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 0 0-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9l273.9 347c12.8 16.2 37.4 16.2 50.3 0l488.4-618.9c4.1-5.1.4-12.8-6.3-12.8z" />
            </svg>
          </i>
        </div>
      </div>
      <!-- 浅色主题选项 -->
      <div class="setting-drawer-block-checbox-item" @click="handleTheme('theme-light')">
        <img src="@/assets/images/light.svg" alt="light" />
        <!-- 选中时显示勾选图标 -->
        <div v-if="sideTheme === 'theme-light'" class="setting-drawer-block-checbox-selectIcon" style="display: block;">
          <i aria-label="图标: check" class="anticon anticon-check">
            <svg viewBox="64 64 896 896" data-icon="check" width="1em" height="1em" :fill="theme" aria-hidden="true" focusable="false" class>
              <path d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 0 0-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9l273.9 347c12.8 16.2 37.4 16.2 50.3 0l488.4-618.9c4.1-5.1.4-12.8-6.3-12.8z" />
            </svg>
          </i>
        </div>
      </div>
    </div>
    <!-- 主题颜色选择器 -->
    <div class="drawer-item">
      <span>主题颜色</span>
      <span class="comp-style">
        <el-color-picker v-model="theme" :predefine="predefineColors" @change="themeChange"/>
      </span>
    </div>
    <el-divider />

    <!-- 系统布局配置区域标题 -->
    <h3 class="drawer-title">系统布局配置</h3>

    <!-- 标签页导航开关 -->
    <div class="drawer-item">
      <span>开启 Tags-Views</span>
      <span class="comp-style">
        <el-switch v-model="settingsStore.tagsView" class="drawer-switch" />
      </span>
    </div>

    <!-- 固定头部开关 -->
    <div class="drawer-item">
      <span>固定 Header</span>
      <span class="comp-style">
        <el-switch v-model="settingsStore.fixedHeader" class="drawer-switch" />
      </span>
    </div>

    <!-- 显示Logo开关 -->
    <div class="drawer-item">
      <span>显示 Logo</span>
      <span class="comp-style">
        <el-switch v-model="settingsStore.sidebarLogo" class="drawer-switch" />
      </span>
    </div>

    <!-- 动态标题开关（浏览器标签页标题跟随路由变化） -->
    <div class="drawer-item">
      <span>动态标题</span>
      <span class="comp-style">
        <el-switch v-model="settingsStore.dynamicTitle" class="drawer-switch" />
      </span>
    </div>

    <el-divider />

    <!-- 保存配置按钮，将设置存入localStorage -->
    <el-button type="primary" plain icon="DocumentAdd" @click="saveSetting">保存配置</el-button>
    <!-- 重置配置按钮，清除localStorage并刷新页面 -->
    <el-button plain icon="Refresh" @click="resetSetting">重置配置</el-button>
  </el-drawer>

</template>

<script setup>
// 引入SCSS变量（主题颜色等）
import variables from '@/assets/styles/variables.module.scss'
// 引入axios（当前未直接使用但保留引用）
import axios from 'axios'
// 引入Element Plus加载动画和消息提示
import { ElLoading, ElMessage } from 'element-plus'
// 引入动态标题工具函数
import { useDynamicTitle } from '@/utils/dynamicTitle'
// 引入布局设置store
import useSettingsStore from '@/store/modules/settings'
// 引入主题样式处理函数
import { handleThemeStyle } from '@/utils/theme'

// 获取当前组件实例代理（用于调用$modal等全局方法）
const { proxy } = getCurrentInstance();
// 获取布局设置store实例
const settingsStore = useSettingsStore()
// 控制抽屉显示/隐藏状态
const showSettings = ref(false);
// 当前主题颜色值
const theme = ref(settingsStore.theme);
// 当前侧边栏主题（dark/light）
const sideTheme = ref(settingsStore.sideTheme);
// 将store设置转为计算属性方便模板访问
const storeSettings = computed(() => settingsStore);
// 预定义的主题颜色列表
const predefineColors = ref(["#409EFF", "#ff4500", "#ff8c00", "#ffd700", "#90ee90", "#00ced1", "#1e90ff", "#c71585"]);

// 主题颜色改变时更新store并应用样式
function themeChange(val) {
  settingsStore.theme = val;
  handleThemeStyle(val);
}
// 切换侧边栏深色/浅色主题
function handleTheme(val) {
  settingsStore.sideTheme = val;
  sideTheme.value = val;
}
// 保存当前所有布局设置到localStorage
function saveSetting() {
  proxy.$modal.loading("正在保存到本地，请稍候...");
  // 组装需要保存的布局配置对象
  let layoutSetting = {
    "topNav": storeSettings.value.topNav,
    "tagsView": storeSettings.value.tagsView,
    "fixedHeader": storeSettings.value.fixedHeader,
    "sidebarLogo": storeSettings.value.sidebarLogo,
    "dynamicTitle": storeSettings.value.dynamicTitle,
    "sideTheme": storeSettings.value.sideTheme,
    "theme": storeSettings.value.theme
  };
  // 序列化后存入localStorage
  localStorage.setItem("layout-setting", JSON.stringify(layoutSetting));
  // 延迟关闭加载动画
  setTimeout(proxy.$modal.closeLoading(), 1000)
}
// 重置布局设置：清除缓存并刷新页面
function resetSetting() {
  proxy.$modal.loading("正在清除设置缓存并刷新，请稍候...");
  localStorage.removeItem("layout-setting")
  setTimeout("window.location.reload()", 1000)
}
// 打开设置抽屉（由父组件通过ref调用）
function openSetting() {
  showSettings.value = true;
}

// 暴露openSetting方法给父组件调用
defineExpose({
  openSetting,
})
</script>

<style lang='scss' scoped>
.setting-drawer-title {
  margin-bottom: 12px;
  color: rgba(0, 0, 0, 0.85);
  line-height: 22px;
  font-weight: bold;
  .drawer-title {
    font-size: 14px;
  }
}
.setting-drawer-block-checbox {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-top: 10px;
  margin-bottom: 20px;

  .setting-drawer-block-checbox-item {
    position: relative;
    margin-right: 16px;
    border-radius: 2px;
    cursor: pointer;

    img {
      width: 48px;
      height: 48px;
    }

    .custom-img {
      width: 48px;
      height: 38px;
      border-radius: 5px;
      box-shadow: 1px 1px 2px #898484;
    }

    .setting-drawer-block-checbox-selectIcon {
      position: absolute;
      top: 0;
      right: 0;
      width: 100%;
      height: 100%;
      padding-top: 15px;
      padding-left: 24px;
      color: #1890ff;
      font-weight: 700;
      font-size: 14px;
    }
  }
}

.drawer-item {
  color: rgba(0, 0, 0, 0.65);
  padding: 12px 0;
  font-size: 14px;

  .comp-style {
    float: right;
    margin: -3px 8px 0px 0px;
  }
}
</style>
