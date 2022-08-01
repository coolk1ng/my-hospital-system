package com.codesniper.api;

import com.codesniper.common.result.Result;
import com.codesniper.service.PaymentService;
import com.codesniper.service.WeChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 微信支付
 *
 * @author CodeSniper
 * @since 2022/7/29 08:38
 */
@RestController
@Api(tags = "微信支付")
@RequestMapping("/api/order/wechat")
public class WeChatController {

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/createNative/{orderId}")
    @ApiOperation("生成支付二维码")
    public Result createNative(@PathVariable Long orderId) throws Exception {
        return Result.ok(weChatService.createNative(orderId));
    }

    @GetMapping("/getPayStatus/{orderId}")
    @ApiOperation("查询订单支付状态")
    public Result getPayStatus(@PathVariable Long orderId) {
        Map<String, String> map = weChatService.getPayStatus(orderId);
        if (map == null) {
            return Result.fail().message("支付出错");
        }
        if (StringUtils.equals("SUCCESS",map.get("trade_status"))) {
            // 更新订单状态
            String outTradeNo = map.get("out_trade_no");
            paymentService.updateOrderStatus(outTradeNo,map);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }

}
