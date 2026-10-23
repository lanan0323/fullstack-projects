// 导入字典 store 用于缓存字典数据
import useDictStore from '@/store/modules/dict'
// 导入获取字典数据的 API 函数
import { getDicts } from '@/api/system/dict/data'

/**
 * 获取字典数据的组合式函数
 */
export function useDict(...args) {
  // 定义响应式对象存储字典数据
  const res = ref({});
  return (() => {
    // 遍历传入的字典类型
    args.forEach((dictType, index) => {
      // 初始化该字典类型的数据为空数组
      res.value[dictType] = [];
      // 尝试从 store 缓存中获取字典数据
      const dicts = useDictStore().getDict(dictType);
      if (dicts) {
        // 缓存中有数据则直接使用
        res.value[dictType] = dicts;
      } else {
        // 缓存中没有则调用 API 获取
        getDicts(dictType).then(resp => {
          // 将 API 返回的数据映射为 { label, value, elTagType, elTagClass } 格式
          res.value[dictType] = resp.data.map(p => ({ label: p.dictLabel, value: p.dictValue, elTagType: p.listClass, elTagClass: p.cssClass }))
          // 将获取到的数据存入 store 缓存
          useDictStore().setDict(dictType, res.value[dictType]);
        })
      }
    })
    // 返回响应式引用
    return toRefs(res.value);
  })()
}
