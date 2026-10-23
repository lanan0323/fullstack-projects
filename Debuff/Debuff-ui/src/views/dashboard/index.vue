<template>
  <!-- 页面主容器，包含整个仪表盘内容 -->
  <div class="app-container dash-page">
    <!-- 欢迎区 -->
    <!-- 欢迎区域容器，展示页面标题和副标题 -->
    <div class="dash-head">
      <!-- 标题文字包裹容器 -->
      <div>
        <!-- 页面主标题，显示"数据看板" -->
        <div class="dash-title">数据看板</div>
        <!-- 页面副标题，说明这是饰品交易后台的运营数据总览 -->
        <div class="dash-sub">饰品交易后台 · 运营数据总览</div>
      </div>
    </div>

    <!-- 趋势 + 状态分布 -->
    <!-- 第一行布局：左侧趋势图 + 右侧状态分布图，使用 Element Plus 栅格系统 -->
    <el-row :gutter="16">
      <!-- 左侧列：在超小屏占满24格，在大屏占16格 -->
      <el-col :xs="24" :lg="16">
        <!-- 趋势图卡片容器 -->
        <div class="chart-card">
          <!-- 趋势图标题，显示"近 7 天交易趋势" -->
          <div class="chart-title">近 7 天交易趋势</div>
          <!-- 趋势图 ECharts 渲染容器，通过 ref 绑定获取 DOM 引用 -->
          <div ref="trendRef" class="chart-box"></div>
        </div>
      </el-col>
      <!-- 右侧列：在超小屏占满24格，在大屏占8格 -->
      <el-col :xs="24" :lg="8">
        <!-- 状态分布图卡片容器 -->
        <div class="chart-card">
          <!-- 状态分布图标题，显示"订单状态分布" -->
          <div class="chart-title">订单状态分布</div>
          <!-- 状态分布饼图 ECharts 渲染容器，通过 ref 绑定获取 DOM 引用 -->
          <div ref="distRef" class="chart-box"></div>
        </div>
      </el-col>
    </el-row>

    <!-- TOP5 + 最新公告 -->
    <!-- 第二行布局：左侧 TOP5 柱状图 + 右侧公告列表 -->
    <el-row :gutter="16">
      <!-- 左侧列：在超小屏占满24格，在大屏占12格 -->
      <el-col :xs="24" :lg="12">
        <!-- TOP5 柱状图卡片容器 -->
        <div class="chart-card">
          <!-- TOP5 图标题，显示"成交金额 TOP5" -->
          <div class="chart-title">成交金额 TOP5</div>
          <!-- TOP5 横向柱状图 ECharts 渲染容器，通过 ref 绑定获取 DOM 引用 -->
          <div ref="topRef" class="chart-box"></div>
        </div>
      </el-col>
      <!-- 右侧列：在超小屏占满24格，在大屏占12格 -->
      <el-col :xs="24" :lg="12">
        <!-- 公告列表卡片容器 -->
        <div class="chart-card">
          <!-- 公告列表标题，显示"最新公告" -->
          <div class="chart-title">最新公告</div>
          <!-- 公告列表容器，遍历 announcements 数组渲染每条公告 -->
          <div class="announce-list">
            <!-- 单条公告项：点击时调用 openAnnounce 打开详情弹窗 -->
            <div v-for="a in announcements" :key="a.id" class="announce-item" @click="openAnnounce(a)">
              <!-- 公告类型标签，根据 announcementType 显示对应文字 -->
              <span class="announce-tag">{{ announceTypeText(a.announcementType) }}</span>
              <!-- 公告标题文字 -->
              <span class="announce-title">{{ a.title }}</span>
              <!-- 公告创建日期，截取前10位显示 YYYY-MM-DD 格式 -->
              <span class="announce-date">{{ (a.createTime || '').slice(0, 10) }}</span>
            </div>
            <!-- 当公告列表为空时，显示空状态占位提示 -->
            <el-empty v-if="!announcements.length" description="暂无公告" :image-size="60" />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 公告详情 -->
    <!-- 公告详情弹窗，通过 v-model 控制显示/隐藏，宽度 520px -->
    <el-dialog v-model="announceOpen" :title="currentAnnounce ? currentAnnounce.title : ''" width="520px" append-to-body>
      <!-- 公告元信息区域：包含类型标签和发布时间 -->
      <div class="announce-meta">
        <!-- 弹窗中的公告类型标签 -->
        <span class="announce-tag">{{ announceTypeText(currentAnnounce && currentAnnounce.announcementType) }}</span>
        <!-- 弹窗中的公告完整发布时间 -->
        <span class="announce-time">{{ currentAnnounce && currentAnnounce.createTime }}</span>
      </div>
      <!-- 公告正文内容，使用 v-html 渲染富文本 HTML 内容 -->
      <div class="announce-content" v-html="currentAnnounce ? currentAnnounce.content : ''"></div>
    </el-dialog>
  </div>
