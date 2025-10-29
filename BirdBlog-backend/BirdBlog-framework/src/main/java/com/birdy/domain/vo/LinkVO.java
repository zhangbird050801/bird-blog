package com.birdy.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkVO {
    /**
     * 友链ID
     */
    private Long id;

    /**
     * 网站名称
     */
    private String name;

    /**
     * LogoURL
     */
    private String logo;

    /**
     * 描述
     */
    private String description;

    /**
     * 网站地址
     */
    private String url;


}
