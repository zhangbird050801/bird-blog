<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import AppNavbar from '@/components/layout/AppNavbar.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import BackToTop from '@/components/blog/common/BackToTop.vue'
import SnowfallEffect from '@/components/effects/SnowfallEffect.vue'
import LgIconButton from '@/components/base/LgIconButton.vue'
import { useTheme } from '@/composables/useTheme'

const route = useRoute()
const { theme, toggleTheme, applyStoredTheme } = useTheme()

// 检查当前路由是否需要显示导航栏和页脚
const showLayout = computed(() => {
  return route.meta.requiresAuthLayout !== false
})

onMounted(() => {
  applyStoredTheme()
})
</script>

<template>
  <div class="app-shell">
    <AppNavbar v-if="showLayout" />
    <main class="lg-container" :class="{ 'full-page': !showLayout }">
      <!-- 简化的导航栏，用于登录注册页面 -->
      <div v-if="!showLayout" class="simple-navbar">
        <div class="simple-navbar-content">
          <router-link to="/" class="back-home-link">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path
                fill="currentColor"
                d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"
              />
            </svg>
            返回首页
          </router-link>
          <LgIconButton :label="theme === 'light' ? '切换夜间模式' : '切换日间模式'" @click="toggleTheme">
            <svg v-if="theme === 'light'" viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
              <path
                fill="currentColor"
                d="M12 18a6 6 0 0 0 0-12v12zm0 2a8 8 0 0 1 0-16v-2a10 10 0 1 0 0 20v-2z"
              />
            </svg>
            <svg v-else viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
              <path
                fill="currentColor"
                d="M12 4a1 1 0 0 1 1 1v1a1 1 0 1 1-2 0V5a1 1 0 0 1 1-1zm6.364 1.636a1 1 0 0 1 1.414 1.414l-.707.707a1 1 0 1 1-1.414-1.414l.707-.707zM19 11a1 1 0 1 1 0 2h-1a1 1 0 1 1 0-2h1zm-7 6a1 1 0 0 1 1 1v1a1 1 0 1 1-2 0v-1a1 1 0 0 1 1-1zm7.071 1.071a1 1 0 1 1-1.414 1.414l-.707-.707a1 1 0 0 1 1.414-1.414l.707.707zM6.343 5.343a1 1 0 0 1 0 1.414l-.707.707A1 1 0 0 1 4.222 6.05l.707-.707a1 1 0 0 1 1.414 0zM5 11a1 1 0 1 1 0 2H4a1 1 0 1 1 0-2h1zm1.757 6.071a1 1 0 0 1 1.414 1.414l-.707.707a1 1 0 1 1-1.414-1.414l.707-.707z"
              />
            </svg>
          </LgIconButton>
        </div>
      </div>
      <RouterView />
    </main>
    <AppFooter v-if="showLayout" />
    <BackToTop v-if="showLayout" />
    <SnowfallEffect v-if="showLayout" />
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

main.full-page {
  padding-top: 0;
}

/* 简化导航栏样式 */
.simple-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(248, 249, 250, 0.95);;
  backdrop-filter: blur(12px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: background 0.3s ease;
}

body.dark .simple-navbar {
  background: rgba(26, 26, 46, 0.75);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.25);
}

.simple-navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.back-home-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.2s ease;
}

.back-home-link:hover {
  color: var(--sg-primary);
}

body.dark .back-home-link {
  color: #fff;
}

body.dark .back-home-link:hover {
  color: #7aa2ff;
}

/* 简化导航栏中的主题切换按钮 */
.simple-navbar :deep(.lg-icon-button) {
  background: rgba(0, 0, 0, 0.05);
  color: #333;
  border-color: rgba(0, 0, 0, 0.1);
}

.simple-navbar :deep(.lg-icon-button:hover) {
  background: rgba(0, 0, 0, 0.1);
}

body.dark .simple-navbar :deep(.lg-icon-button) {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  border-color: rgba(255, 255, 255, 0.2);
}

body.dark .simple-navbar :deep(.lg-icon-button:hover) {
  background: rgba(255, 255, 255, 0.2);
}

@media (max-width: 768px) {
  main {
    padding-top: 70px;
  }

  main.full-page {
    padding-top: 0;
  }

  .simple-navbar-content {
    padding: 10px 16px;
  }

  .back-home-link {
    font-size: 13px;
  }
}
</style>
