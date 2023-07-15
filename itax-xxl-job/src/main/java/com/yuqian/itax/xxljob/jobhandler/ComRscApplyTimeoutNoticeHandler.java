package com.yuqian.itax.xxljob.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.vo.ComRscTimeoutRecordVO;
import com.yuqian.itax.user.service.CompanyResourcesUseRecordService;
import com.yuqian.itax.user.service.MemberAccountService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/4 14:18
 *  @Description: 企业用章申请审核超时提醒任务：
 *  xxl-job定时检查是否有用章未审核超过24小时或72小时的记录，如果有则通过短信提示用户（注册用户）
 */
@Slf4j
@JobHandler(value="comRscApplyTimeoutNoticeHandler")
@Component
public class ComRscApplyTimeoutNoticeHandler extends IJobHandler {

	@Autowired
	private CompanyResourcesUseRecordService companyResourcesUseRecordService;
	@Autowired
	private SmsService smsService;
	@Autowired
	private MemberAccountService memberAccountService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("企业用章申请审核超时提醒任务启动");

		// 查询超过24小时未处理的用章申请条数
		List<ComRscTimeoutRecordVO> listFor24 = companyResourcesUseRecordService.selectTimeOutList(1);

		// 查询超过72小时未处理的用章申请条数
		List<ComRscTimeoutRecordVO> listFor72 = companyResourcesUseRecordService.selectTimeOutList(2);

		if (CollectionUtil.isEmpty(listFor24) && CollectionUtil.isEmpty(listFor72)) {
			XxlJobLogger.log("没有超时未处理的企业用章申请记录，任务结束");
			return SUCCESS;
		}

		// 超时24小时未处理的发送短信通知
        List<Long> list = Lists.newArrayList();// 临时变量，存发送过短信的用户ID
        for(ComRscTimeoutRecordVO record : listFor24){
			// 发送短信
			try {
				Map<String,Object> map = new HashMap();
				map.put("count",record.getDealCount());
				// 查询待通知用户
				MemberAccountEntity member = this.memberAccountService.findById(record.getAuditUser());
				if(null == member){
					XxlJobLogger.log("用章通知短信发送失败，未找到审核人信息");
					continue;
				}
				// 根据通知状态判断是否发送短信，已经通知的不重复发送
				if(record.getIsNotice() == 0 && !list.contains(record.getAuditUser())){
					Map<String,Object> resultMap = smsService.sendTemplateSms(member.getMemberAccount(),member.getOemCode(),"21", map,1);
					if (!"SUCCESS".equals(resultMap.get("code"))){
						XxlJobLogger.log("发送给用户{}的用章通知短信发送失败:{} ", member.getMemberAccount(),resultMap.get("message"));
					}
                    list.add(record.getAuditUser());
					//更新用章申请通知状态
					companyResourcesUseRecordService.updateResourcesNoticeStatusByAccount(record.getAuditUser(),1);
				}
			} catch (Exception e) {
				XxlJobLogger.log("用章通知短信发送失败:{} ", e.getMessage());
				XxlJobLogger.log(e);
			}
		}

		// 超过72小时未处理的发送短信通知
        List<Long> list2 = Lists.newArrayList();// 临时变量，存发送过短信的用户ID
        for(ComRscTimeoutRecordVO record : listFor72){
			// 发送短信
			try {
				Map<String,Object> map = new HashMap();
				map.put("count",record.getDealCount());
				// 查询待通知用户
				MemberAccountEntity member = this.memberAccountService.findById(record.getAuditUser());
				if(null == member){
					XxlJobLogger.log("用章通知短信发送失败，未找到审核人信息");
					continue;
				}
				// 根据通知状态判断是否发送短信，已经通知的不重复发送
				if(record.getIsNotice() == 1 && !list2.contains(record.getAuditUser())){
					Map<String,Object> resultMap = smsService.sendTemplateSms(member.getMemberAccount(),member.getOemCode(),"22", map,1);
					if (!"SUCCESS".equals(resultMap.get("code"))){
						XxlJobLogger.log("发送给用户{}的用章通知短信发送失败:{} ", member.getMemberAccount(),resultMap.get("message"));
					}
                    list2.add(record.getAuditUser());
					//更新用章申请通知状态
					companyResourcesUseRecordService.updateResourcesNoticeStatusByAccount(record.getAuditUser(),2);
				}
			} catch (Exception e) {
				XxlJobLogger.log("用章通知短信发送失败:{} ", e.getMessage());
				XxlJobLogger.log(e);
			}
		}
		XxlJobLogger.log("企业用章申请审核超时提醒任务结束");
		return SUCCESS;
	}
}
