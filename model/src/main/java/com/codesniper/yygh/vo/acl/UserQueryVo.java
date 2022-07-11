package com.codesniper.yygh.vo.acl;

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
@ApiModel(description = "用户查询实体")
public class UserQueryVo extends PageEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "用户名")
	private String username;

	@ApiModelProperty(value = "昵称")
	private String nickName;

}

