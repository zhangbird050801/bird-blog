import { mockTags } from './mockData'
import { get } from './http'

/**
 * 文章标签
 */
export interface Tag {
  id: number
  name: string
  articleCount?: number
}

/**
 * 获取标签列表
 */
export async function fetchTags(): Promise<Tag[]> {
  try {
    return await get<Tag[]>('/tags')
  } catch (error) {
    return mockTags
  }
}
