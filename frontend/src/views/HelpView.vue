<!-- src/views/AllOrders.vue -->
<template>
  <div class="all-orders-page">
        <van-nav-bar 
      title="互助" 
      left-text="返回" 
      left-arrow 
      @click-left="handleBack" 
    />

    <!-- 顶部导航栏 -->
    <div class="tab-bar">
      <div v-for="tab in tabs" :key="tab.key" class="tab-item" :class="{ active: currentTab === tab.key }"
        @click="switchTab(tab.key)">
        <span>{{ tab.name }}</span>
        <div class="badge" v-if="tab.count > 0">{{ tab.count }}</div>
      </div>
    </div>

<div class="publish-section">
  <van-button round block type="primary" size="large" icon="contact" @click="handleAuth" class="publish-btn">
        <span class="btn-text">身份认证</span>
      </van-button>
      <van-button round block type="danger" size="large" icon="add-o" @click="handlePublish" class="publish-btn">
        <span class="btn-text">发布订单</span>
      </van-button>
      
    </div>
    <div class="scroll-container">
      <div class="content">
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
          <van-list v-model:loading="loading" :finished="finished" finished-text="—— 没有更多了 ——" @load="onLoad">
            <div v-for="order in displayOrders" :key="order.id" class="order-card">
              <div class="top">
                <span class="status" :class="'s' + order.status">
                  {{ statusMap[order.status] }}
                </span>
                <span class="price">¥{{ order.price.toFixed(2) }}</span>
              </div>
              <div class="middle" @click="toDetail(order.id)">
                <h3>{{ order.title }}</h3>
                <p><van-icon name="location-o" /> {{ order.address }}</p>
              </div>
              <div class="bottom">
                <span>{{ order.publisherName }} 发布</span>
                <span>{{ formatTime(order.createTime) }}</span>
              </div>
              
              <!-- 操作按钮区域 -->
              <div class="action-buttons" v-if="showOrderActions(order)">
                <van-button 
                  v-if="canCancelAccept(order)" 
                  size="small" 
                  type="warning"
                  @click="handleCancelAccept(order.id)"
                >
                  取消接单
                </van-button>
                <van-button 
                  v-if="canDeleteOrder(order)" 
                  size="small" 
                  type="danger"
                  @click="handleDeleteOrder(order.id)"
                >
                  删除订单
                </van-button>
              </div>
            </div>
          </van-list>
        </van-pull-refresh>
      </div>
    </div>
    

  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { isAuthenticated } from '@/utils/auth' // 导入登录检查函数
import { Toast, Dialog } from 'vant'

// 处理返回
const handleBack = () => {
  router.back()
}

// 发布按钮点击事件
const handlePublish = () => {
  if (isAuthenticated()) {
    router.push('/order/create')
  } else {
    // 未登录时跳转到登录页，并携带重定向参数
    router.push({ 
      name: 'Login', 
      query: { redirect: '/order/create' } 
    })
  }
}

const router = useRouter()
const route = useRoute()

// 当前用户（模拟登录态）
const currentUser = '张三'

