package com.yuqian.itax.xxljob.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.user.entity.CompanyInvoiceRecordEntity;
import com.yuqian.itax.user.service.CompanyInvoiceRecordService;
import com.yuqian.itax.util.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 企业年开票记录数据添加 每天凌晨00:01执行任务
 * @author：jiangni
 * @Date：219-12-26
 * @version：1.0
 */
@JobHandler(value="companyInvoiceRecordByYearHandler")
@Component
public class CompanyInvoiceRecordByYearHandler extends IJobHandler {

	@Autowired
	private CompanyInvoiceRecordService companyInvoiceRecordService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		long t1 = System.currentTimeMillis(); // 代码执行前时间
		XxlJobLogger.log("=========企业年开票记录数据添加任务启动");
		int year = DateUtil.getYear(new Date());
		CompanyInvoiceRecordEntity entity = new CompanyInvoiceRecordEntity();
		entity.setYear((year-1)+"");
		List<CompanyInvoiceRecordEntity> result = companyInvoiceRecordService.select(entity);
		if(result== null || result.size()<1){
			return SUCCESS;
		}
		CompanyInvoiceRecordEntity companyInvoiceRecordEntity = null;
		CompanyInvoiceRecordEntity cire = null;
		for(CompanyInvoiceRecordEntity vo : result) {
			XxlJobLogger.log("companyId-->"+vo.getCompanyId());
			companyInvoiceRecordEntity = new CompanyInvoiceRecordEntity();
			companyInvoiceRecordEntity.setYear(year+"");
			cire = companyInvoiceRecordService.selectOne(companyInvoiceRecordEntity);
			if(cire != null){
				continue;
			}
			companyInvoiceRecordEntity.setCompanyId(vo.getCompanyId());
			companyInvoiceRecordEntity.setTotalInvoiceAmount(vo.getTotalInvoiceAmount());
			companyInvoiceRecordEntity.setRemainInvoiceAmount(vo.getTotalInvoiceAmount());
			companyInvoiceRecordService.insertSelective(companyInvoiceRecordEntity);
		}
		XxlJobLogger.log("=========企业年开票记录数据添加任务结束");
		long t2 = System.currentTimeMillis(); // 代码执行后时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t2 - t1);
		XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
		return SUCCESS;
	}
	
}
