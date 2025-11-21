-- 添加友链评论支持
-- 为 bg_comment 表添加 link_id 字段，用于存储友链评论的友链ID

-- 添加 link_id 字段
ALTER TABLE `bg_comment`
ADD COLUMN `link_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '友链ID（友链评论必填）' AFTER `article_id`;

-- 添加友链评论的外键约束
ALTER TABLE `bg_comment`
ADD CONSTRAINT `fk_comment_link` FOREIGN KEY (`link_id`) REFERENCES `bg_link` (`id`) ON DELETE CASCADE;

-- 更新索引，添加 link_id 字段的索引
ALTER TABLE `bg_comment`
ADD INDEX `idx_comment_link` (`link_id`, `root_id`);