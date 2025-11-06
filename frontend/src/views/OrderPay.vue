<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const payType = ref<'wallet' | 'wechat'>('wallet')

const pay = () => {
  // TODO: 调用支付接口
  router.push('/order')
}
</script>

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
          <van-button round block type="success" @click="pay">立即支付</van-button>
        </div>
      </div>
    </div>

    <div class="h-16"></div>
  </div>
</template>