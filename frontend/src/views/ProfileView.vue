<!-- 个人信息页面  -->
<!-- src/views/ProfileView.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Toast, Uploader, Dialog } from 'vant'
import { isAuthenticated } from '@/utils/auth'
import { getUser, updateUser } from '@/utils/user'

const router = useRouter()
const isAuth = ref(false)
const user = ref(getUser())
const newStudentId = ref('')
const showStudentIdEdit = ref(false)
const newProof = ref('')
const isUploading = ref(false)
const avatarFile = ref<File | null>(null)

onMounted(() => {
  checkAuthStatus()
  // 模拟加载用户数据
  loadUserData()
})

// 检查认证状态
const checkAuthStatus = () => {
  isAuth.value = isAuthenticated()
  if (!isAuth.value) {
    router.push({ name: 'Login', query: { redirect: '/profile' } })
  }
}

// 加载用户数据
const loadUserData = async () => {
  try {
    // 模拟API调用
    console.log('调用获取用户数据API')
    
    // 模拟异步请求
    await new Promise((resolve) => setTimeout(resolve, 300))
    
    user.value = getUser()
  } catch (error) {
    Toast('加载用户数据失败')
  }
}

// 处理返回
const handleBack = () => {
  router.back()
}

// 处理头像上传
const handleAvatarUpload = (file: File) => {
  avatarFile.value = file
  isUploading.value = true
  
  // 模拟上传过程
  setTimeout(() => {
    // 生成预览URL
    const previewUrl = URL.createObjectURL(file)
    
    // 更新用户头像
    const updatedUser = updateUser({ avatar: previewUrl })
    user.value = updatedUser
    
    isUploading.value = false
    Toast('头像上传成功')
  }, 800)
}

// 处理学号点击
const handleStudentIdClick = () => {
  newStudentId.value = user.value.studentId
  showStudentIdEdit.value = true
}

// 保存学号
const saveStudentId = () => {
  if (!newStudentId.value.trim()) {
    Toast('学号不能为空')
    return
  }
  
  // 验证学号格式（简单验证）
  if (!/^\d{8,12}$/.test(newStudentId.value.trim())) {
    Toast('请输入有效的学号')
    return
  }
  
  const updatedUser = updateUser({ studentId: newStudentId.value.trim() })
  user.value = updatedUser
  showStudentIdEdit.value = false
  Toast('学号更新成功')
}

// 处理证明上传
const handleProofUpload = (file: File) => {
  newProof.value = URL.createObjectURL(file)
  Toast('证明文件上传成功')
}

// 提交修改
const handleSubmit = () => {
  Dialog.confirm({
    title: '确认提交',
    message: '确认提交个人信息修改？',
  }).then(() => {
    // 模拟提交
    Toast('个人信息提交成功')
    router.back()
  }).catch(() => {
    // on cancel
  })
}

// 取消修改
const handleCancel = () => {
  Dialog.confirm({
    title: '确认取消',
    message: '取消将不会保存修改，确认取消？',
  }).then(() => {
    router.back()
  }).catch(() => {
    // on cancel
  })
}
</script>

<template>
  <div class="profile">
    <van-nav-bar 
      title="我的信息" 
      left-text="返回" 
      left-arrow 
      @click-left="handleBack" 
    />
    
    <div class="profile-content">
      <!-- 头像区域 -->
      <van-cell-group inset>
        <van-cell title="头像" :border="false">
          <template #right-icon>
            <van-uploader 
              :after-read="handleAvatarUpload" 
              :max-count="1" 
              :max-size="2 * 1024 * 1024"
              @oversize="() => Toast('头像大小不能超过2MB')"
            >
              <div class="avatar-upload">
                <img :src="user.avatar" alt="头像" />
                <div v-if="isUploading" class="uploading-mask">
                  <van-loading size="20" />
                </div>
              </div>
            </van-uploader>
          </template>
        </van-cell>
      </van-cell-group>
      
      <!-- 个人信息 -->
      <van-cell-group inset>
        <van-cell 
          title="昵称" 
          :value="user.nickname" 
          is-link 
          @click="() => $router.push({ name: 'My' })"
        />
        <van-cell 
          title="学号" 
          :value="user.studentId" 
          is-link 
          @click="handleStudentIdClick"
        />
        <van-cell 
          title="手机号" 
          :value="user.phone || '未绑定'" 
          is-link 
          :border="false"
        />
      </van-cell-group>
      
      <!-- 上传证明 -->
      <van-cell-group inset>
        <van-cell title="上传学生证明" :border="false">
          <template #right-icon>
            <van-uploader 
              :after-read="(file: any) => handleProofUpload(file.file)" 
              :max-count="1" 
              :max-size="5 * 1024 * 1024"
              @oversize="() => Toast('证明文件大小不能超过5MB')"
            >
              <van-button type="primary" size="small">选择文件</van-button>
            </van-uploader>
          </template>
        </van-cell>
        <div v-if="newProof" class="proof-preview">
          <img :src="newProof" alt="学生证明" />
        </div>
      </van-cell-group>
      
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <van-button type="primary" size="large" @click="handleSubmit">提交</van-button>
        <van-button type="default" size="large" @click="handleCancel">取消</van-button>
      </div>
    </div>
    
    <!-- 学号编辑浮窗 -->
    <van-popup 
      v-model:show="showStudentIdEdit" 
      position="bottom" 
      :style="{ height: '30%' }"
      round
    >
      <div class="student-id-edit">
        <h3>修改学号</h3>
        <van-field 
          v-model="newStudentId" 
          placeholder="请输入学号" 
          maxlength="12"
          clearable
        />
        <p class="hint">请输入8-12位数字的学号</p>
        <div class="edit-actions">
          <van-button type="default" @click="showStudentIdEdit = false">取消</van-button>
          <van-button type="primary" @click="saveStudentId">确定</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<style scoped>
.profile {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.profile-content {
  padding: 15px;
}

.avatar-upload {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-upload img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.uploading-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.proof-preview {
  padding: 10px 15px;
}

.proof-preview img {
  width: 100%;
  max-height: 200px;
  object-fit: contain;
  border-radius: 8px;
  background-color: #f5f5f5;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 20px;
}

.student-id-edit {
  padding: 20px;
  height: 100%;
}

.student-id-edit h3 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 18px;
}

.hint {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
  text-align: center;
}

.edit-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.edit-actions .van-button {
  width: 45%;
}
</style>