<template>
  <!-- 图片上传组件容器 -->
  <div class="component-upload-image">
    <!-- Element 图片上传组件（卡片模式） -->
    <el-upload
      multiple
      :action="uploadImgUrl"
      list-type="picture-card"
      :on-success="handleUploadSuccess"
      :before-upload="handleBeforeUpload"
      :limit="limit"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      ref="imageUpload"
      :before-remove="handleDelete"
      :show-file-list="true"
      :headers="headers"
      :file-list="fileList"
      :on-preview="handlePictureCardPreview"
      :class="{ hide: fileList.length >= limit }"
    >
      <!-- 添加图片按钮 -->
      <el-icon class="avatar-uploader-icon"><plus /></el-icon>
    </el-upload>
    <!-- 上传提示信息 -->
    <div class="el-upload__tip" v-if="showTip">
      请上传
      <template v-if="fileSize">
        大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b>
      </template>
      <template v-if="fileType">
        格式为 <b style="color: #f56c6c">{{ fileType.join("/") }}</b>
      </template>
      的文件
    </div>

    <!-- 图片预览弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="预览"
      width="800px"
      append-to-body
    >
      <img
        :src="dialogImageUrl"
        style="display: block; max-width: 100%; margin: 0 auto"
      />
    </el-dialog>
  </div>
</template>

<script setup>
// 导入获取 token 的工具函数
import { getToken } from "@/utils/auth";

const props = defineProps({
  // 双向绑定的值
  modelValue: [String, Object, Array],
  // 图片数量限制
  limit: {
    type: Number,
    default: 5,
  },
  // 文件大小限制（MB）
  fileSize: {
    type: Number,
    default: 5,
  },
  // 允许的图片类型
  fileType: {
    type: Array,
    default: () => ["png", "jpg", "jpeg"],
  },
  // 是否显示上传提示
  isShowTip: {
    type: Boolean,
    default: true
  },
});

// 获取组件代理实例
const { proxy } = getCurrentInstance();
// 定义事件
const emit = defineEmits();
// 正在上传的图片计数
const number = ref(0);
// 上传成功的图片列表
const uploadList = ref([]);
// 预览图片地址
const dialogImageUrl = ref("");
// 预览弹窗是否可见
const dialogVisible = ref(false);
// API 基础路径
const baseUrl = import.meta.env.VITE_APP_BASE_API;
// 图片上传接口地址
const uploadImgUrl = ref(import.meta.env.VITE_APP_BASE_API + "/common/upload");
// 请求头（携带 token）
const headers = ref({ Authorization: "Bearer " + getToken() });
// 图片列表
const fileList = ref([]);
// 是否显示提示
const showTip = computed(
  () => props.isShowTip && (props.fileType || props.fileSize)
);

// 监听 modelValue 变化，同步到图片列表
watch(() => props.modelValue, val => {
  if (val) {
    // 将值转为数组
    const list = Array.isArray(val) ? val : props.modelValue.split(",");
    // 将数组转为对象数组
    fileList.value = list.map(item => {
      if (typeof item === "string") {
        // 非外链且不含基础路径的拼接前缀
        if (!isExternalImageUrl(item) && item.indexOf(baseUrl) === -1) {
          item = { name: baseUrl + item, url: baseUrl + item };
        } else {
          item = { name: item, url: item };
        }
      }
      return item;
    });
  } else {
    fileList.value = [];
    return [];
  }
},{ deep: true, immediate: true });

// 判断是否为外部图片 URL（http 链接或 base64）
function isExternalImageUrl(value) {
  return /^(?:https?:)?\/\//i.test(value) || value.startsWith("data:image/");
}

// 上传前校验图片格式和大小
function handleBeforeUpload(file) {
  let isImg = false;
  if (props.fileType.length) {
    let fileExtension = "";
    if (file.name.lastIndexOf(".") > -1) {
      fileExtension = file.name.slice(file.name.lastIndexOf(".") + 1);
    }
    // 检查文件类型是否匹配
    isImg = props.fileType.some(type => {
      if (file.type.indexOf(type) > -1) return true;
      if (fileExtension && fileExtension.indexOf(type) > -1) return true;
      return false;
    });
  } else {
    isImg = file.type.indexOf("image") > -1;
  }
  if (!isImg) {
    proxy.$modal.msgError(
      `文件格式不正确, 请上传${props.fileType.join("/")}图片格式文件!`
    );
    return false;
  }
  // 校验文件大小
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize;
    if (!isLt) {
      proxy.$modal.msgError(`上传头像图片大小不能超过 ${props.fileSize} MB!`);
      return false;
    }
  }
  // 显示上传中加载动画
  proxy.$modal.loading("正在上传图片，请稍候...");
  number.value++;
}

// 文件数量超出限制
function handleExceed() {
  proxy.$modal.msgError(`上传文件数量不能超过 ${props.limit} 个!`);
}

// 上传成功回调
function handleUploadSuccess(res, file) {
  if (res.code === 200) {
    uploadList.value.push({ name: res.fileName, url: res.fileName });
    uploadedSuccessfully();
  } else {
    number.value--;
    proxy.$modal.closeLoading();
    proxy.$modal.msgError(res.msg);
    proxy.$refs.imageUpload.handleRemove(file);
    uploadedSuccessfully();
  }
}

// 删除图片
function handleDelete(file) {
  const findex = fileList.value.map(f => f.name).indexOf(file.name);
  if (findex > -1 && uploadList.value.length === number.value) {
    fileList.value.splice(findex, 1);
    emit("update:modelValue", listToString(fileList.value));
    return false;
  }
}

// 所有图片上传完成后的处理
function uploadedSuccessfully() {
  if (number.value > 0 && uploadList.value.length === number.value) {
    fileList.value = fileList.value.filter(f => f.url !== undefined).concat(uploadList.value);
    uploadList.value = [];
    number.value = 0;
    emit("update:modelValue", listToString(fileList.value));
    proxy.$modal.closeLoading();
  }
}

// 上传失败处理
function handleUploadError() {
  proxy.$modal.msgError("上传图片失败");
  proxy.$modal.closeLoading();
}

// 点击图片预览
function handlePictureCardPreview(file) {
  dialogImageUrl.value = file.url;
  dialogVisible.value = true;
}

// 将图片列表转为逗号分隔的 URL 字符串（去掉基础路径前缀）
function listToString(list, separator) {
  let strs = "";
  separator = separator || ",";
  for (let i in list) {
    if (undefined !== list[i].url && list[i].url.indexOf("blob:") !== 0) {
      strs += list[i].url.replace(baseUrl, "") + separator;
    }
  }
  return strs != "" ? strs.substr(0, strs.length - 1) : "";
}
</script>

<style scoped lang="scss">
// 图片数量达到上限时隐藏加号按钮
:deep(.hide .el-upload--picture-card) {
    display: none;
}
</style>
