package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.order.entity.InvoiceRecordDetailEntity;
import com.yuqian.itax.order.service.InvoiceRecordDetailService;
import com.yuqian.itax.order.service.InvoiceRecordService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.util.util.EmailUtils;
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
public class BwInvoiceIssueReceiver {

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    @Autowired
    private InvoiceRecordDetailService invoiceRecordDetailService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private OemParamsService oemParamsService;

    @RabbitHandler
    @RabbitListener(queues = "bwInvoiceIssue", priority="100")
    public void process(JSONObject json) {
        Map<String,Object> orderMap = json.getObject("orderMap",Map.class);
        Map<String,String> oemParams = json.getObject("oemParams",Map.class);
        String requestNo = json.getString("requestNo");
        String updateUser = json.getString("updateUser");
        String oemCode = json.getString("oemCode");
        log.info("请求号["+requestNo+"],调用百旺开票接口开始");
        //重复次数
        DictionaryEntity dictionaryEntity = dictionaryService.getByCode("bw_invoice_issue_num");
        int num = 1;
        if(dictionaryEntity != null && StringUtils.isNotBlank(dictionaryEntity.getDictValue())){
            num = Integer.parseInt(dictionaryEntity.getDictValue());
        }
        while(num >0){
            try {
                bwInvoiceIssue(orderMap,oemParams,requestNo,updateUser,oemCode);
                break;
            }catch (Exception ex){
                try {
                    Thread.sleep(5*1000L);//睡眠5秒
                }catch (Exception e){}
                num-- ;
                if(num <= 0){
                    try {
                        sendEmail(oemCode, requestNo);
                    }catch (Exception e){
                        log.error("开票邮箱发送失败,请求号："+requestNo+"，错误信息："+e.getMessage());
                    }
                }
                break;
            }
        }
        log.info("请求号["+requestNo+"],调用百旺开票接口结束");
    }

    private void bwInvoiceIssue(Map<String,Object> orderMap,Map<String,String> oemParams,String requestNo,String updateUser,String oemCode){
        String message = invoiceRecordService.bwInvoiceIssue(orderMap, oemParams, requestNo, updateUser);
        if(StringUtils.isNotBlank(message)){
            //更新开票明细
            InvoiceRecordDetailEntity entity = new InvoiceRecordDetailEntity();
            entity.setRequestNo(requestNo);
            List<InvoiceRecordDetailEntity> list = invoiceRecordDetailService.select(entity);
            if(list != null && list.size() == 1){
                entity = list.get(0);
                entity.setUpdateUser(updateUser);
                entity.setUpdateTime(new Date());
                entity.setDetailDesc(message);
                entity.setStatus(3);
                invoiceRecordDetailService.editByIdSelective(entity);
            }else{
                try {
                    sendEmail(oemCode, requestNo);
                }catch (Exception e){
                    log.error("开票邮箱发送失败",e.getMessage());
                }
            }
        }
    }
    /**
     * 开票失败发送邮件
     * @param oemCode
     * @param requestNo
     */
    private void sendEmail(String oemCode,String requestNo){
        DictionaryEntity dictionaryEntity = dictionaryService.getByCode("emergency_contact_email");
        if(dictionaryEntity!=null){
            String emails = dictionaryEntity.getDictValue();
            if(StringUtils.isBlank(emails)){
                return ;
            }
            Map<String,String> params = getEmailParams(oemCode);
            String[] arrs = emails.split(",");
            for (String arr : arrs) {
                try {
                    EmailUtils.send(params, "", "电子发票开票失败", "开票记录明细更新失败，原因：未找到或已找到多条开票记录明细[requestNo:"+requestNo+"]", arr, null);
                }catch (Exception e){
                    log.error("开票记录明细更新失败，原因：未找到或已找到多条开票记录明细[requestNo:"+requestNo+"]");
                }
            }
        }
    }
    /**
     * 获取邮箱发送参数配置
     * @param oemCode
     * @return
     */
    private Map<String,String> getEmailParams(String oemCode){
        OemParamsEntity oemParamsEntity = oemParamsService.getParams(oemCode,OemParamsTypeEnum.EMAIL_CONFIG.getValue());
        Map<String,String> params = new HashMap<>();
        params.put("account",oemParamsEntity.getAccount());
        params.put("password",oemParamsEntity.getSecKey());
        params.put("emailHost",oemParamsEntity.getUrl());
        JSONObject jsonObject = JSONObject.parseObject(oemParamsEntity.getParamsValues());
        params.put("port",jsonObject.getString("port"));
        return params;
    }
}
