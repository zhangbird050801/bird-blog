/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : birdblog

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 15/12/2025 19:20:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bg_article
-- ----------------------------
DROP TABLE IF EXISTS `bg_article`;
CREATE TABLE `bg_article`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `slug` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'URL别名（便于SEO）',
  `summary` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '摘要',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '正文内容',
  `category_id` bigint UNSIGNED NOT NULL COMMENT '分类ID',
  `author_id` bigint UNSIGNED NOT NULL COMMENT '作者(用户ID)',
  `thumbnail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '缩略图URL',
  `is_top` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否置顶',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0已发布，1草稿',
  `is_comment` bit(1) NOT NULL DEFAULT b'1' COMMENT '允许评论',
  `view_count` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞量',
  `comment_count` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '评论数',
  `published_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `pinned_time` datetime NULL DEFAULT NULL COMMENT '置顶时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_bg_article_slug`(`slug` ASC) USING BTREE,
  INDEX `idx_bg_article_cat`(`category_id` ASC) USING BTREE,
  INDEX `idx_bg_article_pub`(`status` ASC, `deleted` ASC, `published_time` ASC) USING BTREE,
  INDEX `fk_article_author`(`author_id` ASC) USING BTREE,
  FULLTEXT INDEX `ft_article_text`(`title`, `summary`, `content`) WITH PARSER `ngram`,
  CONSTRAINT `fk_article_author` FOREIGN KEY (`author_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `bg_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `bg_article_chk_1` CHECK (`status` in (0,1))
) ENGINE = InnoDB AUTO_INCREMENT = 10044 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for bg_article_favorite
-- ----------------------------
DROP TABLE IF EXISTS `bg_article_favorite`;
CREATE TABLE `bg_article_favorite`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏记录ID',
  `article_id` bigint UNSIGNED NOT NULL COMMENT '文章ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_favorite_article_user`(`article_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_article_id`(`article_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_favorite_article_deleted`(`article_id` ASC, `deleted` ASC) USING BTREE,
  CONSTRAINT `fk_favorite_article` FOREIGN KEY (`article_id`) REFERENCES `bg_article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for bg_article_like
-- ----------------------------
DROP TABLE IF EXISTS `bg_article_like`;
CREATE TABLE `bg_article_like`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '点赞记录ID',
  `article_id` bigint UNSIGNED NOT NULL COMMENT '文章ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_article_user`(`article_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_article_id`(`article_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_like_article` FOREIGN KEY (`article_id`) REFERENCES `bg_article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for bg_article_tag
-- ----------------------------
DROP TABLE IF EXISTS `bg_article_tag`;
CREATE TABLE `bg_article_tag`  (
  `article_id` bigint UNSIGNED NOT NULL COMMENT '文章ID',
  `tag_id` bigint UNSIGNED NOT NULL COMMENT '标签ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`article_id`, `tag_id`) USING BTREE,
  INDEX `fk_at_tag`(`tag_id` ASC) USING BTREE,
  CONSTRAINT `fk_at_article` FOREIGN KEY (`article_id`) REFERENCES `bg_article` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_at_tag` FOREIGN KEY (`tag_id`) REFERENCES `bg_tag` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章-标签关联' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for bg_category
-- ----------------------------
DROP TABLE IF EXISTS `bg_category`;
CREATE TABLE `bg_category`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名',
  `pid` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父分类ID，NULL表示顶级',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0正常，1禁用',
  `count` int NULL DEFAULT NULL COMMENT '分类文章数量',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_bg_category_name`(`name` ASC) USING BTREE,
  INDEX `idx_bg_category_pid`(`pid` ASC) USING BTREE,
  CONSTRAINT `fk_bg_category_parent` FOREIGN KEY (`pid`) REFERENCES `bg_category` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `bg_category_chk_1` CHECK (`status` in (0,1))
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for bg_comment
-- ----------------------------
DROP TABLE IF EXISTS `bg_comment`;
CREATE TABLE `bg_comment`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '评论类型：0文章评论，1友链评论',
  `article_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '文章ID（文章评论必填）',
  `link_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '友链ID（友链评论必填）',
  `root_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '根评论ID',
  `parent_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父评论ID（楼中楼）',
  `from_user_id` bigint UNSIGNED NOT NULL COMMENT '评论用户ID',
  `to_user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '被回复用户ID',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0正常 1屏蔽',
  `like_count` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞数',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_comment_article`(`article_id` ASC, `root_id` ASC) USING BTREE,
  INDEX `idx_comment_parent`(`parent_id` ASC) USING BTREE,
  INDEX `fk_comment_root`(`root_id` ASC) USING BTREE,
  INDEX `fk_comment_from_user`(`from_user_id` ASC) USING BTREE,
  INDEX `fk_comment_to_user`(`to_user_id` ASC) USING BTREE,
  INDEX `idx_comment_link`(`link_id` ASC, `root_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `bg_article` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_from_user` FOREIGN KEY (`from_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_link` FOREIGN KEY (`link_id`) REFERENCES `bg_link` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `bg_comment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_root` FOREIGN KEY (`root_id`) REFERENCES `bg_comment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_to_user` FOREIGN KEY (`to_user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `bg_comment_chk_1` CHECK (`type` in (0,1)),
  CONSTRAINT `bg_comment_chk_2` CHECK (`status` in (0,1))
) ENGINE = InnoDB AUTO_INCREMENT = 50098 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for bg_link
-- ----------------------------
DROP TABLE IF EXISTS `bg_link`;
CREATE TABLE `bg_link`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '友链ID',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站名称',
  `logo` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'LogoURL',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站地址',
  `status` tinyint NOT NULL DEFAULT 2 COMMENT '审核状态：0通过 1未通过 2待审核',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_bg_link_address`(`url` ASC) USING BTREE,
  CONSTRAINT `bg_link_chk_1` CHECK (`status` in (0,1,2))
) ENGINE = InnoDB AUTO_INCREMENT = 9039 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '友链' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for bg_tag
-- ----------------------------
DROP TABLE IF EXISTS `bg_tag`;
CREATE TABLE `bg_tag`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_bg_tag_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 130 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父菜单ID',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `is_frame` tinyint NOT NULL DEFAULT 1 COMMENT '是否外链：0是 1否',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'M' COMMENT 'M目录 C菜单 F按钮',
  `visible` tinyint NOT NULL DEFAULT 0 COMMENT '显示：0显示 1隐藏',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0正常 1停用',
  `perms` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#' COMMENT '图标',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_menu_parent`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `fk_sys_menu_parent` FOREIGN KEY (`parent_id`) REFERENCES `sys_menu` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2012 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0正常，1禁用',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_role_code`(`code` ASC) USING BTREE,
  CONSTRAINT `sys_role_chk_1` CHECK (`status` in (0,1))
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色菜单关联ID',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  `menu_id` bigint UNSIGNED NOT NULL COMMENT '菜单ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_role_menu`(`role_id` ASC, `menu_id` ASC) USING BTREE,
  INDEX `fk_rm_menu`(`menu_id` ASC) USING BTREE,
  CONSTRAINT `fk_rm_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_rm_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 131 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单权限关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '昵称',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码Hash',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '账号状态：0正常，1停用',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `sex` tinyint NULL DEFAULT 2 COMMENT '性别：0男，1女，2未知',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_sys_user_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户角色关联ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` bigint UNSIGNED NOT NULL COMMENT '角色ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `fk_ur_role`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- View structure for v_article_comment_rank
-- ----------------------------
DROP VIEW IF EXISTS `v_article_comment_rank`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_article_comment_rank` AS select `a`.`id` AS `article_id`,`a`.`title` AS `title`,`a`.`slug` AS `slug`,`a`.`view_count` AS `view_count`,ifnull(`l`.`like_count`,0) AS `like_count`,ifnull(`f`.`favorite_count`,0) AS `favorite_count`,ifnull(`c`.`comment_count`,0) AS `comment_count`,`a`.`published_time` AS `published_time` from (((`bg_article` `a` left join (select `bg_article_like`.`article_id` AS `article_id`,count(0) AS `like_count` from `bg_article_like` where (`bg_article_like`.`deleted` = 0) group by `bg_article_like`.`article_id`) `l` on((`l`.`article_id` = `a`.`id`))) left join (select `bg_article_favorite`.`article_id` AS `article_id`,count(0) AS `favorite_count` from `bg_article_favorite` where (`bg_article_favorite`.`deleted` = 0) group by `bg_article_favorite`.`article_id`) `f` on((`f`.`article_id` = `a`.`id`))) left join (select `bg_comment`.`article_id` AS `article_id`,count(0) AS `comment_count` from `bg_comment` where (`bg_comment`.`deleted` = 0) group by `bg_comment`.`article_id`) `c` on((`c`.`article_id` = `a`.`id`))) where ((`a`.`deleted` = 0) and (`a`.`status` = 0)) order by ifnull(`c`.`comment_count`,0) desc;

-- ----------------------------
-- View structure for v_article_engagement_rank
-- ----------------------------
DROP VIEW IF EXISTS `v_article_engagement_rank`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_article_engagement_rank` AS select `a`.`id` AS `article_id`,`a`.`title` AS `title`,`a`.`slug` AS `slug`,`a`.`view_count` AS `view_count`,ifnull(`l`.`like_count`,0) AS `like_count`,ifnull(`f`.`favorite_count`,0) AS `favorite_count`,ifnull(`c`.`comment_count`,0) AS `comment_count`,(ifnull(`l`.`like_count`,0) + ifnull(`f`.`favorite_count`,0)) AS `engagement_score`,`a`.`published_time` AS `published_time` from (((`bg_article` `a` left join (select `bg_article_like`.`article_id` AS `article_id`,count(0) AS `like_count` from `bg_article_like` where (`bg_article_like`.`deleted` = 0) group by `bg_article_like`.`article_id`) `l` on((`l`.`article_id` = `a`.`id`))) left join (select `bg_article_favorite`.`article_id` AS `article_id`,count(0) AS `favorite_count` from `bg_article_favorite` where (`bg_article_favorite`.`deleted` = 0) group by `bg_article_favorite`.`article_id`) `f` on((`f`.`article_id` = `a`.`id`))) left join (select `bg_comment`.`article_id` AS `article_id`,count(0) AS `comment_count` from `bg_comment` where (`bg_comment`.`deleted` = 0) group by `bg_comment`.`article_id`) `c` on((`c`.`article_id` = `a`.`id`))) where ((`a`.`deleted` = 0) and (`a`.`status` = 0));

-- ----------------------------
-- View structure for v_hot_articles
-- ----------------------------
DROP VIEW IF EXISTS `v_hot_articles`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_hot_articles` AS select `a`.`id` AS `article_id`,`a`.`title` AS `title`,`a`.`slug` AS `slug`,`a`.`view_count` AS `view_count`,`a`.`published_time` AS `published_time` from `bg_article` `a` where ((`a`.`deleted` = 0) and (`a`.`status` = 0));

-- ----------------------------
-- View structure for v_tag_usage_rank
-- ----------------------------
DROP VIEW IF EXISTS `v_tag_usage_rank`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_tag_usage_rank` AS select `t`.`id` AS `tag_id`,`t`.`name` AS `name`,count(`at`.`article_id`) AS `value` from ((`bg_tag` `t` left join `bg_article_tag` `at` on((`at`.`tag_id` = `t`.`id`))) left join `bg_article` `a` on(((`a`.`id` = `at`.`article_id`) and (`a`.`deleted` = 0) and (`a`.`status` = 0)))) where (`t`.`deleted` = 0) group by `t`.`id`,`t`.`name` order by `value` desc;

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
CREATE TRIGGER `trg_comment_after_insert` AFTER INSERT ON `bg_comment` FOR EACH ROW BEGIN
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
CREATE TRIGGER `trg_comment_after_update` AFTER UPDATE ON `bg_comment` FOR EACH ROW BEGIN
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
