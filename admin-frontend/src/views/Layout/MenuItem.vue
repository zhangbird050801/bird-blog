<template>
  <!-- 有子菜单的情况 -->
  <el-sub-menu v-if="hasChildren" :index="menuPath" :class="{ 'no-arrow': !!menu.component }">
    <template #title>
      <component v-if="iconComponent" :is="iconComponent" class="menu-icon" />
      <span v-else class="menu-icon-fallback">{{ fallbackLetter }}</span>
      
      <span class="menu-title">{{ menu.meta?.title }}</span>
    </template>

    <menu-item
      v-for="child in visibleChildren"
      :key="child.path"
      :menu="child"
      :parent-path="menuPath"
    />
  </el-sub-menu>

  <!-- 单个菜单项 -->
  <el-menu-item v-else :index="menuPath">
    <component v-if="iconComponent" :is="iconComponent" class="menu-icon" />
    <span v-else class="menu-icon-fallback">{{ fallbackLetter }}</span>
    
    <template #title><span class="menu-title">{{ menu.meta?.title }}</span></template>
  </el-menu-item>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { RouteData } from '@/types'

defineOptions({
  name: 'MenuItem'
})

interface Props {
  menu: RouteData
  parentPath?: string
}

const props = defineProps<Props>()

const menuPath = computed(() => {
  const p = props.menu.path || ''
  if (p.startsWith('/')) return p
  if (props.parentPath) return `${props.parentPath}/${p}`.replace(/\/+/g, '/')
  return `/${p}`
})

const visibleChildren = computed(() => {
  const children = props.menu.children || []
  return children.filter(child => {
    return !!(child.component || child.path)
  })
})

const hasChildren = computed(() => visibleChildren.value.length > 0)

// 图标名称映射表
const iconMap: Record<string, string> = {
  'homepage': 'HomeFilled',
  'dashboard': 'DataLine',
  'folder': 'Folder',
  'file-text': 'Document',
  'grid': 'Grid',
  'tag': 'PriceTag',
  'message-square': 'ChatDotSquare',
  'link': 'Link',
  'setting': 'Setting',
  'tree-table': 'Menu'
}

const iconComponent = computed(() => {
  const iconName = props.menu.meta?.icon || ''
  if (!iconName || iconName === '#') return null
  
  if (iconMap[iconName]) {
    return iconMap[iconName]
  }
  
  const pascal = iconName.split(/[-_ ]+/).map(s => s.charAt(0).toUpperCase() + s.slice(1)).join('')
  return pascal
})

const fallbackLetter = computed(() => {
  const title = (props.menu.meta && props.menu.meta.title) || (props.menu.name as string) || ''
  return title ? title.charAt(0).toUpperCase() : ''
})
</script>

<style scoped>
.menu-icon {
  width: 20px;
  height: 20px;
  font-size: 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

:deep(.menu-icon svg) {
  width: 100%;
  height: 100%;
  display: block;
}

/* 回退字母图标样式 */
.menu-icon-fallback {
  width: 20px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  font-size: 12px;
  font-weight: 500;
  color: inherit;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* 菜单标题样式 */
.menu-title {
  margin-left: 12px;
}

.no-arrow :deep(.el-submenu__icon-arrow),
.no-arrow :deep(.el-submenu__title i),
.no-arrow :deep(.el-sub-menu__title__icon) {
  display: none !important;
}
</style>
