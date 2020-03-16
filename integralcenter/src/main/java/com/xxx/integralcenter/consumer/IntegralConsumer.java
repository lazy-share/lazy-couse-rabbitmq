package com.xxx.integralcenter.consumer;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.xxx.integralcenter.config.RabbitmqConfig;
import com.xxx.integralcenter.dto.MsgLogDto;
import com.xxx.integralcenter.entity.TMsgConfirmLogEntity;
import com.xxx.integralcenter.repository.IMsgConfirmLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * <p>
 * 监听死信队列
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/15.
 */
@Component
public class IntegralConsumer {

    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private IMsgConfirmLogRepository iMsgConfirmLogRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitmqConfig.integral_queue,
                    durable = "true",
                    autoDelete = "false",
                    exclusive = "false",
                    arguments = {
                            @Argument(
                                    name = "x-dead-letter-exchange",
                                    value = RabbitmqConfig.integral_dxl_exchange
                            ),
                            @Argument(
                                    name = "x-dead-letter-routing-key",
                                    value = RabbitmqConfig.integral_dxl_binding_key
                            )
                    }),
            exchange = @Exchange(value = RabbitmqConfig.integral_exchange,
                    durable = "true",
                    type = "direct",
                    autoDelete = "false"
            ),
            key = RabbitmqConfig.integral_binding_key
    )
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) {
        try {
            if (message.getBody() == null || message.getBody().length < 1) {
                log.error("接收到没有内容的消息，已被拒绝,路由到死信队列，如果有配置的话，否则丢弃");
                //arg1: 交货标签
                //arg2: 是否拒绝小于等于message.getMessageProperties().getDeliveryTag()的消息，否
                //arg3: 是否重新入队 否：丢弃或路由到死信，如果有配置的话
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                return;
            }
            String content = new String(message.getBody(), "UTF-8");

            MsgLogDto msgLogDto = JSONObject.parseObject(content, MsgLogDto.class);
            if (StringUtils.isEmpty(msgLogDto.getAppId())) {
                log.error("接收到没有appId的消息，已被拒绝,路由到死信队列，如果有配置的话，否则丢弃");
                //arg1: 交货标签
                //arg2: 是否拒绝小于等于message.getMessageProperties().getDeliveryTag()的消息，否
                //arg3: 是否重新入队 否：丢弃或路由到死信，如果有配置的话
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                return;
            }
            if (msgLogDto.getId() == null) {
                log.error("接收到没有id的消息，已被拒绝,路由到死信队列，如果有配置的话，否则丢弃");
                //arg1: 交货标签
                //arg2: 是否拒绝小于等于message.getMessageProperties().getDeliveryTag()的消息，否：只拒绝当条消息
                //arg3: 是否重新入队 否：丢弃或路由到死信，如果有配置的话
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                return;
            }

            TMsgConfirmLogEntity confirmLogEntity = iMsgConfirmLogRepository.findByMsgIdAndAndAppId(msgLogDto.getId().toString(), msgLogDto.getAppId());

            //幂等性处理
            if (confirmLogEntity != null) {
                log.error("该消息已经被处理，已被直接确认");
                //arg1: 交货标签
                //arg2: 是否确认小于等于message.getMessageProperties().getDeliveryTag()的消息，否：只确认当条消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            //todo 这里处理增加或减少积分的业务操作
            this.doChangeIntegral(msgLogDto);

            //保存已经处理过的消息数据，防止重复消费
            confirmLogEntity = new TMsgConfirmLogEntity()
                    .setAppId(msgLogDto.getAppId())
                    .setMsgId(String.valueOf(msgLogDto.getId()))
                    .setCreateTime(LocalDateTime.now())
                    .setLastUpdateTime(LocalDateTime.now());
            iMsgConfirmLogRepository.saveAndFlush(confirmLogEntity);

            //这里测试死信队列路由，生产不允许有这块代码
            if (msgLogDto.getBizId() > 1000) {
                throw new RuntimeException("测试死信队列路由");
            }

            //最后确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("收到消息：" + content + " 正常确认");
        } catch (Exception e) {
            log.error("", e);
            //已经拒绝消息,发送给死信队列，如果有配置的话，否则丢弃
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException e1) {
                log.error("发送异常后拒绝消息异常", e1);
            }
        }
    }

    private void doChangeIntegral(MsgLogDto msgLogDto) {

        //增加积分
        if ("add_integral".equals(msgLogDto.getBizType())) {

            System.out.println("增加积分: " + msgLogDto.getBizVal());
            //扣减积分
        } else if ("reduce_integral".equals(msgLogDto.getBizType())) {

            System.out.println("扣减积分: " + msgLogDto.getBizVal());
        } else {
            //throw not support exception
        }
    }

}
