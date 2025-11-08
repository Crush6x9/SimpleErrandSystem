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
          <van-icon name="/my-good.png" size="100" />
          <span>好评</span>
        </div>
        <div 
          class="rating-option" 
          :class="{ active: rating === 'bad' }"
          @click="selectRating('bad')"
        >
          <van-icon name="/my-bad.png" size="100" />
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
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Toast } from 'vant'

const router = useRouter()
const route = useRoute()
const rating = ref('')

const selectRating = (type: string) => {
  rating.value = type
}

const submitRating = () => {
  if (!rating.value) {
    Toast('请选择评价')
    return
  }
  
  // 这里可以调用API提交评价
  Toast.success('评价提交成功')
  
  // 延迟跳转，让用户看到成功提示
  setTimeout(() => {
    router.push('/home')
  }, 1500)
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
  /* color: #fff; */
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
  /* margin: 25px 0; */
  padding: 15px 15px 0 15px;
  text-align: center;
  /* background: #f8f9fa; */
  /* border-radius: 10px; */
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