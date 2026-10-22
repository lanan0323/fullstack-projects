// 通用 JS 方法封装处理
// Copyright (c) 2019 ruoyi

// 日期格式化，支持 {y}-{m}-{d} {h}:{i}:{s} 等占位符
export function parseTime(time, pattern) {
  // 无参数或 time 为空返回 null
  if (arguments.length === 0 || !time) {
    return null
  }
  // 默认格式
  const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  // time 为 Date 对象直接使用
  if (typeof time === 'object') {
    date = time
  } else {
    // 纯数字字符串转为整数
    if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
      time = parseInt(time)
    } else if (typeof time === 'string') {
      // 将横杠替换为斜杠，去除 T 和毫秒部分
      time = time.replace(new RegExp(/-/gm), '/').replace('T', ' ').replace(new RegExp(/\.[\d]{3}/gm), '');
    }
    // 10 位时间戳转为 13 位毫秒
    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }
  // 构建格式化映射对象
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  // 替换格式字符串中的占位符
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    // 星期占位符返回中文
    if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value] }
    // 小于 10 补零
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return time_str
}

// 重置表单（通过 ref 名称调用 resetFields）
export function resetForm(refName) {
  if (this.$refs[refName]) {
    this.$refs[refName].resetFields();
  }
}

// 添加日期范围参数到查询条件中
export function addDateRange(params, dateRange, propName) {
  let search = params;
  // 确保 params 对象存在
  search.params = typeof (search.params) === 'object' && search.params !== null && !Array.isArray(search.params) ? search.params : {};
  // 确保 dateRange 是数组
  dateRange = Array.isArray(dateRange) ? dateRange : [];
  if (typeof (propName) === 'undefined') {
    // 未指定属性名时使用默认的 beginTime/endTime
    search.params['beginTime'] = dateRange[0];
    search.params['endTime'] = dateRange[1];
  } else {
    // 指定属性名时使用 begin{propName}/end{propName}
    search.params['begin' + propName] = dateRange[0];
    search.params['end' + propName] = dateRange[1];
  }
  return search;
}

// 回显数据字典标签（单个值）
export function selectDictLabel(datas, value) {
  // 值为 undefined 返回空字符串
  if (value === undefined) {
    return "";
  }
  var actions = [];
  // 遍历字典数据查找匹配的值
  Object.keys(datas).some((key) => {
    if (datas[key].value == ('' + value)) {
      actions.push(datas[key].label);
      return true;
    }
  })
  // 未匹配到则直接显示原始值
  if (actions.length === 0) {
    actions.push(value);
  }
  return actions.join('');
}

// 回显数据字典标签（多个值，用分隔符拼接）
export function selectDictLabels(datas, value, separator) {
  // 值为空返回空字符串
  if (value === undefined || value.length ===0) {
    return "";
  }
  // 数组转字符串
  if (Array.isArray(value)) {
    value = value.join(",");
  }
  var actions = [];
  // 默认分隔符为逗号
  var currentSeparator = undefined === separator ? "," : separator;
  // 按分隔符拆分值
  var temp = value.split(currentSeparator);
  // 遍历每个值查找对应的字典标签
  Object.keys(value.split(currentSeparator)).some((val) => {
    var match = false;
    Object.keys(datas).some((key) => {
      if (datas[key].value == ('' + temp[val])) {
        actions.push(datas[key].label + currentSeparator);
        match = true;
      }
    })
    // 未匹配到则保留原始值
    if (!match) {
      actions.push(temp[val] + currentSeparator);
    }
  })
  // 拼接后去掉末尾多余的分隔符
  return actions.join('').substring(0, actions.join('').length - 1);
}

// 字符串格式化，替换 %s 占位符
export function sprintf(str) {
  var args = arguments, flag = true, i = 1;
  // 逐个替换 %s
  str = str.replace(/%s/g, function () {
    var arg = args[i++];
    // 参数不足时标记失败
    if (typeof arg === 'undefined') {
      flag = false;
      return '';
    }
    return arg;
  });
  return flag ? str : '';
}

// 转换字符串，将 undefined/null/"undefined"/"null" 转为空字符串
export function parseStrEmpty(str) {
  if (!str || str == "undefined" || str == "null") {
    return "";
  }
  return str;
}

// 递归合并数据（将 target 的属性合并到 source）
export function mergeRecursive(source, target) {
  // 遍历 target 的每个属性
  for (var p in target) {
    try {
      // 属性值为普通对象则递归合并
      if (target[p].constructor == Object) {
        source[p] = mergeRecursive(source[p], target[p]);
      } else {
        source[p] = target[p];
      }
    } catch (e) {
      // 异常时直接赋值
      source[p] = target[p];
    }
  }
  return source;
};

// 构造树型结构数据（将扁平列表转为嵌套树）
export function handleTree(data, id, parentId, children) {
  // 配置字段名默认值
  let config = {
    id: id || 'id',
    parentId: parentId || 'parentId',
    childrenList: children || 'children'
  };

  // 子节点列表映射表
  var childrenListMap = {};
  // 节点 ID 索引
  var nodeIds = {};
  // 根节点列表
  var tree = [];

  // 第一遍遍历：建立子节点映射和 ID 索引
  for (let d of data) {
    let parentId = d[config.parentId];
    if (childrenListMap[parentId] == null) {
      childrenListMap[parentId] = [];
    }
    nodeIds[d[config.id]] = d;
    childrenListMap[parentId].push(d);
  }

  // 第二遍遍历：找出根节点（parentId 不在索引中的）
  for (let d of data) {
    let parentId = d[config.parentId];
    if (nodeIds[parentId] == null) {
      tree.push(d);
    }
  }

  // 递归填充每个节点的 children
  for (let t of tree) {
    adaptToChildrenList(t);
  }

  // 递归函数：为节点填充子节点列表
  function adaptToChildrenList(o) {
    if (childrenListMap[o[config.id]] !== null) {
      o[config.childrenList] = childrenListMap[o[config.id]];
    }
    if (o[config.childrenList]) {
      for (let c of o[config.childrenList]) {
        adaptToChildrenList(c);
      }
    }
  }
  return tree;
}

// 将参数对象序列化为 URL 查询字符串
export function tansParams(params) {
  let result = ''
  // 遍历参数的每个属性
  for (const propName of Object.keys(params)) {
    const value = params[propName];
    var part = encodeURIComponent(propName) + "=";
    // 跳过空值
    if (value !== null && value !== "" && typeof (value) !== "undefined") {
      // 对象类型的值展开为 propName[key] 格式
      if (typeof value === 'object') {
        for (const key of Object.keys(value)) {
          if (value[key] !== null && value[key] !== "" && typeof (value[key]) !== 'undefined') {
            let params = propName + '[' + key + ']';
            var subPart = encodeURIComponent(params) + "=";
            result += subPart + encodeURIComponent(value[key]) + "&";
          }
        }
      } else {
        result += part + encodeURIComponent(value) + "&";
      }
    }
  }
  return result
}

// 规范化项目路径（去除重复斜杠和末尾斜杠）
export function getNormalPath(p) {
  if (p.length === 0 || !p || p == 'undefined') {
    return p
  };
  // 替换双斜杠为单斜杠
  let res = p.replace('//', '/')
  // 去除末尾斜杠
  if (res[res.length - 1] === '/') {
    return res.slice(0, res.length - 1)
  }
  return res;
}

// 验证响应数据是否为 blob 格式（非 JSON 错误响应）
export function blobValidate(data) {
  return data.type !== 'application/json'
}
