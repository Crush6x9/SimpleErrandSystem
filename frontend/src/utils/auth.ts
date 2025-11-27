// 登录管理
// src/utils/auth.ts
import { authAPI } from '@/api';

/**
 * 检查用户是否已登录
 * @returns boolean 是否已登录
 */
export const isAuthenticated = (): boolean => {
  return !!localStorage.getItem('authToken')
}

/**
 * 获取用户手机号
 * @returns string | null 用户手机号，未登录时返回 null
 */
export const getUserPhone = (): string | null => {
  return localStorage.getItem('userPhone')
}

/**
 * 获取用户ID
 * @returns string | null 用户ID，未登录时返回 null
 */
export const getUserId = (): string | null => {
  return localStorage.getItem('userId')
}

/**
 * 保存用户登录状态
 * @param token 认证令牌
 * @param phone 用户手机号
 * @param userId 用户ID（可选）
 */
export const login = (token: string, phone: string, userId?: string) => {
  localStorage.setItem('authToken', token)
  localStorage.setItem('userPhone', phone)
  if (userId) {
    localStorage.setItem('userId', userId)
  }
}

/**
 * 清除用户登录状态
 */
export const logout = () => {
  localStorage.removeItem('authToken')
  localStorage.removeItem('userPhone')
  localStorage.removeItem('userId')
  localStorage.removeItem('userData')
}

/**
 * 验证验证码
 * @param phone 手机号
 * @param code 验证码
 * @param type 验证码类型
 */
export const verifyCode = async (phone: string, code: string, type: string) => {
  try {
    const response = await authAPI.verifyCode({ phone, code, type })
    return response
  } catch (error) {
    throw error
  }
}

/**
 * 发送验证码
 * @param phone 手机号
 * @param type 验证码类型
 */
export const sendVerificationCode = async (phone: string, type: string) => {
  try {
    const response = await authAPI.sendCode({ phone, type })
    return response
  } catch (error) {
    throw error
  }
}