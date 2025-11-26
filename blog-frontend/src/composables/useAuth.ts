/**
 * 用户认证状态管理
 */

import { ref, computed } from 'vue'
import type { UserInfo } from '@/api'

const _env = import.meta.env as any
const TOKEN_KEY = _env.VITE_TOKEN_KEY || 'blog_token'
const REFRESH_TOKEN_KEY = _env.VITE_REFRESH_TOKEN_KEY || 'blog_refresh_token'
const USER_INFO_KEY = _env.VITE_USER_INFO_KEY || 'blog_user_info'

// 全局状态
const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
const refreshToken = ref<string | null>(localStorage.getItem(REFRESH_TOKEN_KEY))
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
  function setAuth(newToken: string, newRefreshToken: string, newUserInfo: UserInfo) {
    console.log('setAuth called with:', {
      token: newToken?.substring(0, 20) + '...',
      refreshToken: newRefreshToken?.substring(0, 20) + '...',
      userInfo: newUserInfo
    })

    token.value = newToken
    refreshToken.value = newRefreshToken
    userInfo.value = newUserInfo

    // 保存到 localStorage
    localStorage.setItem(TOKEN_KEY, newToken)
    localStorage.setItem(REFRESH_TOKEN_KEY, newRefreshToken)
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(newUserInfo))

    console.log('Token stored in localStorage:', !!localStorage.getItem(TOKEN_KEY))
  }

  /**
   * 清除登录信息
   */
  function clearAuth() {
    token.value = null
    refreshToken.value = null
    userInfo.value = null
    
    // 清除 localStorage
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    localStorage.removeItem(USER_INFO_KEY)
  }

  /**
   * 获取 Token
   */
  function getToken(): string | null {
    return token.value
  }

  /**
   * 获取 RefreshToken
   */
  function getRefreshToken(): string | null {
    return refreshToken.value
  }

  /**
   * 获取用户信息
   */
  function getUserInfo(): UserInfo | null {
    return userInfo.value
  }

  /**
   * 更新 Token（用于刷新令牌后更新）
   */
  function updateTokens(newToken: string, newRefreshToken: string) {
    token.value = newToken
    refreshToken.value = newRefreshToken
    
    localStorage.setItem(TOKEN_KEY, newToken)
    localStorage.setItem(REFRESH_TOKEN_KEY, newRefreshToken)
  }

  return {
    isLoggedIn,
    token: computed(() => token.value),
    refreshToken: computed(() => refreshToken.value),
    userInfo: computed(() => userInfo.value),
    setAuth,
    clearAuth,
    getToken,
    getRefreshToken,
    getUserInfo,
    updateTokens,
  }
}
