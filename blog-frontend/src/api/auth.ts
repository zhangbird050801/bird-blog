import { request, handleApiResponse, type ApiResponse } from './http'

/**
 * 验证码响应
 */
export interface CaptchaResponse {
  img: string  // Base64 编码的验证码图片
  uuid: string // 验证码唯一标识
}

/**
 * 登录请求参数
 */
export interface LoginRequest {
  userName: string
  password: string
  code: string  // 验证码
  uuid: string  // 验证码 UUID
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number
  nickName: string
  avatar: string
  sex: string
  email: string
}

/**
 * 登录响应
 */
export interface LoginResponse {
  token: string
  userInfo: UserInfo
}

/**
 * 获取验证码
 */
export async function fetchCaptcha(): Promise<CaptchaResponse> {
  try {
    const response = await request<ApiResponse<CaptchaResponse>>('/auth/captcha')
    return handleApiResponse(response)
  } catch (error) {
    console.error('获取验证码失败:', error)
    throw error
  }
}

/**
 * 用户登录
 */
export async function login(data: LoginRequest): Promise<LoginResponse> {
  try {
    const response = await request<ApiResponse<LoginResponse>>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    })
    return handleApiResponse(response)
  } catch (error) {
    console.error('登录失败:', error)
    throw error
  }
}

/**
 * 用户退出登录
 */
export async function logout(): Promise<void> {
  try {
    const response = await request<ApiResponse<void>>('/auth/logout', {
      method: 'POST',
      auth: true,
    })
    handleApiResponse(response)
  } catch (error) {
    console.error('退出登录失败:', error)
    throw error
  }
}