</template>

<script setup>
// 从 Vue 导入响应式 API：ref 创建响应式引用，onMounted 和 onBeforeUnmount 为生命周期钩子
import { ref, onMounted, onActivated, onBeforeUnmount } from 'vue'
// 导入 ECharts 库，用于渲染各类图表
import * as echarts from 'echarts'
// 导入获取公开公告列表的 API 方法
import { getPublicAnnouncements } from '@/api/trade/announcement'
// 导入三个后台统计数据的 API 方法：订单趋势、TOP 饰品、状态分布
import { getOrderTrend, getTopSkins, getStatusDist } from '@/api/admin/stats'

// 定义公告类型转换函数，将数字类型映射为中文标签
const announceTypeText = (t) => {
  // 类型映射表：1=特价，2=活动，3=更新
  const map = { 1: '特价', 2: '活动', 3: '更新' }
  // 返回对应的中文标签，未匹配时默认返回"公告"
  return map[Number(t)] || '公告'
}

// 创建公告列表的响应式引用，初始为空数组
const announcements = ref([])
// 创建控制公告详情弹窗显示/隐藏的响应式布尔值
const announceOpen = ref(false)
// 存储当前选中的公告对象的响应式引用
const currentAnnounce = ref(null)

// 趋势图 DOM 元素的响应式引用
const trendRef = ref(null)
// 状态分布饼图 DOM 元素的响应式引用
const distRef = ref(null)
// TOP5 柱状图 DOM 元素的响应式引用
const topRef = ref(null)
// 趋势图 ECharts 实例变量（非响应式）
let trendChart = null
// 状态分布饼图 ECharts 实例变量（非响应式）
let distChart = null
// TOP5 柱状图 ECharts 实例变量（非响应式）
let topChart = null

// 定义异步函数加载所有统计数据（趋势、TOP5、状态分布、公告）
const loadStats = async () => {
  try {
    // 使用 Promise.all 并行请求四个接口，提升加载速度
    const [trend, tops, dist, anns] = await Promise.all([
      // 获取近 7 天的订单趋势数据
      getOrderTrend(7),
      // 获取成交金额排名前 5 的饰品数据
      getTopSkins(5),
      // 获取订单状态分布数据
      getStatusDist(),
      // 获取公开公告列表
      getPublicAnnouncements()
    ])
    // 将公告接口返回的数据赋值给响应式变量，若无数据则使用空数组
    announcements.value = (anns && anns.data) || []
    // 渲染趋势折线/柱状图
    renderTrend(trend && trend.data)
    // 渲染状态分布饼图
    renderDist(dist && dist.data)
    // 渲染 TOP5 横向柱状图
    renderTop(tops && tops.data)
  } catch (e) {
    // 统计加载失败不阻塞页面
  }
}

// 补齐缺失日期（无订单的日期补 0）
// 定义补齐日期函数：确保连续 days 天都有数据，缺失的日期补 0
const fillTrend = (rows, days) => {
  // 创建日期到数据的映射对象
  const map = {}
  // 遍历原始数据行，以日期为键建立映射
  ;(rows || []).forEach((r) => { map[r.day] = r })
  // 创建补齐后的数据列表
  const list = []
  // 从 days-1 天前循环到今天，确保日期连续
  for (let i = days - 1; i >= 0; i--) {
    // 创建日期对象
    const d = new Date()
    // 将日期设置为 i 天前的日期
    d.setDate(d.getDate() - i)
    // 拼接 YYYY-MM-DD 格式的日期字符串作为键
    const key = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0')
    // 将补齐后的数据项推入列表，无数据时订单数和金额均为 0
    list.push({ day: key, orderCount: (map[key] && map[key].order_count) || 0, amount: (map[key] && Number(map[key].amount)) || 0 })
  }
  // 返回补齐后的连续日期数据列表
  return list
}

