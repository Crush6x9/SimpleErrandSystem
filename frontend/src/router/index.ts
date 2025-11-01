// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'

// 使用动态导入，实现懒加载
const routes = [
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/AboutView.vue')
  },
    {
    path: '/help',
    name: 'Help',
    component: () => import('../views/HelpView.vue')
  },
    {
    path: '/my',
    name: 'My',
    component: () => import('../views/MyView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router