// 从localStorage加载订单数据
const loadOrders = () => {
  const savedOrders = localStorage.getItem('orders')
  if (savedOrders) {
    return JSON.parse(savedOrders)
  } else {
    // 如果没有保存的订单，使用模拟数据
    const initialOrders = [
      { 
        id: 101, 
        title: '帮买瑞幸咖啡+面包', 
        address: '瑞幸(校门口)', 
        price: 28.5, 
        status: 0, 
        createTime: '2025-11-08 11:30', 
        publisherName: '王小二', 
        hasRated: false,
        acceptTime: null 
      },
      { 
        id: 102, 
        title: '取韵达快递', 
        address: '快递柜', 
        price: 10, 
        status: 1, 
        createTime: '2025-11-08 10:15', 
        publisherName: '赵六', 
        accepter: currentUser, 
        hasRated: false,
        acceptTime: new Date().toISOString() // 刚刚接单
      },
      { 
        id: 103, 
        title: '送文件到教务处', 
        address: 'A座3楼', 
        price: 15, 
        status: 1, 
        createTime: '2025-11-08 09:40', 
        publisherName: currentUser, 
        accepter: '李四', 
        hasRated: false,
        acceptTime: new Date(Date.now() - 4 * 60 * 1000).toISOString() // 4分钟前接单
      },
      { 
        id: 104, 
        title: '占座图书馆3楼', 
        address: '图书馆', 
        price: 25, 
        status: 0, 
        createTime: '2025-11-08 08:20', 
        publisherName: '孙八', 
        hasRated: false,
        acceptTime: null 
      },
      { 
        id: 105, 
        title: '帮打饭', 
        address: '二餐', 
        price: 12, 
        status: 2, 
        createTime: '2025-11-08 12:00', 
        publisherName: currentUser, 
        accepter: '王五', 
        hasRated: false,
        acceptTime: new Date(Date.now() - 10 * 60 * 1000).toISOString() // 10分钟前接单
      }
    ]
    localStorage.setItem('orders', JSON.stringify(initialOrders))
    return initialOrders
  }
}

// 所有订单数据
const allOrders = ref<any[]>(loadOrders())

// 状态映射
const statusMap = ['待帮助', '进行中', '已完成']

// 导航栏配置
const tabs = ref([
  { key: 'all', name: '全部订单', count: 0 },
  { key: 'pending', name: '未被接单', count: 0 },
  { key: 'mine', name: '我发布的', count: 0 },
  { key: 'helping', name: '我帮助的', count: 0 },
])

const currentTab = ref<'all' | 'pending' | 'mine' | 'helping'>('all')
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 将 onLoad 和相关函数移到 watch 之前
const onLoad = () => {
  // 重新加载订单数据
  allOrders.value = loadOrders()
  
  setTimeout(() => {
    loading.value = false
    finished.value = true
    refreshing.value = false
  }, 600)
}

const onRefresh = () => {
  refreshing.value = true
  onLoad()
}

const formatTime = (t: string) => t.slice(5, 16).replace('-', '/')

const switchTab = (key: any) => {
  currentTab.value = key
  router.replace({ 
    query: { ...route.query, tab: key } 
  })
  finished.value = false
  loading.value = true
  onLoad()
}

const toDetail = (id: number) => {
  router.push(`/order/detail/${id}`)
}

// 检查是否显示订单操作按钮
const showOrderActions = (order: any) => {
  return canCancelAccept(order) || canDeleteOrder(order)
}

// 检查是否可以取消接单（接单后3分钟内）
const canCancelAccept = (order: any) => {
  if (order.status !== 1) return false // 只有进行中的订单
  if (order.accepter !== currentUser) return false // 只有接单者可以取消
  
  // 检查接单时间是否在3分钟内
  if (!order.acceptTime) return false
  
  const acceptTime = new Date(order.acceptTime).getTime()
  const currentTime = new Date().getTime()
  const timeDiff = currentTime - acceptTime
  const threeMinutes = 3 * 60 * 1000 // 3分钟
  
  return timeDiff <= threeMinutes
}

// 检查是否可以删除订单（发布者删除未接单的订单）
const canDeleteOrder = (order: any) => {
  if (order.status !== 0) return false // 只有待帮助的订单
  if (order.publisherName !== currentUser) return false // 只有发布者可以删除
  
  return true
}

// 处理取消接单
const handleCancelAccept = (orderId: number) => {
  Dialog.confirm({
    title: '取消接单',
    message: '确定要取消接单吗？取消后订单将重新变为待帮助状态。'
  }).then(() => {
    // 更新订单状态
    const savedOrders = localStorage.getItem('orders')
    if (savedOrders) {
      const orders = JSON.parse(savedOrders)
      const orderIndex = orders.findIndex((o: any) => o.id === orderId)
      if (orderIndex !== -1) {
        orders[orderIndex] = {
          ...orders[orderIndex],
          status: 0, // 重置为待帮助
          accepter: null, // 清除接单人
          acceptTime: null // 清除接单时间
        }
        localStorage.setItem('orders', JSON.stringify(orders))
        allOrders.value = orders
        
        Toast.success('取消接单成功')
        
        // 重新加载数据
        onLoad()
      }
    }
  })
}

