import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import './styles/global.css'  // 引入全局样式

// 引入 Element Plus/vant 及其 CSS
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import Vant from 'vant'
import 'vant/lib/index.css'

import zhCn from 'element-plus/es/locale/lang/zh-cn'// 可选：中文语言支持

// 创建应用
const app = createApp(App)

const pinia = createPinia()

// 使用插件
app.use(pinia)// 挂载Pinia
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
  size: 'default' // 全局组件尺寸 'small' | 'default' | 'large'
})
app.use(Vant)
app.mount('#app')
