<template>
  <!-- 401无权限错误页面根容器 -->
  <div class="errPage-container">
    <!-- 返回按钮，点击返回上一页或首页 -->
    <el-button icon="arrow-left" class="pan-back-btn" @click="back">
      返回
    </el-button>
    <el-row>
      <!-- 左侧文字描述区域 -->
      <el-col :span="12">
        <!-- 401错误大标题 -->
        <h1 class="text-jumbo text-ginormous">
          401错误!
        </h1>
        <!-- 错误副标题 -->
        <h2>您没有访问权限！</h2>
        <!-- 错误详细说明 -->
        <h6>对不起，您没有访问权限，请不要进行非法操作！您可以返回主页面</h6>
        <!-- 快捷操作链接列表 -->
        <ul class="list-unstyled">
          <li class="link-type">
            <!-- 返回首页链接 -->
            <router-link to="/">
              回首页
            </router-link>
          </li>
        </ul>
      </el-col>
      <!-- 右侧错误插图区域 -->
      <el-col :span="12">
        <!-- 401错误动画GIF图片 -->
        <img :src="errGif" width="313" height="428" alt="Girl has dropped her ice cream.">
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// 导入401错误页面的GIF图片资源
import errImage from "@/assets/401_images/401.gif";

// 获取当前组件实例的代理对象
let { proxy } = getCurrentInstance();

// 错误GIF图片URL，添加时间戳防止缓存
const errGif = ref(errImage + "?" + +new Date());

// 返回上一页，如果路由参数标记了noGoBack则直接回首页
function back() {
  // 判断路由查询参数中是否标记了不允许后退
  if (proxy.$route.query.noGoBack) {
    // 直接跳转到首页
    proxy.$router.push({ path: "/" });
  } else {
    // 返回浏览器历史记录上一页
    proxy.$router.go(-1);
  }
}
</script>

<style lang="scss" scoped>
.errPage-container {
  width: 800px;
  max-width: 100%;
  margin: 100px auto;
  .pan-back-btn {
    background: #008489;
    color: #fff;
    border: none !important;
  }
  .pan-gif {
    margin: 0 auto;
    display: block;
  }
  .pan-img {
    display: block;
    margin: 0 auto;
    width: 100%;
  }
  .text-jumbo {
    font-size: 60px;
    font-weight: 700;
    color: #484848;
  }
  .list-unstyled {
    font-size: 14px;
    li {
      padding-bottom: 5px;
    }
    a {
      color: #008489;
      text-decoration: none;
      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>
