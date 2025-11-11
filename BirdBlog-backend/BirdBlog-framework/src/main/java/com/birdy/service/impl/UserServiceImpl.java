package com.birdy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.birdy.constants.SysConstants;
import com.birdy.domain.CommonResult;
import com.birdy.domain.dto.UserQueryDTO;
import com.birdy.domain.entity.User;
import com.birdy.domain.vo.AdminUserVO;
import com.birdy.domain.vo.PageResult;
import com.birdy.enums.HttpCodeEnum;
import com.birdy.mapper.UserMapper;
import com.birdy.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* 针对表【sys_user(用户表)】的数据库操作Service实现
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public CommonResult<PageResult<AdminUserVO>> getUserPage(UserQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new UserQueryDTO();
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getDeleted, false);

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            String keyword = queryDTO.getKeyword();
            queryWrapper.and(wrapper -> wrapper
                .like(User::getUsername, keyword)
                .or().like(User::getNickName, keyword)
                .or().like(User::getEmail, keyword)
                .or().like(User::getPhone, keyword)
            );
        }

        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(User::getStatus, queryDTO.getStatus());
        }

        if (queryDTO.getType() != null) {
            queryWrapper.eq(User::getType, queryDTO.getType());
        }

        queryWrapper.orderByAsc(User::getId);

        int pageNum = queryDTO.getPageNum() == null ? 1 : queryDTO.getPageNum();
        int pageSize = queryDTO.getPageSize() == null ? 10 : queryDTO.getPageSize();
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> resultPage = baseMapper.selectPage(page, queryWrapper);

        List<AdminUserVO> rows = resultPage.getRecords().stream()
            .map(this::convertToAdminUserVO)
            .collect(Collectors.toList());

        PageResult<AdminUserVO> pageResult = new PageResult<>(
            resultPage.getTotal(),
            rows,
            (int) resultPage.getCurrent(),
            (int) resultPage.getSize()
        );

        return CommonResult.success(pageResult);
    }

    @Override
    public CommonResult<Void> updateUserStatus(Long userId, Integer status) {
        if (userId == null || status == null ||
            (status != SysConstants.USER_STATUS_NORMAL && status != SysConstants.USER_STATUS_DISABLE)) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "参数非法");
        }

        User user = baseMapper.selectById(userId);
        if (user == null || Boolean.TRUE.equals(user.getDeleted())) {
            return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "用户不存在");
        }

        if (user.getStatus() != null && user.getStatus().equals(status)) {
            return CommonResult.success();
        }

        user.setStatus(status);
        user.setUpdateTime(new Date());
        int rows = baseMapper.updateById(user);
        if (rows > 0) {
            return CommonResult.success();
        }
        return CommonResult.error(HttpCodeEnum.SYSTEM_ERROR, "更新用户状态失败");
    }

    private AdminUserVO convertToAdminUserVO(User user) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickName(user.getNickName());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        vo.setType(user.getType());
        vo.setSex(user.getSex());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}
