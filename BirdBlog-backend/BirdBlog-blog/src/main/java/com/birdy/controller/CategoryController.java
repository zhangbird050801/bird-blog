package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.vo.CategoryVO;
import com.birdy.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/category")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/get")
    public CommonResult<List<CategoryVO>> get(){
        return categoryService.get();
    }
}
