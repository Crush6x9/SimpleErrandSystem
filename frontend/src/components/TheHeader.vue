<template>
    <div>
    <van-nav-bar
  title="跑了个腿"
  left-text="返回"
  left-arrow
  @click-left="onClickLeft"
/>
</div>
</template>
<script setup lang="ts">
const onClickLeft = () => history.back();
</script>