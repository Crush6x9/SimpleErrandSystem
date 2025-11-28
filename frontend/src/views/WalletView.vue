<!-- src/views/WalletView.vue -->
<template>
  <div class="wallet">
    <van-nav-bar 
      title="我的钱包" 
      left-text="返回" 
      left-arrow 
      @click-left="handleBack"
    >
      <template #right>
        <van-icon name="replay" size="18" @click="handleRefresh" />
      </template>
    </van-nav-bar>
    
    <!-- 添加滚动容器 -->
    <div class="wallet-scroll-container">
      <div class="wallet-content">
        <!-- 加载状态 -->
        <van-loading v-if="loading" class="loading" type="spinner" size="24px">加载中...</van-loading>
        
        <div v-else>
          <!-- 钱包概览 -->
          <div class="wallet-overview">
            <div class="balance-card">
              <div class="balance-item">
                <div class="balance-label">总收入</div>
                <div class="balance-value">¥{{ formatAmount(walletInfo.totalIncome) }}</div>
              </div>
              <div class="balance-item">
                <div class="balance-label">可提现余额</div>
                <div class="balance-value primary">¥{{ formatAmount(walletInfo.balance) }}</div>
              </div>
            </div>
            
            <van-button 
              type="primary" 
              size="large" 
              class="withdraw-btn"
              @click="handleWithdraw"
              :disabled="walletInfo.balance <= 0"
            >
              提现
            </van-button>
          </div>
          
          <!-- 今日收益 -->
          <div v-if="todayIncome > 0" class="today-income-section">
            <div class="today-income-card">
              <div class="today-income-label">今日收益</div>
              <div class="today-income-value">+¥{{ formatAmount(todayIncome) }}</div>
            </div>
          </div>
          
          <!-- 账单明细 -->
          <div class="bill-section">
            <h2 class="section-title">账单明细</h2>
            
            <div 
              v-for="bill in billList" 
              :key="bill.orderId" 
              class="bill-item"
            >
              <div class="bill-info">
                <div class="bill-desc">{{ bill.title || '订单收入' }}</div>
                <div class="bill-date">{{ bill.completeTime }}</div>
              </div>
              <div class="bill-amount positive">+¥{{ formatAmount(bill.income || 0) }}</div>
            </div>
            
            <div v-if="billList.length === 0" class="empty-bill">
              <van-empty description="暂无账单记录" />
            </div>
          </div>
          
          <!-- 提现记录 -->
          <div class="withdraw-section">
            <h2 class="section-title">提现记录</h2>
            
            <div 
              v-for="item in withdrawHistory" 
              :key="item.id" 
              class="withdraw-item"
            >
              <div class="withdraw-info">
                <div class="withdraw-desc">提现至微信</div>
                <div class="withdraw-date">{{ item.date }}</div>
              </div>
              <div class="withdraw-amount negative" :style="{ color: getStatusColor(item.status) }">
                -¥{{ formatAmount(item.amount) }}
                <span class="status-tag" :style="{ backgroundColor: getStatusColor(item.status) + '20', color: getStatusColor(item.status) }">
                  {{ getStatusText(item.status) }}
                </span>
              </div>
            </div>
            
            <div v-if="withdrawHistory.length === 0" class="empty-withdraw">
              <van-empty description="暂无提现记录" />
            </div>
          </div>
        </div>
        
        <!-- 添加底部填充，避免内容被底部导航栏遮挡 -->
        <div style="height: 60px;"></div>
      </div>
    </div>
    
    <!-- 提现浮窗 -->
    <van-popup 
      v-model:show="showWithdrawDialog" 
      position="bottom" 
      class="popup"
      :style="{ height: '35%' }"
      round
    >
      <div class="withdraw-dialog">
        <h3>提现</h3>
        <p class="available-balance">可提现余额: ¥{{ formatAmount(walletInfo.balance) }}</p>
        
        <div class="amount-input">
          <span class="currency-symbol">¥</span>
          <input 
            v-model="withdrawAmount" 
            type="number" 
            placeholder="请输入提现金额" 
            inputmode="decimal"
            @input="handleAmountInput"
          >
        </div>
        
        <p class="withdraw-hint">提现金额需为整数，且不超过可提现余额</p>
        
        <div class="withdraw-actions">
          <van-button type="default" @click="showWithdrawDialog = false">取消</van-button>
          <van-button 
            type="primary" 
            @click="confirmWithdraw"
            :disabled="!withdrawAmount || Number(withdrawAmount) <= 0"
          >
            确认提现
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Toast, Dialog } from 'vant'
import { isAuthenticated, getUserId } from '@/utils/auth'
import { walletAPI } from '@/api'

