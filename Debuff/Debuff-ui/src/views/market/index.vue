<template>
  <!-- 市场页面根容器 -->
  <div class="market-page">
    <!-- 轮播图组件：有轮播数据时显示 -->
    <el-carousel v-if="banners.length" class="market-carousel" :interval="5000" arrow="always" indicator-position="inside" :pause-on-hover="true">
      <!-- 遍历轮播项 -->
      <el-carousel-item v-for="banner in banners" :key="banner.id">
        <!-- 轮播图片按钮，支持点击跳转 -->
        <button class="banner-slide" type="button" :class="{ 'is-clickable': banner.linkUrl }" :style="{ backgroundImage: `url('${resolveImgUrl(banner.imageUrl)}')` }" @click="openBannerLink(banner)">
        </button>
      </el-carousel-item>
    </el-carousel>
    <!-- 无轮播数据时显示的品牌英雄区 -->
    <div v-else class="market-hero">
      <!-- 英雄区文案内容 -->
      <div class="market-hero-copy">
        <!-- 英雄区标签文字 -->
        <span class="market-hero-label">CS2 饰品市场</span>
        <!-- 英雄区主标题 -->
        <h1 class="hero-title">把下一件心仪饰品<br />放进你的军械库</h1>
        <!-- 英雄区副标题描述 -->
        <p class="hero-sub">在售库存实时更新，按价格、品质与武器快速找到目标饰品。</p>
        <!-- 跳转到筛选区的按钮 -->
        <button class="hero-market-action" type="button" @click="document.querySelector('.filter-panel')?.scrollIntoView({ behavior: 'smooth', block: 'center' })">开始挑选 <span>→</span></button>
      </div>
      <!-- 装饰性军械库图形元素 -->
      <div class="market-hero-arsenal" aria-hidden="true"><span class="arsenal-sight"></span><span class="arsenal-rifle"></span><span class="arsenal-line"></span></div>
      <!-- 英雄区统计数据展示 -->
      <div class="market-hero-stats">
        <!-- 在售模板数量统计 -->
        <div class="hero-stat"><strong>{{ total }}</strong><span>款在售模板</span></div>
        <!-- 平台服务费率展示 -->
        <div class="hero-stat"><strong>5%</strong><span>平台服务费</span></div>
      </div>
    </div>

    <!-- 市场标签栏和商品计数 -->
    <div class="market-tabs" role="tablist" aria-label="市场视图">
      <!-- 全部饰品标签按钮 -->
      <button class="market-tab active" type="button">全部饰品</button>
      <!-- 商品总数显示 -->
      <span class="market-count">共 {{ total }} 件商品</span>
    </div>

    <!-- 公告栏 -->
    <!-- 公告栏区域：有公告时显示 -->
    <div v-if="announcements.length" class="market-announce">
      <!-- 公告标签 -->
      <span class="announce-tag">公告</span>
      <!-- 公告列表项容器 -->
      <span class="announce-items">
        <!-- 遍历每条公告 -->
        <span v-for="a in announcements" :key="a.id" class="announce-item" @click="openAnnounce(a)">{{ a.title }}</span>
      </span>
      <!-- 查看更多公告链接 -->
      <span class="announce-more" @click="openAnnounce(announcements[0])">查看 →</span>
    </div>

    <!-- 筛选区 -->
    <!-- 筛选面板区域 -->
    <div class="filter-panel">
      <!-- 筛选第一行 -->
      <div class="filter-row">
        <!-- 关键词搜索筛选项 -->
        <div class="filter-item">
          <!-- 关键词标签 -->
          <span class="filter-label">关键词</span>
          <!-- 关键词输入框 -->
          <el-input v-model="queryParams.keyword" placeholder="搜索饰品名称" clearable
            class="dark-input" @keyup.enter="handleQuery" />
        </div>
        <!-- 武器分类筛选项 -->
        <div class="filter-item">
          <!-- 武器分类标签 -->
          <span class="filter-label">武器分类</span>
          <!-- 武器分类下拉选择器 -->
          <el-select v-model="queryParams.weaponCategoryId" placeholder="全部分类" clearable
            class="dark-select" popper-class="debuff-popper" @change="handleCategoryChange">
            <!-- 遍历武器分类选项 -->
            <el-option v-for="cat in categories" :key="cat.weaponCategory" :label="cat.categoryName"
              :value="cat.weaponCategory" />
          </el-select>
        </div>
        <!-- 武器筛选项 -->
        <div class="filter-item">
          <!-- 武器标签 -->
          <span class="filter-label">武器</span>
          <!-- 武器下拉选择器 -->
          <el-select v-model="queryParams.weaponTypeId" placeholder="全部武器" clearable
            class="dark-select" popper-class="debuff-popper" :disabled="!queryParams.weaponCategoryId">
            <!-- 遍历武器类型选项 -->
            <el-option v-for="wt in weaponTypes" :key="wt.id" :label="wt.weaponName" :value="wt.id" />
          </el-select>
        </div>
        <!-- 品质筛选项 -->
        <div class="filter-item">
          <!-- 品质标签 -->
          <span class="filter-label">品质</span>
          <!-- 品质下拉选择器 -->
          <el-select v-model="queryParams.qualityId" placeholder="全部品质" clearable
            class="dark-select" popper-class="debuff-popper">
            <!-- 遍历品质选项 -->
            <el-option v-for="q in qualities" :key="q.id" :label="q.qualityName" :value="q.id">
              <!-- 品质名称带颜色显示 -->
              <span :style="{ color: q.qualityColor }">● {{ q.qualityName }}</span>
            </el-option>
          </el-select>
        </div>
      </div>
      <!-- 筛选第二行 -->
      <div class="filter-row">
        <!-- 价格区间筛选项 -->
        <div class="filter-item">
          <!-- 价格标签 -->
          <span class="filter-label">价格</span>
          <!-- 最低价格输入框 -->
          <el-input-number v-model="queryParams.minPrice" :min="0" :precision="2" :controls="false"
            placeholder="最低价" class="dark-number" />
          <!-- 价格区间分隔符 -->
          <span class="price-sep">—</span>
          <!-- 最高价格输入框 -->
          <el-input-number v-model="queryParams.maxPrice" :min="0" :precision="2" :controls="false"
            placeholder="最高价" class="dark-number" />
        </div>
        <!-- 排序筛选项 -->
        <div class="filter-item">
          <!-- 排序标签 -->
          <span class="filter-label">排序</span>
          <!-- 排序方式下拉选择器 -->
          <el-select v-model="queryParams.orderBy" class="dark-select" popper-class="debuff-popper">
            <!-- 综合排序选项 -->
            <el-option label="综合排序" value="" />
            <!-- 价格升序选项 -->
            <el-option label="价格升序" value="price_asc" />
            <!-- 价格降序选项 -->
            <el-option label="价格降序" value="price_desc" />
            <!-- 最新上架选项 -->
            <el-option label="最新上架" value="newest" />
          </el-select>
        </div>
        <!-- 筛选操作按钮区域 -->
        <div class="filter-item filter-actions">
          <!-- 搜索按钮 -->
          <button class="btn-primary" @click="handleQuery">搜索</button>
          <!-- 重置按钮 -->
          <button class="btn-ghost" @click="resetQuery">重置</button>
        </div>
      </div>
    </div>

    <!-- 在售饰品区域标题 -->
    <div class="market-section-head">
      <!-- 标题文字区域 -->
      <div>
        <!-- 区域主标题 -->
        <h2>在售饰品</h2>
        <!-- 区域副标题描述 -->
        <p>实时价格 · 独立饰品实例 · 支持求购</p>
      </div>
      <!-- 排序提示说明 -->
      <span class="market-section-note">按上架时间排序</span>
    </div>

    <!-- 饰品卡片 -->
    <!-- 饰品列表网格：加载中显示loading效果 -->
    <div v-loading="loading" class="skin-grid">
      <!-- 遍历饰品卡片列表 -->
      <div v-for="skin in skinList" :key="skin.skinId" class="skin-card" @click="openDetail(skin)">
        <!-- 品质颜色条 -->
        <div class="quality-strip" :style="{ background: skin.qualityColor }"></div>
        <!-- 卡片图片区域 -->
        <div class="card-img">
          <!-- 饰品图片 -->
          <el-image :src="resolveImgUrl(skin.imageUrl)" fit="cover" style="width: 100%; height: 100%">
            <!-- 图片加载失败时的占位模板 -->
            <template #error>
              <!-- 图片占位容器 -->
              <div class="img-empty">
                <!-- 占位文字标记 -->
                <span class="img-empty-mark">SKIN</span>
              </div>
            </template>
          </el-image>
          <!-- 在售数量角标 -->
          <span class="listing-badge">{{ skin.listingCount }} 在售</span>
        </div>
        <!-- 卡片内容区域 -->
        <div class="card-body">
          <!-- 饰品名称 -->
          <div class="skin-name" :title="skin.skinName">{{ skin.skinName }}</div>
          <!-- 武器和品质元信息行 -->
          <div class="meta-row">
            <!-- 武器名称 -->
            <span class="weapon">{{ skin.weaponName }}</span>
            <!-- 品质名称带颜色 -->
            <span class="quality" :style="{ color: skin.qualityColor }">{{ skin.qualityName }}</span>
          </div>
          <!-- 磨损名称行：有磨损信息时显示 -->
          <div v-if="skin.wearName" class="wear-row">
            <!-- 磨损名称文字 -->
            <span class="wear-text">{{ skin.wearName }}</span>
          </div>
          <!-- 价格和操作行 -->
          <div class="price-row">
            <!-- 价格显示区域 -->
            <div class="price">
              <!-- 货币符号 -->
              <span class="cur">¥</span>
              <!-- 格式化后的最低价格 -->
              <span class="num">{{ formatPrice(skin.lowestPrice) }}</span>
            </div>
            <!-- 卡片操作按钮区域 -->
            <div class="card-actions">
            <!-- 求购同款按钮 -->
            <button class="btn-buy" @click.stop="openBuyDialog(skin)">求购同款</button>
            <!-- 收藏按钮 -->
            <button class="btn-fav" :class="{ active: skin.favorited }" type="button" title="收藏饰品" @click.stop="toggleFav(skin)">
              <!-- 收藏图标SVG -->
              <svg viewBox="0 0 24 24" width="14" height="14" :fill="skin.favorited ? '#e0554f' : 'none'" :stroke="skin.favorited ? '#e0554f' : 'currentColor'" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
            </button>
            <!-- 查看详情提示文字 -->
            <span class="hint">查看详情</span>
          </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 空状态提示：无数据且非加载中时显示 -->
    <div v-if="!loading && skinList.length === 0" class="empty-state">
      <!-- 空状态标记文字 -->
      <div class="empty-mark">NO SKINS</div>
      <!-- 空状态提示文字 -->
      <p>没有找到符合条件的饰品，换个筛选条件试试</p>
    </div>

    <!-- 分页组件区域 -->
    <div class="pager">
      <!-- 分页器：总数大于0时显示 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 公告详情弹窗 -->
    <!-- 公告详情对话框 -->
    <el-dialog v-model="announceOpen" title="公告详情" width="480px" append-to-body class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 有当前公告时显示内容 -->
      <template v-if="currentAnnounce">
        <!-- 公告标题 -->
        <h3 class="announce-title">{{ currentAnnounce.title }}</h3>
        <!-- 公告创建时间 -->
        <div class="announce-meta">{{ currentAnnounce.createTime }}</div>
        <!-- 公告正文内容（富文本） -->
        <div class="announce-content" v-html="currentAnnounce.content"></div>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <!-- 饰品详情对话框 -->
    <el-dialog v-model="detailOpen" title="饰品详情" width="920px" append-to-body
      class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 有详情数据时显示 -->
      <template v-if="detail">
        <!-- 详情布局容器 -->
        <div class="detail-layout">
          <!-- 详情左侧：图片和元信息 -->
          <div class="detail-left">
            <!-- 详情图片区域 -->
            <div class="detail-img">
              <!-- 详情大图 -->
              <el-image :src="resolveImgUrl(detail.imageUrl)" fit="cover" style="width: 100%; height: 260px">
                <!-- 图片加载失败占位 -->
                <template #error>
                  <!-- 无图片提示 -->
                  <div class="img-empty">暂无图片</div>
                </template>
              </el-image>
            </div>
            <!-- 详情元信息列表 -->
            <div class="detail-meta">
              <!-- 武器信息行 -->
              <div class="meta-line"><span class="meta-label">武器</span><span>{{ detail.weaponName }}</span></div>
              <!-- 品质信息行 -->
              <div class="meta-line"><span class="meta-label">品质</span>
                <!-- 品质名称带颜色显示 -->
                <span :style="{ color: detail.qualityColor }">● {{ detail.qualityName }}</span></div>
              <!-- 参考价格信息行 -->
              <div class="meta-line"><span class="meta-label">参考价</span>
                <!-- 格式化后的最低价格 -->
                <span class="ref-price">¥{{ formatPrice(detail.minPrice) }}</span></div>
              <!-- 描述信息行 -->
              <div class="meta-line meta-desc"><span class="meta-label">描述</span>
                <!-- 描述文字，无描述时显示默认文字 -->
                <span>{{ detail.description || '暂无描述' }}</span></div>
            </div>
          </div>
          <!-- 详情右侧：在售列表和求购 -->
          <div class="detail-right">
            <!-- 详情头部：名称和购买按钮 -->
            <div class="detail-head">
              <!-- 饰品名称标题 -->
              <h2 class="detail-name">{{ detail.skinName }}</h2>
              <!-- 立即购买按钮 -->
              <el-button type="danger" :disabled="!detail.listings || detail.listings.length === 0"
                @click="handleBuy()">立即购买</el-button>
            </div>
            <!-- 在售列表标题和数量 -->
            <div class="listing-title">在售列表（{{ detail.listings.length }}）</div>
            <!-- 在售列表表格 -->
            <el-table :data="detail.listings" size="small" max-height="180" empty-text="暂无在售记录"
              class="debuff-table">
              <!-- 卖家名称列 -->
              <el-table-column label="卖家" prop="sellerName" align="center" />
              <!-- 售价列 -->
              <el-table-column label="售价" align="center">
                <!-- 售价自定义模板 -->
                <template #default="scope">
                  <!-- 格式化价格显示 -->
                  <span class="price-num">¥{{ formatPrice(scope.row.price) }}</span>
                </template>
              </el-table-column>
              <!-- 上架时间列 -->
              <el-table-column label="上架时间" align="center" prop="listingTime" />
              <!-- 操作列 -->
              <el-table-column label="操作" align="center" width="90">
                <!-- 操作自定义模板 -->
                <template #default="scope">
                  <!-- 购买按钮 -->
                  <el-button link type="danger" @click="handleBuy(scope.row)">购买</el-button>
                </template>
              </el-table-column>
            </el-table>
            <!-- 求购订单区域 -->
            <div class="buy-order-block">
              <!-- 求购列表标题 -->
              <div class="listing-title">当前求购（{{ detailBuyOrders.length }}）</div>
              <!-- 求购列表：有求购时显示 -->
              <div v-if="detailBuyOrders.length" class="buy-order-list">
                <!-- 遍历求购订单 -->
                <div v-for="bo in detailBuyOrders" :key="bo.id" class="buy-order-item">
                  <!-- 求购订单信息 -->
                  <div class="buy-order-info">
                    <!-- 求购价格 -->
                    <span class="buy-price">¥{{ formatPrice(bo.price) }}</span>
                    <!-- 求购时间 -->
                    <span class="muted-text">{{ parseTime(bo.createTime) }}</span>
                  </div>
                  <!-- 出售按钮：可以出售给该求购单时显示 -->
                  <el-button v-if="canSellToBuyOrder(bo)" type="primary" size="small" plain
                    @click="openSellToBuyOrder(bo)">出售</el-button>
                </div>
              </div>
              <!-- 无求购时的提示文字 -->
              <div v-else class="muted-text">还没有人求购，来当第一个</div>
              <!-- 我来求购按钮 -->
              <el-button type="primary" size="small" style="margin-top: 8px"
                @click="openBuyDialog(detail)">我来求购</el-button>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 发布求购弹窗 -->
    <!-- 发布求购对话框 -->
    <el-dialog v-model="buyOpen" title="发布求购" width="440px" append-to-body class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 求购表单 -->
      <el-form label-width="90px">
        <!-- 饰品名称表单项 -->
        <el-form-item label="饰品">
          <!-- 当前求购的饰品名称 -->
          <span class="detail-name-small">{{ buyForm.skinName }}</span>
        </el-form-item>
        <!-- 当前最高求购价表单项 -->
        <el-form-item label="当前最高求购">
          <!-- 有最高求购价时显示 -->
          <span v-if="buyTopPrice !== null" class="buy-price">¥{{ formatPrice(buyTopPrice) }}</span>
          <!-- 无求购时显示提示 -->
          <span v-else class="muted-text">暂无求购</span>
        </el-form-item>
        <!-- 求购价格输入表单项 -->
        <el-form-item label="求购价">
          <!-- 求购价格数字输入框 -->
          <el-input-number v-model="buyForm.price" :min="0.01" :precision="2" :controls="false"
            style="width: 100%" />
          <!-- 求购价提示说明 -->
          <div class="muted-text" style="font-size: 12px; margin-top: 4px">
            发布后其他玩家可以看到你的求购价，价格越高越容易被卖家优先出售
          </div>
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮区域 -->
      <template #footer>
        <!-- 提交求购按钮 -->
        <el-button type="primary" @click="submitBuyOrder">发布求购</el-button>
        <!-- 取消按钮 -->
        <el-button @click="buyOpen = false">取消</el-button>
      </template>
    </el-dialog>

    <!-- 卖家出售给指定求购单 -->
    <!-- 出售给求购方对话框 -->
    <el-dialog v-model="sellBuyOpen" title="出售给求购方" width="620px" append-to-body class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 有求购订单时显示内容 -->
      <template v-if="sellBuyOrder">
        <!-- 成交价格摘要信息 -->
        <div class="sell-buy-summary">
          <!-- 摘要标签 -->
          <span>成交价格</span>
          <!-- 成交价格金额 -->
          <strong>¥{{ formatPrice(sellBuyOrder.price) }}</strong>
          <!-- 服务费和预计到账提示 -->
          <em>平台服务费 5%，预计到账 ¥{{ formatPrice(sellerIncome) }}</em>
        </div>
        <!-- 库存选择标题 -->
        <div class="listing-title">选择要出售的库存（{{ matchingInventory.length }}）</div>
        <!-- 匹配库存表格 -->
        <el-table :data="matchingInventory" highlight-current-row @current-change="selectSellInventory"
          empty-text="暂无可出售的同款库存" class="debuff-table">
          <!-- 饰品名称列 -->
          <el-table-column label="饰品" prop="skinName" min-width="160" />
          <!-- 磨损度列 -->
          <el-table-column label="磨损度" align="center" width="110">
            <!-- 磨损度自定义模板 -->
            <template #default="scope">{{ formatFloat(scope.row.floatValue) }}</template>
          </el-table-column>
          <!-- 外观名称列 -->
          <el-table-column label="外观" prop="wearName" align="center" width="110" />
          <!-- 选择列 -->
          <el-table-column label="选择" align="center" width="78">
            <!-- 选择自定义模板 -->
            <template #default="scope">
              <!-- 单选按钮选择库存 -->
              <el-radio :model-value="selectedSellInventory?.id" :value="scope.row.id"
                @change="selectSellInventory(scope.row)" />
            </template>
          </el-table-column>
        </el-table>
        <!-- 出售提示信息 -->
        <p class="sell-buy-tip">确认后会创建交易订单，并将所选饰品冻结等待买家确认收货。</p>
      </template>
      <!-- 对话框底部按钮区域 -->
      <template #footer>
        <!-- 确认出售按钮 -->
        <el-button type="primary" :disabled="!selectedSellInventory" :loading="sellSubmitting" @click="submitSellToBuyOrder">确认出售</el-button>
        <!-- 取消按钮 -->
        <el-button @click="sellBuyOpen = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Market">
