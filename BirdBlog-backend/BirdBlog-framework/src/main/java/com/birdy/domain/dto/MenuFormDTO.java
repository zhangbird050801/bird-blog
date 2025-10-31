package com.birdy.domain.dto;

import lombok.Data;

/**
 * 菜单表单 DTO
 *
 * @author Birdy
 * @date 2025/10/31
 * @description 菜单新增/编辑表单对象
 */
@Data
public class MenuFormDTO {

    /**
     * 菜单ID（编辑时需要）
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单类型：M目录 C菜单 F按钮
     */
    private String type;

    /**
     * 显示：0显示 1隐藏
     */
    private Integer visible;

    /**
     * 状态：0正常 1停用
     */
    private Integer status;

    /**
     * 权限标识
     */
    private String perm;

    /**
     * 图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;
}
