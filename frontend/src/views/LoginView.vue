<template>
  <div class="login-container">
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
import { useRouter,useRoute } from 'vue-router'
import { Toast } from 'vant'
import { login } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const phone = ref('')
const password = ref('')
const isPasswordVisible = ref(false)

// 切换密码可见性
const togglePasswordVisibility = () => {
  isPasswordVisible.value = !isPasswordVisible.value
}

// 登录处理
const handleLogin = async () => {
  // 前端基础验证
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    Toast('请输入有效的手机号')
    return
  }
  
  if (password.value.length < 6) {
    Toast('密码至少6位')
    return
  }

  // 显示加载状态
  const toast = Toast.loading({
    message: '登录中...',
    forbidClick: true,
    duration: 0
  })

  try {
    // 模拟API调用 - 实际使用时替换为axios请求
    // POST /api/login { phone, password }
    console.log('调用登录API:', { phone: phone.value, password: password.value })
    
    // 模拟异步请求（实际项目替换为真实API）
    await new Promise((resolve) => setTimeout(resolve, 1000))
    
    // 模拟成功响应
    const mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    
    // 保存登录状态（实际项目应存储后端返回的token）
    // localStorage.setItem('authToken', mockToken)
    // localStorage.setItem('userPhone', phone.value)
    login(mockToken,phone.value)
    
    Toast.clear()
    Toast('登录成功')
    
    //重定向
    const redirectPath = (route.query.redirect as string) || '/home'
    // 跳转到首页
    router.push({ name: 'Home' })
  } catch (error) {
    Toast.clear()
    Toast('手机号或密码错误')
    // 实际项目中这里应该处理错误
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
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

/* .background img {
  width: 100%;
  height: 100%;
  object-fit: cover;
} */

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