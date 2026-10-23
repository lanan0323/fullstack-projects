<template>
  <div>
    <!-- 隐藏的图片上传组件（URL 模式下使用） -->
    <el-upload
      :action="uploadUrl"
      :before-upload="handleBeforeUpload"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      name="file"
      :show-file-list="false"
      :headers="headers"
      class="editor-img-uploader"
      v-if="type == 'url'"
    >
      <!-- 隐藏的上传触发按钮 -->
      <i ref="uploadRef" class="editor-img-uploader"></i>
    </el-upload>
  </div>
  <!-- 富文本编辑器容器 -->
  <div class="editor">
    <quill-editor
      ref="quillEditorRef"
      v-model:content="content"
      contentType="html"
      @textChange="(e) => $emit('update:modelValue', content)"
      :options="options"
      :style="styles"
    />
  </div>
</template>

<script setup>
// 导入 Quill 富文本编辑器组件
import { QuillEditor } from "@vueup/vue-quill";
// 导入 Quill 雪花主题样式
import "@vueup/vue-quill/dist/vue-quill.snow.css";
// 导入获取 token 的工具函数
import { getToken } from "@/utils/auth";

// 获取组件代理实例
const { proxy } = getCurrentInstance();

// Quill 编辑器引用
const quillEditorRef = ref();
// 上传接口地址
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + "/common/upload");
// 请求头（携带 token）
const headers = ref({
  Authorization: "Bearer " + getToken()
});

const props = defineProps({
  // 编辑器的内容（v-model 绑定）
  modelValue: {
    type: String,
  },
  // 编辑器高度
  height: {
    type: Number,
    default: null,
  },
  // 编辑器最小高度
  minHeight: {
    type: Number,
    default: null,
  },
  // 是否只读
  readOnly: {
    type: Boolean,
    default: false,
  },
  // 上传文件大小限制（MB）
  fileSize: {
    type: Number,
    default: 5,
  },
  // 图片上传类型（base64 或 url）
  type: {
    type: String,
    default: "url",
  }
});

// Quill 编辑器配置
const options = ref({
  theme: "snow",
  bounds: document.body,
  debug: "warn",
  modules: {
    // 工具栏配置
    toolbar: [
      ["bold", "italic", "underline", "strike"],      // 加粗 斜体 下划线 删除线
      ["blockquote", "code-block"],                   // 引用 代码块
      [{ list: "ordered" }, { list: "bullet" }],      // 有序、无序列表
      [{ indent: "-1" }, { indent: "+1" }],           // 缩进
      [{ size: ["small", false, "large", "huge"] }],  // 字体大小
      [{ header: [1, 2, 3, 4, 5, 6, false] }],        // 标题
      [{ color: [] }, { background: [] }],            // 字体颜色、背景颜色
      [{ align: [] }],                                // 对齐方式
      ["clean"],                                      // 清除文本格式
      ["link", "image", "video"]                      // 链接、图片、视频
    ],
  },
  placeholder: "请输入内容",
  readOnly: props.readOnly
});

// 计算编辑器样式（高度/最小高度）
const styles = computed(() => {
  let style = {};
  if (props.minHeight) {
    style.minHeight = `${props.minHeight}px`;
  }
  if (props.height) {
    style.height = `${props.height}px`;
  }
  return style;
});

// 编辑器内容
const content = ref("");
// 监听外部 modelValue 变化同步到编辑器
watch(() => props.modelValue, (v) => {
  if (v !== content.value) {
    content.value = v === undefined ? "<p></p>" : v;
  }
}, { immediate: true });

// URL 模式下自定义图片上传处理
onMounted(() => {
  if (props.type == 'url') {
    // 获取 Quill 实例
    let quill = quillEditorRef.value.getQuill();
    // 获取工具栏模块
    let toolbar = quill.getModule("toolbar");
    // 注册图片按钮的自定义处理
    toolbar.addHandler("image", (value) => {
      if (value) {
        // 触发隐藏的上传按钮
        proxy.$refs.uploadRef.click();
      } else {
        quill.format("image", false);
      }
    });
  }
});

// 上传前校验文件格式和大小
function handleBeforeUpload(file) {
  // 允许的图片格式
  const type = ["image/jpeg", "image/jpg", "image/png", "image/svg"];
  const isJPG = type.includes(file.type);
  // 校验文件格式
  if (!isJPG) {
    proxy.$modal.msgError(`图片格式错误!`);
    return false;
  }
  // 校验文件大小
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize;
    if (!isLt) {
      proxy.$modal.msgError(`上传文件大小不能超过 ${props.fileSize} MB!`);
      return false;
    }
  }
  return true;
}

// 上传成功回调，将图片插入编辑器
function handleUploadSuccess(res, file) {
  // 上传成功
  if (res.code == 200) {
    // 获取 Quill 实例
    let quill = toRaw(quillEditorRef.value).getQuill();
    // 获取当前光标位置
    let length = quill.selection.savedRange.index;
    // 在光标位置插入图片
    quill.insertEmbed(length, "image", import.meta.env.VITE_APP_BASE_API + res.fileName);
    // 光标移到图片后面
    quill.setSelection(length + 1);
  } else {
    proxy.$modal.msgError("图片插入失败");
  }
}

// 上传失败回调
function handleUploadError() {
  proxy.$modal.msgError("图片插入失败");
}
</script>

<style>
.editor-img-uploader {
  display: none;
}
.editor, .ql-toolbar {
  white-space: pre-wrap !important;
  line-height: normal !important;
}
.quill-img {
  display: none;
}
.ql-snow .ql-tooltip[data-mode="link"]::before {
  content: "请输入链接地址:";
}
.ql-snow .ql-tooltip.ql-editing a.ql-action::after {
  border-right: 0px;
  content: "保存";
  padding-right: 0px;
}
.ql-snow .ql-tooltip[data-mode="video"]::before {
  content: "请输入视频地址:";
}
.ql-snow .ql-picker.ql-size .ql-picker-label::before,
.ql-snow .ql-picker.ql-size .ql-picker-item::before {
  content: "14px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="small"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="small"]::before {
  content: "10px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="large"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="large"]::before {
  content: "18px";
}
.ql-snow .ql-picker.ql-size .ql-picker-label[data-value="huge"]::before,
.ql-snow .ql-picker.ql-size .ql-picker-item[data-value="huge"]::before {
  content: "32px";
}
.ql-snow .ql-picker.ql-header .ql-picker-label::before,
.ql-snow .ql-picker.ql-header .ql-picker-item::before {
  content: "文本";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="1"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="1"]::before {
  content: "标题1";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="2"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="2"]::before {
  content: "标题2";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="3"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="3"]::before {
  content: "标题3";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="4"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="4"]::before {
  content: "标题4";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="5"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="5"]::before {
  content: "标题5";
}
.ql-snow .ql-picker.ql-header .ql-picker-label[data-value="6"]::before,
.ql-snow .ql-picker.ql-header .ql-picker-item[data-value="6"]::before {
  content: "标题6";
}
.ql-snow .ql-picker.ql-font .ql-picker-label::before,
.ql-snow .ql-picker.ql-font .ql-picker-item::before {
  content: "标准字体";
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value="serif"]::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value="serif"]::before {
  content: "衬线字体";
}
.ql-snow .ql-picker.ql-font .ql-picker-label[data-value="monospace"]::before,
.ql-snow .ql-picker.ql-font .ql-picker-item[data-value="monospace"]::before {
  content: "等宽字体";
}
</style>
