import type {
  ArticleDetail,
  ArticleQuery,
  ArticleSummary,
  Category,
  CommentNode,
  PagedResponse,
  Tag,
} from './types'
import {
  getMockArticle,
  getMockArticleById,
  getMockComments,
  mapToSummary,
  mockArticles,
  mockCategories,
  mockTags,
} from './mockData'

const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api').replace(/\/$/, '')

// 是否使用 Mock 数据（开发模式默认使用）
const USE_MOCK = import.meta.env.VITE_USE_MOCK !== 'false'

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  // 如果启用 Mock，直接抛出错误让 catch 处理
  if (USE_MOCK) {
    throw new Error('使用 Mock 数据')
  }
  
  const url = `${API_BASE}${path}`
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    },
    ...init,
  })
  if (!response.ok) {
    throw new Error(`请求失败: ${response.status}`)
  }
  return response.json() as Promise<T>
}

export async function fetchArticles(query: ArticleQuery = {}): Promise<PagedResponse<ArticleSummary>> {
  const params = new URLSearchParams()
  if (query.page) params.set('page', String(query.page))
  if (query.size) params.set('size', String(query.size))
  if (query.category) params.set('category', String(query.category))
  if (query.tag) params.set('tag', String(query.tag))
  if (query.q) params.set('q', query.q)
  const path = `/articles${params.size ? `?${params.toString()}` : ''}`
  try {
    return await request<PagedResponse<ArticleSummary>>(path)
  } catch (error) {
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

export async function fetchArticleDetail(slug: string): Promise<ArticleDetail> {
  try {
    return await request<ArticleDetail>(`/articles/${slug}`)
  } catch (error) {
    const article = getMockArticle(slug)
    if (!article) {
      throw error
    }
    return article
  }
}

export async function fetchCategories(): Promise<Category[]> {
  try {
    return await request<Category[]>('/categories')
  } catch (error) {
    return mockCategories
  }
}

export async function fetchTags(): Promise<Tag[]> {
  try {
    return await request<Tag[]>('/tags')
  } catch (error) {
    return mockTags
  }
}

export async function fetchComments(articleId: number): Promise<CommentNode[]> {
  try {
    return await request<CommentNode[]>(`/articles/${articleId}/comments`)
  } catch (error) {
    const article = getMockArticleById(articleId)
    if (!article) {
      throw error
    }
    return getMockComments(articleId)
  }
}
