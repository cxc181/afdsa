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
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.vo.OverdueCompanyCropAccInfoVO;
import com.yuqian.itax.user.service.CompanyCorporateAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *  @Author: LiuMenghao
 *  @Date: 2021/09/09
 *  @Description: 企业对公户年费已过期提醒通知 每日上午8:00执行一次
 */
@Slf4j
@JobHandler(value="companyCorpAccExpiredReminderHandler")
@Component
public class CompanyCorpAccExpiredReminderHandler extends IJobHandler {

	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private SmsService smsService;

	@Autowired
	private MessageNoticeService messageNoticeService;

	@Autowired
	private CompanyCorporateAccountService companyCorporateAccountService;

	private static final Integer DEFAULT_OVERDUE_DAYS = 1;// 默认已过期天数

	private static final Integer DEFAULT_SURPLUS_DAYS = 30;// 到期时间剩余天数

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("企业对公户年费到期提醒通知调度任务启动");

		XxlJobLogger.log("开始企业对公户年费已过期提醒通知任务");
		Integer overdueDays = DEFAULT_OVERDUE_DAYS;

		// 查询系统配置到期有效提醒天数
		DictionaryEntity dict1 = this.dictionaryService.getByCode("overdue_days");
		if(null == dict1){
			XxlJobLogger.log("系统未配置到期弹框有效天数，取默认配置");
		}else{
		    // 取系统配置天数
			overdueDays = Integer.parseInt(dict1.getDictValue());
        }

		//查询用户已过期对公户信息
		List<OverdueCompanyCropAccInfoVO> expiredList = companyCorporateAccountService.getOverdueCompanyCropAcc(overdueDays);

		if (null == expiredList || expiredList.isEmpty()) {
			XxlJobLogger.log("没有已过期的企业");
		}
		//筛选出需要发送通知的列表
		for (int i = 0; i < expiredList.size(); i++) {
			Boolean flag = false;
			OverdueCompanyCropAccInfoVO expiredCompany = expiredList.get(i);
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
		List<OverdueCompanyCropAccInfoVO> expirationList = companyCorporateAccountService.getWillExpireCompanyCropAcc(surplusDays);

		if (expirationList.isEmpty()) {
			XxlJobLogger.log("没有即将过期的企业，任务结束");
			return SUCCESS;
		}

		//筛选出需要发送通知的列表
		for (int i = 0; i < expirationList.size(); i++) {
			Boolean flag = false;
			OverdueCompanyCropAccInfoVO expirationCompany = expirationList.get(i);
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
	 * @param overdueCompanyCropAccInfoVO
	 * @param type 1-发送已过期通知 2-发送即将到期通知
	 */
	public void sendNotice(OverdueCompanyCropAccInfoVO overdueCompanyCropAccInfoVO , Integer type){

		//查看是否有未读的通知
		List<MessageNoticeEntity> list = messageNoticeService.findNoticeByType(overdueCompanyCropAccInfoVO.getMemberId(), overdueCompanyCropAccInfoVO.getOemCode(), 10);
		if(!list.isEmpty()) {
			for (MessageNoticeEntity noticeEntity : list) {
				//如果是发送已过期通知，则取消未读已过期通知，并取消未读即将过期通知
				if (type.equals(1)) {
					messageNoticeService.updateStatusById(noticeEntity.getId(), 3);
				}
				//如果是发送即将过期通知，则只需取消未读即将过期通知
				else if (type.equals(2) && noticeEntity.getNoticeContent().contains("对公户年费还有不到")) {
					messageNoticeService.updateStatusById(noticeEntity.getId(), 3);
				}
			}
		}

		//发送短信通知
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("number",overdueCompanyCropAccInfoVO.getCount());
			if (type.equals(2)){
				map.put("days",overdueCompanyCropAccInfoVO.getSurplusDays());
			}
			if (type.equals(1)){
				smsService.sendTemplateSms(overdueCompanyCropAccInfoVO.getMemberPhone(),overdueCompanyCropAccInfoVO.getOemCode(), VerifyCodeTypeEnum.ANNUAL_FEE_IS_OVERDUE.getValue(),map,1);
			} else {
				smsService.sendTemplateSms(overdueCompanyCropAccInfoVO.getMemberPhone(),overdueCompanyCropAccInfoVO.getOemCode(), VerifyCodeTypeEnum.ANNUAL_FEE_WILL_EXPIRE.getValue(),map,1);
			}

		} catch (BusinessException e) {
			XxlJobLogger.log("memberAccount【"+overdueCompanyCropAccInfoVO.getMemberAccount()+"】：短信发送失败，" + e.getMessage());
		}

		//发送站内通知
		// 获取模板
		String content = "";
		if (type.equals(1)){
			DictionaryEntity annualFeeIsOverdue = dictionaryService.getByCode("annual_fee_is_overdue");
			content = annualFeeIsOverdue.getDictValue();
		} else {
			DictionaryEntity annualFeeWillExpire = dictionaryService.getByCode("annual_fee_will_expire");
			content = annualFeeWillExpire.getDictValue();
			content = content.replace("#surplusDays#", overdueCompanyCropAccInfoVO.getSurplusDays().toString());
		}
		content = content.replace("#count#", overdueCompanyCropAccInfoVO.getCount().toString());

		try {
			MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
			messageNoticeEntity.setOemCode(overdueCompanyCropAccInfoVO.getOemCode());
			messageNoticeEntity.setNoticeType(2);
			messageNoticeEntity.setIsAlert(0);
			messageNoticeEntity.setNoticePosition("1,2");
			messageNoticeEntity.setOpenMode(3);
			messageNoticeEntity.setBusinessType(10);
			messageNoticeEntity.setNoticeTitle("对公户年费续费提醒");
			messageNoticeEntity.setNoticeContent(content);
			messageNoticeEntity.setUserPhones(overdueCompanyCropAccInfoVO.getMemberPhone());
			messageNoticeEntity.setStatus(0);
			messageNoticeEntity.setUserId(overdueCompanyCropAccInfoVO.getMemberId());
			messageNoticeEntity.setUserType(1);
			messageNoticeEntity.setAddTime(new Date());
			messageNoticeEntity.setAddUser("admin");
			messageNoticeService.saveMessageNotice(messageNoticeEntity);
			//修改企业表是否发送通知字段
			String[] split = overdueCompanyCropAccInfoVO.getCropAccId().split(",");
			for (String cropAccId : split) {
				CompanyCorporateAccountEntity corporateAccountEntity = new CompanyCorporateAccountEntity();
				corporateAccountEntity.setId(Long.valueOf(cropAccId));
				corporateAccountEntity = companyCorporateAccountService.selectOne(corporateAccountEntity);
				corporateAccountEntity.setIsSendNotice(1);
				companyCorporateAccountService.editByIdSelective(corporateAccountEntity);
			}

		} catch (Exception e) {
			XxlJobLogger.log("memberAccount【"+overdueCompanyCropAccInfoVO.getMemberAccount()+"】：通知发送失败，" + e.getMessage());
		}

	}
}
