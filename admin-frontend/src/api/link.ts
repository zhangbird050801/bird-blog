import http from './http'
import type { Link, LinkQueryParams, PageResult } from '@/types'

/**
 * 获取友链分页列表
 *
 * @param params 查询参数，包括分页、搜索条件等
 * @returns 分页结果，包含友链列表和分页信息
 */
export function getLinkPage(params: LinkQueryParams): Promise<PageResult<Link>> {
  return http.get('/admin/links/page', params)
}

/**
 * 获取友链详情
 *
 * @param id 友链ID
 * @returns 友链详细信息
 */
export function getLinkDetail(id: string | number): Promise<Link> {
  return http.get(`/admin/links/${id}`)
}

/**
 * 创建友链
 *
 * @param data 友链数据
 * @returns 创建结果
 */
export function createLink(data: Partial<Link>): Promise<string> {
  return http.post('/admin/links', data)
}

/**
 * 更新友链
 *
 * @param id 友链ID
 * @param data 友链数据
 * @returns 更新结果
 */
export function updateLink(id: string | number, data: Partial<Link>): Promise<string> {
  return http.put(`/admin/links/${id}`, data)
}

/**
 * 删除友链
 *
 * @param ids 友链ID，多个ID用逗号分隔
 * @returns 删除结果
 */
export function deleteLink(ids: string): Promise<string> {
  return http.delete(`/admin/links/${ids}`)
}
