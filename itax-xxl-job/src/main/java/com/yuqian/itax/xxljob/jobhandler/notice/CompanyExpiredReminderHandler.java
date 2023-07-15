package com.yuqian.itax.xxljob.jobhandler.notice;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.vo.OverdueCompanyInfoVO;
import com.yuqian.itax.user.service.MemberCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *  @Author: LiuMenghao
 *  @Date: 2021/02/18
 *  @Description: 企业托管费已过期提醒通知 每日上午8:00执行一次
 */
@Slf4j
@JobHandler(value="companyExpiredReminderHandler")
@Component
public class CompanyExpiredReminderHandler extends IJobHandler {

	@Autowired
	private MemberCompanyService memberCompanyService;

	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private SmsService smsService;

	@Autowired
	private MessageNoticeService messageNoticeService;

	private static final Integer DEFAULT_OVERDUE_DAYS = 1;// 默认已过期天数

	private static final Integer DEFAULT_SURPLUS_DAYS = 30;// 到期时间剩余天数

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("企业托管费到期提醒通知调度任务启动");

		XxlJobLogger.log("开始企业托管费已过期提醒通知任务");
		Integer overdueDays = DEFAULT_OVERDUE_DAYS;

		// 查询系统配置到期有效提醒天数
		DictionaryEntity dict1 = this.dictionaryService.getByCode("overdue_days");
		if(null == dict1){
			XxlJobLogger.log("系统未配置到期弹框有效天数，取默认配置");
		}else{
		    // 取系统配置天数
			overdueDays = Integer.parseInt(dict1.getDictValue());
        }

		//查询用户已过期企业信息
		List<OverdueCompanyInfoVO> expiredList = memberCompanyService.getOverdueByMemberId(overdueDays);

		if (!expiredList.isEmpty()) {
			//筛选出需要发送通知的列表
			OverdueCompanyInfoVO expiredCompany =null;
			for (int i = 0; i < expiredList.size(); i++) {
				Boolean flag = false;
				expiredCompany = expiredList.get(i);
				String[] isSendNoticeArr = expiredCompany.getIsSendNotice().split(",");
				for (int j = 0; j < isSendNoticeArr.length; j++) {
					if (isSendNoticeArr[j].equals("0")){
						flag = true;
					}
				}
				//会员是否存在
				if (Objects.isNull(expiredCompany.getMemberId()) || Objects.isNull(expiredCompany.getMemberPhone())
				||Objects.isNull(expiredCompany.getMemberAccount())){
					continue;
				}
				//触发过期提醒条件则发送通知
				if (flag){
					sendNotice(expiredCompany,1);
				}
			}
		}else {
			XxlJobLogger.log("没有已过期的企业");
		}
		XxlJobLogger.log("结束企业托管费已过期提醒通知任务");

		XxlJobLogger.log("开始企业托管费即将过期提醒通知任务");
		Integer surplusDays = DEFAULT_SURPLUS_DAYS;

		// 查询系统配置到期有效提醒天数
		DictionaryEntity dict2 = this.dictionaryService.getByCode("expire_surplus_days");
		if(null == dict2){
			XxlJobLogger.log("系统未配置到期弹框有效天数，取默认配置");
		}else{
			// 取系统配置天数
			surplusDays = Integer.parseInt(dict2.getDictValue());
		}

		//查询用户即将过期企业信息
		List<OverdueCompanyInfoVO> expirationList = memberCompanyService.getWillExpireByMemberId(surplusDays);

		if (expirationList.isEmpty()) {
			XxlJobLogger.log("没有即将过期的企业，任务结束");
			return SUCCESS;
		}

		//筛选出需要发送通知的列表
		OverdueCompanyInfoVO expirationCompany = null;
		for (int i = 0; i < expirationList.size(); i++) {
			Boolean flag = false;
			expirationCompany = expirationList.get(i);
			String[] isSendNoticeArr = expirationCompany.getIsSendNotice().split(",");
			for (int j = 0; j < isSendNoticeArr.length; j++) {
				if (isSendNoticeArr[j].equals("0")){
					flag = true;
				}
			}

			//会员是否存在
			if (Objects.isNull(expirationCompany.getMemberId()) || Objects.isNull(expirationCompany.getMemberPhone())
					||Objects.isNull(expirationCompany.getMemberAccount())){
				continue;
			}
			//触发过期提醒条件则发送通知
			if (flag){
				expirationCompany.setSurplusDays(surplusDays);
				sendNotice(expirationCompany,2);
			}
		}
		XxlJobLogger.log("结束企业托管费即将过期提醒通知任务");

