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
            :class="{ active: review === '1' }"
            @click="selectRating('1')"
          >
            <van-image width="100" src="/my-good.png" />
            <span>好评</span>
          </div>
          <div 
            class="rating-option" 
            :class="{ active: review === '0' }"
            @click="selectRating('0')"
          >
            <van-image width="100" src="/my-bad.png" />
            <span>差评</span>
          </div>
        </div>
        
        <div class="message" v-if="review">
          <p>感谢您的评价</p>
          <p>帮助他人分辨</p>
        </div>
        
        <div class="actions">
          <van-button 
            type="primary" 
            size="large" 
            :disabled="!review" 
            @click="submitRating"
            :loading="loading"
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
import { evaluationAPI } from '@/api'
import { isAuthenticated } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const review = ref('')
const orderId = ref('')
const loading = ref(false)

onMounted(() => {
  if (!isAuthenticated()) {
    Toast('请先登录')
    router.push('/login')
    return
  }
  
  // 获取订单ID
  if (route.query.orderId) {
    orderId.value = route.query.orderId as string
  } else {
    Toast('订单ID不存在')
    router.back()
  }
})

const handleBack = () => {
  router.back()
}

const selectRating = (type: string) => {
  review.value = type
}

const submitRating = async () => {
  if (!review.value) {
    Toast('请选择评价')
    return
  }

  try {
    loading.value = true
    
    // 调用评价API
    const response = await evaluationAPI.evaluate(orderId.value, {
      review: review.value
    })
    
    if (response.code === 200) {
      Toast.success('评价提交成功')
      
      // 延迟跳转，让用户看到成功提示
      setTimeout(() => {
        router.push('/home')
      }, 1500)
    } else {
      Toast(response.message || '评价提交失败')
    }
  } catch (error: any) {
    console.error('评价提交失败:', error)
    Toast('评价提交失败')
  } finally {
    loading.value = false
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