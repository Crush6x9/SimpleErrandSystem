<template>
  <div class="wallet">
    <van-nav-bar title="我的钱包" left-text="返回" left-arrow @click-left="handleBack">
      <template #right>
        <van-icon name="replay" size="18" @click="handleRefresh" />
      </template>
    </van-nav-bar>

    <div class="wallet-scroll-container">
      <div class="wallet-content">
        <van-loading v-if="loading" class="loading" type="spinner" size="24px">加载中...</van-loading>

        <div v-else>
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

            <van-button type="primary" size="large" class="withdraw-btn" @click="handleWithdraw"
              :disabled="walletInfo.balance <= 0">
              提现
            </van-button>
          </div>

          <div v-if="billResponse?.todayIncome > 0" class="today-income-section">
            <div class="today-income-card">
              <div class="today-income-label">今日收益</div>
              <div class="today-income-value">+¥{{ formatAmount(billResponse.todayIncome) }}</div>
            </div>
          </div>

          <div class="bill-section">
            <h2 class="section-title">账单明细</h2>

            <div v-for="bill in billResponse?.bills || []" :key="bill.orderId" class="bill-item">
              <div class="bill-info">
                <div class="bill-desc">{{ bill.title || '订单收入' }}</div>
                <div class="bill-date">{{ formatTime(bill.completeTime) }}</div>
              </div>
              <div class="bill-amount positive">+¥{{ formatAmount(bill.income) }}</div>
            </div>

            <div v-if="!billResponse?.bills?.length" class="empty-bill">
              <van-empty description="暂无账单记录" />
            </div>
          </div>

          <div class="withdraw-section">
            <h2 class="section-title">提现记录</h2>

            <div v-for="withdrawal in withdrawals" :key="withdrawal.withdrawalId" class="withdraw-item">
              <div class="withdraw-info">
                <div class="withdraw-desc">提现至微信</div>
                <div class="withdraw-date">{{ formatTime(withdrawal.createTime) }}</div>
              </div>
              <div class="withdraw-amount negative" :style="{ color: getStatusColor(withdrawal.status) }">
                -¥{{ formatAmount(withdrawal.amount) }}
                <span class="status-tag"
                  :style="{ backgroundColor: getStatusColor(withdrawal.status) + '20', color: getStatusColor(withdrawal.status) }">
                  {{ getStatusText(withdrawal.status) }}
                </span>
              </div>
            </div>

            <div v-if="!withdrawals?.length" class="empty-withdraw">
              <van-empty description="暂无提现记录" />
            </div>
          </div>
        </div>

        <div style="height: 60px;"></div>
      </div>
    </div>

    <van-popup v-model:show="showWithdrawDialog" position="bottom" class="popup" :style="{ height: '35%' }" round>
      <div class="withdraw-dialog">
        <h3>提现</h3>
        <p class="available-balance">可提现余额: ¥{{ formatAmount(walletInfo.balance) }}</p>

        <div class="amount-input">
          <span class="currency-symbol">¥</span>
          <input v-model="withdrawAmount" type="number" placeholder="请输入提现金额" inputmode="decimal"
            @input="handleAmountInput">
        </div>

        <p class="withdraw-hint">提现金额需为整数，且不超过可提现余额</p>

        <div class="withdraw-actions">
          <van-button type="default" @click="showWithdrawDialog = false">取消</van-button>
          <van-button type="primary" @click="confirmWithdraw"
            :disabled="!withdrawAmount || Number(withdrawAmount) <= 0">
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
import { isAuthenticated } from '@/utils/auth'
import { walletAPI } from '@/api'

const router = useRouter()

const walletInfo = reactive({
  walletId: 0,
  totalIncome: 0,
  balance: 0
})

const billResponse = ref<any>(null)

// 提现记录
const withdrawals = ref<any[]>([])

// 页面状态
const loading = ref(false)
const showWithdrawDialog = ref(false)
const withdrawAmount = ref('')

onMounted(() => {
  checkAuthStatus()
  loadWalletData()
})

// 检查认证状态
const checkAuthStatus = () => {
  if (!isAuthenticated()) {
    router.push({ name: 'Login', query: { redirect: '/wallet' } })
  }
}