// 引入Vue组合式API
import { ref, reactive, computed, onMounted, onActivated } from 'vue'
// 引入Element Plus消息提示和确认框
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入市场相关API
import { listMarket, getCategories, getMarketDetail } from '@/api/market'
// 引入购买订单API
import { buyOrder } from '@/api/order'
// 引入求购订单相关API
import { createBuyOrder, listSkinBuyOrders, acceptBuyOrder } from '@/api/buyOrder'
// 引入库存列表API
import { listInventory } from '@/api/inventory'
// 引入用户状态管理
import useUserStore from '@/store/modules/user'
// 引入公告API
import { getPublicAnnouncements } from '@/api/trade/announcement'
// 引入轮播图API
import { getPublicBanners } from '@/api/trade/banner'
// 引入收藏相关API
import { toggleFavorite, checkFavorite } from '@/api/favorite'
// 引入图片URL解析工具
import { resolveImgUrl } from '@/utils/img'

// 加载状态标识
const loading = ref(false)
// 饰品列表数据
const skinList = ref([])
// 商品总数
const total = ref(0)
// 武器分类列表
const categories = ref([])
// 品质列表
const qualities = ref([])
// 武器类型列表
const weaponTypes = ref([])
// 用户状态实例
const userStore = useUserStore()

// ---------- 首页轮播 ----------
// 轮播图数据
const banners = ref([])
// 加载轮播图数据
const loadBanners = async () => {
  // 尝试获取轮播数据
  try {
    // 调用轮播图API
    const res = await getPublicBanners()
    // 赋值轮播数据
    banners.value = res.data || []
  } catch (e) {
    // 未执行增量脚本或轮播接口不可用时，保留品牌页头作为兜底。
    // 接口失败时清空轮播数据
    banners.value = []
  }
}
// 打开轮播图链接
const openBannerLink = (banner) => {
  // 无链接地址则返回
  if (!banner.linkUrl) return
  // 判断是否为外部链接
  if (/^https?:\/\//i.test(banner.linkUrl)) {
    // 外部链接在新窗口打开
    window.open(banner.linkUrl, '_blank', 'noopener')
  } else {
    // 内部链接在当前窗口跳转
    window.location.href = banner.linkUrl
  }
}

