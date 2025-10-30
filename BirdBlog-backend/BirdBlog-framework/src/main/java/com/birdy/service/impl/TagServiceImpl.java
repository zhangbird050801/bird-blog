package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.ArticleTag;
import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.TagVO;
import com.birdy.mapper.ArticleTagMapper;
import com.birdy.mapper.TagMapper;
import com.birdy.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Young
* @description 针对表【bg_tag(标签表)】的数据库操作Service实现
* @createDate 2025-10-31 01:45:03
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public CommonResult<List<TagVO>> getAllTags() {
        // 查询所有未删除的标签
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getDeleted, false);
        List<Tag> tags = list(queryWrapper);

        List<TagVO> res = BeanUtil.copyToList(tags, TagVO.class);

        return CommonResult.success(res);
    }

    @Override
    public CommonResult<List<TagVO>> getTagsByArticleId(Long articleId) {
        // 根据文章ID查询文章-标签关联表，获取标签ID列表
        LambdaQueryWrapper<ArticleTag> articleTagWrapper = new LambdaQueryWrapper<>();
        articleTagWrapper.eq(ArticleTag::getArticleId, articleId)
                .eq(ArticleTag::getDeleted, false);
        List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagWrapper);

        // 提取标签ID列表
        List<Long> tagIds = articleTags.stream()
                .map(ArticleTag::getTagId)
                .collect(Collectors.toList());

        // 如果没有标签，返回空列表
        if (tagIds.isEmpty()) {
            return CommonResult.success(List.of());
        }

        // 根据标签ID列表查询标签详情
        LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.in(Tag::getId, tagIds)
                .eq(Tag::getDeleted, false);
        List<Tag> tags = list(tagWrapper);

        List<TagVO> res = BeanUtil.copyToList(tags, TagVO.class);

        return CommonResult.success(res);
    }
}
