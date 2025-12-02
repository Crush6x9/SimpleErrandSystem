<template>
  <div class="order-detail">
    <van-nav-bar title="订单详情" left-text="返回" left-arrow @click-left="handleBack" />

    <div class="detail-content">
      <div class="status-section">
        <div class="status-badge" :class="'status-' + order.status">
          {{ statusMap[order.status] }}
        </div>
        <div v-if="order.evaluated" class="rated-badge">已评价</div>
      </div>

      <van-cell-group>
        <van-cell title="订单主题" :value="order.title" />
        <van-cell title="下单时间" :value="formatTime(order.publishTime)" />
        <van-cell title="期望时间" :value="formatHelpTime(order.helpTime)" />
        <van-cell title="发布者" :value="order.clientUsername" />
        <van-cell title="联系方式" :value="order.phone || '暂无'" />
        <van-cell v-if="order.helperUsername" title="接单人" :value="order.helperUsername" />
        <van-cell v-if="order.acceptTime" title="接单时间" :value="formatTime(order.acceptTime)" />
        <van-cell v-if="order.completeTime" title="完成时间" :value="formatTime(order.completeTime)" />
        <van-cell v-if="order.address" title="送达地址" :value="order.address" />
      </van-cell-group>

      <div class="content-section">
        <h3>订单描述</h3>
        <p class="order-description">{{ order.description }}</p>
      </div>

      <div class="price-section">
        <div class="price">¥{{ order.reward?.toFixed(2) || '0.00' }}</div>
        <div class="price-label">悬赏金额</div>
      </div>

      <div class="action-buttons" v-if="showActions">
        <van-button v-if="order.status === '0' && !isPublisher && userRole === '1'" type="primary" size="large"
          @click="handleAccept" :loading="loading">
          接单
        </van-button>
        <van-button v-else-if="order.status === '1' && isHelper" type="success" size="large" @click="handleComplete"
          :loading="loading">
          完成订单
        </van-button>
        <van-button v-else-if="order.status === '2' && isPublisher && !order.evaluated" type="warning" size="large"
          @click="handleRate">
          评价本次帮助
        </van-button>

        <div v-else-if="order.status === '2' && isPublisher && order.evaluated" class="rated-text">
          您已评价过此订单
        </div>
        <div v-else-if="order.status === '0' && !isPublisher && userRole !== '1'" class="not-helper-text">
          只有跑腿员可以接单
        </div>
      </div>

      <div class="extra-action-buttons" v-if="showExtraActions">
        <van-button v-if="canCancelAccept" type="warning" size="large" @click="handleCancelAccept" :loading="loading">
          取消接单 (剩余{{ cancelAcceptRemainingTime }})
        </van-button>
        <van-button v-if="canCancelOrder" type="danger" size="large" @click="handleCancelOrder" :loading="loading">
          取消订单
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Toast, Dialog } from 'vant'
import { orderAPI } from '@/api'
import { getUserId, getUserInfo } from '@/utils/auth'

const router = useRouter()
const route = useRoute()

const statusMap: { [key: string]: string } = {
  '0': '待帮助',
  '1': '进行中',
  '2': '已完成',
  '3': '已取消'
}

const order = ref<any>({
  orderId: 0,
  title: '',
  description: '',
  address: '',
  reward: 0,
  status: '0',
  publishTime: '',
  clientUsername: '',
  helperUsername: '',
  phone: '',
  helpTime: '',
  evaluated: false,
  acceptTime: null,
  clientId: null,
  helperId: null
})

const loading = ref(false)
const currentUserId = ref<string | null>(null)
const userRole = ref<string>('0')

const cancelAcceptRemainingTime = ref('')
let countdownTimer: number | null = null

onMounted(() => {
  const userInfo = getUserInfo()
  currentUserId.value = userInfo?.id ? userInfo.id.toString() : null
  userRole.value = userInfo?.role || '0'

  loadOrderDetail()
  startCountdownTimer()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
})

// 计算属性
const isPublisher = computed(() => {
  return currentUserId.value && order.value.clientId === parseInt(currentUserId.value)
})

const isHelper = computed(() => {
  return currentUserId.value && order.value.helperId === parseInt(currentUserId.value)
})

// 检查是否可以取消接单（接单后3分钟内）
const canCancelAccept = computed(() => {
  if (order.value.status !== '1') return false // 只有进行中的订单
  if (!isHelper.value) return false // 只有接单者可以取消

  // 检查接单时间是否在3分钟内
  if (!order.value.acceptTime) return false

  const acceptTime = new Date(order.value.acceptTime).getTime()
  const currentTime = new Date().getTime()
  const timeDiff = currentTime - acceptTime
  const threeMinutes = 3 * 60 * 1000

  return timeDiff <= threeMinutes
})

// 检查是否可以取消订单（发布者取消未接单的订单）
const canCancelOrder = computed(() => {
  if (order.value.status !== '0') return false // 只有待帮助的订单
  if (!isPublisher.value) return false // 只有发布者可以取消

  return true
})

const showExtraActions = computed(() => {
  return canCancelAccept.value || canCancelOrder.value
})

const showActions = computed(() => {
  // 发布者不能接自己的单
  if (order.value.status === '0' && isPublisher.value) return false
  // 只有接单者可以完成订单
  if (order.value.status === '1' && !isHelper.value) return false
  // 只有发布者可以评价，且未评价过
  if (order.value.status === '2' && (!isPublisher.value || order.value.evaluated)) return false
  return true
})

