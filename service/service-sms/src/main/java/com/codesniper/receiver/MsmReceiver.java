/*
package com.codesniper.receiver;

import com.codesniper.constant.MqConstant;
import com.codesniper.service.MessageService;
import com.codesniper.yygh.vo.msm.MsmVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

*/
/**
 * @author CodeSniper
 * @since 2022/7/18 22:57
 *//*

@Component
public class MsmReceiver {
    @Autowired

    private MessageService messageService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConstant.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConstant.EXCHANGE_DIRECT_MSM),
            key = {MqConstant.ROUTING_MSM_ITEM}
    ))
    public void send(MsmVo msmVo, Message message, Channel channel) throws Exception {
        messageService.sendMessage(msmVo);
    }

}
*/
