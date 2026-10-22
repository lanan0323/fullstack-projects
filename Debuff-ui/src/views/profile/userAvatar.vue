<template>
  <!-- 头像展示与编辑容器，点击打开裁剪弹窗 -->
  <div class="user-info-head" @click="editCropper()">
    <!-- 显示当前用户头像，点击触发编辑 -->
    <img :src="options.img" title="点击上传头像" class="img-circle img-lg" />
    <!-- 头像裁剪弹窗对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body @opened="modalOpened" @close="closeDialog">
      <!-- 弹窗内容行布局 -->
      <el-row>
        <!-- 左侧裁剪区域列，响应式布局 -->
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <!-- Vue图片裁剪组件，配置裁剪参数 -->
          <vue-cropper
            ref="cropper"
            :img="options.img"
            :info="true"
            :autoCrop="options.autoCrop"
            :autoCropWidth="options.autoCropWidth"
            :autoCropHeight="options.autoCropHeight"
            :fixedBox="options.fixedBox"
            :outputType="options.outputType"
            @realTime="realTime"
            v-if="visible"
          />
        </el-col>
        <!-- 右侧预览区域列 -->
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <!-- 头像预览区域 -->
          <div class="avatar-upload-preview">
            <!-- 实时预览裁剪后的头像效果 -->
            <img :src="options.previews.url" :style="options.previews.img" />
          </div>
        </el-col>
      </el-row>
      <!-- 换行间隔 -->
      <br />
      <!-- 操作按钮行布局 -->
      <el-row>
        <!-- 选择图片按钮列 -->
        <el-col :lg="2" :md="2">
          <!-- 文件上传组件，自定义上传逻辑 -->
          <el-upload
            action="#"
            :http-request="requestUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <!-- 选择文件按钮 -->
            <el-button>
              选择
              <!-- 上传图标 -->
              <el-icon class="el-icon--right"><Upload /></el-icon>
            </el-button>
          </el-upload>
        </el-col>
        <!-- 放大按钮列 -->
        <el-col :lg="{ span: 1, offset: 2 }" :md="2">
          <!-- 放大按钮，点击放大图片 -->
          <el-button icon="Plus" @click="changeScale(1)"></el-button>
        </el-col>
        <!-- 缩小按钮列 -->
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <!-- 缩小按钮，点击缩小图片 -->
          <el-button icon="Minus" @click="changeScale(-1)"></el-button>
        </el-col>
        <!-- 左旋转按钮列 -->
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <!-- 左旋转按钮，点击向左旋转图片 -->
          <el-button icon="RefreshLeft" @click="rotateLeft()"></el-button>
        </el-col>
        <!-- 右旋转按钮列 -->
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <!-- 右旋转按钮，点击向右旋转图片 -->
          <el-button icon="RefreshRight" @click="rotateRight()"></el-button>
        </el-col>
        <!-- 提交按钮列 -->
        <el-col :lg="{ span: 2, offset: 6 }" :md="2">
          <!-- 提交按钮，点击上传裁剪后的头像 -->
          <el-button type="primary" @click="uploadImg()">提 交</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup>
// 导入vue-cropper组件样式
import "vue-cropper/dist/index.css";
// 导入vue-cropper图片裁剪组件
import { VueCropper } from "vue-cropper";
// 导入上传头像API方法
import { uploadAvatar } from "@/api/system/user";
// 导入用户状态管理模块
import useUserStore from "@/store/modules/user";

// 获取用户状态管理实例
const userStore = useUserStore();
// 获取当前组件实例的代理对象
const { proxy } = getCurrentInstance();

// 弹窗显示状态
const open = ref(false);
// 裁剪组件显示状态
const visible = ref(false);
// 弹窗标题
const title = ref("修改头像");

//图片裁剪数据
// 裁剪配置选项
const options = reactive({
  // 裁剪图片的地址
  img: userStore.avatar,     // 裁剪图片的地址
  // 是否默认生成截图框
  autoCrop: true,            // 是否默认生成截图框
  // 默认生成截图框宽度
  autoCropWidth: 200,        // 默认生成截图框宽度
  // 默认生成截图框高度
  autoCropHeight: 200,       // 默认生成截图框高度
  // 固定截图框大小 不允许改变
  fixedBox: true,            // 固定截图框大小 不允许改变
  // 默认生成截图为PNG格式
  outputType: "png",         // 默认生成截图为PNG格式
  // 文件名称
  filename: 'avatar',        // 文件名称
  //预览数据
  previews: {}               //预览数据
});

