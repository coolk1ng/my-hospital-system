package com.codesniper.yygh.vo.user;

import com.codesniper.yygh.model.base.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author CodeSniper
 * @since 2022-05-29
 */

@Data
@ApiModel(description="会员搜索对象")
public class UserInfoQueryVo extends PageEntity {

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "认证状态")
    private Integer authStatus;

    @ApiModelProperty(value = "创建时间")
    private String createTimeBegin;

    @ApiModelProperty(value = "创建时间")
    private String createTimeEnd;

    private Long userId;

}
