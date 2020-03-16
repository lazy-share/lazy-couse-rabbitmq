package com.xxx.ordercenter.job;

import com.xxx.ordercenter.callback.SendIntegralCallback;
import com.xxx.ordercenter.entity.TMsgLogEntity;
import com.xxx.ordercenter.repository.IMsgLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 这里之间使用spring scheduling 实现，实际项目可以通过quartz 或 elastic-job
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/15.
 */
@Configuration
@EnableScheduling
public class SendIntegralJob {

    @Autowired
    private IMsgLogRepository iMsgLogRepository;

    @Autowired
    private SendIntegralCallback sendIntegralCallback;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    //每天23：59分定时执行
//    @Scheduled(cron = "0 59 23 * * ?")
    //测试间隔30s执行一次
    @Scheduled(cron = "0/30 * * * * ?")
    public void process() {
        log.info("开始执行发送积分消息");

        //这里一次性查出全部 状态为待发送 、 重试次数小于等于3次的消息
        //实际业务这里应该做个分页查询，避免一次读取大量数据放在内存
        List<TMsgLogEntity> rows = iMsgLogRepository.findAll((Specification<TMsgLogEntity>) (root, query, cb) -> {
            List<Predicate> predicate = new ArrayList<>();
            //查询消息状态为待发送
            predicate.add(cb.equal(root.get("msgStatus"), TMsgLogEntity.UN_SEND));
            //重试次数小于等于3次的消息
            predicate.add(cb.le(root.get("retryCount"), 3));
            Predicate[] pre = new Predicate[predicate.size()];
            return query.where(predicate.toArray(pre)).getRestriction();
        });

        if (CollectionUtils.isEmpty(rows)) {
            log.info("没有需要发送的积分数据");
            return;
        }

        for (TMsgLogEntity row : rows) {
            sendIntegralCallback.doSend(row);
        }
    }

}
