/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80406 (8.4.6)
 Source Host           : localhost:3306
 Source Schema         : birdblog

 Target Server Type    : MySQL
 Target Server Version : 80406 (8.4.6)
 File Encoding         : 65001

 Date: 31/10/2025 18:37:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bg_article
-- ----------------------------
DROP TABLE IF EXISTS `bg_article`;
CREATE TABLE `bg_article` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `slug` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'URL别名（便于SEO）',
  `summary` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '正文内容',
  `category_id` bigint unsigned NOT NULL COMMENT '分类ID',
  `author_id` bigint unsigned NOT NULL COMMENT '作者(用户ID)',
  `thumbnail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缩略图URL',
  `is_top` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否置顶',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0已发布，1草稿',
  `is_comment` bit(1) NOT NULL DEFAULT b'1' COMMENT '允许评论',
  `view_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '浏览量',
  `like_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '点赞量',
  `comment_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '评论数',
  `published_time` datetime DEFAULT NULL COMMENT '发布时间',
  `pinned_time` datetime DEFAULT NULL COMMENT '置顶时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_bg_article_slug` (`slug`) USING BTREE,
  KEY `idx_bg_article_cat` (`category_id`) USING BTREE,
  KEY `idx_bg_article_pub` (`status`,`deleted`,`published_time`) USING BTREE,
  KEY `fk_article_author` (`author_id`) USING BTREE,
  FULLTEXT KEY `ft_article_text` (`title`,`summary`,`content`) /*!50100 WITH PARSER `ngram` */ ,
  CONSTRAINT `fk_article_author` FOREIGN KEY (`author_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `bg_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `bg_article_chk_1` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=10044 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='文章表';

-- ----------------------------
-- Records of bg_article
-- ----------------------------
BEGIN;
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10001, 'SpringBoot 快速上手', 'springboot-quickstart', '五分钟创建第一个 SpringBoot 项目', '# SpringBoot 入门\n\n本文演示如何通过 Spring Initializr 创建项目……', 13, 2, NULL, b'1', 0, b'1', 337, 8, 12, '2025-10-24 23:15:53', '2025-10-24 23:15:53', 'author1', '2025-10-24 23:15:53', 'author1', '2025-10-31 16:47:39', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10002, 'MySQL 性能优化清单', 'mysql-tuning-checklist', '从索引到执行计划的实战清单', '# MySQL 优化\n\n覆盖索引、联合索引、慢查询……', 11, 2, NULL, b'0', 0, b'1', 1039, 22, 1, '2025-10-24 23:15:53', NULL, 'author1', '2025-10-24 23:15:53', 'author1', '2025-10-31 02:55:57', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10003, '写给自己的效率笔记', 'personal-efficiency-notes', '一些提升效率的小经验', '# 效率笔记\n\n键位设置、脚本自动化、番茄工作法……', 12, 2, NULL, b'0', 1, b'1', 10, 1, 0, NULL, NULL, 'author1', '2025-10-24 23:15:53', 'author1', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10004, '演示文章 01：指南', 'demo-post-01', '简明讲解第 1 篇示例，涵盖要点与代码片段', '## 功能概述\n\nblog-frontend 现已支持完整的 Markdown 渲染和代码高亮功能。\n\n## 使用的技术栈\n\n- **markdown-it**: Vue 3 生态标准 Markdown 解析器\n- **highlight.js**: 业界标准代码高亮库\n- **atom-one-dark 主题**: 专业的深色代码主题\n\n## 支持的 Markdown 特性\n\n### 1. 标题\n支持 h1-h6 所有级别的标题\n\n### 2. 文本格式\n- **粗体文本**\n- *斜体文本*\n- ~~删除线~~\n- `行内代码`\n\n### 3. 列表\n- 无序列表项 1\n- 无序列表项 2\n  - 嵌套项\n\n1. 有序列表项 1\n2. 有序列表项 2\n\n### 4. 引用块\n> 这是一段引用文本\n> 支持多行引用\n\n### 5. 代码块（带语法高亮）\n\n#### JavaScript 示例\n```javascript\nfunction hello(name) {\n  console.log(`Hello, ${name}!`)\n  return {\n    message: \'Welcome to BirdBlog\',\n    timestamp: Date.now()\n  }\n}\n```\n\n#### TypeScript 示例\n```typescript\ninterface User {\n  id: number\n  name: string\n  email: string\n}\n\nasync function fetchUser(id: number): Promise<User> {\n  const response = await fetch(`/api/users/${id}`)\n  return response.json()\n}\n```\n\n#### Python 示例\n```python\ndef fibonacci(n):\n    \"\"\"计算斐波那契数列\"\"\"\n    if n <= 1:\n        return n\n    return fibonacci(n-1) + fibonacci(n-2)\n\n# 使用示例\nfor i in range(10):\n    print(f\"F({i}) = {fibonacci(i)}\")\n```\n\n#### Java 示例\n```java\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, BirdBlog!\");\n        \n        // 创建列表\n        List<String> tags = Arrays.asList(\"Vue\", \"TypeScript\", \"Markdown\");\n        tags.forEach(System.out::println);\n    }\n}\n```\n\n### 6. 表格\n| 语言 | 框架 | 用途 |\n|------|------|------|\n| JavaScript | Vue 3 | 前端框架 |\n| TypeScript | - | 类型系统 |\n| Java | Spring Boot | 后端框架 |\n\n### 7. 链接和图片\n[访问 BirdBlog 项目](https://github.com/zhangbird050801/bird-blog)\n\n### 8. 分割线\n---\n\n## 使用方式\n\n### 后端返回 Markdown 格式\n后端的 `ArticleDetailVO.content` 字段应该返回 Markdown 格式的文本：\n\n```json\n{\n  \"id\": 1,\n  \"title\": \"示例文章\",\n  \"content\": \"# 这是标题\\\\n\\\\n这是正文...\\\\n\\\\n```javascript\\\\nconst x = 1\\\\n```\"\n}\n```\n\n### 前端自动渲染\n前端会自动将 Markdown 渲染为 HTML 并应用代码高亮：\n\n1. `useMarkdown` composable 负责解析 Markdown\n2. `highlight.js` 自动识别代码语言并高亮\n3. 样式自动应用于渲染后的内容\n\n## 样式特点\n\n### 代码块样式\n- 深色背景（#282c34）\n- 圆角设计（12px）\n- 阴影效果\n- 顶部渐变装饰\n- 响应式字体大小\n- 支持横向滚动\n\n### 行内代码样式\n- 浅蓝色背景\n- 小圆角（4px）\n- 青色文字\n\n### Markdown 内容样式\n- 清晰的标题层级\n- 优雅的链接悬停效果\n- 专业的表格样式\n- 美观的引用块\n- 响应式图片\n\n## 自定义配置\n\n### 更换代码主题\n如需更换代码高亮主题，修改 `ArticleView.vue` 中的导入：\n\n```typescript\n// 当前使用 atom-one-dark\nimport \'highlight.js/styles/atom-one-dark.css\'\n\n// 可选其他主题：\n// import \'highlight.js/styles/github.css\'           // GitHub 风格\n// import \'highlight.js/styles/monokai-sublime.css\'   // Monokai\n// import \'highlight.js/styles/vs2015.css\'            // Visual Studio\n```\n\n### 调整 Markdown 解析选项\n在 `useMarkdown.ts` 中修改 MarkdownIt 配置：\n\n```typescript\nconst md: MarkdownIt = new MarkdownIt({\n  html: true,        // 允许 HTML 标签\n  linkify: true,     // 自动转换 URL\n  typographer: true, // 智能排版\n  breaks: false,     // 单换行符是否转换为 <br>\n})\n```\n\n## 测试建议\n\n1. 在后端返回包含各种 Markdown 语法的测试内容\n2. 检查代码高亮是否正确应用\n3. 验证响应式布局在移动端的表现\n4. 测试深色模式下的显示效果\n\n## 注意事项\n\n1. **XSS 安全**: 虽然启用了 HTML 支持，但 markdown-it 会自动转义不安全的内容\n2. **性能**: 大型文档可能需要优化，考虑使用虚拟滚动或分页\n3. **图片**: 确保图片 URL 可访问，建议使用 CDN\n4. **代码语言**: highlight.js 支持 190+ 种语言，自动识别\n', 11, 1, NULL, b'1', 0, b'1', 2299, 62, 2, '2025-07-24 15:34:11', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-31 13:35:38', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10005, '演示文章 02：实践', 'demo-post-02', '简明讲解第 2 篇示例，涵盖要点与代码片段', '# 演示文章 02：实践\n\n这是一篇用于测试的文章内容，第 2 篇。包含若干占位文本与代码片段。', 11, 9, NULL, b'0', 0, b'1', 4858, 108, 2, '2025-10-15 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-29 22:33:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10006, '演示文章 03：实践', 'demo-post-03', '图文讲解第 3 篇示例，涵盖要点与代码片段', '# 演示文章 03：实践\n\n这是一篇用于测试的文章内容，第 3 篇。包含若干占位文本与代码片段。', 11, 4, NULL, b'0', 0, b'1', 4159, 154, 4, '2025-09-27 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10007, '演示文章 04：踩坑记', 'demo-post-04', '详细讲解第 4 篇示例，涵盖要点与代码片段', '# 演示文章 04：踩坑记\n\n这是一篇用于测试的文章内容，第 4 篇。包含若干占位文本与代码片段。', 13, 9, NULL, b'0', 0, b'1', 1825, 114, 3, '2025-09-03 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10008, '演示文章 05：入门', 'demo-post-05', '简明讲解第 5 篇示例，涵盖要点与代码片段', '# 演示文章 05：入门\n\n这是一篇用于测试的文章内容，第 5 篇。包含若干占位文本与代码片段。', 10, 3, NULL, b'0', 1, b'1', 3482, 87, 0, NULL, NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10009, '演示文章 06：实践', 'demo-post-06', '详细讲解第 6 篇示例，涵盖要点与代码片段', '# 演示文章 06：实践\n\n这是一篇用于测试的文章内容，第 6 篇。包含若干占位文本与代码片段。', 13, 6, NULL, b'0', 0, b'1', 779, 97, 3, '2025-10-13 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10010, '演示文章 07：最佳实践', 'demo-post-07', '图文讲解第 7 篇示例，涵盖要点与代码片段', '# 演示文章 07：最佳实践\n\n这是一篇用于测试的文章内容，第 7 篇。包含若干占位文本与代码片段。', 15, 10, NULL, b'0', 0, b'1', 375, 186, 3, '2025-09-23 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10011, '演示文章 08：踩坑记', 'demo-post-08', '详细讲解第 8 篇示例，涵盖要点与代码片段', '# 演示文章 08：踩坑记\n\n这是一篇用于测试的文章内容，第 8 篇。包含若干占位文本与代码片段。', 11, 7, NULL, b'0', 0, b'1', 4545, 75, 1, '2025-10-16 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-29 21:38:20', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10012, '演示文章 09：指南', 'demo-post-09', '详细讲解第 9 篇示例，涵盖要点与代码片段', '# 演示文章 09：指南\n\n这是一篇用于测试的文章内容，第 9 篇。包含若干占位文本与代码片段。', 19, 4, NULL, b'0', 0, b'1', 589, 11, 3, '2025-07-28 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10013, '演示文章 10：入门', 'demo-post-10', '详细讲解第 10 篇示例，涵盖要点与代码片段', '# 演示文章 10：入门\n\n这是一篇用于测试的文章内容，第 10 篇。包含若干占位文本与代码片段。', 11, 4, NULL, b'0', 1, b'1', 847, 97, 0, NULL, NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10014, '演示文章 11：入门', 'demo-post-11', '图文讲解第 11 篇示例，涵盖要点与代码片段', '# 演示文章 11：入门\n\n这是一篇用于测试的文章内容，第 11 篇。包含若干占位文本与代码片段。', 15, 3, NULL, b'0', 0, b'1', 2930, 53, 2, '2025-09-09 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10015, '演示文章 12：指南', 'demo-post-12', '简明讲解第 12 篇示例，涵盖要点与代码片段', '# 演示文章 12：指南\n\n这是一篇用于测试的文章内容，第 12 篇。包含若干占位文本与代码片段。', 11, 10, NULL, b'0', 0, b'1', 1421, 136, 3, '2025-08-06 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10016, '演示文章 13：入门', 'demo-post-13', '简明讲解第 13 篇示例，涵盖要点与代码片段', '# 演示文章 13：入门\n\n这是一篇用于测试的文章内容，第 13 篇。包含若干占位文本与代码片段。', 17, 7, NULL, b'0', 0, b'1', 4583, 56, 3, '2025-09-22 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-31 14:15:24', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10017, '演示文章 14：入门', 'demo-post-14', '简明讲解第 14 篇示例，涵盖要点与代码片段', '# 演示文章 14：入门\n\n这是一篇用于测试的文章内容，第 14 篇。包含若干占位文本与代码片段。', 13, 1, NULL, b'0', 0, b'1', 2604, 102, 3, '2025-07-15 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10018, '演示文章 15：最佳实践', 'demo-post-15', '详细讲解第 15 篇示例，涵盖要点与代码片段', '# 演示文章 15：最佳实践\n\n这是一篇用于测试的文章内容，第 15 篇。包含若干占位文本与代码片段。', 13, 10, NULL, b'0', 1, b'1', 2597, 54, 0, NULL, NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10019, '演示文章 16：踩坑记', 'demo-post-16', '图文讲解第 16 篇示例，涵盖要点与代码片段', '# 演示文章 16：踩坑记\n\n这是一篇用于测试的文章内容，第 16 篇。包含若干占位文本与代码片段。', 17, 3, NULL, b'0', 0, b'1', 1164, 63, 2, '2025-09-23 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-29 22:32:52', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10020, '演示文章 17：入门', 'demo-post-17', '简明讲解第 17 篇示例，涵盖要点与代码片段', '# 演示文章 17：入门\n\n这是一篇用于测试的文章内容，第 17 篇。包含若干占位文本与代码片段。', 14, 10, NULL, b'0', 0, b'1', 4800, 102, 4, '2025-09-02 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10021, '演示文章 18：实践', 'demo-post-18', '简明讲解第 18 篇示例，涵盖要点与代码片段', '# 演示文章 18：实践\n\n这是一篇用于测试的文章内容，第 18 篇。包含若干占位文本与代码片段。', 12, 9, NULL, b'1', 0, b'1', 767, 193, 2, '2025-08-24 15:34:11', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-31 03:21:42', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10022, '演示文章 19：实践', 'demo-post-19', '详细讲解第 19 篇示例，涵盖要点与代码片段', '# 演示文章 19：实践\n\n这是一篇用于测试的文章内容，第 19 篇。包含若干占位文本与代码片段。', 12, 3, NULL, b'0', 0, b'1', 3478, 152, 4, '2025-07-17 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10023, '演示文章 20：入门', 'demo-post-20', '图文讲解第 20 篇示例，涵盖要点与代码片段', '# 演示文章 20：入门\n\n这是一篇用于测试的文章内容，第 20 篇。包含若干占位文本与代码片段。', 16, 10, NULL, b'0', 1, b'1', 3854, 135, 0, NULL, NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10024, '演示文章 21：入门', 'demo-post-21', '图文讲解第 21 篇示例，涵盖要点与代码片段', '# 演示文章 21：入门\n\n这是一篇用于测试的文章内容，第 21 篇。包含若干占位文本与代码片段。', 10, 2, NULL, b'0', 0, b'1', 4418, 192, 4, '2025-07-31 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10025, '演示文章 22：最佳实践', 'demo-post-22', '简明讲解第 22 篇示例，涵盖要点与代码片段', '# 演示文章 22：最佳实践\n\n这是一篇用于测试的文章内容，第 22 篇。包含若干占位文本与代码片段。', 15, 2, NULL, b'0', 0, b'1', 3584, 40, 2, '2025-09-19 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-31 13:35:18', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10026, '演示文章 23：实践', 'demo-post-23', '图文讲解第 23 篇示例，涵盖要点与代码片段', '# 演示文章 23：实践\n\n这是一篇用于测试的文章内容，第 23 篇。包含若干占位文本与代码片段。', 14, 9, NULL, b'0', 0, b'1', 1483, 129, 2, '2025-07-21 15:34:11', NULL, 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10027, '演示文章 24：入门', 'demo-post-24', '简明讲解第 24 篇示例，涵盖要点与代码片段', '# 演示文章 24：入门\n\n这是一篇用于测试的文章内容，第 24 篇。包含若干占位文本与代码片段。', 14, 9, NULL, b'0', 0, b'1', 1649, 39, 1, '2025-08-10 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10028, '演示文章 25：入门', 'demo-post-25', '详细讲解第 25 篇示例，涵盖要点与代码片段', '# 演示文章 25：入门\n\n这是一篇用于测试的文章内容，第 25 篇。包含若干占位文本与代码片段。', 18, 9, NULL, b'0', 1, b'1', 24, 153, 0, NULL, NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10029, '演示文章 26：指南', 'demo-post-26', '简明讲解第 26 篇示例，涵盖要点与代码片段', '# 演示文章 26：指南\n\n这是一篇用于测试的文章内容，第 26 篇。包含若干占位文本与代码片段。', 10, 2, NULL, b'0', 0, b'1', 2993, 78, 4, '2025-06-30 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10030, '演示文章 27：最佳实践', 'demo-post-27', '简明讲解第 27 篇示例，涵盖要点与代码片段', '# 演示文章 27：最佳实践\n\n这是一篇用于测试的文章内容，第 27 篇。包含若干占位文本与代码片段。', 13, 10, NULL, b'0', 0, b'1', 721, 187, 5, '2025-10-16 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10031, '演示文章 28：指南', 'demo-post-28', '详细讲解第 28 篇示例，涵盖要点与代码片段', '# 演示文章 28：指南\n\n这是一篇用于测试的文章内容，第 28 篇。包含若干占位文本与代码片段。', 18, 3, NULL, b'0', 0, b'1', 3913, 140, 1, '2025-10-10 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10032, '演示文章 29：指南', 'demo-post-29', '图文讲解第 29 篇示例，涵盖要点与代码片段', '# 演示文章 29：指南\n\n这是一篇用于测试的文章内容，第 29 篇。包含若干占位文本与代码片段。', 18, 10, NULL, b'0', 0, b'1', 1755, 138, 2, '2025-09-02 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10033, '演示文章 30：踩坑记', 'demo-post-30', '详细讲解第 30 篇示例，涵盖要点与代码片段', '# 演示文章 30：踩坑记\n\n这是一篇用于测试的文章内容，第 30 篇。包含若干占位文本与代码片段。', 14, 7, NULL, b'0', 1, b'1', 3079, 112, 0, NULL, NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10034, '演示文章 31：实践', 'demo-post-31', '图文讲解第 31 篇示例，涵盖要点与代码片段', '# 演示文章 31：实践\n\n这是一篇用于测试的文章内容，第 31 篇。包含若干占位文本与代码片段。', 11, 4, NULL, b'0', 0, b'1', 544, 86, 2, '2025-09-28 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10035, '演示文章 32：实践', 'demo-post-32', '图文讲解第 32 篇示例，涵盖要点与代码片段', '# 演示文章 32：实践\n\n这是一篇用于测试的文章内容，第 32 篇。包含若干占位文本与代码片段。', 18, 4, NULL, b'0', 0, b'1', 1824, 1, 1, '2025-08-12 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10036, '演示文章 33：实践', 'demo-post-33', '图文讲解第 33 篇示例，涵盖要点与代码片段', '# 演示文章 33：实践\n\n这是一篇用于测试的文章内容，第 33 篇。包含若干占位文本与代码片段。', 10, 4, NULL, b'0', 0, b'1', 279, 84, 2, '2025-10-18 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-31 00:59:27', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10037, '演示文章 34：踩坑记', 'demo-post-34', '简明讲解第 34 篇示例，涵盖要点与代码片段', '# 演示文章 34：踩坑记\n\n这是一篇用于测试的文章内容，第 34 篇。包含若干占位文本与代码片段。', 13, 5, NULL, b'0', 0, b'1', 3996, 54, 4, '2025-08-02 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10038, '演示文章 35：最佳实践', 'demo-post-35', '详细讲解第 35 篇示例，涵盖要点与代码片段', '# 演示文章 35：最佳实践\n\n这是一篇用于测试的文章内容，第 35 篇。包含若干占位文本与代码片段。', 19, 10, NULL, b'1', 0, b'1', 3893, 62, 0, NULL, NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-31 14:24:58', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10039, '演示文章 36：最佳实践', 'demo-post-36', '详细讲解第 36 篇示例，涵盖要点与代码片段', '# 演示文章 36：最佳实践\n\n这是一篇用于测试的文章内容，第 36 篇。包含若干占位文本与代码片段。', 13, 2, NULL, b'0', 0, b'1', 3551, 90, 1, '2025-10-14 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10040, '演示文章 37：最佳实践', 'demo-post-37', '图文讲解第 37 篇示例，涵盖要点与代码片段', '# 演示文章 37：最佳实践\n\n这是一篇用于测试的文章内容，第 37 篇。包含若干占位文本与代码片段。', 17, 1, NULL, b'0', 0, b'1', 826, 15, 5, '2025-08-01 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10041, '演示文章 38：踩坑记', 'demo-post-38', '详细讲解第 38 篇示例，涵盖要点与代码片段', '# 演示文章 38：踩坑记\n\n这是一篇用于测试的文章内容，第 38 篇。包含若干占位文本与代码片段。', 15, 2, NULL, b'0', 0, b'1', 1589, 48, 1, '2025-09-25 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10042, '演示文章 39：指南', 'demo-post-39', '简明讲解第 39 篇示例，涵盖要点与代码片段', '# 演示文章 39：指南\n\n这是一篇用于测试的文章内容，第 39 篇。包含若干占位文本与代码片段。', 12, 7, NULL, b'0', 0, b'1', 2302, 118, 1, '2025-10-03 15:34:12', NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-31 02:30:46', b'0');
INSERT INTO `bg_article` (`id`, `title`, `slug`, `summary`, `content`, `category_id`, `author_id`, `thumbnail`, `is_top`, `status`, `is_comment`, `view_count`, `like_count`, `comment_count`, `published_time`, `pinned_time`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10043, '演示文章 40：踩坑记', 'demo-post-40', '简明讲解第 40 篇示例，涵盖要点与代码片段', '# 演示文章 40：踩坑记\n\n这是一篇用于测试的文章内容，第 40 篇。包含若干占位文本与代码片段。', 17, 9, NULL, b'0', 1, b'1', 822, 12, 0, NULL, NULL, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
COMMIT;

-- ----------------------------
-- Table structure for bg_article_tag
-- ----------------------------
DROP TABLE IF EXISTS `bg_article_tag`;
CREATE TABLE `bg_article_tag` (
  `article_id` bigint unsigned NOT NULL COMMENT '文章ID',
  `tag_id` bigint unsigned NOT NULL COMMENT '标签ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`article_id`,`tag_id`) USING BTREE,
  KEY `fk_at_tag` (`tag_id`) USING BTREE,
  CONSTRAINT `fk_at_article` FOREIGN KEY (`article_id`) REFERENCES `bg_article` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_at_tag` FOREIGN KEY (`tag_id`) REFERENCES `bg_tag` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='文章-标签关联';

-- ----------------------------
-- Records of bg_article_tag
-- ----------------------------
BEGIN;
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10001, 101, 'author1', '2025-10-24 23:15:53', 'author1', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10001, 103, 'author1', '2025-10-24 23:15:53', 'author1', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10002, 102, 'author1', '2025-10-24 23:15:53', 'author1', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10002, 104, 'author1', '2025-10-24 23:15:53', 'author1', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10003, 105, 'author1', '2025-10-24 23:15:53', 'author1', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10004, 103, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10004, 125, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10005, 108, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10005, 128, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10006, 106, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10006, 114, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10007, 107, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10007, 116, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10008, 113, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10008, 128, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10009, 102, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10009, 129, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10010, 101, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10010, 106, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10010, 113, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10011, 109, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10011, 113, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10012, 115, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10012, 126, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10013, 110, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10013, 114, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10014, 123, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10014, 124, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10015, 118, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10015, 126, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10016, 122, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10016, 123, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10017, 105, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10017, 107, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10017, 116, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10018, 107, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10018, 110, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10019, 102, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10019, 119, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10020, 118, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10020, 124, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10021, 102, 'seed', '2025-10-26 15:34:12', 'seed', '2025-10-26 15:34:12', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10021, 124, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10022, 102, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10022, 111, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10023, 102, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10023, 119, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10024, 116, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10024, 117, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10024, 128, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10025, 106, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10025, 117, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10026, 102, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10026, 117, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10027, 103, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10027, 128, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10028, 103, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10028, 106, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10029, 103, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10029, 120, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10030, 122, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10030, 128, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10031, 104, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10031, 108, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10031, 113, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10032, 119, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10032, 129, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10033, 108, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10033, 119, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10034, 102, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10034, 120, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10035, 103, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10035, 120, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10036, 114, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10036, 122, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10037, 117, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10037, 119, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10038, 107, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10038, 109, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10038, 111, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10039, 122, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10039, 123, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10040, 108, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10040, 111, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10041, 109, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10041, 113, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10042, 105, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10042, 122, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10043, 110, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_article_tag` (`article_id`, `tag_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10043, 121, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
COMMIT;

-- ----------------------------
-- Table structure for bg_category
-- ----------------------------
DROP TABLE IF EXISTS `bg_category`;
CREATE TABLE `bg_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名',
  `pid` bigint unsigned DEFAULT NULL COMMENT '父分类ID，NULL表示顶级',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0正常，1禁用',
  `count` int DEFAULT NULL COMMENT '分类文章数量',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_bg_category_name` (`name`) USING BTREE,
  KEY `idx_bg_category_pid` (`pid`) USING BTREE,
  CONSTRAINT `fk_bg_category_parent` FOREIGN KEY (`pid`) REFERENCES `bg_category` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `bg_category_chk_1` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='分类表';

-- ----------------------------
-- Records of bg_category
-- ----------------------------
BEGIN;
INSERT INTO `bg_category` (`id`, `name`, `pid`, `description`, `status`, `count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10, '后端', NULL, '后端相关技术', 0, 1, 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-29 10:55:52', b'0');
INSERT INTO `bg_category` (`id`, `name`, `pid`, `description`, `status`, `count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (11, '数据库', NULL, '数据库技术', 0, 2, 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-29 10:55:52', b'0');
INSERT INTO `bg_category` (`id`, `name`, `pid`, `description`, `status`, `count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (12, '生活', NULL, '随笔与记录', 0, 1, 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-29 10:55:52', b'0');
INSERT INTO `bg_category` (`id`, `name`, `pid`, `description`, `status`, `count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (13, 'Java', 10, 'Java 生态', 0, 2, 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-29 10:55:52', b'0');
INSERT INTO `bg_category` (`id`, `name`, `pid`, `description`, `status`, `count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (14, '架构', NULL, '系统架构与设计', 0, 1, 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-29 10:55:52', b'0');
INSERT INTO `bg_category` (`id`, `name`, `pid`, `description`, `status`, `count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (15, '中间件', NULL, '消息队列、缓存、网关', 0, 2, 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-29 10:55:52', b'0');
INSERT INTO `bg_category` (`id`, `name`, `pid`, `description`, `status`, `count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (16, '运维', NULL, 'CI/CD 与部署', 0, 1, 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-29 10:55:52', b'0');
INSERT INTO `bg_category` (`id`, `name`, `pid`, `description`, `status`, `count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (17, '前端', NULL, '前端工程与框架', 0, 3, 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-29 10:55:52', b'0');
INSERT INTO `bg_category` (`id`, `name`, `pid`, `description`, `status`, `count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (18, 'AI', NULL, 'AI/ML/LLM 相关', 0, 4, 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-29 10:55:52', b'0');
INSERT INTO `bg_category` (`id`, `name`, `pid`, `description`, `status`, `count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (19, '测试', NULL, '测试与质量保障', 1, 3, 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-29 10:55:52', b'0');
COMMIT;

-- ----------------------------
-- Table structure for bg_comment
-- ----------------------------
DROP TABLE IF EXISTS `bg_comment`;
CREATE TABLE `bg_comment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '评论类型：0文章评论，1友链评论',
  `article_id` bigint unsigned DEFAULT NULL COMMENT '文章ID（文章评论必填）',
  `root_id` bigint unsigned DEFAULT NULL COMMENT '根评论ID',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '父评论ID（楼中楼）',
  `from_user_id` bigint unsigned NOT NULL COMMENT '评论用户ID',
  `to_user_id` bigint unsigned DEFAULT NULL COMMENT '被回复用户ID',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0正常 1屏蔽',
  `like_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_comment_article` (`article_id`,`root_id`) USING BTREE,
  KEY `idx_comment_parent` (`parent_id`) USING BTREE,
  KEY `fk_comment_root` (`root_id`) USING BTREE,
  KEY `fk_comment_from_user` (`from_user_id`) USING BTREE,
  KEY `fk_comment_to_user` (`to_user_id`) USING BTREE,
  CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `bg_article` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_from_user` FOREIGN KEY (`from_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `bg_comment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_root` FOREIGN KEY (`root_id`) REFERENCES `bg_comment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_to_user` FOREIGN KEY (`to_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `bg_comment_chk_1` CHECK ((`type` in (0,1))),
  CONSTRAINT `bg_comment_chk_2` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=50096 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='评论表';

-- ----------------------------
-- Records of bg_comment
-- ----------------------------
BEGIN;
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50001, 0, 10001, NULL, NULL, 1, NULL, '写得很清晰，感谢分享！', 0, 3, 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50002, 0, 10001, 50001, 50001, 2, 1, '谢谢认可！后续补充源码地址。', 0, 1, 'author1', '2025-10-24 23:15:53', 'author1', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50003, 0, 10002, NULL, NULL, 1, NULL, '索引部分能否再展开一下覆盖索引？', 0, 0, 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50004, 0, 10040, NULL, NULL, 6, NULL, '很赞的文章！学习到了第 2 个技巧。', 0, 0, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50005, 0, 10040, NULL, NULL, 10, NULL, '很赞的文章！学习到了第 10 个技巧。', 0, 3, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50006, 0, 10009, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 4 个技巧。', 0, 16, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50007, 0, 10024, NULL, NULL, 3, NULL, '很赞的文章！学习到了第 6 个技巧。', 0, 2, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50008, 0, 10022, NULL, NULL, 6, NULL, '很赞的文章！学习到了第 5 个技巧。', 0, 5, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50009, 0, 10039, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 5 个技巧。', 0, 19, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50010, 0, 10004, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 5 个技巧。', 0, 3, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50011, 0, 10014, NULL, NULL, 5, NULL, '很赞的文章！学习到了第 2 个技巧。', 0, 3, 'seed', '2025-10-26 15:34:13', 'seed', '2025-10-26 15:34:13', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50012, 0, 10015, NULL, NULL, 5, NULL, '很赞的文章！学习到了第 5 个技巧。', 0, 19, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50013, 0, 10020, NULL, NULL, 6, NULL, '很赞的文章！学习到了第 4 个技巧。', 0, 20, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50014, 0, 10024, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 8 个技巧。', 0, 8, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50015, 0, 10007, NULL, NULL, 2, NULL, '很赞的文章！学习到了第 7 个技巧。', 0, 8, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50016, 0, 10006, NULL, NULL, 1, NULL, '很赞的文章！学习到了第 6 个技巧。', 0, 4, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50017, 0, 10024, NULL, NULL, 3, NULL, '很赞的文章！学习到了第 8 个技巧。', 0, 17, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50018, 0, 10037, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 1 个技巧。', 0, 3, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50019, 0, 10009, NULL, NULL, 3, NULL, '很赞的文章！学习到了第 9 个技巧。', 0, 1, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50020, 0, 10032, NULL, NULL, 10, NULL, '很赞的文章！学习到了第 9 个技巧。', 0, 4, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50021, 0, 10037, NULL, NULL, 3, NULL, '很赞的文章！学习到了第 1 个技巧。', 0, 9, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50022, 0, 10032, NULL, NULL, 1, NULL, '很赞的文章！学习到了第 6 个技巧。', 0, 6, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50023, 0, 10022, NULL, NULL, 2, NULL, '很赞的文章！学习到了第 6 个技巧。', 0, 17, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50024, 0, 10036, NULL, NULL, 10, NULL, '很赞的文章！学习到了第 3 个技巧。', 0, 7, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50025, 0, 10016, NULL, NULL, 3, NULL, '很赞的文章！学习到了第 7 个技巧。', 0, 0, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50026, 0, 10017, NULL, NULL, 6, NULL, '很赞的文章！学习到了第 7 个技巧。', 0, 7, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50027, 0, 10025, NULL, NULL, 3, NULL, '很赞的文章！学习到了第 2 个技巧。', 0, 12, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50028, 0, 10006, NULL, NULL, 8, NULL, '很赞的文章！学习到了第 4 个技巧。', 0, 6, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50029, 0, 10040, NULL, NULL, 6, NULL, '很赞的文章！学习到了第 5 个技巧。', 0, 7, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50030, 0, 10021, NULL, NULL, 1, NULL, '很赞的文章！学习到了第 4 个技巧。', 0, 12, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50031, 0, 10030, NULL, NULL, 5, NULL, '很赞的文章！学习到了第 2 个技巧。', 0, 8, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50032, 0, 10031, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 7 个技巧。', 0, 17, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50033, 0, 10030, NULL, NULL, 1, NULL, '很赞的文章！学习到了第 2 个技巧。', 0, 8, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50034, 0, 10017, NULL, NULL, 10, NULL, '很赞的文章！学习到了第 5 个技巧。', 0, 1, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50035, 0, 10011, NULL, NULL, 10, NULL, '很赞的文章！学习到了第 7 个技巧。', 0, 11, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50036, 0, 10029, NULL, NULL, 7, NULL, '很赞的文章！学习到了第 10 个技巧。', 0, 16, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50037, 0, 10012, NULL, NULL, 7, NULL, '很赞的文章！学习到了第 10 个技巧。', 0, 6, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50038, 0, 10024, NULL, NULL, 1, NULL, '很赞的文章！学习到了第 7 个技巧。', 0, 0, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50039, 0, 10019, NULL, NULL, 6, NULL, '很赞的文章！学习到了第 7 个技巧。', 0, 2, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50040, 0, 10030, NULL, NULL, 10, NULL, '很赞的文章！学习到了第 6 个技巧。', 0, 3, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50041, 0, 10027, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 5 个技巧。', 0, 13, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50042, 0, 10029, NULL, NULL, 7, NULL, '很赞的文章！学习到了第 5 个技巧。', 0, 17, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50043, 0, 10014, NULL, NULL, 4, NULL, '很赞的文章！学习到了第 7 个技巧。', 0, 12, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50044, 0, 10017, NULL, NULL, 10, NULL, '很赞的文章！学习到了第 10 个技巧。', 0, 9, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50045, 0, 10035, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 1 个技巧。', 0, 9, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50046, 0, 10026, NULL, NULL, 4, NULL, '很赞的文章！学习到了第 7 个技巧。', 0, 18, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50047, 0, 10029, NULL, NULL, 8, NULL, '很赞的文章！学习到了第 8 个技巧。', 0, 14, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50048, 0, 10020, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 8 个技巧。', 0, 5, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50049, 0, 10010, NULL, NULL, 5, NULL, '很赞的文章！学习到了第 9 个技巧。', 0, 20, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50050, 0, 10030, NULL, NULL, 2, NULL, '很赞的文章！学习到了第 4 个技巧。', 0, 9, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50051, 0, 10021, NULL, NULL, 4, NULL, '很赞的文章！学习到了第 3 个技巧。', 0, 0, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50052, 0, 10006, NULL, NULL, 4, NULL, '很赞的文章！学习到了第 8 个技巧。', 0, 19, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50053, 0, 10009, NULL, NULL, 8, NULL, '很赞的文章！学习到了第 7 个技巧。', 0, 20, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50054, 0, 10019, NULL, NULL, 7, NULL, '很赞的文章！学习到了第 8 个技巧。', 0, 12, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50055, 0, 10022, NULL, NULL, 3, NULL, '很赞的文章！学习到了第 1 个技巧。', 0, 3, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50056, 0, 10037, NULL, NULL, 4, NULL, '很赞的文章！学习到了第 3 个技巧。', 0, 16, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50057, 0, 10040, NULL, NULL, 1, NULL, '很赞的文章！学习到了第 9 个技巧。', 0, 7, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50058, 0, 10012, NULL, NULL, 8, NULL, '很赞的文章！学习到了第 3 个技巧。', 0, 14, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50059, 0, 10029, NULL, NULL, 8, NULL, '很赞的文章！学习到了第 10 个技巧。', 0, 16, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50060, 0, 10037, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 8 个技巧。', 0, 5, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50061, 0, 10041, NULL, NULL, 8, NULL, '很赞的文章！学习到了第 5 个技巧。', 0, 7, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50062, 0, 10025, NULL, NULL, 9, NULL, '很赞的文章！学习到了第 8 个技巧。', 0, 20, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50063, 0, 10022, NULL, NULL, 5, NULL, '很赞的文章！学习到了第 8 个技巧。', 0, 2, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50064, 0, 10026, 50049, 50049, 4, 5, '回复：我有相同的疑问，请作者再展开一下～', 0, 5, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50065, 0, 10010, 50024, 50024, 3, 3, '回复：我有相同的疑问，请作者再展开一下～', 0, 3, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50066, 0, 10015, 50028, 50028, 4, 2, '回复：我有相同的疑问，请作者再展开一下～', 0, 6, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50067, 0, 10030, 50030, 50030, 9, 8, '回复：我有相同的疑问，请作者再展开一下～', 0, 6, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50068, 0, 10020, 50011, 50011, 7, 7, '回复：我有相同的疑问，请作者再展开一下～', 0, 9, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50069, 0, 10034, 50006, 50006, 8, 1, '回复：我有相同的疑问，请作者再展开一下～', 0, 5, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50070, 0, 10034, 50042, 50042, 7, 9, '回复：我有相同的疑问，请作者再展开一下～', 0, 8, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50071, 0, 10042, 50032, 50032, 4, 5, '回复：我有相同的疑问，请作者再展开一下～', 0, 6, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50072, 0, 10005, 50066, 50066, 7, 6, '回复：我有相同的疑问，请作者再展开一下～', 0, 10, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50073, 0, 10016, 50055, 50055, 8, 3, '回复：我有相同的疑问，请作者再展开一下～', 0, 9, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50074, 0, 10005, 50072, 50072, 7, 10, '回复：我有相同的疑问，请作者再展开一下～', 0, 9, 'seed', '2025-10-26 15:34:14', 'seed', '2025-10-26 15:34:14', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50075, 0, 10010, 50007, 50007, 7, 3, '回复：我有相同的疑问，请作者再展开一下～', 0, 7, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50076, 0, 10007, 50027, 50027, 5, 7, '回复：我有相同的疑问，请作者再展开一下～', 0, 5, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50077, 0, 10040, 50031, 50031, 6, 6, '回复：我有相同的疑问，请作者再展开一下～', 0, 6, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50078, 0, 10036, 50039, 50039, 5, 2, '回复：我有相同的疑问，请作者再展开一下～', 0, 7, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50079, 0, 10007, 50006, 50006, 6, 4, '回复：我有相同的疑问，请作者再展开一下～', 0, 10, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50080, 0, 10006, 50012, 50012, 1, 4, '回复：我有相同的疑问，请作者再展开一下～', 0, 3, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50081, 0, 10015, 50006, 50006, 4, 3, '回复：我有相同的疑问，请作者再展开一下～', 0, 7, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50082, 0, 10020, 50018, 50018, 8, 5, '回复：我有相同的疑问，请作者再展开一下～', 0, 5, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50083, 0, 10012, 50025, 50025, 3, 5, '回复：我有相同的疑问，请作者再展开一下～', 0, 1, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-26 15:34:15', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50084, 0, 10001, NULL, NULL, 15, NULL, '123', 0, 0, '', '2025-10-31 13:03:26', '', '2025-10-31 13:03:26', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50085, 0, 10001, 50001, 50002, 15, 2, '123', 0, 0, '', '2025-10-31 13:03:32', '', '2025-10-31 13:03:32', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50086, 0, 10001, 50001, 50085, 15, 15, '我是大哥', 0, 0, '', '2025-10-31 13:04:09', '', '2025-10-31 13:04:09', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50087, 0, 10001, NULL, NULL, 15, NULL, '🐻😀1😘🥰😌😌😉', 0, 0, '', '2025-10-31 13:14:16', '', '2025-10-31 13:14:16', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50088, 0, 10001, NULL, NULL, 15, NULL, '# Test\n```pthon\nprint(1)\n```', 0, 0, '', '2025-10-31 13:14:36', '', '2025-10-31 13:14:36', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50089, 0, 10004, NULL, NULL, 15, NULL, '#### Java 示例\n```java\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, BirdBlog!\");\n        \n        // 创建列表\n        List<String> tags = Arrays.asList(\"Vue\", \"TypeScript\", \"Markdown\");\n        tags.forEach(System.out::println);\n    }\n}\n```', 0, 0, '', '2025-10-31 13:32:18', '', '2025-10-31 13:32:18', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50090, 0, 10001, NULL, NULL, 1, NULL, 'dajiahao', 0, 0, '', '2025-10-31 13:42:21', '', '2025-10-31 13:42:21', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50091, 0, 10001, 50001, 50002, 1, 2, '生生世世', 0, 0, '', '2025-10-31 13:50:53', '', '2025-10-31 13:50:53', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50092, 0, 10016, NULL, NULL, 15, NULL, '1111😍', 0, 0, '', '2025-10-31 14:15:24', '', '2025-10-31 14:15:24', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50093, 0, 10001, 50084, 50084, 15, 15, 'hello!Paddi,I am CYT.🐽', 0, 0, '', '2025-10-31 14:22:43', '', '2025-10-31 14:22:43', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50094, 0, 10001, 50084, 50093, 1, 15, 'hello,Paddi🥰', 0, 0, '', '2025-10-31 14:23:27', '', '2025-10-31 14:23:27', b'0');
INSERT INTO `bg_comment` (`id`, `type`, `article_id`, `root_id`, `parent_id`, `from_user_id`, `to_user_id`, `content`, `status`, `like_count`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (50095, 0, 10001, NULL, NULL, 15, NULL, 'test', 0, 0, '', '2025-10-31 16:47:39', '', '2025-10-31 16:47:39', b'0');
COMMIT;

-- ----------------------------
-- Table structure for bg_link
-- ----------------------------
DROP TABLE IF EXISTS `bg_link`;
CREATE TABLE `bg_link` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '友链ID',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站名称',
  `logo` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'LogoURL',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站地址',
  `status` tinyint NOT NULL DEFAULT '2' COMMENT '审核状态：0通过 1未通过 2待审核',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_bg_link_address` (`url`) USING BTREE,
  CONSTRAINT `bg_link_chk_1` CHECK ((`status` in (0,1,2)))
) ENGINE=InnoDB AUTO_INCREMENT=9034 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='友链';

-- ----------------------------
-- Records of bg_link
-- ----------------------------
BEGIN;
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9001, '官方文档 - Spring', 'https://www.google.com/s2/favicons?sz=64&domain=spring.io', 'Spring 官方入口', 'https://spring.io', 0, 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-31 01:14:34', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9002, 'MySQL 文档', 'https://www.google.com/s2/favicons?sz=64&domain=dev.mysql.com', 'MySQL 官方文档', 'https://dev.mysql.com', 0, 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-31 01:14:35', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9003, 'GitHub', 'https://www.google.com/s2/favicons?sz=64&domain=github.com', '开源托管平台', 'https://github.com', 0, 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-31 01:14:35', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9004, '开源中国', 'https://www.google.com/s2/favicons?sz=64&domain=www.oschina.net', '开源社区', 'https://www.oschina.net', 0, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-31 01:14:35', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9005, 'InfoQ', 'https://www.google.com/s2/favicons?sz=64&domain=www.infoq.cn', '技术媒体', 'https://www.infoq.cn', 0, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-31 01:14:35', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9006, '掘金', 'https://www.google.com/s2/favicons?sz=64&domain=juejin.cn', '开发者社区', 'https://juejin.cn', 0, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-31 01:14:35', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9007, 'Stack Overflow', 'https://www.google.com/s2/favicons?sz=64&domain=stackoverflow.com', '编程问答', 'https://stackoverflow.com', 0, 'seed', '2025-10-26 15:34:15', 'seed', '2025-10-31 01:14:35', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9008, 'Vue.js', 'https://www.google.com/s2/favicons?sz=64&domain=vuejs.org', 'Vue.js 官方文档', 'https://vuejs.org', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9009, 'React', 'https://www.google.com/s2/favicons?sz=64&domain=react.dev', 'React 官方文档', 'https://react.dev', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9010, 'TypeScript', 'https://www.google.com/s2/favicons?sz=64&domain=typescriptlang.org', 'TypeScript 官方文档', 'https://www.typescriptlang.org', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9011, 'Node.js', 'https://www.google.com/s2/favicons?sz=64&domain=nodejs.org', 'Node.js 官方网站', 'https://nodejs.org', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9012, 'MDN Web Docs', 'https://www.google.com/s2/favicons?sz=64&domain=developer.mozilla.org', 'Web 开发文档', 'https://developer.mozilla.org', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9013, 'Docker Hub', 'https://www.google.com/s2/favicons?sz=64&domain=hub.docker.com', 'Docker 镜像仓库', 'https://hub.docker.com', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9014, 'Redis', 'https://www.google.com/s2/favicons?sz=64&domain=redis.io', 'Redis 官方文档', 'https://redis.io', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9015, 'MongoDB', 'https://www.google.com/s2/favicons?sz=64&domain=mongodb.com', 'MongoDB 官方网站', 'https://www.mongodb.com', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9016, 'PostgreSQL', 'https://www.google.com/s2/favicons?sz=64&domain=postgresql.org', 'PostgreSQL 数据库', 'https://www.postgresql.org', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9017, 'Nginx', 'https://www.google.com/s2/favicons?sz=64&domain=nginx.org', 'Nginx 官方网站', 'https://nginx.org', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9018, 'Kubernetes', 'https://www.google.com/s2/favicons?sz=64&domain=kubernetes.io', 'Kubernetes 容器编排', 'https://kubernetes.io', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9019, 'Git', 'https://www.google.com/s2/favicons?sz=64&domain=git-scm.com', 'Git 版本控制', 'https://git-scm.com', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9020, 'VS Code', 'https://www.google.com/s2/favicons?sz=64&domain=code.visualstudio.com', 'Visual Studio Code', 'https://code.visualstudio.com', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9021, 'JetBrains', 'https://www.google.com/s2/favicons?sz=64&domain=jetbrains.com', 'JetBrains IDE工具', 'https://www.jetbrains.com', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9022, 'LeetCode', 'https://www.google.com/s2/favicons?sz=64&domain=leetcode.cn', '力扣算法题库', 'https://leetcode.cn', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9023, 'Gitee', 'https://www.google.com/s2/favicons?sz=64&domain=gitee.com', 'Gitee 代码托管', 'https://gitee.com', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9024, 'CSDN', 'https://www.google.com/s2/favicons?sz=64&domain=csdn.net', 'CSDN 技术社区', 'https://www.csdn.net', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9025, '博客园', 'https://www.google.com/s2/favicons?sz=64&domain=cnblogs.com', '博客园开发者社区', 'https://www.cnblogs.com', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9026, 'SegmentFault', 'https://www.google.com/s2/favicons?sz=64&domain=segmentfault.com', '思否技术社区', 'https://segmentfault.com', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9027, 'V2EX', 'https://www.google.com/s2/favicons?sz=64&domain=v2ex.com', 'V2EX 创意工作者社区', 'https://www.v2ex.com', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9028, 'Vite', 'https://www.google.com/s2/favicons?sz=64&domain=vitejs.dev', 'Vite 构建工具', 'https://vitejs.dev', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9029, 'Tailwind CSS', 'https://www.google.com/s2/favicons?sz=64&domain=tailwindcss.com', 'Tailwind CSS框架', 'https://tailwindcss.com', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9030, 'Element Plus', 'https://www.google.com/s2/favicons?sz=64&domain=element-plus.org', 'Element Plus组件库', 'https://element-plus.org', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9031, 'Ant Design', 'https://www.google.com/s2/favicons?sz=64&domain=ant.design', 'Ant Design组件库', 'https://ant.design', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9032, 'Webpack', 'https://www.google.com/s2/favicons?sz=64&domain=webpack.js.org', 'Webpack 打包工具', 'https://webpack.js.org', 0, 'admin', '2025-10-31 01:18:52', 'admin', '2025-10-31 01:18:52', b'0');
INSERT INTO `bg_link` (`id`, `name`, `logo`, `description`, `url`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9033, 'Paddi', 'https://youke1.picui.cn/s1/2025/10/31/690454d702e51.jpeg', '懒羊羊大王', 'https://baike.baidu.com/item/%E6%87%92%E7%BE%8A%E7%BE%8A/5534132', 0, 'admin', '2025-10-31 14:31:26', 'admin', '2025-10-31 14:31:26', b'0');
COMMIT;

-- ----------------------------
-- Table structure for bg_tag
-- ----------------------------
DROP TABLE IF EXISTS `bg_tag`;
CREATE TABLE `bg_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_bg_tag_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='标签表';

-- ----------------------------
-- Records of bg_tag
-- ----------------------------
BEGIN;
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (101, 'SpringBoot', 'SpringBoot 专题', 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (102, 'MySQL', 'MySQL 专题', 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (103, 'Java', 'Java 语言', 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (104, '经验', '经验分享', 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (105, '随笔', '日常记录', 'admin', '2025-10-24 23:15:53', 'admin', '2025-10-24 23:15:53', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (106, 'Redis', 'Redis 相关', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-26 15:34:10', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (107, 'Kafka', 'Kafka 相关', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-26 15:34:10', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (108, 'Docker', 'Docker 相关', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-26 15:34:10', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (109, 'Kubernetes', 'Kubernetes 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (110, 'Gateway', 'Gateway 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (111, 'Nginx', 'Nginx 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (112, 'ElasticSearch', 'ElasticSearch 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (113, 'RabbitMQ', 'RabbitMQ 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (114, 'PostgreSQL', 'PostgreSQL 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (115, 'ShardingSphere', 'ShardingSphere 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (116, 'MyBatisPlus', 'MyBatisPlus 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (117, 'JPA', 'JPA 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (118, 'AOP', 'AOP 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (119, 'DevOps', 'DevOps 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (120, 'CI-CD', 'CI-CD 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (121, 'Monitoring', 'Monitoring 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (122, 'Vue3', 'Vue3 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (123, 'React', 'React 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (124, 'TypeScript', 'TypeScript 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (125, 'TailwindCSS', 'TailwindCSS 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (126, 'NodeJS', 'NodeJS 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (127, 'LLM', 'LLM 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (128, 'Prompt', 'Prompt 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
INSERT INTO `bg_tag` (`id`, `name`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (129, 'RAG', 'RAG 相关', 'seed', '2025-10-26 15:34:11', 'seed', '2025-10-26 15:34:11', b'0');
COMMIT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '父菜单ID',
  `order_num` int NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件路径',
  `is_frame` tinyint NOT NULL DEFAULT '1' COMMENT '是否外链：0是 1否',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'M' COMMENT 'M目录 C菜单 F按钮',
  `visible` tinyint NOT NULL DEFAULT '0' COMMENT '显示：0显示 1隐藏',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0正常 1停用',
  `perms` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '#' COMMENT '图标',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sys_menu_parent` (`parent_id`) USING BTREE,
  CONSTRAINT `fk_sys_menu_parent` FOREIGN KEY (`parent_id`) REFERENCES `sys_menu` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=2006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (100, '首页', NULL, 0, '/dashboard', 'Layout', 1, 'M', 0, 0, NULL, 'homepage', '系统首页', 'system', '2025-10-31 17:02:43', 'system', '2025-10-31 17:02:43', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (101, '工作台', 100, 1, 'index', 'dashboard/index', 1, 'C', 0, 0, 'dashboard:view', 'dashboard', '首页工作台', 'system', '2025-10-31 17:02:43', 'system', '2025-10-31 17:02:43', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1000, '内容管理', NULL, 1, '/content', 'Layout', 1, 'M', 0, 0, NULL, 'folder', '内容管理入口', 'system', '2025-10-24 23:15:53', 'system', '2025-10-31 17:07:37', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1001, '文章管理', 1000, 1, 'article', 'content/article/index', 1, 'C', 0, 0, 'content:article:list', 'file-text', '', 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1002, '分类管理', 1000, 2, 'category', 'content/category/index', 1, 'C', 0, 0, 'content:category:list', 'grid', '', 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1003, '标签管理', 1000, 3, 'tag', 'content/tag/index', 1, 'C', 0, 0, 'content:tag:list', 'tag', '', 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1004, '评论管理', 1000, 4, 'comment', 'content/comment/index', 1, 'C', 0, 0, 'content:comment:list', 'message-square', '', 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1005, '友链管理', 1000, 5, 'link', 'content/link/index', 1, 'C', 0, 0, 'content:link:list', 'link', '', 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2000, '系统管理', NULL, 10, '/system', 'Layout', 1, 'M', 0, 0, NULL, 'setting', '系统管理模块', 'system', '2025-10-31 17:12:05', 'system', '2025-10-31 17:12:05', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2001, '菜单管理', 2000, 1, 'menu', 'system/menu/index', 1, 'C', 0, 0, 'system:menu:list', 'tree-table', '菜单权限管理', 'system', '2025-10-31 17:12:05', 'system', '2025-10-31 17:12:05', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2002, '菜单查询', 2001, 1, '', NULL, 1, 'F', 0, 0, 'system:menu:query', '#', '菜单查询权限', 'system', '2025-10-31 17:12:05', 'system', '2025-10-31 17:12:05', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2003, '菜单新增', 2001, 2, '', NULL, 1, 'F', 0, 0, 'system:menu:add', '#', '菜单新增权限', 'system', '2025-10-31 17:12:05', 'system', '2025-10-31 17:12:05', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2004, '菜单修改', 2001, 3, '', NULL, 1, 'F', 0, 0, 'system:menu:edit', '#', '菜单修改权限', 'system', '2025-10-31 17:12:05', 'system', '2025-10-31 17:12:05', b'0');
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2005, '菜单删除', 2001, 4, '', NULL, 1, 'F', 0, 0, 'system:menu:delete', '#', '菜单删除权限', 'system', '2025-10-31 17:12:05', 'system', '2025-10-31 17:12:05', b'0');
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限标识',
  `role_sort` int NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0正常，1停用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_sys_role_key` (`role_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `role_sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1, '管理员', 'admin', 1, 0, '平台管理员', 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `role_sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2, '作者', 'author', 2, 0, '内容作者', 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint unsigned NOT NULL COMMENT '角色ID',
  `menu_id` bigint unsigned NOT NULL COMMENT '菜单ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`role_id`,`menu_id`) USING BTREE,
  KEY `fk_rm_menu` (`menu_id`) USING BTREE,
  CONSTRAINT `fk_rm_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_rm_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='角色-菜单关联';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1, 1000, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1, 1001, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1, 1002, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1, 1003, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1, 1004, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1, 1005, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2, 1000, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2, 1001, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2, 1002, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2, 1003, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '昵称',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码Hash',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '用户类型：0普通用户，1管理员',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '账号状态：0正常，1停用',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `sex` tinyint DEFAULT '2' COMMENT '性别：0男，1女，2未知',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_sys_user_username` (`username`) USING BTREE,
  UNIQUE KEY `uk_sys_user_email` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1, 'admin', '超级管理员', '$2a$10$ITkLEsFPAQT6owS0H24HDuhBZH.VNIpNPXr2emhSpViDi.hhj3BJK', 1, 0, 'admin@birdblog.local', '18800000000', 0, 'https://youke1.picui.cn/s1/2025/10/31/690455356db0f.jpg', 'system', '2025-10-24 23:15:53', 'system', '2025-10-31 14:21:40', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2, 'author1', '小鹤', '$2a$10$XCPiZ/UEKgFhKG7hSNP0u.PegL7dxm0u.Hd3YdXm51plgqTlD0m.a', 0, 0, 'author1@birdblog.local', '18800000001', 0, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'system', '2025-10-24 23:15:53', 'system', '2025-10-31 13:05:04', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (3, 'user3', '用户3号', '$2a$10$XCPiZ/UEKgFhKG7hSNP0u.PegL7dxm0u.Hd3YdXm51plgqTlD0m.a', 0, 0, 'user3@birdblog.local', '18800000003', 0, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-31 13:05:04', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (4, 'user4', '用户4号', '$2a$10$XCPiZ/UEKgFhKG7hSNP0u.PegL7dxm0u.Hd3YdXm51plgqTlD0m.a', 0, 0, 'user4@birdblog.local', '18800000004', 1, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-31 13:05:04', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (5, 'user5', '用户5号', '$2a$10$XCPiZ/UEKgFhKG7hSNP0u.PegL7dxm0u.Hd3YdXm51plgqTlD0m.a', 0, 0, 'user5@birdblog.local', '18800000005', 2, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-31 13:05:04', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (6, 'user6', '用户6号', '$2a$10$XCPiZ/UEKgFhKG7hSNP0u.PegL7dxm0u.Hd3YdXm51plgqTlD0m.a', 0, 0, 'user6@birdblog.local', '18800000006', 0, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-31 13:05:04', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (7, 'user7', '用户7号', '$2a$10$XCPiZ/UEKgFhKG7hSNP0u.PegL7dxm0u.Hd3YdXm51plgqTlD0m.a', 0, 0, 'user7@birdblog.local', '18800000007', 1, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-31 13:05:04', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (8, 'user8', '用户8号', '$2a$10$XCPiZ/UEKgFhKG7hSNP0u.PegL7dxm0u.Hd3YdXm51plgqTlD0m.a', 0, 0, 'user8@birdblog.local', '18800000008', 2, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-31 13:05:04', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (9, 'user9', '用户9号', '$2a$10$XCPiZ/UEKgFhKG7hSNP0u.PegL7dxm0u.Hd3YdXm51plgqTlD0m.a', 0, 0, 'user9@birdblog.local', '18800000009', 0, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-31 13:05:04', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (10, 'user10', '用户10号', '$2a$10$XCPiZ/UEKgFhKG7hSNP0u.PegL7dxm0u.Hd3YdXm51plgqTlD0m.a', 0, 0, 'user10@birdblog.local', '18800000010', 1, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'seed', '2025-10-26 15:34:10', 'seed', '2025-10-31 13:05:04', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (11, 'sg', 'sg333', '$2a$10$RMuFXGQ5AtH4wOvkUqyvuecpqUSeoxZYqilXzbz50dceRsga.WYiq', 0, 0, '23412332@qq.com', '13800138000', 1, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'system', '2025-10-30 15:29:10', 'system', '2025-10-31 13:05:04', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (15, 'bg', 'bg333', '$2a$10$dMO8w8Pi/gQ1kpC45QW60e8mLshnEv.IquHPFZ6.goVVn8tvs0brK', 0, 0, '2341233211@qq.com', '13800138000', 1, 'https://youke1.picui.cn/s1/2025/10/31/690454d702e51.jpeg', 'system', '2025-10-30 15:29:46', 'system', '2025-10-31 14:20:14', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (16, 'admin1111', '管理员', '$2a$10$ITkLEsFPAQT6owS0H24HDuhBZH.VNIpNPXr2emhSpViDi.hhj3BJK', 1, 0, 'admi1122@birdblog.com', '13900139000', 0, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'system', '2025-10-30 15:29:46', 'system', '2025-10-30 15:46:21', b'0');
INSERT INTO `sys_user` (`id`, `username`, `nick_name`, `password`, `type`, `status`, `email`, `phone`, `sex`, `avatar`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (18, 'admin1113', '管理员', '$2a$10$ITkLEsFPAQT6owS0H24HDuhBZH.VNIpNPXr2emhSpViDi.hhj3BJK', 1, 0, 'admin112122@birdblog.com', '13900139000', 0, 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif', 'system', '2025-10-30 15:47:10', 'system', '2025-10-30 15:47:10', b'0');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `role_id` bigint unsigned NOT NULL COMMENT '角色ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE,
  KEY `fk_ur_role` (`role_id`) USING BTREE,
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户-角色关联';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (1, 1, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`) VALUES (2, 2, 'system', '2025-10-24 23:15:53', 'system', '2025-10-24 23:15:53', b'0');
COMMIT;

-- ----------------------------
-- View structure for vw_bg_article_public
-- ----------------------------
DROP VIEW IF EXISTS `vw_bg_article_public`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `vw_bg_article_public` AS select `a`.`id` AS `id`,`a`.`title` AS `title`,`a`.`slug` AS `slug`,`a`.`summary` AS `summary`,`a`.`category_id` AS `category_id`,`c`.`name` AS `category_name`,`a`.`author_id` AS `author_id`,`a`.`thumbnail` AS `thumbnail`,`a`.`view_count` AS `view_count`,`a`.`like_count` AS `like_count`,`a`.`comment_count` AS `comment_count`,`a`.`published_time` AS `published_time` from (`bg_article` `a` join `bg_category` `c` on((`a`.`category_id` = `c`.`id`))) where ((`a`.`status` = 0) and (`a`.`deleted` = 0x00));

-- ----------------------------
-- Procedure structure for sp_publish_article
-- ----------------------------
DROP PROCEDURE IF EXISTS `sp_publish_article`;
delimiter ;;
CREATE PROCEDURE `sp_publish_article`(IN p_article_id BIGINT UNSIGNED, IN p_publish_time DATETIME)
BEGIN
    UPDATE bg_article
    SET status = 0,
        published_time = IFNULL(p_publish_time, NOW()),
        update_time = NOW()
    WHERE id = p_article_id AND deleted = b'0';
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table bg_comment
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_comment_after_insert`;
delimiter ;;
CREATE TRIGGER `birdblog`.`trg_comment_after_insert` AFTER INSERT ON `bg_comment` FOR EACH ROW BEGIN
    IF NEW.type = 0 AND NEW.deleted = b'0' THEN
        UPDATE bg_article SET comment_count = comment_count + 1 WHERE id = NEW.article_id;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table bg_comment
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_comment_after_update`;
delimiter ;;
CREATE TRIGGER `birdblog`.`trg_comment_after_update` AFTER UPDATE ON `bg_comment` FOR EACH ROW BEGIN
    IF NEW.type = 0 THEN
        IF OLD.deleted = b'0' AND NEW.deleted = b'1' THEN
            UPDATE bg_article SET comment_count = comment_count - 1 WHERE id = NEW.article_id;
        ELSEIF OLD.deleted = b'1' AND NEW.deleted = b'0' THEN
            UPDATE bg_article SET comment_count = comment_count + 1 WHERE id = NEW.article_id;
        END IF;
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
