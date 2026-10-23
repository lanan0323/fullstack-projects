<!-- Cron表达式生成器主组件 -->
<template>
    <div>
        <!-- 选项卡式切换，每个tab对应cron的一个字段 -->
        <el-tabs type="border-card">
            <!-- 秒选项卡，根据hideComponent控制是否显示 -->
            <el-tab-pane label="秒" v-if="shouldHide('second')">
                <CrontabSecond
                    @update="updateCrontabValue"
                    :check="checkNumber"
                    :cron="crontabValueObj"
                    ref="cronsecond"
                />
            </el-tab-pane>

            <!-- 分钟选项卡 -->
            <el-tab-pane label="分钟" v-if="shouldHide('min')">
                <CrontabMin
                    @update="updateCrontabValue"
                    :check="checkNumber"
                    :cron="crontabValueObj"
                    ref="cronmin"
                />
            </el-tab-pane>

            <!-- 小时选项卡 -->
            <el-tab-pane label="小时" v-if="shouldHide('hour')">
                <CrontabHour
                    @update="updateCrontabValue"
                    :check="checkNumber"
                    :cron="crontabValueObj"
                    ref="cronhour"
                />
            </el-tab-pane>

            <!-- 日选项卡 -->
            <el-tab-pane label="日" v-if="shouldHide('day')">
                <CrontabDay
                    @update="updateCrontabValue"
                    :check="checkNumber"
                    :cron="crontabValueObj"
                    ref="cronday"
                />
            </el-tab-pane>

            <!-- 月选项卡 -->
            <el-tab-pane label="月" v-if="shouldHide('month')">
                <CrontabMonth
                    @update="updateCrontabValue"
                    :check="checkNumber"
                    :cron="crontabValueObj"
                    ref="cronmonth"
                />
            </el-tab-pane>

            <!-- 周选项卡 -->
            <el-tab-pane label="周" v-if="shouldHide('week')">
                <CrontabWeek
                    @update="updateCrontabValue"
                    :check="checkNumber"
                    :cron="crontabValueObj"
                    ref="cronweek"
                />
            </el-tab-pane>

            <!-- 年选项卡 -->
            <el-tab-pane label="年" v-if="shouldHide('year')">
                <CrontabYear
                    @update="updateCrontabValue"
                    :check="checkNumber"
                    :cron="crontabValueObj"
                    ref="cronyear"
                />
            </el-tab-pane>
        </el-tabs>

        <!-- 结果展示区域 -->
        <div class="popup-main">
            <!-- cron表达式各字段和最终结果的表格展示 -->
            <div class="popup-result">
                <p class="title">时间表达式</p>
                <table>
                    <thead>
                        <!-- 表头：各字段名称 -->
                        <th v-for="item of tabTitles" :key="item">{{item}}</th>
                        <th>Cron 表达式</th>
                    </thead>
                    <tbody>
                        <!-- 秒字段，超过10字符时用tooltip展示完整内容 -->
                        <td>
                            <span v-if="crontabValueObj.second.length < 10">{{crontabValueObj.second}}</span>
                            <el-tooltip v-else :content="crontabValueObj.second" placement="top"><span>{{crontabValueObj.second}}</span></el-tooltip>
                        </td>
                        <!-- 分钟字段 -->
                        <td>
                            <span v-if="crontabValueObj.min.length < 10">{{crontabValueObj.min}}</span>
                            <el-tooltip v-else :content="crontabValueObj.min" placement="top"><span>{{crontabValueObj.min}}</span></el-tooltip>
                        </td>
                        <!-- 小时字段 -->
                        <td>
                            <span v-if="crontabValueObj.hour.length < 10">{{crontabValueObj.hour}}</span>
                            <el-tooltip v-else :content="crontabValueObj.hour" placement="top"><span>{{crontabValueObj.hour}}</span></el-tooltip>
                        </td>
                        <!-- 日字段 -->
                        <td>
                            <span v-if="crontabValueObj.day.length < 10">{{crontabValueObj.day}}</span>
                            <el-tooltip v-else :content="crontabValueObj.day" placement="top"><span>{{crontabValueObj.day}}</span></el-tooltip>
                        </td>
                        <!-- 月字段 -->
                        <td>
                            <span v-if="crontabValueObj.month.length < 10">{{crontabValueObj.month}}</span>
                            <el-tooltip v-else :content="crontabValueObj.month" placement="top"><span>{{crontabValueObj.month}}</span></el-tooltip>
                        </td>
                        <!-- 周字段 -->
                        <td>
                            <span v-if="crontabValueObj.week.length < 10">{{crontabValueObj.week}}</span>
                            <el-tooltip v-else :content="crontabValueObj.week" placement="top"><span>{{crontabValueObj.week}}</span></el-tooltip>
                        </td>
                        <!-- 年字段 -->
                        <td>
                            <span v-if="crontabValueObj.year.length < 10">{{crontabValueObj.year}}</span>
                            <el-tooltip v-else :content="crontabValueObj.year" placement="top"><span>{{crontabValueObj.year}}</span></el-tooltip>
                        </td>
                        <!-- 最终拼接的cron表达式 -->
                        <td class="result">
                            <span v-if="crontabValueString.length < 90">{{crontabValueString}}</span>
                            <el-tooltip v-else :content="crontabValueString" placement="top"><span>{{crontabValueString}}</span></el-tooltip>
                        </td>
                    </tbody>
                </table>
            </div>
            <!-- 最近5次运行时间预测组件 -->
            <CrontabResult :ex="crontabValueString"></CrontabResult>

            <!-- 底部按钮：确定/重置/取消 -->
            <div class="pop_btn">
                <el-button type="primary" @click="submitFill">确定</el-button>
                <el-button type="warning" @click="clearCron">重置</el-button>
                <el-button @click="hidePopup">取消</el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
