<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const balance = ref(88.00)
const records = ref([
  { id: 1, desc: '任务收入', amount: 10, time: '2025-04-05 10:30' },
  { id: 2, desc: '任务支出', amount: -8, time: '2025-04-04 18:20' }
])

const toRecharge = () => router.push('/wallet/recharge')
const toWithdraw = () => router.push('/wallet/withdraw')
</script>

<template>
  <div class="page-container">
    <van-nav-bar title="我的钱包" left-arrow @click-left="router.back()" />

    <div class="content">
      <div class="bg-gradient-to-b from-blue-500 to-blue-600 text-white rounded-xl p-6 mb-4">
        <p class="text-sm opacity-90">钱包余额</p>
        <p class="text-3xl font-bold">¥{{ balance.toFixed(2) }}</p>
      </div>

      <div class="grid grid-cols-2 gap-3 mb-6">
        <van-button round color="#07c160" @click="toRecharge">充值</van-button>
        <van-button round plain @click="toWithdraw">提现</van-button>
      </div>

      <van-list>
        <van-cell
          v-for="item in records"
          :key="item.id"
          :title="item.desc"
          :value="item.amount > 0 ? `+ ¥${item.amount}` : `- ¥${Math.abs(item.amount)}`"
          :label="item.time"
          :value-class="item.amount > 0 ? 'text-green-600' : 'text-red-600'"
        />
      </van-list>
    </div>

    <div class="h-16"></div>
  </div>
</template>