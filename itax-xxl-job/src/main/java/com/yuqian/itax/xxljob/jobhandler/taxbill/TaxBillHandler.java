package com.yuqian.itax.xxljob.jobhandler.taxbill;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.tax.service.ParkTaxBillService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 每个季度第一个月的5号为每个园区生成一个园区总税单和为企业生成企业税单
 * @author：hz
 * @Date：2020-12-1
 * @version：1.0
 */
@JobHandler(value="taxBillHandler")
@Component
public class TaxBillHandler extends IJobHandler {

    @Autowired
    CompanyTaxBillService companyTaxBillService;
    @Autowired
    ParkTaxBillService parkTaxBillService;
    @Autowired
    TaxPolicyService taxPolicyService;
    @Autowired
    MemberCompanyService memberCompanyService;
    @Autowired
    InvoiceOrderService invoiceOrderService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        int type=0;
        if(StringUtil.isNotBlank(param)){
            JSONObject jsonObject=JSONObject.parseObject(param);
            type=jsonObject.getIntValue("type");
        }
        parkTaxBillService.addTaxBillXXJOB(type,null,"季度初始生成税单","XXJOB-ADMIN");

        return SUCCESS;
    }
}
