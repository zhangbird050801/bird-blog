-- 更新分类表的count字段为实际的文章数量
UPDATE bg_category c
SET count = (
    SELECT COUNT(*)
    FROM bg_article a
    WHERE a.category_id = c.id
      AND a.status = 0  -- 已发布
      AND a.deleted = 0  -- 未删除
)
WHERE EXISTS (
    SELECT 1 FROM bg_article a
    WHERE a.category_id = c.id
      AND a.status = 0
      AND a.deleted = 0
);

-- 更新没有文章的分类为0
UPDATE bg_category c
SET count = 0
WHERE NOT EXISTS (
    SELECT 1 FROM bg_article a
    WHERE a.category_id = c.id
      AND a.status = 0
      AND a.deleted = 0
);

-- 查看更新后的结果
SELECT
    c.id,
    c.name,
    c.count as updated_count,
    (SELECT COUNT(*) FROM bg_article a WHERE a.category_id = c.id AND a.status = 0 AND a.deleted = 0) as actual_count
FROM bg_category c
WHERE c.deleted = 0
ORDER BY c.id;