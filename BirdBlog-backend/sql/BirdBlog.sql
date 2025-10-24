-- =====================================================================
-- 0) 基础设置
-- =====================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS birdblog;
CREATE DATABASE birdblog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE birdblog;

-- =====================================================================
-- 1) 系统表（sys_）：用户/角色/菜单/关联
--    说明：来自样表 sys_user / sys_role / sys_menu 等并按统一规范增强
-- =====================================================================

-- 1.1 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(64) NOT NULL COMMENT '用户名',
  `nick_name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '昵称',
  `password_hash` VARCHAR(100) NOT NULL COMMENT '密码Hash',
  `type` TINYINT NOT NULL DEFAULT 0 COMMENT '用户类型：0普通用户，1管理员',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '账号状态：0正常，1停用',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(32)  DEFAULT NULL COMMENT '手机号',
  `sex` TINYINT DEFAULT 2 COMMENT '性别：0男，1女，2未知',
  `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  UNIQUE KEY `uk_sys_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 1.2 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
  `role_key` VARCHAR(100) NOT NULL COMMENT '角色权限标识',
  `role_sort` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0正常，1停用',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 1.3 菜单表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` VARCHAR(100) NOT NULL COMMENT '菜单名称',
  `parent_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '父菜单ID',
  `order_num` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `path` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '路由地址',
  `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  `is_frame` TINYINT NOT NULL DEFAULT 1 COMMENT '是否外链：0是 1否',
  `menu_type` CHAR(1) NOT NULL DEFAULT 'M' COMMENT 'M目录 C菜单 F按钮',
  `visible` TINYINT NOT NULL DEFAULT 0 COMMENT '显示：0显示 1隐藏',
  `status`  TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0正常 1停用',
  `perms` VARCHAR(128) DEFAULT NULL COMMENT '权限标识',
  `icon`  VARCHAR(100) DEFAULT '#' COMMENT '图标',
  `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_menu_parent` (`parent_id`),
  CONSTRAINT `fk_sys_menu_parent` FOREIGN KEY (`parent_id`) REFERENCES `sys_menu` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 1.4 用户-角色
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`user_id`, `role_id`),
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-角色关联';

