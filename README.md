# Bird Blog 项目说明

Bird Blog 是一个前后端分离的内容站点，目标是为博主提供沉浸式的 Liquid Glass 风格阅读体验，并支持后台管理、文章发布等场景。仓库包含前端展示、后台管理界面以及 Spring Boot 多模块后端。

## 仓库结构

```
BirdBlog
├── BirdBlog-backend           # Spring Boot 多模块后端工程
│   ├── BirdBlog-framework     # 共用配置与基础能力
│   ├── BirdBlog-blog          # 博客文章服务（读者端接口）
│   └── BirdBlog-admin         # 管理后台服务（增删改等）
├── blog-frontend              # 博客前台（Liquid Glass 主题，Vue3 + TS）
├── admin-frontend             # 管理后台前端（待实现）
├── README.md                  # 项目说明（当前文件）
└── AGENTS.md                  # Codex 操作规范（请勿删除）
```

## 技术栈概览

| 方向 | 主要框架/工具 | 说明 |
| --- | --- | --- |
| 前台展示 | Vite 5、Vue 3、TypeScript、Vue Router | 构建 Liquid Glass 风格博客界面，支持暗色模式与动态主题。
| 样式 | 原生 CSS + 自定义变量 | 统一 `rounded-2xl` / `rounded-full` 曲线；自定义 Glass 效果与 hover 动效。
| 测试 | Vitest、@vue/test-utils | 组件级单元测试（`npm run test:unit`）。
| 后端 | Spring Boot 3、Java 17、MyBatis Plus | 多模块结构，`BirdBlog-blog` 提供博客 API，`BirdBlog-admin` 负责后台管理。
| 数据层 | MySQL（参考 `BirdBlog-backend/sql`） | 提供文章、分类、标签、评论等表结构。

## admin-frontend

## 前端目录说明（`blog-frontend`）

```
blog-frontend
├── src
│   ├── api/                  # API 客户端、类型定义与 Mock 数据
│   ├── assets/               # 全局样式（液态玻璃变量、基础样式）
│   ├── components/
│   │   ├── base/             # 基础组件：LgButton、LgCard、LgInput 等
│   │   ├── blog/             # 博客业务组件：ArticleCard、LatestPostsList 等
│   │   └── layout/           # 导航栏、页脚等布局组件
│   ├── composables/          # 主题切换、异步数据管理等复用逻辑
│   ├── router/               # Vue Router 路由配置
│   ├── views/                # 页面级组件（HomeView、ArticleView）
│   ├── App.vue               # 应用根组件
│   └── main.ts               # 入口文件
├── env.d.ts & tsconfig.json  # TypeScript 配置
├── vite.config.js            # Vite 构建与代理配置
└── tests/                    # Vitest 组件单元测试
```

### Liquid Glass UI 规范

- **材质与曲线**：所有容器默认 `rounded-2xl`，徽章、芯片等使用 `rounded-full`。
- **高亮规则**：交互元素在 hover 时采用 `box-shadow: 0 0 0 3px #50CCD5`、圆角 8px 并伴随轻微放大，确保视觉一致。
- **主题切换**：通过 `useTheme` 组合式函数控制，支持本地存储记忆和暗色模式。
- **可访问性**：重点交互组件遵循语义标签与 `aria-label`，使用 `focus-visible` 样式强化键盘可用性。

## 后端模块说明（`BirdBlog-backend`）

- `BirdBlog-framework`：公共配置、异常处理、响应封装等基础能力。
- `BirdBlog-blog`：对外开放的文章、分类、标签、评论等接口，当前包含内存样例数据，可扩展至数据库。
- `BirdBlog-admin`：面向后台的管理接口（草稿、审核、发布等），待按需扩展。
- `sql/`：初始化脚本，定义文章、分类、标签、评论、用户等核心表结构。

## 开发与运行

### 前台（blog-frontend）

```bash
cd blog-frontend
npm install
npm run dev        # 启动本地开发服务器
npm run build      # 生产构建
npm run test:unit  # 组件单元测试
```

默认开发服务器运行在 `http://127.0.0.1:5173`，通过 Vite 代理请求 `/api` 转发到 `http://localhost:8080`。

### 后端（BirdBlog-backend）

```bash
cd BirdBlog-backend
```

> 注意：`BirdBlog-blog` 当前使用内存模拟数据，若要接入真实数据库，请配置数据源并导入 `sql/` 中的表结构。

## 编码规范与约定

1. **TypeScript 首选**：前端组件统一使用 `<script setup lang="ts">`，保持类型安全。
2. **CSS 变量**：所有配色、半径、阴影、滤镜通过 `src/assets/base.css` 定义的变量统一管理。
3. **模块化组件**：基础组件放置于 `components/base`，业务组件放置在 `components/blog` 或对应领域目录。
4. **API 调用**：统一通过 `src/api/client.ts`，同时提供 Mock 数据保证前后端解耦开发。
5. **日志与计划**：按照 `AGENTS.md` 要求，在 `.codex/` 目录维护上下文、日志、测试记录。

## 后续规划

- 后端接入真实数据库与鉴权逻辑。
- 管理后台前端 (`admin-frontend`) 初始化与权限控制。
- 完善 Vitest 覆盖率与端到端测试脚本。
- 设计更多 Liquid Glass 主题组件（如时间轴、弹窗、评论表单等）。

---

如需了解 Codex 环境内运行规范，请参考仓库根目录的 `AGENTS.md`。欢迎根据实际需求调整配色、交互与模块划分。
