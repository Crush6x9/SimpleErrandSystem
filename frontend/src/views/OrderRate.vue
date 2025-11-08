<template>
  <div class="order-rate">
    <van-nav-bar title="评价" left-text="返回" left-arrow @click-left="handleBack" />
    <div class="order-complete-container">
      <div class="content">
        <h1 class="title">发布者评价</h1>
        <p class="subtitle">恭喜</p>
        <p class="subtitle">订单已被完成</p>
        <p class="prompt">请点击下方图标进行评价</p>
        
        <div class="rating-options">
          <div 
            class="rating-option" 
            :class="{ active: rating === 'good' }"
            @click="selectRating('good')"
          >
            <van-image width="100" src="/my-good.png" />
            <span>好评</span>
          </div>
          <div 
            class="rating-option" 
            :class="{ active: rating === 'bad' }"
            @click="selectRating('bad')"
          >
            <van-image width="100" src="/my-bad.png" />
            <span>差评</span>
          </div>
        </div>
        
        <div class="message" v-if="rating">
          <p>感谢您的评价</p>
          <p>帮助他人分辨</p>
        </div>
        
        <div class="actions">
          <van-button 
            type="primary" 
            size="large" 
            :disabled="!rating" 
            @click="submitRating"
          >
            提交评价
          </van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Toast } from 'vant'

const router = useRouter()
const route = useRoute()
const rating = ref('')
const orderId = ref(0)

onMounted(() => {
  // 获取订单ID
  if (route.query.orderId) {
    orderId.value = parseInt(route.query.orderId as string)
  }
})

const handleBack = () => {
  router.back()
}

const selectRating = (type: string) => {
  rating.value = type
}

const submitRating = () => {
  if (!rating.value) {
    Toast('请选择评价')
    return
  }
  
  // 更新订单评价状态
  updateOrderRating()
  
  Toast.success('评价提交成功')
  
  // 延迟跳转，让用户看到成功提示
  setTimeout(() => {
    router.push('/home')
  }, 1500)
}

// 更新订单评价状态
const updateOrderRating = () => {
  const savedOrders = localStorage.getItem('orders')
  if (savedOrders && orderId.value) {
    const orders = JSON.parse(savedOrders)
    const orderIndex = orders.findIndex((o: any) => o.id === orderId.value)
    if (orderIndex !== -1) {
      orders[orderIndex] = {
        ...orders[orderIndex],
        hasRated: true,
        rating: rating.value // 保存评价类型
      }
      localStorage.setItem('orders', JSON.stringify(orders))
      
      // 更新用户评价数据
      updateUserRating(rating.value)
    }
  }
}

// 更新用户评价数据
const updateUserRating = (ratingType: string) => {
  // 获取当前用户数据
  const userData = localStorage.getItem('userData')
  if (userData) {
    const user = JSON.parse(userData)
    
    // 更新评价数据
    if (ratingType === 'good') {
      user.好评 = (user.好评 || 0) + 1
    } else {
      user.差评 = (user.差评 || 0) + 1
    }
    
    // 保存更新后的用户数据
    localStorage.setItem('userData', JSON.stringify(user))
  }
}
</script>

<style scoped>
.order-complete-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 90vh;
  background: linear-gradient(180deg, #F2BE45 40%, #fff 20%);
  padding: 20px;
}

.content {
  background: #F2BE45;
  padding: 40px 30px;
  box-shadow: 0 0px 10px #FF9900;
  width: 100%;
  max-width: 350px;
  color: #fff;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
}

.subtitle {
  font-size: 18px;
  margin: 5px 0;
}

.prompt {
  font-size: 16px;
  margin: 25px 0;
}

.rating-options {
  display: flex;
  justify-content: space-around;
  margin: 30px 0 10px;
}

.rating-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  border-radius: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.rating-option span {
  margin-top: 10px;
  font-size: 16px;
}

.rating-option.active {
  background: #f0f8ff;
  transform: scale(1.05);
}

.rating-option.active span {
  color: #FF9900;
  font-weight: bold;
}

.message {
  padding: 15px 15px 0 15px;
  text-align: center;
  animation: fadeIn 0.5s;
}

.message p {
  margin: 8px 0;
  font-size: 28px;
}

.actions {
  margin-top: 20px;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>