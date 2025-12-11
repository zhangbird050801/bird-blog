package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签使用统计
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagStatVO {
    private Long tagId;
    private String name;
    private Long value;
}
