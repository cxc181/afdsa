package com.yuqian.itax.xxljob.jobhandler.notice;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.park.enums.IncomeLevyTypeEnum;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.query.PendingTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.PendingTaxBillVO;
import com.yuqian.itax.tax.enums.TaxBillStatusEnum;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  @Author: LiuMenghao
 *  @Date: 2022/03/17
 *  @Description: 企业税单确认成本提醒通知
 */
@Slf4j
@JobHandler(value="taxConfirmCostReminderHandler")
@Component
public class TaxConfirmCostReminderHandler extends IJobHandler {

	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private SmsService smsService;
	@Autowired
	private OemService oemService;

	@Autowired
	private MessageNoticeService messageNoticeService;

	@Autowired
	private CompanyTaxBillService companyTaxBillService;

	@Autowired
	private MemberCompanyService memberCompanyService;

	private static final Integer DEFAULT_REMIND_DAYS = 3; // 默认提前提醒天数
	private static final Integer DEFAULT_OVERTIME_DAYS = -1;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("企业税单确认成本提醒通知调度任务启动");


		Integer remindDay = DEFAULT_REMIND_DAYS;

		// 查询系统配置到期有效提醒天数
		DictionaryEntity dict1 = this.dictionaryService.getByCode("confirm_cost_remind_days");
		if(null == dict1){
			XxlJobLogger.log("系统未配置确认成本提醒天数，取默认配置");
		}else{
		    // 取系统配置天数
			remindDay = Integer.parseInt(dict1.getDictValue());
        }

		// 查询需要提醒的用户
		PendingTaxBillQuery query = new PendingTaxBillQuery();
		query.setStatusRange(1);
		List<PendingTaxBillVO> vos = companyTaxBillService.pendingTaxBill(query);

		// 过滤掉本期开票金额为0的企业
		vos = vos.stream().filter(x -> x.getInvoiceMoney() > 0L).collect(Collectors.toList());

		// 过滤掉非查账征收的企业
		vos = vos.stream().filter(x -> IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(x.getIncomeLevyType())).collect(Collectors.toList());

		if (CollectionUtil.isEmpty(vos)) {
			XxlJobLogger.log("无超时或即将超时确认成本税单，调度任务结束");
		}
		// 过滤掉待核对状态税单
		vos = vos.stream().filter(x -> !TaxBillStatusEnum.TO_BE_CHECK.getValue().equals(x.getTaxBillStatus())).collect(Collectors.toList());

		// 过滤出需发送截止日提醒确认成本的用户
		Integer finalRemindDay = remindDay;
		List<PendingTaxBillVO> confirmCostReminds = vos.stream().filter(x -> Objects.equals(x.getTimeDifference(), finalRemindDay)).collect(Collectors.toList());
		if (CollectionUtil.isEmpty(confirmCostReminds)) {
			XxlJobLogger.log("无即将超时确认成本的税单");
		} else {
			this.sendNotice(confirmCostReminds, 1, remindDay);
		}

		// 过滤出需要发送确认成本已超时提醒的用户
		List<PendingTaxBillVO> confirmOvertimeReminds = vos.stream().filter(x -> x.getIsSendNotice() == 0
				&& (Objects.equals(x.getTimeDifference(), DEFAULT_OVERTIME_DAYS) || x.getTimeDifference() < DEFAULT_OVERTIME_DAYS ))
				.collect(Collectors.toList());
		if (CollectionUtil.isEmpty(confirmOvertimeReminds)) {
			XxlJobLogger.log("无超时未确认成本的税单");
		} else {
			this.sendNotice(confirmOvertimeReminds, 2, remindDay);
		}

