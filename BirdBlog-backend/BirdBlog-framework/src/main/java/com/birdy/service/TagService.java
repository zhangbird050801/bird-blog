package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.PageResult;
import com.birdy.domain.vo.TagVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Young
* @description 针对表【bg_tag(标签表)】的数据库操作Service
* @createDate 2025-10-31 01:45:03
*/
public interface TagService extends IService<Tag> {

    /**
     * 获取所有标签
     * @return 标签列表
     */
    CommonResult<List<TagVO>> getAllTags();

    /**
     * 根据文章ID获取该文章的所有标签
     * @param articleId 文章ID
     * @return 标签列表
     */
    CommonResult<List<TagVO>> getTagsByArticleId(Long articleId);

    /**
     * 获取分页标签列表（支持模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param name 标签名称（支持模糊查询）
     * @return 分页结果
     */
    PageResult<Tag> getPageListWithQuery(Integer pageNum, Integer pageSize, String name);

    /**
     * 新增标签
     * @param tag 标签信息
     * @return 新增结果
     */
    CommonResult<String> addTag(Tag tag);

    /**
     * 更新标签
     * @param tag 标签信息
     * @return 更新结果
     */
    CommonResult<String> updateTag(Tag tag);

    /**
     * 删除标签
     * @param ids 标签ID列表
     * @return 删除结果
     */
    CommonResult<String> deleteTags(List<Long> ids);

    /**
     * 获取标签详情
     * @param id 标签ID
     * @return 标签信息
     */
    CommonResult<Tag> getDetail(Long id);
}
