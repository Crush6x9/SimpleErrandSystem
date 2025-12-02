<template>
  <div class="my">
    <van-nav-bar title="我的" left-text="返回" left-arrow @click-left="handleBack" />
    <!-- 联系客服按钮 -->
    <div class="contact-btn" @click="handleContactClick">
      <van-icon name="service-o" size="24" />
    </div>

    <div class="user-section">
      <div class="avatar">
        <img :src="user.avatarUrl || '/default-profile-photo.png'" @click="handleAvatarClick" alt="头像" />
      </div>
      <div class="user-info">
        <div class="user-name" @click.stop="handleNicknameClick">{{ user.username || '用户' }}</div>
        <div class="user-id" @click.stop="handleStudentIdClick">
          学号: {{ user.studentId || '未设置' }}
        </div>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="data-stats">
      <div class="stat-item" @click="handleDataItemClick('receivedOrders')">
        <div class="stat-value">{{ stats.myAcceptedOrders || 0 }}</div>
        <div class="stat-label">已接订单</div>
      </div>
      <div class="stat-item" @click="handleDataItemClick('postedOrders')">
        <div class="stat-value">{{ stats.myPublishedOrders || 0 }}</div>
        <div class="stat-label">已发订单</div>
      </div>
      <div class="stat-item--earning" @click="handleDataItemClick('earnings')">
        <div class="stat-value">¥{{ (walletInfo?.balance || 0).toFixed(2) }}</div>
        <div class="stat-label">收益 ></div>
      </div>
    </div>

    <!-- 评价区域 -->
    <div class="rating-section">
      <div class="rating-item--good">
        <van-image width="150px" height="150px" src="/my-good.png" class="rating-img" />
        <i class="rating-value">{{ evaluationStats.goodReviews || 0 }}</i>
        <i class="rating-label">好评</i>
      </div>
      <div class="rating-item--bad">
        <van-image width="150px" src="/my-bad.png" class="rating-img" />
        <i class="rating-value">{{ evaluationStats.badReviews || 0 }}</i>
        <i class="rating-label">差评</i>
      </div>
    </div>

    <van-popup v-model:show="showNicknameEdit" position="bottom" :style="{ height: '30%' }" round class="popup">
      <div class="nickname-edit">
        <h3>修改昵称</h3>
        <van-field v-model="newNickname" placeholder="请输入新昵称" maxlength="10" clearable />
        <div class="edit-actions">
          <van-button type="default" @click="showNicknameEdit = false">取消</van-button>
          <van-button type="primary" @click="saveNickname">确定</van-button>
        </div>
      </div>
    </van-popup>

    <van-dialog v-model:show="showContactDialog" title="联系客服" :show-confirm-button="false"
      :close-on-click-overlay="true">
      <div class="contact-dialog">
        <p>请扫描下方二维码添加客服微信：</p>
        <img src="/wechat-qr.png" class="qr-code" alt="客服微信二维码" />
        <p class="wechat-id">微信号：runner_service</p>
      </div>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Toast } from 'vant'
import { isAuthenticated, getUserId } from '@/utils/auth'
import { getUser, updateUser, loadUserFromServer, updateUsername } from '@/utils/user'
import { evaluationAPI, orderAPI, walletAPI } from '@/api'

const router = useRouter()
const isAuth = ref(false)
const user = ref(getUser())
const showNicknameEdit = ref(false)
const newNickname = ref('')
const showContactDialog = ref(false)

// 统计数据
const stats = reactive({
  myAcceptedOrders: 0,
  myPublishedOrders: 0
})

const evaluationStats = reactive({
  goodReviews: 0,
  badReviews: 0
})

const walletInfo = ref<any>(null)

onMounted(() => {
  checkAuthStatus()
  loadUserData()
  loadStats()
})

const checkAuthStatus = () => {
  isAuth.value = isAuthenticated()
  if (!isAuth.value) {
    router.push({ name: 'Login', query: { redirect: '/my' } })
  }
}

const handleBack = () => {
  router.back()
}

const loadUserData = async () => {
  try {
    if (isAuthenticated()) {
      const userData = await loadUserFromServer()
      user.value = userData
    }
  } catch (error) {
    Toast('加载用户数据失败')
  }
}

