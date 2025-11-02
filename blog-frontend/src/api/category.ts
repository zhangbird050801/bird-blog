import { get, handleApiResponse, type ApiResponse } from './http'

/**
 * 文章分类
 */
export interface Category {
  id: number
  name: string
  slug?: string
  description?: string
  /** 分类下的文章数量(后端暂不返回,前端显示为0) */
  count?: number
}

/**
 * 获取分类列表
 *
 * @returns 分类列表数组
 */
export async function fetchCategories(): Promise<Category[]> {
  try {
    const response = await get<ApiResponse<Category[]>>('/category/get')
    return handleApiResponse(response)
  } catch (error) {
    console.error('获取分类列表失败:', error)
    return []
  }
}
