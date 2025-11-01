<script setup lang="ts">

</script>

<template>
  <div class="home">
    <div>
      <van-image width="100%" fit="cover" src="/home-img.png" />
    </div>
    <van-card class="home_card" desc="跑了个腿 Run A Leg" title="欢迎加入">
      <template #footer>
        <van-button size="normal">注册/登录</van-button>
      </template>
    </van-card>
    <van-grid clickable :column-num="2">
      <van-grid-item to="/help" class="home_grid-item_help">
      <div class="home_grid-item_text"><h1>我要帮忙</h1><br>
        <h3>give a hand</h3></div>
        
        <van-image width="100%" fit="cover" src="/give-a-hand.png" />
      </van-grid-item>
      <van-grid-item to="/my" class="home_grid-item_ask">
      <div class="home_grid-item_text"><h1>我要求助</h1><br>
        <h3>ask for help</h3></div>
        
        <van-image width="100%" fit="cover" src="/ask-for-help.png" />
      </van-grid-item>
    </van-grid>
  </div>
</template>

<style scoped>
.home_card {
  margin: 0;
}

.home_card :deep(.van-card__title) {
  font-size: 20px;
  line-height: normal;
  max-height: fit-content;
  margin: 15px;
}

.home_card :deep(.van-card__desc) {
  color: black;
  font-size: 24px;
  line-height: normal;
  max-height: fit-content;
  margin: 15px;
}
.home_grid-item_help :deep(.van-grid-item__content){
  background-color: #F9C12D;
}
.home_grid-item_ask :deep(.van-grid-item__content){
  background-color: #85D1E7;
}
.home_grid-item_text{
  text-align: right;
  color: white;
  
}
</style>
