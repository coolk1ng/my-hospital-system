package com.codesniper.yygh.vo.hosp;

import com.codesniper.yygh.model.base.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author CodeSniper
 * @since 2022-05-29
 */

@Data
@ApiModel(description = "Hospital")
public class HospitalQueryVo extends PageEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "医院编号")
	private String hoscode;

	@ApiModelProperty(value = "医院名称")
	private String hosname;

	@ApiModelProperty(value = "医院类型")
	private String hostype;

	@ApiModelProperty(value = "省code")
	private String provinceCode;

	@ApiModelProperty(value = "市code")
	private String cityCode;

	@ApiModelProperty(value = "区code")
	private String districtCode;

	@ApiModelProperty(value = "状态")
	private Integer status;
}

