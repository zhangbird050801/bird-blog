import { getMockArticle, mapToSummary, mockArticles } from './mockData'
import { get, type ApiResponse, handleApiResponse } from './http'

/**
 * ArticleVO
 */
interface ArticleVO {
  id: number
  title: string
  slug: string
  summary: string
  categoryName?: string
  thumbnail?: string
  viewCount?: number
  likeCount?: number
  publishedTime?: string
}

/**
 * 分页结果
 */
interface PageResultVO<T> {
  total: number
  rows: T[]
  pageNum: number
  pageSize: number
  totalPages: number
}

/**
 * ArticleDetailVO
 */
interface ArticleDetailVO {
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
}

/**
 * 后端返回的热门文章 VO
 */
interface HotArticleVO {
  id: number
  slug: string
  title: string
  viewCount?: number
  thumbnail?: string
}

/**
 * 文章摘要信息
 */
export interface ArticleSummary {
  id: number
  title: string
  slug: string
  summary: string
  thumbnail?: string
  publishedAt?: string
  categoryName?: string
  tags: string[]
  viewCount?: number
  likeCount?: number
}

/**
 * 文章详细信息
 */
export interface ArticleDetail extends ArticleSummary {
  content: string
  commentCount?: number
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


function convertToArticleSummary(vo: ArticleVO): ArticleSummary {
  return {
    id: vo.id,
    title: vo.title,
    slug: vo.slug,
    summary: vo.summary,
    thumbnail: vo.thumbnail,
    publishedAt: vo.publishedTime,
    categoryName: vo.categoryName,
    tags: [], // 后端列表接口暂不返回标签
    viewCount: vo.viewCount,
    likeCount: vo.likeCount,
  }
}

function convertToArticleDetail(vo: ArticleDetailVO): ArticleDetail {
  return {
    id: vo.id,
    title: vo.title,
    slug: vo.slug,
    summary: vo.summary,
    content: vo.content,
    thumbnail: vo.thumbnail,
    publishedAt: vo.publishedTime,
    categoryName: vo.categoryName,
    tags: [], // 后端详情接口暂不返回标签
    viewCount: vo.viewCount,
    likeCount: vo.likeCount,
    commentCount: vo.commentCount,
  }
}

/**
 * 获取文章列表（分页）
 * @param query 查询参数（分页、筛选等）
 */
export async function fetchArticles(query: ArticleQuery = {}): Promise<PagedResponse<ArticleSummary>> {
  try {
    // /article/list?categoryId=xxx&pageNum=1&pageSize=20
    const params = new URLSearchParams()
    if (query.category) params.set('categoryId', String(query.category))
    params.set('pageNum', String(query.page ?? 1))
    params.set('pageSize', String(query.size ?? 20))
    
    const path = `/article/list?${params.toString()}`
    const response = await get<ApiResponse<PageResultVO<ArticleVO>>>(path)
    const pageResult = handleApiResponse(response)
    
    return {
      total: pageResult.total,
      items: pageResult.rows.map(convertToArticleSummary),
    }
  } catch (error) {
    console.warn('文章列表接口调用失败，使用 Mock 数据:', error)
    // 使用 Mock 数据
    const filtered = mockArticles.filter((article) => {
      let match = true
      if (query.category) {
        match &&= article.categoryName?.includes(String(query.category)) ?? false
      }
      if (query.tag) {
        match &&= article.tags.includes(String(query.tag))
      }
      if (query.q) {
        const q = query.q.toLowerCase()
        match &&=
          article.title.toLowerCase().includes(q) ||
          article.summary.toLowerCase().includes(q) ||
          article.content.toLowerCase().includes(q)
      }
      return match
    })
    const page = query.page ?? 1
    const size = query.size ?? 20
    const start = (page - 1) * size
    const items = filtered.slice(start, start + size).map(mapToSummary)
    return { total: filtered.length, items }
  }
}

/**
 * 获取文章详情（智能识别 ID 或 slug）
 * @param idOrSlug 文章ID（纯数字）或 slug
 */
export async function fetchArticleDetail(idOrSlug: string): Promise<ArticleDetail> {
  try {
    const response = await get<ApiResponse<ArticleDetailVO>>(`/article/${idOrSlug}`)
    const detail = handleApiResponse(response)
    return convertToArticleDetail(detail)
  } catch (error) {
    console.warn('文章详情接口调用失败，使用 Mock 数据:', error)
    const article = getMockArticle(idOrSlug)
    if (!article) {
      throw new Error(`文章不存在: ${idOrSlug}`)
    }
    return article
  }
}

/**
 * 获取热门文章列表
 * @param limit 返回数量限制，默认5篇
 */
export async function fetchHotArticles(limit = 5): Promise<ArticleSummary[]> {
  try {
    const response = await get<ApiResponse<HotArticleVO[]>>('/article/hot')
    const list = handleApiResponse(response)
    return list
      .slice(0, limit)
      .map((item) => ({
        id: item.id,
        slug: item.slug,
        title: item.title,
        summary: '',
        thumbnail: item.thumbnail,
        viewCount: item.viewCount,
        tags: [],
      }))
  } catch (error) {
    console.warn('热门文章接口调用失败，使用 Mock 数据:', error)
    // 使用 Mock 数据：按浏览量倒序取前 N 篇
    const items = [...mockArticles]
      .sort((a, b) => (b.viewCount ?? 0) - (a.viewCount ?? 0))
      .slice(0, limit)
      .map(mapToSummary)
    return items
  }
}
