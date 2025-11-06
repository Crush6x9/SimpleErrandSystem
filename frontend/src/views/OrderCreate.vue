<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = reactive({
  title: '',
  address: '',
  price: '',
  expectTime: new Date()
})

const submit = () => {
  // TODO: 调用 API
  router.push('/order')
}
</script>

<template>
  <div class="page-container">
    <van-nav-bar title="发布跑腿" left-arrow @click-left="router.back()" safe-area-inset-top />

    <div class="content">
      <div class="card">
        <van-form @submit="submit">
          <van-field v-model="form.title" label="任务标题" placeholder="例如：帮我买奶茶" required />
          <van-field v-model="form.address" label="送达地址" placeholder="详细地址" required />
          <van-field v-model="form.price" label="悬赏金额" type="digit" required>
            <template #left-icon><span class="text-lg">¥</span></template>
          </van-field>
          <van-field label="期望时间">
            <template #input>
              <van-datetime-picker v-model="form.expectTime" type="datetime" />
            </template>
          </van-field>

          <div class="p-4">
            <van-button round block type="primary" native-type="submit">
              发布任务
            </van-button>
          </div>
        </van-form>
      </div>
    </div>

    <div class="h-16"></div>
  </div>
</template>