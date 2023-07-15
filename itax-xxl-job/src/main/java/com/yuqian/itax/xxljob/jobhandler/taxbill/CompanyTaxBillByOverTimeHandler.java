package com.yuqian.itax.xxljob.jobhandler.taxbill;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.user.service.MemberCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 查询超过15天未补交的企业发送短信
 * @author：hz
 * @Date：2020-12-1
 * @version：1.0
 */

@JobHandler(value="companyTaxBillByOverTimeHandler")
@Component
@Slf4j
public class CompanyTaxBillByOverTimeHandler extends IJobHandler {
    @Autowired
    CompanyTaxBillService companyTaxBillService;
    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    SmsService smsService;
    @Autowired
    MemberCompanyService memberCompanyService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long t1 = System.currentTimeMillis(); // 代码执行前时间
        XxlJobLogger.log("=========超过15天未补交的企业发送短信任务启动");
       companyTaxBillService.xxjob();

        XxlJobLogger.log("=========超过15天未补交的企业发送短信任务结束");
        return SUCCESS;
    }
}
