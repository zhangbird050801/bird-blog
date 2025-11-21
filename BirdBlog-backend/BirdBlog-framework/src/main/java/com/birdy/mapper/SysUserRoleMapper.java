package com.birdy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.birdy.domain.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 *
 * @author Birdy
 * @date 2025/11/21
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}