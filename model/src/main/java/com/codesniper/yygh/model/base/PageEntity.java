package com.codesniper.yygh.model.base;


import lombok.Data;

/**
 * 分页参数
 *
 * @author CodeSniper
 * @since 2022-05-29
 */
@Data
public class PageEntity {

    /**
     * 页数
     */
    private Integer pageNum;

    /**
     * 数量
     */
    private Integer pageSize;
}
