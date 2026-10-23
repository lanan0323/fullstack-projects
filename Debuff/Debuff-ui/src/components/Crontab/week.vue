<!-- Cron星期配置子组件 -->
<template>
    <el-form>
        <!-- 每周 -->
        <el-form-item>
            <el-radio v-model='radioValue' :label="1">
                周，允许的通配符[, - * ? / L #]
            </el-radio>
        </el-form-item>

        <!-- 不指定，与日互斥 -->
        <el-form-item>
            <el-radio v-model='radioValue' :label="2">
                不指定
            </el-radio>
        </el-form-item>

        <!-- 周期范围：从星期几到星期几 -->
        <el-form-item>
            <el-radio v-model='radioValue' :label="3">
                周期从
                <el-select clearable v-model="cycle01">
                    <el-option
                        v-for="(item,index) of weekList"
                        :key="index"
                        :label="item.value"
                        :value="item.key"
                        :disabled="item.key === 7"
                    >{{item.value}}</el-option>
                </el-select>
                -
                <el-select clearable v-model="cycle02">
                    <el-option
                        v-for="(item,index) of weekList"
                        :key="index"
                        :label="item.value"
                        :value="item.key"
                        :disabled="item.key <= cycle01"
                    >{{item.value}}</el-option>
                </el-select>
            </el-radio>
        </el-form-item>

        <!-- 第N周的星期M -->
        <el-form-item>
            <el-radio v-model='radioValue' :label="4">
                第
                <el-input-number v-model='average01' :min="1" :max="4" /> 周的
                <el-select clearable v-model="average02">
                    <el-option v-for="item in weekList" :key="item.key" :label="item.value" :value="item.key" />
                </el-select>
            </el-radio>
        </el-form-item>

        <!-- 本月最后一个星期几 -->
        <el-form-item>
            <el-radio v-model='radioValue' :label="5">
                本月最后一个
                <el-select clearable v-model="weekday">
                    <el-option v-for="item in weekList" :key="item.key" :label="item.value" :value="item.key" />
                </el-select>
            </el-radio>
        </el-form-item>

        <!-- 指定星期几 -->
        <el-form-item>
            <el-radio v-model='radioValue' :label="6">
                指定
                <el-select class="multiselect" clearable v-model="checkboxList" placeholder="可多选" multiple :multiple-limit="6">
                    <el-option v-for="item in weekList" :key="item.key" :label="item.value" :value="item.key" />
                </el-select>
            </el-radio>
        </el-form-item>

    </el-form>
</template>

<script setup>
// 定义emit事件，向父组件更新cron值
const emit = defineEmits(['update'])
// 接收父组件传入的cron对象和校验函数
const props = defineProps({
    cron: {
        type: Object,
        default: {
            second: "*",
            min: "*",
            hour: "*",
            day: "*",
            month: "*",
            week: "?",
            year: ""
        }
    },
    check: {
        type: Function,
        default: () => {
        }
    }
})
// 当前选中的单选按钮值，默认不指定
const radioValue = ref(2)
// 周期起始星期
const cycle01 = ref(2)
// 周期结束星期
const cycle02 = ref(3)
// 第几周
const average01 = ref(1)
// 星期几
const average02 = ref(2)
// 本月最后一个星期几
const weekday = ref(2)
// 多选指定的星期列表
const checkboxList = ref([])
// 多选列表的备份
const checkCopy = ref([2])
// 星期中英文映射列表（1=星期日，符合Quartz规范）
const weekList = ref([
    {key: 1, value: '星期日'},
    {key: 2, value: '星期一'},
    {key: 3, value: '星期二'},
    {key: 4, value: '星期三'},
    {key: 5, value: '星期四'},
    {key: 6, value: '星期五'},
    {key: 7, value: '星期六'}
])
// 计算属性：周期范围表达式（如 2-5）
const cycleTotal = computed(() => {
    cycle01.value = props.check(cycle01.value, 1, 6)
    cycle02.value = props.check(cycle02.value, cycle01.value + 1, 7)
    return cycle01.value + '-' + cycle02.value
})
// 计算属性：第N周星期M表达式（如 3#2 表示第2周的星期3）
const averageTotal = computed(() => {
    average01.value = props.check(average01.value, 1, 4)
    average02.value = props.check(average02.value, 1, 7)
    return average02.value + '#' + average01.value
})
// 计算属性：本月最后一个星期几表达式（如 5L）
const weekdayTotal = computed(() => {
    weekday.value = props.check(weekday.value, 1, 7)
    return weekday.value + 'L'
})
// 计算属性：多选列表拼接为逗号分隔字符串
const checkboxString = computed(() => {
    return checkboxList.value.join(',')
})
// 监听父组件传入的week字段变化
watch(() => props.cron.week, value => changeRadioValue(value))
// 监听单选按钮和计算值变化
watch([radioValue, cycleTotal, averageTotal, weekdayTotal, checkboxString], () => onRadioChange())
// 根据cron表达式值解析并设置对应的单选按钮
function changeRadioValue(value) {
    if (value === "*") {
        radioValue.value = 1
    } else if (value === "?") {
        radioValue.value = 2
    } else if (value.indexOf("-") > -1) {
        const indexArr = value.split('-')
        cycle01.value = Number(indexArr[0])
        cycle02.value = Number(indexArr[1])
        radioValue.value = 3
    } else if (value.indexOf("#") > -1) {
        const indexArr = value.split('#')
        average01.value = Number(indexArr[1])
        average02.value = Number(indexArr[0])
        radioValue.value = 4
    } else if (value.indexOf("L") > -1) {
        const indexArr = value.split("L")
        weekday.value = Number(indexArr[0])
        radioValue.value = 5
    } else {
        checkboxList.value = [...new Set(value.split(',').map(item => Number(item)))]
        radioValue.value = 6
    }
}
// 单选按钮值变化时，处理周与日的互斥关系并emit更新
function onRadioChange() {
    // 周设为不指定时，日改为*
    if (radioValue.value === 2 && props.cron.day === '?') {
        emit('update', 'day', '*', 'week')
    }
    // 周指定了值时，日必须为?
    if (radioValue.value !== 2 && props.cron.day !== '?') {
        emit('update', 'day', '?', 'week')
    }
    switch (radioValue.value) {
        case 1:
            emit('update', 'week', '*', 'week')
            break
        case 2:
            emit('update', 'week', '?', 'week')
            break
        case 3:
            emit('update', 'week', cycleTotal.value, 'week')
            break
        case 4:
            emit('update', 'week', averageTotal.value, 'week')
            break
        case 5:
            emit('update', 'week', weekdayTotal.value, 'week')
            break
        case 6:
            if (checkboxList.value.length === 0) {
                checkboxList.value.push(checkCopy.value[0])
            } else {
                checkCopy.value = checkboxList.value
            }
            emit('update', 'week', checkboxString.value, 'week')
            break
    }
}
</script>

<style lang="scss" scoped>
.el-input-number--small, .el-select, .el-select--small {
    margin: 0 0.5rem;
}
.el-select, .el-select--small {
    width: 8rem;
}
.el-select.multiselect, .el-select--small.multiselect {
    width: 17.8rem;
}
</style>