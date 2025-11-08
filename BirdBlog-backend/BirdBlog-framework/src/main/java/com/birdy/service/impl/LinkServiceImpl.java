package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Link;
import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.CategoryVO;
import com.birdy.domain.vo.LinkVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.service.LinkService;
import com.birdy.mapper.LinkMapper;
//import org.springframework.data.domain.Page;
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

    @Override
    public PageResult<Link> getPageListWithQuery(Integer pageNum, Integer pageSize, String name, Integer status) {
        //创建分页对象
        Page<Link> page = new Page<>(pageNum, pageSize);

        // 构建查询条件：按创建时间降序
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();

        //查询未删除的友链
        queryWrapper.and(wrapper ->
                wrapper.isNull(Link::getDeleted)
                        .or()
                        .eq(Link::getDeleted, false)
                        .or()
                        .eq(Link::getDeleted, 0)
        );

        //增加模糊查询条件
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(Link::getName, name.trim());
        }

        //增加状态筛选条件
        if (status != null) {
            queryWrapper.eq(Link::getStatus, status);
        }

        //查询结果按创建时间的降序排列
        queryWrapper.orderByDesc(Link::getCreateTime);

        //执行分页查询
        page(page,queryWrapper);

        // 打印调试信息
        System.out.println("=== 友链分页查询调试信息 ===");
        System.out.println("页码: " + pageNum);
        System.out.println("每页大小: " + pageSize);
        System.out.println("查询条件 name: " + name);
        System.out.println("查询条件 status: " + status);
        System.out.println("总数: " + page.getTotal());
        System.out.println("当前页数据量: " + page.getRecords().size());

        //封装为PageResult
        return new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                (int) page.getCurrent(),
                (int) page.getSize()
        );
    }
}




