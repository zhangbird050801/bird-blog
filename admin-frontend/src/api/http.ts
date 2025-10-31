import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

/**
 * HTTP 客户端封装
 * 统一处理请求拦截、响应拦截、错误处理
 */
class HttpClient {
  private instance: AxiosInstance

  constructor(config: AxiosRequestConfig) {
    this.instance = axios.create(config)
    this.setupInterceptors()
  }

  /**
   * 设置请求和响应拦截器
   */
  private setupInterceptors(): void {
    // 请求拦截器
    this.instance.interceptors.request.use(
      (config) => {
        const userStore = useUserStore()
        const token = userStore.token

        // 如果存在 token 则附加认证头
        if (token) {
          config.headers = config.headers || {}
          config.headers.Authorization = `Bearer ${token}`
        }

        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // 响应拦截器
    this.instance.interceptors.response.use(
      (response: AxiosResponse) => {
        const { data } = response

        // 后端统一返回格式: { code, msg, data }
        if (data.code === 200) {
          return data.data
        } else {
          ElMessage.error(data.msg || '请求失败')
          return Promise.reject(new Error(data.msg || '请求失败'))
        }
      },
      (error) => {
        if (error.response) {
          const { status } = error.response

          switch (status) {
            case 401:
              ElMessage.error('未授权,请重新登录')
              const userStore = useUserStore()
              userStore.clearLoginInfo()
              router.push('/login')
              break
            case 403:
              ElMessage.error('没有权限访问该资源')
              break
            case 404:
              ElMessage.error('请求的资源不存在')
              break
            case 500:
              ElMessage.error('服务器错误')
              break
            default:
              ElMessage.error(error.response.data?.msg || '请求失败')
          }
        } else {
          ElMessage.error('网络错误,请检查网络连接')
        }

        return Promise.reject(error)
      }
    )
  }

  /**
   * GET 请求
   */
  get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.get(url, { params, ...config })
  }

  /**
   * POST 请求
   */
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.post(url, data, config)
  }

  /**
   * PUT 请求
   */
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.put(url, data, config)
  }

  /**
   * DELETE 请求
   */
  delete<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    return this.instance.delete(url, { params, ...config })
  }
}

// 创建 HTTP 客户端实例
const http = new HttpClient({
  baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8989',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

export default http