// 定义 ECharts 工具提示的公共样式配置（深色主题）
const tooltipStyle = {
  // 工具提示背景色：深蓝灰色
  backgroundColor: '#1e293b',
  // 工具提示边框色：稍浅的蓝灰色
  borderColor: '#334155',
  // 工具提示文字样式：浅灰白色文字
  textStyle: { color: '#e9edf6' }
}

// 定义趋势图渲染函数，接收原始数据行参数
const renderTrend = (rows) => {
  // 如果趋势图实例未初始化则直接返回
  if (!trendChart) return
  // 调用 fillTrend 补齐最近 7 天的数据
  const data = fillTrend(rows, 7)
  // 设置趋势图的配置项
  trendChart.setOption({
    // 配置工具提示：按坐标轴触发，合并公共样式
    tooltip: Object.assign({ trigger: 'axis' }, tooltipStyle),
    // 配置图例：显示"订单数"和"成交额"两个系列，位于顶部
    legend: { data: ['订单数', '成交额'], top: 0, textStyle: { color: '#a8b3c5' } },
    // 配置图表网格：设定四周留白大小
    grid: { left: 44, right: 56, top: 36, bottom: 28 },
    // 配置 X 轴（分类轴，显示日期）
    xAxis: {
      // X 轴类型为分类轴
      type: 'category',
      // X 轴标签数据：取日期字符串的后5位（MM-DD 格式）
      data: data.map((d) => d.day.slice(5)),
      // X 轴轴线样式：深蓝灰色
      axisLine: { lineStyle: { color: '#2a3650' } },
      // X 轴标签文字颜色：浅蓝灰色
      axisLabel: { color: '#a8b3c5' }
    },
    // 配置 Y 轴：双 Y 轴，左侧为订单数，右侧为成交额
    yAxis: [
      // 左侧 Y 轴：显示订单数，带分割线
      { type: 'value', name: '订单数', nameTextStyle: { color: '#a8b3c5' }, splitLine: { lineStyle: { color: '#1d2635' } }, axisLabel: { color: '#a8b3c5' } },
      // 右侧 Y 轴：显示成交额，隐藏分割线
      { type: 'value', name: '成交额', nameTextStyle: { color: '#a8b3c5' }, splitLine: { show: false }, axisLabel: { color: '#a8b3c5' } }
    ],
    // 配置数据系列
    series: [
      // 第一个系列：订单数柱状图
      {
        // 系列名称，与图例对应
        name: '订单数',
        // 图表类型为柱状图
        type: 'bar',
        // 柱子宽度设为 18 像素
        barWidth: 18,
        // 柱子样式配置
        itemStyle: {
          // 柱子顶部圆角，左上和右上各 4px
          borderRadius: [4, 4, 0, 0],
          // 柱子颜色：从上到下的蓝色线性渐变
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            // 渐变起始色：亮蓝色
            { offset: 0, color: '#4d9fff' },
            // 渐变结束色：半透明蓝色
            { offset: 1, color: 'rgba(77,159,255,0.15)' }
          ])
        },
        // 柱子数据：映射每天的订单数
        data: data.map((d) => d.orderCount)
      },
      // 第二个系列：成交额折线图
      {
        // 系列名称，与图例对应
        name: '成交额',
        // 图表类型为折线图
        type: 'line',
        // 启用平滑曲线
        smooth: true,
        // 数据点标记形状为圆形
        symbol: 'circle',
        // 数据点标记大小为 7 像素
        symbolSize: 7,
        // 数据点和线条颜色：橙黄色
        itemStyle: { color: '#ffb454' },
        // 线条宽度为 2 像素
        lineStyle: { width: 2 },
        // 使用右侧第二个 Y 轴（索引为 1）
        yAxisIndex: 1,
        // 折线数据：映射每天的成交额
        data: data.map((d) => d.amount)
      }
    ]
  })
}

