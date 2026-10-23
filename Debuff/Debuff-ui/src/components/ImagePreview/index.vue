<template>
  <!-- 图片预览组件，支持点击放大预览 -->
  <el-image
    :src="`${realSrc}`"
    fit="cover"
    :style="`width:${realWidth};height:${realHeight};`"
    :preview-src-list="realSrcList"
    preview-teleported
  >
    <!-- 加载失败时显示占位图标 -->
    <template #error>
      <div class="image-slot">
        <el-icon><picture-filled /></el-icon>
      </div>
    </template>
  </el-image>
</template>

<script setup>
// 导入外链判断工具
import { isExternal } from "@/utils/validate";

const props = defineProps({
  // 图片地址（支持逗号分隔的多图）
  src: {
    type: String,
    default: ""
  },
  // 图片宽度
  width: {
    type: [Number, String],
    default: ""
  },
  // 图片高度
  height: {
    type: [Number, String],
    default: ""
  }
});

// 计算实际图片地址（相对路径拼接 API 前缀）
const realSrc = computed(() => {
  if (!props.src) {
    return;
  }
  // 取逗号分隔的第一张图
  let real_src = props.src.split(",")[0];
  // 外链直接返回
  if (isExternal(real_src)) {
    return real_src;
  }
  return import.meta.env.VITE_APP_BASE_API + real_src;
});

// 计算预览图片列表（支持多图）
const realSrcList = computed(() => {
  if (!props.src) {
    return;
  }
  // 按逗号拆分为多图数组
  let real_src_list = props.src.split(",");
  let srcList = [];
  // 遍历处理每张图片地址
  real_src_list.forEach(item => {
    if (isExternal(item)) {
      return srcList.push(item);
    }
    return srcList.push(import.meta.env.VITE_APP_BASE_API + item);
  });
  return srcList;
});

// 计算实际宽度（数字自动加 px）
const realWidth = computed(() =>
  typeof props.width == "string" ? props.width : `${props.width}px`
);

// 计算实际高度（数字自动加 px）
const realHeight = computed(() =>
  typeof props.height == "string" ? props.height : `${props.height}px`
);
</script>

<style lang="scss" scoped>
.el-image {
  border-radius: 5px;
  background-color: #ebeef5;
  box-shadow: 0 0 5px 1px #ccc;
  :deep(.el-image__inner) {
    transition: all 0.3s;
    cursor: pointer;
    &:hover {
      transform: scale(1.2);
    }
  }
  :deep(.image-slot) {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    color: #909399;
    font-size: 30px;
  }
}
</style>
