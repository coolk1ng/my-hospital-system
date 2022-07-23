
package com.codesniper.receiver;

import com.alibaba.fastjson.JSON;
import com.codesniper.common.exception.MyException;
import com.codesniper.common.result.ResultCodeEnum;
import com.codesniper.constant.MqConstant;
import com.codesniper.repository.ScheduleRepository;
import com.codesniper.service.RabbitService;
import com.codesniper.service.ScheduleService;
import com.codesniper.yygh.model.hosp.Schedule;
import com.codesniper.yygh.vo.order.OrderMqVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class HospitalReceiver {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConstant.QUEUE_ORDER, durable = "true"),
            exchange = @Exchange(value = MqConstant.EXCHANGE_DIRECT_ORDER),
            key = {MqConstant.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
        log.info(JSON.toJSONString(orderMqVo));
        //下单成功更新预约数
        Schedule schedule = scheduleRepository.getScheduleByHosScheduleId(orderMqVo.getScheduleId());
        log.info(JSON.toJSONString(schedule));
        if (schedule == null) {
            throw new MyException(ResultCodeEnum.PARAM_ERROR);
        }
        schedule.setReservedNumber(orderMqVo.getReservedNumber());
        schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
        scheduleService.update(schedule);
        //发送短信
        /*MsmVo msmVo = orderMqVo.getMsmVo();
        if(null != msmVo) {
            rabbitService.sendMessage(MqConstant.EXCHANGE_DIRECT_MSM, MqConstant.ROUTING_MSM_ITEM, msmVo);
        }*/
    }

}
