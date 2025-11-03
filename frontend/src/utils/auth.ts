
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
 * 保存用户登录状态
 * @param token 认证令牌
 * @param phone 用户手机号
 */
export const login = (token: string, phone: string) => {
  localStorage.setItem('authToken', token)
  localStorage.setItem('userPhone', phone)
}

/**
 * 清除用户登录状态
 */
export const logout = () => {
  localStorage.removeItem('authToken')
  localStorage.removeItem('userPhone')
}