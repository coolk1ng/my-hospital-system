package com.codesniper.yygh.vo.acl;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author CodeSniper
 * @since 2022-05-29
 */

@Data
@ApiModel(description = "角色查询实体")
public class RoleQueryVo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "角色名称")
	private String roleName;

}

