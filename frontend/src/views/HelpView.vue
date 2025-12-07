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
        <!-- 添加调试信息显示 -->
        <!-- <div class="debug-info" style="padding: 10px; background: #f0f0f0; margin-bottom: 10px;">
          <p>调试信息:</p>
          <p>当前用户ID: {{ currentUserId }}</p>
          <p>当前标签: {{ currentTab }}</p>
          <p>全部订单数: {{ allOrders.length }}</p>
          <p>显示订单数: {{ displayOrders.length }}</p>
        </div> -->

        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
          <van-list v-model:loading="loading" :finished="finished" finished-text="—— 没有更多了 ——" @load="onLoad">
            <!-- 添加信息显示 -->
             <div v-if="displayOrders.length === 0" class="empty-state">
              <van-empty description="暂无订单数据" />
              <!-- <div style="text-align: center; color: #999; margin-top: 10px;">
                <p>全部订单数: {{ allOrders.length }}</p>
                <p>当前过滤条件: {{ 
                  currentTab === 'pending' ? '待帮助订单' : 
                  currentTab === 'mine' ? '我发布的' : 
                  currentTab === 'helping' ? '我帮助的' : '全部'
                }}</p>
              </div> -->
            </div>

            <!-- 订单列表 -->
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

            <!-- 空状态提示 -->
            <!-- <div v-if="displayOrders.length === 0" class="empty-state">
              <van-image width="100" src="/empty-order.png" />
              <p>暂无订单数据</p>
              <van-button type="primary" @click="handlePublish">发布订单</van-button>
            </div> -->
          </van-list>
        </van-pull-refresh>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { isAuthenticated, getUserId } from '@/utils/auth'
import { Toast, Dialog } from 'vant'
import { orderAPI } from '@/api'

const router = useRouter()
const route = useRoute()

// 获取当前用户ID - 增强容错处理
const currentUserId = ref<number | null>(null)

// 增强的用户ID初始化
const initUserId = () => {
  try {
    const userIdStr = getUserId()
    console.log('从localStorage获取的用户ID:', userIdStr)
    
    if (userIdStr) {
      currentUserId.value = parseInt(userIdStr)
      console.log('当前用户ID:', currentUserId.value)
    } else {
      console.warn('未获取到用户ID，用户可能未登录')
      // 不设置currentUserId.value，保持为null
    }
  } catch (error) {
    console.error('初始化用户ID失败:', error)
    currentUserId.value = null
  }
}

// 状态映射
const statusMap: Record<string, string> = {
  '0': '待帮助',
  '1': '进行中', 
  '2': '已完成',
  '3': '已取消'
}

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
const allOrders = ref<any[]>([])

// 从本地存储加载订单数据并转换格式
const loadLocalOrders = () => {
  const savedOrders = localStorage.getItem('orders')
  if (savedOrders) {
    try {
      const localOrders = JSON.parse(savedOrders)
      console.log('原始本地订单数据:', localOrders)
      
      // 转换本地数据格式 - 修复clientId为null的问题
      const transformedOrders = localOrders.map((order: any) => {
        // 确保clientId有合理值
        let clientId = order.clientId
        if (clientId === null || clientId === undefined) {
          // 为null的clientId分配一个默认值，避免过滤时误判
          clientId = 0 // 使用0表示未知用户
        }
        
        const transformedOrder = {
          id: order.id || order.orderId || Math.random().toString(36).substr(2, 9),
          title: order.title || order.content || '无标题',
          address: order.address || '未指定地址',
          price: order.price || order.reward || 0,
          status: typeof order.status === 'number' ? order.status.toString() : (order.status || '0'),
          createTime: order.createTime || order.publishTime || new Date().toISOString(),
          publisherName: order.publisherName || order.clientUsername || '未知用户',
          accepter: order.accepter || order.helperUsername,
          content: order.content || order.description,
          phone: order.phone,
          helpTime: order.helpTime,
          hasRated: order.hasRated || order.evaluated || false,
          acceptTime: order.acceptTime,
          // 使用修正后的clientId
          clientId: clientId,
          helperId: order.helperId || null
        }
        
        return transformedOrder
      })
      
      console.log('转换后的本地订单数据:', transformedOrders)
      return transformedOrders
    } catch (e) {
      console.error('解析本地订单数据失败:', e)
      return []
    }
  }
  return []
}

