// 用户数据管理
import { isAuthenticated, getUserId } from './auth'
import { userAPI } from '@/api'

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
  
  // 同步到后端（如果有用户ID）
  const userId = getUserId()
  if (userId) {
    userAPI.updateUserInfo(userId, updates).catch(error => {
      console.error('同步用户信息到后端失败:', error)
    })
  }
  
  return updatedUser
}

// 从后端加载用户信息
export const loadUserFromServer = async (userId: string) => {
  try {
    const response = await userAPI.getUserInfo(userId)
    if (response.data) {
      saveUser(response.data)
      return response.data
    }
  } catch (error) {
    console.error('从后端加载用户信息失败:', error)
  }
  return getUser()
}

// 上传头像
export const uploadAvatar = async (userId: string, file: File) => {
  try {
    const formData = new FormData()
    formData.append('avatar', file)
    
    const response = await userAPI.uploadAvatar(userId, formData)
    if (response.data && response.data.avatarUrl) {
      const updatedUser = updateUser({ avatar: response.data.avatarUrl })
      return updatedUser
    }
  } catch (error) {
    console.error('上传头像失败:', error)
    throw error
  }
}

// 用户认证
export const submitCertification = async (userId: string, certificationData: {
  studentId: string
  proof: string
}) => {
  try {
    const response = await userAPI.certification(userId, certificationData)
    return response
  } catch (error) {
    console.error('提交认证失败:', error)
    throw error
  }
}