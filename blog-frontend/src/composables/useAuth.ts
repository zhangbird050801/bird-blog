/**
 * 用户认证状态管理
 */

import { ref, computed } from 'vue'
import type { UserInfo } from '@/api'

const _env = import.meta.env as any
const TOKEN_KEY = _env.VITE_TOKEN_KEY || 'blog_token'
const USER_INFO_KEY = _env.VITE_USER_INFO_KEY || 'blog_user_info'

// 全局状态
const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
const userInfo = ref<UserInfo | null>(getUserInfoFromStorage())

/**
 * 从 localStorage 获取用户信息
 */
function getUserInfoFromStorage(): UserInfo | null {
  const userInfoStr = localStorage.getItem(USER_INFO_KEY)
  if (!userInfoStr) return null
  
  try {
    return JSON.parse(userInfoStr)
  } catch {
    return null
  }
}

/**
 * 用户认证状态管理
 */
export function useAuth() {
  // 是否已登录
  const isLoggedIn = computed(() => !!token.value && !!userInfo.value)

  /**
   * 设置登录信息
   */
  function setAuth(newToken: string, newUserInfo: UserInfo) {
    token.value = newToken
    userInfo.value = newUserInfo
    
    // 保存到 localStorage
    localStorage.setItem(TOKEN_KEY, newToken)
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(newUserInfo))
  }

  /**
   * 清除登录信息
   */
  function clearAuth() {
    token.value = null
    userInfo.value = null
    
    // 清除 localStorage
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_INFO_KEY)
  }

  /**
   * 获取 Token
   */
  function getToken(): string | null {
    return token.value
  }

  /**
   * 获取用户信息
   */
  function getUserInfo(): UserInfo | null {
    return userInfo.value
  }

  return {
    isLoggedIn,
    token: computed(() => token.value),
    userInfo: computed(() => userInfo.value),
    setAuth,
    clearAuth,
    getToken,
    getUserInfo,
  }
}
