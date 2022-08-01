package com.codesniper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.order.OrderInfo;
import com.codesniper.yygh.model.order.PaymentInfo;

import java.util.Map;

/**
 * @author CodeSniper
 * @since 2022/7/29 08:49
 */
public interface PaymentService extends IService<PaymentInfo> {

    /**
     * 添加支付信息
     * @param orderInfo
     * @param status
     * @return void
     */
    void savePaymentInfo(OrderInfo orderInfo, Integer status);

    /**
     * 更新支付信息
     * @param outTradeNo
     * @param map
     * @return void
     */
    void updateOrderStatus(String outTradeNo, Map<String, String> map);
}
