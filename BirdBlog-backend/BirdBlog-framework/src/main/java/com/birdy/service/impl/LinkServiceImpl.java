package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Link;
import com.birdy.domain.vo.CategoryVO;
import com.birdy.domain.vo.LinkVO;
import com.birdy.service.LinkService;
import com.birdy.mapper.LinkMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Young
* @description 针对表【bg_link(友链)】的数据库操作Service实现
* @createDate 2025-10-29 23:07:13
*/
@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link>
    implements LinkService{

    @Override
    public CommonResult<List<LinkVO>> getAllLink() {
        //查询所以审核通过的友链
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SysConstants.LINK_STATUS_ENABLE);
        List<Link> links = list(queryWrapper);

        //转换成vo
        List<LinkVO> res = BeanUtil.copyToList(links, LinkVO.class);

        //封装返回
        return CommonResult.success(res);
    }
}




