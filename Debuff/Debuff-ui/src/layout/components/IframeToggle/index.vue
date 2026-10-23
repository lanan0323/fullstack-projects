<template>
  <!-- iframe切换容器：遍历所有iframe视图，仅显示当前路由匹配的iframe -->
  <inner-link
    v-for="(item, index) in tagsViewStore.iframeViews"
    :key="item.path"
    :iframeId="'iframe' + index"
    v-show="route.path === item.path"
    :src="iframeUrl(item.meta.link, item.query)"
  ></inner-link>
</template>

<script setup>
// 引入内嵌链接组件
import InnerLink from "../InnerLink/index";
// 引入标签视图store（获取iframe视图列表）
import useTagsViewStore from "@/store/modules/tagsView";

// 获取当前路由对象
const route = useRoute();
// 获取标签视图store实例
const tagsViewStore = useTagsViewStore();

// 拼接iframe的完整URL（将query参数拼接到URL上）
function iframeUrl(url, query) {
  // 有查询参数时拼接为URL参数格式
  if (Object.keys(query).length > 0) {
    let params = Object.keys(query).map((key) => key + "=" + query[key]).join("&");
    return url + "?" + params;
  }
  // 无参数时直接返回原始URL
  return url;
}
</script>
