<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTheme } from '@/composables/useTheme'
import LgIconButton from '@/components/base/LgIconButton.vue'
import LgInput from '@/components/base/LgInput.vue'

const search = ref('')
const router = useRouter()
const route = useRoute()
const { theme, toggleTheme } = useTheme()

// 提供简单导航跳转
const navItems = [
  { name: '主页', path: '/' },
  { name: '归档', path: '/#archive' },
  { name: '关于', path: '/#about' },
]

function onSearchSubmit() {
  if (!search.value.trim()) return
  router.push({ path: '/', query: { q: search.value } })
}
</script>

<template>
  <header class="nav-wrapper">
    <div class="nav-bar">
      <a class="brand" href="#" aria-label="BirdBlog 博客 首页">
        <span class="brand__logo" aria-hidden="true">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <linearGradient id="lg-brand" x1="0" y1="0" x2="40" y2="40" gradientUnits="userSpaceOnUse">
                <stop stop-color="#7aa2ff" />
                <stop offset="0.5" stop-color="#4f7cff" />
                <stop offset="1" stop-color="#2dd4bf" />
              </linearGradient>
            </defs>
            <rect x="2" y="2" width="36" height="36" rx="18" fill="url(#lg-brand)" opacity="0.32" />
            <path d="M12 20c4.5-8 11.5-8 16 0" stroke="url(#lg-brand)" stroke-width="2.6" stroke-linecap="round" />
            <path d="M14 23.5c2.5 4.8 9.5 4.8 12 0" stroke="url(#lg-brand)" stroke-width="2.4" stroke-linecap="round" />
            <circle cx="20" cy="26.5" r="1.9" fill="#fff" opacity="0.85" />
          </svg>
        </span>
        <span class="brand__text">
          <strong>BirdBlog</strong>
          <span>流动质感博客</span>
        </span>
      </a>

      <nav class="nav-links" aria-label="主导航">
        <a
          v-for="item in navItems"
          :key="item.name"
          :href="item.path"
          :class="['nav-link', route.path === item.path ? 'nav-link--active' : '']"
        >
          {{ item.name }}
        </a>
      </nav>

      <div class="nav-actions">
        <LgInput
          v-model="search"
          placeholder="搜索文章..."
          aria-label="搜索文章"
          icon
          @submit="onSearchSubmit"
        >
          <template #icon>
            <svg viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M14.752 13.493 18 16.74l-1.26 1.26-3.247-3.247a6.5 6.5 0 1 1 1.26-1.26zm-2.002-.741a4.75 4.75 0 1 0-.001 0z"
                fill="currentColor"
              />
            </svg>
          </template>
        </LgInput>

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
  </header>
</template>

<style scoped>
.nav-wrapper {
  width: 100%;
  position: sticky;
  top: 0;
  z-index: 40;
  background: rgba(248, 250, 255, 0.82);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
}

body.dark .nav-wrapper {
  background: rgba(8, 12, 28, 0.82);
  border-bottom-color: rgba(148, 163, 184, 0.16);
}

.nav-bar {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: clamp(16px, 2vw, 28px);
  padding: clamp(10px, 1.6vw, 18px) clamp(24px, 6vw, 72px);
  width: 100%;
  max-width: none;
  margin: 0 auto;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  border-radius: var(--lg-radius-2xl);
  padding: 4px 8px;
}

.brand__logo {
  display: inline-flex;
  padding: 6px;
  border-radius: var(--lg-radius-full);
  background: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.46);
}

.brand__text {
  display: flex;
  flex-direction: column;
  font-size: 13px;
  color: var(--lg-text-secondary);
}

.brand__text strong {
  font-size: 16px;
  line-height: 1;
  color: var(--lg-text-primary);
}

.nav-links {
  display: flex;
  justify-content: center;
  gap: clamp(12px, 2vw, 24px);
  flex-wrap: wrap;
}

.nav-link {
  padding: 8px 14px;
  border-radius: var(--lg-radius-full);
  color: var(--lg-text-secondary);
  transition: all var(--lg-transition);
}

.nav-link:hover,
.nav-link--active {
  color: var(--lg-text-primary);
  background: rgba(79, 124, 255, 0.18);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: clamp(10px, 1.2vw, 16px);
}

.nav-actions :deep(.lg-input__wrapper) {
  max-width: 220px;
}

@media (max-width: 768px) {
  .nav-bar {
    grid-template-columns: 1fr;
    justify-items: stretch;
  }

  .nav-actions {
    flex-wrap: wrap;
    justify-content: space-between;
  }

  .nav-actions :deep(.lg-input__wrapper) {
    flex: 1 1 100%;
    max-width: 100%;
  }
}
</style>
