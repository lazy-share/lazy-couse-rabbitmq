package com.xxx.ordercenter.callback;

import com.xxx.common.SendEmailHelper;
import com.xxx.ordercenter.config.RabbitmqConfig;
import com.xxx.ordercenter.entity.TMsgLogEntity;
import com.xxx.ordercenter.repository.IMsgLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

/**
 * <p>
 * 发送积分回调
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/15.
 */
@Component
public class SendIntegralCallback implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private static Logger log = LoggerFactory.getLogger(SendIntegralCallback.class);

    @Autowired
    private IMsgLogRepository iMsgLogRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //ack
        if (ack) {
            //update TMsgLogEntity t set t.msgStatus = 'SEND', t.lastUpdateTime = ?1 where t.id = ?2
            iMsgLogRepository.updateStatusById(LocalDateTime.now(), Long.valueOf(correlationData.getId()));
        } else {
            //nack
            log.error("消息id: " + correlationData.getId() + "被拒绝，原因是： " + cause);
            //重新发送
            doSend(Long.valueOf(correlationData.getId()));
        }
    }

    public void doSend(Long id) {
        if (id == null) {
            log.error("消息id: id == null ");
            return;
        }
        //这里做重发
        TMsgLogEntity row = iMsgLogRepository.findById(id).orElse(null);
        if (row == null) {
            log.error("消息id: " + id + "数据不存在,放弃重发 ");
            return;
        }
        this.doSend(row);
    }

    //这里需要将更新发送状态和发送消息绑定一个事务
    @Transactional
    public void doSend(TMsgLogEntity row) {
        if (row == null) {
            log.error("数据不存在,放弃发送");
            return;
        }
        if (TMsgLogEntity.SEND.equals(row.getMsgStatus())) {
            return;
        }

        if (row.getRetryCount() > 3) {
            String content = "消息id: " + row.getId() + " 超过最大重试次数，系统不再重新发送需要人工介入... ";
            log.error(content);
            //发送邮件
            SendEmailHelper.sendEmail(content);
            return;
        }

        //发送Rabbitmq
        rabbitTemplate.convertAndSend(
                RabbitmqConfig.integral_exchange,
                RabbitmqConfig.integral_binding_key,
                row,
                new CorrelationData(String.valueOf(row.getId())));

        //更新重试次数
        row.setRetryCount(row.getRetryCount() == null ? 1 : row.getRetryCount() + 1);
        iMsgLogRepository.saveAndFlush(row);
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        try {

            String content = "消息：" + new String(message.getBody(), "UTF-8") + " 找不到可路由的队列"
                    + " routingKey " + routingKey + " exchange " + exchange + " replyCode " + replyCode
                    + " replyText " + replyText + " 需要人工介入处理";

            log.error(content);

            //发送邮件
            SendEmailHelper.sendEmail(content);

        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
    }

}
