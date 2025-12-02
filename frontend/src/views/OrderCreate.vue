<template>
  <div class="page-container">
    <van-nav-bar title="发布跑腿" left-arrow @click-left="router.back()" safe-area-inset-top />

    <div class="content">
      <div class="card">
        <van-form @submit="submit">
          <van-field v-model="form.title" label="任务标题" placeholder="例如：帮我买奶茶" required maxlength="20" show-word-limit />

          <van-field v-model="form.description" rows="3" autosize label="订单内容" type="textarea"
            placeholder="请详细描述您需要的帮助内容..." required maxlength="200" show-word-limit />

          <van-field v-model="form.address" label="送达地址" placeholder="详细地址（如：图书馆3楼自习室）" required />

          <van-field v-model="form.phone" type="tel" label="手机号码" placeholder="请输入您的手机号" required maxlength="11" />

          <van-field v-model="form.reward" label="悬赏金额" type="text" placeholder="0.00" required
            @update:model-value="handlePriceInput" @blur="handlePriceBlur">
            <template #left-icon><span class="text-lg">¥</span></template>
          </van-field>

          <div class="custom-field">
            <div class="van-field">
              <div class="van-field__label">期望时间</div>
              <div class="van-field__body">
                <input :value="dateToInputFormat(form.helpTime)" @input="handleDateTimeInput" type="datetime-local"
                  :min="getCurrentDateTimeString()" class="datetime-input" required />
              </div>
            </div>
          </div>

          <div class="bottom-actions">
            <div class="price-summary">
              <span class="label">悬赏金额:</span>
              <span class="price">¥{{ roundPrice(form.reward) }}</span>
            </div>
            <van-button round block type="primary" native-type="submit" size="large" class="confirm-button">
              确认发布
            </van-button>
          </div>
        </van-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Toast } from 'vant'
import { orderAPI } from '@/api'
import { isAuthenticated } from '@/utils/auth'

const router = useRouter()

const form = reactive({
  title: '',
  description: '',
  address: '',
  helpTime: new Date(),
  phone: '',
  reward: ''
})

onMounted(() => {
  if (!isAuthenticated()) {
    Toast('请先登录')
    router.push('/login')
    return
  }
})

const dateToInputFormat = (date: Date) => {
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}T${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const getCurrentDateTimeString = () => {
  const now = new Date()
  return `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')}T${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
}

const handleDateTimeInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.value) {
    form.helpTime = new Date(target.value)
  }
}

// 格式化金额
const formatPrice = (value: string) => {
  if (!value) return ''

  const cleaned = value.replace(/[^\d.]/g, '')

  const parts = cleaned.split('.')
  if (parts.length > 2) {
    return parts[0] + '.' + parts.slice(1).join('')
  }

  if (parts.length === 2) {
    parts[1] = parts[1].slice(0, 2)
  }

  return parts.join('.')
}

const roundPrice = (value: string) => {
  if (!value) return '0.00'
  const num = parseFloat(value)
  if (isNaN(num)) return '0.00'
  return (Math.round(num * 100) / 100).toFixed(2)
}

// 处理价格输入
const handlePriceInput = (value: string) => {
  // 先格式化输入
  const formatted = formatPrice(value)
  // 然后四舍五入
  form.reward = roundPrice(formatted)
}

// 处理价格失去焦点事件
const handlePriceBlur = () => {
  if (form.reward) {
    form.reward = roundPrice(form.reward)
  }
}

const formatToLocalDateTime = (date: Date): string => {
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2, '0')

  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`
}

const submit = async () => {
  // 表单验证
  if (!form.title.trim()) {
    Toast('请输入任务标题')
    return
  }

  if (!form.description.trim()) {
    Toast('请输入订单内容')
    return
  }

  if (!form.address.trim()) {
    Toast('请输入送达地址')
    return
  }

  if (!form.phone.trim()) {
    Toast('请输入手机号码')
    return
  }

  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    Toast('请输入有效的手机号码')
    return
  }

  if (!form.reward || parseFloat(form.reward) <= 0) {
    Toast('请输入有效的悬赏金额')
    return
  }

  const roundedPrice = roundPrice(form.reward)
  const rewardNumber = parseFloat(roundedPrice)

  if (!rewardNumber || rewardNumber <= 0) {
    Toast('请输入有效的悬赏金额')
    return
  }

  if (form.helpTime <= new Date()) {
    Toast('帮助时间必须晚于当前时间')
    return
  }

  const toast = Toast.loading({
    message: '发布中...',
    forbidClick: true,
    duration: 0
  })

  try {
    const response = await orderAPI.publish({
      title: form.title.trim(),
      address: form.address.trim(),
      description: form.description.trim(),
      helpTime: formatToLocalDateTime(form.helpTime),
      phone: form.phone.trim(),
      reward: rewardNumber
    })

    Toast.clear()

    if (response.code === 200) {
      Toast.success('发布成功！')
      router.push('/home')
    } else {
      Toast(response.message || '发布失败，请重试')
    }
  } catch (error: any) {
    Toast.clear()
    if (error.response && error.response.data) {
      Toast(error.response.data.message || '发布失败，请重试')
    } else if (error.message) {
      Toast(error.message)
    } else {
      Toast('网络错误，请稍后重试')
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.content {
  padding: 16px;
}

.card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 100px;
}

:deep(.van-field__label) {
  font-weight: 500;
  color: #333;
}

:deep(.van-field__control) {
  color: #333;
}

:deep(.van-field--error .van-field__control) {
  color: #ee0a24;
}

.text-lg {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.custom-field {
  padding: 10px 16px;
  position: relative;
}

.custom-field .van-field {
  padding: 0;
}

.custom-field .van-field__label {
  width: 90px;
  flex: none;
  font-size: 14px;
  line-height: 24px;
  color: #333;
  font-weight: 500;
}

.custom-field .van-field__body {
  display: flex;
  align-items: center;
  min-height: 24px;
}

.datetime-input {
  flex: 1;
  margin: 0;
  padding: 0;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  line-height: 24px;
  color: #333;
  width: 100%;
  font-family: inherit;
}

.datetime-input:focus {
  outline: none;
}

.bottom-actions {
  position: fixed;
  bottom: 50px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 393px;
  background: white;
  padding: 12px 16px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  z-index: 100;
}

.price-summary {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.price-summary .label {
  font-size: 14px;
  color: #666;
}

.price-summary .price {
  font-size: 20px;
  font-weight: bold;
  color: #ee0a24;
}

.confirm-button {
  width: 60%;
  margin-left: 16px;
}

@media (max-width: 393px) {
  .bottom-actions {
    max-width: 100vw;
  }

  .datetime-input {
    font-size: 16px;
  }
}
</style>