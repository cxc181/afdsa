package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.order.entity.InvoiceRecordDetailEntity;
import com.yuqian.itax.order.service.InvoiceRecordDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 电子发票pdf转成img
 * @ClassName InvoicePdf2ImgReceiver
 * @Description TODO
 * @Author jiangni
 * @Date 2021/1/6
 * @Version 1.0
 */
@Component
@Slf4j
public class InvoicePdf2ImgReceiver {

    @Autowired
    private InvoiceRecordDetailService invoiceRecordDetailService;

    @RabbitHandler
    @RabbitListener(queues = "invoicePdf2Img", priority="100",containerFactory = "pointTaskContainerFactory")
    public void process(JSONObject json) {
        log.info("==============================电子发票pdf转成img开始==========================================");
        log.info("收到电子发票数据，请求参数：{}", JSONObject.toJSONString(json));
        invoiceRecordDetailService.invoicePdf2Img(JSONObject.toJavaObject(json,InvoiceRecordDetailEntity.class));
        log.info("==============================电子发票pdf转成img结束==========================================");
        return ;
    }
}
