<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { isAuthenticated, logout } from '@/utils/auth'
import { Dialog, Toast } from 'vant'

const router = useRouter()
const isAuthenticatedRef = ref(false)
// 处理返回
const handleBack = () => {
  router.back()
}
// 检查登录状态
onMounted(() => {
  isAuthenticatedRef.value = isAuthenticated()
})

// 处理登录/注册按钮点击
const handleAuthClick = () => {
  router.push({ name: 'Login',query: { redirect: '/home' }  })
}

// 处理退出点击
const handleLogout = () => {
  Dialog.confirm({
    title: '退出登录',
    message: '确定要退出登录吗？',
  }).then(() => {
    logout()
    isAuthenticatedRef.value = false
    Toast('已退出登录')
  }).catch(() => {
    // 用户点击取消
  })
}
</script>

<template>
  <div class="home">
        <van-nav-bar 
      title="首页" 
      left-text="返回" 
      left-arrow 
      @click-left="handleBack" 
    />
    <div>
      <van-image width="100%" fit="cover" src="/home-img.png" />
    </div>
    <van-card class="home_card" desc="跑了个腿 Run A Leg" title="欢迎加入">
      <template #footer>
        <!-- 根据登录状态显示不同按钮 -->
        <van-button 
          v-if="!isAuthenticatedRef" 
          size="normal" 
          @click="handleAuthClick"
        >
          注册/登录
        </van-button>
        <van-button 
          v-else 
          size="normal" 
          type="danger" 
          @click="handleLogout"
        >
          退出
        </van-button>
      </template>
    </van-card>
    <van-grid clickable :column-num="2">
      <van-grid-item to="/help" class="home_grid-item_help">
      <div class="home_grid-item_text"><h1>我要帮忙</h1><br>
        <h3>give a hand</h3></div>
        
        <van-image width="100%" fit="cover" src="/give-a-hand.png" />
      </van-grid-item>
      <van-grid-item to="/help" class="home_grid-item_ask">
      <div class="home_grid-item_text"><h1>我要求助</h1><br>
        <h3>ask for help</h3></div>
        
        <van-image width="100%" fit="cover" src="/ask-for-help.png" />
      </van-grid-item>
    </van-grid>
  </div>
</template>

<style scoped>
.home_card {
  margin: 0;
}

.home_card :deep(.van-card__title) {
  font-size: 20px;
  line-height: normal;
  max-height: fit-content;
  margin: 15px;
}

.home_card :deep(.van-card__desc) {
  color: black;
  font-size: 24px;
  line-height: normal;
  max-height: fit-content;
  margin: 15px;
}
.home_grid-item_help :deep(.van-grid-item__content){
  background-color: #F9C12D;
}
.home_grid-item_ask :deep(.van-grid-item__content){
  background-color: #85D1E7;
}
.home_grid-item_text{
  text-align: right;
  color: white;
  
}
</style>