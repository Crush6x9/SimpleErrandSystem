import axios from 'axios';
import { Toast } from 'vant';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

// 请求拦截器,添加token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器,处理错误
api.interceptors.response.use(
  (response) => {
    console.log('API响应成功:', {
      url: response.config.url,
      method: response.config.method,
      data: response.data
    });

    // 直接返回整个响应数据
    return response.data;
  },
  (error) => {
    console.error('API请求错误详情:', {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message
    });

    if (error.response) {
      switch (error.response.status) {
        case 401:
          Toast('登录已过期，请重新登录');
          localStorage.removeItem('authToken');
          localStorage.removeItem('userId');
          localStorage.removeItem('userPhone');
          localStorage.removeItem('userInfo');
          window.location.href = '/login';
          break;
        case 403:
          Toast('没有权限访问');
          break;
        case 404:
          Toast('请求的资源不存在');
          break;
        case 500:
          Toast('服务器错误，请稍后重试');
          break;
        default:
          Toast(`网络错误: ${error.response.status}`);
      }
    } else if (error.request) {
      console.error('没有收到响应:', error.request);
      Toast('网络连接失败，请检查网络连接');
    } else {
      Toast('请求配置错误');
    }
    return Promise.reject(error);
  }
);

// 认证相关接口
export const authAPI = {
  register: (data: { phone: string; password: string; verifyCode: string }) =>
    api.post('/auth/register', data),

  login: (data: { phone: string; password: string }) =>
    api.post('/auth/login', data),

  checkPhone: (data: { phone: string }) =>
    api.post('/auth/forget-password/check', data),

  resetPassword: (data: { phone: string; newPassword: string; verifyCode: string }) =>
    api.post('/auth/forget-password/reset', data),

  sendCode: (data: { phone: string }) =>
    api.post('/auth/send-code', data),

  verifyCode: (data: { phone: string; code: string }) =>
    api.post('/auth/verify-code', data),
};

// 用户相关接口
export const userAPI = {
  updateUsername: (data: { username: string }) =>
    api.put('/user/username', data),

  // 用户认证成为跑腿员
  certification: (data: FormData) =>
    api.post('/user/certification', data, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }),

  getUserInfo: () =>
    api.get('/user/info'),

  updateUserInfo: (data: FormData) =>
    api.put('/user/profile', data, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }),

  // 上传头像
  uploadAvatar: (data: FormData) =>
    api.post('/user/avatar', data, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }),
};

// 订单相关接口
export const orderAPI = {
  publish: (data: {
    title: string;
    address: string;
    description: string;
    helpTime: string;
    phone: string;
    reward: number;
  }) => api.post('/orders/publish', data),

  accept: (orderId: string) =>
    api.put(`/orders/${orderId}/accept`),

  cancelAccept: (orderId: string) =>
    api.put(`/orders/${orderId}/cancel-accept`),

  complete: (orderId: string) =>
    api.put(`/orders/${orderId}/complete`),

  cancel: (orderId: string) =>
    api.delete(`/orders/${orderId}/cancel`),

  // 查询订单列表
  getList: (params: {
    type?: string;
    page?: number;
    size?: number;
  }) => {
    return api.get('/orders/list', {
      params: {
        ...params,
        page: params.page || 1,
        size: params.size || 10
      }
    });
  },

  getDetail: (orderId: string) =>
    api.get(`/orders/${orderId}`),

  // 获取订单统计
  getStats: () =>
    api.get('/orders/stats'),
};

// 钱包相关接口
export const walletAPI = {
  getWallet: () =>
    api.get('/wallet/list'),

  // 提现
  withdraw: (data: { amount: number }) =>
    api.post('/wallet/withdraw', data),

  getWithdrawals: () =>
    api.get('/wallet/withdrawals'),

  // 获取账单明细
  getBills: (params: { page?: number; size?: number; type?: string }) =>
    api.get('/wallet/bills', { params }),
};

// 评价相关接口
export const evaluationAPI = {
  createEvaluation: (orderId: string, data: { review: string }) =>
    api.post(`/evaluations/${orderId}`, data),

  getEvaluationByOrderId: (orderId: string) =>
    api.get(`/evaluations/${orderId}`),

  getHelperEvaluationStats: () =>
    api.get('/evaluations/stats'),
};

export default api;