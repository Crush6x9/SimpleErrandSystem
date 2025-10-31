<script setup lang="ts">
import TheHeader from '@/components/TheHeader.vue';
</script>

<template>
  <div class="home">
    <div><TheHeader/></div>
    <div>
    <h1>首页</h1>
    <el-button type="primary">Element Plus 按钮</el-button>
    <el-alert title="这是 Element Plus 组件" type="success" />
  </div>
  </div>
</template>

<style scoped>
.home {
  padding: 20px;
}
</style>
