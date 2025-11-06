<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import type { Order } from '@/types/order'  // 待创建

const router = useRouter()
const active = ref(0)
const orders = ref<Order[]>([])

const statusMap = {
  0: { text: '待支付', type: 'warning' as const },
  1: { text: '进行中', type: 'primary' as const },
  2: { text: '待评价', type: 'info' as const },
  3: { text: '已完成', type: 'success' as const }
}

const toPay = (id: number) => router.push(`/order/pay/${id}`)
const toCreate = () => router.push('/order/create')
</script>

<template>
  <div class="page-container">
    <van-nav-bar title="我的订单" left-arrow @click-left="router.back()" safe-area-inset-top />

    <div class="content">
      <van-tabs v-model:active="active">
        <van-tab title="我发布的">
          <div v-for="o in orders" :key="o.id" class="card" @click="router.push(`/order/${o.id}`)">
            <div class="flex justify-between items-center">
              <div class="flex-1">
                <p class="font-medium text-base">{{ o.title }}</p>
                <p class="text-sm text-gray-500 mt-1">{{ o.address }}</p>
              </div>
              <div class="text-right ml-3">
                <p class="text-lg font-bold text-orange-500">¥{{ o.price }}</p>
                <van-tag :type="statusMap[o.status].type">
                  {{ statusMap[o.status].text }}
                </van-tag>
              </div>
            </div>
            <div v-if="o.status === 0" class="mt-3 text-right">
              <van-button size="small" type="primary" round @click.stop="toPay(o.id)">去支付</van-button>
            </div>
          </div>
        </van-tab>
        <van-tab title="我接手的"> <!-- 同结构 --> </van-tab>
      </van-tabs>

      <van-button type="primary" round block class="fixed-bottom-btn" @click="toCreate">
        发布跑腿
      </van-button>
    </div>

    <div class="h-16"></div>
  </div>
</template>

<style scoped>
.card {
  @apply bg-white rounded-xl shadow-sm p-4 mb-3;
}
.fixed-bottom-btn {
  position: fixed;
  bottom: 74px;
  left: 50%;
  transform: translateX(-50%);
  width: 351px;
  z-index: 998;
}
</style>