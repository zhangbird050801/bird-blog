package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签VO - 用于前端展示
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagVO {
    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 文章数量
     */
    private Integer articleCount;
}

