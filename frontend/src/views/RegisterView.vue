<template>
  <div class="register-container">
    <van-nav-bar 
      title="注册" 
      left-text="返回" 
      left-arrow 
      @click-left="handleBack" 
    />
    <div class="register-child-container">
    <div class="background">
      <van-image src="/register-bg.png" width="100%" fit="cover" alt="背景" />
    </div>

    <div class="form-container">
      <div class="form">
        <h1 class="title">注册账号</h1>
        <p class="section-title">请输入手机号用于注册</p>

        <van-cell-group inset>
          <van-field v-model="phone" type="tel" label="手机号" placeholder="请输入手机号"
            :rules="[{ required: true, message: '请填写手机号' }]" />
        </van-cell-group>

        <h1 class="title">设置密码</h1>
        <p class="section-title">设置登录密码用于登录</p>
        <van-cell-group inset>
          <van-field v-model="password" :type="isPasswordVisible ? 'text' : 'password'" label="密码" placeholder="请输入密码"
            :right-icon="isPasswordVisible ? 'eye-o' : 'closed-eye'" @click-right-icon="togglePasswordVisibility" />
        </van-cell-group>

        <div class="agreement">
          <van-checkbox v-model="agreed" shape="square">
            我已阅读并同意
            <span class="agreement-link" @click.stop="showAgreement">《用户协议》</span>
            和
            <span class="agreement-link" @click.stop="showPrivacy">《隐私政策》</span>
          </van-checkbox>
        </div>

        <div class="register-button">
          <van-button type="primary" size="large" :disabled="!canRegister" @click="handleRegister">
            同意协议并注册
          </van-button>
        </div>
      </div>
    </div>
    
    <van-dialog
      v-model:show="showVerificationDialogVisible"
      title="输入验证码"
      :show-confirm-button="false"
      :close-on-click-overlay="false"
      class="verification-dialog"
    >
      <div class="verification-content">
        <p class="verification-desc">已向手机号 {{ phone }} 发送验证码</p>
        
        <div class="code-input-container">
          <input 
            v-for="(code, index) in verificationCodeArray" 
            :key="index"
            type="tel"
            maxlength="1"
            class="code-input"
            @input="handleCodeInput(index, $event)"
            @keydown="handleCodeKeyDown(index, $event)"
            ref="codeInputs"
          >
        </div>
        
        <p class="resend-timer" v-if="resendEnabled">
          {{ resendCountdown > 0 ? `重新发送(${resendCountdown}s)` : '未收到验证码？' }}
          <span v-if="!resendCountdown" class="resend-link" @click="resendVerificationCode">重新发送</span>
        </p>
        
        <van-button type="primary" size="large" @click="verifyCode" class="verify-button">
          验证
        </van-button>
      </div>
    </van-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Toast, Dialog } from 'vant'
import { authAPI } from '@/api'

// 处理返回
const handleBack = () => {
  router.back()
}

const router = useRouter()
const phone = ref('')
const password = ref('')
const agreed = ref(false)
const isPasswordVisible = ref(false)

// 验证码相关
const showVerificationDialogVisible = ref(false)
const verificationCodeArray = ref(['', '', '', '', '', ''])
const codeInputs = ref<HTMLInputElement[]>([])
const resendCountdown = ref(60)
const resendEnabled = ref(false)
let countdownTimer: number | null = null

// 切换密码可见性
const togglePasswordVisibility = () => {
  isPasswordVisible.value = !isPasswordVisible.value
}

// 显示协议
const showAgreement = () => {
  Dialog.confirm({
    title: '用户协议',
    message: '这里是用户协议的详细内容...',
    theme: 'round-button',
    confirmButtonText: '同意',
    cancelButtonText: '拒绝'
  }).then(() => {
    agreed.value = true
  }).catch(() => {
    agreed.value = false
  })
}