		XxlJobLogger.log("企业托管费到期提醒通知调度任务结束");
		return SUCCESS;
	}

	/**
	 *
	 * @param overdueCompanyInfoVO
	 * @param type 1-发送已过期通知 2-发送即将到期通知
	 */
	public  void sendNotice(OverdueCompanyInfoVO overdueCompanyInfoVO , Integer type){

		//查看是否有未读的通知
		List<MessageNoticeEntity> list = messageNoticeService.findNoticeByType(overdueCompanyInfoVO.getMemberId(), overdueCompanyInfoVO.getOemCode(), 7);
		if(!list.isEmpty()) {
			for (MessageNoticeEntity noticeEntity : list) {
				//如果是发送已过期通知，则取消未读已过期通知，并取消未读即将过期通知
				if (type.equals(1)) {
					messageNoticeService.updateStatusById(noticeEntity.getId(), 3);
				}
				//如果是发送即将过期通知，则只需取消未读即将过期通知
				else if (type.equals(2) && noticeEntity.getNoticeContent().contains("有效期不足")) {
					messageNoticeService.updateStatusById(noticeEntity.getId(), 3);
				}
			}
		}

		//发送短信通知
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("number",overdueCompanyInfoVO.getCount());
			if (type.equals(2)){
				map.put("days",overdueCompanyInfoVO.getSurplusDays());
			}
			if (type.equals(1)){
				smsService.sendTemplateSms(overdueCompanyInfoVO.getMemberPhone(),overdueCompanyInfoVO.getOemCode(), VerifyCodeTypeEnum.CUSTODIAN_IS_OVERDUE.getValue(),map,1);
			} else {
				smsService.sendTemplateSms(overdueCompanyInfoVO.getMemberPhone(),overdueCompanyInfoVO.getOemCode(), VerifyCodeTypeEnum.CUSTODIAN_WILL_EXPIRE.getValue(),map,1);
			}

		} catch (BusinessException e) {
			XxlJobLogger.log("memberAccount【"+overdueCompanyInfoVO.getMemberAccount()+"】：短信发送失败，" + e.getMessage());
		}

		//发送站内通知
		try {
			MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
			messageNoticeEntity.setOemCode(overdueCompanyInfoVO.getOemCode());
			messageNoticeEntity.setNoticeType(2);
			messageNoticeEntity.setIsAlert(0);
			messageNoticeEntity.setNoticePosition("1,2");
			messageNoticeEntity.setOpenMode(3);
			messageNoticeEntity.setBusinessType(7);
			messageNoticeEntity.setNoticeTitle("托管费续费提醒");
			if (type.equals(1)){
				messageNoticeEntity.setNoticeContent("您有" + overdueCompanyInfoVO.getCount() + "家企业托管费已过期，为了不影响您的业务，请尽快去续费哦~");
			} else {
				messageNoticeEntity.setNoticeContent("您有"+ overdueCompanyInfoVO.getCount() +"家企业的托管费有效期不足"+overdueCompanyInfoVO.getSurplusDays()+"天，为了不影响您的业务，请尽快去续费哦~");
			}
			messageNoticeEntity.setUserPhones(overdueCompanyInfoVO.getMemberPhone());
			messageNoticeEntity.setStatus(0);
			messageNoticeEntity.setUserId(overdueCompanyInfoVO.getMemberId());
			messageNoticeEntity.setUserType(1);
			messageNoticeEntity.setAddTime(new Date());
			messageNoticeEntity.setAddUser("admin");
			messageNoticeService.saveMessageNotice(messageNoticeEntity);
			//修改企业表是否发送通知字段
			String[] split = overdueCompanyInfoVO.getCompanyId().split(",");
			for (String companyId : split) {
				MemberCompanyEntity memberCompanyEntity = new MemberCompanyEntity();
				memberCompanyEntity.setId(Long.valueOf(companyId));
				MemberCompanyEntity companyEntity = memberCompanyService.selectOne(memberCompanyEntity);
				companyEntity.setIsSendNotice(1);
				memberCompanyService.editByIdSelective(companyEntity);
			}

		} catch (Exception e) {
			XxlJobLogger.log("memberAccount【"+overdueCompanyInfoVO.getMemberAccount()+"】：通知发送失败，" + e.getMessage());
		}

	}
}
