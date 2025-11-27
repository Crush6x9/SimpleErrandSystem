// src/router/index.ts 
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/help',
    name: 'Help',
    component: () => import('@/views/HelpView.vue')
  },
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
    props: (route: { query: { phone: any } }) => ({ phone: route.query.phone })
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue')
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
    path: '/order/detail/:id',
    name: 'OrderDetail',
    component: () => import('@/views/OrderDetail.vue'),
    props: true
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue')
  },
  {
    path: '/wallet',
    name: 'Wallet',
    component: () => import('../views/WalletView.vue')
  },
  {
    path: '/order-complete',
    name: 'OrderComplete',
    component: () => import('../views/OrderComplete.vue'),
    props: true
  },
  {
    path: '/order-rate',
    name: 'OrderRate',
    component: () => import('../views/OrderRate.vue'),
    props: true
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router