<!-- Cron月份配置子组件 -->
<template>
    <el-form>
        <!-- 每月 -->
        <el-form-item>
            <el-radio v-model='radioValue' :label="1">
                月，允许的通配符[, - * /]
            </el-radio>
        </el-form-item>

        <!-- 周期范围 -->
        <el-form-item>
            <el-radio v-model='radioValue' :label="2">
                周期从
                <el-input-number v-model='cycle01' :min="1" :max="11" /> -
                <el-input-number v-model='cycle02' :min="cycle01 + 1" :max="12" /> 月
            </el-radio>
        </el-form-item>

        <!-- 间隔执行 -->
        <el-form-item>
            <el-radio v-model='radioValue' :label="3">
                从
                <el-input-number v-model='average01' :min="1" :max="11" /> 月开始，每
                <el-input-number v-model='average02' :min="1" :max="12 - average01" /> 月月执行一次
            </el-radio>
        </el-form-item>

        <!-- 指定月份 -->
        <el-form-item>
            <el-radio v-model='radioValue' :label="4">
                指定
                <el-select clearable v-model="checkboxList" placeholder="可多选" multiple :multiple-limit="8">
                    <el-option v-for="item in monthList" :key="item.key" :label="item.value" :value="item.key" />
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
            year: "",
        }
    },
    check: {
        type: Function,
        default: () => {
        }
    }
})
// 当前选中的单选按钮值
const radioValue = ref(1)
// 周期起始月
const cycle01 = ref(1)
// 周期结束月
const cycle02 = ref(2)
// 间隔起始月
const average01 = ref(1)
// 间隔步长
const average02 = ref(1)
// 多选指定的月份列表
const checkboxList = ref([])
// 多选列表的备份
const checkCopy = ref([1])
// 月份中英文映射列表
const monthList = ref([
    {key: 1, value: '一月'},
    {key: 2, value: '二月'},
    {key: 3, value: '三月'},
    {key: 4, value: '四月'},
    {key: 5, value: '五月'},
    {key: 6, value: '六月'},
    {key: 7, value: '七月'},
    {key: 8, value: '八月'},
    {key: 9, value: '九月'},
    {key: 10, value: '十月'},
    {key: 11, value: '十一月'},
    {key: 12, value: '十二月'}
])
// 计算属性：周期范围表达式
const cycleTotal = computed(() => {
    cycle01.value = props.check(cycle01.value, 1, 11)
    cycle02.value = props.check(cycle02.value, cycle01.value + 1, 12)
    return cycle01.value + '-' + cycle02.value
})
// 计算属性：间隔表达式
const averageTotal = computed(() => {
    average01.value = props.check(average01.value, 1, 11)
    average02.value = props.check(average02.value, 1, 12 - average01.value)
    return average01.value + '/' + average02.value
})
// 计算属性：多选列表拼接为逗号分隔字符串
const checkboxString = computed(() => {
    return checkboxList.value.join(',')
})
// 监听父组件传入的month字段变化
watch(() => props.cron.month, value => changeRadioValue(value))
// 监听单选按钮和计算值变化
watch([radioValue, cycleTotal, averageTotal, checkboxString], () => onRadioChange())
// 根据cron表达式值解析并设置对应的单选按钮
function changeRadioValue(value) {
    if (value === '*') {
        radioValue.value = 1
    } else if (value.indexOf('-') > -1) {
        const indexArr = value.split('-')
        cycle01.value = Number(indexArr[0])
        cycle02.value = Number(indexArr[1])
        radioValue.value = 2
    } else if (value.indexOf('/') > -1) {
        const indexArr = value.split('/')
        average01.value = Number(indexArr[0])
        average02.value = Number(indexArr[1])
        radioValue.value = 3
    } else {
        checkboxList.value = [...new Set(value.split(',').map(item => Number(item)))]
        radioValue.value = 4
    }
}
// 单选按钮值变化时，向父组件emit对应的cron值
function onRadioChange() {
    switch (radioValue.value) {
        case 1:
            emit('update', 'month', '*', 'month')
            break
        case 2:
            emit('update', 'month', cycleTotal.value, 'month')
            break
        case 3:
            emit('update', 'month', averageTotal.value, 'month')
            break
        case 4:
            if (checkboxList.value.length === 0) {
                checkboxList.value.push(checkCopy.value[0])
            } else {
                checkCopy.value = checkboxList.value
            }
            emit('update', 'month', checkboxString.value, 'month')
            break
    }
}
</script>

<style lang="scss" scoped>
.el-input-number--small, .el-select, .el-select--small {
    margin: 0 0.2rem;
}
.el-select, .el-select--small {
    width: 18.8rem;
}
</style>