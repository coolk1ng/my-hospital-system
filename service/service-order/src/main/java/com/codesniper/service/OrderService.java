package com.codesniper.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.order.OrderInfo;
import com.codesniper.yygh.vo.order.OrderQueryVo;

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

    /**
     * 查询订单详情
     * @param orderId
     * @return OrderInfo
     */
    OrderInfo getOrderInfo(String orderId);

    /**
     * 查询订单列表
     * @param pageParam
     * @param orderQueryVo
     * @return IPage<OrderInfo>
     */
    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);
}
