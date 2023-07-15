package com.yuqian.itax.xxljob.jobhandler.haixin;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayMemberQuery;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayMemberVO;
import com.yuqian.itax.user.service.UserOrderStatisticsDayService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.EmailUtils;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * 每周一上午8点前筛选海星会员定时发送模板数据
 * @Author HZ
 * @Date 2021/2/4 15:34
 * @return
 */
@JobHandler(value="sendEmailToHaixinMemberHandler")
@Component
@Slf4j
public class SendEmailToHaixinMemberHandler extends IJobHandler {

	@Autowired
	private OemParamsService oemParamsService;

	@Autowired
	private UserOrderStatisticsDayService userOrderStatisticsDayService;
	@Autowired
	private DictionaryService dictionaryService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		long t1 = System.currentTimeMillis(); // 代码执行前时间
		XxlJobLogger.log("=========每周一上午8点前筛选海星会员定时发送业绩数据启动");


		UserOrderStatisticsDayMemberQuery query=new UserOrderStatisticsDayMemberQuery();
		String startDate= DateUtil.getLastWeekDate(0);//上周一
		String endDate=DateUtil.getLastWeekDate(7);//上周日
		query.setStartDate(startDate);
		query.setEndDate(endDate);
		if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
			log.info("日期不能为null");
			return SUCCESS;
		}
		query.setStartDate(query.getStartDate()+" 00:00:00");
		query.setEndDate(query.getEndDate()+" 23:59:59");
		query.setSign(1);
		List<UserOrderStatisticsDayMemberVO> list= userOrderStatisticsDayService.queryUserOrderStatisticsDayMember(query);
		if (CollectionUtil.isEmpty(list)) {
			log.info("暂无数据发送");
			return SUCCESS;
		}
		ExportParams exportParams = new ExportParams();
		exportParams.setSheetName("海星会员业绩统计");

		Workbook workbook = ExcelExportUtil.exportExcel(exportParams, UserOrderStatisticsDayMemberVO.class, list);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		workbook.write(os);
		ByteArrayInputStream iss = new ByteArrayInputStream(os.toByteArray());
		os.close();
		Map<String,String> params = getEmailParams("YCS");
		DictionaryEntity dictionaryEntity = dictionaryService.getByCode("haixin_email");
		String emails = dictionaryEntity.getDictValue();
		String[] arrs = emails.split(",");
		String to=arrs[0];
		String copy="";
		for (int i=1;i<arrs.length;i++) {
			copy=copy+","+arrs[i];
		}
		EmailUtils.sendMsgFileDs(params,to,copy.substring(1),"海星会员业绩统计"+startDate+"-"+endDate,"本期数据报表请见附件",iss);


		XxlJobLogger.log("=========每周一上午8点前筛选海星会员定时发送业绩数据启动");
		long t2 = System.currentTimeMillis(); // 代码执行后时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t2 - t1);
		XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
		return SUCCESS;
	}

	/**
	 * 获取邮箱发送参数配置
	 * @param oemCode
	 * @return
	 */
	private Map<String,String> getEmailParams(String oemCode){
		OemParamsEntity oemParamsEntity = oemParamsService.getParams(oemCode, OemParamsTypeEnum.EMAIL_CONFIG.getValue());
		Map<String,String> params = new HashMap<>();
		params.put("account",oemParamsEntity.getAccount());
		params.put("password",oemParamsEntity.getSecKey());
		params.put("emailHost",oemParamsEntity.getUrl());
		JSONObject jsonObject = JSONObject.parseObject(oemParamsEntity.getParamsValues());
		params.put("port",jsonObject.getString("port"));
		return params;
	}
}