// 创建模拟订单数据（用于API失败时）
const createMockOrders = () => {
  // 初始化用户ID
  initUserId()
  
  // 创建一些固定用户ID的测试订单
  const mockOrders = [
    {
      id: 101,
      title: '帮买瑞幸咖啡+面包',
      address: '瑞幸咖啡(校门口店)',
      price: 28.5,
      status: '0',
      createTime: '2025-11-08T11:30:00',
      publisherName: '王小二',
      accepter: '',
      content: '需要一杯拿铁和一个牛角包，送到图书馆3楼',
      phone: '13800138000',
      helpTime: '2025-11-08T12:30:00',
      hasRated: false,
      acceptTime: null,
      clientId: 1001, // 固定测试用户ID
      helperId: null
    },
    {
      id: 102,
      title: '取韵达快递',
      address: '快递柜A区',
      price: 10,
      status: '1',
      createTime: '2025-11-08T10:15:00',
      publisherName: '赵六',
      accepter: '李四',
      content: '取一个小包裹，送到宿舍楼A栋',
      phone: '13800138001',
      helpTime: '2025-11-08T11:00:00',
      hasRated: false,
      acceptTime: '2025-11-08T10:20:00',
      clientId: 1002,
      helperId: 1003
    },
    {
      id: 103,
      title: '送文件到教务处',
      address: '行政楼A座3楼',
      price: 15,
      status: '1',
      createTime: '2025-11-08T09:40:00',
      publisherName: '张三',
      accepter: '王五',
      content: '送一份申请表到教务处',
      phone: '13800138002',
      helpTime: '2025-11-08T10:30:00',
      hasRated: false,
      acceptTime: '2025-11-08T09:45:00',
      clientId: 1004,
      helperId: 1005
    }
  ]
  
  // 如果当前用户已登录，添加一些当前用户的订单
  if (currentUserId.value) {
    mockOrders.push(
      {
        id: 201,
        title: '帮忙打印资料',
        address: '图书馆打印室',
        price: 8,
        status: '0',
        createTime: '2025-11-08T14:20:00',
        publisherName: `用户${currentUserId.value}`,
        accepter: '',
        content: '打印10页资料，送到教学楼B座',
        phone: '13800138003',
        helpTime: '2025-11-08T15:00:00',
        hasRated: false,
        acceptTime: null,
        clientId: currentUserId.value,
        helperId: null
      },
      {
        id: 202,
        title: '代取外卖',
        address: '校门口外卖点',
        price: 12,
        status: '1',
        createTime: '2025-11-08T12:00:00',
        publisherName: `用户${currentUserId.value}`,
        accepter: '热心同学',
        content: '取一份麻辣烫，送到宿舍3号楼',
        phone: '13800138004',
        helpTime: '2025-11-08T12:30:00',
        hasRated: false,
        acceptTime: '2025-11-08T12:05:00',
        clientId: currentUserId.value,
        helperId: 1006
      }
    )
  }
  
  // 保存模拟数据到本地存储
  localStorage.setItem('orders', JSON.stringify(mockOrders))
  return mockOrders
}

// 转换后端订单数据为前端格式
const transformOrderData = (apiOrder: any) => {
  return {
    id: apiOrder.orderId,
    title: apiOrder.title || '无标题',
    address: apiOrder.address || '未指定地址',
    price: apiOrder.reward || 0,
    status: apiOrder.status || '0',
    createTime: apiOrder.publishTime,
    publisherName: apiOrder.clientUsername || '未知用户',
    accepter: apiOrder.helperUsername,
    content: apiOrder.description,
    phone: apiOrder.phone,
    helpTime: apiOrder.helpTime,
    hasRated: apiOrder.evaluated || false,
    acceptTime: apiOrder.acceptTime,
    clientId: apiOrder.clientId,
    helperId: apiOrder.helperId
  }
}