// ---------- 公告 ----------
// 公告列表数据
const announcements = ref([])
// 公告弹窗显示状态
const announceOpen = ref(false)
// 当前选中的公告
const currentAnnounce = ref(null)

// 加载公告数据
const loadAnnouncements = async () => {
  // 尝试获取公告
  try {
    // 调用公告API
    const res = await getPublicAnnouncements()
    // 赋值公告数据
    announcements.value = res.data || []
  } catch (e) {
    // 公告加载失败不影响市场页
  }
}

// 打开公告详情弹窗
const openAnnounce = (a) => {
  // 设置当前公告
  currentAnnounce.value = a
  // 打开弹窗
  announceOpen.value = true
}

// 查询参数响应式对象
const queryParams = reactive({
  // 当前页码
  pageNum: 1,
  // 每页条数
  pageSize: 20,
  // 搜索关键词
  keyword: undefined,
  // 武器分类ID
  weaponCategoryId: undefined,
  // 武器类型ID
  weaponTypeId: undefined,
  // 品质ID
  qualityId: undefined,
  // 最低价格
  minPrice: undefined,
  // 最高价格
  maxPrice: undefined,
  // 排序方式
  orderBy: ''
})

// 分类变更时更新武器类型列表
const handleCategoryChange = (val) => {
  // 根据选中分类查找对应武器类型
  weaponTypes.value = val
    ? (categories.value.find(c => c.weaponCategory === val)?.weaponTypes || [])
    : []
  // 清空已选武器类型
  queryParams.weaponTypeId = undefined
}

// 获取饰品列表
const getList = async () => {
  // 开启加载状态
  loading.value = true
  // 尝试请求列表数据
  try {
    // 调用市场列表API
    const res = await listMarket(queryParams)
    // 赋值饰品列表
    skinList.value = res.rows || []
    // 赋值总数
    total.value = res.total || 0
    // 加载收藏状态
    await loadFavoriteStatus()
  } finally {
    // 关闭加载状态
    loading.value = false
  }
}

// 加载分类和品质数据
const loadCategories = async () => {
  // 调用分类API
  const res = await getCategories()
  // 赋值武器分类
  categories.value = res.data?.weaponCategories || []
  // 赋值品质列表
  qualities.value = res.data?.qualities || []
}

// 执行搜索（重置到第一页）
const handleQuery = () => {
  // 重置页码为1
  queryParams.pageNum = 1
  // 获取列表
  getList()
}

// 重置所有筛选条件
const resetQuery = () => {
  // 清空关键词
  queryParams.keyword = undefined
  // 清空武器分类
  queryParams.weaponCategoryId = undefined
  // 清空武器类型
  queryParams.weaponTypeId = undefined
  // 清空品质
  queryParams.qualityId = undefined
  // 清空最低价
  queryParams.minPrice = undefined
  // 清空最高价
  queryParams.maxPrice = undefined
  // 重置排序
  queryParams.orderBy = ''
  // 清空武器类型列表
  weaponTypes.value = []
  // 重新查询
  handleQuery()
}

// 格式化价格显示
const formatPrice = (v) => {
  // 空值返回占位符，否则保留两位小数
  return v === null || v === undefined ? '--' : Number(v).toFixed(2)
}

// ---------- 收藏 ----------
// 切换收藏状态
const toggleFav = async (skin) => {
  // 无在售记录时提示
  if (!skin.firstListingId) {
    // 显示警告消息
    ElMessage.warning('该饰品暂无在售记录，无法收藏')
    // 返回不执行
    return
  }
  // 尝试切换收藏
  try {
    // 调用收藏切换API
    const res = await toggleFavorite(skin.firstListingId)
    // 操作成功时
    if (res.code === 200) {
      // 反转收藏状态
      skin.favorited = !skin.favorited
      // 显示成功提示
      ElMessage.success(res.msg || (skin.favorited ? '已收藏' : '已取消收藏'))
    }
  } catch (e) {
    // 错误已由 request.js 统一提示
  }
}

// 加载饰品列表的收藏状态
const loadFavoriteStatus = async () => {
  // 遍历每个饰品
  for (const skin of skinList.value) {
    // 有在售记录时才检查收藏
    if (skin.firstListingId) {
      // 尝试检查收藏状态
      try {
        // 调用检查收藏API
        const res = await checkFavorite(skin.firstListingId)
        // 设置收藏状态
        skin.favorited = res.data === true
      } catch (e) {
        // 检查失败默认未收藏
        skin.favorited = false
      }
    } else {
      // 无在售记录默认未收藏
      skin.favorited = false
    }
  }
}

// ---------- 详情 ----------
// 详情弹窗显示状态
const detailOpen = ref(false)
// 详情数据
const detail = ref(null)
// 匹配的同款库存
const matchingInventory = ref([])

// 打开详情弹窗
const openDetail = async (row) => {
  // 获取饰品详情
  const res = await getMarketDetail(row.skinId)
  // 赋值详情数据
  detail.value = res.data
  // 打开弹窗
  detailOpen.value = true
  // 并行加载求购订单和匹配库存
  await Promise.all([loadBuyOrders(row.skinId), loadMatchingInventory(row.skinId)])
}

// ---------- 求购 ----------
// 求购弹窗显示状态
const buyOpen = ref(false)
// 求购表单数据
const buyForm = reactive({ skinId: undefined, skinName: '', price: 100 })
// 当前最高求购价
const buyTopPrice = ref(null)
// 详情中的求购订单列表
const detailBuyOrders = ref([])

// 加载饰品的求购订单
const loadBuyOrders = async (skinId) => {
  // 调用求购订单列表API
  const res = await listSkinBuyOrders(skinId, 5)
  // 赋值求购订单数据
  detailBuyOrders.value = res.data || []
}

// 加载匹配的同款库存
const loadMatchingInventory = async (skinId) => {
  // 调用库存列表API
  const res = await listInventory({ pageNum: 1, pageSize: 100, status: 0 })
  // 过滤出同款库存
  matchingInventory.value = (res.rows || []).filter(item => Number(item.skinId) === Number(skinId))
}

// 判断是否可以出售给求购方
const canSellToBuyOrder = (buyOrder) => {
  // 有匹配库存且不是自己的求购单
  return matchingInventory.value.length > 0 && Number(buyOrder.userId) !== Number(userStore.id)
}

// 打开求购弹窗
const openBuyDialog = async (skin) => {
  // 设置求购的饰品ID
  buyForm.skinId = skin.skinId
  // 设置饰品名称
  buyForm.skinName = skin.skinName
  // 计算建议价格
  const suggest = skin.lowestPrice ? Number(skin.lowestPrice) : (skin.minPrice ? Number(skin.minPrice) : 100)
  // 设置求购价格
  buyForm.price = suggest
  // 获取当前求购订单
  const res = await listSkinBuyOrders(skin.skinId, 3)
  // 处理返回数据
  const list = res.data || []
  // 设置最高求购价
  buyTopPrice.value = list.length ? Number(list[0].price) : null
  // 打开弹窗
  buyOpen.value = true
}

