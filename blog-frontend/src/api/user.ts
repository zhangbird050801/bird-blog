import http from './http'
import type { ApiResponse, PageResult, Article } from './types'

// 用户信息接口
export interface UserProfile {
  id: number
  username: string
  nickName: string
  email?: string
  phone?: string
  sex?: number // 0男，1女，2未知
  avatar?: string
  createTime: string
  favoriteCount: number
  likeCount: number
}

// 更新用户信息请求
export interface UpdateUserInfoRequest {
  nickName?: string
  email?: string
  phone?: string
  sex?: number
  avatar?: string
}

// 用户个人中心相关接口
export const userApi = {
  // 获取用户个人信息
  getUserInfo: (): Promise<ApiResponse<UserProfile>> => {
    return http.get('/user/profile/info', { auth: true })
  },

  // 更新用户个人信息
  updateUserInfo: (data: UpdateUserInfoRequest): Promise<ApiResponse<string>> => {
    return http.put('/user/profile/update', data, { auth: true })
  },

  // 上传用户头像
  uploadAvatar: (file: File): Promise<ApiResponse<string>> => {
    const formData = new FormData()
    formData.append('avatar', file)
    return http.request('/user/profile/avatar', {
      method: 'POST',
      body: formData,
      auth: true
      // 不设置 headers，让浏览器自动设置 Content-Type 为 multipart/form-data
    })
  },

  // 获取用户收藏的文章
  getFavoriteArticles: (pageNum: number = 1, pageSize: number = 10): Promise<ApiResponse<PageResult<Article>>> => {
    return http.get(`/user/profile/favorites?pageNum=${pageNum}&pageSize=${pageSize}`, { auth: true })
  },

  // 获取用户点赞的文章
  getLikedArticles: (pageNum: number = 1, pageSize: number = 10): Promise<ApiResponse<PageResult<Article>>> => {
    return http.get(`/user/profile/likes?pageNum=${pageNum}&pageSize=${pageSize}`, { auth: true })
  }
}