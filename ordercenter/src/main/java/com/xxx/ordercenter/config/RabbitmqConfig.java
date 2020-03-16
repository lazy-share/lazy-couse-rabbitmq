package com.xxx.ordercenter.config;

import com.alibaba.fastjson.JSON;
import com.xxx.ordercenter.callback.SendIntegralCallback;
import com.xxx.ordercenter.entity.TMsgLogEntity;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/15.
 */
@Configuration
public class RabbitmqConfig {

    public static final String integral_dxl_exchange = "integral_dxl_exchange";
    public static final String integral_dxl_queue = "integral_dxl_queue";
    public static final String integral_dxl_binding_key = "integral_dxl_binding_key";
    public static final String integral_exchange = "integral_exchange";
    public static final String integral_queue = "integral_queue";
    public static final String integral_binding_key = "integral_binding_key";

    @Autowired
    private SendIntegralCallback sendIntegralCallback;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        /**
         * rabbitTemplate 是单例的，所以这里的配置基本是全局的
         *
         * 为rabbitTemplate注册全局发送确认异步监听器
         * @param correlationData 回调的相关数据
         * @param ack ack为真，nack为假
         * @param cause 一个可选的原因，对于nack才可能有值，否则为null。
         */
        rabbitTemplate.setConfirmCallback((CorrelationData correlationData, boolean ack, String cause) -> {

            //todo 请注意，rabbitTemplate是单例的，所以建议一个应用公用一张消息表，通过bizType区分业务操作
            try {
                sendIntegralCallback.confirm(correlationData, ack, cause);
            } catch (Exception e) {
                //忽略确认过程的异常，下次定时任务会重发消息，超过次数便发邮件，且进入死信
                e.printStackTrace();
            }
        });


        rabbitTemplate.setEncoding("UTF-8");
        rabbitTemplate.setMandatory(true);
        //rabbitTemplate.convertAndSend(...) 接口会使用这里配置的消息转换
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter() {
            @Override
            protected Message createMessage(Object objectToConvert, MessageProperties messageProperties) throws MessageConversionException {

                //todo 这里可以自定义全局配置一些属性
                return super.createMessage(objectToConvert, messageProperties);
            }
        });

        /**
         * rabbitTemplate.setMandatory(true);
         *
         * 当mandatory标志位设置为true时，如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息，
         * 那么broker会调用basic.return方法将消息返还给生产者;
         * 当mandatory设置为false时，出现上述情况broker会直接将消息丢弃
         */
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {

            try {
                //todo 请注意，rabbitTemplate是单例的，所以建议一个应用公用一张消息表，通过bizType区分业务操作
                sendIntegralCallback.returnedMessage(message, replyCode, replyText, exchange, routingKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //在发布消息之前被调用，从消息对象抽取数据配置到关联对象中
        rabbitTemplate.setCorrelationDataPostProcessor((message, correlationData) -> {
            try {
                if (StringUtils.isEmpty(correlationData.getId())) {
                    TMsgLogEntity msgLogEntity = JSON.parseObject(
                            JSON.toJSONString(new String(message.getBody(), "UTF-8")), TMsgLogEntity.class);
                    correlationData.setId(String.valueOf(msgLogEntity.getId()));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return correlationData;
        });

    }

    //配置队列
    @Bean
    public Queue integralQueue() {

        Map<String, Object> args = new HashMap<>();
        //绑定名称为：integral_dxl_exchange 死信交换器
        args.put("x-dead-letter-exchange", integral_dxl_exchange);
        //路由到死信交换器路由键 route key
        args.put("x-dead-letter-routing-key", integral_dxl_binding_key);

        //arg1: 名称为积分队列
        //arg2: true 表示持久类型队列
        //arg3: 非排他队列
        //arg4: 非自动删除
        //arg5: 队列属性参数
        return new Queue(integral_queue, true, false, false, args);
    }

    //配置Direct类型交换器，绑定键完全匹配类型
    @Bean
    public DirectExchange integralExchange() {
        //arg1: 交换器名称
        //arg2: 持久类型
        //arg3: 取消最后绑定对象不自动删除
        return new DirectExchange(integral_exchange, true, false);
    }

    //将队列和交换器绑定
    @Bean
    public Binding bindingIntegralExchange() {
        //通过绑定键为：integral将队列和交换器绑定
        return BindingBuilder.bind(integralQueue()).to(integralExchange()).with(integral_binding_key);
    }

    //配置死信队列
    @Bean
    public Queue integralDxlQueue() {
        //arg1: 名称为积分死信队列
        //arg2: true 表示持久类型队列
        //arg3: 非排他队列
        //arg4: 非自动删除
        return new Queue(integral_dxl_queue, true, false, false);
    }

    //配置Direct类型死信交换器，绑定键完全匹配类型
    @Bean
    public DirectExchange integralDxlExchange() {
        //arg1: 交换器名称
        //arg2: 持久类型
        //arg3: 取消最后绑定对象不自动删除
        return new DirectExchange(integral_dxl_exchange, true, false);
    }

    //将队列和死信交换器绑定
    @Bean
    public Binding bindingIntegralDxlExchange() {
        //通过绑定键为：integral将队列和交换器绑定
        return BindingBuilder.bind(integralDxlQueue()).to(integralDxlExchange()).with(integral_dxl_binding_key);
    }
}