// 处理删除订单
const handleDeleteOrder = (orderId: number) => {
  Dialog.confirm({
    title: '删除订单',
    message: '确定要删除这个订单吗？删除后无法恢复。'
  }).then(() => {
    // 从订单列表中删除
    const savedOrders = localStorage.getItem('orders')
    if (savedOrders) {
      const orders = JSON.parse(savedOrders)
      const filteredOrders = orders.filter((o: any) => o.id !== orderId)
      localStorage.setItem('orders', JSON.stringify(filteredOrders))
      allOrders.value = filteredOrders
      
      Toast.success('订单删除成功')
      
      // 重新加载数据
      onLoad()
    }
  })
}

// 现在 watch 可以安全地调用 onLoad
watch(
  () => route.query.tab,
  (newTab) => {
    if (newTab && ['all', 'pending', 'mine', 'helping'].includes(newTab as string)) {
      currentTab.value = newTab as any
      finished.value = false
      loading.value = true
      onLoad()
    }
  },
  { immediate: true }
)

// 计算当前显示的订单 + 角标数量
const displayOrders = computed(() => {
  let filtered = allOrders.value

  if (currentTab.value === 'pending') filtered = filtered.filter(o => o.status === 0)
  if (currentTab.value === 'mine') filtered = filtered.filter(o => o.publisherName === currentUser)
  if (currentTab.value === 'helping') filtered = filtered.filter(o => o.accepter === currentUser)

  // 更新角标
  tabs.value.forEach(tab => {
    if (tab.key === 'all') tab.count = allOrders.value.length
    if (tab.key === 'pending') tab.count = allOrders.value.filter(o => o.status === 0).length
    if (tab.key === 'mine') tab.count = allOrders.value.filter(o => o.publisherName === currentUser).length
    if (tab.key === 'helping') tab.count = allOrders.value.filter(o => o.accepter === currentUser).length
  })

  return filtered
})

onMounted(() => {
  if (route.query.tab && ['all', 'pending', 'mine', 'helping'].includes(route.query.tab as string)) {
    currentTab.value = route.query.tab as any
  }
  onLoad()
})
const handleAuth = () => {
  router.push('/profile')
}
</script>

<style scoped>
.all-orders-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.tab-bar {
  position: sticky;
  top: 0;
  background: white;
  display: flex;
  border-bottom: 1px solid #eee;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.tab-item {
  flex: 1;
  padding: 14px 0;
  text-align: center;
  font-size: 15px;
  color: #666;
  position: relative;
  transition: all 0.3s;
}

.tab-item.active {
  color: #ee0a24;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 3px;
  background: #ee0a24;
  border-radius: 2px;
}

.badge {
  position: absolute;
  top: 8px;
  right: 30%;
  background: #ee0a24;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 16px;
}

.content {
  padding: 10px 16px 80px;
}

.order-card {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  position: relative;
}

.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.status {
  padding: 4px 12px;
  border-radius: 20px;
  color: #fff;
  font-size: 13px;
}

.s0 {
  background: #ff6b6b;
}

.s1 {
  background: #4ecdc4;
}

.s2 {
  background: #1abc9c;
}

.price {
  font-size: 22px;
  font-weight: bold;
  color: #e74c3c;
}

.middle {
  cursor: pointer;
}

.middle h3 {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
}

.middle p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.bottom {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  color: #999;
  font-size: 12px;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.scroll-container {
  height: calc(100vh - 150px);
  /* 减去导航栏高度 */
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  /* 平滑滚动 */
}
.publish-section{
  display: flex;
  width: 90%;
  margin: 20px auto 10px;
}
</style>