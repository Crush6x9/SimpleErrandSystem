<template>
  <div class="set-password-container">
   <van-nav-bar 
      title="设置新密码" 
      left-text="返回" 
      left-arrow 
      @click-left="handleBack" 
    />
    <div class="form-container">
      <h1 class="title">设置新密码</h1>
      
      <!-- 新密码输入 -->
      <van-cell-group inset>
        <van-field
          v-model="newPassword"
          :type="isPasswordVisible ? 'text' : 'password'"
          label="新密码"
          placeholder="请输入新密码"
          :right-icon="isPasswordVisible ? 'eye-o' : 'closed-eye'"
          @click-right-icon="togglePasswordVisibility"
        />
      </van-cell-group>
      
      <!-- 确认密码输入 -->
      <van-cell-group inset>
        <van-field
          v-model="confirmPassword"
          :type="isConfirmVisible ? 'text' : 'password'"
          label="确认密码"
          placeholder="请再次输入新密码"
          :right-icon="isConfirmVisible ? 'eye-o' : 'closed-eye'"
          @click-right-icon="toggleConfirmVisibility"
        />
      </van-cell-group>
      
      <!-- 确认按钮 -->
      <div class="confirm-button">
        <van-button 
          type="primary" 
          size="large" 
          :disabled="!canSubmit"
          @click="handleSubmit"
        >
          确认更改
        </van-button>
      </div>
    </div>
    <!-- 背景图 -->
    <div class="background">
      <van-image src="/password-bg.png" width="100%" fit="cover" alt="背景" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute, type LocationQueryValue } from 'vue-router'
import { Toast } from 'vant'

// 处理返回
const handleBack = () => {
  router.back()
}

const router = useRouter()
const route = useRoute()
const newPassword = ref('')
const confirmPassword = ref('')
const isPasswordVisible = ref(false)
const isConfirmVisible = ref(false)

// 安全获取手机号（处理所有可能情况）
const getPhoneFromQuery = (query: LocationQueryValue | LocationQueryValue[]) => {
  if (Array.isArray(query)) {
    return query[0] || ''
  }
  return query || ''
}

const phone = getPhoneFromQuery(route.query.phone)

// 返回登录页
const onClickLeft = () => {
  router.push({ name: 'Login' })
}

// 切换密码可见性
const togglePasswordVisibility = () => {
  isPasswordVisible.value = !isPasswordVisible.value
}

const toggleConfirmVisibility = () => {
  isConfirmVisible.value = !isConfirmVisible.value
}

// 提交验证
const canSubmit = computed(() => {
  return newPassword.value.length >= 6 && 
         newPassword.value === confirmPassword.value
})

// 提交表单
const handleSubmit = async () => {
  if (newPassword.value.length < 6) {
    Toast('密码至少6位')
    return
  }
  
  if (newPassword.value !== confirmPassword.value) {
    Toast('两次密码不一致')
    return
  }

  const toast = Toast.loading({
    message: '提交中...',
    forbidClick: true,
    duration: 0
  })

  try {
    // 模拟API调用 - 实际使用时替换为axios请求
    // POST /api/set-password { phone, newPassword }
    console.log('调用设置密码API:', { phone, newPassword: newPassword.value })
    
    await new Promise((resolve) => setTimeout(resolve, 800))
    
    Toast.clear()
    Toast('密码修改成功')
    
    // 跳转回登录页
    router.push({ name: 'Login' })
  } catch (error) {
    Toast.clear()
    Toast('修改失败，请重试')
  }
}
</script>

<style scoped>
.set-password-container {
  min-height: 100vh;
  background: linear-gradient(white,#9CD6FE 35%);
}

.form-container {
  padding: 20px;
}

.title {
  text-align: center;
  margin: 30px 0 20px;
  font-size: 24px;
  color: #3E47AE;
}

.confirm-button {
  width: 90%;
  margin: 20px 5%;
}

:deep(.van-cell-group) {
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 20px;
}
</style>