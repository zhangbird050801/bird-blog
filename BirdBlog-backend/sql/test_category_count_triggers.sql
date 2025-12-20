-- ============================================
-- 分类文章数量触发器测试脚本
-- 日期：2025-12-15
-- ============================================

-- 准备测试环境
-- 注意：此脚本仅用于测试，不要在生产环境直接执行

-- ============================================
-- 测试前：查看当前分类 count 状态
-- ============================================
SELECT 
    c.id,
    c.name,
    c.count as current_count,
    (SELECT COUNT(*) FROM bg_article a 
     WHERE a.category_id = c.id 
       AND a.status = 0 
       AND a.deleted = 0) as actual_count
FROM bg_category c
WHERE c.deleted = 0
ORDER BY c.id;

-- ============================================
-- 测试场景1：新增已发布文章
-- 预期：对应分类 count +1
-- ============================================
-- 记录测试前的 count
SET @test_category_id = 10;
SET @count_before = (SELECT count FROM bg_category WHERE id = @test_category_id);

-- 插入测试文章（已发布状态）
INSERT INTO bg_article 
(title, slug, summary, content, category_id, author_id, status, view_count, like_count, comment_count, is_top, thumbnail, published_time, creator, create_time, updater, update_time, deleted)
VALUES
('触发器测试文章1', 'trigger-test-1', '测试摘要', '测试内容', @test_category_id, 1, 0, 0, 0, 0, 0, NULL, NOW(), 'test', NOW(), 'test', NOW(), b'0');

-- 验证 count 是否 +1
SELECT 
    @test_category_id as category_id,
    @count_before as count_before,
    (SELECT count FROM bg_category WHERE id = @test_category_id) as count_after,
    (SELECT count FROM bg_category WHERE id = @test_category_id) - @count_before as diff,
    '预期 diff = 1' as expected;

-- ============================================
-- 测试场景2：新增草稿文章
-- 预期：分类 count 不变
-- ============================================
SET @count_before = (SELECT count FROM bg_category WHERE id = @test_category_id);

-- 插入测试文章（草稿状态）
INSERT INTO bg_article 
(title, slug, summary, content, category_id, author_id, status, view_count, like_count, comment_count, is_top, thumbnail, published_time, creator, create_time, updater, update_time, deleted)
VALUES
('触发器测试文章2', 'trigger-test-2', '测试摘要', '测试内容', @test_category_id, 1, 1, 0, 0, 0, 0, NULL, NULL, 'test', NOW(), 'test', NOW(), b'0');

-- 验证 count 是否不变
SELECT 
    @test_category_id as category_id,
    @count_before as count_before,
    (SELECT count FROM bg_category WHERE id = @test_category_id) as count_after,
    (SELECT count FROM bg_category WHERE id = @test_category_id) - @count_before as diff,
    '预期 diff = 0' as expected;

-- ============================================
-- 测试场景3：草稿变为已发布
-- 预期：分类 count +1
-- ============================================
SET @test_article_id = (SELECT id FROM bg_article WHERE slug = 'trigger-test-2');
SET @count_before = (SELECT count FROM bg_category WHERE id = @test_category_id);

-- 更新文章状态为已发布
UPDATE bg_article 
SET status = 0, published_time = NOW()
WHERE id = @test_article_id;

-- 验证 count 是否 +1
SELECT 
    @test_category_id as category_id,
    @count_before as count_before,
    (SELECT count FROM bg_category WHERE id = @test_category_id) as count_after,
    (SELECT count FROM bg_category WHERE id = @test_category_id) - @count_before as diff,
    '预期 diff = 1' as expected;

-- ============================================
-- 测试场景4：已发布文章软删除
-- 预期：分类 count -1
-- ============================================
SET @count_before = (SELECT count FROM bg_category WHERE id = @test_category_id);

-- 软删除文章
UPDATE bg_article 
SET deleted = b'1'
WHERE id = @test_article_id;

-- 验证 count 是否 -1
SELECT 
    @test_category_id as category_id,
    @count_before as count_before,
    (SELECT count FROM bg_category WHERE id = @test_category_id) as count_after,
    (SELECT count FROM bg_category WHERE id = @test_category_id) - @count_before as diff,
    '预期 diff = -1' as expected;

-- ============================================
-- 测试场景5：文章恢复
-- 预期：分类 count +1
-- ============================================
SET @count_before = (SELECT count FROM bg_category WHERE id = @test_category_id);

-- 恢复文章
UPDATE bg_article 
SET deleted = b'0'
WHERE id = @test_article_id;

-- 验证 count 是否 +1
SELECT 
    @test_category_id as category_id,
    @count_before as count_before,
    (SELECT count FROM bg_category WHERE id = @test_category_id) as count_after,
    (SELECT count FROM bg_category WHERE id = @test_category_id) - @count_before as diff,
    '预期 diff = 1' as expected;

-- ============================================
-- 测试场景6：更换分类
-- 预期：旧分类 count -1，新分类 count +1
-- ============================================
SET @old_category_id = @test_category_id;
SET @new_category_id = 11;
SET @old_count_before = (SELECT count FROM bg_category WHERE id = @old_category_id);
SET @new_count_before = (SELECT count FROM bg_category WHERE id = @new_category_id);

-- 更换文章分类
UPDATE bg_article 
SET category_id = @new_category_id
WHERE id = @test_article_id;

-- 验证旧分类 count -1，新分类 count +1
SELECT 
    @old_category_id as old_category_id,
    @old_count_before as old_count_before,
    (SELECT count FROM bg_category WHERE id = @old_category_id) as old_count_after,
    (SELECT count FROM bg_category WHERE id = @old_category_id) - @old_count_before as old_diff,
    '预期 old_diff = -1' as expected_old,
    @new_category_id as new_category_id,
    @new_count_before as new_count_before,
    (SELECT count FROM bg_category WHERE id = @new_category_id) as new_count_after,
    (SELECT count FROM bg_category WHERE id = @new_category_id) - @new_count_before as new_diff,
    '预期 new_diff = 1' as expected_new;

-- ============================================
-- 清理测试数据
-- ============================================
-- 删除测试文章
DELETE FROM bg_article WHERE slug LIKE 'trigger-test-%';

-- 验证清理后的状态
SELECT 
    c.id,
    c.name,
    c.count as current_count,
    (SELECT COUNT(*) FROM bg_article a 
     WHERE a.category_id = c.id 
       AND a.status = 0 
       AND a.deleted = 0) as actual_count,
    c.count - (SELECT COUNT(*) FROM bg_article a 
               WHERE a.category_id = c.id 
                 AND a.status = 0 
                 AND a.deleted = 0) as diff
FROM bg_category c
WHERE c.deleted = 0
ORDER BY c.id;

-- ============================================
-- 测试总结
-- ============================================
-- 如果所有测试场景的 diff 都符合预期，说明触发器工作正常
-- 如果有差异，需要检查触发器逻辑或重新初始化 count 值
