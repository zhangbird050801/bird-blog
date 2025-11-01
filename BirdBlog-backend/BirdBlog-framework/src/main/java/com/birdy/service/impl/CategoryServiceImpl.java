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
                .map(article -> article.getCategoryId())
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
        queryWrapper.eq(Category::getDeleted, false)
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
}