// 提交求购订单
const submitBuyOrder = async () => {
  // 验证价格有效性
  if (!buyForm.price || buyForm.price <= 0) {
    // 提示输入有效价格
    ElMessage.warning('请输入有效的求购价')
    // 返回不执行
    return
  }
  // 调用创建求购订单API
  const res = await createBuyOrder({ skinId: buyForm.skinId, price: buyForm.price })
  // 创建成功
  if (res.code === 200) {
    // 显示成功提示
    ElMessage.success('求购已发布')
    // 关闭弹窗
    buyOpen.value = false
    // 刷新求购列表
    if (detailOpen.value) loadBuyOrders(detail.value.skinId)
  } else {
    // 显示失败提示
    ElMessage.error(res.msg || '发布失败')
  }
}

// ---------- 出售给求购方 ----------
// 出售弹窗显示状态
const sellBuyOpen = ref(false)
// 出售提交中状态
const sellSubmitting = ref(false)
// 当前出售的求购订单
const sellBuyOrder = ref(null)
// 选中的出售库存
const selectedSellInventory = ref(null)
// 计算卖家实际到账金额（扣除5%服务费）
const sellerIncome = computed(() => {
  // 无求购订单时返回0
  if (!sellBuyOrder.value) return 0
  // 计算到账金额
  return Number(sellBuyOrder.value.price) * 0.95
})

// 格式化磨损度数值
const formatFloat = (value) => {
  // 空值返回占位符
  if (value === null || value === undefined) return '--'
  // 保留6位小数并去除末尾多余的0
  return Number(value).toFixed(6).replace(/0+$/, '').replace(/\.$/, '')
}

// 打开出售给求购方弹窗
const openSellToBuyOrder = (buyOrder) => {
  // 设置当前求购订单
  sellBuyOrder.value = buyOrder
  // 默认选中第一个库存
  selectedSellInventory.value = matchingInventory.value[0] || null
  // 打开弹窗
  sellBuyOpen.value = true
}

// 选择要出售的库存
const selectSellInventory = (inventory) => {
  // 设置选中的库存
  selectedSellInventory.value = inventory || null
}

// 提交出售给求购方
const submitSellToBuyOrder = async () => {
  // 无求购订单或未选择库存时返回
  if (!sellBuyOrder.value || !selectedSellInventory.value) return
  // 开启提交中状态
  sellSubmitting.value = true
  // 尝试提交
  try {
    // 弹出确认对话框
    await ElMessageBox.confirm(
      `确认以 ¥${formatPrice(sellBuyOrder.value.price)} 将「${detail.value.skinName}」出售给 ${sellBuyOrder.value.buyerName || '该买家'} 吗？`,
      '确认出售',
      { confirmButtonText: '确认出售', cancelButtonText: '取消', type: 'warning' }
    )
    // 调用接受求购订单API
    const res = await acceptBuyOrder(sellBuyOrder.value.id, selectedSellInventory.value.id)
    // 操作成功
    if (res.code === 200) {
      // 显示成功提示
      ElMessage.success('交易订单已创建，请前往「我的交易」查看')
      // 关闭弹窗
      sellBuyOpen.value = false
      // 刷新求购订单和匹配库存
      await Promise.all([loadBuyOrders(detail.value.skinId), loadMatchingInventory(detail.value.skinId)])
    }
  } catch (e) {
    // 取消确认或服务端校验失败时，保留弹窗以便重新选择。
  } finally {
    // 关闭提交中状态
    sellSubmitting.value = false
  }
}

// ---------- 购买 ----------
// 购买中状态标识
const buying = ref(false)

// 处理购买操作
const handleBuy = async (item) => {
  // 顶部"立即购买"按钮未指定条目时，默认买在售列表第一条
  const listing = item || (detail.value.listings && detail.value.listings[0])
  // 防御：item 可能是误传的 MouseEvent 等非条目对象，必须含有效 id
  if (!listing || typeof listing.id !== 'number' || listing.id <= 0) return
  // 尝试确认购买
  try {
    // 弹出购买确认对话框
    await ElMessageBox.confirm(
      `确认以 ¥${formatPrice(listing.price)} 购买「${detail.value.skinName}」？\n卖家：${listing.sellerName}\n平台将收取 5% 手续费`,
      '购买确认',
      { confirmButtonText: '确认购买', cancelButtonText: '再想想', type: 'warning' }
    )
  } catch {
    // 用户取消则返回
    return
  }
  // 开启购买中状态
  buying.value = true
  // 尝试执行购买
  try {
    // 调用购买API
    await buyOrder(listing.id)
    // 显示成功提示
    ElMessage.success('购买成功，请在「我的交易」中确认收货')
    // 关闭详情弹窗
    detailOpen.value = false
    // 刷新列表
    getList()
  } catch (e) {
    // 余额不足等业务错误已由 request.js 统一提示
  } finally {
    // 关闭购买中状态
    buying.value = false
  }
}

// 组件挂载时初始化数据
onMounted(() => {
  // 加载轮播图
  loadBanners()
  // 加载公告
  loadAnnouncements()
  // 加载分类数据
  loadCategories()
  // 加载饰品列表
  getList()
})
// keep-alive缓存激活时重新加载数据
onActivated(() => {
  loadBanners()
  loadAnnouncements()
  loadCategories()
  getList()
})
</script>

<style scoped>
/* 市场页面根容器样式 */
.market-page {
  /* 页面背景色变量 */
  --bg: #0d1220;
  /* 卡片背景色变量 */
  --card: #141b2d;
  /* 卡片悬停背景色变量 */
  --card-hover: #1a2338;
  /* 边框颜色变量 */
  --border: #232e45;
  /* 主文字颜色变量 */
  --text: #e9edf6;
  /* 次要文字颜色变量 */
  --muted: #8f9ab3;
  /* 价格颜色变量 */
  --price: #ff6b5e;
  /* 最小高度占满视口 */
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
  /* Element Plus 覆盖层背景色 */
  --el-bg-color-overlay: var(--card-hover);
  /* Element Plus 边框颜色 */
  --el-border-color: var(--border);
  /* Element Plus 浅边框颜色 */
  --el-border-color-light: var(--border);
  /* Element Plus 主文字颜色 */
  --el-text-color-primary: var(--text);
  /* Element Plus 常规文字颜色 */
  --el-text-color-regular: #c2cad9;
  /* Element Plus 次要文字颜色 */
  --el-text-color-secondary: var(--muted);
  /* Element Plus 空白填充色 */
  --el-fill-color-blank: #0d1220;
  /* Element Plus 输入框背景色 */
  --el-input-bg-color: #0d1220;
  /* Element Plus 输入框边框色 */
  --el-input-border-color: var(--border);
  /* Element Plus 输入框悬停边框色 */
  --el-input-hover-border-color: #35507a;
  /* Element Plus 输入框占位符颜色 */
  --el-input-placeholder-color: #5b6680;
  /* 文字颜色 */
  color: var(--text);
}

