// v-copyText 复制文本内容指令
// Copyright (c) 2022 ruoyi

export default {
  // 指令挂载前执行，根据参数区分回调和复制值
  beforeMount(el, { value, arg }) {
    // 如果参数是 callback，则存储复制完成后的回调函数
    if (arg === "callback") {
      el.$copyCallback = value;
    } else {
      // 存储需要复制的文本值
      el.$copyValue = value;
      // 定义点击事件处理函数
      const handler = () => {
        // 调用复制到剪贴板函数
        copyTextToClipboard(el.$copyValue);
        // 如果有回调则执行
        if (el.$copyCallback) {
          el.$copyCallback(el.$copyValue);
        }
      };
      // 给元素绑定点击事件
      el.addEventListener("click", handler);
      // 存储销毁函数以便卸载时移除事件监听
      el.$destroyCopy = () => el.removeEventListener("click", handler);
    }
  }
}

// 将文本复制到剪贴板的核心函数
function copyTextToClipboard(input, { target = document.body } = {}) {
  // 创建一个隐藏的 textarea 元素
  const element = document.createElement('textarea');
  // 记录当前获得焦点的元素，以便复制后恢复
  const previouslyFocusedElement = document.activeElement;

  // 设置 textarea 的值
  element.value = input;

  // 防止移动端弹出键盘
  element.setAttribute('readonly', '');

  // 设置样式防止页面闪烁和 iOS 缩放
  element.style.contain = 'strict';
  element.style.position = 'absolute';
  element.style.left = '-9999px';
  element.style.fontSize = '12pt';

  // 获取当前选区对象
  const selection = document.getSelection();
  // 记录当前选区范围
  const originalRange = selection.rangeCount > 0 && selection.getRangeAt(0);

  // 将 textarea 添加到页面中
  target.append(element);
  // 选中文本内容
  element.select();

  // iOS 兼容的选区设置
  element.selectionStart = 0;
  element.selectionEnd = input.length;

  let isSuccess = false;
  try {
    // 执行复制命令
    isSuccess = document.execCommand('copy');
  } catch { }

  // 移除临时 textarea 元素
  element.remove();

  // 恢复之前的选区范围
  if (originalRange) {
    selection.removeAllRanges();
    selection.addRange(originalRange);
  }

  // 恢复之前获得焦点的元素
  if (previouslyFocusedElement) {
    previouslyFocusedElement.focus();
  }

  return isSuccess;
}
