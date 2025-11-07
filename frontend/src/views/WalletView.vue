<!-- src/views/WalletView.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Toast, Dialog } from 'vant'
import { isAuthenticated } from '@/utils/auth'
import { getUser, updateUser } from '@/utils/user'

const router = useRouter()
const isAuth = ref(false)
const user = ref(getUser())
const showWithdrawDialog = ref(false)
const withdrawAmount = ref('')
const withdrawHistory = ref([
  { id: 1, amount: 50.00, status: 'success', date: '2023-06-15' },
  { id: 2, amount: 30.00, status: 'pending', date: '2023-06-10' },
  { id: 3, amount: 20.00, status: 'success', date: '2023-06-05' }
])

onMounted(() => {
  checkAuthStatus()
  loadWalletData()
})

// 检查认证状态
const checkAuthStatus = () => {
  isAuth.value = isAuthenticated()
  if (!isAuth.value) {
    router.push({ name: 'Login', query: { redirect: '/wallet' } })
  }
}

// 加载钱包数据
const loadWalletData = async () => {
  try {
    // 模拟API调用
    console.log('调用获取钱包数据API')
    
    // 模拟异步请求
    await new Promise((resolve) => setTimeout(resolve, 300))
    
    user.value = getUser()
  } catch (error) {
    Toast('加载钱包数据失败')
  }
}

// 处理返回
const handleBack = () => {
  router.back()
}

// 处理提现
const handleWithdraw = () => {
  if (user.value.withdrawableBalance <= 0) {
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
  const maxAmount = user.value.withdrawableBalance
  if (Number(value) > maxAmount) {
    value = maxAmount.toFixed(2)
  }
  
  withdrawAmount.value = value
}

// 确认提现
const confirmWithdraw = async () => {
  const amount = Number(withdrawAmount.value)
  
  if (isNaN(amount) || amount <= 0) {
    Toast('请输入有效的提现金额')
    return
  }
  
  if (amount > user.value.withdrawableBalance) {
    Toast('提现金额不能超过可提现余额')
    return
  }
  
  try {
    // 模拟API调用
    console.log('调用提现API:', { amount })
    
    // 模拟异步请求
    await new Promise((resolve) => setTimeout(resolve, 500))
    
    // 更新用户数据
    const newBalance = user.value.withdrawableBalance - amount
    const updatedUser = updateUser({ 
      withdrawableBalance: newBalance,
      totalEarnings: user.value.totalEarnings
    })
    
    // 添加提现记录
    withdrawHistory.value.unshift({
      id: Date.now(),
      amount,
      status: 'pending',
      date: new Date().toISOString().split('T')[0]
    })
    
    user.value = updatedUser
    showWithdrawDialog.value = false
    Toast(`已提交${amount.toFixed(2)}元提现申请，请等待审核`)
  } catch (error) {
    Toast('提现申请提交失败，请重试')
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
</script>

<template>
  <div class="wallet">
        <van-nav-bar 
      title="我的钱包" 
      left-text="返回" 
      left-arrow 
      @click-left="handleBack" 
    />
    <!-- 添加滚动容器 -->
    <div class="wallet-scroll-container">
      <div class="wallet-content">
        <!-- 钱包概览 -->
        <div class="wallet-overview">
          <div class="balance-card">
            <div class="balance-item">
              <div class="balance-label">总收入</div>
              <div class="balance-value">¥{{ user.totalEarnings.toFixed(2) }}</div>
            </div>
            <div class="balance-item">
              <div class="balance-label">可提现余额</div>
              <div class="balance-value primary">¥{{ user.withdrawableBalance.toFixed(2) }}</div>
            </div>
          </div>
          
          <van-button 
            type="primary" 
            size="large" 
            class="withdraw-btn"
            @click="handleWithdraw"
            :disabled="user.withdrawableBalance <= 0"
          >
            提现
          </van-button>
        </div>
        
        <!-- 账单明细 -->
        <div class="bill-section">
          <h2 class="section-title">账单明细</h2>
          
          <div v-if="user.earnings > 0" class="bill-item">
            <div class="bill-info">
              <div class="bill-desc">今日收益</div>
              <div class="bill-date">刚刚</div>
            </div>
            <div class="bill-amount">+¥{{ user.earnings.toFixed(2) }}</div>
          </div>
          
          <div class="bill-item">
            <div class="bill-info">
              <div class="bill-desc">接单收入</div>
              <div class="bill-date">2023-06-20</div>
            </div>
            <div class="bill-amount">+¥50.00</div>
          </div>
          
          <div class="bill-item">
            <div class="bill-info">
              <div class="bill-desc">接单收入</div>
              <div class="bill-date">2023-06-18</div>
            </div>
            <div class="bill-amount">+¥30.00</div>
          </div>
          
          <div class="bill-item">
            <div class="bill-info">
              <div class="bill-desc">接单收入</div>
              <div class="bill-date">2023-06-15</div>
            </div>
            <div class="bill-amount">+¥20.50</div>
          </div>
          
          <div v-if="user.earnings === 0" class="empty-bill">
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
            <div class="withdraw-amount" :style="{ color: getStatusColor(item.status) }">
              -¥{{ item.amount.toFixed(2) }}
              <span class="status-tag" :style="{ backgroundColor: getStatusColor(item.status) + '20', color: getStatusColor(item.status) }">
                {{ getStatusText(item.status) }}
              </span>
            </div>
          </div>
          
          <div v-if="withdrawHistory.length === 0" class="empty-withdraw">
            <van-empty description="暂无提现记录" />
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
        <p class="available-balance">可提现余额: ¥{{ user.withdrawableBalance.toFixed(2) }}</p>
        
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
          <van-button type="primary" @click="confirmWithdraw">确认提现</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<style scoped>
.wallet {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 关键修复：添加滚动容器 */
.wallet-scroll-container {
  height: calc(100vh - 50px); /* 减去导航栏高度 */
  overflow-y: auto;
  -webkit-overflow-scrolling: touch; /* 平滑滚动 */
}

.wallet-content {
  padding: 15px;
  min-height: 100%;
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

.bill-section, .withdraw-section {
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

.bill-item, .withdraw-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.bill-info, .withdraw-info {
  flex: 1;
}

.bill-desc, .withdraw-desc {
  font-size: 16px;
  color: #333;
  margin-bottom: 4px;
}

.bill-date, .withdraw-date {
  font-size: 12px;
  color: #999;
}

.bill-amount, .withdraw-amount {
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

.empty-bill, .empty-withdraw {
  min-height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>