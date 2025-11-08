package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.ArticleTag;
import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.PageResult;
import com.birdy.domain.vo.TagVO;
import com.birdy.mapper.ArticleTagMapper;
import com.birdy.mapper.TagMapper;
import com.birdy.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.birdy.constants.SysConstants.TAG_NOT_DELETED;

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
        // 直接查询带文章数量的标签列表
        List<TagVO> tagVOs = baseMapper.selectTagsWithArticleCount();

        return CommonResult.success(tagVOs);
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

    @Override
    public PageResult<Tag> getPageListWithQuery(Integer pageNum, Integer pageSize, String name) {
        // 创建分页对象
        Page<Tag> page = new Page<>(pageNum, pageSize);

        // 构建查询条件：按创建时间降序
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();

        // 查询未删除的标签（支持多种情况：null、false、0）
        queryWrapper.and(wrapper ->
            wrapper.isNull(Tag::getDeleted)
                   .or()
                   .eq(Tag::getDeleted, false)
                   .or()
                   .eq(Tag::getDeleted, 0)
        );

        // 如果名称不为空，则添加模糊查询条件
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(Tag::getName, name.trim());
        }

        queryWrapper.orderByDesc(Tag::getCreateTime);

        // 执行分页查询
        page(page, queryWrapper);

        // 打印调试信息
        System.out.println("=== 标签分页查询调试信息 ===");
        System.out.println("页码: " + pageNum);
        System.out.println("每页大小: " + pageSize);
        System.out.println("查询条件 name: " + name);
        System.out.println("总数: " + page.getTotal());
        System.out.println("当前页数据量: " + page.getRecords().size());
        System.out.println("SQL: " + queryWrapper.getCustomSqlSegment());

        // 封装为 PageResult
        return new PageResult<>(
            page.getTotal(),
            page.getRecords(),
            (int) page.getCurrent(),
            (int) page.getSize()
        );
    }
}
