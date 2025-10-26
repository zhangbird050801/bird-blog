package com.birdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Article;
import com.birdy.domain.vo.HotArticleVO;
import com.birdy.service.ArticleService;
import com.birdy.mapper.ArticleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author birdy
* @description 针对表【bg_article(文章表)】的数据库操作Service实现
* @createDate 2025-10-24 16:59:24
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{


    @Override
    public CommonResult hot() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        // status 状态为 0 -> 已发布
        queryWrapper.eq(Article::getStatus, 0);
        // view_count 浏览量降序
        queryWrapper.orderByDesc(Article::getViewCount);
        // 10 条
        Page<Article> page = new Page<>(1, 10);
        page(page, queryWrapper);

        List<HotArticleVO> res = page.getRecords().stream()
                .map(article -> {
                    HotArticleVO vo = new HotArticleVO();
                    BeanUtils.copyProperties(article, vo);
                    return vo;
                })
                .toList();

        return CommonResult.success(res);
    }
}




