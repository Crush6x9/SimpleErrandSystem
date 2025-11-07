<script setup lang="ts">
// import TheHeader from '@/components/TheHeader.vue';
import TheFooter from '@/components/TheFooter.vue';
</script>

<template>
  <div id="app" class="page-container">
    <!-- <TheHeader/> -->
    
    <div class="app-content">
      <router-view />
    </div>
    
    <TheFooter/>
  </div>
</template>

<style scoped>
.app-content {
  flex: 1;
  padding-bottom: 50px; /* 为底部导航栏留出空间 */
}
</style>