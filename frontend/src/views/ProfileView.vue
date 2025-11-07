<!-- 个人信息页面  -->
<!-- src/views/ProfileView.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Toast, Uploader, Dialog } from 'vant'
import { isAuthenticated, getUserPhone } from '@/utils/auth'
import { getUser, updateUser } from '@/utils/user'

const router = useRouter()
const isAuth = ref(false)
const user = ref(getUser())
const newStudentId = ref('')
const showStudentIdEdit = ref(false)
const newPhone = ref('')
const showPhoneEdit = ref(false)
const newNickname = ref('')
const showNicknameEdit = ref(false)
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
    // 初始化表单数据
    newPhone.value = user.value.phone || getUserPhone() || ''
  } catch (error) {
    Toast('加载用户数据失败')
  }
}

// 处理返回
const handleBack = () => {
  router.back()
}


// 处理头像上传
const handleAvatarUpload = (file: any) => {
  // Vant Uploader 的 after-read 返回的是包含 file 属性的对象
  if (file && file.file) {
    avatarFile.value = file.file
    isUploading.value = true
    
    const reader = new FileReader()
    reader.onload = (e) => {
      const base64String = e.target?.result as string
      
      // 更新用户头像
      const updatedUser = updateUser({ avatar: base64String })
      user.value = updatedUser
      
      isUploading.value = false
      Toast('头像上传成功')
    }
    reader.onerror = () => {
      isUploading.value = false
      Toast('头像上传失败，请重试')
    }
    reader.readAsDataURL(file.file)
  } else {
    Toast('文件上传失败，请重试')
  }
}


// 处理昵称点击
const handleNicknameClick = () => {
  newNickname.value = user.value.nickname
  showNicknameEdit.value = true
}

// 保存昵称
const saveNickname = () => {
  if (!newNickname.value.trim()) {
    Toast('昵称不能为空')
    return
  }
  
  const updatedUser = updateUser({ nickname: newNickname.value.trim() })
  user.value = updatedUser
  showNicknameEdit.value = false
  Toast('昵称更新成功')
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

// 处理手机号点击
const handlePhoneClick = () => {
  newPhone.value = user.value.phone || getUserPhone() || ''
  showPhoneEdit.value = true
}

// 保存手机号
const savePhone = () => {
  if (!newPhone.value.trim()) {
    Toast('手机号不能为空')
    return
  }
  
  // 验证手机号格式
  if (!/^1[3-9]\d{9}$/.test(newPhone.value.trim())) {
    Toast('请输入有效的手机号')
    return
  }
  
  const updatedUser = updateUser({ phone: newPhone.value.trim() })
  user.value = updatedUser
  showPhoneEdit.value = false
  Toast('手机号更新成功')
}

// 处理证明上传
// const handleProofUpload = (file: any) => {
//   // Vant Uploader 的 after-read 返回的是包含 file 属性的对象
//   if (file && file.file) {
//     const reader = new FileReader()
//     reader.onload = (e) => {
//       const base64String = e.target?.result as string
//       newProof.value = base64String
      
//       // 保存证明到用户数据
//       const updatedUser = updateUser({ proof: base64String })
//       user.value = updatedUser
      
//       Toast('证明文件上传成功')
//     }
//     reader.onerror = () => {
//       Toast('证明文件上传失败，请重试')
//     }
//     reader.readAsDataURL(file.file)
//   } else {
//     Toast('文件上传失败，请重试')
//   }
// }
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
    // 保存所有修改到用户数据
    const updatedData = {
      avatar: user.value.avatar,
      nickname: user.value.nickname,
      studentId: user.value.studentId,
      phone: user.value.phone,
      proof: newProof.value || user.value.proof
    }
    
    updateUser(updatedData)
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
    @click="handleNicknameClick"
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
    @click="handlePhoneClick"
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
      class="popup"
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
    <!-- 昵称编辑浮窗 -->
<van-popup 
  v-model:show="showNicknameEdit" 
  position="bottom" 
  :style="{ height: '30%' }"
  round
  class="popup"
>
  <div class="nickname-edit">
    <h3>修改昵称</h3>
    <van-field 
      v-model="newNickname" 
      placeholder="请输入新昵称" 
      maxlength="10"
      clearable
    />
    <p class="hint">昵称最多10个字符</p>
    <div class="edit-actions">
      <van-button type="default" @click="showNicknameEdit = false">取消</van-button>
      <van-button type="primary" @click="saveNickname">确定</van-button>
    </div>
  </div>
</van-popup>

<!-- 手机号编辑浮窗 -->
<van-popup 
  v-model:show="showPhoneEdit" 
  position="bottom" 
  :style="{ height: '30%' }"
  round
  class="popup"
>
  <div class="phone-edit">
    <h3>绑定手机号</h3>
    <van-field 
      v-model="newPhone" 
      type="tel"
      placeholder="请输入手机号" 
      maxlength="11"
      clearable
    />
    <p class="hint">请输入11位手机号码</p>
    <div class="edit-actions">
      <van-button type="default" @click="showPhoneEdit = false">取消</van-button>
      <van-button type="primary" @click="savePhone">确定</van-button>
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
.phone-edit {
  padding: 20px;
  height: 100%;
}

.phone-edit h3 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 18px;
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
</style>