import { get, post, handleApiResponse, type ApiResponse } from './http'

/**
 * 友链信息
 */
export interface Link {
  id: number
  name: string
  logo: string
  description: string
  url: string
  status?: 'pending' | 'approved' | 'rejected'
  createTime?: string
  updateTime?: string
}

/**
 * 友链申请请求
 */
export interface LinkApplicationRequest {
  name: string
  logo: string
  description: string
  url: string
  contactEmail: string
  contactName: string
  message: string
}

/**
 * 获取友链列表
 *
 * @returns 友链列表数组
 * @throws 请求失败时抛出异常
 */
export async function fetchLinks(): Promise<Link[]> {
  try {
    const response = await get<ApiResponse<Link[]>>('/link/get')
    return handleApiResponse<Link[]>(response)
  } catch (error) {
    console.error('获取友链列表失败:', error)
    throw error
  }
}

/**
 * 申请友情链接
 *
 * @param request 友链申请信息
 * @throws 请求失败时抛出异常
 */
export async function applyForLink(request: LinkApplicationRequest): Promise<void> {
  try {
    const response = await post<ApiResponse<void>>('/link/apply', request)
    handleApiResponse(response)
  } catch (error) {
    console.error('申请友链失败:', error)
    throw error
  }
}

