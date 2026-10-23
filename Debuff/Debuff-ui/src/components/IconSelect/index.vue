<template>
  <!-- 图标选择器容器 -->
  <div class="icon-body">
    <!-- 图标搜索输入框 -->
    <el-input
      v-model="iconName"
      class="icon-search"
      clearable
      placeholder="请输入图标名称"
      @clear="filterIcons"
      @input="filterIcons"
    >
      <template #suffix><i class="el-icon-search el-input__icon" /></template>
    </el-input>
    <!-- 图标列表区域 -->
    <div class="icon-list">
      <div class="list-container">
        <!-- 遍历渲染每个图标 -->
        <div v-for="(item, index) in iconList" class="icon-item-wrapper" :key="index" @click="selectedIcon(item)">
          <div :class="['icon-item', { active: activeIcon === item }]">
            <!-- SVG 图标 -->
            <svg-icon :icon-class="item" class-name="icon" style="height: 25px;width: 16px;"/>
            <!-- 图标名称 -->
            <span>{{ item }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 导入 SVG 图标列表
import icons from './requireIcons'

const props = defineProps({
  // 当前选中的图标名称
  activeIcon: {
    type: String
  }
});

// 搜索关键字
const iconName = ref('');
// 图标列表（可被搜索过滤）
const iconList = ref(icons);
// 定义选中事件
const emit = defineEmits(['selected']);

// 根据输入关键字过滤图标
function filterIcons() {
  iconList.value = icons
  if (iconName.value) {
    // 按关键字模糊匹配过滤
    iconList.value = icons.filter(item => item.indexOf(iconName.value) !== -1)
  }
}

// 选中图标
function selectedIcon(name) {
  emit('selected', name)
  document.body.click()
}

// 重置搜索和列表
function reset() {
  iconName.value = ''
  iconList.value = icons
}

// 暴露 reset 方法供父组件调用
defineExpose({
  reset
})
</script>

<style lang='scss' scoped>
   .icon-body {
    width: 100%;
    padding: 10px;
    .icon-search {
      position: relative;
      margin-bottom: 5px;
    }
    .icon-list {
      height: 200px;
      overflow: auto;
      .list-container {
        display: flex;
        flex-wrap: wrap;
        .icon-item-wrapper {
          width: calc(100% / 3);
          height: 25px;
          line-height: 25px;
          cursor: pointer;
          display: flex;
          .icon-item {
            display: flex;
            max-width: 100%;
            height: 100%;
            padding: 0 5px;
            &:hover {
              background: #ececec;
              border-radius: 5px;
            }
            .icon {
              flex-shrink: 0;
            }
            span {
              display: inline-block;
              vertical-align: -0.15em;
              fill: currentColor;
              padding-left: 2px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }
          .icon-item.active {
            background: #ececec;
            border-radius: 5px;
          }
        }
      }
    }
  }
</style>
