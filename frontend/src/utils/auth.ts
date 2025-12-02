import { authAPI } from '@/api';

/**
 * 检查用户是否已登录
 * @returns boolean 是否已登录
 */
export const isAuthenticated = (): boolean => {
  return !!localStorage.getItem('authToken');
}

/**
 * 获取用户手机号
 * @returns string | null 用户手机号，未登录时返回 null
 */
export const getUserPhone = (): string | null => {
  return localStorage.getItem('userPhone');
}

/**
 * 获取用户ID
 * @returns string | null 用户ID，未登录时返回 null
 */
export const getUserId = (): string | null => {
  return localStorage.getItem('userId');
}

/**
 * 获取用户令牌
 * @returns string | null 用户令牌，未登录时返回 null
 */
export const getAuthToken = (): string | null => {
  return localStorage.getItem('authToken');
}

/**
 * 获取用户信息
 * @returns any | null 用户信息，未登录时返回 null
 */
export const getUserInfo = (): any | null => {
  const userInfo = localStorage.getItem('userInfo');
  return userInfo ? JSON.parse(userInfo) : null;
}

/**
 * 保存用户登录状态
 * @param token 认证令牌
 * @param phone 用户手机号
 * @param userId 用户ID（可选）
 * @param userInfo 用户信息（可选）
 */
export const login = (token: string, phone: string, userId?: string, userInfo?: any) => {
  localStorage.setItem('authToken', token);
  localStorage.setItem('userPhone', phone);
  if (userId) {
    localStorage.setItem('userId', userId);
  }
  if (userInfo) {
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
  }
}

/**
 * 清除用户登录状态
 */
export const logout = () => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('userPhone');
  localStorage.removeItem('userId');
  localStorage.removeItem('userInfo');
}

/**
 * 验证验证码
 * @param phone 手机号
 * @param code 验证码
 */
export const verifyCode = async (phone: string, code: string) => {
  try {
    const response = await authAPI.verifyCode({ phone, code });
    return response;
  } catch (error) {
    throw error;
  }
}

/**
 * 发送验证码
 * @param phone 手机号
 */
export const sendVerificationCode = async (phone: string) => {
  try {
    const response = await authAPI.sendCode({ phone });
    return response;
  } catch (error) {
    throw error;
  }
}

/**
 * 检查手机号是否存在（忘记密码时）
 */
export const checkPhoneExists = async (phone: string) => {
  try {
    const response = await authAPI.checkPhone({ phone });
    return response;
  } catch (error) {
    throw error;
  }
}