<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTheme } from '@/composables/useTheme'
import { useAuth } from '@/composables/useAuth'
import { logout } from '@/api'
import LgIconButton from '@/components/base/LgIconButton.vue'
import LgInput from '@/components/base/LgInput.vue'
import LgButton from '@/components/base/LgButton.vue'
import LinkModal from '@/components/blog/common/LinkModal.vue'
import { fetchCategories } from '@/api'
import type { Category } from '@/api'

const search = ref('')
const router = useRouter()
const route = useRoute()
const { theme, toggleTheme } = useTheme()
const { isLoggedIn, userInfo, clearAuth } = useAuth()
const categories = ref<Category[]>([])
const showCategoryMenu = ref(false)
const categoryDropdownRef = ref<HTMLElement | null>(null)


// 友链弹窗
const showLinkModal = ref(false)

// 用户菜单
const showUserMenu = ref(false)
const userMenuRef = ref<HTMLElement | null>(null)

// 提供简单导航跳转
const navItems = [
  { name: '主页', path: '/' },
]

// 打开友链弹窗
function openLinkModal() {
  showLinkModal.value = true
}

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
  if (userMenuRef.value && !userMenuRef.value.contains(event.target as Node)) {
    closeUserMenu()
  }
}

// 切换用户菜单
function toggleUserMenu(event: Event) {
  event.stopPropagation()
  showUserMenu.value = !showUserMenu.value
}

// 关闭用户菜单
function closeUserMenu() {
  showUserMenu.value = false
}


// 退出登录
async function handleLogout() {
  try {
    await logout()
  } catch (error) {
    console.error('退出登录失败:', error)
  } finally {
    clearAuth()
    closeUserMenu()
    router.push('/')
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

        <!-- 友链按钮 -->
        <button class="nav-link" @click="openLinkModal">
          友链
        </button>
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

        <!-- 未登录：显示登录/注册按钮 -->
        <div v-if="!isLoggedIn" class="auth-buttons">
          <router-link to="/register" class="nav-register-link">
            注册
          </router-link>
          <router-link to="/login" class="nav-login-link">
            登录
          </router-link>
        </div>

        <!-- 已登录：显示用户菜单 -->
        <div v-else ref="userMenuRef" class="user-menu-container">
          <button class="user-avatar" @click="toggleUserMenu" :aria-expanded="showUserMenu">
            <img :src="userInfo?.avatar || '/default-avatar.png'" :alt="userInfo?.nickName" />
          </button>

          <div v-show="showUserMenu" class="user-menu">
            <div class="user-info">
              <img :src="userInfo?.avatar || '/default-avatar.png'" :alt="userInfo?.nickName" class="user-info-avatar" />
              <div class="user-info-text">
                <div class="user-info-name">{{ userInfo?.nickName }}</div>
                <div class="user-info-email">{{ userInfo?.email }}</div>
              </div>
            </div>
            <div class="user-menu-divider"></div>
            <button class="user-menu-item" @click="handleLogout">
              <svg viewBox="0 0 24 24" width="18" height="18">
                <path
                  fill="currentColor"
                  d="M16 13v-2H7V8l-5 4 5 4v-3h9zM20 3h-9c-1.103 0-2 .897-2 2v4h2V5h9v14h-9v-4H9v4c0 1.103.897 2 2 2h9c1.103 0 2-.897 2-2V5c0-1.103-.897-2-2-2z"
                />
              </svg>
              退出登录
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 友链弹窗 -->
    <LinkModal :show="showLinkModal" @close="showLinkModal = false" />
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

/* 友链按钮 - 确保与其他导航按钮样式一致 */
.nav-link[type="button"],
button.nav-link {
  background: none;
  border: none;
  cursor: pointer;
  font-family: inherit;
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

/* 用户菜单容器 */
.user-menu-container {
  position: relative;
}

/* 用户头像按钮 */
.user-avatar {
  width: 38px;
  height: 38px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.2s ease;
  background: rgba(255, 255, 255, 0.1);
  padding: 0;
}

.user-avatar:hover {
  border-color: var(--sg-primary);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 用户下拉菜单 */
.user-menu {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  min-width: 240px;
  background: rgba(40, 42, 44, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(255, 255, 255, 0.1);
  padding: 12px 0;
  z-index: 1000;
  animation: slideDown 0.2s ease;
}

body.dark .user-menu {
  background: rgba(26, 26, 46, 0.95);
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
}

/* 认证按钮组 */
.auth-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 导航链接样式 */
.nav-register-link,
.nav-login-link {
  padding: 8px 16px;
  height: 32px;
  line-height: 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-register-link {
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.nav-register-link:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
}

.nav-login-link {
  background: var(--sg-primary);
  color: #fff;
  border: 1px solid var(--sg-primary);
}

.nav-login-link:hover {
  background: var(--sg-primary-hover);
  border-color: var(--sg-primary-hover);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.user-info-avatar {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.user-info-text {
  flex: 1;
  min-width: 0;
}

.user-info-name {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-info-email {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 分隔线 */
.user-menu-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 8px 0;
}

/* 菜单项 */
.user-menu-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: #fff;
  font-size: 14px;
  transition: all 0.2s ease;
  background: none;
  border: none;
  cursor: pointer;
  text-align: left;
}

.user-menu-item:hover {
  background: rgba(231, 76, 60, 0.2);
  padding-left: 20px;
}

.user-menu-item svg {
  flex-shrink: 0;
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
