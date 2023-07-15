package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.tax.entity.ParkTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.TParkTaxBillEntity;
import com.yuqian.itax.tax.service.ParkTaxBillChangeService;
import com.yuqian.itax.tax.service.ParkTaxBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 园区税单立即推送
 */
@Component
@Slf4j
public class ParkSendReceiver {

    @Autowired
    ParkTaxBillService parkTaxBillService;
    @Autowired
    ParkTaxBillChangeService parkTaxBillChangeService;

    @RabbitHandler
    @RabbitListener(queues = "parkSend", priority="100")
    public void process(JSONObject json) {
        log.info("==============================园区税单推送开始==========================================");
        log.info("收到解析税单数据，请求参数：{}", JSONObject.toJSONString(json));
        Long id=json.getLong("id");
        String account=json.getString("account");
        TParkTaxBillEntity tParkTaxBillEntity=parkTaxBillService.findById(id);
        try{
            parkTaxBillService.send(id,account);
        }catch (Exception e){
            //增加历史记录
            ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();
            BeanUtils.copyProperties(tParkTaxBillEntity,parkTaxBillChangeEntity);
            parkTaxBillChangeEntity.setAddTime(new Date());
            parkTaxBillChangeEntity.setAddUser("mq异步推送");
            parkTaxBillChangeEntity.setId(null);
            parkTaxBillChangeEntity.setParkBillsId(tParkTaxBillEntity.getId());
            parkTaxBillChangeEntity.setRemark(e.getMessage());
            parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
            e.printStackTrace();
        }
        log.info("==============================解析园区推送结束==========================================");
        return ;
    }



}
