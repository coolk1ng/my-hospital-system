package com.codesniper.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codesniper.service.OrderService;
import com.codesniper.service.PaymentService;
import com.codesniper.service.RefundInfoService;
import com.codesniper.service.WeChatService;
import com.codesniper.utils.HttpClient;
import com.codesniper.yygh.enums.PaymentTypeEnum;
import com.codesniper.yygh.enums.RefundStatusEnum;
import com.codesniper.yygh.model.order.OrderInfo;
import com.codesniper.yygh.model.order.PaymentInfo;
import com.codesniper.yygh.model.order.RefundInfo;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 微信支付
 *
 * @author CodeSniper
 * @since 2022/7/29 08:40
 */
@Service("WeChatService")
@Slf4j
public class WeChatServiceImpl implements WeChatService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.partnerKey}")
    private String partnerKey;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RefundInfoService refundInfoService;

    @Override
    public Map<String, Object> createNative(Long orderId) {
        // 获取订单信息
        OrderInfo orderInfo = orderService.getById(orderId);
        // 支付记录表中添加信息
        paymentService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());

        HashMap<String, String> paramMap = new HashMap<>();
        HashMap<String, Object> map = new HashMap<>();
        try {
            // redis获取数据
            String s = redisTemplate.opsForValue().get(orderId.toString());
            Map<String, Object> jsonObject = JSON.parseObject(s);
            log.info(JSON.toJSONString(jsonObject));
            if (jsonObject != null) {
                return jsonObject;
            }
            paramMap.put("appid", appid);
            paramMap.put("mch_id", partner);
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            String body = orderInfo.getReserveDate() + "就诊" + orderInfo.getDepname();
            paramMap.put("body", body);
            paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");
            paramMap.put("spbill_create_ip", "127.0.0.1");
            paramMap.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            paramMap.put("trade_type", "NATIVE");
            //2、HTTPClient来根据URL访问第三方接口并且传递参数
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            // client设置参数
            client.setXmlParam(WXPayUtil.generateSignedXml(paramMap, partnerKey));
            client.setHttps(true);
            client.post();

            // 返回数据
            String content = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            log.info(JSON.toJSONString(resultMap));
            // 封装返回结果集
            map.put("orderId", orderId);
            map.put("totalFee", orderInfo.getAmount());
            map.put("resultCode", resultMap.get("result_code"));
            // 二维码地址
            map.put("codeUrl", resultMap.get("code_url"));
            if (resultMap.get("result_code") != null) {
                redisTemplate.opsForValue().set(orderId.toString(), String.valueOf(map), 120, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    @Override
    public Map<String, String> getPayStatus(Long orderId) {
        try {
            // 获取订单信息
            OrderInfo orderInfo = orderService.getById(orderId);
            // 封装提交参数
            HashMap<String, String> map = new HashMap<>();
            map.put("appid", appid);
            map.put("mch_id", partner);
            map.put("out_trade_no", orderInfo.getOutTradeNo());
            map.put("nonce_str", WXPayUtil.generateNonceStr());

            // 设置请求内容
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(map, partnerKey));
            client.setHttps(true);
            client.post();

            // 得到微信接口返回数据
            String content = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            log.info("支付状态:{}", JSON.toJSONString(resultMap));
            return resultMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean refund(Long orderId, Integer paymentType) {
        try {
            // 获取支付记录信息
            PaymentInfo paymentInfo = paymentService.getPaymentInfo(orderId, paymentType);
            // 添加到退款记录表
            RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
            // 判断当前订单是否已经退款
            if (refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus()) {
                return true;
            }

            // 调用微信接口退款
            Map<String, String> paramMap = new HashMap<>(8);
            paramMap.put("appid", appid);       //公众账号ID
            paramMap.put("mch_id", partner);   //商户编号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            paramMap.put("transaction_id", paymentInfo.getTradeNo()); //微信订单号
            paramMap.put("out_trade_no", paymentInfo.getOutTradeNo()); //商户订单编号
            paramMap.put("out_refund_no", "tk" + paymentInfo.getOutTradeNo()); //商户退款单号
//       paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
//       paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
            paramMap.put("total_fee", "1");
            paramMap.put("refund_fee", "1");

            String xml = WXPayUtil.generateSignedXml(paramMap, partnerKey);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
            client.setXmlParam(xml);
            client.setCert(true);
            client.setHttps(true);
            client.setCertPassword(partner);
            client.post();
            String content = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);

            if (WXPayConstants.SUCCESS.equalsIgnoreCase(map.get("result_code"))) {
                refundInfo.setCallbackTime(new Date());
                refundInfo.setTradeNo(map.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(map));
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
