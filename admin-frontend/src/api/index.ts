import http from './http'
import type {
  LoginRequest,
  LoginResponse,
  CaptchaResponse,
  MenuItem,
  RouteData,
  BackendUserInfo
} from '@/types'

/**
 * 认证相关 API
 */
export const authApi = {
  /**
   * 获取验证码
   */
  getCaptcha(): Promise<CaptchaResponse> {
    return http.get('/auth/captcha')
  },

  /**
   * 登录
   */
  login(data: LoginRequest): Promise<LoginResponse> {
    return http.post('/auth/login', data)
  },

  /**
   * 退出登录
   */
  logout(): Promise<void> {
    return http.post('/auth/logout')
  },

  /**
   * 刷新 Token
   */
  refreshToken(refreshToken: string): Promise<LoginResponse> {
    return http.post('/auth/refresh-token', null, {
      params: { refreshToken }
    })
  }
}

/**
 * 菜单相关 API
 */
export const menuApi = {
  /**
   * 获取当前用户的路由菜单（后端已处理为路由格式）
   */
  getRoutes(): Promise<RouteData[]> {
    return http.get('/menus/routes')
  },

  /**
   * 获取菜单列表(树形)
   */
  getMenuList(): Promise<MenuItem[]> {
    return http.get('/menus')
  },

  /**
   * 获取菜单详情
   */
  getMenuDetail(id: number): Promise<MenuItem> {
    return http.get(`/menus/${id}`)
  }
}

/**
 * 用户信息相关 API
 */
export const userApi = {
  /**
   * 获取当前登录用户信息
   */
  getCurrentUser(): Promise<BackendUserInfo> {
    return http.get('/users/me')
  }
}
