import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(false)
  const userInfo = ref({
    phone: '',
    token: ''
  })

  const login = (phone: string, token: string) => {
    isLoggedIn.value = true
    userInfo.value = { phone, token }
    localStorage.setItem('auth_token', token)
    localStorage.setItem('user_phone', phone)
  }

  const logout = () => {
    isLoggedIn.value = false
    userInfo.value = { phone: '', token: '' }
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_phone')
  }

  const checkAuth = () => {
    const token = localStorage.getItem('auth_token')
    const phone = localStorage.getItem('user_phone')
    if (token && phone) {
      isLoggedIn.value = true
      userInfo.value = { phone, token }
    }
  }

  return {
    isLoggedIn,
    userInfo,
    login,
    logout,
    checkAuth
  }
})