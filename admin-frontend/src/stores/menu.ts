import { defineStore } from 'pinia'
import { ref } from 'vue'
import { menuApi } from '@/api'
import type { RouteData } from '@/types'
import type { RouteRecordRaw } from 'vue-router'

/**
 * 菜单状态管理
 */
export const useMenuStore = defineStore('menu', () => {
  // 状态
  const menuList = ref<RouteData[]>([])
  const routes = ref<RouteRecordRaw[]>([])

  /**
   * 加载菜单路由
   */
  async function loadMenuRoutes(): Promise<RouteRecordRaw[]> {
    try {
      console.log('开始加载菜单路由...')
      const routeData = await menuApi.getRoutes()
      console.log('后端返回的路由数据:', routeData)
      menuList.value = routeData
      routes.value = buildRoutes(routeData)
      console.log('构建的路由配置:', routes.value)
      return routes.value
    } catch (error) {
      console.error('加载菜单路由失败:', error)
      throw error
    }
  }

  /**
   * 构建路由配置
   * 将后端返回的路由数据转换为 vue-router 路由配置
   */
  function buildRoutes(routeData: RouteData[], parentPath = ''): RouteRecordRaw[] {
    const routeList: RouteRecordRaw[] = []

    routeData.forEach((item) => {
      // 过滤掉按钮类型的菜单（没有 component 且 path 为空）
      if (!item.component && !item.path) {
        console.log('跳过按钮类型菜单:', item.meta?.title)
        return
      }

      console.log('处理路由项:', item)
      
      // 构建完整路径
      const fullPath = item.path.startsWith('/') 
        ? item.path 
        : parentPath ? `${parentPath}/${item.path}` : `/${item.path}`
      
      const route: RouteRecordRaw = {
        path: item.path,
        name: item.name || item.path,
        component: loadComponent(item.component),
        meta: {
          title: item.meta.title,
          icon: item.meta.icon,
          hidden: item.meta.hidden || false,
          alwaysShow: item.meta.alwaysShow || false,
          roles: item.meta.roles,
          fullPath: fullPath  // 保存完整路径用于菜单高亮
        }
      } as RouteRecordRaw

      // 如果有重定向
      if (item.redirect) {
        (route as any).redirect = item.redirect
      }

      // 递归处理子路由
      if (item.children && item.children.length > 0) {
        (route as any).children = buildRoutes(item.children, fullPath)
      }

      console.log('构建的路由:', route)
      routeList.push(route)
    })

    return routeList
  }

  /**
   * 动态加载组件
   */
  function loadComponent(component?: string) {
    console.log('加载组件:', component)
    
    if (!component || component === 'Layout') {
      return () => import('@/views/Layout/index.vue')
    }

    // 使用映射表来加载组件，避免动态导入路径问题
    const componentMap: Record<string, () => Promise<any>> = {
      'dashboard/index': () => import('@/views/dashboard/index.vue'),
      'content/article/index': () => import('@/views/content/article/index.vue'),
      'content/category/index': () => import('@/views/content/category/index.vue'),
      'content/tag/index': () => import('@/views/content/tag/index.vue'),
      'content/comment/index': () => import('@/views/content/comment/index.vue'),
      'content/link/index': () => import('@/views/content/link/index.vue'),
      'system/menu/index': () => import('@/views/system/menu/index.vue'),
    }

    const loader = componentMap[component]
    if (loader) {
      console.log('使用映射加载组件:', component)
      return loader
    }

    console.warn(`未找到组件映射: ${component}，使用404页面`)
    return () => import('@/views/Error/404.vue')
  }

  /**
   * 清除菜单数据
   */
  function clearMenus(): void {
    menuList.value = []
    routes.value = []
  }

  return {
    menuList,
    routes,
    loadMenuRoutes,
    clearMenus
  }
})
