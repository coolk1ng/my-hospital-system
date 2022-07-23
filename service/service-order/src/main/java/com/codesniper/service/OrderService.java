package com.codesniper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.order.OrderInfo;

/**
 * 订单
 *
 * @author CodeSniper
 * @since 2022/7/18 00:29
 */
public interface OrderService extends IService<OrderInfo> {
    /**
     * 订单挂号
     * @param orderInfo
     * @return Object
     */
    Long saveOrder(OrderInfo orderInfo);
}
