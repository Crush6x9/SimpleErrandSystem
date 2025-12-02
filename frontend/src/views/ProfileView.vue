<!-- 个人信息页面  -->
<template>
  <div class="profile">
    <van-nav-bar title="我的信息" left-text="返回" left-arrow @click-left="handleBack" />

    <div class="profile-content">
      <van-cell-group inset>
        <van-cell title="头像" :border="false">
          <template #right-icon>
            <van-uploader :after-read="handleAvatarUpload" :max-count="1" :max-size="5 * 1024 * 1024"
              @oversize="() => Toast('头像大小不能超过5MB')">
              <div class="avatar-upload">
                <img :src="user.avatarUrl || '/default-profile-photo.png'" alt="头像" />
                <div v-if="isUploading" class="uploading-mask">
                  <van-loading size="20" />
                </div>
              </div>
            </van-uploader>
          </template>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset>
        <van-cell title="昵称" :value="user.username || '未设置'" is-link @click="handleNicknameClick" />
        <van-cell title="学号" :value="user.studentId || '未设置'" is-link @click="handleStudentIdClick" />
        <van-cell title="手机号" :value="user.phone || '未绑定'" is-link :border="false" @click="handlePhoneClick" />
      </van-cell-group>

      <van-cell-group inset v-if="user.role === '0'">
        <van-cell title="上传学生证明" :border="false">
          <template #right-icon>
            <van-uploader :after-read="handleProofUpload" :max-count="1" :max-size="5 * 1024 * 1024"
              @oversize="() => Toast('证明文件大小不能超过5MB')">
              <van-button type="primary" size="small">选择文件</van-button>
            </van-uploader>
          </template>
        </van-cell>
        <div v-if="newProof" class="proof-preview">
          <img :src="newProof" alt="学生证明" />
        </div>
      </van-cell-group>

      <div class="action-buttons">
        <van-button type="primary" size="large" @click="handleSubmit">提交</van-button>
        <van-button type="default" size="large" @click="handleCancel">取消</van-button>
      </div>
    </div>

    <van-popup v-model:show="showStudentIdEdit" position="bottom" :style="{ height: '30%' }" round class="popup">
      <div class="student-id-edit">
        <h3>修改学号</h3>
        <van-field v-model="newStudentId" placeholder="请输入学号" maxlength="12" clearable />
        <p class="hint">请输入8-12位数字的学号</p>
        <div class="edit-actions">
          <van-button type="default" @click="showStudentIdEdit = false">取消</van-button>
          <van-button type="primary" @click="saveStudentId">确定</van-button>
        </div>
      </div>
    </van-popup>

    <van-popup v-model:show="showNicknameEdit" position="bottom" :style="{ height: '30%' }" round class="popup">
      <div class="nickname-edit">
        <h3>修改昵称</h3>
        <van-field v-model="newNickname" placeholder="请输入新昵称" maxlength="10" clearable />
        <p class="hint">昵称最多10个字符</p>
        <div class="edit-actions">
          <van-button type="default" @click="showNicknameEdit = false">取消</van-button>
          <van-button type="primary" @click="saveNickname">确定</van-button>
        </div>
      </div>
    </van-popup>

    <van-popup v-model:show="showPhoneEdit" position="bottom" :style="{ height: '30%' }" round class="popup">
      <div class="phone-edit">
        <h3>绑定手机号</h3>
        <van-field v-model="newPhone" type="tel" placeholder="请输入手机号" maxlength="11" clearable />
        <p class="hint">请输入11位手机号码</p>
        <div class="edit-actions">
          <van-button type="default" @click="showPhoneEdit = false">取消</van-button>
          <van-button type="primary" @click="savePhone">确定</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Toast, Dialog } from 'vant'
import { isAuthenticated, getUserPhone } from '@/utils/auth'
import { getUser, updateUser, loadUserFromServer, updateUsername, updateUserInfo, uploadAvatar } from '@/utils/user'

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
const idCardImage = ref<File | null>(null)

onMounted(() => {
  checkAuthStatus()
  loadUserData()
})

const checkAuthStatus = () => {
  isAuth.value = isAuthenticated()
  if (!isAuth.value) {
    router.push({ name: 'Login', query: { redirect: '/profile' } })
  }
}
const loadUserData = async () => {
  try {
    if (isAuthenticated()) {
      const userData = await loadUserFromServer()
      user.value = userData
      newPhone.value = user.value.phone || getUserPhone() || ''
    }
  } catch (error) {
    Toast('加载用户数据失败')
  }
}

const handleBack = () => {
  router.back()
}

const handleAvatarUpload = async (file: any) => {
  if (file && file.file) {
    isUploading.value = true

    try {
      const avatarPath = await uploadAvatar(file.file)
      user.value.avatarUrl = avatarPath
      Toast('头像上传成功')
    } catch (error: any) {
      Toast(error.message || '头像上传失败，请重试')
    } finally {
      isUploading.value = false
    }
  } else {
    Toast('文件上传失败，请重试')
  }
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
  newStudentId.value = user.value.studentId || ''
  showStudentIdEdit.value = true
}

const saveStudentId = async () => {
  if (!newStudentId.value.trim()) {
    Toast('学号不能为空')
    return
  }

  if (!/^\d{8,12}$/.test(newStudentId.value.trim())) {
    Toast('请输入有效的学号')
    return
  }

  try {
    const userData = await updateUserInfo({ studentId: newStudentId.value.trim() })
    user.value = userData
    showStudentIdEdit.value = false
    Toast('学号更新成功')
  } catch (error: any) {
    Toast(error.message || '学号更新失败')
  }
}

const handlePhoneClick = () => {
  newPhone.value = user.value.phone || getUserPhone() || ''
  showPhoneEdit.value = true
}

const savePhone = async () => {
  if (!newPhone.value.trim()) {
    Toast('手机号不能为空')
    return
  }

  if (!/^1[3-9]\d{9}$/.test(newPhone.value.trim())) {
    Toast('请输入有效的手机号')
    return
  }

  try {
    const userData = await updateUserInfo({ phone: newPhone.value.trim() })
    user.value = userData
    showPhoneEdit.value = false
    Toast('手机号更新成功')
  } catch (error: any) {
    Toast(error.message || '手机号更新失败')
  }
}

const handleProofUpload = (file: any) => {
  if (file && file.file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      const base64String = e.target?.result as string
      newProof.value = base64String
      idCardImage.value = file.file

      Toast('证明文件上传成功')
    }
    reader.onerror = () => {
      Toast('证明文件上传失败，请重试')
    }
    reader.readAsDataURL(file.file)
  } else {
    Toast('文件上传失败，请重试')
  }
}

const handleSubmit = async () => {
  Dialog.confirm({
    title: '确认提交',
    message: '确认提交个人信息修改？',
  }).then(async () => {
    try {
      const updates: any = {}

      if (user.value.username) updates.username = user.value.username
      if (user.value.phone) updates.phone = user.value.phone
      if (user.value.studentId) updates.studentId = user.value.studentId
      if (idCardImage.value) updates.idCardImage = idCardImage.value

      await updateUserInfo(updates)
      Toast('个人信息提交成功')
      router.back()
    } catch (error: any) {
      Toast(error.message || '个人信息提交失败')
    }
  }).catch(() => {
    // on cancel
  })
}

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
