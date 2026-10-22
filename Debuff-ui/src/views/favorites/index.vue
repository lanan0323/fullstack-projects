<template>
  <!-- 收藏页面根容器 -->
  <div class="fav-page">
    <!-- 页头区域 -->
    <div class="fav-hero">
      <!-- 英文标识 -->
      <div class="hero-eyebrow">MY FAVORITES</div>
      <!-- 页面主标题 -->
      <h1 class="hero-title">我的收藏</h1>
      <!-- 收藏总数提示 -->
      <p class="hero-sub">共 {{ total }} 件收藏饰品</p>
    </div>

    <!-- 收藏卡片网格列表 -->
    <div v-loading="loading" class="fav-grid">
      <!-- 遍历收藏列表渲染卡片 -->
      <div v-for="item in favList" :key="item.id" class="fav-card">
        <!-- 卡片图片区域 -->
        <div class="fav-card-img">
          <!-- 饰品图片，使用resolveImgUrl处理路径 -->
          <el-image :src="resolveImgUrl(item.imageUrl)" fit="cover" style="width:100%;height:100%">
            <!-- 图片加载失败时的占位 -->
            <template #error><div class="img-empty">NO IMG</div></template>
          </el-image>
          <!-- 已失效状态标签（非在售时显示） -->
          <span v-if="item.listingStatus !== '0'" class="status-tag expired">已失效</span>
          <!-- 在售状态标签 -->
          <span v-else class="status-tag active">在售</span>
        </div>
        <!-- 卡片内容区域 -->
        <div class="fav-card-body">
          <!-- 饰品名称 -->
          <h3 class="fav-skin-name">{{ item.skinName || '未知饰品' }}</h3>
          <!-- 武器类型和品质元信息 -->
          <div class="fav-meta">
            <!-- 武器名称（有值时显示） -->
            <span v-if="item.weaponName" class="fav-weapon">{{ item.weaponName }}</span>
            <!-- 品质名称（有值时显示，颜色由后端字段决定） -->
            <span v-if="item.qualityName" class="fav-quality" :style="{ color: item.qualityColor }">{{ item.qualityName }}</span>
          </div>
          <!-- 价格和时间行 -->
          <div class="fav-price-row">
            <!-- 饰品价格，空值显示-- -->
            <span class="fav-price">{{ item.price != null ? '¥' + Number(item.price).toFixed(2) : '--' }}</span>
            <!-- 收藏时间 -->
            <span class="fav-time">{{ item.createTime }}</span>
          </div>
          <!-- 操作按钮区 -->
          <div class="fav-actions">
            <!-- 取消收藏按钮 -->
            <button class="btn-unfav" @click="handleRemove(item)">取消收藏</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态提示 -->
    <div v-if="!loading && favList.length === 0" class="empty-state">
      <!-- 空状态大标识 -->
      <div class="empty-mark">NO FAVORITES</div>
      <!-- 空状态提示文字 -->
      <p>还没有收藏任何饰品，去市场逛逛吧</p>
    </div>

    <!-- 分页组件 -->
    <div class="pager">
      <!-- 分页控件，绑定总数/页码/每页条数 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>
  </div>
</template>

<script setup name="UserFavorites">
// 引入Vue组合式API
import { ref, reactive, onMounted, onActivated } from 'vue'
// 引入Element Plus消息提示和确认弹框
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入收藏列表和取消收藏API
import { listMyFavorites, removeFavorite } from '@/api/favorite'
// 引入图片URL解析工具
import { resolveImgUrl } from '@/utils/img'

// 加载状态
const loading = ref(false)
// 收藏列表数据
const favList = ref([])
// 收藏总条数
const total = ref(0)

// 查询参数（分页）
const queryParams = reactive({
  // 当前页码
  pageNum: 1,
  // 每页条数
  pageSize: 10
})

