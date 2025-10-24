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

export interface ArticleDetail extends ArticleSummary {
  content: string
  commentCount?: number
}

export interface Category {
  id: number
  name: string
  slug?: string
  description?: string
  articleCount?: number
}

export interface Tag {
  id: number
  name: string
  articleCount?: number
}

export interface CommentNode {
  id: number
  author: string
  content: string
  createdAt?: string
  replies: CommentNode[]
}

export interface PagedResponse<T> {
  total: number
  items: T[]
}

export interface ArticleQuery {
  page?: number
  size?: number
  category?: number
  tag?: number
  q?: string
}