/* ---------- 轮播图 (buff风格) ---------- */
/* 轮播图容器样式 */
.market-carousel {
  /* 底部外边距 */
  margin-bottom: 24px;
  /* 圆角 */
  border-radius: 8px;
  /* 溢出隐藏 */
  overflow: hidden;
  /* 轮播箭头背景色 */
  --el-carousel-arrow-background: rgba(0, 0, 0, 0.3);
  /* 轮播箭头悬停背景色 */
  --el-carousel-arrow-hover-background: rgba(0, 0, 0, 0.5);
}
/* 轮播图容器高度 */
.market-carousel :deep(.el-carousel__container) { height: 320px; }
/* 圆点指示器 - 内部底部居中 */
.market-carousel :deep(.el-carousel__indicators--inside) { bottom: 16px; text-align: center; }
/* 圆点指示器按钮样式 */
.market-carousel :deep(.el-carousel__button) { width: 8px; height: 8px; border-radius: 50%; background: rgba(255, 255, 255, 0.4); margin: 0 4px; transition: all 0.3s; }
/* 激活状态的圆点指示器 */
.market-carousel :deep(.is-active .el-carousel__button) { background: #fff; transform: scale(1.2); }
/* 箭头按钮样式 */
.market-carousel :deep(.el-carousel__arrow) { width: 36px; height: 36px; border-radius: 50%; font-size: 14px; }
/* 箭头按钮悬停效果 */
.market-carousel :deep(.el-carousel__arrow:hover) { background: rgba(0, 0, 0, 0.6); }
/* 纯图片slide样式 */
.banner-slide {
  /* 相对定位 */
  position: relative;
  /* 宽度100% */
  width: 100%;
  /* 高度100% */
  height: 100%;
  /* 去除边框 */
  border: 0;
  /* 去除内边距 */
  padding: 0;
  /* 背景色 */
  background-color: #141b2d;
  /* 背景图居中 */
  background-position: center;
  /* 背景图覆盖 */
  background-size: cover;
  /* 背景图不重复 */
  background-repeat: no-repeat;
  /* 默认鼠标样式 */
  cursor: default;
}
/* 可点击的轮播图鼠标样式 */
.banner-slide.is-clickable { cursor: pointer; }
/* 英雄区容器 */
.market-hero {
  /* 内边距 */
  padding: 8px 4px 20px;
}
/* 英雄区眉标文字 */
.hero-eyebrow {
  /* 字体大小 */
  font-size: 12px;
  /* 字间距 */
  letter-spacing: 4px;
  /* 文字颜色 */
  color: var(--muted);
  /* 大写字母变换 */
  text-transform: uppercase;
}
/* 英雄区主标题 */
.hero-title {
  /* 外边距 */
  margin: 6px 0 4px;
  /* 字体大小 */
  font-size: 30px;
  /* 字体粗细 */
  font-weight: 800;
  /* 字间距 */
  letter-spacing: 1px;
  /* 文字颜色 */
  color: var(--text);
}
/* 英雄区副标题 */
.hero-sub {
  /* 无外边距 */
  margin: 0;
  /* 字体大小 */
  font-size: 13px;
  /* 文字颜色 */
  color: var(--muted);
}

/* ---------- 公告栏 ---------- */
/* 公告栏容器 */
.market-announce {
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 间距 */
  gap: 12px;
  /* 渐变背景 */
  background: linear-gradient(90deg, rgba(255, 107, 94, 0.12), rgba(77, 159, 255, 0.08));
  /* 边框 */
  border: 1px solid var(--border);
  /* 圆角 */
  border-radius: 10px;
  /* 内边距 */
  padding: 10px 16px;
  /* 底部外边距 */
  margin-bottom: 16px;
}
/* 公告标签样式 */
.announce-tag {
  /* 不缩小 */
  flex-shrink: 0;
  /* 字体大小 */
  font-size: 12px;
  /* 字体粗细 */
  font-weight: 700;
  /* 文字颜色 */
  color: var(--price);
  /* 边框 */
  border: 1px solid rgba(255, 107, 94, 0.5);
  /* 圆角 */
  border-radius: 4px;
  /* 内边距 */
  padding: 2px 8px;
  /* 字间距 */
  letter-spacing: 2px;
}
/* 公告项容器 */
.announce-items {
  /* 弹性布局 */
  display: flex;
  /* 间距 */
  gap: 18px;
  /* 溢出隐藏 */
  overflow: hidden;
}
/* 单条公告项 */
.announce-item {
  /* 字体大小 */
  font-size: 13px;
  /* 文字颜色 */
  color: var(--text);
  /* 手型鼠标 */
  cursor: pointer;
  /* 不换行 */
  white-space: nowrap;
  /* 颜色过渡 */
  transition: color 0.2s;
}
/* 公告项悬停效果 */
.announce-item:hover {
  /* 悬停时变为价格颜色 */
  color: var(--price);
}
/* 查看更多链接 */
.announce-more {
  /* 自动推到右侧 */
  margin-left: auto;
  /* 字体大小 */
  font-size: 12px;
  /* 文字颜色 */
  color: var(--muted);
  /* 手型鼠标 */
  cursor: pointer;
  /* 不换行 */
  white-space: nowrap;
}
/* 查看更多悬停效果 */
.announce-more:hover {
  /* 悬停时变为主文字颜色 */
  color: var(--text);
}
/* 公告标题样式 */
.announce-title {
  /* 外边距 */
  margin: 0 0 6px;
  /* 字体大小 */
  font-size: 18px;
  /* 文字颜色 */
  color: var(--text);
}
/* 公告元信息（时间等） */
.announce-meta {
  /* 字体大小 */
  font-size: 12px;
  /* 文字颜色 */
  color: var(--muted);
  /* 底部外边距 */
  margin-bottom: 12px;
}
/* 公告正文内容 */
.announce-content {
  /* 字体大小 */
  font-size: 14px;
  /* 行高 */
  line-height: 1.8;
  /* 文字颜色 */
  color: #c2cad9;
  /* 保留换行 */
  white-space: pre-wrap;
}

/* ---------- 筛选区 ---------- */
/* 筛选面板容器 */
.filter-panel {
  /* 卡片背景 */
  background: var(--card);
  /* 边框 */
  border: 1px solid var(--border);
  /* 圆角 */
  border-radius: 10px;
  /* 内边距 */
  padding: 14px 18px;
  /* 底部外边距 */
  margin-bottom: 20px;
}
/* 筛选行 */
.filter-row {
  /* 弹性布局 */
  display: flex;
  /* 允许换行 */
  flex-wrap: wrap;
  /* 间距 */
  gap: 14px 26px;
  /* 垂直居中 */
  align-items: center;
}
/* 相邻筛选行间距 */
.filter-row + .filter-row {
  /* 顶部外边距 */
  margin-top: 12px;
}
/* 单个筛选项 */
.filter-item {
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 间距 */
  gap: 8px;
}
/* 筛选标签 */
.filter-label {
  /* 字体大小 */
  font-size: 12px;
  /* 文字颜色 */
  color: var(--muted);
  /* 不换行 */
  white-space: nowrap;
}
/* 暗色输入框宽度 */
.dark-input {
  /* 宽度 */
  width: 180px;
}
/* 暗色选择器宽度 */
.dark-select {
  /* 宽度 */
  width: 150px;
}
/* 暗色数字输入框宽度 */
.dark-number {
  /* 宽度 */
  width: 110px;
}
/* 价格分隔符颜色 */
.price-sep {
  /* 颜色 */
  color: var(--muted);
}
/* 筛选操作按钮区域靠右 */
.filter-actions {
  /* 自动推到右侧 */
  margin-left: auto;
}
/* 主要按钮和幽灵按钮共用样式 */
.btn-primary,
.btn-ghost {
  /* 无边框 */
  border: none;
  /* 圆角 */
  border-radius: 6px;
  /* 内边距 */
  padding: 9px 22px;
  /* 字体大小 */
  font-size: 13px;
  /* 手型鼠标 */
  cursor: pointer;
  /* 过渡动画 */
  transition: all 0.18s;
}
/* 主要按钮样式 */
.btn-primary {
  /* 渐变背景 */
  background: linear-gradient(135deg, #4d9fff, #3a7bd5);
  /* 白色文字 */
  color: #fff;
  /* 字体加粗 */
  font-weight: 600;
}
/* 主要按钮悬停效果 */
.btn-primary:hover {
  /* 亮度增强 */
  filter: brightness(1.12);
}
/* 幽灵按钮样式 */
.btn-ghost {
  /* 透明背景 */
  background: transparent;
  /* 次要文字颜色 */
  color: var(--muted);
  /* 边框 */
  border: 1px solid var(--border);
}
/* 幽灵按钮悬停效果 */
.btn-ghost:hover {
  /* 悬停时文字变亮 */
  color: var(--text);
  /* 悬停时边框变亮 */
  border-color: #35507a;
}

/* ---------- 卡片 ---------- */
/* 饰品网格布局 */
.skin-grid {
  /* 网格布局 */
  display: grid;
  /* 自适应列宽 */
  grid-template-columns: repeat(auto-fill, minmax(235px, 1fr));
  /* 网格间距 */
  gap: 16px;
}
/* 饰品卡片样式 */
.skin-card {
  /* 相对定位 */
  position: relative;
  /* 卡片背景 */
  background: var(--card);
  /* 边框 */
  border: 1px solid var(--border);
  /* 圆角 */
  border-radius: 10px;
  /* 溢出隐藏 */
  overflow: hidden;
  /* 手型鼠标 */
  cursor: pointer;
  /* 过渡动画 */
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}
/* 卡片悬停效果 */
.skin-card:hover {
  /* 向上偏移 */
  transform: translateY(-4px);
  /* 边框高亮 */
  border-color: #35507a;
  /* 阴影效果 */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.45);
}
/* 品质颜色条 */
.quality-strip {
  /* 高度3像素 */
  height: 3px;
  /* 宽度100% */
  width: 100%;
}
/* 卡片图片区域 */
.card-img {
  /* 相对定位 */
  position: relative;
  /* 固定高度 */
  height: 150px;
  /* 深色背景 */
  background: #0a0f1a;
}
/* 图片元素显示方式 */
.card-img :deep(.el-image) {
  /* 块级显示 */
  display: block;
}
/* 图片空占位容器 */
.img-empty {
  /* 撑满高度 */
  height: 100%;
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 水平居中 */
  justify-content: center;
  /* 占位文字颜色 */
  color: #4a5570;
}
/* 占位文字标记 */
.img-empty-mark {
  /* 字体大小 */
  font-size: 13px;
  /* 字间距 */
  letter-spacing: 4px;
}
/* 在售数量角标 */
.listing-badge {
  /* 绝对定位 */
  position: absolute;
  /* 距顶部8px */
  top: 8px;
  /* 距右侧8px */
  right: 8px;
  /* 半透明深色背景 */
  background: rgba(13, 18, 32, 0.82);
  /* 绿色文字 */
  color: #3ddc97;
  /* 字体大小 */
  font-size: 11px;
  /* 内边距 */
  padding: 3px 8px;
  /* 圆角 */
  border-radius: 20px;
  /* 绿色边框 */
  border: 1px solid rgba(61, 220, 151, 0.35);
}
/* 卡片内容区域 */
.card-body {
  /* 内边距 */
  padding: 12px 14px 14px;
}
/* 饰品名称 */
.skin-name {
  /* 字体大小 */
  font-size: 15px;
  /* 字体加粗 */
  font-weight: 600;
  /* 不换行 */
  white-space: nowrap;
  /* 溢出隐藏 */
  overflow: hidden;
  /* 文字截断省略号 */
  text-overflow: ellipsis;
}
/* 元信息行 */
.meta-row {
  /* 顶部外边距 */
  margin-top: 5px;
  /* 弹性布局 */
  display: flex;
  /* 两端对齐 */
  justify-content: space-between;
  /* 字体大小 */
  font-size: 12px;
}
/* 武器名称 */
.weapon {
  /* 次要文字颜色 */
  color: var(--muted);
}
/* 品质名称 */
.quality {
  /* 字体加粗 */
  font-weight: 600;
}
/* 磨损行 */
.wear-row {
  /* 顶部外边距 */
  margin-top: 4px;
  /* 字体大小 */
  font-size: 12px;
}
/* 磨损文字 */
.wear-text {
  /* 次要文字颜色 */
  color: var(--muted);
}
/* 价格行 */
.price-row {
  /* 顶部外边距 */
  margin-top: 10px;
  /* 弹性布局 */
  display: flex;
  /* 两端对齐 */
  justify-content: space-between;
  /* 底部对齐 */
  align-items: flex-end;
}
/* 价格容器 */
.price {
  /* 弹性布局 */
  display: flex;
  /* 基线对齐 */
  align-items: baseline;
}
/* 货币符号 */
.cur {
  /* 价格颜色 */
  color: var(--price);
  /* 字体大小 */
  font-size: 13px;
  /* 右侧间距 */
  margin-right: 2px;
}
/* 价格数字 */
.num {
  /* 价格颜色 */
  color: var(--price);
  /* 大号字体 */
  font-size: 24px;
  /* 超粗字体 */
  font-weight: 800;
  /* 等宽数字 */
  font-variant-numeric: tabular-nums;
  /* 字间距微调 */
  letter-spacing: -0.5px;
}
/* 查看详情提示 */
.hint {
  /* 字体大小 */
  font-size: 11px;
  /* 暗色文字 */
  color: #4a5570;
}
/* 卡片操作按钮区域 */
.card-actions {
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 间距 */
  gap: 8px;
}
/* 求购按钮样式 */
.btn-buy {
  /* 蓝色边框 */
  border: 1px solid rgba(77, 159, 255, 0.45);
  /* 蓝色半透明背景 */
  background: rgba(77, 159, 255, 0.12);
  /* 蓝色文字 */
  color: #4d9fff;
  /* 字体大小 */
  font-size: 12px;
  /* 内边距 */
  padding: 4px 12px;
  /* 圆角 */
  border-radius: 6px;
  /* 手型鼠标 */
  cursor: pointer;
  /* 过渡动画 */
  transition: all 0.18s;
}
/* 求购按钮悬停效果 */
.btn-buy:hover {
  /* 背景加深 */
  background: rgba(77, 159, 255, 0.24);
  /* 边框加深 */
  border-color: #4d9fff;
}
/* 收藏按钮样式 */
.btn-fav {
  /* 边框 */
  border: 1px solid var(--border);
  /* 透明背景 */
  background: transparent;
  /* 次要文字颜色 */
  color: var(--muted);
  /* 固定宽度 */
  width: 28px;
  /* 固定高度 */
  height: 28px;
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 水平居中 */
  justify-content: center;
  /* 圆角 */
  border-radius: 6px;
  /* 手型鼠标 */
  cursor: pointer;
  /* 过渡动画 */
  transition: all 0.18s;
  /* 内边距归零 */
  padding: 0;
}
/* 收藏按钮悬停效果 */
.btn-fav:hover {
  /* 红色边框 */
  border-color: #ff6b5e;
  /* 红色文字 */
  color: #ff6b5e;
}
/* 已激活的收藏按钮 */
.btn-fav.active {
  /* 红色边框 */
  border-color: #ff6b5e;
  /* 红色半透明背景 */
  background: rgba(255, 107, 94, 0.12);
  /* 红色文字 */
  color: #ff6b5e;
}

/* ---------- 空状态 / 分页 ---------- */
/* 空状态容器 */
.empty-state {
  /* 文字居中 */
  text-align: center;
  /* 上下内边距 */
  padding: 60px 0;
  /* 次要文字颜色 */
  color: var(--muted);
}
/* 空状态标记文字 */
.empty-mark {
  /* 大号字体 */
  font-size: 26px;
  /* 宽字间距 */
  letter-spacing: 8px;
  /* 暗色 */
  color: #35405c;
  /* 字体加粗 */
  font-weight: 700;
}
/* 空状态提示文字 */
.empty-state p {
  /* 顶部外边距 */
  margin-top: 10px;
  /* 字体大小 */
  font-size: 13px;
}
/* 分页容器 */
.pager {
  /* 顶部外边距 */
  margin-top: 20px;
  /* 弹性布局 */
  display: flex;
  /* 右对齐 */
  justify-content: flex-end;
}

/* ---------- 详情 ---------- */
/* 详情布局网格 */
.detail-layout {
  /* 网格布局 */
  display: grid;
  /* 左320px右自适应 */
  grid-template-columns: 320px 1fr;
  /* 间距 */
  gap: 22px;
}
/* 详情图片容器 */
.detail-img {
  /* 圆角 */
  border-radius: 8px;
  /* 溢出隐藏 */
  overflow: hidden;
  /* 边框 */
  border: 1px solid var(--border);
  /* 深色背景 */
  background: #0a0f1a;
}
/* 详情元信息区域 */
.detail-meta {
  /* 顶部外边距 */
  margin-top: 14px;
  /* 字体大小 */
  font-size: 13px;
}
/* 元信息行 */
.meta-line {
  /* 弹性布局 */
  display: flex;
  /* 两端对齐 */
  justify-content: space-between;
  /* 上下内边距 */
  padding: 7px 0;
  /* 虚线下边框 */
  border-bottom: 1px dashed var(--border);
  /* 文字颜色 */
  color: var(--text);
}
/* 元信息标签 */
.meta-label {
  /* 次要文字颜色 */
  color: var(--muted);
}
/* 描述行块级显示 */
.meta-desc {
  /* 块级显示 */
  display: block;
}
/* 描述行标签块级显示 */
.meta-desc .meta-label {
  /* 块级显示 */
  display: block;
  /* 底部外边距 */
  margin-bottom: 4px;
}
/* 参考价格样式 */
.ref-price {
  /* 价格颜色 */
  color: var(--price);
  /* 字体加粗 */
  font-weight: 600;
}
/* 详情头部区域 */
.detail-head {
  /* 弹性布局 */
  display: flex;
  /* 两端对齐 */
  justify-content: space-between;
  /* 垂直居中 */
  align-items: center;
  /* 底部外边距 */
  margin-bottom: 14px;
}
/* 详情名称标题 */
.detail-name {
  /* 无外边距 */
  margin: 0;
  /* 字体大小 */
  font-size: 20px;
  /* 字体加粗 */
  font-weight: 700;
}
/* 列表标题 */
.listing-title {
  /* 字体大小 */
  font-size: 13px;
  /* 次要文字颜色 */
  color: var(--muted);
  /* 底部外边距 */
  margin-bottom: 8px;
}
/* 价格数字样式 */
.price-num {
  /* 价格颜色 */
  color: var(--price);
  /* 字体加粗 */
  font-weight: 700;
}
/* 求购订单区域 */
.buy-order-block {
  /* 顶部外边距 */
  margin-top: 14px;
}
/* 求购订单列表 */
.buy-order-list {
  /* 纵向弹性布局 */
  display: flex;
  /* 纵向排列 */
  flex-direction: column;
  /* 间距 */
  gap: 6px;
  /* 最大高度 */
  max-height: 132px;
  /* 纵向滚动 */
  overflow-y: auto;
}
/* 单个求购订单项 */
.buy-order-item {
  /* 弹性布局 */
  display: flex;
  /* 两端对齐 */
  justify-content: space-between;
  /* 垂直居中 */
  align-items: center;
  /* 浅灰背景 */
  background: #f3f4f6;
  /* 边框 */
  border: 1px solid #e5e7eb;
  /* 圆角 */
  border-radius: 6px;
  /* 内边距 */
  padding: 6px 12px;
}
/* 求购订单信息区域 */
.buy-order-info { display: flex; min-width: 0; align-items: center; gap: 14px; }
/* 出售摘要信息区域 */
.sell-buy-summary { display: flex; align-items: baseline; gap: 10px; margin-bottom: 16px; padding: 12px 14px; border: 1px solid var(--border); border-radius: 6px; background: #1a2338; }
/* 摘要标签文字 */
.sell-buy-summary > span { color: var(--muted); font-size: 12px; }
/* 摘要价格金额 */
.sell-buy-summary strong { color: var(--price); font-size: 22px; font-variant-numeric: tabular-nums; }
/* 摘要服务费提示 */
.sell-buy-summary em { margin-left: auto; color: var(--muted); font-size: 12px; font-style: normal; }
/* 出售提示信息 */
.sell-buy-tip { margin: 12px 0 0; color: var(--muted); font-size: 12px; line-height: 1.6; }
/* 求购价格样式 */
.buy-price {
  /* 红色 */
  color: #ff6b5e;
  /* 字体加粗 */
  font-weight: 700;
  /* 字体大小 */
  font-size: 14px;
  /* 等宽数字 */
  font-variant-numeric: tabular-nums;
}
/* 次要文字样式 */
.muted-text {
  /* 次要颜色 */
  color: var(--muted);
  /* 小号字体 */
  font-size: 12px;
}
/* 详情小名称 */
.detail-name-small {
  /* 字体大小 */
  font-size: 14px;
  /* 字体加粗 */
  font-weight: 600;
  /* 主文字颜色 */
  color: var(--text);
}

/* 减少动画偏好时禁用过渡效果 */
@media (prefers-reduced-motion: reduce) {
  /* 卡片禁用过渡 */
  .skin-card {
    transition: none;
  }
  /* 轮播项禁用过渡 */
  .market-carousel :deep(.el-carousel__item) { transition: none !important; }
}
</style>

<style>
/* 亮色主题下市场页面Element Plus变量覆盖 */
body.user-surface .market-page {
  /* Element Plus 背景色 */
  --el-bg-color: #ffffff;
  /* Element Plus 覆盖层背景色 */
  --el-bg-color-overlay: #ffffff;
  /* Element Plus 空白填充色 */
  --el-fill-color-blank: #ffffff;
  /* Element Plus 边框颜色 */
  --el-border-color: #e3e8ef;
  /* Element Plus 浅边框颜色 */
  --el-border-color-light: #edf0f4;
  /* Element Plus 主文字颜色 */
  --el-text-color-primary: #1f2d3d;
  /* Element Plus 常规文字颜色 */
  --el-text-color-regular: #526275;
  /* Element Plus 次要文字颜色 */
  --el-text-color-secondary: #8290a3;
}
/* 亮色主题英雄区样式 */
body.user-surface .market-page .market-hero {
  /* 最小高度 */
  min-height: 168px;
  /* 内边距 */
  padding: 30px 34px;
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 两端对齐 */
  justify-content: space-between;
  /* 浅蓝背景 */
  background: #edf5ff;
  /* 边框 */
  border: 1px solid #d9e9fb;
  /* 圆角 */
  border-radius: 4px;
  /* 底部外边距 */
  margin-bottom: 18px;
}
/* 亮色主题轮播图样式 */
body.user-surface .market-page .market-carousel { margin-bottom: 22px; border-radius: 4px; border: 1px solid #dce8f5; }
/* 亮色主题轮播容器高度 */
body.user-surface .market-page .market-carousel :deep(.el-carousel__container) { height: 280px; }
/* 亮色主题圆点指示器 */
body.user-surface .market-page .market-carousel :deep(.el-carousel__button) { background: rgba(0, 0, 0, 0.25); }
/* 亮色主题激活圆点 */
body.user-surface .market-page .market-carousel :deep(.is-active .el-carousel__button) { background: #2f80ed; }
/* 亮色主题箭头 */
body.user-surface .market-page .market-carousel :deep(.el-carousel__arrow) { background: rgba(255, 255, 255, 0.7); color: #333; }
/* 亮色主题箭头悬停 */
body.user-surface .market-page .market-carousel :deep(.el-carousel__arrow:hover) { background: rgba(255, 255, 255, 0.95); }
/* 亮色主题英雄区文案最大宽度 */
body.user-surface .market-page .market-hero-copy { max-width: 620px; }
/* 亮色主题眉标文字 */
body.user-surface .market-page .hero-eyebrow { color: #5b7fa8; letter-spacing: 2px; }
/* 亮色主题主标题 */
body.user-surface .market-page .hero-title { color: #1f2d3d; font-size: 30px; letter-spacing: 0; }
/* 亮色主题副标题 */
body.user-surface .market-page .hero-sub { color: #6c7c8f; font-size: 14px; }
/* 亮色主题统计区域 */
body.user-surface .market-page .market-hero-stats { display: flex; gap: 36px; padding-right: 20px; }
/* 亮色主题统计项 */
body.user-surface .market-page .hero-stat { display: flex; flex-direction: column; gap: 4px; }
/* 亮色主题统计数字 */
body.user-surface .market-page .hero-stat strong { color: #2f80ed; font-size: 25px; font-weight: 700; }
/* 亮色主题统计标签 */
body.user-surface .market-page .hero-stat span { color: #8290a3; font-size: 12px; }
/* 亮色主题标签栏 */
body.user-surface .market-page .market-tabs {
  /* 最小高度 */
  min-height: 48px;
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 间距 */
  gap: 26px;
  /* 底部边框 */
  border-bottom: 1px solid #e3e8ef;
  /* 底部外边距 */
  margin-bottom: 18px;
}
/* 亮色主题标签按钮 */
body.user-surface .market-page .market-tab {
  /* 高度 */
  height: 48px;
  /* 相对定位 */
  position: relative;
  /* 无边框 */
  border: 0;
  /* 透明背景 */
  background: transparent;
  /* 文字颜色 */
  color: #8290a3;
  /* 手型鼠标 */
  cursor: pointer;
  /* 字体大小 */
  font-size: 14px;
}
/* 亮色主题激活标签 */
body.user-surface .market-page .market-tab.active { color: #2f80ed; font-weight: 600; }
/* 亮色主题激活标签下划线 */
body.user-surface .market-page .market-tab.active::after {
  /* 伪元素内容 */
  content: '';
  /* 绝对定位 */
  position: absolute;
  /* 右侧归零 */
  right: 0;
  /* 底部偏移 */
  bottom: -1px;
  /* 左侧归零 */
  left: 0;
  /* 高度2px */
  height: 2px;
  /* 蓝色下划线 */
  background: #2f80ed;
}
/* 亮色主题商品计数 */
body.user-surface .market-page .market-count { margin-left: auto; color: #9aa6b5; font-size: 12px; }
/* 亮色主题区域标题容器 */
body.user-surface .market-page .market-section-head {
  /* 弹性布局 */
  display: flex;
  /* 底部对齐 */
  align-items: flex-end;
  /* 两端对齐 */
  justify-content: space-between;
  /* 外边距 */
  margin: 4px 0 14px;
}
/* 亮色主题区域标题 */
body.user-surface .market-page .market-section-head h2 { margin: 0; color: #1f2d3d; font-size: 19px; font-weight: 700; }
/* 亮色主题区域副标题 */
body.user-surface .market-page .market-section-head p { margin: 5px 0 0; color: #8290a3; font-size: 12px; }
/* 亮色主题排序提示 */
body.user-surface .market-page .market-section-note { color: #9aa6b5; font-size: 12px; }
/* 亮色主题公告栏 */
body.user-surface .market-page .market-announce { background: #fffaf0; border-color: #f3dfb4; border-radius: 4px; }
/* 亮色主题公告标签 */
body.user-surface .market-page .announce-tag { color: #b97817; border-color: #e8c986; }
/* 亮色主题公告项 */
body.user-surface .market-page .announce-item { color: #5d6b7c; }
/* 亮色主题饰品网格 */
body.user-surface .market-page .skin-grid { grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 14px; }
/* 亮色主题卡片圆角 */
body.user-surface .market-page .skin-card { border-radius: 4px; }
/* 亮色主题图片高度 */
body.user-surface .market-page .card-img { height: 168px; }
/* 亮色主题饰品名称 */
body.user-surface .market-page .skin-name { color: #29394c; }
/* 亮色主题价格颜色 */
body.user-surface .market-page .num,
body.user-surface .market-page .cur,
body.user-surface .market-page .price-num,
body.user-surface .market-page .buy-price { color: #d95550; }
/* 亮色主题提示文字 */
body.user-surface .market-page .hint { color: #98a4b1; }
/* 亮色主题求购按钮 */
body.user-surface .market-page .btn-buy { color: #2f80ed; border-color: #b8d3f4; background: #f1f7ff; }
/* 亮色主题收藏按钮 */
body.user-surface .market-page .btn-fav { border-color: #dce4ed; }
/* 亮色主题空状态 */
body.user-surface .market-page .empty-state { color: #8290a3; }
/* 移动端响应式适配 */
@media (max-width: 720px) {
  /* 小屏轮播高度 */
  .market-carousel :deep(.el-carousel__container) { height: 200px; }
  /* 小屏箭头尺寸 */
  .market-carousel :deep(.el-carousel__arrow) { width: 28px; height: 28px; font-size: 12px; }
  /* 亮色主题小屏轮播高度 */
  body.user-surface .market-page .market-carousel :deep(.el-carousel__container) { height: 180px; }
  /* 亮色主题小屏英雄区内边距 */
  body.user-surface .market-page .market-hero { padding: 22px; }
  /* 亮色主题小屏隐藏统计 */
  body.user-surface .market-page .market-hero-stats { display: none; }
  /* 亮色主题小屏隐藏排序提示 */
  body.user-surface .market-page .market-section-note { display: none; }
}

/* 下拉面板(teleport到body)暗色覆盖 */
/* 暗色下拉面板变量 */
.debuff-popper {
  /* 覆盖层背景色 */
  --el-bg-color-overlay: #1a2338;
  /* 浅边框颜色 */
  --el-border-color-light: #2a3654;
  /* 主文字颜色 */
  --el-text-color-primary: #e9edf6;
  /* 常规文字颜色 */
  --el-text-color-regular: #c2cad9;
  /* 空白填充色 */
  --el-fill-color-blank: #1a2338;
}
/* 暗色下拉面板背景 */
.debuff-popper.el-popper {
  /* 强制深色背景 */
  background: #1a2338 !important;
  /* 强制深色边框 */
  border: 1px solid #2a3654 !important;
}
/* 暗色下拉项文字颜色 */
.debuff-popper .el-select-dropdown__item {
  /* 强制浅色文字 */
  color: #c2cad9 !important;
}
/* 暗色下拉项悬停背景 */
.debuff-popper .el-select-dropdown__item.is-hovering {
  /* 强制深色悬停背景 */
  background: #1f2b45 !important;
}
/* 暗色下拉项选中样式 */
.debuff-popper .el-select-dropdown__item.is-selected {
  /* 强制蓝色文字 */
  color: #4d9fff !important;
  /* 字体加粗 */
  font-weight: 600;
}
/* 暗色下拉列表背景 */
.debuff-popper .el-select-dropdown {
  /* 强制深色背景 */
  background: #1a2338 !important;
}
/* 暗色下拉滚动条样式 */
.debuff-popper .el-scrollbar__bar.is-horizontal,
.debuff-popper .el-scrollbar__bar.is-vertical {
  /* 强制深色滚动条 */
  background: #2a3654 !important;
}
/* 暗色下拉箭头样式 */
.debuff-popper .el-popper__arrow::before {
  /* 箭头背景色 */
  background: #1a2338;
  /* 箭头边框色 */
  border-color: #2a3654;
}
/* 分页下拉选择器弹出层 */
/* 分页下拉背景 */
.el-pagination .el-select-dropdown {
  /* 强制深色背景 */
  background: #1a2338 !important;
  /* 强制深色边框 */
  border: 1px solid #2a3654 !important;
}
/* 分页下拉项文字 */
.el-pagination .el-select-dropdown__item {
  /* 强制浅色文字 */
  color: #c2cad9 !important;
}
/* 分页下拉项悬停 */
.el-pagination .el-select-dropdown__item.is-hovering {
  /* 强制深色悬停背景 */
  background: #1f2b45 !important;
}
/* 分页下拉项选中 */
.el-pagination .el-select-dropdown__item.is-selected {
  /* 强制蓝色文字 */
  color: #4d9fff !important;
  /* 字体加粗 */
  font-weight: 600;
}
/* 全局暗色覆盖 - 针对所有 select 下拉和 popper */
/* 全局下拉背景 */
.el-select-dropdown {
  /* 强制深色背景 */
  background: #1a2338 !important;
  /* 强制深色边框 */
  border: 1px solid #2a3654 !important;
}
/* 全局下拉项文字 */
.el-select-dropdown__item {
  /* 强制浅色文字 */
  color: #c2cad9 !important;
}
/* 全局下拉项悬停 */
.el-select-dropdown__item.is-hovering {
  /* 强制深色悬停背景 */
  background: #1f2b45 !important;
}
/* 全局下拉项选中 */
.el-select-dropdown__item.is-selected {
  /* 强制蓝色文字 */
  color: #4d9fff !important;
  /* 字体加粗 */
  font-weight: 600;
}
/* 全局弹出层背景 */
.el-popper {
  /* 强制深色背景 */
  background: #1a2338 !important;
  /* 强制深色边框 */
  border: 1px solid #2a3654 !important;
}
/* 全局弹出层箭头 */
.el-popper__arrow::before {
  /* 强制深色背景 */
  background: #1a2338 !important;
  /* 强制深色边框 */
  border-color: #2a3654 !important;
}
/* 弹窗(teleport到body)与表格的暗色覆盖 —— 全局但限定类名 */
/* 暗色弹窗样式 */
.debuff-dialog {
  /* 弹窗背景色 */
  --el-dialog-bg-color: #ffffff !important;
  /* 主文字颜色 */
  --el-text-color-primary: #1a1a2e;
  /* 常规文字颜色 */
  --el-text-color-regular: #3a3a50;
  /* 次要文字颜色 */
  --el-text-color-secondary: #6b7280;
  /* 主文字变量 */
  --text: #1a1a2e;
  /* 次要文字变量 */
  --muted: #6b7280;
  /* 价格颜色变量 */
  --price: #ff6b5e;
  /* 边框颜色变量 */
  --border: #e5e7eb;
  /* 卡片颜色变量 */
  --card: #f3f4f6;
  /* 白色背景 */
  background: #ffffff !important;
  /* 浅灰边框 */
  border: 1px solid #e5e7eb;
  /* 圆角 */
  border-radius: 12px;
  /* 深色文字 */
  color: #1a1a2e;
}
/* 弹窗内容区域 */
.debuff-dialog .el-dialog__body {
  /* 白色背景 */
  background: #ffffff !important;
  /* 深色文字 */
  color: #1a1a2e;
}
/* 弹窗头部区域 */
.debuff-dialog .el-dialog__header {
  /* 白色背景 */
  background: #ffffff !important;
  /* 底部分隔线 */
  border-bottom: 1px solid #e5e7eb;
}
/* 弹窗遮罩层 */
.debuff-overlay {
  /* 半透明黑色遮罩 */
  background: rgba(6, 9, 16, 0.72);
}
/* 弹窗标题 */
.debuff-dialog .el-dialog__title {
  /* 深色文字 */
  color: #1a1a2e;
  /* 字体加粗 */
  font-weight: 600;
}
/* 弹窗关闭按钮 */
.debuff-dialog .el-dialog__headerbtn .el-dialog__close {
  /* 灰色文字 */
  color: #6b7280;
}
/* 弹窗内表格样式 */
.debuff-dialog .el-table {
  /* 表格背景透明 */
  --el-table-bg-color: transparent;
  /* 行背景透明 */
  --el-table-tr-bg-color: transparent;
  /* 表头背景 */
  --el-table-header-bg-color: #f3f4f6;
  /* 表头文字颜色 */
  --el-table-header-text-color: #6b7280;
  /* 表格边框颜色 */
  --el-table-border-color: #e5e7eb;
  /* 表格文字颜色 */
  --el-table-text-color: #3a3a50;
  /* 行悬停背景 */
  --el-table-row-hover-bg-color: #f9fafb;
  /* 表格背景透明 */
  background: transparent;
}
/* 分页容器背景透明 */
.market-page .pagination-container {
  /* 强制透明背景 */
  background: transparent !important;
}
/* 分页组件变量 */
.market-page .el-pagination {
  /* 背景透明 */
  --el-pagination-bg-color: transparent;
  /* 文字颜色 */
  --el-pagination-text-color: #c2cad9;
  /* 按钮颜色 */
  --el-pagination-button-color: #c2cad9;
  /* 悬停颜色 */
  --el-pagination-hover-color: #4d9fff;
  /* 禁用按钮背景 */
  --el-pagination-button-disabled-bg-color: transparent;
}
/* 分页页码项 */
.market-page .el-pagination .el-pager li {
  /* 透明背景 */
  background: transparent !important;
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 分页激活页码 */
.market-page .el-pagination .el-pager li.is-active {
  /* 蓝色文字 */
  color: #4d9fff !important;
  /* 字体加粗 */
  font-weight: 700;
}
/* 分页按钮 */
.market-page .el-pagination button {
  /* 透明背景 */
  background: transparent !important;
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 分页跳转文字 */
.market-page .el-pagination .el-pagination__jump {
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 分页跳转输入框 */
.market-page .el-pagination .el-pagination__jump .el-input__wrapper {
  /* 深色背景 */
  background-color: #1a2338 !important;
  /* 深色边框 */
  box-shadow: 0 0 0 1px #2a3654 inset !important;
}
/* 分页跳转输入文字 */
.market-page .el-pagination .el-pagination__jump .el-input__inner {
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 分页大小选择器 */
/* 分页大小选择器背景 */
.market-page .el-pagination .el-select .el-input__wrapper {
  /* 深色背景 */
  background-color: #1a2338 !important;
  /* 深色边框 */
  box-shadow: 0 0 0 1px #2a3654 inset !important;
}
/* 分页大小选择器文字 */
.market-page .el-pagination .el-select .el-input__inner {
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 分页相关所有元素暗色 */
/* 分页所有元素透明背景 */
.market-page .el-pagination * {
  /* 强制透明 */
  background-color: transparent !important;
}
/* 分页输入框统一背景 */
.market-page .el-pagination .el-select .el-input__wrapper,
.market-page .el-pagination .el-pagination__jump .el-input__wrapper {
  /* 深色背景 */
  background-color: #1a2338 !important;
}
/* 分页输入框和页码显示 */
/* 分页输入框样式 */
.market-page .el-pagination .el-input__wrapper {
  /* 深色背景 */
  background-color: #1a2338 !important;
  /* 深色边框 */
  box-shadow: 0 0 0 1px #2a3654 inset !important;
}
/* 分页输入框文字 */
.market-page .el-pagination .el-input__inner {
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* el-select 暗色主题 */
/* 选择器输入框背景 */
.market-page .el-select .el-input__wrapper {
  /* 深色背景 */
  background-color: #1a2338 !important;
  /* 深色边框 */
  box-shadow: 0 0 0 1px #2a3654 inset !important;
}
/* 选择器输入框文字 */
.market-page .el-select .el-input__inner {
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 选择器输入框悬停 */
.market-page .el-select .el-input__wrapper:hover {
  /* 蓝色边框高亮 */
  box-shadow: 0 0 0 1px #4d9fff inset !important;
}
/* 选择器箭头图标 */
.market-page .el-select .el-select__caret {
  /* 灰色图标 */
  color: #8f9ab3 !important;
}
/* 加载遮罩层 */
.market-page .el-loading-mask {
  /* 半透明深色背景 */
  background-color: rgba(13, 18, 32, 0.6);
}
/* 亮色主题下拉面板覆盖 */
body.user-surface .market-page .el-popper,
body.user-surface .market-page .el-select-dropdown,
body.user-surface .market-page .el-pagination .el-select-dropdown {
  /* 白色背景 */
  background: #ffffff !important;
  /* 浅灰边框 */
  border-color: #e3e8ef !important;
}
/* 亮色主题下拉项 */
body.user-surface .market-page .el-select-dropdown__item {
  /* 深色文字 */
  color: #526275 !important;
}
/* 亮色主题下拉项悬停 */
body.user-surface .market-page .el-select-dropdown__item.is-hovering {
  /* 浅蓝悬停背景 */
  background: #f0f5fb !important;
}
/* 亮色主题下拉项选中 */
body.user-surface .market-page .el-select-dropdown__item.is-selected {
  /* 蓝色文字 */
  color: #2f80ed !important;
}
/* 亮色主题弹出层箭头 */
body.user-surface .market-page .el-popper__arrow::before {
  /* 白色背景 */
  background: #ffffff !important;
  /* 浅灰边框 */
  border-color: #e3e8ef !important;
}
/* 亮色主题弹窗表格 */
body.user-surface .market-page .debuff-dialog .el-table {
  /* 表格背景 */
  --el-table-bg-color: #ffffff;
  /* 行背景 */
  --el-table-tr-bg-color: #ffffff;
  /* 表头背景 */
  --el-table-header-bg-color: #f7f9fb;
  /* 表头文字 */
  --el-table-header-text-color: #8290a3;
  /* 表格边框 */
  --el-table-border-color: #edf0f4;
  /* 表格文字 */
  --el-table-text-color: #526275;
  /* 行悬停背景 */
  --el-table-row-hover-bg-color: #f7faff;
}
/* 亮色主题弹窗表头 */
body.user-surface .market-page .debuff-dialog .el-table th.el-table__cell {
  /* 浅灰背景 */
  background: #f7f9fb !important;
  /* 灰色文字 */
  color: #8290a3 !important;
}
</style>
