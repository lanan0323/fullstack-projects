<template>
  <!-- 库存页面根容器 -->
  <div class="inventory-page">
    <!-- 页头 -->
    <!-- 页头英雄区域 -->
    <div class="inv-hero">
      <!-- 英文眉标 -->
      <div class="hero-eyebrow">MY ARMORY</div>
      <!-- 页面主标题 -->
      <h1 class="hero-title">我的库存</h1>
      <!-- 页面副标题描述 -->
      <p class="hero-sub">共 {{ stats.total }} 件饰品，按分类 / 状态管理你的收藏</p>
    </div>

    <!-- 统计卡 -->
    <!-- 统计卡片行 -->
    <div class="stat-row">
      <!-- 总库存统计卡 -->
      <div class="stat-card">
        <!-- 总库存数量 -->
        <span class="stat-num">{{ stats.total }}</span>
        <!-- 总库存标签 -->
        <span class="stat-label">总库存</span>
      </div>
      <!-- 在库数量统计卡 -->
      <div class="stat-card">
        <!-- 在库数量（绿色） -->
        <span class="stat-num" style="color: #3ddc97">{{ stats.storage }}</span>
        <!-- 在库标签 -->
        <span class="stat-label">在库</span>
      </div>
      <!-- 在售数量统计卡 -->
      <div class="stat-card">
        <!-- 在售数量（橙色） -->
        <span class="stat-num" style="color: #ffb454">{{ stats.selling }}</span>
        <!-- 在售标签 -->
        <span class="stat-label">在售</span>
      </div>
      <!-- 交易中数量统计卡 -->
      <div class="stat-card">
        <!-- 交易中数量（红色） -->
        <span class="stat-num" style="color: #ff6b5e">{{ stats.frozen }}</span>
        <!-- 交易中标签 -->
        <span class="stat-label">交易中</span>
      </div>
    </div>

    <!-- 筛选区 -->
    <!-- 筛选面板 -->
    <div class="filter-panel">
      <!-- 筛选行 -->
      <div class="filter-row">
        <!-- 关键词筛选项 -->
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
          <!-- 武器分类下拉选择 -->
          <el-select v-model="queryParams.weaponCategoryId" placeholder="全部分类" clearable
            class="dark-select" popper-class="debuff-popper" @change="handleCategoryChange">
            <!-- 遍历分类选项 -->
            <el-option v-for="cat in categories" :key="cat.weaponCategory" :label="cat.categoryName"
              :value="cat.weaponCategory" />
          </el-select>
        </div>
        <!-- 武器筛选项 -->
        <div class="filter-item">
          <!-- 武器标签 -->
          <span class="filter-label">武器</span>
          <!-- 武器下拉选择 -->
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
          <!-- 品质下拉选择 -->
          <el-select v-model="queryParams.qualityId" placeholder="全部品质" clearable class="dark-select" popper-class="debuff-popper">
            <!-- 遍历品质选项 -->
            <el-option v-for="q in qualities" :key="q.id" :label="q.qualityName" :value="q.id">
              <!-- 品质名称带颜色 -->
              <span :style="{ color: q.qualityColor }">● {{ q.qualityName }}</span>
            </el-option>
          </el-select>
        </div>
        <!-- 状态筛选项 -->
        <div class="filter-item">
          <!-- 状态标签 -->
          <span class="filter-label">状态</span>
          <!-- 状态下拉选择 -->
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable class="dark-select" popper-class="debuff-popper">
            <!-- 遍历状态选项 -->
            <el-option v-for="(s, key) in statusMap" :key="key" :label="s.label" :value="Number(key)" />
          </el-select>
        </div>
        <!-- 筛选操作按钮 -->
        <div class="filter-item filter-actions">
          <!-- 搜索按钮 -->
          <button class="btn-primary" @click="handleQuery">搜索</button>
          <!-- 重置按钮 -->
          <button class="btn-ghost" @click="resetQuery">重置</button>
        </div>
      </div>
    </div>

    <!-- 表格 -->
    <!-- 库存表格容器 -->
    <div class="table-wrap">
      <!-- 库存数据表格 -->
      <el-table v-loading="loading" :data="inventoryList" class="debuff-table">
        <!-- 饰品列 -->
        <el-table-column label="饰品" align="left" min-width="180">
          <!-- 饰品自定义模板 -->
          <template #default="scope">
            <!-- 饰品单元格容器 -->
            <div class="skin-cell">
              <!-- 饰品缩略图 -->
              <div class="cell-img">
                <!-- 饰品图片 -->
                <el-image :src="resolveImgUrl(scope.row.imageUrl)" fit="cover"
                  style="width: 100%; height: 100%">
                  <!-- 图片加载失败占位 -->
                  <template #error><span class="cell-img-empty">SKIN</span></template>
                </el-image>
              </div>
              <!-- 饰品名称和武器信息 -->
              <div>
                <!-- 饰品名称 -->
                <div class="cell-name">{{ scope.row.skinName }}</div>
                <!-- 武器名称 -->
                <div class="cell-sub">{{ scope.row.weaponName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <!-- 品质列 -->
        <el-table-column label="品质" align="center" width="110">
          <!-- 品质自定义模板 -->
          <template #default="scope">
            <!-- 品质名称带颜色 -->
            <span class="quality" :style="{ color: scope.row.qualityColor }">● {{ scope.row.qualityName }}</span>
          </template>
        </el-table-column>
        <!-- 磨损列 -->
        <el-table-column label="磨损" align="center" width="160">
          <!-- 磨损自定义模板 -->
          <template #default="scope">
            <!-- 磨损信息单元格 -->
            <div class="wear-cell">
              <!-- 磨损名称 -->
              <span class="wear-name">{{ scope.row.wearName }}</span>
              <!-- 磨损数值 -->
              <span class="wear-float">{{ formatFloat(scope.row.floatValue) }}</span>
            </div>
          </template>
        </el-table-column>
        <!-- 贴纸列 -->
        <el-table-column label="贴纸" align="center" min-width="120" :show-overflow-tooltip="true">
          <!-- 贴纸自定义模板 -->
          <template #default="scope">
            <!-- 有贴纸信息时显示 -->
            <span v-if="scope.row.stickerInfo" class="sticker-text">{{ scope.row.stickerInfo }}</span>
            <!-- 无贴纸时显示 -->
            <span v-else class="muted-text">无</span>
          </template>
        </el-table-column>
        <!-- 来源列 -->
        <el-table-column label="来源" align="center" width="100">
          <!-- 来源自定义模板 -->
          <template #default="scope">
            <!-- 来源标签 -->
            <span class="source-chip">{{ sourceMap[scope.row.source] || scope.row.source }}</span>
          </template>
        </el-table-column>
        <!-- 状态列 -->
        <el-table-column label="状态" align="center" width="90">
          <!-- 状态自定义模板 -->
          <template #default="scope">
            <!-- 状态标签带样式类 -->
            <span class="status-chip" :class="'st-' + scope.row.status">
              <!-- 状态文字 -->
              {{ statusMap[scope.row.status]?.label }}
            </span>
          </template>
        </el-table-column>
        <!-- 获得时间列 -->
        <el-table-column label="获得时间" align="center" width="160" prop="createTime">
          <!-- 时间自定义模板 -->
          <template #default="scope">
            <!-- 格式化后的时间 -->
            <span class="muted-text">{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <!-- 操作列 -->
        <el-table-column label="操作" align="center" width="290" class-name="small-padding fixed-width">
          <!-- 操作自定义模板 -->
          <template #default="scope">
            <!-- 上架按钮：在库状态显示 -->
            <button class="link-btn link-green" v-if="scope.row.status === 0" @click="openShelfDialog(scope.row)">上架</button>
            <!-- 匹配求购按钮：在库状态显示 -->
            <button class="link-btn link-orange" v-if="scope.row.status === 0" @click="openMatchBuyOrderDialog(scope.row)">匹配求购</button>
            <!-- 下架按钮：在售状态显示 -->
            <button class="link-btn link-red" v-if="scope.row.status === 1" @click="handleOffShelf(scope.row)">下架</button>
            <!-- 详情按钮 -->
            <button class="link-btn" @click="handleDetail(scope.row)">详情</button>
            <!-- 修改按钮：交易中禁用 -->
            <button class="link-btn" :disabled="scope.row.status === 2"
              @click="handleEdit(scope.row)">
              <!-- 按钮文字：交易中或修改 -->
              {{ scope.row.status === 2 ? '交易中' : '修改' }}
            </button>
            <!-- 求购同款按钮 -->
            <button class="link-btn link-green" @click="openBuyDialog(scope.row)">求购同款</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 表格分页 -->
    <div class="pager">
      <!-- 分页组件 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 我的求购 -->
    <!-- 我的求购区域 -->
    <div class="my-buy-wrap">
      <!-- 我的求购头部 -->
      <div class="my-buy-head">
        <!-- 标题区域 -->
        <div>
          <!-- 求购标题 -->
          <div class="my-buy-title">我的求购</div>
          <!-- 求购说明文字 -->
          <div class="muted-text" style="font-size: 12px">挂出求购价，卖家可优先把饰品卖给你</div>
        </div>
        <!-- 发布求购按钮 -->
        <button class="btn-primary" @click="openPublishDialog">发布求购</button>
      </div>
      <!-- 我的求购表格 -->
      <el-table v-loading="buyLoading" :data="myBuyList" class="debuff-table">
        <!-- 求购饰品列 -->
        <el-table-column label="饰品" align="left" min-width="160">
          <!-- 饰品自定义模板 -->
          <template #default="scope">
            <!-- 饰品单元格 -->
            <div class="skin-cell">
              <!-- 饰品缩略图 -->
              <div class="cell-img">
                <!-- 饰品图片 -->
                <el-image :src="resolveImgUrl(scope.row.imageUrl)" fit="cover"
                  style="width: 100%; height: 100%">
                  <!-- 图片加载失败占位 -->
                  <template #error><span class="cell-img-empty">SKIN</span></template>
                </el-image>
              </div>
              <!-- 饰品名称和武器 -->
              <div>
                <!-- 饰品名称 -->
                <div class="cell-name">{{ scope.row.skinName }}</div>
                <!-- 武器名称 -->
                <div class="cell-sub">{{ scope.row.weaponName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <!-- 求购品质列 -->
        <el-table-column label="品质" align="center" width="110">
          <!-- 品质自定义模板 -->
          <template #default="scope">
            <!-- 品质名称带颜色 -->
            <span class="quality" :style="{ color: scope.row.qualityColor }">● {{ scope.row.qualityName }}</span>
          </template>
        </el-table-column>
        <!-- 求购价列 -->
        <el-table-column label="求购价" align="center" width="120">
          <!-- 求购价自定义模板 -->
          <template #default="scope">
            <!-- 格式化求购价格 -->
            <span class="buy-price">¥{{ Number(scope.row.price).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <!-- 求购状态列 -->
        <el-table-column label="状态" align="center" width="90">
          <!-- 状态自定义模板 -->
          <template #default="scope">
            <!-- 求购状态标签 -->
            <span class="status-chip" :class="'bo-' + scope.row.status">
              <!-- 状态文字 -->
              {{ buyStatusMap[scope.row.status]?.label }}
            </span>
          </template>
        </el-table-column>
        <!-- 发布时间列 -->
        <el-table-column label="发布时间" align="center" width="160">
          <!-- 时间自定义模板 -->
          <template #default="scope">
            <!-- 格式化时间 -->
            <span class="muted-text">{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <!-- 求购操作列 -->
        <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
          <!-- 操作自定义模板 -->
          <template #default="scope">
            <!-- 有效状态时显示操作按钮 -->
            <template v-if="scope.row.status === 0">
              <!-- 改价按钮 -->
              <button class="link-btn" @click="openPriceDialog(scope.row)">改价</button>
              <!-- 取消按钮 -->
              <button class="link-btn link-red" @click="handleCancelBuy(scope.row)">取消</button>
            </template>
            <!-- 非有效状态显示占位 -->
            <span v-else class="muted-text">--</span>
          </template>
        </el-table-column>
      </el-table>
      <!-- 求购列表分页 -->
      <div class="pager">
        <!-- 分页组件 -->
        <pagination v-show="buyTotal > 0" :total="buyTotal" v-model:page="buyQuery.pageNum"
          v-model:limit="buyQuery.pageSize" @pagination="getMyBuyOrders" />
      </div>
    </div>

    <!-- 详情弹窗 -->
    <!-- 库存详情对话框 -->
    <el-dialog v-model="detailOpen" title="库存详情" width="620px" append-to-body class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 有详情数据时显示描述列表 -->
      <el-descriptions v-if="detail" :column="2" border class="debuff-desc">
        <!-- 饰品名称 -->
        <el-descriptions-item label="饰品">{{ detail.skinName }}</el-descriptions-item>
        <!-- 武器名称 -->
        <el-descriptions-item label="武器">{{ detail.weaponName }}</el-descriptions-item>
        <!-- 品质信息 -->
        <el-descriptions-item label="品质">
          <!-- 品质名称带颜色 -->
          <span :style="{ color: detail.qualityColor }">● {{ detail.qualityName }}</span>
        </el-descriptions-item>
        <!-- 状态信息 -->
        <el-descriptions-item label="状态">
          <!-- 状态标签 -->
          <span class="status-chip" :class="'st-' + detail.status">{{ statusMap[detail.status]?.label }}</span>
        </el-descriptions-item>
        <!-- 磨损度信息 -->
        <el-descriptions-item label="磨损度">
          <!-- 磨损名称和数值 -->
          {{ detail.wearName }} ({{ formatFloat(detail.floatValue) }})
        </el-descriptions-item>
        <!-- 来源信息 -->
        <el-descriptions-item label="来源">{{ sourceMap[detail.source] || detail.source }}</el-descriptions-item>
        <!-- 检视链接 -->
        <el-descriptions-item label="检视链接" :span="2">
          <!-- 有链接时显示可点击链接 -->
          <el-link v-if="detail.inspectLink" :href="detail.inspectLink" target="_blank" type="primary">
            {{ detail.inspectLink }}
          </el-link>
          <!-- 无链接时显示 -->
          <span v-else class="muted-text">无</span>
        </el-descriptions-item>
        <!-- 贴纸信息 -->
        <el-descriptions-item label="贴纸信息" :span="2">
          <!-- 有贴纸时显示 -->
          <span v-if="detail.stickerInfo">{{ detail.stickerInfo }}</span>
          <!-- 无贴纸时显示 -->
          <span v-else class="muted-text">无</span>
        </el-descriptions-item>
        <!-- 获得时间 -->
        <el-descriptions-item label="获得时间">{{ parseTime(detail.createTime) }}</el-descriptions-item>
        <!-- 更新时间 -->
        <el-descriptions-item label="更新时间">{{ parseTime(detail.updateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 修改弹窗 -->
    <!-- 修改库存信息对话框 -->
    <el-dialog v-model="editOpen" title="修改库存信息" width="540px" append-to-body class="debuff-dialog" modal-class="debuff-overlay"
      @closed="resetForm">
      <!-- 修改表单 -->
      <el-form ref="editFormRef" :model="editForm" label-width="90px">
        <!-- 饰品名称（只读） -->
        <el-form-item label="饰品">
          <!-- 禁用的饰品名称输入框 -->
          <el-input :model-value="editForm.skinName" disabled />
        </el-form-item>
        <!-- 磨损度修改 -->
        <el-form-item label="磨损度">
          <!-- 磨损度数字输入框 -->
          <el-input-number v-model="editForm.floatValue" :min="0" :max="1" :precision="6" :step="0.01"
            :controls="false" style="width: 200px" />
          <!-- 修改提示 -->
          <span class="form-tip">修改后自动更新磨损名称</span>
        </el-form-item>
        <!-- 贴纸信息修改 -->
        <el-form-item label="贴纸信息">
          <!-- 贴纸信息文本域 -->
          <el-input v-model="editForm.stickerInfo" type="textarea" :rows="3"
            placeholder='贴纸信息，JSON格式，如：[{"name":"NaVi Holo"}]' />
        </el-form-item>
        <!-- 检视链接修改 -->
        <el-form-item label="检视链接">
          <!-- 检视链接输入框 -->
          <el-input v-model="editForm.inspectLink" placeholder="游戏内检视链接" />
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮 -->
      <template #footer>
        <!-- 保存修改按钮 -->
        <button class="btn-primary" style="padding: 9px 24px" @click="submitEdit">保存修改</button>
        <!-- 取消按钮 -->
        <button class="btn-ghost" style="padding: 9px 20px" @click="editOpen = false">取消</button>
      </template>
    </el-dialog>

    <!-- 上架弹窗 -->
    <!-- 上架出售对话框 -->
    <el-dialog v-model="shelfOpen" title="上架出售" width="420px" append-to-body class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 上架表单 -->
      <el-form label-width="80px">
        <!-- 饰品名称 -->
        <el-form-item label="饰品">
          <!-- 饰品名称显示 -->
          <span class="shelf-skin">{{ shelfForm.skinName }}</span>
        </el-form-item>
        <!-- 售价输入 -->
        <el-form-item label="售价">
          <!-- 售价数字输入框 -->
          <el-input-number v-model="shelfForm.price" :min="0.01" :max="1000000" :precision="2"
            :controls="false" placeholder="请输入售价" class="shelf-price-input" />
          <!-- 售价提示 -->
          <span class="form-tip">上架后买家可直接购买，平台收取 5% 手续费</span>
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮 -->
      <template #footer>
        <!-- 确认上架按钮 -->
        <button class="btn-primary" style="padding: 9px 24px" :disabled="shelfSubmitting" @click="submitShelf">确认上架</button>
        <!-- 取消按钮 -->
        <button class="btn-ghost" style="padding: 9px 20px" @click="shelfOpen = false">取消</button>
      </template>
    </el-dialog>

    <!-- 卖家选择具体库存后匹配求购 -->
    <!-- 匹配求购对话框 -->
    <el-dialog v-model="matchBuyOpen" title="匹配求购" width="620px" append-to-body class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 有匹配库存时显示 -->
      <template v-if="matchInventory">
        <!-- 匹配介绍区域 -->
        <div class="match-intro">
          <!-- 介绍标签 -->
          <div class="match-intro-label">本次出售库存</div>
          <!-- 饰品名称 -->
          <div class="match-skin-name">{{ matchInventory.skinName }}</div>
          <!-- 磨损和库存编号 -->
          <div class="muted-text">{{ matchInventory.wearName || '磨损未知' }} · 库存 #{{ matchInventory.id }}</div>
        </div>
        <!-- 求购订单列表表格 -->
        <el-table v-loading="matchBuyLoading" :data="matchBuyOrders" highlight-current-row @current-change="selectMatchBuyOrder" empty-text="暂无有效求购">
          <!-- 买家列 -->
          <el-table-column label="买家" min-width="120">
            <!-- 买家自定义模板 -->
            <template #default="scope">{{ scope.row.buyerName || '匿名用户' }}</template>
          </el-table-column>
          <!-- 求购价列 -->
          <el-table-column label="求购价" width="130" align="center">
            <!-- 求购价自定义模板 -->
            <template #default="scope"><span class="buy-price">¥{{ Number(scope.row.price).toFixed(2) }}</span></template>
          </el-table-column>
          <!-- 发布时间列 -->
          <el-table-column label="发布时间" width="170" align="center">
            <!-- 时间自定义模板 -->
            <template #default="scope"><span class="muted-text">{{ parseTime(scope.row.createTime) }}</span></template>
          </el-table-column>
          <!-- 选择列 -->
          <el-table-column label="选择" width="80" align="center">
            <!-- 选择自定义模板 -->
            <template #default="scope">
              <!-- 自定义单选按钮 -->
              <div class="match-radio" :class="{ 'is-checked': selectedMatchBuyOrderId === scope.row.id }" @click="selectMatchBuyOrder(scope.row)" />
            </template>
          </el-table-column>
        </el-table>
        <!-- 匹配提示信息 -->
        <div class="match-tip">成交价以选中的求购价为准，平台将收取 5% 服务费。确认后会创建交易订单。</div>
      </template>
      <!-- 对话框底部按钮 -->
      <template #footer>
        <!-- 确认出售按钮 -->
        <el-button type="primary" :disabled="!selectedMatchBuyOrder" :loading="matchSubmitting" @click="submitMatchBuyOrder">确认出售</el-button>
        <!-- 取消按钮 -->
        <el-button @click="matchBuyOpen = false">取消</el-button>
      </template>
    </el-dialog>

    <!-- 发布求购弹窗（含选择皮肤） -->
    <!-- 发布求购对话框 -->
    <el-dialog v-model="publishOpen" title="发布求购" width="460px" append-to-body class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 发布求购表单 -->
      <el-form label-width="80px">
        <!-- 饰品选择：非固定饰品时显示下拉 -->
        <el-form-item label="饰品" v-if="!publishForm.fixedSkin">
          <!-- 饰品搜索下拉框 -->
          <el-select v-model="publishForm.skinId" placeholder="搜索并选择饰品" filterable style="width: 100%">
            <!-- 遍历市场饰品选项 -->
            <el-option v-for="s in marketSkins" :key="s.skinId" :label="s.skinName + '（' + s.weaponName + '）'"
              :value="s.skinId" />
          </el-select>
        </el-form-item>
        <!-- 饰品选择：固定饰品时显示名称 -->
        <el-form-item label="饰品" v-else>
          <!-- 固定饰品名称 -->
          <span class="cell-name">{{ publishForm.skinName }}</span>
        </el-form-item>
        <!-- 求购价输入 -->
        <el-form-item label="求购价">
          <!-- 求购价数字输入框 -->
          <el-input-number v-model="publishForm.price" :min="0.01" :precision="2" :controls="false"
            style="width: 100%" />
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮 -->
      <template #footer>
        <!-- 发布求购按钮 -->
        <el-button type="primary" @click="submitPublish">发布求购</el-button>
        <!-- 取消按钮 -->
        <el-button @click="publishOpen = false">取消</el-button>
      </template>
    </el-dialog>

    <!-- 改价弹窗 -->
    <!-- 修改求购价对话框 -->
    <el-dialog v-model="priceOpen" title="修改求购价" width="380px" append-to-body class="debuff-dialog" modal-class="debuff-overlay">
      <!-- 改价表单 -->
      <el-form label-width="80px">
        <!-- 饰品名称 -->
        <el-form-item label="饰品">
          <!-- 饰品名称显示 -->
          <span class="cell-name">{{ priceForm.skinName }}</span>
        </el-form-item>
        <!-- 求购价输入 -->
        <el-form-item label="求购价">
          <!-- 求购价数字输入框 -->
          <el-input-number v-model="priceForm.price" :min="0.01" :precision="2" :controls="false"
            style="width: 100%" />
        </el-form-item>
      </el-form>
      <!-- 对话框底部按钮 -->
      <template #footer>
        <!-- 保存按钮 -->
        <el-button type="primary" @click="submitPrice">保存</el-button>
        <!-- 取消按钮 -->
        <el-button @click="priceOpen = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Inventory">
// 引入Vue组合式API
import { ref, reactive, onMounted, onActivated, watch } from 'vue'
// 引入Element Plus消息和确认框
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入库存相关API
import { listInventory, getInventory, updateInventory } from '@/api/inventory'
// 引入市场和分类API
import { getCategories, listMarket } from '@/api/market'
// 引入求购订单相关API
import { acceptBuyOrder, createBuyOrder, listMyBuyOrders, listSkinBuyOrders, updateBuyOrderPrice, cancelBuyOrder } from '@/api/buyOrder'
// 引入上架下架API
import { createListing, offShelfListing } from '@/api/listing'
// 引入图片URL解析工具
import { resolveImgUrl } from '@/utils/img'

// 加载状态标识
const loading = ref(false)
// 库存列表数据
const inventoryList = ref([])
// 库存总数
const total = ref(0)
// 武器分类列表
const categories = ref([])
// 品质列表
const qualities = ref([])
// 武器类型列表
const weaponTypes = ref([])
// 统计数据响应式对象
const stats = reactive({ total: 0, storage: 0, selling: 0, frozen: 0 })

// 库存状态映射表
const statusMap = {
  // 在库状态
  0: { label: '在库', color: '#3ddc97' },
  // 在售状态
  1: { label: '在售', color: '#ffb454' },
  // 冻结状态
  2: { label: '冻结', color: '#ff6b5e' }
}
// 来源映射表
const sourceMap = {
  // 购买获得
  purchase: '购买获得',
  // 上传入库
  upload: '上传入库',
  // 交易获得
  trade: '交易获得'
}

// 查询参数响应式对象
const queryParams = reactive({
  // 当前页码
  pageNum: 1,
  // 每页条数
  pageSize: 10,
  // 搜索关键词
  keyword: undefined,
  // 武器分类ID
  weaponCategoryId: undefined,
  // 武器类型ID
  weaponTypeId: undefined,
  // 品质ID
  qualityId: undefined,
  // 状态筛选
  status: undefined
})

// 分类变更时更新武器类型
const handleCategoryChange = (val) => {
  // 根据分类查找武器类型
  weaponTypes.value = val
    ? (categories.value.find(c => c.weaponCategory === val)?.weaponTypes || [])
    : []
  // 清空武器类型选择
  queryParams.weaponTypeId = undefined
}

// 获取库存列表
const getList = async () => {
  // 开启加载状态
  loading.value = true
  // 尝试请求数据
  try {
    // 调用库存列表API
    const res = await listInventory(queryParams)
    // 赋值库存列表
    inventoryList.value = res.rows || []
    // 赋值总数
    total.value = res.total || 0
  } finally {
    // 关闭加载状态
    loading.value = false
  }
}

// 统计全量库存（不传分页参数返回全部）
// 加载统计数据
const loadStats = async () => {
  // 获取全量库存
  const res = await listInventory({})
  // 获取所有行
  const rows = res.rows || []
  // 计算总数
  stats.total = rows.length
  // 计算在库数
  stats.storage = rows.filter(r => r.status === 0).length
  // 计算在售数
  stats.selling = rows.filter(r => r.status === 1).length
  // 计算冻结数
  stats.frozen = rows.filter(r => r.status === 2).length
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

// 执行搜索
const handleQuery = () => {
  // 重置页码
  queryParams.pageNum = 1
  // 获取列表
  getList()
}

// 重置筛选条件
const resetQuery = () => {
  // 清空关键词
  queryParams.keyword = undefined
  // 清空分类
  queryParams.weaponCategoryId = undefined
  // 清空武器
  queryParams.weaponTypeId = undefined
  // 清空品质
  queryParams.qualityId = undefined
  // 清空状态
  queryParams.status = undefined
  // 清空武器类型列表
  weaponTypes.value = []
  // 重新查询
  handleQuery()
}

// 格式化磨损度数值
const formatFloat = (v) => {
  // 空值返回占位符
  if (v === null || v === undefined) return '--'
  // 保留6位小数并去除末尾多余的0
  return Number(v).toFixed(6).replace(/0+$/, '').replace(/.$/, '')
}

// ---------- 详情 ----------
// 详情弹窗显示状态
const detailOpen = ref(false)
// 详情数据
const detail = ref(null)
// 查看详情
const handleDetail = async (row) => {
  // 获取库存详情
  const res = await getInventory(row.id)
  // 赋值详情数据
  detail.value = res.data
  // 打开弹窗
  detailOpen.value = true
}

// ---------- 修改 ----------
// 修改弹窗显示状态
const editOpen = ref(false)
// 修改表单引用
const editFormRef = ref(null)
// 修改表单数据
const editForm = reactive({})

// 打开修改弹窗
const handleEdit = (row) => {
  // 填充表单数据
  Object.assign(editForm, {
    // 库存ID
    id: row.id,
    // 饰品名称
    skinName: row.skinName,
    // 磨损度数值
    floatValue: row.floatValue === null || row.floatValue === undefined ? 0 : Number(row.floatValue),
    // 贴纸信息
    stickerInfo: row.stickerInfo || '',
    // 检视链接
    inspectLink: row.inspectLink || ''
  })
  // 打开弹窗
  editOpen.value = true
}

// 提交修改
const submitEdit = async () => {
  // 调用更新API
  const res = await updateInventory(editForm)
  // 修改成功
  if (res.code === 200) {
    // 显示成功提示
    ElMessage.success('修改成功')
    // 关闭弹窗
    editOpen.value = false
    // 刷新列表
    getList()
    // 刷新统计
    loadStats()
  } else {
    // 显示失败提示
    ElMessage.error(res.msg || '修改失败')
  }
}

// 重置表单
const resetForm = () => {
  // 清空表单所有字段
  Object.keys(editForm).forEach(k => delete editForm[k])
}

// ---------- 上架/下架 ----------
// 上架弹窗显示状态
const shelfOpen = ref(false)
// 上架提交中状态
const shelfSubmitting = ref(false)
// 上架表单数据
const shelfForm = reactive({ inventoryId: undefined, skinName: '', price: undefined })

// 打开上架弹窗
const openShelfDialog = (row) => {
  // 填充上架表单
  Object.assign(shelfForm, {
    // 库存ID
    inventoryId: row.id,
    // 饰品名称
    skinName: row.skinName,
    // 价格清空
    price: undefined
  })
  // 打开弹窗
  shelfOpen.value = true
}

// 提交上架
const submitShelf = async () => {
  // 验证价格有效性
  if (!shelfForm.price || shelfForm.price <= 0) {
    // 提示输入有效价格
    ElMessage.warning('请输入有效的售价')
    // 返回不执行
    return
  }
  // 开启提交中状态
  shelfSubmitting.value = true
  // 尝试提交
  try {
    // 调用上架API
    const res = await createListing({ inventoryId: shelfForm.inventoryId, price: shelfForm.price })
    // 上架成功
    if (res.code === 200) {
      // 显示成功提示
      ElMessage.success('上架成功，已展示到市场')
      // 关闭弹窗
      shelfOpen.value = false
      // 刷新列表
      getList()
      // 刷新统计
      loadStats()
    } else {
      // 显示失败提示
      ElMessage.error(res.msg || '上架失败')
    }
  } finally {
    // 关闭提交中状态
    shelfSubmitting.value = false
  }
}

// 处理下架操作
const handleOffShelf = (row) => {
  // 弹出下架确认框
  ElMessageBox.confirm(`确定下架「${row.skinName}」吗？下架后回到库存，不再展示在市场中。`, '下架确认', {
    // 确认按钮文字
    confirmButtonText: '确定下架',
    // 取消按钮文字
    cancelButtonText: '取消',
    // 警告类型
    type: 'warning'
  }).then(async () => {
    // 调用下架API
    const res = await offShelfListing(row.id)
    // 下架成功
    if (res.code === 200) {
      // 显示成功提示
      ElMessage.success('已下架')
      // 刷新列表
      getList()
      // 刷新统计
      loadStats()
    } else {
      // 显示失败提示
      ElMessage.error(res.msg || '下架失败')
    }
  }).catch(() => {})
}

// ---------- 匹配求购（卖家砸单） ----------
// 匹配求购弹窗显示状态
const matchBuyOpen = ref(false)
// 匹配求购加载中状态
const matchBuyLoading = ref(false)
// 匹配提交中状态
const matchSubmitting = ref(false)
// 当前匹配的库存
const matchInventory = ref(null)
// 匹配到的求购订单列表
const matchBuyOrders = ref([])
// 选中的求购订单
const selectedMatchBuyOrder = ref(null)
// 选中的求购订单ID
const selectedMatchBuyOrderId = ref(null)

// 打开匹配求购弹窗
const openMatchBuyOrderDialog = async (row) => {
  // 设置匹配库存
  matchInventory.value = row
  // 清空选中订单
  selectedMatchBuyOrder.value = null
  // 清空选中ID
  selectedMatchBuyOrderId.value = null
  // 清空求购列表
  matchBuyOrders.value = []
  // 打开弹窗
  matchBuyOpen.value = true
  // 开启加载状态
  matchBuyLoading.value = true
  // 尝试加载求购订单
  try {
    // 调用求购订单列表API
    const res = await listSkinBuyOrders(row.skinId)
    // 赋值求购订单数据
    matchBuyOrders.value = res.data || []
  } finally {
    // 关闭加载状态
    matchBuyLoading.value = false
  }
}

// 选择求购订单
const selectMatchBuyOrder = (row) => {
  // 设置选中的订单
  selectedMatchBuyOrder.value = row || null
  // 设置选中的订单ID
  selectedMatchBuyOrderId.value = row?.id ?? null
}

// 监听选中ID变化
watch(selectedMatchBuyOrderId, (id) => {
  // 根据ID查找对应订单
  selectedMatchBuyOrder.value = id ? matchBuyOrders.value.find(o => o.id === id) ?? null : null
})

// 提交匹配求购
const submitMatchBuyOrder = async () => {
  // 无库存或未选择订单时返回
  if (!matchInventory.value || !selectedMatchBuyOrder.value) return
  // 开启提交中状态
  matchSubmitting.value = true
  // 尝试提交
  try {
    // 弹出确认框
    await ElMessageBox.confirm(
      `确认以 ¥${Number(selectedMatchBuyOrder.value.price).toFixed(2)} 将「${matchInventory.value.skinName}」出售给 ${selectedMatchBuyOrder.value.buyerName || '该买家'} 吗？`,
      '确认匹配求购',
      { confirmButtonText: '确认出售', cancelButtonText: '取消', type: 'warning' }
    )
    // 调用接受求购订单API
    const res = await acceptBuyOrder(selectedMatchBuyOrder.value.id, matchInventory.value.id)
    // 操作成功
    if (res.code === 200) {
      // 显示成功提示
      ElMessage.success('交易订单已创建，请前往我的交易查看')
      // 关闭弹窗
      matchBuyOpen.value = false
      // 刷新列表
      getList()
      // 刷新统计
      loadStats()
    }
  } catch (e) {
    // 用户取消或后端业务校验失败均不关闭弹窗，方便重新选择。
  } finally {
    // 关闭提交中状态
    matchSubmitting.value = false
  }
}

// ---------- 求购 ----------
// 求购加载中状态
const buyLoading = ref(false)
// 我的求购列表
const myBuyList = ref([])
// 求购总数
const buyTotal = ref(0)
// 求购查询参数
const buyQuery = reactive({ pageNum: 1, pageSize: 5 })
// 市场饰品列表（用于发布求购选择）
const marketSkins = ref([])

// 求购状态映射表
const buyStatusMap = {
  // 有效状态
  0: { label: '有效', color: '#3ddc97' },
  // 已完成状态
  1: { label: '已完成', color: '#8f9ab3' },
  // 已取消状态
  2: { label: '已取消', color: '#5b6680' }
}

// 获取我的求购订单
const getMyBuyOrders = async () => {
  // 开启加载状态
  buyLoading.value = true
  // 尝试请求数据
  try {
    // 调用我的求购列表API
    const res = await listMyBuyOrders(buyQuery)
    // 赋值求购列表
    myBuyList.value = res.rows || []
    // 赋值总数
    buyTotal.value = res.total || 0
  } finally {
    // 关闭加载状态
    buyLoading.value = false
  }
}

// 求购同款 / 从市场发布
// 发布求购弹窗显示状态
const publishOpen = ref(false)
// 发布求购表单数据
const publishForm = reactive({ skinId: undefined, skinName: '', price: 100, fixedSkin: false })
// 打开发布求购弹窗
const openPublishDialog = async () => {
  // 重置为自由选择模式
  publishForm.fixedSkin = false
  // 清空饰品ID
  publishForm.skinId = undefined
  // 清空饰品名称
  publishForm.skinName = ''
  // 默认价格
  publishForm.price = 100
  // 首次打开时加载市场饰品
  if (marketSkins.value.length === 0) {
    // 调用市场列表API
    const res = await listMarket({ pageNum: 1, pageSize: 100 })
    // 赋值市场饰品
    marketSkins.value = res.rows || []
  }
  // 打开弹窗
  publishOpen.value = true
}

// 从库存行打开求购弹窗
const openBuyDialog = (row) => {
  // 固定饰品模式
  publishForm.fixedSkin = true
  // 设置饰品ID
  publishForm.skinId = row.skinId
  // 设置饰品名称
  publishForm.skinName = row.skinName
  // 默认价格
  publishForm.price = 100
  // 打开弹窗
  publishOpen.value = true
}

// 提交发布求购
const submitPublish = async () => {
  // 验证是否选择了饰品
  if (!publishForm.skinId) {
    // 提示选择饰品
    ElMessage.warning('请选择饰品')
    // 返回不执行
    return
  }
  // 验证价格有效性
  if (!publishForm.price || publishForm.price <= 0) {
    // 提示输入有效价格
    ElMessage.warning('请输入有效的求购价')
    // 返回不执行
    return
  }
  // 调用创建求购订单API
  const res = await createBuyOrder({ skinId: publishForm.skinId, price: publishForm.price })
  // 创建成功
  if (res.code === 200) {
    // 显示成功提示
    ElMessage.success('求购已发布')
    // 关闭弹窗
    publishOpen.value = false
    // 刷新我的求购列表
    getMyBuyOrders()
  } else {
    // 显示失败提示
    ElMessage.error(res.msg || '发布失败')
  }
}

// 改价 / 取消
// 改价弹窗显示状态
const priceOpen = ref(false)
// 改价表单数据
const priceForm = reactive({ id: undefined, skinName: '', price: 0 })
// 打开改价弹窗
const openPriceDialog = (row) => {
  // 设置求购订单ID
  priceForm.id = row.id
  // 设置饰品名称
  priceForm.skinName = row.skinName
  // 设置当前价格
  priceForm.price = Number(row.price)
  // 打开弹窗
  priceOpen.value = true
}

// 提交改价
const submitPrice = async () => {
  // 验证价格有效性
  if (!priceForm.price || priceForm.price <= 0) {
    // 提示输入有效价格
    ElMessage.warning('请输入有效的求购价')
    // 返回不执行
    return
  }
  // 调用更新求购价API
  const res = await updateBuyOrderPrice(priceForm.id, priceForm.price)
  // 改价成功
  if (res.code === 200) {
    // 显示成功提示
    ElMessage.success('改价成功')
    // 关闭弹窗
    priceOpen.value = false
    // 刷新我的求购列表
    getMyBuyOrders()
  } else {
    // 显示失败提示
    ElMessage.error(res.msg || '改价失败')
  }
}

// 取消求购
const handleCancelBuy = (row) => {
  // 弹出取消确认框
  ElMessageBox.confirm('确定取消对「' + row.skinName + '」的求购吗？', '系统提示', {
    // 确认按钮文字
    confirmButtonText: '确定',
    // 取消按钮文字
    cancelButtonText: '取消',
    // 警告类型
    type: 'warning'
  }).then(async () => {
    // 调用取消求购API
    const res = await cancelBuyOrder(row.id)
    // 取消成功
    if (res.code === 200) {
      // 显示成功提示
      ElMessage.success('已取消求购')
      // 刷新我的求购列表
      getMyBuyOrders()
    } else {
      // 显示失败提示
      ElMessage.error(res.msg || '取消失败')
    }
  }).catch(() => {})
}

// 组件挂载时初始化数据
onMounted(() => {
  // 加载分类数据
  loadCategories()
  // 加载统计数据
  loadStats()
  // 加载库存列表
  getList()
  // 加载我的求购列表
  getMyBuyOrders()
})
// keep-alive缓存激活时重新加载数据
onActivated(() => {
  loadCategories()
  loadStats()
  getList()
  getMyBuyOrders()
})
</script>

<style scoped>
/* 库存页面根容器样式 */
.inventory-page {
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
  /* 最小高度 */
  min-height: calc(100vh - 84px);
  /* 渐变背景 */
  background:
    radial-gradient(900px 360px at 90% -10%, rgba(61, 220, 151, 0.07), transparent 60%),
    var(--bg);
  /* 内边距 */
  padding: 24px 28px 32px;
  /* Element Plus 背景色 */
  --el-bg-color: var(--card);
  /* Element Plus 覆盖层背景色 */
  --el-bg-color-overlay: var(--card-hover);
  /* Element Plus 边框颜色 */
  --el-border-color: var(--border);
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

/* 英雄区容器 */
.inv-hero {
  /* 内边距 */
  padding: 8px 4px 18px;
}
/* 英文眉标文字 */
.hero-eyebrow {
  /* 字体大小 */
  font-size: 12px;
  /* 字间距 */
  letter-spacing: 4px;
  /* 文字颜色 */
  color: var(--muted);
  /* 大写变换 */
  text-transform: uppercase;
}
/* 页面主标题 */
.hero-title {
  /* 外边距 */
  margin: 6px 0 4px;
  /* 字体大小 */
  font-size: 30px;
  /* 字体粗细 */
  font-weight: 800;
  /* 字间距 */
  letter-spacing: 1px;
}
/* 页面副标题 */
.hero-sub {
  /* 无外边距 */
  margin: 0;
  /* 字体大小 */
  font-size: 13px;
  /* 文字颜色 */
  color: var(--muted);
}

/* 统计卡 */
/* 统计卡片行 */
.stat-row {
  /* 网格布局 */
  display: grid;
  /* 4列等宽 */
  grid-template-columns: repeat(4, 1fr);
  /* 间距 */
  gap: 14px;
  /* 底部外边距 */
  margin-bottom: 18px;
}
/* 单个统计卡片 */
.stat-card {
  /* 卡片背景 */
  background: var(--card);
  /* 边框 */
  border: 1px solid var(--border);
  /* 圆角 */
  border-radius: 10px;
  /* 内边距 */
  padding: 16px 20px;
  /* 弹性布局 */
  display: flex;
  /* 基线对齐 */
  align-items: baseline;
  /* 间距 */
  gap: 10px;
}
/* 统计数字 */
.stat-num {
  /* 大号字体 */
  font-size: 28px;
  /* 超粗字体 */
  font-weight: 800;
  /* 等宽数字 */
  font-variant-numeric: tabular-nums;
}
/* 统计标签 */
.stat-label {
  /* 字体大小 */
  font-size: 13px;
  /* 次要文字颜色 */
  color: var(--muted);
}

/* 筛选区 */
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
  margin-bottom: 16px;
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
  /* 次要文字颜色 */
  color: var(--muted);
  /* 不换行 */
  white-space: nowrap;
}
/* 暗色输入框宽度 */
.dark-input { width: 180px; }
/* 暗色选择器宽度 */
.dark-select { width: 150px; }
/* 筛选操作按钮靠右 */
.filter-actions { margin-left: auto; }
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
.btn-primary:hover { filter: brightness(1.12); }
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

/* 表格 */
/* 表格容器 */
.table-wrap {
  /* 卡片背景 */
  background: var(--card);
  /* 边框 */
  border: 1px solid var(--border);
  /* 圆角 */
  border-radius: 10px;
  /* 内边距 */
  padding: 6px 10px;
  /* 溢出隐藏 */
  overflow: hidden;
}
/* 饰品单元格 */
.skin-cell {
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 间距 */
  gap: 10px;
}
/* 单元格缩略图 */
.cell-img {
  /* 固定宽度 */
  width: 44px;
  /* 固定高度 */
  height: 44px;
  /* 圆角 */
  border-radius: 6px;
  /* 溢出隐藏 */
  overflow: hidden;
  /* 深色背景 */
  background: #0a0f1a;
  /* 不缩小 */
  flex-shrink: 0;
}
/* 缩略图空占位 */
.cell-img-empty {
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 水平居中 */
  justify-content: center;
  /* 撑满高度 */
  height: 100%;
  /* 小号字体 */
  font-size: 9px;
  /* 字间距 */
  letter-spacing: 2px;
  /* 暗色文字 */
  color: #4a5570;
}
/* 单元格饰品名称 */
.cell-name {
  /* 字体大小 */
  font-size: 14px;
  /* 字体加粗 */
  font-weight: 600;
}
/* 单元格副标题 */
.cell-sub {
  /* 字体大小 */
  font-size: 12px;
  /* 次要文字颜色 */
  color: var(--muted);
}
/* 品质名称 */
.quality {
  /* 字体加粗 */
  font-weight: 600;
  /* 字体大小 */
  font-size: 13px;
}
/* 磨损信息单元格 */
.wear-cell {
  /* 纵向弹性布局 */
  display: flex;
  /* 纵向排列 */
  flex-direction: column;
  /* 行高 */
  line-height: 1.4;
}
/* 磨损名称 */
.wear-name {
  /* 字体大小 */
  font-size: 13px;
}
/* 磨损数值 */
.wear-float {
  /* 字体大小 */
  font-size: 11px;
  /* 次要文字颜色 */
  color: var(--muted);
  /* 等宽数字 */
  font-variant-numeric: tabular-nums;
}
/* 贴纸信息文字 */
.sticker-text {
  /* 字体大小 */
  font-size: 12px;
  /* 蓝色文字 */
  color: #9db4d8;
}
/* 次要文字样式 */
.muted-text {
  /* 次要颜色 */
  color: var(--muted);
  /* 小号字体 */
  font-size: 12px;
}
/* 来源标签 */
.source-chip {
  /* 字体大小 */
  font-size: 12px;
  /* 灰色文字 */
  color: #4f6175;
  /* 浅灰背景 */
  background: #f0f4f8;
  /* 边框 */
  border: 1px solid #dce4ed;
  /* 内边距 */
  padding: 2px 10px;
  /* 圆角 */
  border-radius: 20px;
}
/* 状态标签 */
.status-chip {
  /* 字体大小 */
  font-size: 12px;
  /* 内边距 */
  padding: 2px 10px;
  /* 圆角 */
  border-radius: 20px;
  /* 边框 */
  border: 1px solid;
}
/* 在库状态样式 */
.st-0 { color: #3ddc97; border-color: rgba(61, 220, 151, 0.4); background: rgba(61, 220, 151, 0.08); }
/* 在售状态样式 */
.st-1 { color: #ffb454; border-color: rgba(255, 180, 84, 0.4); background: rgba(255, 180, 84, 0.08); }
/* 冻结状态样式 */
.st-2 { color: #ff6b5e; border-color: rgba(255, 107, 94, 0.4); background: rgba(255, 107, 94, 0.08); }
/* 链接按钮样式 */
.link-btn {
  /* 无背景 */
  background: none;
  /* 无边框 */
  border: none;
  /* 蓝色文字 */
  color: #4d9fff;
  /* 字体大小 */
  font-size: 13px;
  /* 手型鼠标 */
  cursor: pointer;
  /* 内边距 */
  padding: 2px 6px;
}
/* 链接按钮悬停 */
.link-btn:hover { text-decoration: underline; }
/* 链接按钮禁用 */
.link-btn:disabled {
  /* 灰色文字 */
  color: #5b6680;
  /* 禁止鼠标 */
  cursor: not-allowed;
  /* 无下划线 */
  text-decoration: none;
}
/* 绿色链接按钮 */
.link-green { color: #3ddc97; }
/* 橙色链接按钮 */
.link-orange { color: #ffb454; }
/* 红色链接按钮 */
.link-red { color: #ff6b5e; }
/* 求购有效状态 */
.bo-0 { color: #3ddc97; border-color: rgba(61, 220, 151, 0.4); background: rgba(61, 220, 151, 0.08); }
/* 求购完成状态 */
.bo-1 { color: #8f9ab3; border-color: rgba(143, 154, 179, 0.4); background: rgba(143, 154, 179, 0.08); }
/* 求购取消状态 */
.bo-2 { color: #5b6680; border-color: rgba(91, 102, 128, 0.4); background: rgba(91, 102, 128, 0.08); }
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
/* 分页容器 */
.pager {
  /* 顶部外边距 */
  margin-top: 16px;
  /* 弹性布局 */
  display: flex;
  /* 右对齐 */
  justify-content: flex-end;
}
/* 我的求购 */
/* 我的求购区域 */
.my-buy-wrap {
  /* 顶部外边距 */
  margin-top: 22px;
  /* 卡片背景 */
  background: var(--card);
  /* 边框 */
  border: 1px solid var(--border);
  /* 圆角 */
  border-radius: 10px;
  /* 内边距 */
  padding: 18px 18px 6px;
}
/* 我的求购头部 */
.my-buy-head {
  /* 弹性布局 */
  display: flex;
  /* 垂直居中 */
  align-items: center;
  /* 两端对齐 */
  justify-content: space-between;
  /* 底部外边距 */
  margin-bottom: 14px;
}
/* 我的求购标题 */
.my-buy-title {
  /* 字体大小 */
  font-size: 17px;
  /* 字体加粗 */
  font-weight: 700;
}
/* 表单提示文字 */
.form-tip {
  /* 左侧外边距 */
  margin-left: 10px;
  /* 字体大小 */
  font-size: 12px;
  /* 次要文字颜色 */
  color: var(--muted);
}
/* 上架饰品名称 */
.shelf-skin {
  /* 字体大小 */
  font-size: 14px;
  /* 字体加粗 */
  font-weight: 600;
  /* 主文字颜色 */
  color: var(--text);
}
/* 上架价格输入框宽度 */
.shelf-price-input { width: 100%; }
/* 匹配介绍区域 */
.match-intro { margin: 0 0 16px; padding: 12px 14px; border: 1px solid var(--border); border-radius: 8px; background: #10182a; }
/* 匹配介绍标签 */
.match-intro-label { margin-bottom: 4px; color: var(--muted); font-size: 12px; }
/* 匹配饰品名称 */
.match-skin-name { margin-bottom: 4px; font-size: 16px; font-weight: 700; color: var(--text); }
/* 匹配提示信息 */
.match-tip { margin-top: 12px; color: var(--muted); font-size: 12px; line-height: 1.6; }
</style>

<style>
/* 亮色主题英雄区统一样式 */
body.user-surface .inventory-page .inv-hero,
body.user-surface .orders-page .ord-hero,
body.user-surface .wallet-page .wallet-hero {
  /* 内边距 */
  padding: 26px 28px 22px !important;
  /* 白色背景 */
  background: #ffffff !important;
  /* 边框 */
  border: 1px solid #e3e8ef;
  /* 圆角 */
  border-radius: 4px;
  /* 底部外边距 */
  margin-bottom: 16px;
}
/* 亮色主题眉标文字 */
body.user-surface .inventory-page .hero-eyebrow,
body.user-surface .orders-page .hero-eyebrow,
body.user-surface .wallet-page .hero-eyebrow { color: #6b8db4 !important; letter-spacing: 2px; }
/* 亮色主题主标题 */
body.user-surface .inventory-page .hero-title,
body.user-surface .orders-page .hero-title,
body.user-surface .wallet-page .hero-title { color: #1f2d3d !important; font-size: 25px; letter-spacing: 0; }
/* 亮色主题副标题 */
body.user-surface .inventory-page .hero-sub,
body.user-surface .orders-page .hero-sub,
body.user-surface .wallet-page .hero-sub { color: #8290a3 !important; }
/* 亮色主题统计行间距 */
body.user-surface .inventory-page .stat-row,
body.user-surface .orders-page .stat-row { gap: 12px; }
/* 亮色主题统计卡片 */
body.user-surface .inventory-page .stat-card,
body.user-surface .orders-page .stat-card { border-radius: 4px !important; padding: 14px 18px; }
/* 亮色主题统计数字 */
body.user-surface .inventory-page .stat-num,
body.user-surface .orders-page .stat-num { color: #2f80ed; }
/* 亮色主题表格和求购区域 */
body.user-surface .inventory-page .table-wrap,
body.user-surface .inventory-page .my-buy-wrap,
body.user-surface .orders-page .table-wrap { border-radius: 4px !important; padding: 4px 8px; }
/* 亮色主题求购区域外边距 */
body.user-surface .inventory-page .my-buy-wrap { margin-top: 18px; }
/* 亮色主题下拉面板 */
body.user-surface .inventory-page .el-popper,
body.user-surface .inventory-page .el-select-dropdown,
body.user-surface .orders-page .el-popper,
body.user-surface .orders-page .el-select-dropdown {
  /* 白色背景 */
  background: #ffffff !important;
  /* 浅灰边框 */
  border-color: #e3e8ef !important;
}
/* 亮色主题下拉项 */
body.user-surface .inventory-page .el-select-dropdown__item,
body.user-surface .orders-page .el-select-dropdown__item { color: #526275 !important; }
/* 亮色主题下拉项悬停 */
body.user-surface .inventory-page .el-select-dropdown__item.is-hovering,
body.user-surface .orders-page .el-select-dropdown__item.is-hovering { background: #f0f5fb !important; }
/* 亮色主题下拉项选中 */
body.user-surface .inventory-page .el-select-dropdown__item.is-selected,
body.user-surface .orders-page .el-select-dropdown__item.is-selected { color: #2f80ed !important; }

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
/* 暗色下拉项文字 */
.debuff-popper .el-select-dropdown__item {
  /* 强制浅色文字 */
  color: #c2cad9 !important;
}
/* 暗色下拉项悬停 */
.debuff-popper .el-select-dropdown__item.is-hovering {
  /* 强制深色悬停背景 */
  background: #1f2b45 !important;
}
/* 暗色下拉项选中 */
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
/* 暗色下拉滚动条 */
.debuff-popper .el-scrollbar__bar.is-horizontal,
.debuff-popper .el-scrollbar__bar.is-vertical {
  /* 强制深色滚动条 */
  background: #2a3654 !important;
}
/* 暗色下拉箭头 */
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
/* 弹窗暗色覆盖（teleport 到 body） */
/* 暗色弹窗样式 */
.debuff-dialog {
  /* 深色背景 */
  background: #161e30;
  /* 深色边框 */
  border: 1px solid #2a3654;
  /* 圆角 */
  border-radius: 12px;
}
/* 弹窗遮罩层 */
.debuff-overlay {
  /* 半透明黑色遮罩 */
  background: rgba(6, 9, 16, 0.72);
}
/* 弹窗标题 */
.debuff-dialog .el-dialog__title {
  /* 浅色文字 */
  color: #e9edf6;
  /* 字体加粗 */
  font-weight: 600;
}
/* 弹窗关闭按钮 */
.debuff-dialog .el-dialog__headerbtn .el-dialog__close {
  /* 灰色文字 */
  color: #8f9ab3;
}
/* 弹窗内描述列表 */
.debuff-dialog .el-descriptions {
  /* 表格边框 */
  --el-descriptions-table-border: 1px solid #232e45;
  /* 标签背景 */
  --el-descriptions-item-bordered-label-background: #1a2338;
}
/* 弹窗描述标签 */
.debuff-dialog .el-descriptions__label {
  /* 强制灰色文字 */
  color: #8f9ab3 !important;
  /* 强制深色背景 */
  background: #1a2338 !important;
}
/* 弹窗描述内容 */
.debuff-dialog .el-descriptions__content {
  /* 强制浅色文字 */
  color: #c2cad9 !important;
  /* 强制深色背景 */
  background: #161e30 !important;
}
/* 弹窗内输入框和文本域 */
.debuff-dialog .el-input__wrapper,
.debuff-dialog .el-textarea__inner {
  /* 深色背景 */
  background-color: #0d1220;
  /* 深色边框 */
  box-shadow: 0 0 0 1px #2a3654 inset;
}
/* 弹窗内输入框文字 */
.debuff-dialog .el-input__inner {
  /* 浅色文字 */
  color: #e9edf6;
}
/* 弹窗内文本域文字 */
.debuff-dialog .el-textarea__inner {
  /* 浅色文字 */
  color: #e9edf6;
}
/* 弹窗内单选按钮选中 */
.debuff-dialog .el-radio__input.is-checked .el-radio__inner {
  /* 蓝色边框 */
  border-color: #4d9fff;
  /* 蓝色背景 */
  background: #4d9fff;
}
/* 弹窗内单选按钮选中标签 */
.debuff-dialog .el-radio__input.is-checked + .el-radio__label {
  /* 浅色文字 */
  color: #e9edf6;
}
/* 自定义匹配单选按钮 */
.match-radio {
  /* 固定宽度 */
  width: 18px;
  /* 固定高度 */
  height: 18px;
  /* 圆形 */
  border-radius: 50%;
  /* 灰色边框 */
  border: 2px solid #8f9ab3;
  /* 水平居中 */
  margin: 0 auto;
  /* 手型鼠标 */
  cursor: pointer;
  /* 过渡动画 */
  transition: all 0.2s;
  /* 盒模型 */
  box-sizing: border-box;
}
/* 自定义单选按钮选中状态 */
.match-radio.is-checked {
  /* 蓝色边框 */
  border-color: #4d9fff;
  /* 蓝色背景 */
  background: #4d9fff;
  /* 内阴影模拟空心 */
  box-shadow: inset 0 0 0 3px #161e30;
}
/* 库存表格样式 */
.inventory-page .el-table {
  /* 表格背景透明 */
  --el-table-bg-color: transparent;
  /* 行背景透明 */
  --el-table-tr-bg-color: transparent;
  /* 表头背景 */
  --el-table-header-bg-color: #1a2338;
  /* 表头文字颜色 */
  --el-table-header-text-color: #8f9ab3;
  /* 表格边框颜色 */
  --el-table-border-color: #232e45;
  /* 表格文字颜色 */
  --el-table-text-color: #c2cad9;
  /* 行悬停背景 */
  --el-table-row-hover-bg-color: #1a2338;
  /* 表格背景透明 */
  background: transparent;
}
/* 库存表格表头 */
.inventory-page .el-table th.el-table__cell {
  /* 深色背景 */
  background-color: #1a2338 !important;
  /* 灰色文字 */
  color: #8f9ab3;
  /* 深色底边框 */
  border-bottom-color: #232e45;
}
/* 库存表格单元格 */
.inventory-page .el-table td.el-table__cell {
  /* 深色底边框 */
  border-bottom-color: #232e45;
}
/* 分页容器背景透明 */
.inventory-page .pagination-container {
  /* 强制透明背景 */
  background: transparent !important;
}
/* 分页组件变量 */
.inventory-page .el-pagination {
  /* 背景透明 */
  --el-pagination-bg-color: transparent;
  /* 文字颜色 */
  --el-pagination-text-color: #c2cad9;
  /* 按钮颜色 */
  --el-pagination-button-color: #c2cad9;
  /* 悬停颜色 */
  --el-pagination-hover-color: #4d9fff;
}
/* 分页页码项 */
.inventory-page .el-pagination .el-pager li {
  /* 透明背景 */
  background: transparent !important;
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 分页激活页码 */
.inventory-page .el-pagination .el-pager li.is-active {
  /* 蓝色文字 */
  color: #4d9fff !important;
  /* 字体加粗 */
  font-weight: 700;
}
/* 分页按钮 */
.inventory-page .el-pagination button {
  /* 透明背景 */
  background: transparent !important;
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 分页跳转文字 */
.inventory-page .el-pagination .el-pagination__jump {
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 分页跳转输入框 */
.inventory-page .el-pagination .el-pagination__jump .el-input__wrapper {
  /* 深色背景 */
  background-color: #1a2338 !important;
  /* 深色边框 */
  box-shadow: 0 0 0 1px #2a3654 inset !important;
}
/* 分页跳转输入文字 */
.inventory-page .el-pagination .el-pagination__jump .el-input__inner {
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 分页大小选择器 */
/* 分页大小选择器背景 */
.inventory-page .el-pagination .el-select .el-input__wrapper {
  /* 深色背景 */
  background-color: #1a2338 !important;
  /* 深色边框 */
  box-shadow: 0 0 0 1px #2a3654 inset !important;
}
/* 分页大小选择器文字 */
.inventory-page .el-pagination .el-select .el-input__inner {
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 分页相关所有元素暗色 */
/* 分页所有元素透明背景 */
.inventory-page .el-pagination * {
  /* 强制透明 */
  background-color: transparent !important;
}
/* 分页输入框统一背景 */
.inventory-page .el-pagination .el-select .el-input__wrapper,
.inventory-page .el-pagination .el-pagination__jump .el-input__wrapper {
  /* 深色背景 */
  background-color: #1a2338 !important;
}
/* el-select 暗色主题 */
/* 选择器输入框背景 */
.inventory-page .el-select .el-input__wrapper {
  /* 深色背景 */
  background-color: #1a2338 !important;
  /* 深色边框 */
  box-shadow: 0 0 0 1px #2a3654 inset !important;
}
/* 选择器输入框文字 */
.inventory-page .el-select .el-input__inner {
  /* 浅色文字 */
  color: #c2cad9 !important;
}
/* 选择器输入框悬停 */
.inventory-page .el-select .el-input__wrapper:hover {
  /* 蓝色边框高亮 */
  box-shadow: 0 0 0 1px #4d9fff inset !important;
}
/* 选择器箭头图标 */
.inventory-page .el-select .el-select__caret {
  /* 灰色图标 */
  color: #8f9ab3 !important;
}
/* 加载遮罩层 */
.inventory-page .el-loading-mask {
  /* 半透明深色背景 */
  background-color: rgba(13, 18, 32, 0.6);
}
</style>
