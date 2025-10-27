import { getMockArticleById, getMockComments } from './mockData'
import { get } from './http'

/**
 * 评论节点（树形结构）
 */
export interface CommentNode {
  id: number
  author: string
  content: string
  createdAt?: string
  replies: CommentNode[]
}

/**
 * 获取文章评论列表
 * @param articleId 文章ID
 */
export async function fetchComments(articleId: number): Promise<CommentNode[]> {
  try {
    return await get<CommentNode[]>(`/articles/${articleId}/comments`)
  } catch (error) {
    const article = getMockArticleById(articleId)
    if (!article) {
      throw error
    }
    return getMockComments(articleId)
  }
}
