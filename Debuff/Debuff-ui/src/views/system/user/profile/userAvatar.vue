<!-- 系统用户头像编辑组件，使用vue-cropper裁剪上传头像 -->
<template>
  <!-- 头像展示区，点击打开裁剪弹窗 -->
  <div class="user-info-head" @click="editCropper()">
    <img :src="options.img" title="点击上传头像" class="img-circle img-lg" />
    <!-- 头像裁剪弹窗 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body @opened="modalOpened" @close="closeDialog">
      <el-row>
        <!-- 左侧：裁剪器 -->
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
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
        <!-- 右侧：预览区 -->
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <div class="avatar-upload-preview">
            <img :src="options.previews.url" :style="options.previews.img" />
          </div>
        </el-col>
      </el-row>
      <br />
      <!-- 操作按钮行 -->
      <el-row>
        <!-- 选择文件按钮 -->
        <el-col :lg="2" :md="2">
          <el-upload
            action="#"
            :http-request="requestUpload"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <el-button>
              选择
              <el-icon class="el-icon--right"><Upload /></el-icon>
            </el-button>
          </el-upload>
        </el-col>
        <!-- 放大按钮 -->
        <el-col :lg="{ span: 1, offset: 2 }" :md="2">
          <el-button icon="Plus" @click="changeScale(1)"></el-button>
        </el-col>
        <!-- 缩小按钮 -->
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <el-button icon="Minus" @click="changeScale(-1)"></el-button>
        </el-col>
        <!-- 左旋转按钮 -->
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <el-button icon="RefreshLeft" @click="rotateLeft()"></el-button>
        </el-col>
        <!-- 右旋转按钮 -->
        <el-col :lg="{ span: 1, offset: 1 }" :md="2">
          <el-button icon="RefreshRight" @click="rotateRight()"></el-button>
        </el-col>
        <!-- 提交按钮 -->
        <el-col :lg="{ span: 2, offset: 6 }" :md="2">
          <el-button type="primary" @click="uploadImg()">提 交</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script setup>
// 导入vue-cropper样式和组件
import "vue-cropper/dist/index.css";
import { VueCropper } from "vue-cropper";
// 导入头像上传API
import { uploadAvatar } from "@/api/system/user";
// 导入用户状态管理
import useUserStore from "@/store/modules/user";

// 获取用户store实例
const userStore = useUserStore();
// 获取组件代理实例
const { proxy } = getCurrentInstance();

// 弹窗显示状态
const open = ref(false);
// 裁剪器可见状态
const visible = ref(false);
// 弹窗标题
const title = ref("修改头像");

// 图片裁剪配置数据
const options = reactive({
  img: userStore.avatar,     // 裁剪图片的地址
  autoCrop: true,            // 是否默认生成截图框
  autoCropWidth: 200,        // 默认生成截图框宽度
  autoCropHeight: 200,       // 默认生成截图框高度
  fixedBox: true,            // 固定截图框大小 不允许改变
  outputType: "png",         // 默认生成截图为PNG格式
  filename: 'avatar',        // 文件名称
  previews: {}               // 预览数据
});

/** 编辑头像，打开弹窗 */
function editCropper() {
  open.value = true;
}

/** 弹窗打开动画结束后的回调，显示裁剪器 */
function modalOpened() {
  visible.value = true;
}

/** 覆盖默认上传行为(空函数，使用自定义上传) */
function requestUpload() {}

/** 向左旋转 */
function rotateLeft() {
  proxy.$refs.cropper.rotateLeft();
}

/** 向右旋转 */
function rotateRight() {
  proxy.$refs.cropper.rotateRight();
}

/** 图片缩放，num为正放大，为负缩小 */
function changeScale(num) {
  num = num || 1;
  proxy.$refs.cropper.changeScale(num);
}

/** 上传前预处理，校验文件类型并读取为Base64 */
function beforeUpload(file) {
  // 校验文件是否为图片类型
  if (file.type.indexOf("image/") == -1) {
    proxy.$modal.msgError("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。");
  } else {
    // 使用FileReader读取文件为Base64
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      options.img = reader.result;       // 设置裁剪图片地址
      options.filename = file.name;      // 设置文件名
    };
  }
}

/** 上传图片，获取裁剪后的Blob数据并提交 */
function uploadImg() {
  // 获取裁剪后的图片Blob数据
  proxy.$refs.cropper.getCropBlob(data => {
    let formData = new FormData();        // 构建表单数据
    formData.append("avatarfile", data, options.filename);  // 添加头像文件
    // 调用上传头像接口
    uploadAvatar(formData).then(response => {
      open.value = false;                 // 关闭弹窗
      options.img = import.meta.env.VITE_APP_BASE_API + response.imgUrl;  // 更新头像地址
      userStore.avatar = options.img;     // 更新store中的头像
      proxy.$modal.msgSuccess("修改成功");
      visible.value = false;              // 隐藏裁剪器
    });
  });
}

/** 实时预览回调，更新预览数据 */
function realTime(data) {
  options.previews = data;
}

/** 关闭弹窗，恢复原始头像地址 */
function closeDialog() {
  options.img = userStore.avatar;
  options.visible = false;
}
</script>
