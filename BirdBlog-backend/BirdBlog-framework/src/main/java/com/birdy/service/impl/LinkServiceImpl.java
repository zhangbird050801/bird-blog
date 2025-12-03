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

    @Override
    public CommonResult<String> addLink(Link link) {
        try {
            // 1. 参数校验
            if (link == null) {
                return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "友链信息不能为空");
            }

            if (StrUtil.isBlank(link.getName())) {
                return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "网站名称不能为空");
            }
            if (StrUtil.isBlank(link.getUrl())) {
                return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "网站地址不能为空");
            }
            if (StrUtil.isBlank(link.getDescription())) {
                return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "网站描述不能为空");
            }

            // URL 格式校验
            String url = link.getUrl().trim();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "网站地址格式不正确，请以 http:// 或 https:// 开头");
            }

            // 2. 检查友链名称或URL是否重复
            LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Link::getUrl, url)
                    .and(wrapper ->
                            wrapper.isNull(Link::getDeleted)
                                    .or()
                                    .eq(Link::getDeleted, false)
                                    .or()
                                    .eq(Link::getDeleted, 0)
                    );
            Link existingLink = getOne(queryWrapper);
            if (existingLink != null) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "该网站已存在");
            }

            // 3. 设置友链信息
            link.setName(link.getName().trim());
            link.setUrl(url);
            link.setDescription(link.getDescription().trim());

            // Logo 处理
            if (StrUtil.isNotBlank(link.getLogo())) {
                String logo = link.getLogo().trim();
                if (!logo.startsWith("http://") && !logo.startsWith("https://")) {
                    link.setLogo(null); // 如果logo格式不正确，设为null
                }
            } else {
                link.setLogo(null);
            }

            // 设置默认状态为已通过审核
            link.setStatus(SysConstants.LINK_STATUS_ENABLE);

            // 设置创建信息
            link.setCreator("admin");
            link.setCreateTime(new Date());
            link.setUpdater("admin");
            link.setUpdateTime(new Date());
            link.setDeleted(SysConstants.LINK_NOT_DELETED);

            // 4. 保存到数据库
            boolean saved = save(link);
            if (saved) {
                return CommonResult.success("新增友链成功");
            } else {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "新增友链失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "新增友链失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<String> updateLink(Link link) {
        try {
            // 1. 参数校验
            if (link == null || link.getId() == null) {
                return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "友链信息不能为空");
            }

            if (StrUtil.isBlank(link.getName())) {
                return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "网站名称不能为空");
            }
            if (StrUtil.isBlank(link.getUrl())) {
                return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "网站地址不能为空");
            }
            if (StrUtil.isBlank(link.getDescription())) {
                return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "网站描述不能为空");
            }

            // 2. 检查友链是否存在
            Link existingLink = getById(link.getId());
            if (existingLink == null) {
                return CommonResult.error(HttpCodeEnum.NOT_FOUND, "友链不存在");
            }

            // URL 格式校验
            String url = link.getUrl().trim();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "网站地址格式不正确，请以 http:// 或 https:// 开头");
            }

            // 3. 检查URL是否重复（排除当前友链）
            if (!existingLink.getUrl().equals(url)) {
                LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Link::getUrl, url)
                        .ne(Link::getId, link.getId())
                        .and(wrapper ->
                                wrapper.isNull(Link::getDeleted)
                                        .or()
                                        .eq(Link::getDeleted, false)
                                        .or()
                                        .eq(Link::getDeleted, 0)
                        );
                Link duplicateLink = getOne(queryWrapper);
                if (duplicateLink != null) {
                    return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "该网站地址已存在");
                }
            }

            // 4. 更新友链信息
            link.setName(link.getName().trim());
            link.setUrl(url);
            link.setDescription(link.getDescription().trim());

            // Logo 处理
            if (StrUtil.isNotBlank(link.getLogo())) {
                String logo = link.getLogo().trim();
                if (!logo.startsWith("http://") && !logo.startsWith("https://")) {
                    link.setLogo(null); // 如果logo格式不正确，设为null
                }
            } else {
                link.setLogo(null);
            }

            link.setUpdater("admin");
            link.setUpdateTime(new Date());

            // 保持原有的deleted状态，不修改

            boolean updated = updateById(link);
            if (updated) {
                return CommonResult.success("更新友链成功");
            } else {
                return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新友链失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新友链失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<String> deleteLinks(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "请选择要删除的友链");
            }

            // 2. 软删除友链（将deleted字段设为true）
            for (Long id : ids) {
                Link link = getById(id);
                if (link != null) {
                    link.setDeleted(true);
                    link.setUpdater("admin");
                    link.setUpdateTime(new Date());
                    updateById(link);
                }
            }

            return CommonResult.success("删除友链成功");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "删除友链失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<Link> getDetail(Long id) {
        if (id == null) {
            return CommonResult.error(HttpCodeEnum.PARAM_ERROR, "友链ID不能为空");
        }
        Link link = getById(id);
        if (link == null || Boolean.TRUE.equals(link.getDeleted())) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "友链不存在");
        }
        return CommonResult.success(link);
    }
}
