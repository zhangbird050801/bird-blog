import http from './http'
import type { PageResult } from '@/types'

/**
 * 文章信息
 */
export interface Article {
  id: number
  title: string
  slug?: string
  summary?: string
  content?: string
  categoryId?: number
  categoryName?: string
  authorId?: number
  authorName?: string
  thumbnail?: string
  isTop?: boolean
  status: number
  statusDesc?: string
  isComment?: boolean
  viewCount?: number
  likeCount?: number
  commentCount?: number
  publishedTime?: string
  pinnedTime?: string
  createTime?: string
  updateTime?: string
  creator?: string
  updater?: string
  deleted?: boolean
  tagIds?: number[]
  newTags?: string[]
  newTagsDetail?: { name: string; remark?: string }[]
  newCategoryName?: string
  newTagRemark?: string
  newCategoryRemark?: string
}

/**
 * 文章查询参数
 */
export interface ArticleQueryParams {
  pageNum?: number
  pageSize?: number
  keyword?: string
  categoryId?: number
  status?: number
  authorId?: number
  isTop?: boolean
}


/**
 * 获取文章分页列表
 *
 * @param params 查询参数，包括分页、搜索条件等
 * @returns 分页结果，包含文章列表和分页信息
 */
export function getArticlePage(params: ArticleQueryParams): Promise<PageResult<Article>> {
  return http.get('/admin/article/list', params)
}

/**
 * 获取文章详情
 *
 * @param id 文章ID
 * @returns 文章详细信息
 */
export function getArticleDetail(id: string | number): Promise<Article> {
  return http.get(`/admin/article/${id}`)
}

/**
 * 创建文章
 *
 * @param data 文章数据
 * @returns 创建结果
 */
export function createArticle(data: Partial<Article>): Promise<void> {
  return http.post('/admin/article', data)
}

/**
 * 更新文章
 *
 * @param id 文章ID
 * @param data 文章数据
 * @returns 更新结果
 */
export function updateArticle(id: string | number, data: Partial<Article>): Promise<void> {
  return http.put(`/admin/article/${id}`, data)
}

/**
 * 删除文章
 *
 * @param ids 文章ID，多个ID用逗号分隔
 * @returns 删除结果
 */
export function deleteArticle(ids: string): Promise<void> {
  return http.delete(`/admin/article/${ids}`)
}

/**
 * 发布文章（将草稿状态改为已发布）
 *
 * @param id 文章ID
 * @returns 发布结果
 */
export function publishArticle(id: string | number): Promise<void> {
  return http.put(`/admin/article/${id}/publish`)
}

/**
 * 将文章设为草稿
 */
export function draftArticle(id: string | number): Promise<void> {
  return http.put(`/admin/article/${id}/draft`)
}

/**
 * 置顶/取消置顶文章
 *
 * @param id 文章ID
 * @param isTop 是否置顶
 * @returns 操作结果
 */
export function toggleArticleTop(id: string | number, isTop: boolean): Promise<void> {
  return http.put(`/admin/article/${id}/top`, { isTop })
}
