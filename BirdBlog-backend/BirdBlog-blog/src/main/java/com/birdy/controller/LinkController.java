package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/link")
public class LinkController {
    @Autowired
    private LinkService linkService;
    @GetMapping("/get")
    public CommonResult getAllLink(){
        return linkService.getAllLink();
    }
}
