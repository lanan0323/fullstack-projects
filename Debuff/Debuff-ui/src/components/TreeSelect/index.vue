<template>
  <!-- 树形选择器组件容器 -->
  <div class="el-tree-select">
    <!-- 下拉选择框 -->
    <el-select
      style="width: 100%"
      v-model="valueId"
      ref="treeSelect"
      :filterable="true"
      :clearable="true"
      @clear="clearHandle"
      :filter-method="selectFilterData"
      :placeholder="placeholder"
    >
      <el-option :value="valueId" :label="valueTitle">
        <!-- 下拉面板中的树形结构 -->
        <el-tree
          id="tree-option"
          ref="selectTree"
          :accordion="accordion"
          :data="options"
          :props="objMap"
          :node-key="objMap.value"
          :expand-on-click-node="false"
          :default-expanded-keys="defaultExpandedKey"
          :filter-node-method="filterNode"
          @node-click="handleNodeClick"
        ></el-tree>
      </el-option>
    </el-select>
  </div>
</template>

<script setup>

// 获取组件代理实例
const { proxy } = getCurrentInstance();

const props = defineProps({
  // 字段映射配置（value/label/children）
  objMap: {
    type: Object,
    default: () => {
      return {
        value: 'id', // ID 字段名
        label: 'label', // 显示名称
        children: 'children' // 子级字段名
      }
    }
  },
  // 是否手风琴模式（同时只展开一个同级节点）
  accordion: {
    type: Boolean,
    default: () => {
      return false
    }
  },
  // 当前双向绑定的值
  value: {
    type: [String, Number],
    default: ''
  },
  // 树形数据源
  options: {
    type: Array,
    default: () => []
  },
  // 输入框占位文字
  placeholder: {
    type: String,
    default: ''
  }
})

// 定义事件
const emit = defineEmits(['update:value']);

// 双向绑定值
const valueId = computed({
  get: () => props.value,
  set: (val) => {
    emit('update:value', val)
  }
});
// 当前选中节点的显示名称
const valueTitle = ref('');
// 默认展开的节点 key
const defaultExpandedKey = ref([]);

// 初始化选中状态
function initHandle() {
  nextTick(() => {
    const selectedValue = valueId.value;
    // 有值时设置树的选中状态
    if(selectedValue !== null && typeof (selectedValue) !== 'undefined') {
      const node = proxy.$refs.selectTree.getNode(selectedValue)
      if (node) {
        valueTitle.value = node.data[props.objMap.label]
        proxy.$refs.selectTree.setCurrentKey(selectedValue) // 设置默认选中
        defaultExpandedKey.value = [selectedValue] // 设置默认展开
      }
    } else {
      clearHandle()
    }
  })
}
// 点击树节点处理
function handleNodeClick(node) {
  valueTitle.value = node[props.objMap.label]
  valueId.value = node[props.objMap.value];
  defaultExpandedKey.value = [];
  // 关闭下拉框
  proxy.$refs.treeSelect.blur()
  // 清空搜索过滤
  selectFilterData('')
}
// 搜索过滤树节点
function selectFilterData(val) {
  proxy.$refs.selectTree.filter(val)
}
// 树节点过滤方法
function filterNode(value, data) {
  if (!value) return true
  return data[props.objMap['label']].indexOf(value) !== -1
}
// 清空选择
function clearHandle() {
  valueTitle.value = ''
  valueId.value = ''
  defaultExpandedKey.value = [];
  clearSelected()
}
// 清除树的选中样式
function clearSelected() {
  const allNode = document.querySelectorAll('#tree-option .el-tree-node')
  allNode.forEach((element) => element.classList.remove('is-current'))
}

// 挂载时初始化
onMounted(() => {
  initHandle()
})

// 监听值变化重新初始化
watch(valueId, () => {
  initHandle();
})
</script>

<style lang='scss' scoped>
@import "@/assets/styles/variables.module.scss";
.el-scrollbar .el-scrollbar__view .el-select-dropdown__item {
  padding: 0;
  background-color: var(--el-bg-color);
  height: auto;
}

.el-select-dropdown__item.selected {
  font-weight: normal;
}

ul li .el-tree .el-tree-node__content {
  height: auto;
  padding: 0 20px;
  box-sizing: border-box;
}

:deep(.el-tree-node__content:hover),
:deep(.el-tree-node__content:active),
:deep(.is-current > div:first-child),
:deep(.el-tree-node__content:focus) {
  background-color: var(--el-fill-color-light);
  color: $--color-primary;
}
</style>
