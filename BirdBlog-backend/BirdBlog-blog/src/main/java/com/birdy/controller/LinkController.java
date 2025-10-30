package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.vo.LinkVO;
import com.birdy.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/link")
public class LinkController {
    @Autowired
    private LinkService linkService;
    
    @GetMapping("/get")
    public CommonResult<List<LinkVO>> getAllLink(){
        return linkService.getAllLink();
    }
}