const showPrivacy = () => {
  Dialog.confirm({
    title: '隐私政策',
    message: '这里是隐私政策的详细内容...',
    theme: 'round-button',
    confirmButtonText: '同意',
    cancelButtonText: '拒绝'
  }).then(() => {
    agreed.value = true
  }).catch(() => {
    agreed.value = false
  })
}

// 计算是否可以注册
const canRegister = computed(() => {
  return /^1[3-9]\d{9}$/.test(phone.value) &&
    password.value.length >= 6 &&
    agreed.value
})

// 注册处理 - 发送验证码
const handleRegister = async () => {
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    Toast('请输入有效的手机号')
    return
  }

  if (password.value.length < 6) {
    Toast('密码至少6位')
    return
  }

  if (!agreed.value) {
    Toast('请阅读并同意用户协议')
    return
  }

  const toast = Toast.loading({
    message: '发送验证码中...',
    forbidClick: true,
    duration: 0
  })

  try {
    // 调用发送验证码API
    const response = await authAPI.sendCode({
      phone: phone.value,
      type: 'register'
    })

    Toast.clear()
    
    // 检查响应状态
    if (response.code !== 200) {
      Toast(response.message || '发送验证码失败')
      return
    }
    
    Toast('验证码已发送')
    
    verificationCodeArray.value = ['', '', '', '', '', '']
    showVerificationDialogVisible.value = true
    startResendCountdown()
    
    setTimeout(() => {
      if (codeInputs.value[0]) {
        codeInputs.value[0].focus()
      }
    }, 300)
    
  } catch (error: any) {
    Toast.clear()
    // 显示具体的错误信息
    if (error.response && error.response.data) {
      Toast(error.response.data.message || '发送验证码失败')
    } else if (error.message) {
      Toast(error.message)
    } else {
      Toast('网络错误，请稍后重试')
    }
  }
}

// 处理验证码输入
const handleCodeInput = (index: number, event: Event) => {
  const input = event.target as HTMLInputElement
  const value = input.value
  
  if (/^\d$/.test(value)) {
    // 更新当前输入框
    verificationCodeArray.value[index] = value
    
    // 自动跳转到下一个输入框
    if (index < 5) {
      const nextInput = codeInputs.value[index + 1]
      if (nextInput) {
        nextInput.focus()
      }
    }
  } else {
    // 清空无效输入
    input.value = ''
  }
}

// 处理键盘事件
const handleCodeKeyDown = (index: number, event: KeyboardEvent) => {
  if (event.key === 'Backspace' && !verificationCodeArray.value[index] && index > 0) {
    const prevInput = codeInputs.value[index - 1]
    if (prevInput) {
      prevInput.focus()
    }
  }
}

// 验证验证码并注册
const verifyCode = async () => {
  const verificationCode = verificationCodeArray.value.join('')
  
  if (verificationCode.length !== 6) {
    Toast('请输入6位验证码')
    return
  }
  
  try {
    // 验证验证码
    const verifyResponse = await authAPI.verifyCode({
      phone: phone.value,
      code: verificationCode,
      type: 'register'
    })
    
    if (verifyResponse.code !== 200) {
      Toast(verifyResponse.message || '验证码错误')
      return
    }
    
    // 验证码正确，调用注册API
    const registerResponse = await authAPI.register({
      phone: phone.value,
      password: password.value,
      verifyCode: verificationCode
    })
    
    if (registerResponse.code !== 200) {
      Toast(registerResponse.message || '注册失败')
      return
    }
    
    showVerificationDialogVisible.value = false
    Toast('注册成功')
    
    // 跳转到登录页面
    router.push({ name: 'Login' })
  } catch (error: any) {
    // 显示具体的错误信息
    if (error.response && error.response.data) {
      Toast(error.response.data.message || '注册失败')
    } else if (error.message) {
      Toast(error.message)
    } else {
      Toast('网络错误，请稍后重试')
    }
  }
}

