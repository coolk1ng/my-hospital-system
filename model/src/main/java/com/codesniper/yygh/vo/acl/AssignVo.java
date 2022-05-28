package com.codesniper.yygh.vo.acl;

import lombok.Data;

/**
 * @author CodeSniper
 * @since 2022-05-29
 */

@Data
public class AssignVo {

    private Long roleId;

    private Long[] permissionId;
}
