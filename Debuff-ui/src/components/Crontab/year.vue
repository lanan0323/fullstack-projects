<!-- Cron年份配置子组件 -->
<template>
    <el-form>
        <!-- 不填（留空） -->
        <el-form-item>
            <el-radio :label="1" v-model='radioValue'>
                不填，允许的通配符[, - * /]
            </el-radio>
        </el-form-item>

        <!-- 每年 -->
        <el-form-item>
            <el-radio :label="2" v-model='radioValue'>
                每年
            </el-radio>
        </el-form-item>

        <!-- 周期范围 -->
        <el-form-item>
            <el-radio :label="3" v-model='radioValue'>
                周期从
                <el-input-number v-model='cycle01' :min='fullYear' :max="2098"/> -
                <el-input-number v-model='cycle02' :min="cycle01 ? cycle01 + 1 : fullYear + 1" :max="2099"/>
            </el-radio>
        </el-form-item>

        <!-- 间隔执行 -->
        <el-form-item>
            <el-radio :label="4" v-model='radioValue'>
                从
                <el-input-number v-model='average01' :min='fullYear' :max="2098"/> 年开始，每
                <el-input-number v-model='average02' :min="1" :max="2099 - average01 || fullYear"/> 年执行一次
            </el-radio>

        </el-form-item>

        <!-- 指定年份 -->
        <el-form-item>
            <el-radio :label="5" v-model='radioValue'>
                指定
                <el-select clearable v-model="checkboxList" placeholder="可多选" multiple :multiple-limit="8">
                    <el-option v-for="item in 9" :key="item" :value="item - 1 + fullYear" :label="item -1 + fullYear" />
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
// 当前年份
const fullYear = ref(0)
// 最大年份（当前+10）
const maxFullYear = ref(0)
// 当前选中的单选按钮值
const radioValue = ref(1)
// 周期起始年
const cycle01 = ref(0)
// 周期结束年
const cycle02 = ref(0)
// 间隔起始年
const average01 = ref(0)
// 间隔步长
const average02 = ref(1)
// 多选指定的年份列表
const checkboxList = ref([])
// 多选列表的备份
const checkCopy = ref([])
// 计算属性：周期范围表达式
const cycleTotal = computed(() => {
    cycle01.value = props.check(cycle01.value, fullYear.value, maxFullYear.value - 1)
    cycle02.value = props.check(cycle02.value, cycle01.value + 1, maxFullYear.value)
    return cycle01.value + '-' + cycle02.value
})
// 计算属性：间隔表达式
const averageTotal = computed(() => {
    average01.value = props.check(average01.value, fullYear.value, maxFullYear.value - 1)
    average02.value = props.check(average02.value, 1, 10)
    return average01.value + '/' + average02.value
})
// 计算属性：多选列表拼接为逗号分隔字符串
const checkboxString = computed(() => {
    return checkboxList.value.join(',')
})
// 监听父组件传入的year字段变化
watch(() => props.cron.year, value => changeRadioValue(value))
// 监听单选按钮和计算值变化
watch([radioValue, cycleTotal, averageTotal, checkboxString], () => onRadioChange())
// 根据cron表达式值解析并设置对应的单选按钮
function changeRadioValue(value) {
    if (value === '') {
        radioValue.value = 1
    } else if (value === "*") {
        radioValue.value = 2
    } else if (value.indexOf("-") > -1) {
        const indexArr = value.split('-')
        cycle01.value = Number(indexArr[0])
        cycle02.value = Number(indexArr[1])
        radioValue.value = 3
    } else if (value.indexOf("/") > -1) {
        const indexArr = value.split('/')
        average01.value = Number(indexArr[1])
        average02.value = Number(indexArr[0])
        radioValue.value = 4
    } else {
        checkboxList.value = [...new Set(value.split(',').map(item => Number(item)))]
        radioValue.value = 5
    }
}
// 单选按钮值变化时，向父组件emit对应的cron值
function onRadioChange() {
    switch (radioValue.value) {
        case 1:
            emit('update', 'year', '', 'year')
            break
        case 2:
            emit('update', 'year', '*', 'year')
            break
        case 3:
            emit('update', 'year', cycleTotal.value, 'year')
            break
        case 4:
            emit('update', 'year', averageTotal.value, 'year')
            break
        case 5:
            if (checkboxList.value.length === 0) {
                checkboxList.value.push(checkCopy.value[0])
            } else {
                checkCopy.value = checkboxList.value
            }
            emit('update', 'year', checkboxString.value, 'year')
            break
    }
}
// 组件挂载时初始化当前年份和默认值
onMounted(() => {
    fullYear.value = Number(new Date().getFullYear())
    maxFullYear.value = fullYear.value + 10
    cycle01.value = fullYear.value
    cycle02.value = cycle01.value + 1
    average01.value = fullYear.value
    checkCopy.value = [fullYear.value]
})
</script>

<style lang="scss" scoped>
.el-input-number--small, .el-select, .el-select--small {
    margin: 0 0.2rem;
}
.el-select, .el-select--small {
    width: 18.8rem;
}
</style>