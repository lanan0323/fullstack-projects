// 注释：导入 Vue 3 的 createApp 方法，用于创建应用实例
import { createApp } from 'vue'

// 注释：导入 js-cookie 库，用于操作浏览器 Cookie
import Cookies from 'js-cookie'

// 注释：导入 Element Plus UI 框架
import ElementPlus from 'element-plus'
// 注释：导入 Element Plus 样式文件
import 'element-plus/dist/index.css'
// 注释：导入 Element Plus 暗色主题样式
import 'element-plus/theme-chalk/dark/css-vars.css'
// 注释：导入 Element Plus 中文语言包
import locale from 'element-plus/es/locale/lang/zh-cn'

// 注释：导入全局样式文件
import '@/assets/styles/index.scss' // global css
// 注释：导入用户端界面专用样式文件
import '@/assets/styles/user-surface.scss' // C 端交易界面，仅在 user-surface 作用域生效

// 注释：导入根组件 App
import App from './App'
// 注释：导入 Pinia store
import store from './store'
// 注释：导入 Vue Router 路由器
import router from './router'
// 注释：导入自定义指令
import directive from './directive' // directive

// 注释：注册自定义指令
// 注册指令
// 注释：导入插件
import plugins from './plugins' // plugins
// 注释：导入下载工具函数
import { download } from '@/utils/request'

// 注释：导入 SVG 图标注册模块
// svg图标
import 'virtual:svg-icons-register'
// 注释：导入 SVG 图标组件
import SvgIcon from '@/components/SvgIcon'
// 注释：导入 Element Plus 图标组件
import elementIcons from '@/components/SvgIcon/svgicon'

// 注释：导入权限控制模块
import './permission' // permission control

// 注释：导入字典工具函数
import { useDict } from '@/utils/dict'
// 注释：导入通用工具函数（时间解析、表单重置、日期范围处理、树形数据处理、字典标签等）
import { parseTime, resetForm, addDateRange, handleTree, selectDictLabel, selectDictLabels } from '@/utils/ruoyi'

// 注释：导入分页组件
// 分页组件
import Pagination from '@/components/Pagination'
// 注释：导入自定义表格工具栏组件
// 自定义表格工具组件
import RightToolbar from '@/components/RightToolbar'
// 注释：导入富文本编辑器组件
// 富文本组件
import Editor from "@/components/Editor"
// 注释：导入文件上传组件
// 文件上传组件
import FileUpload from "@/components/FileUpload"
// 注释：导入图片上传组件
// 图片上传组件
import ImageUpload from "@/components/ImageUpload"
// 注释：导入图片预览组件
// 图片预览组件
import ImagePreview from "@/components/ImagePreview"
// 注释：导入自定义树形选择组件
// 自定义树选择组件
import TreeSelect from '@/components/TreeSelect'
// 注释：导入字典标签组件
// 字典标签组件
import DictTag from '@/components/DictTag'

// 注释：为 HTML 根元素添加暗色主题类名
// 全局暗色主题（用户端与管理端统一深色）
document.documentElement.classList.add('dark')

// 注释：创建 Vue 应用实例
const app = createApp(App)

// 注释：挂载全局方法到应用实例
// 全局方法挂载
// 注释：挂载字典工具方法
app.config.globalProperties.useDict = useDict
// 注释：挂载下载方法
app.config.globalProperties.download = download
// 注释：挂载时间解析方法
app.config.globalProperties.parseTime = parseTime
// 注释：挂载表单重置方法
app.config.globalProperties.resetForm = resetForm
// 注释：挂载树形数据处理方法
app.config.globalProperties.handleTree = handleTree
// 注释：挂载日期范围处理方法
app.config.globalProperties.addDateRange = addDateRange
// 注释：挂载字典标签选择方法
app.config.globalProperties.selectDictLabel = selectDictLabel
// 注释：挂载多字典标签选择方法
app.config.globalProperties.selectDictLabels = selectDictLabels

// 注释：注册全局组件
// 全局组件挂载
// 注释：注册字典标签组件
app.component('DictTag', DictTag)
// 注释：注册分页组件
app.component('Pagination', Pagination)
// 注释：注册树形选择组件
app.component('TreeSelect', TreeSelect)
// 注释：注册文件上传组件
app.component('FileUpload', FileUpload)
// 注释：注册图片上传组件
app.component('ImageUpload', ImageUpload)
// 注释：注册图片预览组件
app.component('ImagePreview', ImagePreview)
// 注释：注册表格工具栏组件
app.component('RightToolbar', RightToolbar)
// 注释：注册富文本编辑器组件
app.component('Editor', Editor)

// 注释：使用 Vue Router 插件
app.use(router)
// 注释：使用 Pinia store 插件
app.use(store)
// 注释：使用自定义插件
app.use(plugins)
// 注释：使用 Element Plus 图标插件
app.use(elementIcons)
// 注释：注册 SVG 图标组件
app.component('svg-icon', SvgIcon)

// 注释：注册自定义指令
directive(app)

// 注释：配置并使用 Element Plus
// 使用element-plus 并且设置全局的大小
app.use(ElementPlus, {
  // 注释：设置语言为中文
  locale: locale,
  // 注释：设置组件大小，从 Cookie 中读取或使用默认值
  // 支持 large、default、small
  size: Cookies.get('size') || 'default'
})

// 注释：将应用实例挂载到 id 为 app 的 DOM 元素
app.mount('#app')