// 获取收藏列表
const getList = async () => {
  // 开启加载状态
  loading.value = true
  try {
    // 调用收藏列表接口
    const res = await listMyFavorites(queryParams)
    // 更新收藏列表
    favList.value = res.rows
    // 更新总条数
    total.value = res.total
  } catch (e) {
    // 错误已由 request.js 统一提示
  } finally {
    // 关闭加载状态
    loading.value = false
  }
}

// 取消收藏处理
const handleRemove = (item) => {
  // 弹出确认对话框
  ElMessageBox.confirm('确定取消收藏该饰品吗？', '提示', {
    // 确认按钮文字
    confirmButtonText: '确定',
    // 取消按钮文字
    cancelButtonText: '取消',
    // 弹框类型
    type: 'warning'
  }).then(async () => {
    try {
      // 调用取消收藏接口
      await removeFavorite(item.id)
      // 提示取消成功
      ElMessage.success('已取消收藏')
      // 重新加载列表
      await getList()
    } catch (e) {
      // 错误已由 request.js 统一提示
    }
  }).catch(() => {})
  // 点击取消时静默处理
}

// 组件挂载时加载数据
onMounted(() => {
  // 获取收藏列表
  getList()
})
// keep-alive缓存激活时重新加载数据
onActivated(() => {
  getList()
})
</script>

<style scoped>
/* 收藏页面根样式 */
.fav-page {
  /* 背景色变量 */
  --bg: #0d1220;
  /* 卡片背景变量 */
  --card: #141b2d;
  /* 卡片悬停背景变量 */
  --card-hover: #1a2338;
  /* 边框颜色变量 */
  --border: #232e45;
  /* 主文本颜色变量 */
  --text: #e9edf6;
  /* 次要文本颜色变量 */
  --muted: #8f9ab3;
  /* 价格颜色变量 */
  --price: #ff6b5e;
  /* 最小高度撑满视口 */
  min-height: calc(100vh - 84px);
  /* 多层渐变背景 */
  background:
    radial-gradient(1000px 400px at 80% -10%, rgba(77, 159, 255, 0.08), transparent 60%),
    radial-gradient(800px 300px at 10% 0%, rgba(255, 107, 94, 0.06), transparent 60%),
    var(--bg);
  /* 内边距 */
  padding: 24px 28px 32px;
  /* Element Plus 暗色变量覆盖 */
  --el-bg-color: var(--card);
  --el-bg-color-overlay: var(--card-hover);
  --el-border-color: var(--border);
  --el-border-color-light: var(--border);
  --el-text-color-primary: var(--text);
}

/* ---------- 页头 ---------- */
/* 页头区域 */
.fav-hero {
  /* 下外边距 */
  margin-bottom: 24px;
}
/* 英文标识 */
.hero-eyebrow {
  /* 字号 */
  font-size: 11px;
  /* 字间距 */
  letter-spacing: 4px;
  /* 蓝色 */
  color: #4d9fff;
  /* 粗体 */
  font-weight: 700;
}
/* 主标题 */
.hero-title {
  /* 外边距 */
  margin: 6px 0 4px;
  /* 字号 */
  font-size: 26px;
  /* 字重 */
  font-weight: 800;
  /* 主文本颜色 */
  color: var(--text);
}
/* 副标题 */
.hero-sub {
  /* 字号 */
  font-size: 13px;
  /* 次要文本颜色 */
  color: var(--muted);
}

