<template>
  <!-- 有子菜单的情况：title 中包含可点击链接 -->
  <el-sub-menu v-if="hasChildren" :index="menuPath" :class="{ 'no-arrow': !!menu.component }">
    <template #title>
      <!-- 只有在菜单有 component 时才渲染可点击的 router-link，目录类菜单只作为展开/收缩用 -->
      <template v-if="menu.component">
        <router-link :to="menuPath" class="submenu-link">
          {{ menu.meta?.title }}
        </router-link>
      </template>
      <template v-else>
        <span class="submenu-link">{{ menu.meta?.title }}</span>
      </template>
    </template>

    <!-- 渲染过滤后的子菜单 -->
    <menu-item
      v-for="(child, index) in visibleChildren"
      :key="index"
      :menu="child"
      :parent-path="menuPath"
    />
  </el-sub-menu>

  <!-- 单个菜单项 -->
  <el-menu-item v-else :index="menuPath">
    <template #title>
      <router-link :to="menuPath" class="submenu-link">{{ menu.meta?.title }}</router-link>
    </template>
  </el-menu-item>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { RouteData } from '@/types'

// 递归组件需要显式定义组件名
defineOptions({
  name: 'MenuItem'
})

interface Props {
  menu: RouteData
  parentPath?: string
}

const props = defineProps<Props>()

// 计算完整菜单路径（支持相对 path）
const menuPath = computed(() => {
  const p = props.menu.path || ''
  if (p.startsWith('/')) return p
  if (props.parentPath) return `${props.parentPath}/${p}`.replace(/\/+/g, '/')
  return `/${p}`
})

// 过滤掉按钮类型的子菜单（没有 component 且 path 为空或为按钮）
const visibleChildren = computed(() => {
  const children = props.menu.children || []
  return children.filter(child => {
    // 保留有 component 或有 path 的菜单项
    return !!(child.component || child.path)
  })
})

// 仅当存在可见子菜单时，才渲染子菜单结构
const hasChildren = computed(() => visibleChildren.value.length > 0)
</script>

<style scoped>
/* 让 router-link 在菜单中看起来像普通菜单项 */
.submenu-link {
  color: inherit;
  display: flex;
  align-items: center;
  width: 100%;
  height: 100%;
  text-decoration: none;
  transition: color 0.2s ease;
}

/* 隐藏 el-sub-menu 的箭头指示（用于父级既有跳转目标时） */
.no-arrow >>> .el-submenu__icon-arrow,
.no-arrow >>> .el-submenu__title i,
.no-arrow >>> .el-sub-menu__title__icon {
  display: none !important;
}
</style>
