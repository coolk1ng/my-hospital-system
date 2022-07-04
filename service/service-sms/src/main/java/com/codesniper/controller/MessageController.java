package com.codesniper.controller;

import com.codesniper.common.result.Result;
import com.codesniper.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 短信Controller
 *
 * @author CodeSniper
 * @since 2022/7/3 01:29
 */
@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信验证码")
public class MessageController {

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("/send/{phone}")
    @ApiOperation("发送验证码")
    public Result<String> sendCode(@PathVariable String phone) throws Exception {
        // 从redis获取验证码
        String code = redisTemplate.opsForValue().get("phone");

        if (StringUtils.isNotEmpty(code)) {
            return Result.ok();
        }

        // redis中不存在,生成验证码发送短信,并存入redis
        code = Math.round((Math.random() + 1) * 1000) + "";

        // 发送短信
        Boolean isSend = messageService.sendMessage(phone, code, accessKeyId, accessKeySecret);
        if (isSend) {
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            return Result.ok("发送短信成功");
        }else {
            return Result.fail("发送短信失败");
        }
    }
}
