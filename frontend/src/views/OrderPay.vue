<template>
  <div class="page-container">
    <van-nav-bar title="支付订单" left-arrow @click-left="router.back()" />

    <div class="content">
      <div class="card text-center py-6">
        <p class="text-sm text-gray-500">订单金额</p>
        <p class="text-3xl font-bold text-orange-500">¥{{ route.params.id }}</p>
      </div>

      <div class="card">
        <van-radio-group v-model="payType">
          <van-cell title="钱包支付" icon="balance-o" clickable @click="payType = 'wallet'">
            <van-radio name="wallet" />
          </van-cell>
          <van-cell title="微信支付" icon="wechat" clickable @click="payType = 'wechat'">
            <van-radio name="wechat" />
          </van-cell>
        </van-radio-group>

        <div class="p-4">
          <van-button 
            round 
            block 
            type="success" 
            @click="pay"
            :loading="loading"
          >
            立即支付
          </van-button>
        </div>
      </div>
    </div>

    <div class="h-16"></div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Toast } from 'vant'

const route = useRoute()
const router = useRouter()
const payType = ref<'wallet' | 'wechat'>('wallet')
const loading = ref(false)

const pay = async () => {
  // TODO: 调用支付接口
  // 目前先模拟支付成功
  loading.value = true
  
  try {
    // 模拟支付过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    Toast.success('支付成功')
    router.push('/home')
  } catch (error) {
    Toast('支付失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.content {
  padding: 16px;
}

.card {
  background: white;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow: hidden;
}

.text-center {
  text-align: center;
}

.py-6 {
  padding-top: 24px;
  padding-bottom: 24px;
}

.text-sm {
  font-size: 14px;
}

.text-gray-500 {
  color: #666;
}

.text-3xl {
  font-size: 30px;
}

.font-bold {
  font-weight: bold;
}

.text-orange-500 {
  color: #ff6b00;
}

.p-4 {
  padding: 16px;
}

.h-16 {
  height: 64px;
}
</style>