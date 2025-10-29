package com.birdy.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Birdy
 * @date 2025/10/29 21:11
 * @description LatestArticleVO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LatestArticleVO extends HotArticleVO {
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishedTime;
}
