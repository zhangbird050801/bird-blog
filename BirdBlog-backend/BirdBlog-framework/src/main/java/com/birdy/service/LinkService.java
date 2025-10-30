package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Link;
import com.baomidou.mybatisplus.extension.service.IService;
import com.birdy.domain.vo.LinkVO;

import java.util.List;

/**
* @author Young
* @description 针对表【bg_link(友链)】的数据库操作Service
* @createDate 2025-10-29 23:07:13
*/
public interface LinkService extends IService<Link> {

    CommonResult<List<LinkVO>> getAllLink();
}
