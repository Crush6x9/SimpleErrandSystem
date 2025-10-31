import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './styles/global.css'  // 引入全局样式

// 引入 Element Plus/vant 及其 CSS
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import Vant from 'vant'
import 'vant/lib/index.css'
// 可选：中文语言支持
import zhCn from 'element-plus/es/locale/lang/zh-cn'
// import { NavBar, Button } from 'vant'
// 创建应用
const app = createApp(App)

// 使用插件
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
  size: 'default' // 全局组件尺寸 'small' | 'default' | 'large'
})
// app.use(NavBar)
// app.use(Button)
app.use(Vant)
app.mount('#app')
