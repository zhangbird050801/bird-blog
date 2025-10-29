import type { ArticleDetailVO } from './article'
import type { Category } from './category'
import type { Tag } from './tag'
import type { CommentNode } from './comment'

const now = new Date()

export const mockArticles: ArticleDetailVO[] = [
  {
    id: 1,
    title: 'BirdBlog 博客界面设计心法',
    slug: 'liquid-glass-design',
    summary: '拆解 BirdBlog 哲学，打造具备沉浸感的博客体验。',
    content:
      '<p>BirdBlog 设计系统源自 visionOS 的深度质感，将玻璃材质与动态光影融合。本文分享如何在 Web 上复刻这份体验，包括变量体系、动效节奏与组件结构设计。</p>',
    thumbnail: '/src/assets/wallhaven-1qppz3.png',
    publishedTime: new Date(now.getTime() - 1000 * 60 * 60 * 24 * 2).toISOString(),
    categoryName: '探索',
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
    thumbnail: '/src/assets/wallhaven-d8633m.jpg',
    publishedTime: new Date(now.getTime() - 1000 * 60 * 60 * 24 * 5).toISOString(),
    categoryName: '教程',
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
    publishedTime: new Date(now.getTime() - 1000 * 60 * 60 * 24 * 9).toISOString(),
    categoryName: '探索',
    viewCount: 1560,
    likeCount: 156,
    commentCount: 3,
  },
  {
    id: 4,
    title: 'TypeScript 类型体操实战指南',
    slug: 'typescript-advanced-types',
    summary: '深入探讨 TypeScript 高级类型系统，提升代码质量与开发效率。',
    content:
      '<p>本文介绍条件类型、映射类型、infer 关键字等高级特性，帮助你写出更优雅的类型代码。</p>',
    thumbnail: 'https://images.unsplash.com/photo-1516116216624-53e697fedbea?auto=format&fit=crop&w=1200&q=80',
    publishedTime: new Date(now.getTime() - 1000 * 60 * 60 * 24 * 12).toISOString(),
    categoryName: '教程',
    viewCount: 1890,
    likeCount: 245,
    commentCount: 8,
  },
  {
    id: 5,
    title: 'CSS Grid 布局完全指南',
    slug: 'css-grid-layout-guide',
    summary: '从基础到进阶，全面掌握 CSS Grid 布局系统。',
    content:
      '<p>CSS Grid 是现代网页布局的利器，本文通过实例演示如何构建响应式网格系统。</p>',
    thumbnail: 'https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?auto=format&fit=crop&w=1200&q=80',
    publishedTime: new Date(now.getTime() - 1000 * 60 * 60 * 24 * 15).toISOString(),
    categoryName: '教程',
    viewCount: 2340,
    likeCount: 312,
    commentCount: 12,
  },
  {
    id: 6,
    title: '前端性能优化实践总结',
    slug: 'frontend-performance-optimization',
    summary: '分享前端性能优化的常用技巧和最佳实践。',
    content:
      '<p>涵盖资源加载优化、渲染性能提升、代码分割等多个方面的实用技巧。</p>',
    thumbnail: 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=1200&q=80',
    publishedTime: new Date(now.getTime() - 1000 * 60 * 60 * 24 * 18).toISOString(),
    categoryName: '探索',
    viewCount: 2890,
    likeCount: 387,
    commentCount: 15,
  },
]

export const mockCategories: Category[] = [
  { id: 1, name: '探索', slug: 'explore', description: '探索自然与科技灵感', count: 8 },
  { id: 2, name: '教程', slug: 'tutorial', description: '前端与设计技巧', count: 5 },
  { id: 3, name: '思考', slug: 'insight', description: '记录创作背后的洞见', count: 3 },
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

export function getMockArticle(slug: string): ArticleDetailVO | undefined {
  return mockArticles.find((article) => article.slug === slug)
}

export function getMockArticleById(id: number): ArticleDetailVO | undefined {
  return mockArticles.find((article) => article.id === id)
}

export function getMockComments(articleId: number): CommentNode[] {
  return mockComments[articleId] ?? []
}
