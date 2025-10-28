package com.birdy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Category;
import com.birdy.service.CategoryService;
import com.birdy.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author Young
* @description 针对表【bg_category(分类表)】的数据库操作Service实现
* @createDate 2025-10-29 01:00:54
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Override
    public CommonResult get() {
        //查询articals：状态为已发布的（）
        return null;
    }
}




