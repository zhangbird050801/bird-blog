package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Article;
import com.birdy.domain.entity.Category;
import com.birdy.domain.vo.CategoryVO;
import com.birdy.domain.vo.HotArticleVO;
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
    public CommonResult get() {
        //查询articals：状态为已发布的（status为0）
        LambdaQueryWrapper<Article> articlesWrapper = new LambdaQueryWrapper<>();
        articlesWrapper.eq(Article::getStatus, SysConstants.ARTICLE_STATUS_RELEASE);
        List<Article> list = articleService.list(articlesWrapper);

        //查询article的分类id，去重
        Set<Long> categoryIds = list.stream()
                .map(article -> article.getCategoryId())
                .collect(Collectors.toSet());
        //查询分类表（通过categoryId）
        List<Category> categories = listByIds(categoryIds);
        categories.stream().
                filter(category -> SysConstants.STATUS_RELEASE.equals(category.getStatus()))
                .collect(Collectors.toList());

        //封装vo
        List<CategoryVO> res = BeanUtil.copyToList(categories, CategoryVO.class);

        return CommonResult.success(res);
    }
}




