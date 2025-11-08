<!-- src/views/OrderDetail.vue -->
<template>
  <div class="detail">
    <TheHeader />
    <div class="content">
      <div class="card">
        <h2>{{ order.title }}</h2>
        <van-cell title="发布人" :value="order.publisherName" />
        <van-cell title="价格" value-class="price">¥{{ order.price }}</van-cell>
        <van-cell title="地址" :value="order.address" icon="location-o" />
        <van-cell title="状态" :value="statusText(order.status)" />
        <van-cell title="发布时间" :value="formatTime(order.createTime)" />
        
        <div class="actions">
          <van-button v-if="order.status === 0" type="primary" block @click="acceptOrder">
            我要接单
          </van-button>
          <van-button v-else-if="order.status === 2" type="success" block @click="toRate">
            去评价
          </van-button>
        </div>
      </div>
    </div>
    <TheFooter />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const orderId = route.params.id

// 模拟数据
const order = ref({
  id: Number(orderId),
  title: '帮买奶茶',
  publisherName: '张三',
  price: 15,
  address: '3栋宿舍',
  status: 0,
  createTime: '2025-04-05T10:00:00'
})

const statusText = (s: number) => ['待接单', '进行中', '待评价', '已完成'][s]
const formatTime = (t: string) => t.slice(0, 16).replace('T', ' ')

const acceptOrder = () => {
  order.value.status = 1
  alert('接单成功！')
}

const toRate = () => {
  // 跳转评价
}
</script>

<style scoped>
.detail { background: #f7f8fa; min-height: 100vh; }
.content { padding: 16px; padding-top: 60px; padding-bottom: 60px; }
.card { background: white; border-radius: 12px; padding: 16px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); }
.price { color: #ff5000; font-weight: bold; }
.actions { margin-top: 20px; }
</style>