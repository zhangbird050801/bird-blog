-- BirdBlog demo data (DDL-independent)
-- Generated: 2025-10-24 08:30:21 UTC
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
USE birdblog;
START TRANSACTION;

-- sys_user
INSERT INTO sys_user
(id, username, nick_name, password_hash, type, status, email, phone, sex, avatar, creator, create_time, updater, update_time, deleted)
VALUES
(1, 'admin', '超级管理员', '$2a$10$adminhashxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 1, 0, 'admin@birdblog.local', '18800000000', 0, 'https://example.com/avatars/admin.png', 'system', NOW(), 'system', NOW(), b'0'),
(2, 'author1', '小鹤', '$2a$10$authorhashxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 0, 0, 'author1@birdblog.local', '18800000001', 0, 'https://example.com/avatars/author1.png', 'system', NOW(), 'system', NOW(), b'0');

-- sys_role
INSERT INTO sys_role
(id, role_name, role_key, role_sort, status, remark, creator, create_time, updater, update_time, deleted)
VALUES
(1, '管理员', 'admin', 1, 0, '平台管理员', 'system', NOW(), 'system', NOW(), b'0'),
(2, '作者', 'author', 2, 0, '内容作者', 'system', NOW(), 'system', NOW(), b'0');

-- sys_menu
INSERT INTO sys_menu
(id, menu_name, parent_id, order_num, path, component, is_frame, menu_type, visible, status, perms, icon, remark, creator, create_time, updater, update_time, deleted)
VALUES
(1000, '内容管理', NULL, 1, 'content', NULL, 1, 'M', 0, 0, NULL, 'folder', '内容管理入口', 'system', NOW(), 'system', NOW(), b'0'),
(1001, '文章管理', 1000, 1, 'article', 'content/article/index', 1, 'C', 0, 0, 'content:article:list', 'file-text', '', 'system', NOW(), 'system', NOW(), b'0'),
(1002, '分类管理', 1000, 2, 'category', 'content/category/index', 1, 'C', 0, 0, 'content:category:list', 'grid', '', 'system', NOW(), 'system', NOW(), b'0'),
(1003, '标签管理', 1000, 3, 'tag', 'content/tag/index', 1, 'C', 0, 0, 'content:tag:list', 'tag', '', 'system', NOW(), 'system', NOW(), b'0'),
(1004, '评论管理', 1000, 4, 'comment', 'content/comment/index', 1, 'C', 0, 0, 'content:comment:list', 'message-square', '', 'system', NOW(), 'system', NOW(), b'0'),
(1005, '友链管理', 1000, 5, 'link', 'content/link/index', 1, 'C', 0, 0, 'content:link:list', 'link', '', 'system', NOW(), 'system', NOW(), b'0');

-- sys_user_role
INSERT INTO sys_user_role (user_id, role_id, creator, create_time, updater, update_time, deleted)
VALUES
(1, 1, 'system', NOW(), 'system', NOW(), b'0'),
(2, 2, 'system', NOW(), 'system', NOW(), b'0');

-- sys_role_menu
INSERT INTO sys_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted)
VALUES
(1, 1000, 'system', NOW(), 'system', NOW(), b'0'),
(1, 1001, 'system', NOW(), 'system', NOW(), b'0'),
(1, 1002, 'system', NOW(), 'system', NOW(), b'0'),
(1, 1003, 'system', NOW(), 'system', NOW(), b'0'),
(1, 1004, 'system', NOW(), 'system', NOW(), b'0'),
(1, 1005, 'system', NOW(), 'system', NOW(), b'0'),
(2, 1000, 'system', NOW(), 'system', NOW(), b'0'),
(2, 1001, 'system', NOW(), 'system', NOW(), b'0'),
(2, 1002, 'system', NOW(), 'system', NOW(), b'0'),
(2, 1003, 'system', NOW(), 'system', NOW(), b'0');

-- bg_category
INSERT INTO bg_category
(id, name, pid, description, status, creator, create_time, updater, update_time, deleted)
VALUES
(10, '后端', NULL, '后端相关技术', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(11, '数据库', NULL, '数据库技术', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(12, '生活', NULL, '随笔与记录', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(13, 'Java', 10, 'Java 生态', 0, 'admin', NOW(), 'admin', NOW(), b'0');

-- bg_tag
INSERT INTO bg_tag
(id, name, remark, creator, create_time, updater, update_time, deleted)
VALUES
(101, 'SpringBoot', 'SpringBoot 专题', 'admin', NOW(), 'admin', NOW(), b'0'),
(102, 'MySQL', 'MySQL 专题', 'admin', NOW(), 'admin', NOW(), b'0'),
(103, 'Java', 'Java 语言', 'admin', NOW(), 'admin', NOW(), b'0'),
(104, '经验', '经验分享', 'admin', NOW(), 'admin', NOW(), b'0'),
(105, '随笔', '日常记录', 'admin', NOW(), 'admin', NOW(), b'0');

-- bg_article
INSERT INTO bg_article
(id, title, slug, summary, content, category_id, author_id, thumbnail, is_top, status, is_comment, view_count, like_count, comment_count, published_time, pinned_time, creator, create_time, updater, update_time, deleted)
VALUES
(10001, 'SpringBoot 快速上手', 'springboot-quickstart', '五分钟创建第一个 SpringBoot 项目', '# SpringBoot 入门\n\n本文演示如何通过 Spring Initializr 创建项目……', 13, 2, NULL, b'1', 0, b'1', 256, 8, 0, NOW(), NOW(), 'author1', NOW(), 'author1', NOW(), b'0'),
(10002, 'MySQL 性能优化清单', 'mysql-tuning-checklist', '从索引到执行计划的实战清单', '# MySQL 优化\n\n覆盖索引、联合索引、慢查询……', 11, 2, NULL, b'0', 0, b'1', 1024, 22, 0, NOW(), NULL, 'author1', NOW(), 'author1', NOW(), b'0'),
(10003, '写给自己的效率笔记', 'personal-efficiency-notes', '一些提升效率的小经验', '# 效率笔记\n\n键位设置、脚本自动化、番茄工作法……', 12, 2, NULL, b'0', 1, b'1', 10, 1, 0, NULL, NULL, 'author1', NOW(), 'author1', NOW(), b'0');

-- bg_article_tag
INSERT INTO bg_article_tag
(article_id, tag_id, creator, create_time, updater, update_time, deleted)
VALUES
(10001, 101, 'author1', NOW(), 'author1', NOW(), b'0'),
(10001, 103, 'author1', NOW(), 'author1', NOW(), b'0'),
(10002, 102, 'author1', NOW(), 'author1', NOW(), b'0'),
(10002, 104, 'author1', NOW(), 'author1', NOW(), b'0'),
(10003, 105, 'author1', NOW(), 'author1', NOW(), b'0');

-- bg_comment
INSERT INTO bg_comment
(id, type, article_id, root_id, parent_id, from_user_id, to_user_id, content, status, like_count, creator, create_time, updater, update_time, deleted)
VALUES
(50001, 0, 10001, NULL, NULL, 1, NULL, '写得很清晰，感谢分享！', 0, 3, 'admin', NOW(), 'admin', NOW(), b'0'),
(50002, 0, 10001, 50001, 50001, 2, 1, '谢谢认可！后续补充源码地址。', 0, 1, 'author1', NOW(), 'author1', NOW(), b'0'),
(50003, 0, 10002, NULL, NULL, 1, NULL, '索引部分能否再展开一下覆盖索引？', 0, 0, 'admin', NOW(), 'admin', NOW(), b'0');

-- bg_link
INSERT INTO bg_link
(id, name, logo, description, address, status, creator, create_time, updater, update_time, deleted)
VALUES
(9001, '官方文档 - Spring', 'https://example.com/logo/spring.png', 'Spring 官方入口', 'https://spring.io', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9002, 'MySQL 文档', 'https://example.com/logo/mysql.png', 'MySQL 官方文档', 'https://dev.mysql.com', 0, 'admin', NOW(), 'admin', NOW(), b'0'),
(9003, 'GitHub', 'https://example.com/logo/github.png', '开源托管平台', 'https://github.com', 0, 'admin', NOW(), 'admin', NOW(), b'0');
COMMIT;
SET FOREIGN_KEY_CHECKS = 1;