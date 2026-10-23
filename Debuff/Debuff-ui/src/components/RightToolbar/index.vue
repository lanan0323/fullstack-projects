<template>
  <!-- 表格右上方工具栏 -->
  <div class="top-right-btn" :style="style">
    <el-row>
      <!-- 搜索/隐藏搜索切换按钮 -->
      <el-tooltip class="item" effect="dark" :content="showSearch ? '隐藏搜索' : '显示搜索'" placement="top" v-if="search">
        <el-button circle icon="Search" @click="toggleSearch()" />
      </el-tooltip>
      <!-- 刷新按钮 -->
      <el-tooltip class="item" effect="dark" content="刷新" placement="top">
        <el-button circle icon="Refresh" @click="refresh()" />
      </el-tooltip>
      <!-- 显隐列按钮（穿梭框模式） -->
      <el-tooltip class="item" effect="dark" content="显隐列" placement="top" v-if="columns">
        <el-button circle icon="Menu" @click="showColumn()" v-if="showColumnsType == 'transfer'"/>
        <!-- 显隐列按钮（复选框模式下拉菜单） -->
        <el-dropdown trigger="click" :hide-on-click="false" style="padding-left: 12px" v-if="showColumnsType == 'checkbox'">
          <el-button circle icon="Menu" />
          <template #dropdown>
            <el-dropdown-menu>
              <template v-for="item in columns" :key="item.key">
                <el-dropdown-item>
                  <el-checkbox :checked="item.visible" @change="checkboxChange($event, item.label)" :label="item.label" />
                </el-dropdown-item>
              </template>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-tooltip>
    </el-row>
    <!-- 穿梭框弹窗（显隐列配置） -->
    <el-dialog :title="title" v-model="open" append-to-body>
      <el-transfer
        :titles="['显示', '隐藏']"
        v-model="value"
        :data="columns"
        @change="dataChange"
      ></el-transfer>
    </el-dialog>
  </div>
</template>

<script setup>
const props = defineProps({
  // 是否显示检索条件
  showSearch: {
    type: Boolean,
    default: true,
  },
  // 显隐列信息数组
  columns: {
    type: Array,
  },
  // 是否显示检索图标
  search: {
    type: Boolean,
    default: true,
  },
  // 显隐列类型（transfer 穿梭框 / checkbox 复选框）
  showColumnsType: {
    type: String,
    default: "checkbox",
  },
  // 右外边距
  gutter: {
    type: Number,
    default: 10,
  },
})

// 定义事件
const emits = defineEmits(['update:showSearch', 'queryTable']);

// 穿梭框显隐数据
const value = ref([]);
// 弹出层标题
const title = ref("显示/隐藏");
// 是否显示弹出层
const open = ref(false);

// 计算工具栏样式
const style = computed(() => {
  const ret = {};
  if (props.gutter) {
    ret.marginRight = `${props.gutter / 2}px`;
  }
  return ret;
});

// 切换搜索栏显示/隐藏
function toggleSearch() {
  emits("update:showSearch", !props.showSearch);
}

// 触发刷新表格事件
function refresh() {
  emits("queryTable");
}

// 穿梭框数据变化时更新列可见性
function dataChange(data) {
  for (let item in props.columns) {
    const key = props.columns[item].key;
    props.columns[item].visible = !data.includes(key);
  }
}

// 打开显隐列穿梭框弹窗
function showColumn() {
  open.value = true;
}

// 穿梭框模式下初始化默认隐藏列
if (props.showColumnsType == 'transfer') {
  for (let item in props.columns) {
    if (props.columns[item].visible === false) {
      value.value.push(parseInt(item));
    }
  }
}

// 复选框模式下切换列的显隐
function checkboxChange(event, label) {
  props.columns.filter(item => item.label == label)[0].visible = event;
}

</script>

<style lang='scss' scoped>
:deep(.el-transfer__button) {
  border-radius: 50%;
  display: block;
  margin-left: 0px;
}
:deep(.el-transfer__button:first-child) {
  margin-bottom: 10px;
}
:deep(.el-dropdown-menu__item) {
  line-height: 30px;
  padding: 0 17px;
}
</style>
