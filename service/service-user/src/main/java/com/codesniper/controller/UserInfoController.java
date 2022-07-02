package com.codesniper.controller;

import com.codesniper.common.result.Result;
import com.codesniper.service.UserInfoService;
import com.codesniper.yygh.vo.user.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 用户信息Controller
 *
 * @author CodeSniper
 * @since 2022/7/1 23:42
 */
@RestController
@RequestMapping("/api/user")
@Api(tags = "用户信息")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@RequestBody LoginVo loginVo) {
        return Result.ok(userInfoService.login(loginVo));
    }
}
