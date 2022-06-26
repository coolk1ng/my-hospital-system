package com.codesniper.yygh.vo.hosp;

import com.codesniper.yygh.model.base.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author CodeSniper
 * @since 2022-05-29
 */

@Data
@ApiModel(description = "Schedule")
public class ScheduleQueryVo extends PageEntity {
	
	@ApiModelProperty(value = "医院编号")
	private String hoscode;

	@ApiModelProperty(value = "科室编号")
	private String depcode;

	@ApiModelProperty(value = "医生编号")
	private String doccode;

	@ApiModelProperty(value = "安排日期")
	private String workDate;

	@ApiModelProperty(value = "安排时间（0：上午 1：下午）")
	private Integer workTime;

}

