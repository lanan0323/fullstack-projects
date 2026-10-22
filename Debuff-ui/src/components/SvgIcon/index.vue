<template>
  <!-- SVG 图标组件，通过 xlink:href 引用 symbol 图标 -->
  <svg :class="svgClass" aria-hidden="true">
    <use :xlink:href="iconName" :fill="color" />
  </svg>
</template>

<script>
export default defineComponent({
  props: {
    // 图标类名（对应 SVG 文件名）
    iconClass: {
      type: String,
      required: true
    },
    // 额外的 CSS 类名
    className: {
      type: String,
      default: ''
    },
    // 图标颜色
    color: {
      type: String,
      default: ''
    },
  },
  setup(props) {
    return {
      // 计算图标引用 ID
      iconName: computed(() => `#icon-${props.iconClass}`),
      // 计算 SVG 的 CSS 类名
      svgClass: computed(() => {
        if (props.className) {
          return `svg-icon ${props.className}`
        }
        return 'svg-icon'
      })
    }
  }
})
</script>

<style scope lang="scss">
.sub-el-icon,
.nav-icon {
  display: inline-block;
  font-size: 15px;
  margin-right: 12px;
  position: relative;
}

.svg-icon {
  width: 1em;
  height: 1em;
  position: relative;
  fill: currentColor;
  vertical-align: -2px;
}
</style>
