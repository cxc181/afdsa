package com.yuqian.itax.xxljob.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.service.MemberCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 公司过期状态修改 每天凌晨00:01执行任务
 * @author：jiangni
 * @Date：219-12-26
 * @version：1.0
 */
@JobHandler(value="memberCompanyOverdueHandler")
@Component
public class MemberCompanyOverdueHandler extends IJobHandler {

	@Autowired
	private MemberCompanyService memberCompanyService;

    @Autowired
    private DictionaryService dictionaryService;

    private static final Integer DEFAULT_SURPLUS_DAYS = 30;// 到期时间剩余天数

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("=========公司过期状态修改任务启动");
		//修改已过期企业状态
		memberCompanyService.updateOverdueCompanyStatus();

		//修改即将过期企业状态
        Integer surplusDays = DEFAULT_SURPLUS_DAYS;
        // 查询系统配置到期有效提醒天数
        DictionaryEntity dict2 = this.dictionaryService.getByCode("expire_surplus_days");
        if(null == dict2){
            XxlJobLogger.log("系统未配置到期弹框有效天数，取默认配置");
        }else{
            // 取系统配置天数
            surplusDays = Integer.parseInt(dict2.getDictValue());
        }
		memberCompanyService.updateCompanyOverdueStatus(surplusDays);
		XxlJobLogger.log("=========公司过期状态修改任务结束");
		return SUCCESS;
	}

}
