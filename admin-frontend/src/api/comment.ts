import http from './http'
import type { Comment, CommentQueryParams, PageResult } from '@/types'

/**
 * 获取评论分页列表
 *
 * @param params 查询参数，包括分页、搜索条件等
 * @returns 分页结果，包含评论列表和分页信息
 */
export function getCommentPage(params: CommentQueryParams): Promise<PageResult<Comment>> {
  return http.get('/admin/comments/page', params)
}

/**
 * 获取评论详情
 *
 * @param id 评论ID
 * @returns 评论详细信息
 */
export function getCommentDetail(id: string | number): Promise<Comment> {
  return http.get(`/admin/comments/${id}`)
}

/**
 * 更新评论状态
 *
 * @param id 评论ID
 * @param status 状态：0正常，1屏蔽
 * @returns 更新结果
 */
export function updateCommentStatus(id: string | number, status: number): Promise<void> {
  return http.put(`/admin/comments/${id}/status?status=${status}`)
}

/**
 * 批量删除评论
 *
 * @param ids 评论ID，多个ID用逗号分隔
 * @returns 删除结果
 */
export function deleteComments(ids: string): Promise<void> {
  return http.delete(`/admin/comments/${ids}`)
}