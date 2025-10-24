package com.birdy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.entity.Article;
import com.birdy.service.ArticleService;
import com.birdy.mapper.ArticleMapper;
import org.springframework.stereotype.Service;

/**
* @author birdy
* @description 针对表【bg_article(文章表)】的数据库操作Service实现
* @createDate 2025-10-24 16:59:24
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

}




