// 导入 Element Plus 的消息、弹窗、通知、加载组件
import { ElMessage, ElMessageBox, ElNotification, ElLoading } from 'element-plus'

// 加载遮罩实例变量
let loadingInstance;

// 导出模态框工具对象
export default {
  // 普通消息提示
  msg(content) {
    ElMessage.info(content)
  },
  // 错误消息提示
  msgError(content) {
    ElMessage.error(content)
  },
  // 成功消息提示
  msgSuccess(content) {
    ElMessage.success(content)
  },
  // 警告消息提示
  msgWarning(content) {
    ElMessage.warning(content)
  },
  // 弹出提示框（普通）
  alert(content) {
    ElMessageBox.alert(content, "系统提示")
  },
  // 弹出错误提示框
  alertError(content) {
    ElMessageBox.alert(content, "系统提示", { type: 'error' })
  },
  // 弹出成功提示框
  alertSuccess(content) {
    ElMessageBox.alert(content, "系统提示", { type: 'success' })
  },
  // 弹出警告提示框
  alertWarning(content) {
    ElMessageBox.alert(content, "系统提示", { type: 'warning' })
  },
  // 通知消息（普通）
  notify(content) {
    ElNotification.info(content)
  },
  // 通知消息（错误）
  notifyError(content) {
    ElNotification.error(content);
  },
  // 通知消息（成功）
  notifySuccess(content) {
    ElNotification.success(content)
  },
  // 通知消息（警告）
  notifyWarning(content) {
    ElNotification.warning(content)
  },
  // 确认弹窗，返回 Promise
  confirm(content) {
    return ElMessageBox.confirm(content, "系统提示", {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: "warning",
    })
  },
  // 输入弹窗，返回 Promise
  prompt(content) {
    return ElMessageBox.prompt(content, "系统提示", {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: "warning",
    })
  },
  // 打开全屏加载遮罩层
  loading(content) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: content,
      background: "rgba(0, 0, 0, 0.7)",
    })
  },
  // 关闭加载遮罩层
  closeLoading() {
    loadingInstance.close();
  }
}
