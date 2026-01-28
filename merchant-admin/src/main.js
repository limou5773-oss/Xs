import { createApp } from 'vue'
import App from './App.vue'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

import router from './router'

const app = createApp(App)

// 🔹 注册所有 Element Plus 图标（非常关键）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 🔹 Element Plus 使用中文
app.use(ElementPlus, {
  locale: zhCn,
})

// 🔹 全局覆盖 MessageBox 默认按钮
ElMessageBox.defaults = {
  confirmButtonText: '确定',
  cancelButtonText: '取消',
}

app.use(router)
app.mount('#app')
