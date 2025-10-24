# 设计规范 — 2025-02-15 (Codex)

## API 契约

- `GET /api/articles`
  - 查询参数：`page`(int, 默认1)、`size`(int, 默认10)、`category`(long, 可选)、`tag`(long, 可选)
  - 响应：`{ total: number, items: ArticleSummary[] }`
- `GET /api/articles/{slug}`
  - 响应：`ArticleDetail`
- `GET /api/categories`
  - 响应：`Category[]`
- `GET /api/tags`
  - 响应：`Tag[]`
- `GET /api/articles/{articleId}/comments`
  - 响应：`CommentNode[]`（嵌套）
- `POST /api/articles/{articleId}/comments`
  - 请求体：`{ author: string, email?: string, content: string, parentId?: number }`
  - 响应：`CommentNode`

> 数据结构 `ArticleSummary`、`ArticleDetail`、`Category`、`Tag`、`CommentNode` 将在前后端统一。

## 后端实现策略

- 模块：`BirdBlog-backend/BirdBlog-blog`
- 技术：Spring Boot 3、MyBatis Plus（若时间不足，使用内存仓库）
- 存储：初始阶段采用内存数据集（基于 SQL 结构构造），后续可切换至数据库
- 主要类：
  - `ArticleController`, `CategoryController`, `TagController`, `CommentController`
  - `ArticleService` 等服务层封装查询逻辑
  - `ArticleRepository`（内存实现）
- 配置：
  - `application.yml` 配置端口 `8080`
  - 启用 `@CrossOrigin` 或全局 WebMvcConfigurer 允许来自 `http://localhost:5173`

## 前端架构

- 框架：Vue 3 + TypeScript + Vite
- 状态：使用 `pinia`?（为保持生态简洁，改用 `@vue/reactivity` 组合式 API + composables` useArticleStore`）
- 路由：`vue-router`，页面包括：
  - `/` 博客首页（文章列表 + 侧栏）
  - `/article/:slug` 文章详情
- 样式：
  - 建立全局 `liquid-glass.css`，使用 CSS 变量（色彩、玻璃材质、阴影）
  - 遵循 `rounded-2xl`、`rounded-full`
  - 支持暗色模式（`body.dark`）
- 组件：
  - 基础：`LgCard`, `LgButton`, `LgBadge`, `LgScrollContainer`, `LgAccordion`, `LgModal`, `LgNavbar`
  - 复合：`PostList`, `PostCard`, `SidebarPanel`, `CommentTree`, `SearchBar`
  - 动效：使用 `transition` + `cubic-bezier(0.22, 1, 0.36, 1)`
- 无第三方 UI 库，纯自定义实现。

## Liquid Glass 设计要点

- 玻璃效果：
  - 背景色：`rgba(255,255,255,0.08)`（明亮模式），暗色模式 `rgba(0,0,0,0.35)`
  - `backdrop-filter: blur(28px) saturate(160%) contrast(120%)`
  - 内发光：`box-shadow: inset 0 0 0 1px rgba(255,255,255,0.25), 0 30px 60px -40px rgba(15,23,42,0.65)`
- 动画：
  - 交互过渡 `transition: all 260ms cubic-bezier(0.22, 1, 0.36, 1)`
  - 悬浮抬升 `transform: translateY(-2px)` + 光效增强
- 字体：`font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', sans-serif;`

## 页面布局草图

1. 顶部浮动导航（玻璃胶囊），包含 Logo、主题切换、搜索按钮
2. 主区域分两栏（宽屏）：
   - 左侧：文章卡片列表，卡片内含标题、摘要、标签、日期、阅读指标
   - 右侧：侧栏卡片包含作者信息、分类、标签云、最近文章
3. 页脚：玻璃化透明区域，展示版权、社交链接
4. 文章详情：顶部横向 Hero（featured image），正文 Markdown 样式玻璃容器，底部评论区

## 测试计划

- 前端：
  - 使用 Vitest + @vue/test-utils 测试 `PostList`、`LiquidGlassButton` 等组件（渲染 +交互)
  - 使用 `msw`?（避免额外依赖，改用 `fetch` mock）
- 后端：
  - Spring Boot `@WebMvcTest` 验证 `/api/articles` 返回结构

## 风险与对策

- **组件数量庞大**：聚焦博客界面所需的核心组件，其余在设计系统中定义样式与结构规范，提供示例。
- **后端数据来源缺失**：临时使用内存假数据，保证联调可运行；后续可替换为数据库。
- **性能**：限制动画数量，使用 `will-change`，避免昂贵滤镜在低端设备大量叠加。
- **可访问性**：确保焦点状态、aria 标签、对比度。
