package com.codesniper.yygh.vo.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CodeSniper
 * @since 2022/7/20 03:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeignVo {

    private Long id;

    private String scheduleId;

    private String hoscode;
}
