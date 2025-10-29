<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTheme } from '@/composables/useTheme'
import LgIconButton from '@/components/base/LgIconButton.vue'
import LgInput from '@/components/base/LgInput.vue'
import { fetchCategories } from '@/api'
import type { Category } from '@/api'

const search = ref('')
const router = useRouter()
const route = useRoute()
const { theme, toggleTheme } = useTheme()
const categories = ref<Category[]>([])
const showCategoryMenu = ref(false)
const categoryDropdownRef = ref<HTMLElement | null>(null)

// 提供简单导航跳转
const navItems = [
  { name: '主页', path: '/' },
  { name: '归档', path: '/#archive' },
  { name: '关于', path: '/#about' },
]

// 加载分类数据
async function loadCategories() {
  try {
    categories.value = await fetchCategories()
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 切换分类菜单显示状态
function toggleCategoryMenu(event: Event) {
  event.stopPropagation()
  showCategoryMenu.value = !showCategoryMenu.value
}

// 关闭分类菜单
function closeCategoryMenu() {
  showCategoryMenu.value = false
}

// 点击外部关闭菜单
function handleClickOutside(event: MouseEvent) {
  if (categoryDropdownRef.value && !categoryDropdownRef.value.contains(event.target as Node)) {
    closeCategoryMenu()
  }
}

// 检查分类是否被激活
function isCategoryActive(categoryId: number): boolean {
  return route.query.category === String(categoryId)
}

function onSearchSubmit() {
  if (!search.value.trim()) return
  router.push({ path: '/', query: { q: search.value } })
}

onMounted(() => {
  loadCategories()
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
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
        
        <!-- 分类下拉菜单 -->
        <div ref="categoryDropdownRef" class="category-dropdown">
          <button
            class="nav-link category-trigger"
            @click="toggleCategoryMenu"
            aria-haspopup="true"
            :aria-expanded="showCategoryMenu"
          >
            分类
            <svg
              class="dropdown-icon"
              :class="{ 'dropdown-icon--open': showCategoryMenu }"
              viewBox="0 0 20 20"
              fill="currentColor"
              width="16"
              height="16"
            >
              <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
            </svg>
          </button>
          
          <div v-show="showCategoryMenu" class="category-menu" role="menu">
            <a
              v-for="category in categories"
              :key="category.id"
              :href="`/?category=${category.id}`"
              :class="['category-item', isCategoryActive(category.id) ? 'category-item--active' : '']"
              role="menuitem"
              @click="closeCategoryMenu"
            >
              <span>{{ category.name }}</span>
              <span v-if="category.count" class="category-count">{{ category.count }}</span>
            </a>
            <div v-if="!categories.length" class="category-empty">暂无分类</div>
          </div>
        </div>
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
/* 导航栏容器 */
.nav-wrapper {
  width: 100%;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(40, 42, 44, 0.6);
  backdrop-filter: blur(12px) saturate(140%);
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.12), 0 0 6px 0 rgba(0, 0, 0, 0.04);
  transition: background 0.3s ease;
}

body.dark .nav-wrapper {
  background: rgba(26, 26, 46, 0.75);
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.25);
}

.nav-bar {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: clamp(16px, 2vw, 28px);
  padding: 0 clamp(24px, 6vw, 72px);
  height: 60px;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

/* Logo品牌 */
.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  border-radius: var(--lg-radius-2xl);
  padding: 4px 8px;
  color: #fff;
  transition: opacity 0.2s ease;
}

.brand:hover {
  opacity: 0.85;
}

.brand__logo {
  display: inline-flex;
  padding: 6px;
  border-radius: var(--lg-radius-full);
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
}

.brand__text {
  display: flex;
  flex-direction: column;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.brand__text strong {
  font-size: 16px;
  line-height: 1;
  color: #fff;
  font-weight: 600;
}

/* 导航链接 */
.nav-links {
  display: flex;
  justify-content: center;
  gap: 4px;
  flex-wrap: wrap;
}

.nav-link {
  padding: 8px 20px;
  height: 38px;
  line-height: 22px;
  border-radius: 2px;
  color: #fff;
  font-size: 14px;
  transition: all 0.2s ease;
  border-bottom: 2px solid transparent;
}

.nav-link:hover {
  border-bottom-color: var(--sg-primary);
}

.nav-link--active {
  border-bottom-color: var(--sg-primary);
}

/* 分类下拉菜单容器 */
.category-dropdown {
  position: relative;
  display: inline-block;
}

/* 分类触发按钮 */
.category-trigger {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  cursor: pointer;
}

/* 下拉图标 */
.dropdown-icon {
  transition: transform 0.2s ease;
}

.dropdown-icon--open {
  transform: rotate(180deg);
}

/* 分类下拉菜单 */
.category-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  min-width: 180px;
  max-height: 400px;
  overflow-y: auto;
  background: rgba(40, 42, 44, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25), 0 0 0 1px rgba(255, 255, 255, 0.1);
  padding: 8px 0;
  z-index: 1000;
  animation: slideDown 0.2s ease;
}

body.dark .category-menu {
  background: rgba(26, 26, 46, 0.95);
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 分类菜单项 */
.category-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  color: #fff;
  font-size: 14px;
  transition: all 0.2s ease;
  gap: 8px;
}

.category-item:hover {
  background: rgba(80, 204, 213, 0.2);
  padding-left: 20px;
}

.category-item--active {
  background: rgba(80, 204, 213, 0.3);
  color: var(--sg-primary);
  font-weight: 600;
}

.category-count {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
}

.category-item--active .category-count {
  background: rgba(80, 204, 213, 0.3);
  color: var(--sg-primary);
}

.category-empty {
  padding: 16px;
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  font-size: 13px;
}

/* 右侧操作区 */
.nav-actions {
  display: flex;
  align-items: center;
  gap: clamp(10px, 1.2vw, 16px);
}

.nav-actions :deep(.lg-input__wrapper) {
  max-width: 220px;
}

.nav-actions :deep(.lg-input__field) {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.25);
  color: #fff;
}

.nav-actions :deep(.lg-input__field::placeholder) {
  color: rgba(255, 255, 255, 0.6);
}

.nav-actions :deep(.lg-icon-button) {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  border-color: rgba(255, 255, 255, 0.2);
}

.nav-actions :deep(.lg-icon-button:hover) {
  background: rgba(255, 255, 255, 0.2);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .nav-bar {
    grid-template-columns: 1fr;
    height: auto;
    padding: 12px clamp(12px, 4vw, 24px);
    gap: 12px;
  }

  .brand__text span {
    display: none;
  }

  .nav-links {
    justify-content: flex-start;
    gap: 2px;
  }

  .nav-link {
    padding: 6px 12px;
    font-size: 13px;
    height: 32px;
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

@media (max-width: 500px) {
  .nav-bar {
    padding: 10px 12px;
  }
  
  .nav-links {
    display: none;
  }
}
</style>
