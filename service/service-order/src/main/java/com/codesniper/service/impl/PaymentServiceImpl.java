package com.codesniper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codesniper.client.HospFeignClient;
import com.codesniper.common.helper.HttpRequestHelper;
import com.codesniper.mapper.PaymentMapper;
import com.codesniper.service.OrderService;
import com.codesniper.service.PaymentService;
import com.codesniper.yygh.enums.OrderStatusEnum;
import com.codesniper.yygh.enums.PaymentStatusEnum;
import com.codesniper.yygh.enums.PaymentTypeEnum;
import com.codesniper.yygh.model.order.OrderInfo;
import com.codesniper.yygh.model.order.PaymentInfo;
import com.codesniper.yygh.vo.order.SignInfoVo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CodeSniper
 * @since 2022/7/29 08:49
 */
@Service("PaymentService")
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HospFeignClient hospFeignClient;

    @Override
    public void savePaymentInfo(OrderInfo orderInfo, Integer status) {
        // 是否存在相同订单
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderInfo.getId());
        queryWrapper.eq("payment_type",status);
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count <= 0) {
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setCreateTime(new Date());
            paymentInfo.setOrderId(orderInfo.getId());
            paymentInfo.setPaymentType(status);
            paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
            paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
            String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")+"|"+orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle();
            paymentInfo.setSubject(subject);
            paymentInfo.setTotalAmount(orderInfo.getAmount());
            baseMapper.insert(paymentInfo);
        }
    }

    @Override
    public void updateOrderStatus(String outTradeNo, Map<String, String> map) {
        // 获取支付记录
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no",outTradeNo);
        queryWrapper.eq("payment_type", PaymentTypeEnum.WEIXIN.name());
        PaymentInfo paymentInfo = baseMapper.selectOne(queryWrapper);

        // 更新支付信息
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(map.toString());
        paymentInfo.setTradeNo(map.get("transaction_id"));
        baseMapper.updateById(paymentInfo);

        // 更新订单信息
        OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);

        // 调用医院接口,更新订单支付信息
        SignInfoVo signInfo = hospFeignClient.getSignInfo(orderInfo.getHoscode());
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("hoscode",orderInfo.getHoscode());
        requestMap.put("hosRecordId",orderInfo.getHosRecordId());
        requestMap.put("timestamp", HttpRequestHelper.getTimestamp());

        String sign = HttpRequestHelper.getSign(requestMap, signInfo.getSignKey());
        requestMap.put("sing",sign);

        HttpRequestHelper.sendRequest(requestMap,signInfo.getApiUrl()+ "/order/updatePayStatus");
    }
}
