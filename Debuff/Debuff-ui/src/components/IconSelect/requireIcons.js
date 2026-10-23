// 定义图标数组
let icons = []
// 动态导入 assets/icons/svg/ 目录下所有 SVG 文件
const modules = import.meta.glob('./../../assets/icons/svg/*.svg');
// 遍历提取文件名作为图标名称
for (const path in modules) {
  // 从路径中提取 SVG 文件名（去掉路径前缀和 .svg 后缀）
  const p = path.split('assets/icons/svg/')[1].split('.svg')[0];
  icons.push(p);
}

// 导出图标名称数组
export default icons