const loadOrderDetail = async () => {
  const orderId = route.params.id as string

  try {
    loading.value = true
    const response = await orderAPI.getDetail(orderId)

    if (response.code === 200 && response.data) {
      order.value = response.data
      updateCancelAcceptRemainingTime()
    } else {
      Toast(response.message || '获取订单详情失败')
    }
  } catch (error: any) {
    console.error('加载订单详情失败:', error)
    Toast('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const updateCancelAcceptRemainingTime = () => {
  if (!order.value.acceptTime) {
    cancelAcceptRemainingTime.value = ''
    return
  }

  const acceptTime = new Date(order.value.acceptTime).getTime()
  const currentTime = new Date().getTime()
  const timeDiff = currentTime - acceptTime
  const threeMinutes = 3 * 60 * 1000

  if (timeDiff > threeMinutes) {
    cancelAcceptRemainingTime.value = '已过期'
    return
  }

  const remainingTime = threeMinutes - timeDiff
  const minutes = Math.floor(remainingTime / (60 * 1000))
  const seconds = Math.floor((remainingTime % (60 * 1000)) / 1000)

  cancelAcceptRemainingTime.value = `${minutes}分${seconds}秒`
}

const startCountdownTimer = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }

  countdownTimer = setInterval(() => {
    if (canCancelAccept.value) {
      updateCancelAcceptRemainingTime()
    } else {
      cancelAcceptRemainingTime.value = ''
      if (countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }
  }, 1000)
}

const handleBack = () => {
  router.back()
}

const handleAccept = async () => {
  Dialog.confirm({
    title: '确认接单',
    message: '确定要接这个订单吗？'
  }).then(async () => {
    try {
      loading.value = true
      const response = await orderAPI.accept(order.value.orderId.toString())

      if (response.code === 200) {
        Toast.success('接单成功')
        await loadOrderDetail()
        startCountdownTimer()
      } else {
        Toast(response.message || '接单失败')
      }
    } catch (error: any) {
      Toast('接单失败')
    } finally {
      loading.value = false
    }
  })
}

const handleComplete = async () => {
  Dialog.confirm({
    title: '确认完成',
    message: '确定订单已经完成了吗？'
  }).then(async () => {
    try {
      loading.value = true
      const response = await orderAPI.complete(order.value.orderId.toString())

      if (response.code === 200) {
        Toast.success('订单完成成功')
        router.push({
          name: 'OrderComplete',
          query: { amount: order.value.reward }
        })
      } else {
        Toast(response.message || '完成订单失败')
      }
    } catch (error: any) {
      Toast('完成订单失败')
    } finally {
      loading.value = false
    }
  })
}

const handleRate = () => {
  router.push({
    name: 'OrderRate',
    query: { orderId: order.value.orderId }
  })
}

const handleCancelAccept = async () => {
  Dialog.confirm({
    title: '取消接单',
    message: '确定要取消接单吗？取消后订单将重新变为待帮助状态。'
  }).then(async () => {
    try {
      loading.value = true
      const response = await orderAPI.cancelAccept(order.value.orderId.toString())

      if (response.code === 200) {
        Toast.success('取消接单成功')
        await loadOrderDetail()

        if (countdownTimer) {
          clearInterval(countdownTimer)
          countdownTimer = null
        }
      } else {
        Toast(response.message || '取消接单失败')
      }
    } catch (error: any) {
      Toast('取消接单失败')
    } finally {
      loading.value = false
    }
  })
}

const handleCancelOrder = async () => {
  Dialog.confirm({
    title: '取消订单',
    message: '确定要取消这个订单吗？取消后无法恢复。'
  }).then(async () => {
    try {
      loading.value = true
      const response = await orderAPI.cancel(order.value.orderId.toString())

      if (response.code === 200) {
        Toast.success('订单取消成功')
        router.back()
      } else {
        Toast(response.message || '取消订单失败')
      }
    } catch (error: any) {
      Toast('取消订单失败')
    } finally {
      loading.value = false
    }
  })
}

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const formatHelpTime = (helpTime: string) => {
  if (!helpTime) return '尽快'
  return formatTime(helpTime)
}
</script>

<style scoped>
.order-detail {
  min-height: 100vh;
  background: #f5f5f5;
}

.detail-content {
  padding: 16px;
}

.status-section {
  text-align: center;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.status-badge {
  display: inline-block;
  padding: 8px 16px;
  border-radius: 20px;
  color: white;
  font-size: 14px;
  font-weight: bold;
}

.rated-badge {
  display: inline-block;
  padding: 8px 16px;
  border-radius: 20px;
  background: #1abc9c;
  color: white;
  font-size: 14px;
  font-weight: bold;
}

.status-0 {
  background: #ff6b6b;
}

/* 待帮助 */
.status-1 {
  background: #4ecdc4;
}

/* 进行中 */
.status-2 {
  background: #1abc9c;
}

/* 已完成 */
.status-3 {
  background: #95a5a6;
}

/* 已取消 */

.content-section {
  background: white;
  padding: 16px;
  border-radius: 8px;
  margin: 16px 0;
}

.content-section h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.content-section p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.price-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  margin: 16px 0;
}

.price {
  font-size: 32px;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 8px;
}

.price-label {
  color: #999;
  font-size: 14px;
}

.action-buttons {
  padding: 20px 0;
}

.extra-action-buttons {
  padding: 10px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rated-text,
.not-helper-text {
  text-align: center;
  color: #999;
  font-size: 16px;
  padding: 10px;
}
</style>