		XxlJobLogger.log("企业税单确认成本提醒通知调度任务结束");
		return SUCCESS;
	}

	/**
	 *
	 * @param pendingTaxBillVO
	 * @param type 1-发送即将超时确认成本通知 2-发送超时确认成本通知
	 */
	private void sendNotice(List<PendingTaxBillVO> pendingTaxBillVO, Integer type, Integer remindDay) {
		List<Long> list = Lists.newArrayList();
		for (PendingTaxBillVO taxBillVO : pendingTaxBillVO) {
			if (list.contains(taxBillVO.getMemberId())) {
				// 修改是否发送通知标识
				if (type == 2) {
					try {
						editNoticeMark(taxBillVO.getCompanyTaxBillId());
					} catch (Exception e) {
						continue;
					}
				}
				continue;
			} else {
				list.add(taxBillVO.getMemberId());
			}
			// 查询企业信息
			MemberCompanyEntity company = memberCompanyService.findById(taxBillVO.getCompanyId());
			if (null == company) {
				XxlJobLogger.log("未查询到企业信息，企业id为：" + taxBillVO.getCompanyId());
				continue;
			}
			// 已冻结企业不发通知
			if (MemberCompanyStatusEnum.PROHIBIT.getValue().equals(company.getStatus())) {
				continue;
			}
			if (StringUtil.isBlank(taxBillVO.getMemberPhone()) && StringUtil.isBlank(taxBillVO.getOperatorTel())) {
				continue;
			}
			String phone = taxBillVO.getMemberPhone();
			String oemCode = taxBillVO.getOemCode();

			OemEntity oemEntity = oemService.getOem(oemCode);
			if (oemEntity.getIsSendAuditBillsMessage().equals(0)){
				continue;
			}
			//发送短信通知
			try {
				Map<String, Object> map = new HashMap<>();
				String verifyCodeType;
				if (type == 1) {
					map.put("taxBillSeasonal", taxBillVO.getTaxBillSeasonal());
					map.put("remindDay", remindDay);
					verifyCodeType = VerifyCodeTypeEnum.CONFIRM_COST_REMIND.getValue();
				} else {
					verifyCodeType = VerifyCodeTypeEnum.CONFIRM_OVERTIME_REMIND.getValue();
				}
				smsService.sendTemplateSms(phone, oemCode, verifyCodeType, map, 1);
				// 经营者手机号与用户手机号不一致时，也发送一条短信
				if (!phone.equals(taxBillVO.getOperatorTel())) {
					phone = taxBillVO.getOperatorTel();
					smsService.sendTemplateSms(phone, oemCode, verifyCodeType, map, 1);
				}
			} catch (BusinessException e) {
				XxlJobLogger.log("memberAccount【" + phone + "】：短信发送失败，" + e.getMessage());
				continue;
			}

			//发送站内通知
			try {
				String value = "";
				if (type == 1) {
					value = dictionaryService.getValueByCode("confirm_cost_remind");
				} else {
					value = dictionaryService.getValueByCode("confirm_overtime_remind");
				}
				JSONObject jsonObject = JSONObject.parseObject(value);
				String noticeTitle = jsonObject.getString("noticeTitle");
				String noticeContent = jsonObject.getString("noticeContent");
				String noticeSubtitle = jsonObject.getString("noticeSubtitle");
				if (type == 1) {
					noticeContent = noticeContent.replace("#taxBillSeasonal#", taxBillVO.getTaxBillSeasonal().toString());
					noticeContent = noticeContent.replace("#remindDay#", remindDay.toString());
					noticeSubtitle = noticeSubtitle.replace("#taxBillSeasonal#", taxBillVO.getTaxBillSeasonal().toString());
					noticeSubtitle = noticeSubtitle.replace("#remindDay#", remindDay.toString());
				}
				MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
				messageNoticeEntity.setOemCode(oemCode);
				messageNoticeEntity.setNoticeType(2);
				messageNoticeEntity.setIsAlert(0);
				messageNoticeEntity.setNoticePosition("1,2");
				messageNoticeEntity.setBusinessType(13);
				messageNoticeEntity.setOpenMode(3);
				messageNoticeEntity.setNoticeTitle(noticeTitle);
				messageNoticeEntity.setNoticeSubtitle(noticeSubtitle);
				messageNoticeEntity.setNoticeContent(noticeContent);
				messageNoticeEntity.setUserPhones(taxBillVO.getMemberPhone());
				messageNoticeEntity.setStatus(0);
				messageNoticeEntity.setUserId(taxBillVO.getMemberId());
				messageNoticeEntity.setUserType(1);
				messageNoticeEntity.setAddTime(new Date());
				messageNoticeEntity.setAddUser("xxl-job");
				messageNoticeService.saveMessageNotice(messageNoticeEntity);
			} catch (Exception e) {
				XxlJobLogger.log("memberAccount【"+ phone +"】：通知发送失败，" + e.getMessage());
				continue;
			}

			// 修改是否发送通知标识
			if (type == 2) {
				try {
					editNoticeMark(taxBillVO.getCompanyTaxBillId());
				} catch (Exception e) {
					continue;
				}
			}
		}
	}

	/**
	 * 修改是否发送通知标识
	 * @param companyTaxBillId
	 */
	private void editNoticeMark(Long companyTaxBillId) {
		// 查询企业税单
		CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(companyTaxBillId);
		if (null == companyTaxBillEntity) {
			XxlJobLogger.log("未查询到企业税单，企业税单id为：" + companyTaxBillId);
		}
		companyTaxBillEntity.setIsSendNotice(1);
		companyTaxBillService.editByIdSelective(companyTaxBillEntity);
	}

}
