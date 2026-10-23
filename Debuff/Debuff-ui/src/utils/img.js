// 图片地址解析：后端返回的相对路径(/profile/...)需要拼上 API 前缀才能被前端访问
export function resolveImgUrl(url) {
  // 空值返回空字符串
  if (!url) return ''
  // http(s) 绝对地址直接返回，不拼接前缀
  if (/^https?:\/\//.test(url)) return url
  // 获取 API 基础路径
  const base = import.meta.env.VITE_APP_BASE_API
  // 已带前缀（如 /dev-api）不重复拼接
  if (url.startsWith(base)) return url
  // 相对路径拼接 API 前缀后返回
  return base + url
}
