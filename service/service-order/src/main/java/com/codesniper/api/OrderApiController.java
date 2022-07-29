package com.codesniper.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codesniper.common.result.Result;
import com.codesniper.service.OrderService;
import com.codesniper.yygh.enums.OrderStatusEnum;
import com.codesniper.yygh.model.order.OrderInfo;
import com.codesniper.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result<String> saveOrder(@RequestBody OrderInfo orderInfo) {
        log.info(JSON.toJSONString(orderInfo));
        Long orderId = orderService.saveOrder(orderInfo);
        return Result.ok(Long.toString(orderId));
    }

    @ApiOperation("查询订单详情")
    @GetMapping("/auth/getOrderInfo/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable String orderId) {
        log.info(orderId);
        return Result.ok(orderService.getOrderInfo(orderId));
    }

    @ApiOperation(value = "获取分页列表")
    @GetMapping("auth/getOrderInfoList/{page}/{limit}")
    public Result<IPage<OrderInfo>> getOrderInfoList(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "orderCountQueryVo", value = "查询对象", required = false) OrderQueryVo orderQueryVo) {
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("/auth/getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }
}
