import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, userApi } from '@/api'
import type { BackendUserInfo, LoginRequest, LoginResponse, UserInfo } from '@/types'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore(
  'user',
  () => {
    // 状态
    const token = ref<string>('')
    const refreshToken = ref<string>('')
    const userInfo = ref<UserInfo | null>(null)
    let profilePromise: Promise<UserInfo | null> | null = null

    // 计算属性
    const isLoggedIn = computed(() => !!token.value)
    const userName = computed(() => userInfo.value?.displayName || '')
    const avatar = computed(() => userInfo.value?.avatar || '')
    const profileLoaded = computed(() => userInfo.value !== null)

    /**
     * 登录
     */
    async function login(loginData: LoginRequest): Promise<void> {
      const data = await authApi.login(loginData)
      setSession(data)
      await fetchCurrentUser(true)
    }

    /**
     * 保存登录信息
     */
    function setSession(data: LoginResponse): void {
      token.value = data.token
      refreshToken.value = data.refreshToken
      if (data.userInfo) {
        applyUserInfo(data.userInfo)
      }
    }

    /**
     * 退出登录
     */
    async function logout(): Promise<void> {
      try {
        await authApi.logout()
      } catch (error) {
        console.error('退出登录失败:', error)
      } finally {
        clearLoginInfo()
      }
    }

    /**
     * 清除登录信息
     */
    function clearLoginInfo(): void {
      token.value = ''
      refreshToken.value = ''
      userInfo.value = null
      profilePromise = null
    }

    /**
     * 刷新 Token
     */
    async function refreshAccessToken(): Promise<void> {
      if (!refreshToken.value) {
        throw new Error('RefreshToken 不存在')
      }

      const data = await authApi.refreshToken(refreshToken.value)
      setSession(data)
    }

    /**
     * 从后端拉取当前用户信息
     */
    async function fetchCurrentUser(force = false): Promise<UserInfo | null> {
      if (!token.value) {
        clearLoginInfo()
        return null
      }

      if (!force && userInfo.value) {
        return userInfo.value
      }

      if (!force && profilePromise) {
        return profilePromise
      }

      profilePromise = (async () => {
        try {
          const data = await userApi.getCurrentUser()
          return applyUserInfo(data)
        } catch (error) {
          console.error('获取用户信息失败:', error)
          return null
        } finally {
          profilePromise = null
        }
      })()

      return profilePromise
    }

    /**
     * 规范化后端返回的用户信息
     */
    function applyUserInfo(raw?: BackendUserInfo | null): UserInfo | null {
      if (!raw) {
        userInfo.value = null
        return null
      }

      const normalizedSex = normalizeSex(raw.sex)
      const normalized: UserInfo = {
        id: raw.id,
        nickName: raw.nickName || '',
        displayName: raw.nickName || extractFallbackName(raw),
        username: extractFallbackName(raw),
        email: raw.email || undefined,
        avatar: raw.avatar || undefined,
        sex: normalizedSex,
        phone: (raw as any).phone || undefined,
        type: (raw as any).type ?? undefined,
        status: (raw as any).status ?? undefined
      }

      userInfo.value = normalized
      return normalized
    }

    /**
     * 性别字段容错处理
     */
    function normalizeSex(sex?: string | number | null): 0 | 1 | 2 {
      const parsed = Number(sex)
      if (parsed === 0 || parsed === 1) {
        return parsed as 0 | 1
      }
      return 2
    }

    /**
     * 提取用户名兜底展示
     */
    function extractFallbackName(raw: BackendUserInfo | Record<string, any>): string | undefined {
      if ('username' in raw && typeof raw.username === 'string' && raw.username.length > 0) {
        return raw.username
      }
      return raw.nickName
    }

    return {
      // 状态
      token,
      refreshToken,
      userInfo,
      // 计算属性
      isLoggedIn,
      userName,
      avatar,
      profileLoaded,
      // 方法
      login,
      logout,
      setSession,
      clearLoginInfo,
      refreshAccessToken,
      fetchCurrentUser,
      applyUserInfo
    }
  },
  {
    // 持久化配置
    persist: {
      key: 'user-store',
      storage: localStorage,
      paths: ['token', 'refreshToken', 'userInfo']
    }
  }
)
