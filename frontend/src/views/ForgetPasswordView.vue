<template>
  <div class="forget-container">
    <van-nav-bar title="忘记密码" left-text="返回" left-arrow @click-left="handleBack" />

    <div class="form-container">
      <h1 class="title">忘记密码</h1>

      <van-cell-group inset>
        <van-field v-model="phone" type="tel" label="手机号" placeholder="请输入注册手机号"
          :rules="[{ required: true, message: '请填写手机号' }]" />
      </van-cell-group>

      <div class="verify-button">
        <van-button type="primary" size="large" :disabled="!/^1[3-9]\d{9}$/.test(phone) || countdown > 0"
          @click="sendVerificationCode">
          {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
        </van-button>
      </div>
    </div>

    <van-dialog v-model:show="showVerificationDialogVisible" title="输入验证码" :show-confirm-button="false"
      :close-on-click-overlay="false" class="verification-dialog">
      <div class="verification-content">
        <p class="verification-desc">已向手机号 {{ phone }} 发送验证码</p>

        <div class="code-input-container">
          <input v-for="(code, index) in verificationCodeArray" :key="index" type="tel" maxlength="1" class="code-input"
            @input="handleCodeInput(index, $event)" @keydown="handleCodeKeyDown(index, $event)" ref="codeInputs">
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
    <div class="background">
      <van-image src="/password-bg.png" width="100%" fit="cover" alt="背景" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Toast } from 'vant';
import { authAPI } from '@/api';

const handleBack = () => {
  router.back();
}

const router = useRouter();
const phone = ref('');
const countdown = ref(0);

const showVerificationDialogVisible = ref(false);
const verificationCodeArray = ref(['', '', '', '', '', '']);
const codeInputs = ref<HTMLInputElement[]>([]);
const resendCountdown = ref(60);
const resendEnabled = ref(false);
let countdownTimer: number | null = null;
let resendTimer: number | null = null;

const sendVerificationCode = async () => {
  if (countdown.value > 0) return;

  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    Toast('请输入有效的手机号');
    return;
  }

  const toast = Toast.loading({
    message: '检查手机号中...',
    forbidClick: true,
    duration: 0
  });

  try {
    const checkResponse = await authAPI.checkPhone({ phone: phone.value });

    if (checkResponse.code !== 200) {
      Toast.clear();
      Toast(checkResponse.message || '手机号未注册');
      return;
    }

    Toast.loading({
      message: '发送验证码中...',
      forbidClick: true,
      duration: 0
    });

    const response = await authAPI.sendCode({
      phone: phone.value
    });

    Toast.clear();

    if (response.code !== 200) {
      Toast(response.message || '发送验证码失败');
      return;
    }

    Toast('验证码已发送');

    verificationCodeArray.value = ['', '', '', '', '', ''];
    showVerificationDialogVisible.value = true;
    startResendCountdown();

    setTimeout(() => {
      if (codeInputs.value[0]) {
        codeInputs.value[0].focus();
      }
    }, 300);

    countdown.value = 60;
    const timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);
  } catch (error: any) {
    Toast.clear();
    if (error.response && error.response.data) {
      Toast(error.response.data.message || '发送验证码失败');
    } else if (error.message) {
      Toast(error.message);
    } else {
      Toast('网络错误，请稍后重试');
    }
  }
}

const handleCodeInput = (index: number, event: Event) => {
  const input = event.target as HTMLInputElement;
  const value = input.value;

  if (/^\d$/.test(value)) {

    verificationCodeArray.value[index] = value;

    if (index < 5) {
      const nextInput = codeInputs.value[index + 1];
      if (nextInput) {
        nextInput.focus();
      }
    }
  } else {

    input.value = '';
  }
}
const handleCodeKeyDown = (index: number, event: KeyboardEvent) => {
  if (event.key === 'Backspace' && !verificationCodeArray.value[index] && index > 0) {
    const prevInput = codeInputs.value[index - 1];
    if (prevInput) {
      prevInput.focus();
    }
  }
}

const verifyCode = async () => {
  const verificationCode = verificationCodeArray.value.join('');

  if (verificationCode.length !== 6) {
    Toast('请输入6位验证码');
    return;
  }

  try {
    const response = await authAPI.verifyCode({
      phone: phone.value,
      code: verificationCode
    });

    if (response.code !== 200) {
      Toast(response.message || '验证码错误');
      return;
    }

    showVerificationDialogVisible.value = false;
    Toast('验证成功');

    router.push({
      name: 'SetNewPassword',
      query: {
        phone: phone.value,
        verifyCode: verificationCode
      }
    });
  } catch (error: any) {
    if (error.response && error.response.data) {
      Toast(error.response.data.message || '验证码错误');
    } else if (error.message) {
      Toast(error.message);
    } else {
      Toast('网络错误，请稍后重试');
    }
  }
}


const startResendCountdown = () => {
  resendCountdown.value = 60;
  resendEnabled.value = false;

  if (resendTimer) {
    window.clearInterval(resendTimer);
    resendTimer = null;
  }

  resendTimer = window.setInterval(() => {
    resendCountdown.value--;
    if (resendCountdown.value <= 0) {
      resendEnabled.value = true;
      if (resendTimer) {
        window.clearInterval(resendTimer);
        resendTimer = null;
      }
    }
  }, 1000);
}

const resendVerificationCode = async () => {
  if (!resendEnabled.value) return;

  try {
    Toast.loading({
      message: '发送中...',
      forbidClick: true,
      duration: 0
    });

    const response = await authAPI.sendCode({
      phone: phone.value
    });

    Toast.clear();

    if (response.code !== 200) {
      Toast(response.message || '发送验证码失败');
      return;
    }

    Toast('验证码已重新发送');

    verificationCodeArray.value = ['', '', '', '', '', ''];

    if (codeInputs.value[0]) {
      codeInputs.value[0].focus();
    }

    startResendCountdown();
  } catch (error: any) {
    Toast.clear();
    if (error.response && error.response.data) {
      Toast(error.response.data.message || '发送验证码失败');
    } else if (error.message) {
      Toast(error.message);
    } else {
      Toast('网络错误，请稍后重试');
    }
  }
}

onMounted(() => {
  window.addEventListener('beforeunload', () => {
    if (countdownTimer) {
      window.clearInterval(countdownTimer);
      countdownTimer = null;
    }
    if (resendTimer) {
      window.clearInterval(resendTimer);
      resendTimer = null;
    }
  });
})
</script>

<style scoped>
.forget-container {
  min-height: 100vh;
  background: linear-gradient(white, #9CD6FE 35%);
}

.form-container {
  padding: 20px;
  margin: 30px 0 50px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #3E47AE;
}

:deep(.van-cell-group) {
  border-radius: 10px;
  overflow: hidden;
}

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
  width: 90%;
  margin: 20px 5%;
}
</style>