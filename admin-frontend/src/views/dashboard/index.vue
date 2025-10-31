<template>
  <div class="dashboard-container">
    <h2>欢迎使用 BirdBlog 管理后台</h2>
    <p>当前登录用户: {{ userStore.userName }}</p>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

h2 {
  font-size: 24px;
  margin-bottom: 20px;
}
</style>
