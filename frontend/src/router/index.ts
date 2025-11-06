// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'

// 使用动态导入，实现懒加载
const routes = [
  {
    path: '/',
    redirect: '/home'
  },
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
    component: () => import('../views/OrderList.vue')//直接跳到OrderList.vue  Jump directly to OrderList.vue
  },
    {
    path: '/my',
    name: 'my',
    component: () => import('../views/MyView.vue'),  
    meta: { requiresAuth: true }
},
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/forget-password',
    name: 'ForgetPassword',
    component: () => import('../views/ForgetPasswordView.vue')
  },
  {
    path: '/set-new-password',
    name: 'SetNewPassword',
    component: () => import('../views/SetNewPasswordView.vue'),
    // props: (route) => ({ phone: route.query.phone })
    props: (route: { query: { phone: any } }) => ({ phone: route.query.phone })
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue')
  },
  //追加路由
  {
  path: '/order',
  name: 'OrderList',
  component: () => import('@/views/OrderList.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/order/create',
  name: 'OrderCreate',
  component: () => import('@/views/OrderCreate.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/order/pay/:id',
  name: 'OrderPay',
  component: () => import('@/views/OrderPay.vue'),
  props: true,
  meta: { requiresAuth: true }
},
{
  path: '/wallet',
  name: 'Wallet',
  component: () => import('@/views/Wallet.vue'),
  meta: { requiresAuth: true }
}
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})


// // 全局路由守卫：检查登录状态
// router.beforeEach((to, from, next) => {
//   const isAuthenticated = !!localStorage.getItem('authToken')
  
//   // 如果未登录且访问非登录/注册相关页面
//   if (!isAuthenticated && !['Login', 'Register', 'ForgetPassword', 'SetNewPassword'].includes(to.name as string)) {
//     next({ name: 'Login' })
//   } 
//   // 如果已登录且访问登录页
//   else if (isAuthenticated && to.name === 'Login') {
//     next({ name: 'Home' })
//   } 
//   else {
//     next()
//   }
// })

export default router