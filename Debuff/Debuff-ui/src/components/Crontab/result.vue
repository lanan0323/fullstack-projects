<!-- Cron表达式运行结果预测组件，计算最近5次运行时间 -->
<template>
	<div class="popup-result">
		<p class="title">最近5次运行时间</p>
		<ul class="popup-result-scroll">
			<!-- 计算完成后展示结果列表 -->
			<template v-if='isShow'>
				<li v-for='item in resultList' :key="item">{{item}}</li>
			</template>
			<!-- 计算中提示 -->
			<li v-else>计算结果中...</li>
		</ul>
	</div>
</template>

<script setup>
// 接收父组件传入的cron表达式
const props = defineProps({
    ex: {
        type: String,
        default: ''
    }
})
// 日期规则类型（workDay/lastDay/weekDay/assWeek/lastWeek）
const dayRule = ref('')
// 日期规则的辅助值
const dayRuleSup = ref('')
// 存储各时间维度的可选值数组 [秒,分,时,日,月,年]
const dateArr = ref([])
// 计算出的结果列表
const resultList = ref([])
// 是否显示计算结果
const isShow = ref(false)
// 监听表达式变化时重新计算
watch(() => props.ex, () => expressionChange())
// 表达式值变化时，开始去计算结果
function expressionChange() {
    // 计算开始-隐藏结果
    isShow.value = false;
    // 获取规则数组[0秒、1分、2时、3日、4月、5星期、6年]
    let ruleArr = props.ex.split(' ');
    // 用于记录进入循环的次数
    let nums = 0;
    // 用于暂时存符号时间规则结果的数组
    let resultArr = [];
    // 获取当前时间精确至[年、月、日、时、分、秒]
    let nTime = new Date();
    let nYear = nTime.getFullYear();
    let nMonth = nTime.getMonth() + 1;
    let nDay = nTime.getDate();
    let nHour = nTime.getHours();
    let nMin = nTime.getMinutes();
    let nSecond = nTime.getSeconds();
    // 根据规则获取到近100年可能年数组、月数组等等
    getSecondArr(ruleArr[0]);
    getMinArr(ruleArr[1]);
    getHourArr(ruleArr[2]);
    getDayArr(ruleArr[3]);
    getMonthArr(ruleArr[4]);
    getWeekArr(ruleArr[5]);
    getYearArr(ruleArr[6], nYear);
    // 将获取到的数组赋值-方便使用
    let sDate = dateArr.value[0];
    let mDate = dateArr.value[1];
    let hDate = dateArr.value[2];
    let DDate = dateArr.value[3];
    let MDate = dateArr.value[4];
    let YDate = dateArr.value[5];
    // 获取当前时间在数组中的索引
    let sIdx = getIndex(sDate, nSecond);
    let mIdx = getIndex(mDate, nMin);
    let hIdx = getIndex(hDate, nHour);
    let DIdx = getIndex(DDate, nDay);
    let MIdx = getIndex(MDate, nMonth);
    let YIdx = getIndex(YDate, nYear);
    // 重置秒的函数
    const resetSecond = function () {
        sIdx = 0;
        nSecond = sDate[sIdx]
    }
    // 重置分的函数
    const resetMin = function () {
        mIdx = 0;
        nMin = mDate[mIdx]
        resetSecond();
    }
    // 重置时的函数
    const resetHour = function () {
        hIdx = 0;
        nHour = hDate[hIdx]
        resetMin();
    }
    // 重置日的函数
    const resetDay = function () {
        DIdx = 0;
        nDay = DDate[DIdx]
        resetHour();
    }
    // 重置月的函数
    const resetMonth = function () {
        MIdx = 0;
        nMonth = MDate[MIdx]
        resetDay();
    }
    // 如果当前年份不为数组中当前值
    if (nYear !== YDate[YIdx]) {
        resetMonth();
    }
    // 如果当前月份不为数组中当前值
    if (nMonth !== MDate[MIdx]) {
        resetDay();
    }
    // 如果当前"日"不为数组中当前值
    if (nDay !== DDate[DIdx]) {
        resetHour();
    }
    // 如果当前"时"不为数组中当前值
    if (nHour !== hDate[hIdx]) {
        resetMin();
    }
    // 如果当前"分"不为数组中当前值
    if (nMin !== mDate[mIdx]) {
        resetSecond();
    }
    // 循环年份数组
    goYear: for (let Yi = YIdx; Yi < YDate.length; Yi++) {
        let YY = YDate[Yi];
        // 如果月份超过最大值则重置
        if (nMonth > MDate[MDate.length - 1]) {
            resetMonth();
            continue;
        }
        // 循环月份数组
        goMonth: for (let Mi = MIdx; Mi < MDate.length; Mi++) {
            // 赋值、补零
            let MM = MDate[Mi];
            MM = MM < 10 ? '0' + MM : MM;
            // 如果日超过最大值则重置
            if (nDay > DDate[DDate.length - 1]) {
                resetDay();
                if (Mi === MDate.length - 1) {
                    resetMonth();
                    continue goYear;
                }
                continue;
            }
            // 循环日期数组
            goDay: for (let Di = DIdx; Di < DDate.length; Di++) {
                // 赋值、补零
                let DD = DDate[Di];
                let thisDD = DD < 10 ? '0' + DD : DD;
                // 如果时超过最大值则重置
                if (nHour > hDate[hDate.length - 1]) {
                    resetHour();
                    if (Di === DDate.length - 1) {
                        resetDay();
                        if (Mi === MDate.length - 1) {
                            resetMonth();
                            continue goYear;
                        }
                        continue goMonth;
                    }
                    continue;
                }
                // 判断日期的合法性
                if (checkDate(YY + '-' + MM + '-' + thisDD + ' 00:00:00') !== true && dayRule.value !== 'workDay' && dayRule.value !== 'lastWeek' && dayRule.value !== 'lastDay') {
                    resetDay();
                    continue goMonth;
                }
                // 如果日期规则为每月最后一天
                if (dayRule.value === 'lastDay') {
                    // 调整到月末合法日期
                    if (checkDate(YY + '-' + MM + '-' + thisDD + ' 00:00:00') !== true) {
                        while (DD > 0 && checkDate(YY + '-' + MM + '-' + thisDD + ' 00:00:00') !== true) {
                            DD--;
                            thisDD = DD < 10 ? '0' + DD : DD;
                        }
                    }
                } else if (dayRule.value === 'workDay') {
                    // 工作日规则：调整非法日期到月末
                    if (checkDate(YY + '-' + MM + '-' + thisDD + ' 00:00:00') !== true) {
                        while (DD > 0 && checkDate(YY + '-' + MM + '-' + thisDD + ' 00:00:00') !== true) {
                            DD--;
                            thisDD = DD < 10 ? '0' + DD : DD;
                        }
                    }
                    // 获取当前日期是星期几
                    let thisWeek = formatDate(new Date(YY + '-' + MM + '-' + thisDD + ' 00:00:00'), 'week');
                    // 当星期日时，往后找一天
                    if (thisWeek === 1) {
                        DD++;
                        thisDD = DD < 10 ? '0' + DD : DD;
                        // 如果下一日不是合法日期，往前退3天（到周五）
                        if (checkDate(YY + '-' + MM + '-' + thisDD + ' 00:00:00') !== true) {
                            DD -= 3;
                        }
                    } else if (thisWeek === 7) {
                        // 当星期六时，判断是否为1号
                        if (dayRuleSup.value !== 1) {
                            DD--;
                        } else {
                            DD += 2;
                        }
                    }
                } else if (dayRule.value === 'weekDay') {
                    // 指定星期几的规则
                    let thisWeek = formatDate(new Date(YY + '-' + MM + '-' + DD + ' 00:00:00'), 'week');
                    // 校验当前星期是否在指定列表中
                    if (dayRuleSup.value.indexOf(thisWeek) < 0) {
                        if (Di === DDate.length - 1) {
                            resetDay();
                            if (Mi === MDate.length - 1) {
                                resetMonth();
                                continue goYear;
                            }
                            continue goMonth;
                        }
                        continue;
                    }
                } else if (dayRule.value === 'assWeek') {
                    // 第N周星期M的规则
                    let thisWeek = formatDate(new Date(YY + '-' + MM + '-' + DD + ' 00:00:00'), 'week');
                    if (dayRuleSup.value[1] >= thisWeek) {
                        DD = (dayRuleSup.value[0] - 1) * 7 + dayRuleSup.value[1] - thisWeek + 1;
                    } else {
                        DD = dayRuleSup.value[0] * 7 + dayRuleSup.value[1] - thisWeek + 1;
                    }
                } else if (dayRule.value === 'lastWeek') {
                    // 每月最后一个星期几的规则
                    if (checkDate(YY + '-' + MM + '-' + thisDD + ' 00:00:00') !== true) {
                        while (DD > 0 && checkDate(YY + '-' + MM + '-' + thisDD + ' 00:00:00') !== true) {
                            DD--;
                            thisDD = DD < 10 ? '0' + DD : DD;
                        }
                    }
                    // 获取月末最后一天是星期几
                    let thisWeek = formatDate(new Date(YY + '-' + MM + '-' + thisDD + ' 00:00:00'), 'week');
                    // 找到要求的星期几对应的日期
                    if (dayRuleSup.value < thisWeek) {
                        DD -= thisWeek - dayRuleSup.value;
                    } else if (dayRuleSup.value > thisWeek) {
                        DD -= 7 - (dayRuleSup.value - thisWeek)
                    }
                }
                // 日期补零
                DD = DD < 10 ? '0' + DD : DD;
                // 循环"时"数组
                goHour: for (let hi = hIdx; hi < hDate.length; hi++) {
                    let hh = hDate[hi] < 10 ? '0' + hDate[hi] : hDate[hi]
                    // 如果分超过最大值则重置
                    if (nMin > mDate[mDate.length - 1]) {
                        resetMin();
                        if (hi === hDate.length - 1) {
                            resetHour();
                            if (Di === DDate.length - 1) {
                                resetDay();
                                if (Mi === MDate.length - 1) {
                                    resetMonth();
                                    continue goYear;
                                }
                                continue goMonth;
                            }
                            continue goDay;
                        }
                        continue;
                    }
                    // 循环"分"数组
                    goMin: for (let mi = mIdx; mi < mDate.length; mi++) {
                        let mm = mDate[mi] < 10 ? '0' + mDate[mi] : mDate[mi];
                        // 如果秒超过最大值则重置
                        if (nSecond > sDate[sDate.length - 1]) {
                            resetSecond();
                            if (mi === mDate.length - 1) {
                                resetMin();
                                if (hi === hDate.length - 1) {
                                    resetHour();
                                    if (Di === DDate.length - 1) {
                                        resetDay();
                                        if (Mi === MDate.length - 1) {
                                            resetMonth();
                                            continue goYear;
                                        }
                                        continue goMonth;
                                    }
                                    continue goDay;
                                }
                                continue goHour;
                            }
                            continue;
                        }
                        // 循环"秒"数组
                        goSecond: for (let si = sIdx; si <= sDate.length - 1; si++) {
                            let ss = sDate[si] < 10 ? '0' + sDate[si] : sDate[si];
                            // 添加合法的结果时间
                            if (MM !== '00' && DD !== '00') {
                                resultArr.push(YY + '-' + MM + '-' + DD + ' ' + hh + ':' + mm + ':' + ss)
                                nums++;
                            }
                            // 收集满5条结果就退出所有循环
                            if (nums === 5) break goYear;
                            // 秒到达末尾时重置
                            if (si === sDate.length - 1) {
                                resetSecond();
                                if (mi === mDate.length - 1) {
                                    resetMin();
                                    if (hi === hDate.length - 1) {
                                        resetHour();
                                        if (Di === DDate.length - 1) {
                                            resetDay();
                                            if (Mi === MDate.length - 1) {
                                                resetMonth();
                                                continue goYear;
                                            }
                                            continue goMonth;
                                        }
                                        continue goDay;
                                    }
                                    continue goHour;
                                }
                                continue goMin;
                            }
                        } //goSecond
                    } //goMin
                }//goHour
            }//goDay
        }//goMonth
    }
    // 判断100年内的结果条数
    if (resultArr.length === 0) {
        resultList.value = ['没有达到条件的结果！'];
    } else {
        resultList.value = resultArr;
        // 如果不足5条，追加提示信息
        if (resultArr.length !== 5) {
            resultList.value.push('最近100年内只有上面' + resultArr.length + '条结果！')
        }
    }
    // 计算完成-显示结果
    isShow.value = true;
}
// 用于计算某位数字在数组中的索引位置
function getIndex(arr, value) {
    if (value <= arr[0] || value > arr[arr.length - 1]) {
        return 0;
    } else {
        for (let i = 0; i < arr.length - 1; i++) {
            if (value > arr[i] && value <= arr[i + 1]) {
                return i + 1;
            }
        }
    }
}
// 根据规则解析"年"数组
function getYearArr(rule, year) {
    dateArr.value[5] = getOrderArr(year, year + 100);
    if (rule !== undefined) {
        if (rule.indexOf('-') >= 0) {
            dateArr.value[5] = getCycleArr(rule, year + 100, false)
        } else if (rule.indexOf('/') >= 0) {
            dateArr.value[5] = getAverageArr(rule, year + 100)
        } else if (rule !== '*') {
            dateArr.value[5] = getAssignArr(rule)
        }
    }
}
// 根据规则解析"月"数组
function getMonthArr(rule) {
    dateArr.value[4] = getOrderArr(1, 12);
    if (rule.indexOf('-') >= 0) {
        dateArr.value[4] = getCycleArr(rule, 12, false)
    } else if (rule.indexOf('/') >= 0) {
        dateArr.value[4] = getAverageArr(rule, 12)
    } else if (rule !== '*') {
        dateArr.value[4] = getAssignArr(rule)
    }
}
// 根据规则解析"周"数组，设置dayRule和dayRuleSup
function getWeekArr(rule) {
    // 只有当日期规则的两个值均为空时则表达日期是有选项的
    if (dayRule.value === '' && dayRuleSup.value === '') {
        if (rule.indexOf('-') >= 0) {
            dayRule.value = 'weekDay';
            dayRuleSup.value = getCycleArr(rule, 7, false)
        } else if (rule.indexOf('#') >= 0) {
            dayRule.value = 'assWeek';
            let matchRule = rule.match(/[0-9]{1}/g);
            dayRuleSup.value = [Number(matchRule[1]), Number(matchRule[0])];
            dateArr.value[3] = [1];
            if (dayRuleSup.value[1] === 7) {
                dayRuleSup.value[1] = 0;
            }
        } else if (rule.indexOf('L') >= 0) {
            dayRule.value = 'lastWeek';
            dayRuleSup.value = Number(rule.match(/[0-9]{1,2}/g)[0]);
            dateArr.value[3] = [31];
            if (dayRuleSup.value === 7) {
                dayRuleSup.value = 0;
            }
        } else if (rule !== '*' && rule !== '?') {
            dayRule.value = 'weekDay';
            dayRuleSup.value = getAssignArr(rule)
        }
    }
}
// 根据规则解析"日"数组，处理W/L等特殊符号
function getDayArr(rule) {
    dateArr.value[3] = getOrderArr(1, 31);
    dayRule.value = '';
    dayRuleSup.value = '';
    if (rule.indexOf('-') >= 0) {
        dateArr.value[3] = getCycleArr(rule, 31, false)
        dayRuleSup.value = 'null';
    } else if (rule.indexOf('/') >= 0) {
        dateArr.value[3] = getAverageArr(rule, 31)
        dayRuleSup.value = 'null';
    } else if (rule.indexOf('W') >= 0) {
        dayRule.value = 'workDay';
        dayRuleSup.value = Number(rule.match(/[0-9]{1,2}/g)[0]);
        dateArr.value[3] = [dayRuleSup.value];
    } else if (rule.indexOf('L') >= 0) {
        dayRule.value = 'lastDay';
        dayRuleSup.value = 'null';
        dateArr.value[3] = [31];
    } else if (rule !== '*' && rule !== '?') {
        dateArr.value[3] = getAssignArr(rule)
        dayRuleSup.value = 'null';
    } else if (rule === '*') {
        dayRuleSup.value = 'null';
    }
}
// 根据规则解析"时"数组
function getHourArr(rule) {
    dateArr.value[2] = getOrderArr(0, 23);
    if (rule.indexOf('-') >= 0) {
        dateArr.value[2] = getCycleArr(rule, 24, true)
    } else if (rule.indexOf('/') >= 0) {
        dateArr.value[2] = getAverageArr(rule, 23)
    } else if (rule !== '*') {
        dateArr.value[2] = getAssignArr(rule)
    }
}
// 根据规则解析"分"数组
function getMinArr(rule) {
    dateArr.value[1] = getOrderArr(0, 59);
    if (rule.indexOf('-') >= 0) {
        dateArr.value[1] = getCycleArr(rule, 60, true)
    } else if (rule.indexOf('/') >= 0) {
        dateArr.value[1] = getAverageArr(rule, 59)
    } else if (rule !== '*') {
        dateArr.value[1] = getAssignArr(rule)
    }
}
// 根据规则解析"秒"数组
function getSecondArr(rule) {
    dateArr.value[0] = getOrderArr(0, 59);
    if (rule.indexOf('-') >= 0) {
        dateArr.value[0] = getCycleArr(rule, 60, true)
    } else if (rule.indexOf('/') >= 0) {
        dateArr.value[0] = getAverageArr(rule, 59)
    } else if (rule !== '*') {
        dateArr.value[0] = getAssignArr(rule)
    }
}
// 生成min到max的连续数组
function getOrderArr(min, max) {
    let arr = [];
    for (let i = min; i <= max; i++) {
        arr.push(i);
    }
    return arr;
}
// 解析逗号分隔的指定值列表为数组
function getAssignArr(rule) {
    let arr = [];
    let assiginArr = rule.split(',');
    for (let i = 0; i < assiginArr.length; i++) {
        arr[i] = Number(assiginArr[i])
    }
    arr.sort(compare)
    return arr;
}
// 解析步长表达式（如 5/10）为数组
function getAverageArr(rule, limit) {
    let arr = [];
    let agArr = rule.split('/');
    let min = Number(agArr[0]);
    let step = Number(agArr[1]);
    while (min <= limit) {
        arr.push(min);
        min += step;
    }
    return arr;
}
// 解析周期表达式（如 3-8）为数组
function getCycleArr(rule, limit, status) {
    // status--表示是否从0开始（则从1开始）
    let arr = [];
    let cycleArr = rule.split('-');
    let min = Number(cycleArr[0]);
    let max = Number(cycleArr[1]);
    if (min > max) {
        max += limit;
    }
    for (let i = min; i <= max; i++) {
        let add = 0;
        if (status === false && i % limit === 0) {
            add = limit;
        }
        arr.push(Math.round(i % limit + add))
    }
    arr.sort(compare)
    return arr;
}
// 数组排序比较函数
function compare(value1, value2) {
    if (value2 - value1 > 0) {
        return -1;
    } else {
        return 1;
    }
}
// 格式化日期，type为'week'时返回星期值
function formatDate(value, type) {
    // 计算日期相关值
    let time = typeof value == 'number' ? new Date(value) : value;
    let Y = time.getFullYear();
    let M = time.getMonth() + 1;
    let D = time.getDate();
    let h = time.getHours();
    let m = time.getMinutes();
    let s = time.getSeconds();
    let week = time.getDay();
    // 如果传递了type为week则返回星期
    if (type === undefined) {
        return Y + '-' + (M < 10 ? '0' + M : M) + '-' + (D < 10 ? '0' + D : D) + ' ' + (h < 10 ? '0' + h : h) + ':' + (m < 10 ? '0' + m : m) + ':' + (s < 10 ? '0' + s : s);
    } else if (type === 'week') {
        // 在quartz中 1为星期日
        return week + 1;
    }
}
// 检查日期是否存在（验证日期合法性）
function checkDate(value) {
    let time = new Date(value);
    let format = formatDate(time)
    return value === format;
}
// 组件挂载时开始计算
onMounted(() => {
    expressionChange()
})
</script>