<template>
    <div class="order-complete">
        <van-nav-bar title="完成订单" left-text="返回" left-arrow @click-left="handleBack" />
        <div class="order-complete-container">
            <div class="content">
                <h1 class="title">恭喜完成</h1>
                <p class="subtitle">本次互助订单</p>
                <div class="amount">钱包+￥{{ amount }}</div>
                <van-image width="150px" src="/order-complete.png" class="complete-img" />
                <div class="message">
                    <p>感谢好心人帮助</p>
                    <p>生活因你更精彩</p>
                </div>

            </div>
            <div class="actions">
                <van-image width="150px" src="/order-complete-back.png" class="complete-img" @click="handleBack" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Toast } from 'vant'

const router = useRouter()
const route = useRoute()
const amount = ref('0.00')

onMounted(() => {
  // 如果有传递金额参数，使用参数中的金额
  if (route.query.amount) {
    amount.value = route.query.amount as string
  } else {
    // 默认金额
    amount.value = '0.00'
  }
})

const handleBack = () => {
    router.push('/home')
}
</script>

<style scoped>
.order-complete-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    min-height: 90vh;
    background: linear-gradient(180deg, #F2BE45 40%, #fff 20%);
    padding: 20px;
}

.content {

    background: #F2BE45;
    padding: 40px 30px;
    box-shadow: 0 0px 10px #FF9900;
    width: 100%;
    max-width: 350px;
    color: #fff;
}

.icon {
    margin-bottom: 20px;
}

.title {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 10px;
}

.subtitle {
    font-size: 18px;
    margin-bottom: 25px;
}

.amount {
    font-size: 32px;
    font-weight: bold;

    margin-bottom: 30px;
}

.complete-img {
    margin: 0 25%;
}

.message {
    padding: 15px 15px 0 15px;
    text-align: center;
}

.message p {
    margin: 8px 0;
    font-size: 28px;
}

.actions {
    display: flex;
    align-items: center;
    margin-top: 40px;
}
</style>