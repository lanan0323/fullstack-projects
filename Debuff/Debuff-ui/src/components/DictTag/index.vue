<template>
  <div>
    <!-- 遍历字典选项 -->
    <template v-for="(item, index) in options">
      <!-- 当前值匹配选项时显示 -->
      <template v-if="values.includes(item.value)">
        <!-- 无标签样式时使用普通 span -->
        <span
          v-if="(item.elTagType == 'default' || item.elTagType == '') && (item.elTagClass == '' || item.elTagClass == null)"
          :key="item.value"
          :index="index"
          :class="item.elTagClass"
        >{{ item.label + " " }}</span>
        <!-- 有标签样式时使用 el-tag 组件 -->
        <el-tag
          v-else
          :disable-transitions="true"
          :key="item.value + ''"
          :index="index"
          :type="item.elTagType === 'primary' ? '' : item.elTagType"
          :class="item.elTagClass"
        >{{ item.label + " " }}</el-tag>
      </template>
    </template>
    <!-- 有未匹配项且 showValue 为 true 时显示原始值 -->
    <template v-if="unmatch && showValue">
      {{ unmatchArray | handleArray }}
    </template>
  </div>
</template>

<script setup>
// 记录未匹配的项
const unmatchArray = ref([]);

const props = defineProps({
  // 字典数据数组
  options: {
    type: Array,
    default: null,
  },
  // 当前值（支持数字、字符串、数组）
  value: [Number, String, Array],
  // 当未找到匹配数据时是否显示原始值
  showValue: {
    type: Boolean,
    default: true,
  },
  // 多值分隔符
  separator: {
    type: String,
    default: ",",
  }
});

// 将当前值转为数组格式
const values = computed(() => {
  // 空值返回空数组
  if (props.value === null || typeof props.value === 'undefined' || props.value === '') return [];
  // 数组类型直接映射为字符串，否则按分隔符拆分
  return Array.isArray(props.value) ? props.value.map(item => '' + item) : String(props.value).split(props.separator);
});

// 计算是否有未匹配的项
const unmatch = computed(() => {
  unmatchArray.value = [];
  // 值为空或选项为空时不显示
  if (props.value === null || typeof props.value === 'undefined' || props.value === '' || props.options.length === 0) return false
  let unmatch = false
  // 遍历每个值检查是否匹配
  values.value.forEach(item => {
    if (!props.options.some(v => v.value === item)) {
      unmatchArray.value.push(item)
      unmatch = true
    }
  })
  return unmatch
});

// 将未匹配数组拼接为字符串
function handleArray(array) {
  if (array.length === 0) return "";
  return array.reduce((pre, cur) => {
    return pre + " " + cur;
  });
}
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
