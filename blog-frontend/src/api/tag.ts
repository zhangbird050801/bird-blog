import { mockTags } from './mockData'
import { get, handleApiResponse, type ApiResponse } from './http'

/**
 * 文章标签
 */
export interface Tag {
  id: number
  name: string
  remark?: string
  articleCount?: number
}

/**
 * 获取标签列表
 */
export async function fetchTags(): Promise<Tag[]> {
  try {
    const response = await get<ApiResponse<Tag[]>>('/tag/list')
    return handleApiResponse(response)
  } catch (error) {
    console.error('获取标签列表失败:', error)
    return mockTags
  }
}

/**
 * 根据文章ID获取标签列表
 */
export async function fetchArticleTags(articleId: number): Promise<Tag[]> {
  try {
    const response = await get<ApiResponse<Tag[]>>(`/tag/article/${articleId}`)
    return handleApiResponse(response)
  } catch (error) {
    console.error(`获取文章${articleId}的标签失败:`, error)
    return []
  }
}