const router = useRouter()
const isAuth = ref(false)
const userId = ref('')
const walletInfo = reactive({
  totalIncome: 0,
  balance: 0
})
const showWithdrawDialog = ref(false)
const withdrawAmount = ref('')
const withdrawHistory = ref([])
const billList = ref([])
const todayIncome = ref(0)
const loading = ref(false)
const useMockData = ref(false) // 标记是否使用模拟数据

// 模拟数据
const mockWalletData = {
  totalIncome: 156.80,
  balance: 85.50
}

const mockBillList = [
  { orderId: 1, title: '代取快递', income: 15.00, completeTime: '2023-06-20' },
  { orderId: 2, title: '代买午餐', income: 25.00, completeTime: '2023-06-18' },
  { orderId: 3, title: '代送文件', income: 30.00, completeTime: '2023-06-15' }
]

const mockWithdrawHistory = [
  { id: 1, amount: 50.00, status: 'success', createTime: '2023-06-15' },
  { id: 2, amount: 30.00, status: 'pending', createTime: '2023-06-10' }
]

onMounted(() => {
  checkAuthStatus()
  loadWalletData()
})

// 检查认证状态
const checkAuthStatus = () => {
  isAuth.value = isAuthenticated()
  if (!isAuth.value) {
    router.push({ name: 'Login', query: { redirect: '/wallet' } })
  } else {
    userId.value = getUserId() || ''
  }
}

// 加载钱包数据
const loadWalletData = async () => {
  try {
    loading.value = true
    useMockData.value = false
    
    // 尝试加载真实数据
    await Promise.all([
      loadWalletInfo(),
      loadBills(),
      loadWithdrawals()
    ])
    
  } catch (error) {
    console.error('加载钱包数据失败，使用模拟数据:', error)
    useMockData.value = true
    loadMockData()
    Toast('使用演示数据')
  } finally {
    loading.value = false
  }
}

// 加载模拟数据
const loadMockData = () => {
  walletInfo.totalIncome = mockWalletData.totalIncome
  walletInfo.balance = mockWalletData.balance
  billList.value = mockBillList
  todayIncome.value = 15.00 // 模拟今日收益
  withdrawHistory.value = mockWithdrawHistory.map(item => ({
    ...item,
    date: formatDate(item.createTime)
  }))
}

// 加载钱包信息
const loadWalletInfo = async () => {
  try {
    const response = await walletAPI.getWallet()
    console.log('钱包信息响应:', response)
    
    if (response.code === 200 && response.data) {
      walletInfo.totalIncome = parseFloat(response.data.totalIncome) || 0
      walletInfo.balance = parseFloat(response.data.balance) || 0
    } else {
      throw new Error(response.message || '获取钱包信息失败')
    }
  } catch (error: any) {
    console.error('加载钱包信息失败:', error)
    throw error
  }
}

// 加载账单
const loadBills = async () => {
  try {
    const response = await walletAPI.getBills({
      page: 1,
      size: 10
    })
    console.log('账单响应:', response)
    
    if (response.code === 200 && response.data) {
      billList.value = response.data.billList || []
      todayIncome.value = parseFloat(response.data.todayIncome) || 0
    } else {
      throw new Error(response.message || '获取账单失败')
    }
  } catch (error: any) {
    console.error('加载账单失败:', error)
    throw error
  }
}

// 处理返回
const handleBack = () => {
  router.back()
}

// 处理提现
const handleWithdraw = () => {
  if (walletInfo.balance <= 0) {
    Toast('无可提现余额')
    return
  }
  
  withdrawAmount.value = ''
  showWithdrawDialog.value = true
}

// 处理金额输入
const handleAmountInput = (event: Event) => {
  const input = event.target as HTMLInputElement
  let value = input.value
  
  // 只允许数字和小数点
  value = value.replace(/[^0-9.]/g, '')
  
  // 限制小数点后最多2位
  if (value.includes('.')) {
    const parts = value.split('.')
    value = parts[0] + '.' + parts[1].slice(0, 2)
  }
  
  // 限制最大值
  const maxAmount = walletInfo.balance
  if (Number(value) > maxAmount) {
    value = maxAmount.toFixed(2)
  }
  
  withdrawAmount.value = value
}

// 加载提现记录
const loadWithdrawals = async () => {
  try {
    const response = await walletAPI.getWithdrawals()
    console.log('提现记录响应:', response)
    
    if (response.code === 200 && response.data) {
      withdrawHistory.value = response.data.map((item: any) => ({
        id: item.withdrawalId,
        amount: parseFloat(item.amount) || 0,
        status: mapWithdrawalStatus(item.status),
        date: formatDate(item.createTime)
      }))
    } else {
      throw new Error(response.message || '获取提现记录失败')
    }
  } catch (error: any) {
    console.error('加载提现记录失败:', error)
    throw error
  }
}

