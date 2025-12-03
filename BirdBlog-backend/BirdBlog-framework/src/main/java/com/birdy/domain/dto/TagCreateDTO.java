package com.birdy.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签创建 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagCreateDTO {
    private String name;
    private String remark;
}