// 定义状态分布饼图渲染函数，接收原始数据行参数
const renderDist = (rows) => {
  // 如果饼图实例未初始化则直接返回
  if (!distChart) return
  // 初始化三种状态计数的对象：0=待确认，1=已完成，2=已取消
  const cnt = { 0: 0, 1: 0, 2: 0 }
  // 遍历数据行，将每种状态的数量填入 cnt 对象
  ;(rows || []).forEach((r) => { cnt[r.status] = r.count })
  // 设置饼图的配置项
  distChart.setOption({
    // 配置工具提示：按数据项触发，合并公共样式
    tooltip: Object.assign({ trigger: 'item' }, tooltipStyle),
    // 配置图例：位于底部，使用圆形图标
    legend: { bottom: 0, icon: 'circle', textStyle: { color: '#a8b3c5' } },
    // 配置数据系列
    series: [
      // 饼图系列配置
      {
        // 图表类型为饼图
        type: 'pie',
        // 设置环形饼的内外半径：内径 52%，外径 72%
        radius: ['52%', '72%'],
        // 设置饼图中心位置：水平居中，垂直偏上
        center: ['50%', '44%'],
        // 配置标签样式：显示名称和数值
        label: { color: '#a8b3c5', formatter: '{b} {c}' },
        // 扇区边框样式：深色边框用于分隔扇区
        itemStyle: { borderColor: '#141b2d', borderWidth: 2 },
        // 饼图数据：三种订单状态及其对应颜色和数值
        data: [
          // 待确认状态：橙黄色
          { name: '待确认', value: cnt[0], itemStyle: { color: '#e6a23c' } },
          // 已完成状态：绿色
          { name: '已完成', value: cnt[1], itemStyle: { color: '#67c23a' } },
          // 已取消状态：灰色
          { name: '已取消', value: cnt[2], itemStyle: { color: '#909399' } }
        ]
      }
    ]
  })
}

// 定义 TOP5 横向柱状图渲染函数，接收原始数据行参数
const renderTop = (rows) => {
  // 如果 TOP5 图实例未初始化则直接返回
  if (!topChart) return
  // 获取数据列表，若无数据则使用空数组
  const list = rows || []
  // 设置 TOP5 图的配置项
  topChart.setOption({
    // 配置工具提示：按坐标轴触发，使用阴影指示器，合并公共样式
    tooltip: Object.assign({ trigger: 'axis', axisPointer: { type: 'shadow' } }, tooltipStyle),
    // 配置图表网格：左侧留更多空间显示饰品名称
    grid: { left: 84, right: 30, top: 16, bottom: 26 },
    // 配置 X 轴（数值轴，显示成交金额）
    xAxis: {
      // X 轴类型为数值轴
      type: 'value',
      // 分割线样式：深色细线
      splitLine: { lineStyle: { color: '#1d2635' } },
      // X 轴标签颜色：浅蓝灰色
      axisLabel: { color: '#a8b3c5' }
    },
    // 配置 Y 轴（分类轴，显示饰品名称）
    yAxis: {
      // Y 轴类型为分类轴
      type: 'category',
      // Y 轴标签数据：映射每个饰品的名称
      data: list.map((r) => r.skin_name),
      // Y 轴轴线样式：深蓝灰色
      axisLine: { lineStyle: { color: '#2a3650' } },
      // Y 轴标签颜色：浅蓝灰色
      axisLabel: { color: '#a8b3c5' }
    },
    // 配置数据系列
    series: [
      // 横向柱状图系列
      {
        // 图表类型为柱状图
        type: 'bar',
        // 柱子宽度设为 14 像素
        barWidth: 14,
        // 柱子样式配置
        itemStyle: {
          // 柱子右侧圆角，右上和右下各 7px
          borderRadius: [0, 7, 7, 0],
          // 柱子颜色：从左到右的绿色线性渐变
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            // 渐变起始色：半透明绿色
            { offset: 0, color: 'rgba(61,220,151,0.25)' },
            // 渐变结束色：亮绿色
            { offset: 1, color: '#3ddc97' }
          ])
        },
        // 柱子数据：映射每个饰品的成交金额
        data: list.map((r) => r.amount)
      }
    ]
  })
}

