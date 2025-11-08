<template>
  <div class="order-detail">
    <van-nav-bar 
      title="订单详情" 
      left-text="返回" 
      left-arrow 
      @click-left="handleBack" 
    />
    
    <div class="detail-content">
      <!-- 订单状态 -->
      <div class="status-section">
        <div class="status-badge" :class="'status-' + order.status">
          {{ statusMap[order.status] }}
        </div>
        <div v-if="order.hasRated" class="rated-badge">已评价</div>
      </div>
      
      <!-- 订单信息 -->
      <van-cell-group>
        <van-cell title="订单编号" :value="order.id" />
        <van-cell title="下单时间" :value="formatTime(order.createTime)" />
        <van-cell title="互助时间" :value="order.helpTime || '尽快'" />
        <van-cell title="发布者" :value="order.publisherName" />
        <van-cell title="联系方式" :value="order.phone || '暂无'" />
      </van-cell-group>
      
      <!-- 订单内容 -->
      <div class="content-section">
        <h3>订单内容</h3>
        <p>{{ order.content || order.title }}</p>
      </div>
      
      <!-- 佣金信息 -->
      <div class="price-section">
        <div class="price">¥{{ order.price.toFixed(2) }}</div>
        <div class="price-label">悬赏佣金</div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="action-buttons" v-if="showActions">
        <van-button 
          v-if="order.status === 0" 
          type="primary" 
          size="large" 
          @click="handleAccept"
        >
          接单
        </van-button>
        
        <van-button 
          v-else-if="order.status === 1 && isAccepter" 
          type="success" 
          size="large" 
          @click="handleComplete"
        >
          完成订单
        </van-button>
        
        <van-button 
          v-else-if="order.status === 2 && isPublisher && !order.hasRated" 
          type="warning" 
          size="large" 
          @click="handleRate"
        >
          评价本次帮助
        </van-button>
        
        <div v-else-if="order.status === 2 && isPublisher && order.hasRated" class="rated-text">
          您已评价过此订单
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Toast, Dialog } from 'vant'

const router = useRouter()
const route = useRoute()

// 状态映射
const statusMap = ['待帮助', '进行中', '已完成']

// 当前用户（模拟登录态）
const currentUser = ref('张三')

// 订单数据
const order = ref<any>({
  id: 0,
  title: '',
  address: '',
  price: 0,
  status: 0,
  createTime: '',
  publisherName: '',
  accepter: '',
  phone: '13800138000',
  helpTime: '尽快',
  content: '',
  hasRated: false // 添加评价状态字段
})

// 计算属性
const isPublisher = computed(() => order.value.publisherName === currentUser.value)
const isAccepter = computed(() => order.value.accepter === currentUser.value)
const showActions = computed(() => {
  // 发布者不能接自己的单
  if (order.value.status === 0 && isPublisher.value) return false
  // 只有接单者可以完成订单
  if (order.value.status === 1 && !isAccepter.value) return false
  // 只有发布者可以评价，且未评价过
  if (order.value.status === 2 && !isPublisher.value) return false
  return true
})

onMounted(() => {
  loadOrderDetail()
})

const loadOrderDetail = () => {
  const orderId = parseInt(route.params.id as string)
  // 从localStorage加载订单数据
  const savedOrders = localStorage.getItem('orders')
  let orders = []
  
  if (savedOrders) {
    orders = JSON.parse(savedOrders)
  } else {
    // 如果没有保存的订单，使用模拟数据
    orders = [
      { id: 101, title: '帮买瑞幸咖啡+面包', address: '瑞幸(校门口)', price: 28.5, status: 0, createTime: '2025-11-08 11:30', publisherName: '王小二', content: '需要一杯拿铁和一个牛角包，送到图书馆3楼', hasRated: false },
      { id: 102, title: '取韵达快递', address: '快递柜', price: 10, status: 1, createTime: '2025-11-08 10:15', publisherName: '赵六', accepter: currentUser.value, content: '取一个小包裹，送到宿舍楼A栋', hasRated: false },
      { id: 103, title: '送文件到教务处', address: 'A座3楼', price: 15, status: 1, createTime: '2025-11-08 09:40', publisherName: currentUser.value, accepter: '李四', content: '送一份申请表到教务处', hasRated: false },
      { id: 104, title: '占座图书馆3楼', address: '图书馆', price: 25, status: 2, createTime: '2025-11-08 08:20', publisherName: '孙八', accepter: '王五', content: '需要在图书馆3楼占一个位置', hasRated: false },
      { id: 105, title: '帮打饭', address: '二餐', price: 12, status: 2, createTime: '2025-11-08 12:00', publisherName: currentUser.value, accepter: '王五', content: '帮忙打一份午餐', hasRated: false }
    ]
    localStorage.setItem('orders', JSON.stringify(orders))
  }
  
  const foundOrder = orders.find(o => o.id === orderId)
  if (foundOrder) {
    order.value = foundOrder
  } else {
    Toast('订单不存在')
    router.back()
  }
}

const handleBack = () => {
  router.back()
}

const handleAccept = () => {
  Dialog.confirm({
    title: '确认接单',
    message: '确定要接这个订单吗？'
  }).then(() => {
    // 更新订单状态
    updateOrderStatus(1, { accepter: currentUser.value })
    Toast.success('接单成功')
  })
}

const handleComplete = () => {
  Dialog.confirm({
    title: '确认完成',
    message: '确定订单已经完成了吗？'
  }).then(() => {
    // 更新订单状态
    updateOrderStatus(2)
    // 跳转到完成页面
    router.push({
      name: 'OrderComplete',
      query: { amount: order.value.price }
    })
  })
}

const handleRate = () => {
  // 跳转到评价页面
  router.push({
    name: 'OrderRate',
    query: { orderId: order.value.id }
  })
}

// 更新订单状态
const updateOrderStatus = (newStatus: number, additionalData: any = {}) => {
  const savedOrders = localStorage.getItem('orders')
  if (savedOrders) {
    const orders = JSON.parse(savedOrders)
    const orderIndex = orders.findIndex((o: any) => o.id === order.value.id)
    if (orderIndex !== -1) {
      orders[orderIndex] = {
        ...orders[orderIndex],
        status: newStatus,
        ...additionalData
      }
      localStorage.setItem('orders', JSON.stringify(orders))
      order.value = orders[orderIndex]
    }
  }
}

const formatTime = (t: string) => t.slice(5, 16).replace('-', '/')
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

.status-0 { background: #ff6b6b; } /* 待帮助 */
.status-1 { background: #4ecdc4; } /* 进行中 */
.status-2 { background: #1abc9c; } /* 已完成 */

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

.rated-text {
  text-align: center;
  color: #999;
  font-size: 16px;
  padding: 10px;
}
</style>