// src/router/index.ts 
import { createRouter, createWebHistory } from 'vue-router';
import { isAuthenticated } from '@/utils/auth';

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
    component: () => import('../views/LoginView.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/forget-password',
    name: 'ForgetPassword',
    component: () => import('../views/ForgetPasswordView.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/set-new-password',
    name: 'SetNewPassword',
    component: () => import('../views/SetNewPasswordView.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: { requiresGuest: true }
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
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/wallet',
    name: 'Wallet',
    component: () => import('../views/WalletView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order-complete',
    name: 'OrderComplete',
    component: () => import('../views/OrderComplete.vue'),
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/order-rate',
    name: 'OrderRate',
    component: () => import('../views/OrderRate.vue'),
    props: true,
    meta: { requiresAuth: true }
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

// 导航守卫
router.beforeEach((to, from, next) => {
  const isLoggedIn = isAuthenticated();

  // 检查是否需要认证
  if (to.meta.requiresAuth && !isLoggedIn) {
    next({
      name: 'Login',
      query: { redirect: to.fullPath }
    });
  }
  // 检查是否已登录的用户不能访问登录/注册页面
  else if (to.meta.requiresGuest && isLoggedIn) {
    next({ name: 'Home' });
  }
  else {
    next();
  }
});

export default router;