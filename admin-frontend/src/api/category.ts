import http from './http'
import type { Category, CategoryQueryParams, PageResult } from '@/types'

/**
 * 获取分类分页列表
 * 
 * @param params 查询参数，包括分页、搜索条件等
 * @returns 分页结果，包含分类列表和分页信息
 */
export function getCategoryPage(params: CategoryQueryParams): Promise<PageResult<Category>> {
  return http.get('/admin/categories/page', params)
}

/**
 * 获取分类详情
 * 
 * @param id 分类ID
 * @returns 分类详细信息
 */
export function getCategoryDetail(id: string | number): Promise<Category> {
  return http.get(`/admin/categories/${id}`)
}

/**
 * 创建分类
 *
 * @param data 分类数据
 * @returns 创建结果
 */
export function createCategory(data: Partial<Category>): Promise<string> {
  return http.post('/admin/categories', data)
}

/**
 * 更新分类
 *
 * @param id 分类ID
 * @param data 分类数据
 * @returns 更新结果
 */
export function updateCategory(id: string | number, data: Partial<Category>): Promise<string> {
  return http.put(`/admin/categories/${id}`, data)
}

/**
 * 删除分类
 *
 * @param ids 分类ID，多个ID用逗号分隔
 * @returns 删除结果
 */
export function deleteCategory(ids: string): Promise<string> {
  return http.delete(`/admin/categories/${ids}`)
}
