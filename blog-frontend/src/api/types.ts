export interface PageResultVO<T> {
	total: number
	rows: T[]
	pageNum: number
	pageSize: number
	totalPages: number
}

// 通用API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页结果类型
export interface PageResult<T> {
  total: number
  rows: T[]
}

// 文章类型
export interface Article {
  id: number
  title: string
  slug?: string
  summary?: string
  thumbnail?: string
  isTop?: boolean
  viewCount?: number
  likeCount?: number
  publishedTime?: string
  categoryName?: string
}