package com.birdy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.birdy.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单权限 Mapper 接口
 *
 * @author Birdy
 * @date 2025/10/31
 * @description 系统菜单权限数据访问层
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询所有正常状态的菜单（按排序字段排序）
     *
     * @return 菜单列表
     */
    List<SysMenu> selectAllMenus();
}