// 启动重新发送倒计时
const startResendCountdown = () => {
  // 重置倒计时
  resendCountdown.value = 60
  resendEnabled.value = false
  
  // 清除现有计时器
  if (countdownTimer) {
    window.clearInterval(countdownTimer)
    countdownTimer = null
  }
  
  // 启动新计时器
  countdownTimer = window.setInterval(() => {
    resendCountdown.value--
    if (resendCountdown.value <= 0) {
      resendEnabled.value = true
      if (countdownTimer) {
        window.clearInterval(countdownTimer)
        countdownTimer = null
      }
    }
  }, 1000)
}

// 重新发送验证码
const resendVerificationCode = async () => {
  if (!resendEnabled.value) return
  
  try {
    Toast.loading({
      message: '发送中...',
      forbidClick: true,
      duration: 0
    })
    
    // 调用发送验证码API
    const response = await authAPI.sendCode({
      phone: phone.value,
      type: 'register'
    })
    
    Toast.clear()
    
    if (response.code !== 200) {
      Toast(response.message || '发送验证码失败')
      return
    }
    
    Toast('验证码已重新发送')
    
    // 重置验证码
    verificationCodeArray.value = ['', '', '', '', '', '']
    
    // 如果有焦点，聚焦第一个输入框
    if (codeInputs.value[0]) {
      codeInputs.value[0].focus()
    }
    
    // 重新启动倒计时
    startResendCountdown()
  } catch (error: any) {
    Toast.clear()
    // 显示具体的错误信息
    if (error.response && error.response.data) {
      Toast(error.response.data.message || '发送验证码失败')
    } else if (error.message) {
      Toast(error.message)
    } else {
      Toast('网络错误，请稍后重试')
    }
  }
}

// 组件卸载时清除计时器
onMounted(() => {
  window.addEventListener('beforeunload', () => {
    if (countdownTimer) {
      window.clearInterval(countdownTimer)
      countdownTimer = null
    }
  })
})
</script>

<style scoped>

.register-child-container{
  overflow: hidden;
  position: relative;
  height: 90vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.background {
    position: absolute;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.form-container {
  border-radius: 20px;
  position: absolute;
  z-index: 3;
  background-color: rgba(255, 255, 255, 0.963);
}

.form {
  width: 85%;
  margin: 20px auto;
}

.title {
  margin: 30px 0 10px;
  font-size: 24px;
  color: #333;
}

.section-title {
  margin: 0 0 20px;
  font-size: 14px;
  color: #7f7f7f;
}

.agreement {
  font-size: 12px;
}

.agreement-link {
  color: #1989fa;
  text-decoration: underline;
}

.register-button {
  padding: 30px 0 20px;
}

:deep(.van-cell-group) {
  width: 100%;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 20px;
  margin-left: 0;
}

/* 验证码对话框样式 */
.verification-dialog {
  :deep(.van-dialog) {
    border-radius: 16px;
    overflow: hidden;
  }
  
  :deep(.van-dialog__header) {
    padding: 16px 16px 8px;
    font-size: 18px;
    font-weight: 600;
  }
  
  :deep(.van-dialog__content) {
    padding: 0 16px 16px;
  }
}

.verification-content {
  padding: 10px 0;
}

.verification-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
  text-align: center;
}

.code-input-container {
  display: flex;
  justify-content: space-between;
  margin: 0 auto 20px;
  width: 80%;
}

.code-input {
  width: 15%;
  height: 40px;
  text-align: center;
  font-size: 18px;
  border: 1px solid #eee;
  border-radius: 5px;
  outline: none;
  transition: border-color 0.3s;
}

.code-input:focus {
  border-color: #1989fa;
  box-shadow: 0 0 0 2px rgba(25, 137, 250, 0.2);
}

.resend-timer {
  font-size: 13px;
  color: #999;
  text-align: center;
  margin-bottom: 20px;
}

.resend-link {
  color: #1989fa;
  cursor: pointer;
}

.verify-button {
  width: 80%;
  margin: 10px 10% 20px;
}
</style>