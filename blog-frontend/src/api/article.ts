import { getMockArticle, mapToSummary, mockArticles } from './mockData'
import { get, type ApiResponse } from './http'

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

/**
 * 热门文章 VO（后端返回）
 */
interface HotArticleVO {
  id: number
  slug: string
  title: string
  viewCount?: number
  thumbnail?: string
}


/**
 * 获取文章列表
 * @param query 查询参数（分页、筛选等）
 */
export async function fetchArticles(query: ArticleQuery = {}): Promise<PagedResponse<ArticleSummary>> {
  const params = new URLSearchParams()
  if (query.page) params.set('page', String(query.page))
  if (query.size) params.set('size', String(query.size))
  if (query.category) params.set('category', String(query.category))
  if (query.tag) params.set('tag', String(query.tag))
  if (query.q) params.set('q', query.q)
  const path = `/articles${params.size ? `?${params.toString()}` : ''}`
  
  try {
    return await get<PagedResponse<ArticleSummary>>(path)
  } catch (error) {
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
    const size = query.size ?? filtered.length
    const start = (page - 1) * size
    const items = filtered.slice(start, start + size).map(mapToSummary)
    return { total: filtered.length, items }
  }
}

/**
 * 获取文章详情
 * @param slug 文章唯一标识
 */
export async function fetchArticleDetail(slug: string): Promise<ArticleDetail> {
  try {
    return await get<ArticleDetail>(`/articles/${slug}`)
  } catch (error) {
    const article = getMockArticle(slug)
    if (!article) {
      throw error
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
    const res = await get<ApiResponse<HotArticleVO[]>>('/article/hot')
    const list = res.data ?? []
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
    // 使用 Mock 数据：按浏览量倒序取前 N 篇
    const items = [...mockArticles]
      .sort((a, b) => (b.viewCount ?? 0) - (a.viewCount ?? 0))
      .slice(0, limit)
      .map(mapToSummary)
    return items
  }
}
