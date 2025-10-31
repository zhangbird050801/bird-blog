<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="layout-aside">
      <div class="logo-container">
        <img
          v-if="!isCollapse"
          src="https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif"
          alt="Logo"
          class="logo"
        />
        <h1 v-if="!isCollapse" class="title">BirdBlog</h1>
      </div>

      <!-- 菜单 -->
      <el-scrollbar>
        <el-menu
          ref="menuRef"
          :default-active="activeMenu"
          :default-openeds="openedMenus"
          :collapse="isCollapse"
          :unique-opened="false"
          router
          background-color="#304156"
          text-color="#ffffff"
          active-text-color="#50ccd5"
          @open="onMenuOpen"
          @close="onMenuClose"
        >
          <menu-item
            v-for="(menu, index) in menuList"
            :key="index"
            :menu="menu"
          />
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container>
      <!-- 头部 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon
            class="collapse-icon"
            :size="20"
            @click="toggleCollapse"
          >
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
        </div>

        <div class="header-right">
          <!-- 用户信息 -->
          <el-dropdown>
            <div class="user-info">
              <el-avatar :src="userStore.avatar" :size="32">
                {{ userStore.userName.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userStore.userName }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage, ElMenu } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import MenuItem from './MenuItem.vue'
import type { RouteData } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const menuStore = useMenuStore()

// 侧边栏折叠状态
const isCollapse = ref<boolean>(false)

// 菜单实例与展开状态
const menuRef = ref<InstanceType<typeof ElMenu>>()
const openedMenus = ref<string[]>([])
const collapsedByUser = ref<string[]>([])

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 菜单列表
const menuList = computed(() => menuStore.menuList)

// 路由切换时同步菜单展开状态
watch(
  () => route.path,
  (currentPath) => {
    syncOpenedMenus(currentPath)
  },
  { immediate: true }
)

// 菜单数据发生变化时清理失效状态并重建展开逻辑
watch(
  () => menuList.value,
  () => {
    pruneInvalidMenuState()
    syncOpenedMenus(route.path)
  },
  { deep: true }
)

/**
 * 切换侧边栏折叠状态
 */
function toggleCollapse() {
  isCollapse.value = !isCollapse.value
}

function onMenuOpen(index: string) {
  ensureMenuOpened(index, 'manual')
}

function onMenuClose(index: string) {
  registerMenuClosed(index, 'manual')
}

function syncOpenedMenus(activePath: string) {
  if (!activePath || menuList.value.length === 0) return
  const ancestors = collectAncestorIndexes(menuList.value, activePath)
  if (!ancestors || ancestors.length === 0) return
  const normalizedAncestors = ancestors.map(item => normalizePath(item))

  normalizedAncestors.forEach(index => {
    if (!collapsedByUser.value.includes(index)) {
      ensureMenuOpened(index, 'route')
    }
  })
}

function collectAncestorIndexes(nodes: RouteData[], targetPath: string, parentPath = ''): string[] | null {
  const normalizedTarget = normalizePath(targetPath)
  for (const node of nodes) {
    const currentPath = resolveMenuPath(node.path, parentPath)
    const normalizedCurrent = normalizePath(currentPath)

    if (normalizedCurrent === normalizedTarget) {
      const ancestors: string[] = parentPath ? [normalizePath(parentPath)] : []
      if (hasVisibleChildren(node)) {
        return [...ancestors, normalizedCurrent]
      }
      return ancestors
    }

    if (node.children && node.children.length > 0) {
      const childAncestors = collectAncestorIndexes(node.children, targetPath, currentPath)
      if (childAncestors) {
        if (!childAncestors.includes(normalizedCurrent) && hasVisibleChildren(node)) {
          return [normalizedCurrent, ...childAncestors]
        }
        return childAncestors
      }
    }
  }
  return null
}

function ensureMenuOpened(index: string, source: 'manual' | 'route') {
  if (!index) return
  const normalized = normalizePath(index)
  if (source === 'manual') {
    collapsedByUser.value = collapsedByUser.value.filter(item => item !== normalized)
  }
  if (!openedMenus.value.includes(normalized)) {
    openedMenus.value = [...openedMenus.value, normalized]
  }
  nextTick(() => menuRef.value?.open(normalized))
}

function registerMenuClosed(index: string, source: 'manual' | 'sync') {
  if (!index) return
  const normalized = normalizePath(index)
  if (openedMenus.value.includes(normalized)) {
    openedMenus.value = openedMenus.value.filter(item => item !== normalized)
  }
  if (source === 'manual' && !collapsedByUser.value.includes(normalized)) {
    collapsedByUser.value = [...collapsedByUser.value, normalized]
  }
  nextTick(() => menuRef.value?.close(normalized))
}

function pruneInvalidMenuState() {
  const validKeys = new Set<string>()
  collectMenuIndexes(menuList.value, '', validKeys)
  openedMenus.value = openedMenus.value.filter(key => validKeys.has(key))
  collapsedByUser.value = collapsedByUser.value.filter(key => validKeys.has(key))
}

function collectMenuIndexes(nodes: RouteData[], parentPath = '', bucket: Set<string>) {
  nodes.forEach(node => {
    const currentPath = resolveMenuPath(node.path, parentPath)
    const normalizedCurrent = normalizePath(currentPath)
    if (node.component || hasVisibleChildren(node)) {
      bucket.add(normalizedCurrent)
    }
    if (node.children && node.children.length > 0) {
      collectMenuIndexes(node.children, currentPath, bucket)
    }
  })
}

function hasVisibleChildren(node: RouteData): boolean {
  if (!node.children) return false
  return node.children.some(child => !!(child.component || child.path))
}

function resolveMenuPath(path: string, parentPath = ''): string {
  if (!path) return normalizePath(parentPath)
  if (path.startsWith('/')) return normalizePath(path)
  if (!parentPath) return normalizePath(`/${path}`)
  return normalizePath(`${parentPath}/${path}`)
}

function normalizePath(path: string): string {
  if (!path) return ''
  const segments = path.split('/').filter(Boolean)
  return `/${segments.join('/')}`
}

/**
 * 退出登录
 */
async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await userStore.logout()
    menuStore.clearMenus()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 用户取消操作
  }
}
</script>

<style scoped>
.layout-container {
  height: 100%;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  padding: 0 20px;
  background-color: #2b3a4b;
}

.logo {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin: 0;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-icon {
  cursor: pointer;
  transition: all 0.3s;
}

.collapse-icon:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #333;
}

.layout-main {
  background-color: #f0f2f5;
  min-height: calc(100vh - 60px);
}
</style>
