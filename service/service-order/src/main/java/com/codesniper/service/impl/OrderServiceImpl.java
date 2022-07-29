package com.codesniper.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codesniper.client.HospFeignClient;
import com.codesniper.client.PatientFeignClient;
import com.codesniper.common.exception.MyException;
import com.codesniper.common.helper.HttpRequestHelper;
import com.codesniper.common.result.ResultCodeEnum;
import com.codesniper.constant.MqConstant;
import com.codesniper.mapper.OrderMapper;
import com.codesniper.service.OrderService;
import com.codesniper.service.RabbitService;
import com.codesniper.yygh.enums.OrderStatusEnum;
import com.codesniper.yygh.model.order.OrderInfo;
import com.codesniper.yygh.model.user.Patient;
import com.codesniper.yygh.vo.feign.FeignVo;
import com.codesniper.yygh.vo.hosp.ScheduleOrderVo;
import com.codesniper.yygh.vo.order.OrderMqVo;
import com.codesniper.yygh.vo.order.OrderQueryVo;
import com.codesniper.yygh.vo.order.SignInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author CodeSniper
 * @since 2022/7/18 00:30
 */
@Service("OrderService")
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private HospFeignClient hospFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Override
    @Transactional
    public Long saveOrder(OrderInfo orderInfo) {
        FeignVo feignVo = new FeignVo();
        feignVo.setId(orderInfo.getPatientId());
        // 获取就诊人信息
        Patient patient = patientFeignClient.getPatient(feignVo);

        // 获取排班信息
        ScheduleOrderVo scheduleOrder = hospFeignClient.getScheduleOrder(orderInfo.getScheduleId());

        // 判断当前时间是否可以预约
        /*if (new DateTime(scheduleOrder.getStartTime()).isAfterNow()) {
            throw new MyException(ResultCodeEnum.TIME_NO);
        }*/

        // 获取签名信息
        SignInfoVo signInfo = hospFeignClient.getSignInfo(scheduleOrder.getHoscode());
        OrderInfo result = new OrderInfo();
        BeanUtils.copyProperties(scheduleOrder, result);

        // 设置其他返回值
        String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
        // 订单交易号
        result.setOutTradeNo(outTradeNo);
        result.setScheduleId(scheduleOrder.getHosScheduleId());
        result.setUserId(patient.getUserId());
        result.setPatientId(orderInfo.getPatientId());
        result.setPatientName(patient.getName());
        result.setPatientPhone(patient.getPhone());
        result.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        baseMapper.insert(result);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", result.getHoscode());
        paramMap.put("depcode", result.getDepcode());
        paramMap.put("hosScheduleId", result.getScheduleId());
        paramMap.put("reserveDate", new DateTime(result.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", result.getReserveTime());
        paramMap.put("amount", result.getAmount());
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType", patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex", patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone", patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode", patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode", patient.getDistrictCode());
        paramMap.put("address", patient.getAddress());
        //联系人
        paramMap.put("contactsName", patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo", patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone", patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(paramMap, signInfo.getSignKey());
        paramMap.put("sign", sign);

        // 调用医院管理的接口
        JSONObject object = HttpRequestHelper.sendRequest(paramMap, signInfo.getApiUrl() + "/order/submitOrder");
        if (object.getInteger("code") == 200) {
            JSONObject data = object.getJSONObject("data");

            // 预约记录唯一标识
            String hosRecordId = data.getString("hosRecordId");
            // 预约序号
            Integer number = data.getInteger("number");
            // 取号时间
            String fetchTime = data.getString("fetchTime");
            // 取号地址
            String fetchAddress = data.getString("fetchAddress");
            // 更新订单
            result.setHosRecordId(hosRecordId);
            result.setNumber(number);
            result.setFetchTime(fetchTime);
            result.setFetchAddress(fetchAddress);
            baseMapper.updateById(result);

            // 排班可预约数
            Integer reservedNumber = data.getInteger("reservedNumber");
            // 排班剩余预约数
            Integer availableNumber = data.getInteger("availableNumber");

            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(result.getScheduleId());
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            // todo 发送mq更新号源
            rabbitService.sendMessage(MqConstant.EXCHANGE_DIRECT_ORDER, MqConstant.ROUTING_ORDER, orderMqVo);
        } else {
            throw new MyException(object.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        log.info(JSON.toJSONString("result" + result.getId()));
        return result.getId();
    }

    @Override
    public OrderInfo getOrderInfo(String orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        this.packOrderInfo(orderInfo);
        return orderInfo;
    }

    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        // 医院名称
        String keyword = orderQueryVo.getKeyword();
        // 就诊人名称
        Long patientId = orderQueryVo.getPatientId();
        // 订单状态
        String orderStatus = orderQueryVo.getOrderStatus();
        // 安排时间
        String reserveDate = orderQueryVo.getReserveDate();
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();

        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(keyword), "hosname", keyword);
        queryWrapper.eq(patientId != null, "patient_id", patientId);
        queryWrapper.eq(StringUtils.isNotEmpty(orderStatus), "order_status", orderStatus);
        queryWrapper.ge(StringUtils.isNotEmpty(reserveDate), "reserve_date", reserveDate);
        queryWrapper.ge(StringUtils.isNotEmpty(createTimeBegin), "create_time", createTimeBegin);
        queryWrapper.le(StringUtils.isNotEmpty(createTimeEnd), "create_time", createTimeEnd);
        Page<OrderInfo> orderInfoList = baseMapper.selectPage(pageParam, queryWrapper);
        orderInfoList.getRecords().forEach(this::packOrderInfo);
        return orderInfoList;
    }

    /**
     * 封装订单相关信息
     *
     * @param orderInfo
     * @return void
     */
    private void packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
    }
}
