package com.xxx.ordercenter.consumer;

import com.rabbitmq.client.Channel;
import com.xxx.common.SendEmailHelper;
import com.xxx.ordercenter.config.RabbitmqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>
 * 监听死信队列
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/15.
 */
@Component
public class IntegralDxlConsumer {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitmqConfig.integral_dxl_queue,
                    durable = "true",
                    autoDelete = "false",
                    exclusive = "false"),
            exchange = @Exchange(value = RabbitmqConfig.integral_dxl_exchange,
                    durable = "true",
                    type = "direct",
                    autoDelete = "false"
            ),
            key = RabbitmqConfig.integral_dxl_binding_key
    ))
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        if (message.getBody() != null && message.getBody().length > 0) {
            String jsonStr = new java.lang.String(message.getBody(), "UTF-8");
            log.error("接收到dxl消息： " + jsonStr);
            //先发送邮件
            SendEmailHelper.sendEmail(jsonStr);

            //todo 还可以继续写入系统异常消息表
            try {
                //再确认消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                log.info("收到dxl消息：" + jsonStr + " 正常确认");
            } catch (Exception e) {
                //spring.rabbitmq.listener.simple.prefetch=1
                //如果前面报错,则消息会留在死信队列,不再给该消费者,
                log.error("", e);
            }
        }
    }
}
