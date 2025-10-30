package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.birdy.domain.vo.CategoryVO;

import java.util.List;

/**
* @author Young
* @description 针对表【bg_category(分类表)】的数据库操作Service
* @createDate 2025-10-29 01:00:54
*/
public interface CategoryService extends IService<Category> {

    CommonResult<List<CategoryVO>> get();
}
