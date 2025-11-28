import axios from 'axios';
import { Toast } from 'vant';

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

// 请求拦截器 - 添加token
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

// 响应拦截器 - 处理错误
// 响应拦截器 - 处理错误
api.interceptors.response.use(
  (response) => {
    console.log('API响应成功:', {
      url: response.config.url,
      method: response.config.method,
      data: response.data
    })
    
    // 假设后端返回格式为 { code: number, message: string, data: any }
    if (response.data && response.data.code !== 200 && response.data.code !== 0) {
      const errorMsg = response.data.message || '请求失败'
      Toast(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
    return response.data
  },
  (error) => {
    console.error('API请求错误详情:', {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message
    })
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          Toast('登录已过期，请重新登录')
          localStorage.removeItem('authToken')
          localStorage.removeItem('userId')
          localStorage.removeItem('userPhone')
          window.location.href = '/login'
          break
        case 403:
          Toast('没有权限访问')
          break
        case 404:
          Toast('请求的资源不存在')
          break
        case 500:
          Toast('服务器错误，请稍后重试')
          break
        default:
          Toast(`网络错误: ${error.response.status}`)
      }
    } else if (error.request) {
      console.error('没有收到响应:', error.request)
      Toast('网络连接失败，请检查网络连接')
    } else {
      Toast('请求配置错误')
    }
    return Promise.reject(error)
  }
)

// 认证相关接口
export const authAPI = {
  // 注册
  register: (data: { phone: string; password: string; code: string }) =>
    api.post('/auth/register', data),
  
  // 登录
  login: (data: { phone: string; password: string }) =>
    api.post('/auth/login', data),
  
  // 验证手机号是否存在
  checkPhone: (phone: string) =>
    api.post('/auth/forget-password/check', { phone }),
  
  // 重置密码
  resetPassword: (data: { phone: string; newPassword: string; code: string }) =>
    api.post('/auth/forget-password/reset', data),
  
  // 发送验证码
  sendCode: (data: { phone: string; type: string }) =>
    api.post('/auth/send-code', data),
  
  // 验证验证码
  verifyCode: (data: { phone: string; code: string; type: string }) =>
    api.post('/auth/verify-code', data),
};

// 用户相关接口
export const userAPI = {
  // 获取用户信息
  getUserInfo: (userId: string) =>
    api.get(`/user/${userId}/info`),
  
  // 更新用户信息
  updateUserInfo: (userId: string, data: any) =>
    api.put(`/user/${userId}/profile`, data),
  
  // 修改用户名
  updateUsername: (userId: string, username: string) =>
    api.put(`/user/${userId}/username`, { username }),
  
  // 用户认证
  certification: (userId: string, data: any) =>
    api.post(`/user/${userId}/certification`, data),
  
  // 上传头像
  uploadAvatar: (userId: string, formData: FormData) =>
    api.post(`/user/${userId}/avatar`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
};

// 订单相关接口
export const orderAPI = {
  // 发布订单
  publish: (data: {
    title: string;
    address: string;
    description: string;
    helpTime: string;
    phone: string;
    reward: number;
  }) => api.post('/orders/publish', data),
  
  // 接取订单
  accept: (orderId: string) =>
    api.put(`/orders/${orderId}/accept`),
  
  // 取消接单
  cancelAccept: (orderId: string) =>
    api.put(`/orders/${orderId}/cancel-accept`),
  
  // 完成订单
  complete: (orderId: string) =>
    api.put(`/orders/${orderId}/complete`),
  
  // 取消订单
  cancel: (orderId: string) =>
    api.delete(`/orders/${orderId}/cancel`),
  
  // 查询订单列表 - 修正为 GET 请求并传递参数
  getList: (params: {
    type?: string;
    page?: number;
    size?: number;
  }) => {
    // 将 type 转换为后端需要的格式
    const queryParams: any = {
      page: params.page || 1,
      size: params.size || 10
    };
    
    // 如果有类型参数，添加到查询参数中
    if (params.type && params.type !== 'all') {
      queryParams.type = params.type;
    }
    
    return api.get('/orders/list', { params: queryParams });
  },
  
  // 获取订单详情
  getDetail: (orderId: string) =>
    api.get(`/orders/${orderId}`),
  
  // 获取订单统计
  getStats: () =>
    api.get('/orders/stats'),
};

// 钱包相关接口
export const walletAPI = {
  // 获取钱包信息
  getWallet: () =>
    api.get('/wallet/list'),
  
  // 提现
  withdraw: (data: { amount: number }) =>
    api.post('/wallet/withdraw', data),
  
  // 获取提现记录
  getWithdrawals: () =>
    api.get('/wallet/withdrawals'),
  
  // 获取账单明细
  getBills: (params: { page?: number; size?: number }) =>
    api.get('/wallet/bills', { params }),
};


// 评价相关接口
export const evaluationAPI = {
  // 订单评价
  evaluate: (orderId: string, data: { review: string }) =>
    api.post(`/evaluations/${orderId}`, data),
  
  // 获取订单评价
  getEvaluation: (orderId: string) =>
    api.get(`/evaluations/${orderId}`),
  
  // 获取评价统计
  getStats: (userId: string) =>
    api.get(`/evaluations/${userId}/stats`),
};

export default api;