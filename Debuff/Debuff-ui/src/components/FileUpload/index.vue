<template>
  <!-- 文件上传组件容器 -->
  <div class="upload-file">
    <!-- Element 上传组件（多选模式） -->
    <el-upload
      multiple
      :action="uploadFileUrl"
      :before-upload="handleBeforeUpload"
      :file-list="fileList"
      :limit="limit"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      :on-success="handleUploadSuccess"
      :show-file-list="false"
      :headers="headers"
      class="upload-file-uploader"
      ref="fileUpload"
    >
      <!-- 上传按钮 -->
      <el-button type="primary">选取文件</el-button>
    </el-upload>
    <!-- 上传提示信息 -->
    <div class="el-upload__tip" v-if="showTip">
      请上传
      <template v-if="fileSize"> 大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b> </template>
      <template v-if="fileType"> 格式为 <b style="color: #f56c6c">{{ fileType.join("/") }}</b> </template>
      的文件
    </div>
    <!-- 已上传文件列表（带动画过渡） -->
    <transition-group class="upload-file-list el-upload-list el-upload-list--text" name="el-fade-in-linear" tag="ul">
      <li :key="file.uid" class="el-upload-list__item ele-upload-list__item-content" v-for="(file, index) in fileList">
        <!-- 文件链接 -->
        <el-link :href="`${baseUrl}${file.url}`" :underline="false" target="_blank">
          <span class="el-icon-document"> {{ getFileName(file.name) }} </span>
        </el-link>
        <!-- 删除按钮 -->
        <div class="ele-upload-list__item-content-action">
          <el-link :underline="false" @click="handleDelete(index)" type="danger">删除</el-link>
        </div>
      </li>
    </transition-group>
  </div>
</template>

<script setup>
// 导入获取 token 的工具函数
import { getToken } from "@/utils/auth";

const props = defineProps({
  // 双向绑定的值
  modelValue: [String, Object, Array],
  // 上传数量限制
  limit: {
    type: Number,
    default: 5,
  },
  // 文件大小限制（MB）
  fileSize: {
    type: Number,
    default: 5,
  },
  // 允许的文件类型
  fileType: {
    type: Array,
    default: () => ["doc", "xls", "ppt", "txt", "pdf"],
  },
  // 是否显示上传提示
  isShowTip: {
    type: Boolean,
    default: true
  }
});

// 获取组件代理实例
const { proxy } = getCurrentInstance();
// 定义事件
const emit = defineEmits();
// 正在上传的文件计数
const number = ref(0);
// 上传成功的文件列表
const uploadList = ref([]);
// API 基础路径
const baseUrl = import.meta.env.VITE_APP_BASE_API;
// 文件上传接口地址
const uploadFileUrl = ref(import.meta.env.VITE_APP_BASE_API + "/common/upload");
// 请求头（携带 token）
const headers = ref({ Authorization: "Bearer " + getToken() });
// 文件列表
const fileList = ref([]);
// 是否显示提示
const showTip = computed(
  () => props.isShowTip && (props.fileType || props.fileSize)
);

// 监听 modelValue 变化，同步到文件列表
watch(() => props.modelValue, val => {
  if (val) {
    let temp = 1;
    // 将值转为数组
    const list = Array.isArray(val) ? val : props.modelValue.split(',');
    // 将数组转为对象数组
    fileList.value = list.map(item => {
      if (typeof item === "string") {
        item = { name: item, url: item };
      }
      item.uid = item.uid || new Date().getTime() + temp++;
      return item;
    });
  } else {
    fileList.value = [];
    return [];
  }
},{ deep: true, immediate: true });

// 上传前校验文件格式和大小
function handleBeforeUpload(file) {
  // 校验文件类型
  if (props.fileType.length) {
    const fileName = file.name.split('.');
    const fileExt = fileName[fileName.length - 1];
    const isTypeOk = props.fileType.indexOf(fileExt) >= 0;
    if (!isTypeOk) {
      proxy.$modal.msgError(`文件格式不正确, 请上传${props.fileType.join("/")}格式文件!`);
      return false;
    }
  }
  // 校验文件大小
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize;
    if (!isLt) {
      proxy.$modal.msgError(`上传文件大小不能超过 ${props.fileSize} MB!`);
      return false;
    }
  }
  // 显示上传中加载动画
  proxy.$modal.loading("正在上传文件，请稍候...");
  number.value++;
  return true;
}

// 文件数量超出限制
function handleExceed() {
  proxy.$modal.msgError(`上传文件数量不能超过 ${props.limit} 个!`);
}

// 上传失败处理
function handleUploadError(err) {
  proxy.$modal.msgError("上传文件失败");
}

// 上传成功回调
function handleUploadSuccess(res, file) {
  if (res.code === 200) {
    // 上传成功添加到成功列表
    uploadList.value.push({ name: res.fileName, url: res.fileName });
    uploadedSuccessfully();
  } else {
    number.value--;
    proxy.$modal.closeLoading();
    proxy.$modal.msgError(res.msg);
    proxy.$refs.fileUpload.handleRemove(file);
    uploadedSuccessfully();
  }
}

// 删除文件
function handleDelete(index) {
  fileList.value.splice(index, 1);
  emit("update:modelValue", listToString(fileList.value));
}

// 所有文件上传完成后的处理
function uploadedSuccessfully() {
  // 所有文件都上传完毕
  if (number.value > 0 && uploadList.value.length === number.value) {
    // 合并文件列表
    fileList.value = fileList.value.filter(f => f.url !== undefined).concat(uploadList.value);
    uploadList.value = [];
    number.value = 0;
    emit("update:modelValue", listToString(fileList.value));
    proxy.$modal.closeLoading();
  }
}

// 获取文件名（如果是 URL 则取最后一段）
function getFileName(name) {
  if (name.lastIndexOf("/") > -1) {
    return name.slice(name.lastIndexOf("/") + 1);
  } else {
    return name;
  }
}

// 将文件列表转为逗号分隔的 URL 字符串
function listToString(list, separator) {
  let strs = "";
  separator = separator || ",";
  for (let i in list) {
    if (list[i].url) {
      strs += list[i].url + separator;
    }
  }
  return strs != '' ? strs.substr(0, strs.length - 1) : '';
}
</script>

<style scoped lang="scss">
.upload-file-uploader {
  margin-bottom: 5px;
}
.upload-file-list .el-upload-list__item {
  border: 1px solid #e4e7ed;
  line-height: 2;
  margin-bottom: 10px;
  position: relative;
}
.upload-file-list .ele-upload-list__item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: inherit;
}
.ele-upload-list__item-content-action .el-link {
  margin-right: 10px;
}
</style>
