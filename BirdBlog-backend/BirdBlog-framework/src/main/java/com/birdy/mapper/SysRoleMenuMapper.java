package com.birdy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.birdy.domain.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联Mapper接口
 *
 * @author Birdy
 * @date 2025/11/21
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 物理删除角色的所有菜单权限
     * @param roleId 角色ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    int physicalDeleteByRoleId(Long roleId);
}