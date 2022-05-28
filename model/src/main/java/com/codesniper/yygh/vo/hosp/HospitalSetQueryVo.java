package com.codesniper.yygh.vo.hosp;

import com.codesniper.yygh.model.base.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author CodeSniper
 * @since 2022-05-29
 */

@Data
public class HospitalSetQueryVo extends PageEntity {

    @ApiModelProperty(value = "医院名称")
    private String hosname;

    @ApiModelProperty(value = "医院编号")
    private String hoscode;
}
