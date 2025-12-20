-- ============================================
-- 分类文章数量自动维护触发器
-- 功能：在文章新增/删除/状态变更时自动更新分类表的 count 字段
-- 日期：2025-12-15
-- ============================================

-- 删除已存在的触发器
DROP TRIGGER IF EXISTS `trg_article_category_after_insert`;
DROP TRIGGER IF EXISTS `trg_article_category_after_update`;
DROP TRIGGER IF EXISTS `trg_article_category_after_delete`;

-- ============================================
-- 触发器1：文章新增后更新分类 count
-- ============================================
delimiter ;;
CREATE TRIGGER `trg_article_category_after_insert` AFTER INSERT ON `bg_article` FOR EACH ROW
BEGIN
    -- 只有当文章状态为已发布(0)且未删除时，才增加分类计数
    IF NEW.status = 0 AND NEW.deleted = b'0' THEN
        UPDATE bg_category 
        SET count = count + 1 
        WHERE id = NEW.category_id;
    END IF;
END;;
delimiter ;

-- ============================================
-- 触发器2：文章更新后更新分类 count
-- 处理以下场景：
-- 1. 文章状态从草稿变为已发布：count +1
-- 2. 文章状态从已发布变为草稿：count -1
-- 3. 文章被软删除：count -1
-- 4. 文章被恢复：count +1
-- 5. 文章分类变更：旧分类 -1，新分类 +1
-- ============================================
delimiter ;;
CREATE TRIGGER `trg_article_category_after_update` AFTER UPDATE ON `bg_article` FOR EACH ROW
BEGIN
    DECLARE old_published BOOLEAN DEFAULT FALSE;
    DECLARE new_published BOOLEAN DEFAULT FALSE;
    
    -- 判断旧状态是否为"已发布且未删除"
    SET old_published = (OLD.status = 0 AND OLD.deleted = b'0');
    -- 判断新状态是否为"已发布且未删除"
    SET new_published = (NEW.status = 0 AND NEW.deleted = b'0');
    
    -- 场景1：分类未变更，但发布状态变更
    IF OLD.category_id = NEW.category_id THEN
        -- 从未发布变为已发布：count +1
        IF NOT old_published AND new_published THEN
            UPDATE bg_category 
            SET count = count + 1 
            WHERE id = NEW.category_id;
        -- 从已发布变为未发布：count -1
        ELSEIF old_published AND NOT new_published THEN
            UPDATE bg_category 
            SET count = count - 1 
            WHERE id = OLD.category_id AND count > 0;
        END IF;
    
    -- 场景2：分类变更
    ELSE
        -- 旧分类：如果原来是已发布状态，count -1
        IF old_published THEN
            UPDATE bg_category 
            SET count = count - 1 
            WHERE id = OLD.category_id AND count > 0;
        END IF;
        
        -- 新分类：如果现在是已发布状态，count +1
        IF new_published THEN
            UPDATE bg_category 
            SET count = count + 1 
            WHERE id = NEW.category_id;
        END IF;
    END IF;
END;;
delimiter ;

-- ============================================
-- 触发器3：文章物理删除后更新分类 count
-- 注意：通常使用软删除，此触发器作为保险措施
-- ============================================
delimiter ;;
CREATE TRIGGER `trg_article_category_after_delete` AFTER DELETE ON `bg_article` FOR EACH ROW
BEGIN
    -- 只有当被删除的文章是已发布状态时，才减少分类计数
    IF OLD.status = 0 AND OLD.deleted = b'0' THEN
        UPDATE bg_category 
        SET count = count - 1 
        WHERE id = OLD.category_id AND count > 0;
    END IF;
END;;
delimiter ;

-- ============================================
-- 验证触发器是否创建成功
-- ============================================
SELECT 
    TRIGGER_NAME,
    EVENT_MANIPULATION,
    EVENT_OBJECT_TABLE,
    ACTION_TIMING,
    ACTION_STATEMENT
FROM information_schema.TRIGGERS
WHERE TRIGGER_SCHEMA = DATABASE()
  AND TRIGGER_NAME LIKE 'trg_article_category%'
ORDER BY TRIGGER_NAME;

-- ============================================
-- 使用说明
-- ============================================
-- 1. 执行本脚本创建触发器
-- 2. 执行 update_category_count.sql 初始化现有数据的 count 值
-- 3. 之后所有文章的新增/删除/状态变更都会自动更新分类 count
--
-- 注意事项：
-- - 触发器会在每次文章操作时自动执行，无需手动调用
-- - 如果需要删除触发器，执行：DROP TRIGGER IF EXISTS trg_article_category_after_insert;
-- - 触发器只处理 status=0（已发布）且 deleted=0（未删除）的文章
-- - 分类 count 不会小于 0（使用 count > 0 条件保护）
