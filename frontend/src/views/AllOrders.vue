<!-- src/views/AllOrders.vue -->
<template>
  <div class="all-orders-page">
    <TheHeader />
    
    <!-- 顶部导航栏 -->
    <div class="tab-bar">
      <div 
        v-for="tab in tabs" 
        :key="tab.key"
        class="tab-item"
        :class="{ active: currentTab === tab.key }"
        @click="switchTab(tab.key)"
      >
        <span>{{ tab.name }}</span>
        <div class="badge" v-if="tab.count > 0">{{ tab.count }}</div>
      </div>
    </div>


<div class="publish-section">
  <van-button 
    round 
    block 
    type="danger" 
    size="large" 
    icon="plus" 
    @click="handlePublish"
    class="publish-btn"
  >
    <span class="btn-text">发布跑腿订单</span>
  </van-button>
</div>
    <div class="content">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="—— 没有更多了 ——"
          @load="onLoad"
        >
          <div
            v-for="order in displayOrders"
            :key="order.id"
            class="order-card"
            @click="toDetail(order.id)"
          >
            <div class="top">
              <span class="status" :class="'s' + order.status">
                {{ statusMap[order.status] }}
              </span>
              <span class="price">¥{{ order.price.toFixed(2) }}</span>
            </div>
            <div class="middle">
              <h3>{{ order.title }}</h3>
              <p><van-icon name="location-o" /> {{ order.address }}</p>
            </div>
            <div class="bottom">
              <span>{{ order.publisherName }} 发布</span>
              <span>{{ formatTime(order.createTime) }}</span>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
    
    <TheFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

//模拟登录状态（队友写的 token 就用这个）
const isLoggedIn = () => {
  return localStorage.getItem('token') || localStorage.getItem('userInfo')
}

// 发布按钮点击事件
const handlePublish = () => {
  if (isLoggedIn()) {
    router.push('/order/create')  // 已登录 → 跳发布表单
  } else {
    router.push('/login')         // 未登录 → 跳登录页
  }
}

const router = useRouter()

// 当前用户（模拟登录态）
const currentUser = '张三'  // 改成你的名字

// 所有订单数据
const allOrders = ref<any[]>([
  { id: 101, title: '帮买瑞幸咖啡+面包', address: '瑞幸(校门口)', price: 28.5, status: 0, createTime: '2025-11-08 11:30', publisherName: '王小二' },
  { id: 102, title: '取韵达快递', address: '快递柜', price: 10, status: 1, createTime: '2025-11-08 10:15', publisherName: '赵六', accepter: currentUser },
  { id: 103, title: '送文件到教务处', address: 'A座3楼', price: 15, status: 2, createTime: '2025-11-08 09:40', publisherName: currentUser },
  { id: 104, title: '占座图书馆3楼', address: '图书馆', price: 25, status: 0, createTime: '2025-11-08 08:20', publisherName: '孙八' },
  { id: 105, title: '帮打饭', address: '二餐', price: 12, status: 0, createTime: '2025-11-08 12:00', publisherName: currentUser },
])

// 状态映射
const statusMap = ['待接单', '进行中', '待评价', '已完成']

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

const formatTime = (t: string) => t.slice(5, 16).replace('-', '/')

const onLoad = () => {
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

const switchTab = (key: any) => {
  currentTab.value = key
  finished.value = false
  loading.value = true
  onLoad()
}

const toDetail = (id: number) => {
  router.push(`/order/detail/${id}`)
}

onMounted(() => {
  onLoad()
})
</script>

<style scoped>
.all-orders-page { min-height: 100vh; background: #f5f5f5; }
.tab-bar {
  position: sticky;
  top: 0;
  background: white;
  display: flex;
  border-bottom: 1px solid #eee;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
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
.content { padding: 110px 16px 80px; }
.order-card { background: #fff; border-radius: 16px; padding: 16px; margin-bottom: 12px; box-shadow: 0 4px 16px rgba(0,0,0,0.1); }
.top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.status { padding: 4px 12px; border-radius: 20px; color: #fff; font-size: 13px; }
.s0 { background: #ff6b6b; }
.s1 { background: #4ecdc4; }
.s2 { background: #f7b731; }
.s3 { background: #1abc9c; }
.price { font-size: 22px; font-weight: bold; color: #e74c3c; }
.middle h3 { margin: 0 0 6px; font-size: 16px; font-weight: 600; }
.middle p { margin: 0; color: #666; font-size: 14px; }
.bottom { display: flex; justify-content: space-between; margin-top: 12px; color: #999; font-size: 12px; }
</style>