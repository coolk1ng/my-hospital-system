package com.codesniper.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.codesniper.common.result.Result;
import com.codesniper.service.UserInfoService;
import com.codesniper.yygh.model.user.UserInfo;
import com.codesniper.yygh.vo.user.UserInfoQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户Controller
 *
 * @author CodeSniper
 * @since 2022/7/11 17:37
 */
@RestController
@Api(tags = "用户")
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/getUserList")
    @ApiOperation("查询用户列表")
    public Result<IPage<UserInfo>> getUserList(@RequestBody UserInfoQueryVo userInfoQueryVo) {
        return Result.ok(userInfoService.getUserList(userInfoQueryVo));
    }

    @PostMapping("/lockAndUnLock")
    @ApiOperation("锁定,解锁用户")
    public Result<Boolean> lockAndUnLock(@RequestBody UserInfoQueryVo userInfoQueryVo) {
        return Result.ok(userInfoService.lockAndUnLock(userInfoQueryVo));
    }

    @GetMapping("/getUserDetail/{userId}")
    @ApiOperation("查询用户详情")
    public Result<Map<String, Object>> getUserDetail(@PathVariable Long userId) {
        return Result.ok(userInfoService.getUserDetail(userId));
    }

    @PostMapping("/approval")
    @ApiOperation("用户认证审批")
    public Result<Boolean> approval(@RequestBody UserInfoQueryVo userInfoQueryVo) {
        return Result.ok(userInfoService.approval(userInfoQueryVo));
    }
}
