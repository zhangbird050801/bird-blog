import { mockCategories } from './mockData'
import { get } from './http'

/**
 * 文章分类
 */
export interface Category {
  id: number
  name: string
  slug?: string
  description?: string
  articleCount?: number
}

/**
 * 获取分类列表
 */
export async function fetchCategories(): Promise<Category[]> {
  try {
    return await get<Category[]>('/categories')
  } catch (error) {
    return mockCategories
  }
}
