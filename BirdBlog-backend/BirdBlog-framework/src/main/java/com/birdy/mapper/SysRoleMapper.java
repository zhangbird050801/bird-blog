package com.birdy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.birdy.domain.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 *
 * @author Birdy
 * @date 2025/11/21
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}