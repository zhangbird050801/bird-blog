import http from './http'
import type { ApiResponse, PageResult, Article } from './types'

// 收藏相关接口
export const favoriteApi = {
  // 收藏/取消收藏文章
  toggleFavorite: (articleId: number): Promise<ApiResponse<{ isFavorited: boolean; message: string }>> => {
    return http.post(`/favorite/toggle/${articleId}`, {}, { auth: true })
  },

  // 检查文章收藏状态
  checkFavoriteStatus: (articleId: number): Promise<ApiResponse<{ isFavorited: boolean }>> => {
    return http.get(`/favorite/status/${articleId}`, { auth: true })
  },

  // 获取用户收藏的文章列表
  getFavoriteArticles: (pageNum: number = 1, pageSize: number = 10): Promise<ApiResponse<PageResult<Article>>> => {
    return http.get(`/favorite/list?pageNum=${pageNum}&pageSize=${pageSize}`, { auth: true })
  }
}