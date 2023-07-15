package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.order.entity.InvoiceRecordDetailEntity;
import com.yuqian.itax.order.entity.InvoiceRecordEntity;
import com.yuqian.itax.order.service.InvoiceRecordDetailService;
import com.yuqian.itax.order.service.InvoiceRecordService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.util.util.EmailUtils;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 百旺开票
 * @ClassName BWInvoiceIssueReceiver
 * @Description TODO
 * @Author jiangni
 * @Date 2021/1/18
 * @Version 1.0
 */
@Component
@Slf4j
public class OpenInvoicingReceiver {

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    @RabbitHandler
    @RabbitListener(queues = "openInvoicing", priority="100")
    public void process(JSONObject json) {
        String invoiceRecordNo = json.getString("invoiceRecordNo");
        String orderNo = json.getString("orderNo");
        String updateUser = json.getString("updateUser");
        String isForce = json.getString("isForce");
        log.info("开票记录["+invoiceRecordNo+"]立即出票");
        //查询是否已经生存开票记录
        InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
        invoiceRecordEntity.setInvoiceRecordNo(invoiceRecordNo);
        invoiceRecordEntity.setOrderNo(orderNo);
        invoiceRecordEntity = invoiceRecordService.selectOne(invoiceRecordEntity);
        if(invoiceRecordEntity == null) {
            try {
                //为防止创建开票记录的事务未提及完成,睡眠5s
                Thread.sleep(5000L);
            } catch (Exception e) {}
        }

        String messsage = null;
        try {
            messsage = invoiceRecordService.openInvoice(invoiceRecordNo, orderNo, updateUser, isForce);
            if ("库存不足".equals(messsage) || "查询库存失败".equals(messsage)) {
                return ;
            }
        }catch (Exception e){
            log.error("开票记录["+invoiceRecordNo+"]出票失败："+e.getMessage());
            messsage = e.getMessage();
        }
        if(StringUtil.isNotBlank(messsage)){
            invoiceRecordEntity = new InvoiceRecordEntity();
            invoiceRecordEntity.setInvoiceRecordNo(invoiceRecordNo);
            invoiceRecordEntity.setOrderNo(orderNo);
            invoiceRecordEntity = invoiceRecordService.selectOne(invoiceRecordEntity);
            if(invoiceRecordEntity != null){
               invoiceRecordEntity.setInvoiceDesc(messsage);
               invoiceRecordEntity.setUpdateTime(new Date());
               invoiceRecordEntity.setUpdateUser(updateUser);
               invoiceRecordService.editByIdSelective(invoiceRecordEntity);
               invoiceRecordService.addInvoiceRecordChange(invoiceRecordEntity, invoiceRecordEntity.getStatus(), invoiceRecordEntity.getInvoiceDesc(), updateUser);
            }
        }
        log.info("开票记录["+invoiceRecordNo+"]立即出票结束");
    }
}
