package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
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

@Component
@Slf4j
public class RebuildPrakTaxBillReceiver {
    @Autowired
    ParkTaxBillService parkTaxBillService;
    @Autowired
    ParkTaxBillChangeService parkTaxBillChangeService;

    @RabbitHandler
    @RabbitListener(queues = "rebuildParkTaxBill", priority="100")
    public void process(JSONObject json) {
        log.info("==============================解析园区税单开始==========================================");
        log.info("收到解析税单数据，请求参数：{}", JSONObject.toJSONString(json));
        TParkTaxBillEntity tParkTaxBillEntity=parkTaxBillService.findById(json.getLong("id"));
        if(tParkTaxBillEntity==null){
            log.info("园区税单不存在");
            return;
        }
        try{
            parkTaxBillService.addTaxBillXXJOB(json.getIntValue("type"),tParkTaxBillEntity.getParkId(),json.getString("remark"),json.getString("account"));
        }catch (Exception e){
            //增加历史记录
            ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();
            BeanUtils.copyProperties(tParkTaxBillEntity,parkTaxBillChangeEntity);
            parkTaxBillChangeEntity.setId(null);
            parkTaxBillChangeEntity.setParkBillsId(tParkTaxBillEntity.getId());
            parkTaxBillChangeEntity.setRemark("重新生成失败原因："+e.getMessage());
            parkTaxBillChangeEntity.setAddTime(new Date());
            parkTaxBillChangeEntity.setAddUser(json.getString("account"));
            parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
        }
        log.info("==============================解析园区税单结束==========================================");
        return ;
    }

}
