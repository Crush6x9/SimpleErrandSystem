<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Toast } from 'vant'

const router = useRouter()

// 表单数据
const form = reactive({
  title: '',
  description: '',
  address: '',
  phone: '',
  price: '',
  expectTime: new Date()
})

// 格式化日期时间显示
const formattedDateTime = computed(() => {
  const date = form.expectTime
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
})

// 将Date对象转换为input的datetime-local格式
const dateToInputFormat = (date: Date) => {
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}T${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 获取当前时间的ISO字符串（用于min属性）
const getCurrentDateTimeString = () => {
  const now = new Date()
  return `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')}T${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
}

// 处理日期时间输入
const handleDateTimeInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.value) {
    form.expectTime = new Date(target.value)
  }
}

// 格式化金额，允许输入到小数点后两位，超过则四舍五入
const formatPrice = (value: string) => {
  if (!value) return ''
  
  // 移除非数字和小数点的字符
  const cleaned = value.replace(/[^\d.]/g, '')
  
  // 处理多个小数点的情况
  const parts = cleaned.split('.')
  if (parts.length > 2) {
    return parts[0] + '.' + parts.slice(1).join('')
  }
  
  // 如果包含小数点，限制小数点后最多两位
  if (parts.length === 2) {
    parts[1] = parts[1].slice(0, 2)
  }
  
  return parts.join('.')
}

// 四舍五入金额到小数点后两位
const roundPrice = (value: string) => {
  if (!value) return '0.00'
  
  const num = parseFloat(value)
  if (isNaN(num)) return '0.00'
  
  // 四舍五入到小数点后两位
  return (Math.round(num * 100) / 100).toFixed(2)
}

// 处理价格输入
const handlePriceInput = (value: string) => {
  // 先格式化输入
  const formatted = formatPrice(value)
  // 然后四舍五入
  form.price = roundPrice(formatted)
}

// 处理价格失去焦点事件，确保显示格式正确
const handlePriceBlur = () => {
  if (form.price) {
    form.price = roundPrice(form.price)
  }
}

// 提交表单
const submit = () => {
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
  
  if (!form.price || parseFloat(form.price) <= 0) {
    Toast('请输入有效的悬赏金额')
    return
  }
  
  // 确保金额四舍五入到两位小数
  const roundedPrice = roundPrice(form.price)
  if (!roundedPrice || parseFloat(roundedPrice) <= 0) {
    Toast('请输入有效的悬赏金额')
    return
  }
  
  // 显示加载状态
  Toast.loading({
    message: '发布中...',
    forbidClick: true,
    duration: 0
  })
  
   // 模拟API调用
  setTimeout(() => {
    Toast.clear()
    
     // 创建订单对象
    const newOrder = {
      id: Date.now(), // 使用时间戳作为临时ID
      title: form.title,
      description: form.description,
      address: form.address,
      phone: form.phone,
      price: parseFloat(roundedPrice),
      status: 0, // 待帮助状态
      createTime: new Date().toISOString(),
      publisherName: '当前用户', // 这里应该从登录状态获取
      expectTime: form.expectTime.toISOString(),
      hasRated: false
    }
    
    // 保存到本地存储
    const savedOrders = localStorage.getItem('orders')
    let orders = []
    
    if (savedOrders) {
      orders = JSON.parse(savedOrders)
    }
    
    orders.push(newOrder)
    localStorage.setItem('orders', JSON.stringify(orders))
    
    Toast.success('发布成功！')
    
    // 跳转到订单列表
    router.push('/help')
  }, 1500)
}
</script>

<template>
  <div class="page-container">
    <van-nav-bar title="发布跑腿" left-arrow @click-left="router.back()" safe-area-inset-top />

    <div class="content">
      <div class="card">
        <van-form @submit="submit">
          <!-- 任务标题 -->
          <van-field 
            v-model="form.title" 
            label="任务标题" 
            placeholder="例如：帮我买奶茶" 
            required 
            maxlength="20"
            show-word-limit
          />
          
          <!-- 订单内容 -->
          <van-field
            v-model="form.description"
            rows="3"
            autosize
            label="订单内容"
            type="textarea"
            placeholder="请详细描述您需要的帮助内容..."
            required
            maxlength="200"
            show-word-limit
          />
          
          <!-- 送达地址 -->
          <van-field 
            v-model="form.address" 
            label="送达地址" 
            placeholder="详细地址（如：图书馆3楼自习室）" 
            required 
          />
          
          <!-- 手机号码 -->
          <van-field
            v-model="form.phone"
            type="tel"
            label="手机号码"
            placeholder="请输入您的手机号"
            required
            maxlength="11"
          />
          
          <!-- 悬赏金额 -->
          <van-field 
            v-model="form.price" 
            label="悬赏金额" 
            type="text" 
            placeholder="0.00"
            required
            @update:model-value="handlePriceInput"
            @blur="handlePriceBlur"
          >
            <template #left-icon><span class="text-lg">¥</span></template>
          </van-field>
          
          <!-- 期望时间 - 使用原生input -->
          <div class="custom-field">
            <div class="van-field">
              <div class="van-field__label">期望时间</div>
              <div class="van-field__body">
                <input
                  :value="dateToInputFormat(form.expectTime)"
                  @input="handleDateTimeInput"
                  type="datetime-local"
                  :min="getCurrentDateTimeString()"
                  class="datetime-input"
                  required
                />
              </div>
            </div>
          </div>
          
          <!-- 底部确认区域 -->
          <div class="bottom-actions">
            <div class="price-summary">
              <span class="label">悬赏金额:</span>
              <span class="price">¥{{ roundPrice(form.price) }}</span>
            </div>
            <van-button 
              round 
              block 
              type="primary" 
              native-type="submit" 
              size="large"
              class="confirm-button"
            >
              确认发布
            </van-button>
          </div>
        </van-form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px; /* 为底部按钮留出空间 */
}

.content {
  padding: 16px;
}

.card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 100px; /* 为底部确认区域留出空间 */
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

/* 自定义日期时间输入样式 */
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

/* 底部确认区域样式 - 放在表单内部 */
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

/* 小屏幕设备自适应 */
@media (max-width: 393px) {
  .bottom-actions {
    max-width: 100vw;
  }
  
  .datetime-input {
    font-size: 16px; /* 防止iOS缩放 */
  }
}
</style>