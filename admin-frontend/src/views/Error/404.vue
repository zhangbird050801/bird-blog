<template>
  <div class="error-container">
    <h1>404</h1>
    <p>抱歉,您访问的页面不存在</p>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/')
}
</script>

<style scoped>
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
}

h1 {
  font-size: 120px;
  color: #409eff;
  margin: 0;
}

p {
  font-size: 24px;
  color: #666;
  margin: 20px 0 40px;
}
</style>
