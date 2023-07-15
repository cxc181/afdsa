package com.yuqian.itax.mq.receiver.order;

import com.yuqian.itax.order.entity.vo.ThirdPartyPushVO;
import com.yuqian.itax.util.util.accessParty.AccessPartyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 第三方推送回调数据
 * @author：
 * @Date：2021/10/13
 * @version：1.0
 */
@Component
@Slf4j
public class ThirdPartyPushReceiver {


    @RabbitHandler
    @RabbitListener(queues = "thirdPartyPush", priority="100")
    public void process(ThirdPartyPushVO vo){

        log.info("订单推送第三方接口");
        String billsCode = vo.getOemCode();
        String secretKey = vo.getSecretKey();
        String callbackUrl = vo.getCallbackUrl();
        int num = vo.getNum();
        Map<String, Object> param = vo.getParam();

        AccessPartyUtil.gotoOrderPush(param, billsCode, secretKey, callbackUrl, num);
    }

}
