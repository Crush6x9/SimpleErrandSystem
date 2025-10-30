<script setup lang="ts">
</script>

<template>
  <div id="app">
    <nav style="margin-bottom: 20px;">
      <router-link to="/">首页</router-link> |
      <router-link to="/about">关于</router-link>
    </nav>
    <router-view />
  </div>
</template>

<style>
/* 可添加全局样式 */
</style>