import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './styles/global.css'
import './api'

if (import.meta.env.DEV) {
  console.log('当前环境: 开发环境');
  console.log('API Base URL:', import.meta.env.VITE_API_BASE_URL || '/api');
}

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import Vant from 'vant'
import 'vant/lib/index.css'

import zhCn from 'element-plus/es/locale/lang/zh-cn'

const app = createApp(App)

// 使用插件
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
  size: 'default' // 全局组件尺寸 'small' | 'default' | 'large'
})
app.use(Vant)
app.mount('#app')
