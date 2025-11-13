package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Article;
import com.birdy.domain.entity.Category;
import com.birdy.domain.vo.CategoryVO;
import com.birdy.domain.vo.HotArticleVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.service.ArticleService;
import com.birdy.service.CategoryService;
import com.birdy.mapper.CategoryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.birdy.constants.SysConstants.CATEGORY_NOT_DELETED;

/**
* @author Young
* @description 针对表【bg_category(分类表)】的数据库操作Service实现
* @createDate 2025-10-29 01:00:54
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Autowired
    private ArticleService articleService;
    @Override
    public CommonResult<List<CategoryVO>> get() {
        //查询articles：状态为已发布的（status为0）
        LambdaQueryWrapper<Article> articlesWrapper = new LambdaQueryWrapper<>();
        articlesWrapper.eq(Article::getStatus, SysConstants.ARTICLE_STATUS_RELEASE);
        List<Article> list = articleService.list(articlesWrapper);

        //查询article的分类id，去重
        Set<Long> categoryIds = list.stream()
                .map(Article::getCategoryId)
                .collect(Collectors.toSet());
        //查询分类表（通过categoryId）
        List<Category> categories = listByIds(categoryIds).stream().
                filter(category -> SysConstants.CATEGORY_STATUS_ENABLE == category.getStatus())
                .collect(Collectors.toList());

        //封装vo
        List<CategoryVO> res = BeanUtil.copyToList(categories, CategoryVO.class);

        return CommonResult.success(res);
    }

    @Override
    public PageResult<Category> getPageList(Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<Category> page = new Page<>(pageNum, pageSize);

        // 构建查询条件：未删除的分类，按创建时间降序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDeleted, CATEGORY_NOT_DELETED)
                   .orderByDesc(Category::getCreateTime);

        // 执行分页查询
        page(page, queryWrapper);

        // 封装为 PageResult
        return new PageResult<>(
            page.getTotal(),
            page.getRecords(),
            (int) page.getCurrent(),
            (int) page.getSize()
        );
    }

    @Override
    public PageResult<Category> getPageListWithQuery(Integer pageNum, Integer pageSize, String name, Integer status) {
        // 创建分页对象
        Page<Category> page = new Page<>(pageNum, pageSize);

        // 构建查询条件：未删除的分类，按创建时间降序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDeleted, CATEGORY_NOT_DELETED);

        // 如果名称不为空，则添加模糊查询条件
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(Category::getName, name.trim());
        }

        // 如果状态不为空，则添加状态筛选条件
        if (status != null) {
            queryWrapper.eq(Category::getStatus, status);
        }

        queryWrapper.orderByDesc(Category::getCreateTime);

        // 执行分页查询
        page(page, queryWrapper);

        // 封装为 PageResult
        return new PageResult<>(
            page.getTotal(),
            page.getRecords(),
            (int) page.getCurrent(),
            (int) page.getSize()
        );
    }

    @Override
    public CommonResult<String> addCategory(Category category) {
        try {
            // 1. 参数校验
            if (category.getName() == null || category.getName().trim().isEmpty()) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "分类名称不能为空");
            }

            // 2. 检查分类名称是否重复
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Category::getName, category.getName().trim())
                    .eq(Category::getDeleted, CATEGORY_NOT_DELETED);
            Category existingCategory = getOne(queryWrapper);
            if (existingCategory != null) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "分类名称已存在");
            }

            // 3. 设置默认值
            category.setName(category.getName().trim());
            if (category.getStatus() == null) {
                category.setStatus(SysConstants.CATEGORY_STATUS_ENABLE);
            }
            if (category.getPid() == null) {
                category.setPid(null);
            }
            if (category.getDescription() != null) {
                category.setDescription(category.getDescription().trim());
            } else {
                category.setDescription("");
            }
            if (category.getCount() == 0) {
                category.setCount(0);
            }

            // 4. 设置创建信息
            category.setCreator("admin");
            category.setCreateTime(new java.util.Date());
            category.setUpdater("admin");
            category.setUpdateTime(new java.util.Date());
            category.setDeleted(CATEGORY_NOT_DELETED);

            // 5. 保存到数据库
            boolean saved = save(category);
            if (saved) {
                return CommonResult.success("新增分类成功");
            } else {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "新增分类失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "新增分类失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<String> updateCategory(Category category) {
        try {
            // 1. 参数校验
            if (category.getId() == null) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "分类ID不能为空");
            }
            if (category.getName() == null || category.getName().trim().isEmpty()) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "分类名称不能为空");
            }

            // 2. 检查分类是否存在
            Category existingCategory = getById(category.getId());
            if (existingCategory == null) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "分类不存在");
            }

            // 3. 检查分类名称是否重复（排除当前分类）
            if (!existingCategory.getName().equals(category.getName().trim())) {
                LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Category::getName, category.getName().trim())
                        .eq(Category::getDeleted, CATEGORY_NOT_DELETED)
                        .ne(Category::getId, category.getId());
                Category duplicateCategory = getOne(queryWrapper);
                if (duplicateCategory != null) {
                    return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "分类名称已存在");
                }
            }

            // 4. 检查父分类设置是否合理（不能选择自己或自己的子分类作为父分类）
            if (category.getPid() != null && !category.getPid().equals(0L)) {
                if (category.getPid().equals(category.getId())) {
                    return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "不能选择自己作为父分类");
                }
                // 这里可以添加更复杂的层级检查，但暂时只做基本检查
            }

            // 5. 更新分类信息
            existingCategory.setName(category.getName().trim());
            existingCategory.setPid(category.getPid());
            existingCategory.setStatus(category.getStatus());
            if (category.getDescription() != null) {
                existingCategory.setDescription(category.getDescription().trim());
            }
            existingCategory.setUpdater("admin");
            existingCategory.setUpdateTime(new java.util.Date());

            // 6. 保存更新
            boolean updated = updateById(existingCategory);
            if (updated) {
                return CommonResult.success("更新分类成功");
            } else {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "更新分类失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "更新分类失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<String> deleteCategories(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "请选择要删除的分类");
            }

            // 1. 检查每个分类是否有绑定的文章
            for (Long id : ids) {
                // 查询该分类下是否有文章
                LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
                articleWrapper.eq(Article::getCategoryId, id);
                long articleCount = articleService.count(articleWrapper);

                if (articleCount > 0) {
                    // 获取分类名称用于错误提示
                    Category category = getById(id);
                    String categoryName = category != null ? category.getName() : "未知分类";
                    return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                        String.format("分类【%s】下还有 %d 篇文章，无法删除。请先删除或转移这些文章。", categoryName, articleCount));
                }

                // 检查是否有子分类
                LambdaQueryWrapper<Category> childWrapper = new LambdaQueryWrapper<>();
                childWrapper.eq(Category::getPid, id)
                           .eq(Category::getDeleted, CATEGORY_NOT_DELETED);
                long childCount = count(childWrapper);

                if (childCount > 0) {
                    Category category = getById(id);
                    String categoryName = category != null ? category.getName() : "未知分类";
                    return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                        String.format("分类【%s】下还有 %d 个子分类，无法删除。请先删除子分类。", categoryName, childCount));
                }
            }

            // 2. 软删除分类（将deleted字段设为true）
            for (Long id : ids) {
                Category category = getById(id);
                if (category != null) {
                    category.setDeleted(true);
                    category.setUpdater("admin");
                    category.setUpdateTime(new java.util.Date());
                    updateById(category);
                }
            }

            return CommonResult.success("删除分类成功");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "删除分类失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<Category> getCategoryDetail(Long id) {
        try {
            if (id == null || id <= 0) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.PARAM_ERROR, "分类ID不能为空");
            }

            // 使用查询条件获取未删除的分类（处理多种未删除状态）
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Category::getId, id)
                       .and(wrapper ->
                           wrapper.isNull(Category::getDeleted)
                                   .or()
                                   .eq(Category::getDeleted, false)
                                   .or()
                                   .eq(Category::getDeleted, 0)
                       );

            Category category = getOne(queryWrapper);
            if (category == null) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.NOT_FOUND, "分类不存在或已被删除");
            }

            return CommonResult.success(category);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "获取分类详情失败：" + e.getMessage());
        }
    }
}