/** 编辑头像 */
// 打开头像编辑弹窗
function editCropper() {
  // 设置弹窗为显示状态
  open.value = true;
}
/** 打开弹出层结束时的回调 */
// 弹窗打开完成后的回调
function modalOpened() {
  // 设置裁剪组件为可见
  visible.value = true;
}
/** 覆盖默认上传行为 */
// 覆盖默认上传行为，不做任何操作
function requestUpload() {}
/** 向左旋转 */
// 向左旋转图片
function rotateLeft() {
  // 调用裁剪组件的左旋转方法
  proxy.$refs.cropper.rotateLeft();
}
/** 向右旋转 */
// 向右旋转图片
function rotateRight() {
  // 调用裁剪组件的右旋转方法
  proxy.$refs.cropper.rotateRight();
}
/** 图片缩放 */
// 图片缩放方法
function changeScale(num) {
  // 默认缩放比例为1
  num = num || 1;
  // 调用裁剪组件的缩放方法
  proxy.$refs.cropper.changeScale(num);
}
/** 上传预处理 */
// 上传前文件校验和预处理
function beforeUpload(file) {
  // 如果文件类型不是图片
  if (file.type.indexOf("image/") == -1) {
    // 提示文件格式错误
    proxy.$modal.msgError("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。");
  } else {
    // 创建文件读取器
    const reader = new FileReader();
    // 以DataURL方式读取文件
    reader.readAsDataURL(file);
    // 文件读取完成后的回调
    reader.onload = () => {
      // 将读取结果设置为裁剪图片地址
      options.img = reader.result;
      // 保存原始文件名
      options.filename = file.name;
    };
  }
}
/** 上传图片 */
// 上传裁剪后的头像图片
function uploadImg() {
  // 获取裁剪后的图片Blob数据
  proxy.$refs.cropper.getCropBlob(data => {
    // 创建FormData对象用于上传文件
    let formData = new FormData();
    // 将裁剪数据追加到FormData中
    formData.append("avatarfile", data, options.filename);
    // 调用上传头像API
    uploadAvatar(formData).then(response => {
      // 关闭弹窗
      open.value = false;
      // 拼接完整图片URL并更新显示
      options.img = import.meta.env.VITE_APP_BASE_API + response.imgUrl;
      // 更新用户状态管理中的头像地址
      userStore.avatar = options.img;
      // 显示修改成功提示
      proxy.$modal.msgSuccess("修改成功");
      // 隐藏裁剪组件
      visible.value = false;
    });
  });
}
/** 实时预览 */
// 实时更新裁剪预览数据
function realTime(data) {
  // 将预览数据赋值到options
  options.previews = data;
}
/** 关闭窗口 */
// 关闭弹窗时的清理操作
function closeDialog() {
  // 恢复为原始头像地址
  options.img = userStore.avatar;
  // 隐藏裁剪组件
  options.visible = false;
}
</script>

<style lang='scss' scoped>
/* 头像展示区域样式 */
.user-info-head {
  /* 相对定位，用于悬浮效果 */
  position: relative;
  /* 行内块级元素 */
  display: inline-block;
  /* 固定高度120px */
  height: 120px;
}

/* 头像悬浮遮罩层样式 */
.user-info-head:hover:after {
  /* 显示加号内容 */
  content: "+";
  /* 绝对定位覆盖整个区域 */
  position: absolute;
  /* 左侧对齐 */
  left: 0;
  /* 右侧对齐 */
  right: 0;
  /* 顶部对齐 */
  top: 0;
  /* 底部对齐 */
  bottom: 0;
  /* 加号颜色为浅灰 */
  color: #eee;
  /* 半透明黑色背景 */
  background: rgba(0, 0, 0, 0.5);
  /* 加号字体大小24px */
  font-size: 24px;
  /* 正常字体样式 */
  font-style: normal;
  /* Webkit字体平滑 */
  -webkit-font-smoothing: antialiased;
  /* Mozilla字体平滑 */
  -moz-osx-font-smoothing: grayscale;
  /* 鼠标手型 */
  cursor: pointer;
  /* 行高110px实现垂直居中 */
  line-height: 110px;
  /* 圆形圆角 */
  border-radius: 50%;
}
</style>