// 定义打开公告详情弹窗的函数，接收公告对象参数
const openAnnounce = (a) => {
  // 将当前点击的公告对象赋值给响应式变量
  currentAnnounce.value = a
  // 将弹窗显示状态设为 true，打开弹窗
  announceOpen.value = true
}

// 定义窗口大小变化时的处理函数，用于自适应调整图表尺寸
const handleResize = () => {
  // 如果趋势图实例存在则调用 resize 方法重绘
  trendChart && trendChart.resize()
  // 如果饼图实例存在则调用 resize 方法重绘
  distChart && distChart.resize()
  // 如果 TOP5 图实例存在则调用 resize 方法重绘
  topChart && topChart.resize()
}

// 组件挂载时的生命周期钩子
onMounted(() => {
  // 初始化趋势图 ECharts 实例，绑定到 trendRef 对应的 DOM 元素
  trendChart = echarts.init(trendRef.value)
  // 初始化状态分布饼图 ECharts 实例，绑定到 distRef 对应的 DOM 元素
  distChart = echarts.init(distRef.value)
  // 初始化 TOP5 柱状图 ECharts 实例，绑定到 topRef 对应的 DOM 元素
  topChart = echarts.init(topRef.value)
  // 调用加载函数获取并渲染所有统计数据
  loadStats()
  // 监听窗口 resize 事件，实现图表自适应缩放
  window.addEventListener('resize', handleResize)
})
// keep-alive缓存激活时重新加载统计数据
onActivated(() => {
  loadStats()
})

// 组件卸载前的生命周期钩子，用于清理资源
onBeforeUnmount(() => {
  // 移除窗口 resize 事件监听器
  window.removeEventListener('resize', handleResize)
  // 如果趋势图实例存在则销毁释放内存
  trendChart && trendChart.dispose()
  // 如果饼图实例存在则销毁释放内存
  distChart && distChart.dispose()
  // 如果 TOP5 图实例存在则销毁释放内存
  topChart && topChart.dispose()
})
</script>

