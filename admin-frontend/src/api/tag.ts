import http from './http'
import type { Tag, TagQueryParams, PageResult } from '@/types'

/**
 * 获取标签分页列表
 *
 * @param params 查询参数，包括分页、搜索条件等
 * @returns 分页结果，包含标签列表和分页信息
 */
export function getTagPage(params: TagQueryParams): Promise<PageResult<Tag>> {
  return http.get('/admin/tags/page', params)
}

/**
 * 获取标签详情
 *
 * @param id 标签ID
 * @returns 标签详细信息
 */
export function getTagDetail(id: string | number): Promise<Tag> {
  return http.get(`/admin/tags/${id}`)
}

/**
 * 创建标签
 *
 * @param data 标签数据
 * @returns 创建结果
 */
export function createTag(data: Partial<Tag>): Promise<string> {
  return http.post('/admin/tags', data)
}

/**
 * 更新标签
 *
 * @param id 标签ID
 * @param data 标签数据
 * @returns 更新结果
 */
export function updateTag(id: string | number, data: Partial<Tag>): Promise<string> {
  return http.put(`/admin/tags/${id}`, data)
}

/**
 * 删除标签
 *
 * @param ids 标签ID，多个ID用逗号分隔
 * @returns 删除结果
 */
export function deleteTag(ids: string): Promise<string> {
  return http.delete(`/admin/tags/${ids}`)
}
