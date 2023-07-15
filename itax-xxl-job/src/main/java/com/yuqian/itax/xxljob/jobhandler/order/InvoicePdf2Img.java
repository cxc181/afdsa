package com.yuqian.itax.xxljob.jobhandler.order;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.order.entity.InvoiceRecordDetailEntity;
import com.yuqian.itax.order.service.InvoiceRecordDetailService;
import com.yuqian.itax.util.util.StringUtil;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 电子发票pdf转成图片 每天中午12点同步一次
 * @ClassName InvoicePdf2Img
 * @Description TODO
 * @Author jiangni
 * @Date 2021/1/6
 * @Version 1.0
 */
@JobHandler(value = "invoicePdf2Img")
@Component
@Logger
public class InvoicePdf2Img extends IJobHandler {
    @Autowired
    private InvoiceRecordDetailService detailService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("=========电子发票pdf转成图片启动");
        String invoiceRecordNo="";
        if(StringUtils.isNotBlank(param)){
            JSONObject jsonObject = JSONObject.parseObject(param);
            invoiceRecordNo = jsonObject.getString("invoiceRecordNo");
        }
        Example example = new Example(InvoiceRecordDetailEntity.class);
        if(StringUtil.isNotBlank(invoiceRecordNo)){
            example.createCriteria().andEqualTo("invoiceRecordNo",invoiceRecordNo);
        }else {
            example.createCriteria().andIsNull("eInvoiceOssImgUrl").orIsNull("eInvoiceOssPdfUrl");
        }
        List<InvoiceRecordDetailEntity> list = detailService.selectByExample(example);
        if(list!=null && list.size()>0){
            list.forEach(vo-> detailService.invoicePdf2Img(vo));
        }
        XxlJobLogger.log("=========电子发票pdf转成图片结束");
        return SUCCESS;
    }
}