<style scoped lang="scss">
/* 仪表盘页面主容器：限制最大宽度为 1280px */
.dash-page {
  /* 限制页面内容最大宽度，防止在超宽屏幕上过度拉伸 */
  max-width: 1280px;
}
/* 欢迎区域容器样式 */
.dash-head {
  /* 使用弹性布局排列子元素 */
  display: flex;
  /* 子元素垂直居中对齐 */
  align-items: center;
  /* 子元素两端对齐，实现左右分布 */
  justify-content: space-between;
  /* 底部外边距 20px，与下方内容分隔 */
  margin-bottom: 20px;
}
/* 页面主标题样式 */
.dash-title {
  /* 标题字号 22px */
  font-size: 22px;
  /* 标题加粗，字重 700 */
  font-weight: 700;
  /* 使用 Element Plus 主文字颜色变量 */
  color: var(--el-text-color-primary);
}
/* 页面副标题样式 */
.dash-sub {
  /* 顶部间距 4px，与主标题保持间距 */
  margin-top: 4px;
  /* 副标题字号 13px，比主标题小 */
  font-size: 13px;
  /* 使用 Element Plus 次要文字颜色变量 */
  color: var(--el-text-color-secondary);
}
/* 图表卡片通用样式 */
.chart-card {
  /* 使用 Element Plus 背景色变量 */
  background: var(--el-bg-color);
  /* 使用 Element Plus 浅色边框变量 */
  border: 1px solid var(--el-border-color-light);
  /* 卡片圆角 10px，呈现圆润效果 */
  border-radius: 10px;
  /* 卡片内边距 16px */
  padding: 16px;
  /* 卡片底部外边距 16px，与下方卡片分隔 */
  margin-bottom: 16px;
}
/* 图表卡片标题样式 */
.chart-title {
  /* 标题字号 15px */
  font-size: 15px;
  /* 标题加粗，字重 700 */
  font-weight: 700;
  /* 使用 Element Plus 主文字颜色变量 */
  color: var(--el-text-color-primary);
  /* 标题与下方图表内容的间距 */
  margin-bottom: 10px;
}
/* 图表渲染容器样式 */
.chart-box {
  /* 固定图表容器高度为 300px */
  height: 300px;
}
/* 公告列表容器样式 */
.announce-list {
  /* 最小高度 300px，与左侧图表卡片高度保持一致 */
  min-height: 300px;
  /* 使用弹性布局纵向排列公告项 */
  display: flex;
  /* 子元素纵向排列 */
  flex-direction: column;
}
/* 单条公告项样式 */
.announce-item {
  /* 使用弹性布局横向排列标签、标题和日期 */
  display: flex;
  /* 子元素垂直居中对齐 */
  align-items: center;
  /* 子元素之间间距 10px */
  gap: 10px;
  /* 上下内边距 12px，左右 4px */
  padding: 12px 4px;
  /* 底部虚线分隔线 */
  border-bottom: 1px dashed var(--el-border-color-lighter);
  /* 鼠标悬停时显示手型光标，提示可点击 */
  cursor: pointer;
  /* 背景色过渡动画，悬停时平滑变化 */
  transition: background 0.15s;
  /* 圆角 6px */
  border-radius: 6px;
  /* 鼠标悬停时的样式 */
  &:hover {
    /* 悬停时背景变为淡蓝色半透明 */
    background: rgba(77, 159, 255, 0.06);
  }
}
/* 公告类型标签样式 */
.announce-tag {
  /* 禁止标签被压缩，保持固有宽度 */
  flex-shrink: 0;
  /* 标签字号 11px，小巧精致 */
  font-size: 11px;
  /* 标签内边距：上下 1px，左右 8px */
  padding: 1px 8px;
  /* 标签圆角 20px，呈现胶囊形状 */
  border-radius: 20px;
  /* 标签文字颜色：珊瑚红色 */
  color: #ff6b5e;
  /* 标签背景色：珊瑚红半透明 */
  background: rgba(255, 107, 94, 0.12);
  /* 标签边框：珊瑚红半透明实线 */
  border: 1px solid rgba(255, 107, 94, 0.35);
}
/* 公告标题文字样式 */
.announce-title {
  /* 占据剩余空间，将日期推到右侧 */
  flex: 1;
  /* 标题字号 14px */
  font-size: 14px;
  /* 使用 Element Plus 主文字颜色变量 */
  color: var(--el-text-color-primary);
  /* 隐藏溢出内容 */
  overflow: hidden;
  /* 文字溢出时显示省略号 */
  text-overflow: ellipsis;
  /* 文字不换行，保持单行显示 */
  white-space: nowrap;
}
/* 公告日期样式 */
.announce-date {
  /* 禁止被压缩，保持固有宽度 */
  flex-shrink: 0;
  /* 日期字号 12px */
  font-size: 12px;
  /* 使用 Element Plus 次要文字颜色变量 */
  color: var(--el-text-color-secondary);
}
/* 弹窗中公告元信息区域样式 */
.announce-meta {
  /* 使用弹性布局横向排列标签和时间 */
  display: flex;
  /* 子元素垂直居中对齐 */
  align-items: center;
  /* 子元素之间间距 12px */
  gap: 12px;
  /* 底部外边距 14px，与正文分隔 */
  margin-bottom: 14px;
}
/* 弹窗中公告发布时间样式 */
.announce-time {
  /* 时间字号 13px */
  font-size: 13px;
  /* 使用 Element Plus 次要文字颜色变量 */
  color: var(--el-text-color-secondary);
}
/* 弹窗中公告正文内容样式 */
.announce-content {
  /* 正文字号 14px */
  font-size: 14px;
  /* 行高 1.8 倍，提升阅读舒适度 */
  line-height: 1.8;
  /* 使用 Element Plus 主文字颜色变量 */
  color: var(--el-text-color-primary);
  /* 允许在任意字符处换行，防止长文本溢出 */
  word-break: break-all;
}
</style>
