/**
 * 统一响应格式
 */
export interface ApiResponse<T = any> {
  code: number
  msg?: string
  message?: string
  data?: T
}

/**
 * API 基础路径配置
 */
const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')

/**
 * 是否使用 Mock 数据
 */
const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

const _env = import.meta.env as any
const TOKEN_KEY = _env.VITE_TOKEN_KEY || 'blog_token'
const REFRESH_TOKEN_KEY = _env.VITE_REFRESH_TOKEN_KEY || 'blog_refresh_token'

// Token 刷新相关状态
let isRefreshingToken = false
let pendingRequests: Array<{
  resolve: (value: any) => void
  reject: (reason?: any) => void
  retryRequest: () => Promise<any>
}> = []

/**
 * HTTP 请求配置选项
 */
export interface RequestOptions extends RequestInit {
  /** 是否需要 token 认证 */
  auth?: boolean
  /** 自定义请求头 */
  headers?: HeadersInit
}

/**
 * 通用 HTTP 请求函数
 * @param path API 路径（相对路径，会自动拼接 API_BASE）
 * @param options 请求配置选项
 * @returns Promise<T> 返回解析后的数据
 * @throws 当启用 Mock 或请求失败时抛出错误
 */
export async function request<T = any>(
  path: string,
  options: RequestOptions = {}
): Promise<T> {
  // 如果启用 Mock，抛出错误让调用方处理
  if (USE_MOCK) {
    throw new Error('使用 Mock 数据')
  }

  const url = `${API_BASE}${path}`
  const { auth = false, headers = {}, ...restOptions } = options

  // 构建请求头
  const requestHeaders: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    ...(headers as Record<string, string>),
  }

  // 如果需要认证，添加 token
  if (auth) {
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) {
      requestHeaders['Authorization'] = `Bearer ${token}`
    }
  }

  // 发起请求
  const response = await fetch(url, {
    headers: requestHeaders,
    ...restOptions,
  })

  // 检查 HTTP 状态码
  if (!response.ok) {
    // 如果是 401 未授权，尝试刷新 Token
    if (response.status === 401 && auth && !path.includes('/auth/refresh-token')) {
      return handleTokenRefresh(path, options)
    }
    throw new Error(`HTTP 请求失败: ${response.status} ${response.statusText}`)
  }

  // 解析响应数据
  const result = await response.json()

  // 检查业务状态码（如果响应中有 code 字段）
  if (result.code === 401 && auth && !path.includes('/auth/refresh-token')) {
    return handleTokenRefresh(path, options)
  }

  return result as T
}

/**
 * 处理 Token 刷新
 */
async function handleTokenRefresh<T>(path: string, options: RequestOptions): Promise<T> {
  return new Promise((resolve, reject) => {
    // 封装需要重试的请求
    const retryRequest = () => request<T>(path, options)

    // 将请求加入等待队列
    pendingRequests.push({ resolve, reject, retryRequest })

    // 如果没有正在刷新，则开始刷新流程
    if (!isRefreshingToken) {
      isRefreshingToken = true

      const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY)
      if (!refreshToken) {
        // 没有 RefreshToken，直接跳转登录
        rejectAllPendingRequests('没有刷新令牌，请重新登录')
        isRefreshingToken = false
        return
      }

      // 调用刷新 Token 接口
      fetch(`${API_BASE}/auth/refresh-token?refreshToken=${refreshToken}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      })
        .then((res) => res.json())
        .then((result) => {
          if (result.code === 200 && result.data) {
            // 刷新成功，更新 Token
            const { token, refreshToken: newRefreshToken } = result.data
            localStorage.setItem(TOKEN_KEY, token)
            localStorage.setItem(REFRESH_TOKEN_KEY, newRefreshToken)

            // 重试所有等待的请求
            retryAllPendingRequests()
          } else {
            // 刷新失败，清空队列并跳转登录
            rejectAllPendingRequests('刷新令牌失败，请重新登录')
          }
        })
        .catch(() => {
          rejectAllPendingRequests('刷新令牌失败，请重新登录')
        })
        .finally(() => {
          isRefreshingToken = false
        })
    }
  })
}

/**
 * 重试所有等待的请求
 */
function retryAllPendingRequests() {
  pendingRequests.forEach(({ resolve, reject, retryRequest }) => {
    retryRequest().then(resolve).catch(reject)
  })
  pendingRequests = []
}

/**
 * 拒绝所有等待的请求
 */
function rejectAllPendingRequests(errorMsg: string) {
  pendingRequests.forEach(({ reject }) => {
    reject(new Error(errorMsg))
  })
  pendingRequests = []
  
  // 清除本地存储并提示用户
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
  console.warn(errorMsg)
  // 可以在这里触发跳转到登录页的逻辑
}

/**
 * 处理后端统一响应格式
 * 自动提取 data 字段，如果响应码不为成功则抛出错误
 * @param response 后端响应对象
 * @param successCode 成功状态码，默认为 200
 * @returns 提取后的 data 数据
 * @throws 当响应码不为成功时抛出错误
 */
export function handleApiResponse<T>(
  response: ApiResponse<T>,
  successCode = 200
): T {
  if (response.code === successCode) {
    return response.data as T
  }

  const errorMsg = response.msg || response.message || '请求失败'
  throw new Error(`API 错误 [${response.code}]: ${errorMsg}`)
}

/**
 * GET 请求
 * @param path API 路径
 * @param options 请求配置
 */
export async function get<T = any>(
  path: string,
  options?: RequestOptions
): Promise<T> {
  return request<T>(path, { ...options, method: 'GET' })
}

/**
 * POST 请求
 * @param path API 路径
 * @param data 请求体数据
 * @param options 请求配置
 */
export async function post<T = any>(
  path: string,
  data?: any,
  options?: RequestOptions
): Promise<T> {
  return request<T>(path, {
    ...options,
    method: 'POST',
    body: JSON.stringify(data),
  })
}

/**
 * PUT 请求
 * @param path API 路径
 * @param data 请求体数据
 * @param options 请求配置
 */
export async function put<T = any>(
  path: string,
  data?: any,
  options?: RequestOptions
): Promise<T> {
  return request<T>(path, {
    ...options,
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

/**
 * DELETE 请求
 * @param path API 路径
 * @param options 请求配置
 */
export async function del<T = any>(
  path: string,
  options?: RequestOptions
): Promise<T> {
  return request<T>(path, { ...options, method: 'DELETE' })
}

/**
 * 导出默认的 HTTP 客户端
 */
export default {
  request,
  get,
  post,
  put,
  delete: del,
  handleApiResponse,
}
