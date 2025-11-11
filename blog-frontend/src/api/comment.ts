import { get, post, handleApiResponse, type ApiResponse } from './http'

/**
 * 评论类型枚举
 */
export enum CommentType {
  ARTICLE = 'ARTICLE',
  LINK = 'LINK'
}

/**
 * 后端返回的评论VO
 */
export interface CommentVO {
  id: number
  articleId: number | null
  linkId: number | null
  type: CommentType
  rootId: number | null
  content: string
  fromUserId: number
  fromUserName: string
  fromUserAvatar: string | null
  toUserId: number | null
  toUserName: string | null
  toUserAvatar: string | null
  createTime: string
  children: CommentVO[]
}

/**
 * 添加评论请求
 */
export interface AddCommentRequest {
  articleId?: number
  linkId?: number
  type: CommentType
  rootId?: number | null
  parentId?: number | null
  toUserId?: number | null
  content: string
}

/**
 * 获取文章评论列表
 * @param articleId 文章ID
 */
export async function fetchComments(articleId: number): Promise<CommentVO[]> {
  try {
    const response = await get<ApiResponse<CommentVO[]>>(`/comment/article/${articleId}`)
    return handleApiResponse(response)
  } catch (error) {
    console.error('获取评论失败:', error)
    // 返回空数组而不是 mock 数据
    return []
  }
}

/**
 * 获取友链评论列表
 * @param linkId 友链ID
 */
export async function fetchLinkComments(linkId: number): Promise<CommentVO[]> {
  try {
    const response = await get<ApiResponse<CommentVO[]>>(`/comment/link/${linkId}`)
    return handleApiResponse(response)
  } catch (error) {
    console.error('获取友链评论失败:', error)
    return []
  }
}

/**
 * 添加评论
 * @param request 评论请求
 */
export async function addComment(request: AddCommentRequest): Promise<void> {
  const response = await post<ApiResponse<void>>('/comment/add', request, { auth: true })
  handleApiResponse(response)
}
