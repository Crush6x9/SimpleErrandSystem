import { defineStore } from 'pinia'  //defineStore：用来创建一个数据存储的地方
import { ref } from 'vue'  //ref：用来创建可以变化的数据（响应式数据）

export const useAuthStore = defineStore('auth', () => { //使用defineStore定义一个名为'auth'的存储空间,useAuthStore 是这个存储的名字
    
  const isLoggedIn = ref(false) // 响应式变量，初始值为false，表示用户是否登录。
  const userInfo = ref({  //响应式对象，包含两个属性：phone（手机号）和token（认证令牌），初始值为空字符串。
    phone: '',
    token: ''
  })

  const login = (phone: string, token: string) => {  //接收手机号和令牌作为参数。
    isLoggedIn.value = true  //将isLoggedIn设置为true，表示用户已登录。
    userInfo.value = { phone, token }  //更新userInfo的值为传入的手机号和令牌。
    localStorage.setItem('auth_token', token)  //将令牌和手机号分别存储到localStorage中:页面刷新后仍能保持登录状态。
    localStorage.setItem('user_phone', phone)
  }

  const logout = () => {
    isLoggedIn.value = false
    userInfo.value = { phone: '', token: '' }
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_phone')
  }

  const checkAuth = () => {  //检查认证状态
    const token = localStorage.getItem('auth_token')
    const phone = localStorage.getItem('user_phone')
    if (token && phone) {
      isLoggedIn.value = true
      userInfo.value = { phone, token }
    }
  }

  return {  //将状态和方法暴露出去，以便在Vue组件中使用。
    isLoggedIn,
    userInfo,
    login,
    logout,
    checkAuth
  }
})

