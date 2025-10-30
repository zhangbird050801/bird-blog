package com.birdy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.entity.ArticleTag;
import com.birdy.service.ArticleTagService;
import com.birdy.mapper.ArticleTagMapper;
import org.springframework.stereotype.Service;

/**
* @author Young
* @description 针对表【bg_article_tag(文章-标签关联)】的数据库操作Service实现
* @createDate 2025-10-31 01:56:33
*/
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag>
    implements ArticleTagService{

}