/* ---------- 卡片网格 ---------- */
/* 网格容器 */
.fav-grid {
  /* 网格布局 */
  display: grid;
  /* 自适应列数，最小240px */
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  /* 间距 */
  gap: 16px;
}
/* 单个收藏卡片 */
.fav-card {
  /* 卡片背景 */
  background: var(--card);
  /* 边框 */
  border: 1px solid var(--border);
  /* 圆角 */
  border-radius: 10px;
  /* 溢出隐藏 */
  overflow: hidden;
  /* 过渡动画 */
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}
/* 卡片悬停效果 */
.fav-card:hover {
  /* 上移 */
  transform: translateY(-4px);
  /* 边框高亮 */
  border-color: #35507a;
  /* 阴影 */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.45);
}
/* 卡片图片区域 */
.fav-card-img {
  /* 相对定位（用于状态标签） */
  position: relative;
  /* 固定高度 */
  height: 160px;
  /* 深色背景（图片加载前） */
  background: #0a0f1a;
}
/* 图片组件块级显示 */
.fav-card-img :deep(.el-image) {
  /* 块级 */
  display: block;
}
/* 图片加载失败占位 */
.img-empty {
  /* 撑满高度 */
  height: 100%;
  /* 弹性居中 */
  display: flex;
  align-items: center;
  justify-content: center;
  /* 暗灰色文字 */
  color: #4a5570;
  /* 小字号 */
  font-size: 12px;
  /* 字间距 */
  letter-spacing: 2px;
}

/* 状态标签通用样式 */
.status-tag {
  /* 绝对定位 */
  position: absolute;
  /* 右上角 */
  top: 8px;
  right: 8px;
  /* 字号 */
  font-size: 11px;
  /* 内边距 */
  padding: 2px 8px;
  /* 圆角 */
  border-radius: 4px;
  /* 字重 */
  font-weight: 600;
}
/* 在售状态标签 */
.status-tag.active {
  /* 绿色半透明背景 */
  background: rgba(61, 220, 151, 0.15);
  /* 绿色文字 */
  color: #3ddc97;
  /* 绿色半透明边框 */
  border: 1px solid rgba(61, 220, 151, 0.3);
}
/* 已失效状态标签 */
.status-tag.expired {
  /* 灰色半透明背景 */
  background: rgba(143, 154, 179, 0.12);
  /* 灰色文字 */
  color: #8f9ab3;
  /* 灰色半透明边框 */
  border: 1px solid rgba(143, 154, 179, 0.2);
}

