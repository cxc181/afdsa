package com.yuqian.itax.mq.receiver;

import com.yuqian.itax.agent.service.OemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName TestReceiver
 * @Description TODO
 * @Author jiangni
 * @Date 2020/3/4
 * @Version 1.0
 */
@Component("testReceiver")
@Slf4j
public class TestReceiver {

    @Autowired
    private OemService oemService;
    /**
     * 清除多余pos信息
     */
    @RabbitHandler
    @RabbitListener(queues = "test", priority="100")
    public void clear(String oemCode) {
        log.info("测试itax-mq开始");
        oemService.getOem("oemCode");
        log.info("测试itax-mq结束");
    }
}