// 加载订单数据
const loadOrders = async (silent = false) => {
  console.log('开始加载订单数据...')
  console.log('当前标签:', currentTab.value)
  console.log('当前用户ID:', currentUserId.value)
  
  // 确保用户ID已初始化
  if (!currentUserId.value) {
    initUserId()
  }
  
  // 先尝试从本地存储加载
  let localOrders = loadLocalOrders()
  
  // 如果没有本地数据，创建模拟数据
  if (localOrders.length === 0) {
    console.log('无本地数据，创建模拟数据')
    localOrders = createMockOrders()
  }
  
  console.log('本地订单数据:', localOrders)
  allOrders.value = localOrders
  console.log('本地数据加载完成，数据量:', allOrders.value.length)
  
  // 更新显示订单和角标
  updateDisplayOrders()
  
  // 如果不是静默模式，显示加载完成提示
  if (!silent && localOrders.length > 0) {
    Toast.clear()
  }
  
  // 静默尝试API调用（不显示加载提示）
  if (silent) {
    console.log('静默尝试API调用...')
    try {
      // 检查认证状态
      if (!isAuthenticated()) {
        console.log('用户未认证，跳过API调用')
        throw new Error('用户未认证')
      }
      
      // 构建查询参数
      const queryParams: any = {
        page: 1,
        size: 50
      }
      
      // 根据当前标签设置类型
      if (currentTab.value !== 'all') {
        const typeMap: { [key: string]: string } = {
          'pending': 'available',
          'mine': 'published',
          'helping': 'accepted'
        }
        queryParams.type = typeMap[currentTab.value] || currentTab.value
      }
      
      const response = await orderAPI.getList(queryParams)
      console.log('API响应:', response)
      
      if (response.code === 200 && response.data) {
        // 转换后端数据格式为前端格式
        const apiOrders = (response.data.orders || []).map(transformOrderData)
        
        if (apiOrders.length > 0) {
          allOrders.value = apiOrders
          // 保存到本地存储
          localStorage.setItem('orders', JSON.stringify(apiOrders))
          console.log('API数据加载成功，数据量:', allOrders.value.length)
          updateDisplayOrders()
        }
      }
    } catch (error) {
      console.log('API调用失败，继续使用本地数据', error)
      // API调用失败时继续使用本地数据，不显示错误
    }
  } else {
    // 非静默模式显示加载提示
    const toast = Toast.loading({
      message: '加载中...',
      forbidClick: true,
      duration: 0
    })
    
    try {
      // 检查认证状态
      if (!isAuthenticated()) {
        Toast.clear()
        console.log('用户未认证，使用本地数据')
        return
      }
      
      // 构建查询参数
      const queryParams: any = {
        page: 1,
        size: 50
      }
      
      if (currentTab.value !== 'all') {
        const typeMap: { [key: string]: string } = {
          'pending': 'available',
          'mine': 'published', 
          'helping': 'accepted'
        }
        queryParams.type = typeMap[currentTab.value] || currentTab.value
      }
      
      const response = await orderAPI.getList(queryParams)
      Toast.clear()
      
      if (response.code === 200 && response.data) {
        const apiOrders = (response.data.orders || []).map(transformOrderData)
        
        if (apiOrders.length > 0) {
          allOrders.value = apiOrders
          localStorage.setItem('orders', JSON.stringify(apiOrders))
          updateDisplayOrders()
          Toast.success('加载成功')
        } else {
          Toast('暂无订单数据')
        }
      } else {
        Toast(response.message || '加载失败')
      }
    } catch (error: any) {
      Toast.clear()
      console.error('加载订单失败:', error)
      // 显示具体的错误信息
      if (error.response && error.response.data) {
        Toast(error.response.data.message || '网络错误')
      } else {
        Toast('网络错误，使用本地数据')
      }
    }
  }
}

// const onLoad = async () => {
//   try {
//     console.log('开始加载订单数据...')
//     console.log('当前标签:', currentTab.value)
    
