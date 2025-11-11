package com.birdy.controller;

import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.LinkApplicationRequest;
import com.birdy.domain.vo.LinkVO;
import com.birdy.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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

    @PostMapping("/apply")
    public CommonResult<String> applyForLink(@RequestBody @Valid LinkApplicationRequest request) {
        return linkService.applyForLink(request);
    }
}
