package com.birdy.mapper;

import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.TagVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

/**
* @author Young
* @description 针对表【bg_tag(标签表)】的数据库操作Mapper
* @createDate 2025-10-31 01:45:03
* @Entity com.birdy.domain.entity.Tag
*/
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 查询带文章数量的标签列表
     * @return 带文章数量的标签列表
     */
    List<TagVO> selectTagsWithArticleCount();
}




