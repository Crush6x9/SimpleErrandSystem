<template>
  <div class="my">
    <van-nav-bar title="我的" left-text="返回" left-arrow @click-left="handleBack" />
    <!-- 联系客服按钮 -->
    <div class="contact-btn" @click="handleContactClick">
      <van-icon name="service-o" size="24" />
    </div>

    <div class="user-section">
      <div class="avatar">
        <img :src="user.avatar || user.avatarUrl || '/default-profile-photo.png'" @click="handleAvatarClick" alt="头像" />
      </div>
      <div class="user-info">
        <div class="user-name" @click.stop="handleNicknameClick">{{ user.nickname || user.username || '用户' }}</div>
        <div class="user-id" @click.stop="handleStudentIdClick">
          学号: {{ user.studentId || '未设置' }}
        </div>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="data-stats">
      <div class="stat-item" @click="handleDataItemClick('receivedOrders')">
        <div class="stat-value">{{ user.receivedOrders || 0 }}</div>
        <div class="stat-label">已接订单</div>
      </div>
      <div class="stat-item" @click="handleDataItemClick('postedOrders')">
        <div class="stat-value">{{ user.postedOrders || 0 }}</div>
        <div class="stat-label">已发订单</div>
      </div>
      <div class="stat-item--earning" @click="handleDataItemClick('earnings')">
        <div class="stat-value">¥{{ (user.earnings || 0).toFixed(2) }}</div>
        <div class="stat-label">收益 ></div>
      </div>
    </div>

    <!-- 评价区域 -->
    <div class="rating-section">
      <div class="rating-item--good">
        <van-image width="150px" height="150px" src="/my-good.png" class="rating-img" />
        <i class="rating-value">{{ user.好评 || 0 }}</i>
        <i class="rating-label">好评</i>
      </div>
      <div class="rating-item--bad">
        <van-image width="150px" src="/my-bad.png" class="rating-img" />
        <i class="rating-value">{{ user.差评 || 0 }}</i>
        <i class="rating-label">差评</i>
      </div>
    </div>

    <!-- 昵称编辑浮窗 -->
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

    <!-- 客服联系浮窗 -->
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Toast, Dialog, ImagePreview } from 'vant'
import { isAuthenticated, getUserPhone, getUserId } from '@/utils/auth'
import { getUser, updateUser, loadUserFromServer } from '@/utils/user'
import { evaluationAPI, orderAPI, userAPI } from '@/api'

const router = useRouter()
const isAuth = ref(false)
const userPhone = ref('')
const user = ref(getUser())
const showNicknameEdit = ref(false)
const newNickname = ref('')
const showContactDialog = ref(false)

onMounted(() => {
  checkAuthStatus()
  loadUserData()
})

// 检查认证状态
const checkAuthStatus = () => {
  isAuth.value = isAuthenticated()
  if (isAuth.value) {
    userPhone.value = getUserPhone() || ''
  } else {
    router.push({ name: 'Login', query: { redirect: '/my' } })
  }
}

// 处理返回
const handleBack = () => {
  router.back()
}

// 加载用户数据
const loadUserData = async () => {
  try {
    const userId = getUserId()
    if (userId) {
      // 从服务器加载用户数据
      const userData = await loadUserFromServer(userId)
      user.value = userData
      
      // 加载订单统计
      await loadOrderStats(userId)
      // 加载评价统计
      await loadEvaluationStats(userId)
    } else {
      user.value = getUser()
    }
  } catch (error) {
    Toast('加载用户数据失败')
  }
}

// 加载订单统计
const loadOrderStats = async (userId: string) => {
  try {
    const response = await orderAPI.getStats()
    if (response.data) {
      const updatedUser = updateUser({
        receivedOrders: response.data.myAcceptedOrders || 0,
        postedOrders: response.data.myPublishedOrders || 0
      })
      user.value = updatedUser
    }
  } catch (error) {
    console.error('加载订单统计失败:', error)
  }
}

// 加载评价统计
const loadEvaluationStats = async (userId: string) => {
  try {
    const response = await evaluationAPI.getStats(userId)
    if (response.data) {
      const updatedUser = updateUser({
        好评: response.data.goodReviews || 0,
        差评: response.data.badReviews || 0
      })
      user.value = updatedUser
    }
  } catch (error) {
    console.error('加载评价统计失败:', error)
  }
}

// 处理头像点击
const handleAvatarClick = () => {
  router.push({ name: 'Profile' })
}

// 处理昵称点击
const handleNicknameClick = () => {
  newNickname.value = user.value.nickname || user.value.username || ''
  showNicknameEdit.value = true
}

// 保存昵称
const saveNickname = async () => {
  if (!newNickname.value.trim()) {
    Toast('昵称不能为空')
    return
  }

  try {
    const userId = getUserId()
    if (userId) {
      const response = await userAPI.updateUsername(userId, newNickname.value.trim())
      if (response.code === 200) {
        const updatedUser = updateUser({ 
          nickname: newNickname.value.trim(),
          username: newNickname.value.trim()
        })
        user.value = updatedUser
        showNicknameEdit.value = false
        Toast('昵称更新成功')
      }
    }
  } catch (error) {
    Toast('昵称更新失败')
  }
}

// 处理学号点击
const handleStudentIdClick = () => {
  if (user.value.studentId) {
    Toast('学号已复制到剪贴板')
    navigator.clipboard.writeText(user.value.studentId)
  }
}

// 处理数据项点击
const handleDataItemClick = (type: string) => {
  if (type === 'earnings') {
    router.push({ name: 'Wallet' })
  } else if (type === 'receivedOrders') {
    // 跳转到全部订单页面，并指定显示"我帮助的"标签页
    router.push({
      path: '/help',
      query: { tab: 'helping' }
    })
  } else if (type === 'postedOrders') {
    // 跳转到全部订单页面，并指定显示"我发布的"标签页
    router.push({
      path: '/help',
      query: { tab: 'mine' }
    })
  } else {
    Toast(`查看${type}详情`)
  }
}

// 处理联系客服
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