-- 1.5 角色-菜单
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT UNSIGNED NOT NULL COMMENT '菜单ID',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`role_id`, `menu_id`),
  CONSTRAINT `fk_rm_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rm_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色-菜单关联';

-- =====================================================================
-- 2) 业务表（bg_）：分类/标签/文章/文章-标签/评论/友链
--    说明：来源于样表 sg_* 并统一命名、补齐公共字段、增强约束
-- =====================================================================

-- 2.1 分类表
DROP TABLE IF EXISTS `bg_category`;
CREATE TABLE `bg_category` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(128) NOT NULL COMMENT '分类名',
  `pid`  BIGINT UNSIGNED DEFAULT NULL COMMENT '父分类ID，NULL表示顶级',
  `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0正常，1禁用',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bg_category_name` (`name`),
  KEY `idx_bg_category_pid` (`pid`),
  CONSTRAINT `fk_bg_category_parent` FOREIGN KEY (`pid`) REFERENCES `bg_category`(`id`) ON DELETE SET NULL,
  CHECK (`status` IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 2.2 标签表
DROP TABLE IF EXISTS `bg_tag`;
CREATE TABLE `bg_tag` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` VARCHAR(128) NOT NULL COMMENT '标签名',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bg_tag_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 2.3 文章表（重点）
DROP TABLE IF EXISTS `bg_article`;
CREATE TABLE `bg_article` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` VARCHAR(256) NOT NULL COMMENT '标题',
  `slug`  VARCHAR(256) NOT NULL COMMENT 'URL别名（便于SEO）',
  `summary` VARCHAR(1024) DEFAULT NULL COMMENT '摘要',
  `content` LONGTEXT NOT NULL COMMENT '正文内容',
  `category_id` BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
  `author_id`   BIGINT UNSIGNED NOT NULL COMMENT '作者(用户ID)',
  `thumbnail` VARCHAR(512) DEFAULT NULL COMMENT '缩略图URL',
  `is_top` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否置顶',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0已发布，1草稿',
  `is_comment` bit(1) NOT NULL DEFAULT b'1' COMMENT '允许评论',
  `view_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞量',
  `comment_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '评论数',
  `published_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `pinned_time` DATETIME DEFAULT NULL COMMENT '置顶时间',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bg_article_slug`(`slug`),
  KEY `idx_bg_article_cat` (`category_id`),
  KEY `idx_bg_article_pub` (`status`, `deleted`, `published_time`),
  FULLTEXT KEY `ft_article_text` (`title`, `summary`, `content`) WITH PARSER ngram,
  CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `bg_category` (`id`),
  CONSTRAINT `fk_article_author` FOREIGN KEY (`author_id`) REFERENCES `sys_user` (`id`),
  CHECK (`status` IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 2.4 文章-标签 关联（多对多）
DROP TABLE IF EXISTS `bg_article_tag`;
CREATE TABLE `bg_article_tag` (
  `article_id` BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
  `tag_id`     BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`article_id`, `tag_id`),
  CONSTRAINT `fk_at_article` FOREIGN KEY (`article_id`) REFERENCES `bg_article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_at_tag`     FOREIGN KEY (`tag_id`)     REFERENCES `bg_tag`     (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章-标签关联';

-- 2.5 评论表
DROP TABLE IF EXISTS `bg_comment`;
CREATE TABLE `bg_comment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `type` TINYINT NOT NULL DEFAULT 0 COMMENT '评论类型：0文章评论，1友链评论',
  `article_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '文章ID（文章评论必填）',
  `root_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '根评论ID',
  `parent_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '父评论ID（楼中楼）',
  `from_user_id` BIGINT UNSIGNED NOT NULL COMMENT '评论用户ID',
  `to_user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '被回复用户ID',
  `content` VARCHAR(512) NOT NULL COMMENT '评论内容',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0正常 1屏蔽',
  `like_count` BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞数',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_comment_article` (`article_id`, `root_id`),
  KEY `idx_comment_parent` (`parent_id`),
  CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `bg_article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_root`    FOREIGN KEY (`root_id`)    REFERENCES `bg_comment` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_parent`  FOREIGN KEY (`parent_id`)  REFERENCES `bg_comment` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_from_user` FOREIGN KEY (`from_user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_comment_to_user`   FOREIGN KEY (`to_user_id`)   REFERENCES `sys_user` (`id`),
  CHECK (`type` IN (0,1)),
  CHECK (`status` IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 2.6 友链表
DROP TABLE IF EXISTS `bg_link`;
CREATE TABLE `bg_link` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '友链ID',
  `name` VARCHAR(256) NOT NULL COMMENT '网站名称',
  `logo` VARCHAR(512) DEFAULT NULL COMMENT 'LogoURL',
  `description` VARCHAR(512) DEFAULT NULL COMMENT '描述',
  `address` VARCHAR(512) NOT NULL COMMENT '网站地址',
  `status` TINYINT NOT NULL DEFAULT 2 COMMENT '审核状态：0通过 1未通过 2待审核',
  -- 公共审计字段
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bg_link_address` (`address`),
  CHECK (`status` IN (0,1,2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='友链';

-- =====================================================================
-- 3) 触发器：自动维护文章评论计数
-- =====================================================================
DROP TRIGGER IF EXISTS trg_comment_after_insert;
DELIMITER $$
CREATE TRIGGER trg_comment_after_insert
AFTER INSERT ON bg_comment
FOR EACH ROW
BEGIN
  IF NEW.type = 0 AND NEW.deleted = b'0' THEN
    UPDATE bg_article SET comment_count = comment_count + 1 WHERE id = NEW.article_id;
  END IF;
END$$
DELIMITER ;

DROP TRIGGER IF EXISTS trg_comment_after_update;
DELIMITER $$
CREATE TRIGGER trg_comment_after_update
AFTER UPDATE ON bg_comment
FOR EACH ROW
BEGIN
  IF NEW.type = 0 THEN
    IF OLD.deleted = b'0' AND NEW.deleted = b'1' THEN
      UPDATE bg_article SET comment_count = comment_count - 1 WHERE id = NEW.article_id;
    ELSEIF OLD.deleted = b'1' AND NEW.deleted = b'0' THEN
      UPDATE bg_article SET comment_count = comment_count + 1 WHERE id = NEW.article_id;
    END IF;
  END IF;
END$$
DELIMITER ;

-- =====================================================================
-- 4) 视图：仅发布、未删除的文章（对读者授予SELECT）
-- =====================================================================
DROP VIEW IF EXISTS vw_bg_article_public;
CREATE VIEW vw_bg_article_public AS
SELECT a.id, a.title, a.slug, a.summary, a.category_id, c.name AS category_name,
       a.author_id, a.thumbnail, a.view_count, a.like_count, a.comment_count, a.published_time
FROM bg_article a
JOIN bg_category c ON a.category_id = c.id
WHERE a.status = 0 AND a.deleted = b'0';

-- =====================================================================
-- 5) 存储过程：发布文章（设置状态与发布时间）
-- =====================================================================
DROP PROCEDURE IF EXISTS sp_publish_article;
DELIMITER $$
CREATE PROCEDURE sp_publish_article(IN p_article_id BIGINT UNSIGNED, IN p_publish_time DATETIME)
BEGIN
  UPDATE bg_article
  SET status = 0,
      published_time = IFNULL(p_publish_time, NOW()),
      update_time = NOW()
  WHERE id = p_article_id AND deleted = b'0';
END$$
DELIMITER ;

-- =====================================================================
-- 6) 权限示例（可按需调整）
-- =====================================================================
-- 角色（MySQL 内置角色，仅示例）
CREATE ROLE IF NOT EXISTS 'blog_reader', 'blog_author';

-- 仅能读公开文章/分类/标签/友链视图
GRANT SELECT ON birdblog.vw_bg_article_public TO 'blog_reader';
GRANT SELECT ON birdblog.bg_category TO 'blog_reader';
GRANT SELECT ON birdblog.bg_tag      TO 'blog_reader';
GRANT SELECT ON birdblog.bg_link     TO 'blog_reader';

-- 作者可写文章/评论（受触发器维护计数）
GRANT SELECT, INSERT, UPDATE ON birdblog.bg_article     TO 'blog_author';
GRANT SELECT, INSERT, UPDATE ON birdblog.bg_article_tag TO 'blog_author';
GRANT SELECT, INSERT, UPDATE ON birdblog.bg_comment     TO 'blog_author';

SET FOREIGN_KEY_CHECKS = 1;

