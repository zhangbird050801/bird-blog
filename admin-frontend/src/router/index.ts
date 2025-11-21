import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'

/**
 * 静态路由配置
 * 这些路由不需要权限控制
 */
const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login/index.vue'),
    meta: {
      title: '登录',
      hidden: true
    }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/Error/403.vue'),
    meta: {
      title: '403',
      hidden: true
    }
  },
  {
    path: '/',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes: constantRoutes
})

/**
 * 路由守卫
 */
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const menuStore = useMenuStore()

  // 设置页面标题
  document.title = (to.meta.title as string) || 'BirdBlog 管理后台'

  // 如果访问登录页,且已登录,则跳转到首页
  if (to.path === '/login') {
    if (userStore.isLoggedIn) {
      next({ path: '/' })
    } else {
      next()
    }
    return
  }

  // 如果未登录,跳转到登录页
  if (!userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  // 确保用户信息已加载
  if (!userStore.profileLoaded) {
    try {
      await userStore.fetchCurrentUser()
    } catch (error) {
      console.error('获取用户信息失败:', error)
      userStore.clearLoginInfo()
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
  }

  // 如果已登录但没有加载菜单路由,则加载
  if (menuStore.routes.length === 0) {
    try {
      const dynamicRoutes = await menuStore.loadMenuRoutes()

      // 添加动态路由
      dynamicRoutes.forEach((route) => {
        router.addRoute(route)
      })

      // 添加 404 路由(必须在最后)
      router.addRoute({
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/Error/404.vue'),
        meta: {
          title: '404',
          hidden: true
        }
      })

      // 重新触发导航,确保动态路由生效
      next({ ...to, replace: true })
    } catch (error) {
      console.error('加载菜单路由失败:', error)
      // 清除登录信息并跳转到登录页
      userStore.clearLoginInfo()
      menuStore.clearMenus()
      next({ path: '/login' })
    }
    return
  }

  // 权限检查
  if (to.meta.roles && Array.isArray(to.meta.roles) && to.meta.roles.length > 0) {
    const requiredRoles = to.meta.roles as string[]
    console.log('路由需要权限:', requiredRoles)

    // 检查用户是否有所需权限
    const hasPermission = requiredRoles.some(role => {
      // 检查角色权限
      if (userStore.hasRole(role)) {
        return true
      }
      // 检查具体权限
      if (userStore.hasPermission(role)) {
        return true
      }
      return false
    })

    if (!hasPermission) {
      console.warn('用户没有访问权限，需要:', requiredRoles)
      next({ path: '/403' })
      return
    }
  }

  next()
})

export default router
