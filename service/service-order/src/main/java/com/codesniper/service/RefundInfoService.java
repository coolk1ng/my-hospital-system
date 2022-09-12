package com.codesniper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codesniper.yygh.model.order.PaymentInfo;
import com.codesniper.yygh.model.order.RefundInfo;

/**
 * 退款
 *
 * @author CodeSniper
 * @since 2022/8/2 00:00
 */
public interface RefundInfoService extends IService<RefundInfo> {

    /**
     * 保存退款信息
     * @param paymentInfo
     * @return RefundInfo
     */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);

}
