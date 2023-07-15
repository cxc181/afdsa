package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.tax.service.ParkTaxBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 解析完税凭证
 */
@Component
@Slf4j
public class UploadVouchersReceiver {

    @Autowired
    ParkTaxBillService parkTaxBillService;


    @RabbitHandler
    @RabbitListener(queues = "uploadVouchers", priority="100")
    public void process(JSONObject json) {
        log.info("==============================解析完税凭证开始==========================================");
        log.info("收到解析税单数据，请求参数：{}", JSONObject.toJSONString(json));
        try {
            parkTaxBillService.uploadVouchersMq(json);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        log.info("==============================解析完税凭证结束==========================================");

        return ;
    }



}
