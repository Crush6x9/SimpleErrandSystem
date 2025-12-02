// 用户数据管理
import { isAuthenticated, getUserId, getAuthToken } from './auth';
import { userAPI } from '@/api';

// 模拟用户数据
const DEFAULT_USER = {
  id: null,
  phone: '',
  username: '用户',
  role: '0',
  studentId: '',
  verified: false,
  avatarUrl: '/default-profile-photo.png',
  certificateUrl: '',
  createdTime: '',
  updatedTime: ''
};

export const getUser = () => {
  if (!isAuthenticated()) return DEFAULT_USER;

  const userInfo = localStorage.getItem('userInfo');
  return userInfo ? JSON.parse(userInfo) : DEFAULT_USER;
}

export const saveUser = (user: any) => {
  localStorage.setItem('userInfo', JSON.stringify(user));
}

export const updateUser = (updates: Partial<typeof DEFAULT_USER>) => {
  const user = getUser();
  const updatedUser = { ...user, ...updates };
  saveUser(updatedUser);

  return updatedUser;
}

// 从后端加载用户信息
export const loadUserFromServer = async () => {
  try {
    if (!isAuthenticated()) {
      console.error('用户未登录');
      return getUser();
    }

    const response = await userAPI.getUserInfo();
    if (response.code === 200 && response.data) {
      saveUser(response.data);
      return response.data;
    }
  } catch (error) {
    console.error('从后端加载用户信息失败:', error);
  }
  return getUser();
}

export const uploadAvatar = async (file: File) => {
  try {
    if (!isAuthenticated()) {
      throw new Error('未登录');
    }

    const formData = new FormData();
    formData.append('avatar', file);

    const response = await userAPI.uploadAvatar(formData);
    if (response.code === 200 && response.data) {
      const user = getUser();
      user.avatarUrl = response.data;
      saveUser(user);
      return response.data;
    }
    throw new Error(response.message || '上传头像失败');
  } catch (error) {
    console.error('上传头像失败:', error);
    throw error;
  }
}

// 用户认证
export const submitCertification = async (certificationData: {
  studentId: string;
  idCardImage: File;
  avatar?: File;
}) => {
  try {
    if (!isAuthenticated()) {
      throw new Error('未登录');
    }

    const formData = new FormData();
    formData.append('studentId', certificationData.studentId);
    formData.append('idCardImage', certificationData.idCardImage);
    if (certificationData.avatar) {
      formData.append('avatar', certificationData.avatar);
    }

    const response = await userAPI.certification(formData);
    if (response.code === 200 && response.data) {
      saveUser(response.data);
      return response.data;
    }
    throw new Error(response.message || '认证失败');
  } catch (error) {
    console.error('提交认证失败:', error);
    throw error;
  }
}

export const updateUsername = async (username: string) => {
  try {
    if (!isAuthenticated()) {
      throw new Error('未登录');
    }

    const response = await userAPI.updateUsername({ username });
    if (response.code === 200 && response.data) {
      saveUser(response.data);
      return response.data;
    }
    throw new Error(response.message || '修改用户名失败');
  } catch (error) {
    console.error('修改用户名失败:', error);
    throw error;
  }
}

export const updateUserInfo = async (data: {
  username?: string;
  phone?: string;
  studentId?: string;
  idCardImage?: File;
}) => {
  try {
    if (!isAuthenticated()) {
      throw new Error('未登录');
    }

    const formData = new FormData();
    if (data.username) formData.append('username', data.username);
    if (data.phone) formData.append('phone', data.phone);
    if (data.studentId) formData.append('studentId', data.studentId);
    if (data.idCardImage) formData.append('idCardImage', data.idCardImage);

    const response = await userAPI.updateUserInfo(formData);
    if (response.code === 200 && response.data) {
      saveUser(response.data);
      return response.data;
    }
    throw new Error(response.message || '更新用户信息失败');
  } catch (error) {
    console.error('更新用户信息失败:', error);
    throw error;
  }
}