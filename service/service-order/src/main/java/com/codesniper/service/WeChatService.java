package com.codesniper.service;

import java.util.Map;

/**
 * 微信支付
 *
 * @author CodeSniper
 * @since 2022/7/29 08:39
 */
public interface WeChatService {
    /**
     * 生成微信二维码
     *
     * @param orderId
     * @return Map<String, Object>
     */
    Map<String, Object> createNative(Long orderId) throws Exception;

    /**
     * 查询支付状态
     * @param orderId
     * @return Map<String,String>
     */
    Map<String, String> getPayStatus(Long orderId);

    /**
     * 退款
     * @param orderId
     * @param paymentType
     * @return Boolean
     */
    Boolean refund(Long orderId,Integer paymentType);
}
