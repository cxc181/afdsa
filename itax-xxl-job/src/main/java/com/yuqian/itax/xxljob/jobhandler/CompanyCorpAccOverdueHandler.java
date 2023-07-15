package com.yuqian.itax.xxljob.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.service.CompanyCorporateAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 对公户过期状态修改 每天凌晨00:01执行任务
 * @author：liumenghao
 * @Date：2021-09-13
 * @version：1.0
 */
@JobHandler(value="CompanyCorpAccOverdueHandler")
@Component
public class CompanyCorpAccOverdueHandler extends IJobHandler {

	@Autowired
	private MemberCompanyService memberCompanyService;

	@Autowired
    private CompanyCorporateAccountService companyCorporateAccountService;

    @Autowired
    private DictionaryService dictionaryService;

    private static final Integer DEFAULT_SURPLUS_DAYS = 30;// 到期时间剩余天数

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("=========对公户过期状态修改任务启动");
		//修改已过期对公户状态
		companyCorporateAccountService.updateOverdueCorpAccStatus();

		//修改即将过期对公户状态
        Integer surplusDays = DEFAULT_SURPLUS_DAYS;
        // 查询系统配置到期有效提醒天数
        DictionaryEntity dict2 = this.dictionaryService.getByCode("expire_surplus_days");
        if(null == dict2){
            XxlJobLogger.log("系统未配置到期弹框有效天数，取默认配置");
        }else{
            // 取系统配置天数
            surplusDays = Integer.parseInt(dict2.getDictValue());
        }
        companyCorporateAccountService.updateCompanyCorpAccOverdueStatus(surplusDays);
		XxlJobLogger.log("=========对公户过期状态修改任务结束");
		return SUCCESS;
	}

}