const loadWalletData = async () => {
  try {
    loading.value = true

    await Promise.all([
      loadWalletInfo(),
      loadBills(),
      loadWithdrawals()
    ])

  } catch (error) {
    console.error('加载钱包数据失败:', error)
    Toast('加载钱包数据失败，请重试')
  } finally {
    loading.value = false
  }
}

const loadWalletInfo = async () => {
  try {
    const response = await walletAPI.getWallet()

    if (response.code === 200 && response.data) {
      walletInfo.walletId = response.data.walletId || 0
      walletInfo.totalIncome = parseFloat(response.data.totalIncome) || 0
      walletInfo.balance = parseFloat(response.data.balance) || 0
    } else {
      throw new Error(response.message || '获取钱包信息失败')
    }
  } catch (error: any) {
    console.error('加载钱包信息失败:', error)
    Toast(error.message || '获取钱包信息失败')
  }
}

const loadBills = async () => {
  try {
    const response = await walletAPI.getBills({
      page: 1,
      size: 10
    })

    if (response.code === 200 && response.data) {
      billResponse.value = response.data
    } else {
      throw new Error(response.message || '获取账单失败')
    }
  } catch (error: any) {
    console.error('加载账单失败:', error)
    Toast(error.message || '获取账单失败')
  }
}

const loadWithdrawals = async () => {
  try {
    const response = await walletAPI.getWithdrawals()

    if (response.code === 200 && response.data) {
      withdrawals.value = response.data
    } else {
      throw new Error(response.message || '获取提现记录失败')
    }
  } catch (error: any) {
    console.error('加载提现记录失败:', error)
    Toast(error.message || '获取提现记录失败')
  }
}

const handleBack = () => {
  router.back()
}

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

  value = value.replace(/[^0-9.]/g, '')

  if (value.includes('.')) {
    const parts = value.split('.')
    value = parts[0] + '.' + parts[1].slice(0, 2)
  }

  const maxAmount = walletInfo.balance
  if (Number(value) > maxAmount) {
    value = maxAmount.toFixed(2)
  }

  withdrawAmount.value = value
}

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

  Dialog.confirm({
    title: '确认提现',
    message: `确认提现 ¥${amount.toFixed(2)} 元？`,
  }).then(async () => {
    try {
      const response = await walletAPI.withdraw({ amount })

      if (response.code === 200) {
        // 更新钱包余额
        walletInfo.balance -= amount
        await loadWithdrawals()

        showWithdrawDialog.value = false
        Toast('提现申请已提交，请等待审核')
      } else {
        Toast(response.message || '提现申请提交失败')
      }
    } catch (error: any) {
      console.error('提现失败:', error)
      Toast(error.message || '提现申请提交失败，请重试')
    }
  }).catch(() => {
    // 用户取消
  })
}

const getStatusText = (status: string) => {
  const statuses: Record<string, string> = {
    '0': '审核中',
    '1': '已到账',
    '2': '已失败'
  }
  return statuses[status] || status
}

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    '0': '#1989fa',  // 审核中 - 蓝色
    '1': '#07c160',  // 已到账 - 绿色
    '2': '#ee0a24'   // 已失败 - 红色
  }
  return colors[status] || '#333'
}

// 格式化金额显示
const formatAmount = (amount: number | string) => {
  const num = typeof amount === 'string' ? parseFloat(amount) : amount
  return isNaN(num) ? '0.00' : num.toFixed(2)
}

const formatTime = (timeString: string) => {
  if (!timeString) return ''
  try {
    const date = new Date(timeString)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')

    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (e) {
    return timeString
  }
}

const handleRefresh = () => {
  loadWalletData()
  Toast('刷新成功')
}
</script>

<style scoped>
.wallet {
  min-height: 100vh;
  background-color: #f5f5f5;
}

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

.withdraw-btn:disabled {
  opacity: 0.6;
  background-color: #ccc;
  color: #666;
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

.bill-item:last-child,
.withdraw-item:last-child {
  border-bottom: none;
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
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.status-tag {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  margin-top: 4px;
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
  font-weight: bold;
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
  text-align: right;
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