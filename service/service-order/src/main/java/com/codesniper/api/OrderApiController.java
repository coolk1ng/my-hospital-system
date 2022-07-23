package com.codesniper.api;

import com.alibaba.fastjson.JSON;
import com.codesniper.common.result.Result;
import com.codesniper.service.OrderService;
import com.codesniper.yygh.model.order.OrderInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CodeSniper
 * @since 2022/7/18 00:30
 */
@RestController
@RequestMapping("/api/order")
@Api(tags = "订单")
@Slf4j
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/auth/saveOrder")
    @ApiOperation("挂号订单")
    public Result<Long> saveOrder(@RequestBody OrderInfo orderInfo) {
        log.info(JSON.toJSONString(orderInfo));
        return Result.ok(orderService.saveOrder(orderInfo));
    }
}