//     // 确保用户ID已初始化
//     initUserId()
    
//     // 加载订单数据（静默模式）
//     await loadOrders(true)
    
//   } catch (error) {
//     console.error('加载订单失败:', error)
//     // 确保无论如何都有数据
//     const localOrders = loadLocalOrders()
//     if (localOrders.length === 0) {
//       allOrders.value = createMockOrders()
//     } else {
//       allOrders.value = localOrders
//     }
//     updateDisplayOrders()
//   } finally {
//     loading.value = false
//     finished.value = true
//     refreshing.value = false
//     console.log('订单加载完成，数据量:', allOrders.value.length)
//   }
// }
const onLoad = async () => {
  try {
    console.log('开始加载订单数据，当前标签:', currentTab.value)
    console.log('当前用户ID:', currentUserId.value)
    
    // 尝试调用API
    try {
      const requestParams = {
        page: 1,
        size: 20
      }
      console.log('API请求参数:', requestParams)
      
      const response = await orderAPI.getList(requestParams)
      console.log('API响应数据:', response)
      
      if (response && response.data) {
        console.log('原始订单数据:', response.data)
        
        // 检查数据格式
        const ordersData = response.data.orders || response.data.orders
        console.log('处理后订单数据:', ordersData)
        console.log('订单数据类型:', typeof ordersData, '是否是数组:', Array.isArray(ordersData))
        
        if (Array.isArray(ordersData) && ordersData.length > 0) {
          const transformedOrders = ordersData.map(transformOrderData)
          allOrders.value = transformedOrders
          localStorage.setItem('orders', JSON.stringify(transformedOrders))
          console.log('成功设置订单数据，数量:', transformedOrders.length)
        } else {
          console.log('订单数据为空或不是数组，使用本地数据')
          allOrders.value = loadLocalOrders()
        }
        updateOrderStats()
      } else {
        console.log('API返回数据格式异常，使用本地数据')
        allOrders.value = loadLocalOrders()
        updateOrderStats()
      }
    } catch (error) {
      console.error('API调用失败，使用本地数据:', error)
      allOrders.value = loadLocalOrders()
      updateOrderStats()
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    allOrders.value = loadLocalOrders()
    updateOrderStats()
  } finally {
    loading.value = false
    finished.value = true
    refreshing.value = false
    console.log('订单加载完成，总数据量:', allOrders.value.length)
  }
}

// 更新订单统计信息（角标）
const updateOrderStats = () => {
  console.log('开始更新订单统计...')
  console.log('全部订单数据:', allOrders.value)
  console.log('当前用户ID:', currentUserId.value)
  
  // 统计全部订单
  const totalOrders = allOrders.value.length
  console.log('全部订单数:', totalOrders)
  
  // 统计待帮助订单 (status=0)
  const pendingOrders = allOrders.value.filter(o => o.status === '0').length
  console.log('待帮助订单数:', pendingOrders)
  
  // 统计我发布的订单
  let myPublishedOrders = 0
  if (currentUserId.value) {
    myPublishedOrders = allOrders.value.filter(o => o.clientId === currentUserId.value).length
    console.log('我发布的订单clientId列表:', allOrders.value.map(o => ({id: o.id, clientId: o.clientId})))
  }
  console.log('我发布的订单数:', myPublishedOrders)
  
  // 统计我帮助的订单
  let myAcceptedOrders = 0
  if (currentUserId.value) {
    myAcceptedOrders = allOrders.value.filter(o => o.helperId === currentUserId.value).length
    console.log('我帮助的订单helperId列表:', allOrders.value.map(o => ({id: o.id, helperId: o.helperId})))
  }
  console.log('我帮助的订单数:', myAcceptedOrders)
  
  // 更新角标
  tabs.value.forEach(tab => {
    if (tab.key === 'all') {
      tab.count = totalOrders
      console.log('全部订单角标更新:', totalOrders)
    }
    if (tab.key === 'pending') {
      tab.count = pendingOrders
      console.log('待帮助订单角标更新:', pendingOrders)
    }
    if (tab.key === 'mine') {
      tab.count = myPublishedOrders
      console.log('我发布的订单角标更新:', myPublishedOrders)
    }
    if (tab.key === 'helping') {
      tab.count = myAcceptedOrders
      console.log('我帮助的订单角标更新:', myAcceptedOrders)
    }
  })
  
  console.log('角标更新完成:', tabs.value.map(tab => ({name: tab.name, count: tab.count})))
}

// 在计算属性中添加调试
const displayOrders = computed(() => {
  console.log('开始计算显示订单，当前标签:', currentTab.value)
  console.log('全部订单数量:', allOrders.value.length)
  console.log('全部订单数据:', allOrders.value)
  
  let filtered = allOrders.value

  // 添加详细的过滤调试
  if (currentTab.value === 'pending') {
    console.log('过滤条件: 待帮助订单 (status=0)')
    filtered = filtered.filter(o => o.status === '0')
    console.log('过滤后数量:', filtered.length)
  }
  
  if (currentTab.value === 'mine') {
    console.log('过滤条件: 我发布的 (clientId=', currentUserId.value, ')')
    console.log('全部订单的clientId列表:', filtered.map(o => o.clientId))
    filtered = filtered.filter(o => o.clientId === currentUserId.value)
    console.log('过滤后数量:', filtered.length)
  }
  
  if (currentTab.value === 'helping') {
    console.log('过滤条件: 我帮助的 (helperId=', currentUserId.value, ')')
    console.log('全部订单的helperId列表:', filtered.map(o => o.helperId))
    filtered = filtered.filter(o => o.helperId === currentUserId.value)
    console.log('过滤后数量:', filtered.length)
  }

  console.log('最终显示订单数量:', filtered.length)
  return filtered
})

const onRefresh = () => {
  console.log('下拉刷新')
  refreshing.value = true
  // 清除本地缓存，重新加载
  localStorage.removeItem('orders')
  onLoad()
}

// 更新显示订单和角标数量 - 增强容错处理
const updateDisplayOrders = () => {
  // 更新角标
  tabs.value.forEach(tab => {
    if (tab.key === 'all') tab.count = allOrders.value.length
    if (tab.key === 'pending') tab.count = allOrders.value.filter(o => o.status === '0').length
    
    // 关键修正：只有当用户ID有效时才计算"我发布的"和"我帮助的"
    if (tab.key === 'mine') {
      if (currentUserId.value) {
        tab.count = allOrders.value.filter(o => {
          const isMyOrder = o.clientId === currentUserId.value
          return isMyOrder
        }).length
      } else {
        tab.count = 0
      }
    }
    
    if (tab.key === 'helping') {
      if (currentUserId.value) {
        tab.count = allOrders.value.filter(o => {
          const isHelping = o.helperId === currentUserId.value
          return isHelping
        }).length
      } else {
        tab.count = 0
      }
    }
  })
  
  console.log('角标更新完成:', tabs.value.map(tab => `${tab.name}: ${tab.count}`))
}

// 计算当前显示的订单 - 增强容错处理
// const displayOrders = computed(() => {
//   let filtered = allOrders.value

//   if (currentTab.value === 'pending') {
//     filtered = filtered.filter(o => o.status === '0')
//   }
  
//   if (currentTab.value === 'mine') {
//     if (currentUserId.value) {
//       filtered = filtered.filter(o => {
//         const isMyOrder = o.clientId === currentUserId.value
//         return isMyOrder
//       })
//     } else {
//       filtered = [] // 用户ID无效时显示空列表
//     }
//   }
  
//   if (currentTab.value === 'helping') {
//     if (currentUserId.value) {
//       filtered = filtered.filter(o => {
//         const isHelping = o.helperId === currentUserId.value
//         return isHelping
//       })
//     } else {
//       filtered = [] // 用户ID无效时显示空列表
//     }
//   }

//   console.log('过滤后的订单数量:', filtered.length, '过滤条件:', currentTab.value)
//   return filtered
// })

// 格式化时间显示
const formatTime = (timeString: string) => {
  if (!timeString) return ''
  try {
    const date = new Date(timeString)
    return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
  } catch (e) {
    return timeString
  }
}

const switchTab = (key: any) => {
  console.log('切换标签:', key)
  currentTab.value = key
  router.replace({ 
    query: { ...route.query, tab: key } 
  })
  finished.value = false
  loading.value = true
  onLoad()
}

const toDetail = (id: number) => {
  console.log('查看订单详情:', id)
  router.push(`/order/detail/${id}`)
}

// 检查是否显示订单操作按钮
const showOrderActions = (order: any) => {
  return canCancelAccept(order) || canDeleteOrder(order)
}

// 检查是否可以取消接单（接单后3分钟内）
const canCancelAccept = (order: any) => {
  if (order.status !== '1') return false
  if (!currentUserId.value || order.helperId !== currentUserId.value) return false
  
  if (!order.acceptTime) return false
  
  try {
    const acceptTime = new Date(order.acceptTime).getTime()
    const currentTime = new Date().getTime()
    const timeDiff = currentTime - acceptTime
    const threeMinutes = 3 * 60 * 1000
    
    return timeDiff <= threeMinutes
  } catch (e) {
    return false
  }
}

// 检查是否可以删除订单（发布者删除未接单的订单）
const canDeleteOrder = (order: any) => {
  if (order.status !== '0') return false
  if (!currentUserId.value || order.clientId !== currentUserId.value) return false
  
  return true
}

// 处理取消接单
const handleCancelAccept = async (orderId: number) => {
  Dialog.confirm({
    title: '取消接单',
    message: '确定要取消接单吗？取消后订单将重新变为待帮助状态。'
  }).then(async () => {
    try {
      // 先更新本地状态
      const orderIndex = allOrders.value.findIndex((o: any) => o.id === orderId)
      if (orderIndex !== -1) {
        allOrders.value[orderIndex] = {
          ...allOrders.value[orderIndex],
          status: '0',
          helperId: null,
          accepter: null,
          acceptTime: null
        }
        localStorage.setItem('orders', JSON.stringify(allOrders.value))
        updateDisplayOrders()
      }
      
      // 尝试调用API
      await orderAPI.cancelAccept(orderId.toString())
      Toast.success('取消接单成功')
    } catch (error) {
      console.log('API取消接单失败，但本地状态已更新')
      Toast.success('取消接单成功（本地）')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 处理删除订单
const handleDeleteOrder = async (orderId: number) => {
  Dialog.confirm({
    title: '删除订单',
    message: '确定要删除这个订单吗？删除后无法恢复。'
  }).then(async () => {
    try {
      // 从本地订单列表中删除
      const filteredOrders = allOrders.value.filter((o: any) => o.id !== orderId)
      allOrders.value = filteredOrders
      localStorage.setItem('orders', JSON.stringify(filteredOrders))
      updateDisplayOrders()
      
      // 尝试调用API
      await orderAPI.cancel(orderId.toString())
      Toast.success('订单删除成功')
    } catch (error) {
      console.log('API删除订单失败，但本地状态已更新')
      Toast.success('订单删除成功（本地）')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 初始化用户ID
initUserId()

onMounted(() => {
  console.log('HelpView 组件挂载')
  if (route.query.tab && ['all', 'pending', 'mine', 'helping'].includes(route.query.tab as string)) {
    currentTab.value = route.query.tab as any
  }
  onLoad()
})

const handleBack = () => {
  router.back()
}

const handlePublish = () => {
  if (isAuthenticated()) {
    router.push('/order/create')
  } else {
    router.push({ 
      name: 'Login', 
      query: { redirect: '/order/create' } 
    })
  }
}

const handleAuth = () => {
  if (isAuthenticated()) {
    router.push('/profile')
  } else {
    router.push({ 
      name: 'Login', 
      query: { redirect: '/profile' } 
    })
  }
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
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}
.publish-section{
  display: flex;
  width: 90%;
  margin: 20px auto 10px;
}
</style>