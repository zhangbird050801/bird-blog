package com.birdy.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 角色菜单关联实体类
 *
 * @author Birdy
 * @date 2025/11/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_role_menu")
public class SysRoleMenu {

    /**
     * 角色ID（联合主键）
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 菜单ID（联合主键）
     */
    @TableField("menu_id")
    private Long menuId;

    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除：0未删除，1已删除
     */
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
}