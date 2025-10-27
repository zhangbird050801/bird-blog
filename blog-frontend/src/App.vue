<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import BackToTop from '@/components/blog/common/BackToTop.vue'
import SnowfallEffect from '@/components/effects/SnowfallEffect.vue'
import { useTheme } from '@/composables/useTheme'

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
    <BackToTop />
    <SnowfallEffect />
  </div>
</template>

<style scoped>
.app-shell {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

main {
  flex: 1;
  width: 100%;
  padding-top: 60px;
}

@media (max-width: 768px) {
  main {
    padding-top: 70px; 
  }
}
</style>