// 导入秒子组件
import CrontabSecond from "./second.vue"
// 导入分钟子组件
import CrontabMin from "./min.vue"
// 导入小时子组件
import CrontabHour from "./hour.vue"
// 导入日子组件
import CrontabDay from "./day.vue"
// 导入月子组件
import CrontabMonth from "./month.vue"
// 导入周子组件
import CrontabWeek from "./week.vue"
// 导入年子组件
import CrontabYear from "./year.vue"
// 导入运行结果预测子组件
import CrontabResult from "./result.vue"
// 获取组件实例代理
const { proxy } = getCurrentInstance()
// 定义emit事件：隐藏弹窗和填充表达式
const emit = defineEmits(['hide', 'fill'])
// 定义props：hideComponent控制隐藏哪些tab，expression为传入的cron表达式
const props = defineProps({
    hideComponent: {
        type: Array,
        default: () => [],
    },
    expression: {
        type: String,
        default: ""
    }
})
// 表格表头标题
const tabTitles = ref(["秒", "分钟", "小时", "日", "月", "周", "年"])
// 当前激活的tab索引
const tabActive = ref(0)
// 需要隐藏的组件列表
const hideComponent = ref([])
// 传入的cron表达式
const expression = ref('')
// cron各字段的值对象
const crontabValueObj = ref({
    second: "*",
    min: "*",
    hour: "*",
    day: "*",
    month: "*",
    week: "?",
    year: "",
})
// 计算属性：将各字段拼接为完整的cron表达式字符串
const crontabValueString = computed(() => {
    const obj = crontabValueObj.value
    return obj.second
        + " "
        + obj.min
        + " "
        + obj.hour
        + " "
        + obj.day
        + " "
        + obj.month
        + " "
        + obj.week
        + (obj.year === "" ? "" : " " + obj.year)
})
// 监听expression变化时重新解析表达式
watch(expression, () => resolveExp())
// 判断某个tab是否应该显示
function shouldHide(key) {
    return !(hideComponent.value && hideComponent.value.includes(key))
}
// 反解析传入的cron表达式到各字段
function resolveExp() {
    // 反解析 表达式
    if (expression.value) {
        // 按空格分割表达式
        const arr = expression.value.split(/\s+/)
        if (arr.length >= 6) {
            // 6位以上是合法表达式，拆分到各字段
            let obj = {
                second: arr[0],
                min: arr[1],
                hour: arr[2],
                day: arr[3],
                month: arr[4],
                week: arr[5],
                year: arr[6] ? arr[6] : ""
            }
            crontabValueObj.value = {
                ...obj,
            }
        }
    } else {
        // 没有传入的表达式 则还原
        clearCron()
    }
}
// tab切换值
function tabCheck(index) {
    tabActive.value = index
}
// 由子组件触发，更改表达式组成的字段值
function updateCrontabValue(name, value, from) {
    crontabValueObj.value[name] = value
}
// 表单选项的子组件校验数字格式（通过props传递给子组件）
function checkNumber(value, minLimit, maxLimit) {
    // 取整
    value = Math.floor(value)
    // 下限校验
    if (value < minLimit) {
        value = minLimit
    } else if (value > maxLimit) {
        // 上限校验
        value = maxLimit
    }
    return value
}
// 隐藏弹窗
function hidePopup() {
    emit("hide")
}
// 填充表达式并关闭弹窗
function submitFill() {
    emit("fill", crontabValueString.value)
    hidePopup()
}
// 重置所有字段为默认值
function clearCron() {
    // 还原选择项
    crontabValueObj.value = {
        second: "*",
        min: "*",
        hour: "*",
        day: "*",
        month: "*",
        week: "?",
        year: "",
    }
}
// 组件挂载时初始化props
onMounted(() => {
    expression.value = props.expression
    hideComponent.value = props.hideComponent
})
</script>

<style lang="scss" scoped>
.pop_btn {
    text-align: center;
    margin-top: 20px;
}
.popup-main {
    position: relative;
    margin: 10px auto;
    background: var(--el-bg-color);
    border-radius: 5px;
    font-size: 12px;
    overflow: hidden;
}
.popup-title {
    overflow: hidden;
    line-height: 34px;
    padding-top: 6px;
    background: var(--el-fill-color-light);
}
.popup-result {
    box-sizing: border-box;
    line-height: 24px;
    margin: 25px auto;
    padding: 15px 10px 10px;
    border: 1px solid #ccc;
    position: relative;
}
.popup-result .title {
    position: absolute;
    top: -28px;
    left: 50%;
    width: 140px;
    font-size: 14px;
    margin-left: -70px;
    text-align: center;
    line-height: 30px;
    background: var(--el-bg-color);
}
.popup-result table {
    text-align: center;
    width: 100%;
    margin: 0 auto;
}
.popup-result table td:not(.result) {
    width: 3.5rem;
    min-width: 3.5rem;
    max-width: 3.5rem;
}
.popup-result table span {
    display: block;
    width: 100%;
    font-family: arial;
    line-height: 30px;
    height: 30px;
    white-space: nowrap;
    overflow: hidden;
    border: 1px solid #e8e8e8;
}
.popup-result-scroll {
    font-size: 12px;
    line-height: 24px;
    height: 10em;
    overflow-y: auto;
}
</style>