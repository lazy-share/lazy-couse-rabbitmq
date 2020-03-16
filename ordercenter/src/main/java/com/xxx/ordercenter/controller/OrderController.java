package com.xxx.ordercenter.controller;

import com.xxx.common.ResultDto;
import com.xxx.ordercenter.entity.TMsgLogEntity;
import com.xxx.ordercenter.repository.IMsgLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * <p>
 *
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/15.
 */
@RestController
@RequestMapping("/btb/orders")
public class OrderController {


    //为了简化，之间调dao层
    @Autowired
    private IMsgLogRepository iMsgLogRepository;

    /**
     * 支付通知 ,参数只是模拟
     *
     * @param orderId 订单id
     * @return
     */
    @GetMapping("/pay_notify")
    @Transactional //笔者这里为了方便直接在controller开启事务，实际业务代码是通过service层进行方法包装
    public ResultDto pay_notify(
            @RequestParam("orderId") Long orderId,
            @RequestParam("status") String status) {

        //todo 判断订单状态是否为已付款,如果是则直接返回不处理,认为是重复回调

        //更新订单状态成功后,记录积分日志
        Random r = new Random(10000);
        TMsgLogEntity msgLogEntity = new TMsgLogEntity()
                //orderId模拟假数据
                .setBizId(orderId == null ? (long) r.nextInt(100000) : orderId)
                .setBizVal("100")
                //userId模拟假数据
                .setBizUserId((long) r.nextInt(100000))
                .setCreateTime(LocalDateTime.now())
                .setLastUpdateTime(LocalDateTime.now())
                .setMsgStatus(TMsgLogEntity.UN_SEND)
                //从分布式发号器获取，这里是自增
//                        .setId(getId())
                .setRetryCount(0);
        if ("success".equals(status)) {
            //todo do update order status to success
            //增加积分
            msgLogEntity.setBizType("add_integral");
        } else {
            //todo do update order status to fail
            //扣减积分
            msgLogEntity.setBizType("reduce_integral");
        }
        iMsgLogRepository.saveAndFlush(msgLogEntity);

        return ResultDto.success();
    }
}
