package com.codesniper.controller;

import com.codesniper.common.result.Result;
import com.codesniper.common.utils.AuthContextHolder;
import com.codesniper.service.UserInfoService;
import com.codesniper.yygh.model.user.UserInfo;
import com.codesniper.yygh.vo.user.LoginVo;
import com.codesniper.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
@Slf4j
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@RequestBody LoginVo loginVo) {
        return Result.ok(userInfoService.login(loginVo));
    }

    @PostMapping("/auth/userAuth")
    @ApiOperation("用户认证")
    public Result<Boolean> userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        return Result.ok(userInfoService.userAuth(AuthContextHolder.getCurrentUserId(request),userAuthVo));
    }

    @GetMapping("/auth/getUserInfo")
    @ApiOperation("获取用户信息")
    public Result<UserInfo> getUserInfo(HttpServletRequest request) {
        Long userId = AuthContextHolder.getCurrentUserId(request);
        return Result.ok(userInfoService.getById(userId));
    }
}