// 映射提现状态
const mapWithdrawalStatus = (status: string) => {
  const statusMap: Record<string, string> = {
    '0': 'pending',    // 处理中
    '1': 'success',    // 成功
    '2': 'failed'      // 失败
  }
  return statusMap[status] || 'pending'
}

// 格式化日期
const formatDate = (dateString: string) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return date.toISOString().split('T')[0]
  } catch (e) {
    return dateString
  }
}

// 确认提现
const confirmWithdraw = async () => {
  const amount = Number(withdrawAmount.value)
  
  if (isNaN(amount) || amount <= 0) {
    Toast('请输入有效的提现金额')
    return
  }
  
  if (amount > walletInfo.balance) {
    Toast('提现金额不能超过可提现余额')
    return
  }
  
  if (useMockData.value) {
    // 模拟提现
    walletInfo.balance -= amount
    withdrawHistory.value.unshift({
      id: Date.now(),
      amount: amount,
      status: 'pending',
      date: new Date().toISOString().split('T')[0]
    })
    showWithdrawDialog.value = false
    Toast(`已提交${amount.toFixed(2)}元提现申请（演示）`)
    return
  }
  
  try {
    const response = await walletAPI.withdraw({ amount })
    
    if (response.code === 200) {
      // 更新钱包余额
      walletInfo.balance -= amount
      
      // 重新加载提现记录
      await loadWithdrawals()
      
      showWithdrawDialog.value = false
      Toast(`已提交${amount.toFixed(2)}元提现申请，请等待审核`)
    } else {
      Toast(response.message || '提现申请提交失败')
    }
  } catch (error: any) {
    console.error('提现失败:', error)
    Toast(error.message || '提现申请提交失败，请重试')
  }
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statuses: Record<string, string> = {
    success: '已到账',
    pending: '审核中',
    failed: '已失败'
  }
  return statuses[status] || status
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    success: '#07c160',
    pending: '#1989fa',
    failed: '#ee0a24'
  }
  return colors[status] || '#333'
}

// 格式化金额显示
const formatAmount = (amount: number) => {
  return amount.toFixed(2)
}

// 刷新数据
const handleRefresh = () => {
  loadWalletData()
}
</script>

<style scoped>
.wallet {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 关键修复：添加滚动容器 */
.wallet-scroll-container {
  height: calc(100vh - 50px);
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.wallet-content {
  padding: 15px;
  min-height: 100%;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.wallet-overview {
  background: linear-gradient(135deg, #1989fa, #07c160);
  border-radius: 16px;
  padding: 20px;
  color: white;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(25, 137, 250, 0.2);
}

.balance-card {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.balance-item {
  text-align: center;
}

.balance-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 5px;
}

.balance-value {
  font-size: 24px;
  font-weight: bold;
}

.balance-value.primary {
  font-size: 28px;
}

.withdraw-btn {
  border-radius: 10px;
  background-color: #fff;
  color: #1989fa;
  font-weight: bold;
  height: 50px;
  margin-top: 10px;
}

.today-income-section {
  margin-bottom: 15px;
}

.today-income-card {
  background: linear-gradient(135deg, #ff6b6b, #ffa726);
  border-radius: 10px;
  padding: 15px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.today-income-label {
  font-size: 16px;
  opacity: 0.9;
}

.today-income-value {
  font-size: 24px;
  font-weight: bold;
}

.bill-section,
.withdraw-section {
  background-color: #fff;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 15px;
}

.section-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 15px;
  font-weight: bold;
}

.bill-item,
.withdraw-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.bill-info,
.withdraw-info {
  flex: 1;
}

.bill-desc,
.withdraw-desc {
  font-size: 16px;
  color: #333;
  margin-bottom: 4px;
}

.bill-date,
.withdraw-date {
  font-size: 12px;
  color: #999;
}

.bill-amount.positive {
  color: #07c160;
  font-weight: bold;
  font-size: 16px;
}

.withdraw-amount.negative {
  font-weight: bold;
  font-size: 16px;
}

.status-tag {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  margin-left: 8px;
}

.empty-bill,
.empty-withdraw {
  min-height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.withdraw-dialog {
  padding: 20px;
}

.withdraw-dialog h3 {
  text-align: center;
  margin-bottom: 15px;
  font-size: 18px;
}

.available-balance {
  text-align: center;
  color: #666;
  margin-bottom: 20px;
}

.amount-input {
  display: flex;
  align-items: center;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 10px 15px;
  margin-bottom: 10px;
}

.currency-symbol {
  font-size: 18px;
  font-weight: bold;
  margin-right: 10px;
}

.amount-input input {
  border: none;
  outline: none;
  font-size: 18px;
  flex: 1;
}

.withdraw-hint {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin-bottom: 20px;
}

.withdraw-actions {
  display: flex;
  justify-content: space-between;
}

.withdraw-actions .van-button {
  width: 45%;
}
</style>