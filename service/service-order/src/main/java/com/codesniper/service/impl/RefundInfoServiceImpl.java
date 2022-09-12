package com.codesniper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codesniper.mapper.RefundInfoMapper;
import com.codesniper.service.RefundInfoService;
import com.codesniper.yygh.enums.RefundStatusEnum;
import com.codesniper.yygh.model.order.PaymentInfo;
import com.codesniper.yygh.model.order.RefundInfo;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 退款
 *
 * @author CodeSniper
 * @since 2022/8/2 00:00
 */
@Service("RefundService")
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", paymentInfo.getOrderId());
        queryWrapper.eq("payment_type", paymentInfo.getPaymentType());
        RefundInfo result = baseMapper.selectOne(queryWrapper);

        if (result == null) {
            // 保存交易记录
            RefundInfo refundInfo = new RefundInfo();
            refundInfo.setCreateTime(new Date());
            refundInfo.setOrderId(paymentInfo.getOrderId());
            refundInfo.setPaymentType(paymentInfo.getPaymentType());
            refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
            refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
            refundInfo.setSubject(paymentInfo.getSubject());
            //paymentInfo.setSubject("test");
            refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
            baseMapper.insert(refundInfo);
            return refundInfo;
        }
        return result;
    }
}
