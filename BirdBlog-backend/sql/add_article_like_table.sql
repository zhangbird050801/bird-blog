-- 创建文章点赞表
DROP TABLE IF EXISTS `bg_article_like`;
CREATE TABLE `bg_article_like` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '点赞记录ID',
  `article_id` bigint UNSIGNED NOT NULL COMMENT '文章ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_article_user` (`article_id`,`user_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_article_id` (`article_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_like_article` FOREIGN KEY (`article_id`) REFERENCES `bg_article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章点赞表';