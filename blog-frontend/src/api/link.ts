import { get, handleApiResponse, type ApiResponse } from './http'

/**
 * 友链信息
 */
export interface Link {
  id: number
  name: string
  logo: string
  description: string
  url: string
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

