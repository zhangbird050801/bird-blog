package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Tag;
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
}
