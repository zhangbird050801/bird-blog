# BirdBlog 管理后台

基于 Vue 3 + TypeScript + Element Plus 的现代化管理后台系统。

## 技术栈

- Vue 3 (Composition API + `<script setup>`)
- TypeScript
- Element Plus (UI 组件库)
- Pinia (状态管理)
- Vue Router 4 (路由管理)
- Axios (HTTP 客户端)

## 项目结构

```
admin-frontend/
├── public/              # 静态资源
├── src/
│   ├── api/             # API 接口
│   │   ├── http.ts      # HTTP 客户端封装
│   │   └── index.ts     # API 接口定义
│   ├── assets/          # 资源文件
│   ├── components/      # 公共组件
│   ├── router/          # 路由配置
│   │   └── index.ts     # 路由定义和守卫
│   ├── stores/          # Pinia 状态管理
│   │   ├── index.ts     # Store 入口
│   │   ├── user.ts      # 用户状态
│   │   └── menu.ts      # 菜单状态
│   ├── styles/          # 全局样式
│   │   └── index.css    # 全局 CSS
│   ├── types/           # TypeScript 类型定义
│   │   └── index.ts     # 通用类型
│   ├── views/           # 页面组件
│   │   ├── Login/       # 登录页
│   │   ├── Layout/      # 布局组件
│   │   ├── dashboard/   # 首页
│   │   ├── content/     # 内容管理
│   │   ├── system/      # 系统管理
│   │   └── Error/       # 错误页面
│   ├── App.vue          # 根组件
│   ├── main.ts          # 入口文件
│   └── shims-vue.d.ts   # Vue 类型声明
├── .env.development     # 开发环境配置
├── .env.production      # 生产环境配置
├── tsconfig.json        # TypeScript 配置
├── vue.config.js        # Vue CLI 配置
└── package.json         # 项目依赖
```

## 功能特性

### 已实现

- ✅ 用户登录/登出
- ✅ 验证码验证
- ✅ Token 认证
- ✅ 动态路由加载
- ✅ 菜单权限控制
- ✅ 响应式布局
- ✅ 侧边栏折叠

### 待实现

- ⏳ 文章管理 (CRUD)
- ⏳ 分类管理
- ⏳ 标签管理
- ⏳ 评论管理
- ⏳ 友链管理
- ⏳ 菜单管理
- ⏳ 用户管理
- ⏳ 角色管理

## 快速开始

### 安装依赖

```bash
npm install --legacy-peer-deps
```

### 开发模式

```bash
npm run serve
```

访问 http://localhost:8080

### 构建生产版本

```bash
npm run build
```

## 默认账号

- 用户名: `admin`
- 密码: `123456`

## 接口地址

- 开发环境: http://localhost:7777/admin
- 生产环境: /admin (相对路径)

## 主要组件说明

### HTTP 客户端 (`src/api/http.ts`)

- 统一的请求拦截器(添加 Token)
- 统一的响应拦截器(错误处理)
- 封装 GET/POST/PUT/DELETE 方法

### 用户状态管理 (`src/stores/user.ts`)

- Token 管理
- 用户信息管理
- 登录/登出逻辑
- 本地持久化

### 菜单状态管理 (`src/stores/menu.ts`)

- 菜单数据管理
- 动态路由生成
- 组件动态加载

### 路由守卫 (`src/router/index.ts`)

- 登录状态检查
- 动态路由注入
- 页面标题设置

## 注意事项

1. **数据库表结构**: 严格遵循后端定义的数据库表结构
   - `sys_user`: 用户表
   - `sys_role`: 角色表
   - `sys_menu`: 菜单表
   - `sys_role_menu`: 角色-菜单关联表
   - `sys_user_role`: 用户-角色关联表

2. **API 规范**: 后端统一响应格式
   ```typescript
   {
     code: number,    // 200 表示成功
     msg: string,     // 提示信息
     data: any        // 响应数据
   }
   ```

3. **组件复用**: 公共组件应抽取到 `src/components/` 目录

4. **TypeScript**: 所有文件使用 TypeScript,类型定义统一放在 `src/types/`

## 常见问题

### npm install 失败

使用 `--legacy-peer-deps` 参数:
```bash
npm install --legacy-peer-deps
```

### Babel 配置警告

已在 `package.json` 中配置 `requireConfigFile: false`,可以忽略

### ESLint 错误

已禁用 `vue/multi-word-component-names` 规则

## 开发规范

1. 组件命名使用 PascalCase
2. 文件夹命名使用 kebab-case
3. 所有 API 调用统一使用 `src/api/` 中定义的方法
4. 状态管理优先使用 Pinia Store
5. 路由懒加载使用动态 import
6. 中文注释优先

## License

MIT
