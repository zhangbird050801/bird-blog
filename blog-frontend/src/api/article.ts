import { get, type ApiResponse, handleApiResponse, post, del } from './http'
import type { PageResultVO } from './types'

/**
 * 文章列表 VO
 */
export interface ArticleVO {
  id: number
  title: string
  slug: string
  summary: string
  categoryName?: string
  thumbnail?: string
  viewCount?: number
  likeCount?: number
  publishedTime?: string
  isTop?: boolean
  tags?: Array<{ id: number; name: string; remark?: string }>
}

/**
 * 文章详情 VO
 */
export interface ArticleDetailVO {
  id: number
  title: string
  slug: string
  summary: string
  content: string
  categoryId?: number
  categoryName?: string
  thumbnail?: string
  viewCount?: number
  likeCount?: number
  commentCount?: number
  isComment?: boolean
  publishedTime?: string
  isLiked?: boolean
}

/**
 * 热门文章 VO
 */
export interface HotArticleVO {
  id: number
  slug: string
  title: string
  viewCount?: number
  thumbnail?: string
}

/**
 * 最新文章 VO
 */
export interface LatestArticleVO extends HotArticleVO {
  publishedTime?: string
}

/**
 * 相关文章 VO
 */
export interface RelatedArticleVO {
  id: number
  title: string
  slug: string
  categoryName?: string
  thumbnail?: string
}

/**
 * 相邻文章 VO
 */
export interface AdjacentArticleVO {
  id: number
  title: string
  slug: string
  thumbnail?: string
}

/**
 * 上一篇/下一篇文章集合 VO
 */
export interface AdjacentArticlesVO {
  previous?: AdjacentArticleVO
  next?: AdjacentArticleVO
}

/**
 * 通用文章卡片接口
 */
export interface ArticleCardInfo {
  id: number
  slug: string
  title: string
  thumbnail?: string
  publishedTime?: string
}

/**
 * 文章查询参数
 */
export interface ArticleQuery {
  page?: number
  size?: number
  category?: number
  tag?: number
  q?: string
}

/**
 * 分页响应数据
 */
export interface PagedResponse<T> {
  total: number
  items: T[]
}

/**
 * 获取文章列表
 * @param query 查询参数
 */
export async function fetchArticles(query: ArticleQuery = {}): Promise<PagedResponse<ArticleVO>> {
  const params = new URLSearchParams()
  if (query.category) params.set('categoryId', String(query.category))
  if (query.tag) params.set('tagId', String(query.tag))
  params.set('pageNum', String(query.page ?? 1))
  params.set('pageSize', String(query.size ?? 20))

  // 如果有搜索关键词，使用搜索接口并返回结果
  if (query.q) {
    const searchResults = await searchArticles(query.q)
    return {
      total: searchResults.length,
      items: searchResults,
    }
  }

  const path = `/article/list?${params.toString()}`
  const response = await get<ApiResponse<PageResultVO<ArticleVO>>>(path)
  const pageResult = handleApiResponse(response)

  return {
    total: pageResult.total,
    items: pageResult.rows,
  }
}

/**
 * 获取文章详情
 * @param idOrSlug 文章ID（纯数字）或 slug
 */
export async function fetchArticleDetail(idOrSlug: string): Promise<ArticleDetailVO> {
  const response = await get<ApiResponse<ArticleDetailVO>>(`/article/${idOrSlug}`)
  const detail = handleApiResponse(response)
  return detail 
}

/**
 * 获取热门文章列表
 * @param limit 返回数量限制，默认5篇
 */
export async function fetchHotArticles(limit = 5): Promise<HotArticleVO[]> {
  const response = await get<ApiResponse<HotArticleVO[]>>('/article/hot')
  const list = handleApiResponse(response)
  return list.slice(0, limit) 
}

/**
 * 获取最新文章列表
 * @returns 最新文章列表
 */
export async function fetchLatestArticles(): Promise<LatestArticleVO[]> {
  const response = await get<ApiResponse<LatestArticleVO[]>>('/article/latest')
  return handleApiResponse(response)
}

/**
 * 获取相关文章列表
 * @param articleId 当前文章ID
 * @returns 相关文章列表
 */
export async function fetchRelatedArticles(articleId: number): Promise<RelatedArticleVO[]> {
  const response = await get<ApiResponse<RelatedArticleVO[]>>(`/article/related/${articleId}`)
  return handleApiResponse(response)
}

/**
 * 获取上一篇和下一篇文章
 * @param articleId 当前文章ID
 * @returns 上一篇和下一篇文章
 */
export async function fetchAdjacentArticles(articleId: number): Promise<AdjacentArticlesVO> {
  const response = await get<ApiResponse<AdjacentArticlesVO>>(`/article/adjacent/${articleId}`)
  return handleApiResponse(response)
}

/**
 * 搜索文章
 * @param keyword 搜索关键词
 * @returns 搜索结果
 */
export async function searchArticles(keyword: string): Promise<ArticleVO[]> {
  if (!keyword || !keyword.trim()) {
    return []
  }

  const params = new URLSearchParams()
  params.set('keyword', keyword.trim())

  const response = await get<ApiResponse<ArticleVO[]>>(`/article/search?${params.toString()}`)
  return handleApiResponse(response)
}

/**
 * 点赞文章
 * @param articleId 文章ID
 * @returns 点赞结果
 */
export async function likeArticle(articleId: number): Promise<boolean> {
  const response = await post<ApiResponse<boolean>>(`/article/like/${articleId}`, null, {
    auth: true
  })
  return handleApiResponse(response)
}

/**
 * 取消点赞文章
 * @param articleId 文章ID
 * @returns 取消点赞结果
 */
export async function unlikeArticle(articleId: number): Promise<boolean> {
  const response = await del<ApiResponse<boolean>>(`/article/like/${articleId}`, {
    auth: true
  })
  return handleApiResponse(response)
}

/**
 * 检查文章点赞状态
 * @param articleId 文章ID
 * @returns 是否已点赞
 */
export async function checkLikeStatus(articleId: number): Promise<boolean> {
  const response = await get<ApiResponse<boolean>>(`/article/like/status/${articleId}`, {
    auth: true
  })
  return handleApiResponse(response)
}

/**
 * 获取文章点赞数
 * @param articleId 文章ID
 * @returns 点赞数量
 */
export async function getLikeCount(articleId: number): Promise<number> {
  const response = await get<ApiResponse<number>>(`/article/like/count/${articleId}`)
  return handleApiResponse(response)
}
