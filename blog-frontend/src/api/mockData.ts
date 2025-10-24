import type { ArticleDetail, ArticleSummary, Category, CommentNode, Tag } from './types'

const now = new Date()

export const mockArticles: ArticleDetail[] = [
  {
    id: 1,
    title: 'BirdBlog 博客界面设计心法',
    slug: 'liquid-glass-design',
    summary: '拆解 BirdBlog 哲学，打造具备沉浸感的博客体验。',
    content:
      '<p>BirdBlog 设计系统源自 visionOS 的深度质感，将玻璃材质与动态光影融合。本文分享如何在 Web 上复刻这份体验，包括变量体系、动效节奏与组件结构设计。</p>',
    thumbnail: 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=80',
    publishedAt: new Date(now.getTime() - 1000 * 60 * 60 * 24 * 2).toISOString(),
    categoryName: '探索',
    tags: ['BirdBlog', '设计'],
    viewCount: 3200,
    likeCount: 420,
    commentCount: 6,
  },
  {
    id: 2,
    title: 'Vue 3 组合式 API 在内容站点的应用',
    slug: 'vue3-composition-blog',
    summary: '讲述如何使用 Vue 3 组合式 API 构建可维护的博客模块，以 BirdBlog 样式为例。',
    content:
      '<p>组合式 API 能够拆分语义模块，配合 TypeScript 可读性更高。本文展示文章数据获取、暗色模式切换与滚动动画等场景。</p>',
    thumbnail: 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=80',
    publishedAt: new Date(now.getTime() - 1000 * 60 * 60 * 24 * 5).toISOString(),
    categoryName: '教程',
    tags: ['Vue3', 'BirdBlog'],
    viewCount: 2100,
    likeCount: 188,
    commentCount: 4,
  },
  {
    id: 3,
    title: '性能与质感的平衡：BirdBlog 的优化策略',
    slug: 'liquid-glass-performance',
    summary: '探讨在启用大量滤镜与模糊效果时，如何维持页面性能。',
    content:
      '<p>通过使用 will-change、适度的滤镜强度与延迟渲染策略，可以让 BirdBlog 既有质感又不牺牲性能。</p>',
    thumbnail: 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1200&q=80',
    publishedAt: new Date(now.getTime() - 1000 * 60 * 60 * 24 * 9).toISOString(),
    categoryName: '探索',
    tags: ['性能', 'BirdBlog'],
    viewCount: 1560,
    likeCount: 156,
    commentCount: 3,
  },
]

export const mockCategories: Category[] = [
  { id: 1, name: '探索', slug: 'explore', description: '探索自然与科技灵感', articleCount: 8 },
  { id: 2, name: '教程', slug: 'tutorial', description: '前端与设计技巧', articleCount: 5 },
  { id: 3, name: '思考', slug: 'insight', description: '记录创作背后的洞见', articleCount: 3 },
]

export const mockTags: Tag[] = [
  { id: 1, name: 'BirdBlog', articleCount: 12 },
  { id: 2, name: 'Vue3', articleCount: 6 },
  { id: 3, name: '设计', articleCount: 9 },
  { id: 4, name: '性能', articleCount: 4 },
  { id: 5, name: '案例', articleCount: 3 },
]

export const mockComments: Record<number, CommentNode[]> = {
  1: [
    {
      id: 201,
      author: 'Mia',
      content: '好喜欢这种有呼吸感的界面，期待更多组件拆解！',
      createdAt: new Date(now.getTime() - 1000 * 60 * 60 * 20).toISOString(),
      replies: [
        {
          id: 202,
          author: 'Noah',
          content: '同感！尤其是 hover 光感的处理太妙了。',
          createdAt: new Date(now.getTime() - 1000 * 60 * 60 * 12).toISOString(),
          replies: [],
        },
      ],
    },
  ],
  2: [
    {
      id: 203,
      author: 'Liam',
      content: '组合式 API + BirdBlog 的示例太有帮助了。',
      createdAt: new Date(now.getTime() - 1000 * 60 * 60 * 30).toISOString(),
      replies: [],
    },
  ],
}

export function getMockArticle(slug: string): ArticleDetail | undefined {
  return mockArticles.find((article) => article.slug === slug)
}

export function getMockArticleById(id: number): ArticleDetail | undefined {
  return mockArticles.find((article) => article.id === id)
}

export function getMockComments(articleId: number): CommentNode[] {
  return mockComments[articleId] ?? []
}

export function mapToSummary(article: ArticleDetail): ArticleSummary {
  const { content: _content, commentCount, ...summary } = article
  return summary
}
