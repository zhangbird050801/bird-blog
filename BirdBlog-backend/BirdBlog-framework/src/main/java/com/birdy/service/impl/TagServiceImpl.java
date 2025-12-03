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

    @Override
    public CommonResult<String> addTag(Tag tag) {
        try {
            // 1. 参数校验
            if (tag == null) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.PARAM_ERROR, "标签信息不能为空");
            }

            if (tag.getName() == null || tag.getName().trim().isEmpty()) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.PARAM_ERROR, "标签名称不能为空");
            }

            // 2. 检查标签名称是否重复
            String tagName = tag.getName().trim();
            System.out.println("=== 标签新增调试信息 ===");
            System.out.println("尝试新增标签名称: " + tagName);

            LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Tag::getName, tagName);

            // 先查询所有同名标签（包括已删除的）进行调试
            List<Tag> allSameNameTags = list(queryWrapper);
            System.out.println("同名标签数量: " + allSameNameTags.size());
            for (Tag existingTag : allSameNameTags) {
                System.out.println("找到同名标签 - ID: " + existingTag.getId() +
                                 ", 名称: " + existingTag.getName() +
                                 ", deleted: " + existingTag.getDeleted());
            }

            // 重新构造查询条件，只查找未删除的
            queryWrapper.and(wrapper ->
                wrapper.isNull(Tag::getDeleted)
                       .or()
                       .eq(Tag::getDeleted, false)
                       .or()
                       .eq(Tag::getDeleted, 0)
            );

            Tag existingUndeletedTag = getOne(queryWrapper);
            if (existingUndeletedTag != null) {
                System.out.println("找到未删除的同名标签，ID: " + existingUndeletedTag.getId());
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "标签名称已存在");
            }

            System.out.println("未找到未删除的同名标签，可以创建");

            // 3. 设置标签信息
            tag.setName(tag.getName().trim());
            tag.setCreator("admin");
            tag.setUpdater("admin");
            tag.setCreateTime(new java.util.Date());
            tag.setUpdateTime(new java.util.Date());
            tag.setDeleted(TAG_NOT_DELETED);

            // 4. 保存到数据库
            boolean saved = save(tag);
            if (saved) {
                return CommonResult.success("新增标签成功");
            } else {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "新增标签失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "新增标签失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<String> updateTag(Tag tag) {
        try {
            // 1. 参数校验
            if (tag == null || tag.getId() == null) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.PARAM_ERROR, "标签信息不能为空");
            }

            if (tag.getName() == null || tag.getName().trim().isEmpty()) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.PARAM_ERROR, "标签名称不能为空");
            }

            // 2. 检查标签是否存在
            Tag existingTag = getById(tag.getId());
            if (existingTag == null) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.NOT_FOUND, "标签不存在");
            }

            // 3. 检查标签名称是否重复（排除当前标签）
            if (!existingTag.getName().equals(tag.getName().trim())) {
                String newTagName = tag.getName().trim();
                LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Tag::getName, newTagName)
                        .ne(Tag::getId, tag.getId())
                        .and(wrapper ->
                            wrapper.isNull(Tag::getDeleted)
                                   .or()
                                   .eq(Tag::getDeleted, false)
                                   .or()
                                   .eq(Tag::getDeleted, 0)
                        );
                Tag duplicateTag = getOne(queryWrapper);
                if (duplicateTag != null) {
                    return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "标签名称已存在");
                }
            }

            // 4. 更新标签信息
            tag.setName(tag.getName().trim());
            tag.setUpdater("admin");
            tag.setUpdateTime(new java.util.Date());
            // 保持原有的deleted状态，不修改

            boolean updated = updateById(tag);
            if (updated) {
                return CommonResult.success("更新标签成功");
            } else {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "更新标签失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "更新标签失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<String> deleteTags(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return CommonResult.error(com.birdy.enums.HttpCodeEnum.PARAM_ERROR, "请选择要删除的标签");
            }

            // 1. 检查每个标签是否有绑定的文章
            for (Long id : ids) {
                // 查询该标签下是否有文章
                LambdaQueryWrapper<ArticleTag> articleTagWrapper = new LambdaQueryWrapper<>();
                articleTagWrapper.eq(ArticleTag::getTagId, id)
                               .eq(ArticleTag::getDeleted, false);
                long articleCount = articleTagMapper.selectCount(articleTagWrapper);

                if (articleCount > 0) {
                    // 获取标签名称用于错误提示
                    Tag tag = getById(id);
                    String tagName = tag != null ? tag.getName() : "未知标签";
                    return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR,
                        String.format("标签【%s】下还有 %d 篇文章，无法删除。请先删除或转移这些文章。", tagName, articleCount));
                }
            }

            // 2. 软删除标签（将deleted字段设为true）
            for (Long id : ids) {
                Tag tag = getById(id);
                if (tag != null) {
                    tag.setDeleted(true);
                    tag.setUpdater("admin");
                    tag.setUpdateTime(new java.util.Date());
                    updateById(tag);
                }
            }

            return CommonResult.success("删除标签成功");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.SYSTEM_ERROR, "删除标签失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<Tag> getDetail(Long id) {
        if (id == null) {
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.PARAM_ERROR, "标签ID不能为空");
        }
        Tag tag = getById(id);
        if (tag == null || Boolean.TRUE.equals(tag.getDeleted())) {
            return CommonResult.error(com.birdy.enums.HttpCodeEnum.NOT_FOUND, "标签不存在");
        }
        return CommonResult.success(tag);
    }
}
