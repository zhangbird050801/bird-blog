<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import { useTheme } from '@/composables/useTheme'

// 通过统一主题钩子在应用初始化时同步 body 状态
const { applyStoredTheme } = useTheme()

onMounted(() => {
  applyStoredTheme()
})
</script>

<template>
  <div class="app-shell">
    <AppNavbar />
    <main class="lg-container">
      <RouterView />
    </main>
    <AppFooter />
  </div>
</template>

<style scoped>
.app-shell {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  gap: clamp(24px, 4vw, 40px);
}

main {
  flex: 1;
  width: 100%;
}
</style>
