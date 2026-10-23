// 导入所有 Element Plus 图标组件
import * as components from '@element-plus/icons-vue'

// 导出插件安装函数，将所有 Element Plus 图标注册为全局组件
export default {
    install: (app) => {
        // 遍历所有图标组件
        for (const key in components) {
            const componentConfig = components[key];
            // 逐个注册为全局组件
            app.component(componentConfig.name, componentConfig);
        }
    },
};
