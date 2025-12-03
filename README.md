# BirdBlog

Monorepo 博客项目，包含前台站点、管理后台与 Java 后端服务。本文汇总模块结构、启动方式、数据表定义、现有功能与待补项。

## 模块结构
- `BirdBlog-backend`：Spring Boot 多模块后端，含 `BirdBlog-blog`（前台接口）、`BirdBlog-admin`（后台接口）、`BirdBlog-framework`（通用组件）、`sql/birdblog1127.sql`（初始化脚本）。
- `blog-frontend`：Vite + Vue 3 + TypeScript 前台博客。
- `admin-frontend`：Vue CLI + Vue 2 + Element UI 管理后台。

## 快速启动
1) 初始化数据库  
   - 创建 MySQL 库，导入 `BirdBlog-backend/sql/birdblog1127.sql`（包含示例数据、角色、菜单）。  
2) 启动后端  
   - 需要 JDK 17、Maven。  
   - `cd BirdBlog-backend` 后，分别启动：
     - 前台接口：`mvn -pl BirdBlog-blog spring-boot:run`
     - 后台接口：`mvn -pl BirdBlog-admin spring-boot:run`
   - 若需修改端口/Redis/MySQL 连接，请在对应模块的 `application*.yml` 中调整。  
3) 启动前台站点  
   - 需要 Node 18+。  
   - `cd blog-frontend && npm install && npm run dev`（生产构建 `npm run build`）。  
4) 启动管理后台  
   - `cd admin-frontend && npm install && npm run serve`（生产构建 `npm run build`）。  
5) 联调建议  
   - 确认前后端使用的 API 基址（dev 环境可通过前端 `.env*` 文件或 axios 创建处配置）。  
   - 确保浏览器指向后台接口的域名/端口时携带 JWT，避免跨域问题。

## 数据表定义（birdblog1127.sql 摘要）
- `bg_article`：文章表；核心字段含 `title/slug/summary/content/category_id/author_id`、状态 `status(0发布/1草稿)`、`is_comment`、计数器 `view_count/like_count/comment_count`、发布时间与置顶时间；`slug` 唯一且全文索引。
- `bg_article_like`：文章点赞记录；`article_id`+`user_id` 唯一，记录创建/更新时间，软删标记。
- `bg_article_tag`：文章标签关联；复合主键 `article_id/tag_id`，级联删除。
- `bg_category`：分类表；`name` 唯一，支持父子结构（`pid`），状态 `0正常/1禁用`，带计数字段。
- `bg_comment`：评论表；`type(0文章/1友链)`、`article_id`、`root_id/parent_id` 构建楼中楼、`from_user_id/to_user_id`、状态与点赞数，外键级联到文章/用户/自身。
- `bg_link`：友链表；`name/logo/url/description`，`status(0审核通过/1审核中/2审核不通过/3失效)`。
- `bg_tag`：标签表；`name` 唯一，`status(0正常/1禁用)`。
- `sys_menu`：菜单与路由表；`name/path/component/parent_id`、`icon`、`type(0目录/1菜单/2按钮)`、`visible`、`status`、`perms`、`order_num` 等，用于后台动态路由与权限。
- `sys_role`：角色表；`name/code/description/status`，示例角色：SUPER_ADMIN、EDITOR、REVIEWER、VISITOR。
- `sys_role_menu`：角色-菜单关联；`role_id/menu_id` 唯一，级联删除。
- `sys_user`：用户表；`username/nick_name/password/status/email/phone/sex/avatar`，软删除标记，用户名与邮箱唯一。
- `sys_user_role`：用户-角色关联；`user_id/role_id` 唯一，级联删除。
- 其他对象：视图 `vw_bg_article_public`（仅发布文章列表），存储过程 `sp_publish_article`（将草稿置为发布并回写时间）。

## 现有后端功能
### 前台接口（`BirdBlog-blog`）
- 文章：热门、最新、列表（按分类/标签）、搜索、相关推荐、上一篇/下一篇、详情支持 ID 或 slug。
- 分类/标签：获取全部分类，获取全部标签，按文章拉取标签。
- 评论：获取文章/友链的树形评论；登录用户可新增评论。
- 点赞：登录用户对文章点赞/取消点赞，查询点赞状态与数量。
- 友链：获取友链列表，提交友链申请（含校验）。
- 认证：验证码获取、登录、注册、刷新 Token、登出、修改密码（LoginScene.FRONT）。

### 管理后台接口（`BirdBlog-admin`）
- 认证：验证码、管理员登录/登出、刷新 Token、修改密码（LoginScene.ADMIN）。
- 菜单与路由：获取当前用户路由、菜单树查询、下拉选项、菜单新增/修改/删除。
- 文章：分页查询、详情、创建、更新、删除。
- 分类/标签：列表查询、增删改、绑定文章（由服务层处理）。
- 评论：后台评论分页与审核（见 `AdminCommentController`）。
- 友链：友链列表与审核（`AdminLinkController`）。
- 角色：角色列表、创建/更新、绑定菜单权限（`AdminRoleController`、`SysRoleMenuService`）。
- 用户：获取当前用户信息、分页查询用户、启用/停用用户、更新用户角色。
- 上传：`UploadController` 提供文件上传接口（具体存储实现见服务层配置）。

## 前端能力
- 前台站点：文章列表/详情渲染、Markdown + 代码高亮、分类与标签筛选、评论树展示与提交、友链展示与申请、文章点赞。
- 管理后台：基于菜单接口的动态路由与侧边栏，文章/分类/标签/评论/友链/用户/角色管理，文件上传与登录会话管理。

## 示例数据与账号
- 角色：SUPER_ADMIN、EDITOR、REVIEWER、VISITOR 已预置并绑定菜单权限。
- 用户：`sys_user` 中含 `admin` 及多个示例用户，密码为 bcrypt 哈希（请根据实际密钥或登录逻辑设置明文密码）。
- 文章/评论/标签/分类/友链：脚本已填充大量示例，便于直接演示前台与后台功能。

## 待补与风险
- 测试缺口：项目未包含自动化测试（后端 JUnit/集成、前端单测/E2E 均缺失），上线前需补充或进行手工验证。
- 部署指引缺失：未提供环境变量示例、端口约定、反向代理/网关配置与容器化说明，需补充才能一键部署。
- 权限与鉴权说明不足：虽有角色/菜单/注解配置，但 README 尚未描述 JWT 携带方式、跨域方案、后台接口的权限拦截链；需结合实际 Security 配置补全文档。
- 账号信息：默认账号明文密码未记录，需在运维文档中标注或重置。
- 监控与运维：未包含日志/监控/告警、数据备份策略。

## 维护提示
- 修改数据库结构后同步更新 `sql/birdblog1127.sql` 与本文档。
- 为新功能编写对应的接口说明与最小自测步骤，保持 README 可落地。