const loadStats = async () => {
  try {
    await loadOrderStats()
    await loadEvaluationStats()
    await loadWalletInfo()
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadOrderStats = async () => {
  try {
    const response = await orderAPI.getStats()
    if (response.code === 200 && response.data) {
      stats.myAcceptedOrders = response.data.myAcceptedOrders || 0
      stats.myPublishedOrders = response.data.myPublishedOrders || 0
    }
  } catch (error) {
    console.error('加载订单统计失败:', error)
  }
}

const loadEvaluationStats = async () => {
  try {
    const response = await evaluationAPI.getHelperEvaluationStats()
    if (response.code === 200 && response.data) {
      evaluationStats.goodReviews = response.data.goodReviews || 0
      evaluationStats.badReviews = response.data.badReviews || 0
    }
  } catch (error) {
    console.error('加载评价统计失败:', error)
  }
}

const loadWalletInfo = async () => {
  try {
    const response = await walletAPI.getWallet()
    if (response.code === 200 && response.data) {
      walletInfo.value = response.data
    }
  } catch (error) {
    console.error('加载钱包信息失败:', error)
  }
}

const handleAvatarClick = () => {
  router.push({ name: 'Profile' })
}

const handleNicknameClick = () => {
  newNickname.value = user.value.username || ''
  showNicknameEdit.value = true
}

const saveNickname = async () => {
  if (!newNickname.value.trim()) {
    Toast('昵称不能为空')
    return
  }

  try {
    const userData = await updateUsername(newNickname.value.trim())
    user.value = userData
    showNicknameEdit.value = false
    Toast('昵称更新成功')
  } catch (error: any) {
    Toast(error.message || '昵称更新失败')
  }
}

const handleStudentIdClick = () => {
  if (user.value.studentId) {
    navigator.clipboard.writeText(user.value.studentId)
    Toast('学号已复制到剪贴板')
  }
}

const handleDataItemClick = (type: string) => {
  if (type === 'earnings') {
    router.push({ name: 'Wallet' })
  } else if (type === 'receivedOrders') {
    router.push({
      path: '/help',
      query: { tab: 'helping' }
    })
  } else if (type === 'postedOrders') {
    router.push({
      path: '/help',
      query: { tab: 'mine' }
    })
  }
}

const handleContactClick = () => {
  showContactDialog.value = true
}
</script>

<style scoped>
.my {
  min-height: 100vh;
}

.contact-btn {
  position: absolute;
  right: 16px;
  top: 12px;
  z-index: 10;
}

.user-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 20px;
  background-color: #fff;
}

.avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #333;
  background-color: #f5f5f5;
  margin-bottom: 10px;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.user-id {
  font-size: 14px;
  color: #666;
  background-color: #f5f5f5;
  padding: 3px 8px;
  border-radius: 4px;
  display: inline-block;
}

.data-stats {
  display: flex;
  background-color: #fff;
  padding: 5px;
  margin-bottom: 10px;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 1px;
  height: 120px;
  background-color: #ffd36a;
  color: #fff;
}

.stat-item--earning {
  flex: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 1px;
  height: 120px;
  background-color: #67d47e;
  color: #fff;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 16px;
}

.rating-section {
  display: flex;
  margin: 20px 0;
}

.rating-item--good,
.rating-item--bad {
  height: 380px;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
  border-radius: 20px;
  font-family: STHupo;
  color: #fff;
  margin: 0 1px;
}

.rating-item--good {
  background-color: #ffd36a;
}

.rating-item--bad {
  background-color: #ff5575;
}

.rating-img {
  margin: 20px 0 0 30px;
}

.rating-label {
  font-size: 70px;
  margin: 10px 0;
}

.rating-value {
  font-size: 80px;
  margin: 10px 0 0;
}

.nickname-edit {
  padding: 20px;
  height: 100%;
}

.nickname-edit h3 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 18px;
}

.edit-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.edit-actions .van-button {
  width: 45%;
}

.contact-dialog {
  text-align: center;
  padding: 15px;
}

.qr-code {
  width: 200px;
  margin: 10px auto;
  border: 1px solid #eee;
  border-radius: 8px;
}

.wechat-id {
  color: #333;
  font-weight: bold;
  margin-top: 10px;
}
</style>