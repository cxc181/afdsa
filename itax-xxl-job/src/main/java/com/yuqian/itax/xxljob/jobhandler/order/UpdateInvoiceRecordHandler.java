package com.yuqian.itax.xxljob.jobhandler.order;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.order.entity.InvoiceRecordDetailEntity;
import com.yuqian.itax.order.enums.InvoiceRecordStatusEnum;
import com.yuqian.itax.order.service.InvoiceRecordDetailService;
import com.yuqian.itax.order.service.InvoiceRecordService;
import com.yuqian.itax.util.util.StringUtil;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * 定时更新开票记录状态 3分钟执行一次
 * @ClassName UpdateInvoiceRecordHandler
 * @Description TODO
 * @Author jiangni
 * @Date 2021/1/19
 * @Version 1.0
 */
@JobHandler(value = "updateInvoiceRecordHandler")
@Component
@Logger
public class UpdateInvoiceRecordHandler extends IJobHandler {

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("=========定时更新开票记录状态启动");
        String invoiceRecordNo="";
        if(StringUtils.isNotBlank(param)){
            JSONObject jsonObject = JSONObject.parseObject(param);
            invoiceRecordNo = jsonObject.getString("invoiceRecordNo");
        }
        invoiceRecordService.updateInvoiceRecordByIng(invoiceRecordNo,"xxl-job");
        XxlJobLogger.log("=========定时更新开票记录状态结束");
        return SUCCESS;
    }
}
