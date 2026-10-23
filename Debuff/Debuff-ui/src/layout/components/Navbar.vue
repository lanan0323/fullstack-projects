<template>
  <!-- 顶部导航栏容器 -->
  <div class="navbar">
    <!-- 侧边栏折叠/展开按钮（汉堡图标） -->
    <hamburger id="hamburger-container" :is-active="appStore.sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
    <!-- 面包屑导航，仅在未开启顶部导航模式时显示 -->
    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!settingsStore.topNav" />

    <!-- 右侧菜单区域 -->
    <div class="right-menu">
      <!-- 用户头像下拉菜单容器 -->
      <div class="avatar-container">
        <!-- 下拉菜单组件，点击触发命令处理 -->
        <el-dropdown @command="handleCommand" class="right-menu-item hover-effect" trigger="click">
          <!-- 头像展示区域 -->
          <div class="avatar-wrapper">
            <!-- 用户头像图片 -->
            <img :src="userStore.avatar" class="user-avatar" />
            <!-- 下拉箭头图标 -->
            <el-icon><caret-bottom /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <!-- 跳转到个人中心页面 -->
              <router-link to="/user/profile">
                <el-dropdown-item>个人中心</el-dropdown-item>
              </router-link>
              <!-- 打开布局设置抽屉（仅当设置面板开启时显示） -->
              <el-dropdown-item command="setLayout" v-if="settingsStore.showSettings">
                <span>布局设置</span>
              </el-dropdown-item>
              <!-- 退出登录（带分割线） -->
              <el-dropdown-item divided command="logout">
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
// 引入Element Plus消息弹框（退出确认）
import { ElMessageBox } from 'element-plus'
// 引入面包屑导航组件
import Breadcrumb from '@/components/Breadcrumb'
// 引入汉堡图标组件（侧边栏折叠按钮）
import Hamburger from '@/components/Hamburger'
// 引入应用状态store（侧边栏状态）
import useAppStore from '@/store/modules/app'
// 引入用户状态store（用户信息/登出）
import useUserStore from '@/store/modules/user'
// 引入布局设置store（判断是否显示设置按钮）
import useSettingsStore from '@/store/modules/settings'

// 获取应用store实例
const appStore = useAppStore()
// 获取用户store实例
const userStore = useUserStore()
// 获取设置store实例
const settingsStore = useSettingsStore()

// 切换侧边栏展开/收起状态
function toggleSideBar() {
  appStore.toggleSideBar()
}

// 处理下拉菜单命令分发
function handleCommand(command) {
  switch (command) {
    // 打开布局设置面板
    case "setLayout":
      setLayout();
      break;
    // 执行退出登录
    case "logout":
      logout();
      break;
    default:
      break;
  }
}

// 退出登录：弹出确认框，确认后调用store登出并跳转
function logout() {
  ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 调用用户store的登出方法，完成后跳转到首页
    userStore.logOut().then(() => {
      location.href = '/index';
    })
  }).catch(() => { });
}

// 定义向父组件发射的事件
const emits = defineEmits(['setLayout'])
// 向父组件发射setLayout事件以打开设置抽屉
function setLayout() {
  emits('setLayout');
}
</script>

<style lang='scss' scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: var(--el-bg-color);
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.3);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(255, 255, 255, 0.04);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;
    display: flex;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: var(--el-text-color-regular);
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(255, 255, 255, 0.04);
        }
      }
    }

    .avatar-container {
      margin-right: 40px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        i {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
