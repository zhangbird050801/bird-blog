package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类统计视图
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatVO {
    private String name;
    private Long value;
}