/* 卡片内容区域 */
.fav-card-body {
  /* 内边距 */
  padding: 14px;
}
/* 饰品名称 */
.fav-skin-name {
  /* 外边距 */
  margin: 0 0 8px;
  /* 字号 */
  font-size: 14px;
  /* 字重 */
  font-weight: 600;
  /* 主文本颜色 */
  color: var(--text);
  /* 单行溢出省略 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
/* 元信息区（武器+品质） */
.fav-meta {
  /* 弹性布局 */
  display: flex;
  /* 间距 */
  gap: 8px;
  /* 下外边距 */
  margin-bottom: 10px;
  /* 字号 */
  font-size: 12px;
}
/* 武器名称 */
.fav-weapon {
  /* 次要文本颜色 */
  color: var(--muted);
}
/* 品质名称 */
.fav-quality {
  /* 半粗体 */
  font-weight: 500;
}
/* 价格行 */
.fav-price-row {
  /* 弹性布局 */
  display: flex;
  /* 两端对齐 */
  justify-content: space-between;
  /* 垂直居中 */
  align-items: center;
  /* 下外边距 */
  margin-bottom: 12px;
}
/* 价格文字 */
.fav-price {
  /* 字号 */
  font-size: 16px;
  /* 粗体 */
  font-weight: 700;
  /* 价格颜色 */
  color: var(--price);
}
/* 收藏时间 */
.fav-time {
  /* 小字号 */
  font-size: 11px;
  /* 次要文本颜色 */
  color: var(--muted);
}
/* 操作按钮区 */
.fav-actions {
  /* 弹性布局 */
  display: flex;
  /* 右对齐 */
  justify-content: flex-end;
}
/* 取消收藏按钮 */
.btn-unfav {
  /* 红色半透明边框 */
  border: 1px solid rgba(255, 107, 94, 0.35);
  /* 红色半透明背景 */
  background: rgba(255, 107, 94, 0.08);
  /* 红色文字 */
  color: #ff6b5e;
  /* 字号 */
  font-size: 12px;
  /* 内边距 */
  padding: 4px 12px;
  /* 圆角 */
  border-radius: 6px;
  /* 手型光标 */
  cursor: pointer;
  /* 过渡动画 */
  transition: all 0.18s;
}
/* 取消收藏按钮悬停 */
.btn-unfav:hover {
  /* 加深背景 */
  background: rgba(255, 107, 94, 0.2);
  /* 加深边框 */
  border-color: #ff6b5e;
}

/* ---------- 空状态 ---------- */
/* 空状态容器 */
.empty-state {
  /* 居中 */
  text-align: center;
  /* 上下内边距 */
  padding: 60px 0;
  /* 次要文本颜色 */
  color: var(--muted);
}
/* 空状态大标识 */
.empty-mark {
  /* 大字号 */
  font-size: 26px;
  /* 宽字间距 */
  letter-spacing: 8px;
  /* 暗灰色 */
  color: #35405c;
  /* 粗体 */
  font-weight: 700;
}
/* 空状态提示段落 */
.empty-state p {
  /* 上外边距 */
  margin-top: 10px;
  /* 字号 */
  font-size: 13px;
}

/* ---------- 分页 ---------- */
/* 分页容器 */
.pager {
  /* 上外边距 */
  margin-top: 16px;
  /* 弹性布局 */
  display: flex;
  /* 右对齐 */
  justify-content: flex-end;
}
</style>

<style>
/* 分页暗色主题 */
/* 分页容器背景透明 */
.fav-page .pagination-container { background: transparent !important; }
/* 分页组件变量覆盖 */
.fav-page .el-pagination {
  /* 透明背景 */
  --el-pagination-bg-color: transparent;
  /* 文本颜色 */
  --el-pagination-text-color: #c2cad9;
  /* 按钮颜色 */
  --el-pagination-button-color: #c2cad9;
  /* 悬停颜色 */
  --el-pagination-hover-color: #4d9fff;
  /* 禁用状态背景 */
  --el-pagination-button-disabled-bg-color: transparent;
}
/* 页码按钮样式 */
.fav-page .el-pagination .el-pager li { background: transparent !important; color: #c2cad9 !important; }
/* 当前激活页码 */
.fav-page .el-pagination .el-pager li.is-active { color: #4d9fff !important; font-weight: 700; }
/* 前后翻页按钮 */
.fav-page .el-pagination button { background: transparent !important; color: #c2cad9 !important; }
/* 跳转文字颜色 */
.fav-page .el-pagination .el-pagination__jump { color: #c2cad9 !important; }
/* 跳转输入框样式 */
.fav-page .el-pagination .el-pagination__jump .el-input__wrapper { background-color: #1a2338 !important; box-shadow: 0 0 0 1px #2a3654 inset !important; }
/* 跳转输入框文字 */
.fav-page .el-pagination .el-pagination__jump .el-input__inner { color: #c2cad9 !important; }
/* 每页条数选择器 */
.fav-page .el-pagination .el-select .el-input__wrapper { background-color: #1a2338 !important; box-shadow: 0 0 0 1px #2a3654 inset !important; }
/* 选择器文字 */
.fav-page .el-pagination .el-select .el-input__inner { color: #c2cad9 !important; }
/* 所有子元素背景透明 */
.fav-page .el-pagination * { background-color: transparent !important; }
/* 下拉面板样式 */
.fav-page .el-pagination .el-select-dropdown { background: #1a2338 !important; border: 1px solid #2a3654 !important; }
/* 下拉选项文字 */
.fav-page .el-pagination .el-select-dropdown__item { color: #c2cad9 !important; }
/* 下拉选项悬停 */
.fav-page .el-pagination .el-select-dropdown__item.is-hovering { background: #1f2b45 !important; }
/* 下拉选项选中 */
.fav-page .el-pagination .el-select-dropdown__item.is-selected { color: #4d9fff !important; font-weight: 600; }
</style>
