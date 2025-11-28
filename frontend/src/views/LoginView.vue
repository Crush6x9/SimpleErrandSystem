<template>
  <div class="login-container">
    <van-nav-bar 
      title="登录" 
      left-text="返回" 
      left-arrow 
      @click-left="handleBack" 
    />
    <!-- 背景图 -->
    <div class="background">
      <van-image src="/login-bg.png" height="100%" fit="cover" alt="背景" />
    </div>
    
    <!-- 登录表单 -->
    <div class="login-form">
      <h1 class="title">欢迎使用</h1>
      
      <!-- 手机号输入 -->
      <van-cell-group inset>
        <van-field
          v-model="phone"
          type="tel"
          label="手机号"
          placeholder="请输入手机号"
          :rules="[{ required: true, message: '请填写手机号' }]"
        />
      </van-cell-group>
      
      <!-- 密码输入 -->
      <van-cell-group inset>
        <van-field
          v-model="password"
          :type="isPasswordVisible ? 'text' : 'password'"
          label="密码"
          placeholder="请输入密码"
          :right-icon="isPasswordVisible ? 'eye-o' : 'closed-eye'"
          @click-right-icon="togglePasswordVisibility"
        />
      </van-cell-group>
      
      <!-- 登录按钮 -->
      <div class="login-button">
        <van-button type="primary" size="large" @click="handleLogin">登录</van-button>
      </div>
      
      <!-- 底部链接 -->
      <div class="links">
        <router-link to="/forget-password" class="link-left">忘记密码</router-link>
        <router-link to="/register" class="link-right">注册账号</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Toast } from 'vant'
import { login } from '@/utils/auth'
import { authAPI } from '@/api'

const router = useRouter()
const route = useRoute()
const phone = ref('')
const password = ref('')
const isPasswordVisible = ref(false)

// 处理返回
const handleBack = () => {
  router.back()
}

// 切换密码可见性
const togglePasswordVisibility = () => {
  isPasswordVisible.value = !isPasswordVisible.value
}

// 登录处理
const handleLogin = async () => {
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    Toast('请输入有效的手机号')
    return
  }
  
  if (password.value.length < 6) {
    Toast('密码至少6位')
    return
  }

  const toast = Toast.loading({
    message: '登录中...',
    forbidClick: true,
    duration: 0
  })

  try {
    // 调用登录API
    const response = await authAPI.login({
      phone: phone.value,
      password: password.value
    })
    
    Toast.clear()
    
    // 检查响应状态
    if (response.code !== 200) {
      Toast(response.message || '登录失败')
      return
    }
    
    Toast('登录成功')
    
    // 保存登录状态
    const loginData = response.data
    if (loginData && loginData.accessToken) {
      // 如果有用户信息，保存用户ID
      const userId = loginData.userInfo?.id
      login(loginData.accessToken, phone.value, userId?.toString())
      
      // 保存完整的用户信息到本地存储
      if (loginData.userInfo) {
        localStorage.setItem('userInfo', JSON.stringify(loginData.userInfo))
      }
    }
    
    // 重定向到首页或之前要访问的页面
    const redirectPath = (route.query.redirect as string) || '/home'
    router.push(redirectPath)
  } catch (error: any) {
    Toast.clear()
    // 显示具体的错误信息
    if (error.response && error.response.data) {
      Toast(error.response.data.message || '登录失败')
    } else if (error.message) {
      Toast(error.message)
    } else {
      Toast('网络错误，请稍后重试')
    }
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  height: 100vh;
  overflow: hidden;
}

.background {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1;
}


.login-form {
  position: absolute;
  padding: 60px 20px 0;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  margin: 40% 20px 0;
  min-height: 50vh;
  z-index: 3;
}

.title {
  text-align: center;
  margin: 20px 0;
  font-size: 24px;
  color: #333;
}

.login-button {
  padding: 30px 15px 0;
}

.links {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  font-size: 14px;
  color: #1989fa;
}

.link-left {
  color: #1989fa;
  text-decoration: none;
}

.link-right {
  color: #1989fa;
  text-decoration: none;
}

:deep(.van-cell-group) {
  margin-bottom: 20px;
  border-radius: 10px;
  overflow: hidden;
}

:deep(.van-field) {
  padding: 12px 15px;
}
</style>