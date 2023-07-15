package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.tax.service.ParkTaxBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 解析园区税单
 */
@Component
@Slf4j
public class PrakTaxBillReceiver {

    @Autowired
    ParkTaxBillService parkTaxBillService;


    @RabbitHandler
    @RabbitListener(queues = "parkTaxBill", priority="100")
    public void process(JSONObject json) {
        log.info("==============================解析园区税单开始==========================================");
        log.info("收到解析税单数据，请求参数：{}", JSONObject.toJSONString(json));
        parkTaxBillService.mq(json);
        log.info("==============================解析园区税单结束==========================================");
        return ;
    }



}
