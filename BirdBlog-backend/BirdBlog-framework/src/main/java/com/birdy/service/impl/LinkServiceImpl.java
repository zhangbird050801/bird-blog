package com.birdy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.LinkApplicationRequest;
import com.birdy.domain.entity.Link;
import com.birdy.domain.entity.Tag;
import com.birdy.domain.vo.CategoryVO;
import com.birdy.domain.vo.LinkVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.service.LinkService;
import com.birdy.mapper.LinkMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public CommonResult<String> applyForLink(LinkApplicationRequest request) {
        // 参数校验
        if (StrUtil.isBlank(request.getName())) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "网站名称不能为空");
        }
        if (StrUtil.isBlank(request.getUrl())) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "网站地址不能为空");
        }
        if (StrUtil.isBlank(request.getDescription())) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "网站描述不能为空");
        }

        // URL 格式校验
        String url = request.getUrl().trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "网站地址格式不正确，请以 http:// 或 https:// 开头");
        }

        // 检查是否重复申请
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getUrl, url);
        queryWrapper.and(wrapper ->
                wrapper.isNull(Link::getDeleted)
                        .or()
                        .eq(Link::getDeleted, false)
                        .or()
                        .eq(Link::getDeleted, 0)
        );
        Link existingLink = getOne(queryWrapper);
        if (existingLink != null) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "该网站已申请过友链");
        }

        // 构建申请者信息
        StringBuilder creatorInfo = new StringBuilder("游客申请");
        if (StrUtil.isNotBlank(request.getContactName())) {
            creatorInfo.append(" - ").append(request.getContactName().trim());
        }
        if (StrUtil.isNotBlank(request.getContactEmail())) {
            creatorInfo.append(" (").append(request.getContactEmail().trim()).append(")");
        }

        // 构建完整描述（包含申请留言）
        String fullDescription = request.getDescription().trim();
        if (StrUtil.isNotBlank(request.getMessage())) {
            fullDescription += "\n\n申请留言：" + request.getMessage().trim();
        }

        // 创建友链对象
        Link link = new Link();
        link.setName(request.getName().trim());
        link.setUrl(url);
        link.setDescription(fullDescription);
        
        // Logo 处理
        if (StrUtil.isNotBlank(request.getLogo())) {
            String logo = request.getLogo().trim();
            if (logo.startsWith("http://") || logo.startsWith("https://")) {
                link.setLogo(logo);
            }
        }
        
        // 设置为待审核状态
        link.setStatus(SysConstants.LINK_STATUS_PENDING);
        
        // 设置创建信息
        link.setCreator(creatorInfo.toString());
        link.setCreateTime(new Date());
        link.setUpdater("system");
        link.setUpdateTime(new Date());
        link.setDeleted(false);

        // 保存到数据库
        boolean saved = save(link);
        if (saved) {
            return CommonResult.success("友链申请提交成功，请等待管理员审核");
        } else {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "友链申请提交失败，请稍后重试");
        }
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




