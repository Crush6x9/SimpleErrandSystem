// 用户数据管理
import { isAuthenticated } from './auth'

// 模拟用户数据
const DEFAULT_USER = {
  avatar: '/default-profile-photo.png',
  nickname: '用户',
  studentId: '',
  phone: '',
  proof: '',
  receivedOrders: 0,
  postedOrders: 0,
  earnings: 0,
 好评: 0,
  差评: 0,
  totalEarnings: 0,
  withdrawableBalance: 0
}

// 获取用户数据
export const getUser = () => {
  if (!isAuthenticated()) return DEFAULT_USER
  
  const userData = localStorage.getItem('userData')
  return userData ? JSON.parse(userData) : DEFAULT_USER
}

// 保存用户数据
export const saveUser = (user: any) => {
  localStorage.setItem('userData', JSON.stringify(user))
}

// 更新用户数据
export const updateUser = (updates: Partial<typeof DEFAULT_USER>) => {
  const user = getUser()
  const updatedUser = { ...user, ...updates }
  saveUser(updatedUser)
  return updatedUser
}