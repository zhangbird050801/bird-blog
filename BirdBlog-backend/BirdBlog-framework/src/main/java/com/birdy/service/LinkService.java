package com.birdy.service;

import com.birdy.domain.CommonResult;
import com.birdy.domain.entity.Link;
import com.baomidou.mybatisplus.extension.service.IService;
import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.LinkVO;
import com.birdy.domain.vo.PageResult;

import java.util.List;

/**
* @author Young
* @description 针对表【bg_link(友链)】的数据库操作Service
* @createDate 2025-10-29 23:07:13
*/
public interface LinkService extends IService<Link> {

    /**
     * 获取所有友链
     * @return 友链列表
     */
    CommonResult<List<LinkVO>> getAllLink();

    /**
     * 获取分页友链列表（支持模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param name 友链名称（支持模糊查询）
     * @return 分页结果
     */

    PageResult<Link> getPageListWithQuery(Integer pageNum, Integer pageSize, String name);

}
