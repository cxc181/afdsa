package com.yuqian.itax.xxljob.jobhandler.withdraw;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountWithdrawalOrderEntity;
import com.yuqian.itax.corporateaccount.service.CorporateAccountCollectionRecordService;
import com.yuqian.itax.corporateaccount.service.CorporateAccountWithdrawalOrderService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.enums.RACWStatusEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.service.CompanyCorporateAccountService;
import com.yuqian.itax.user.service.DaifuApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Description 对公户提现订单结果轮询处理任务
 * @Author  Kaven
 * @Date   2020/9/9 13:57
*/
@JobHandler(value="corporateAccountWithdrawHandler")
@Component
public class CorporateAccountWithdrawHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;
	@Autowired
	private DaifuApiService daifuApiService;
	@Autowired
	private OemParamsService oemParamsService;
	@Autowired
	private CorporateAccountCollectionRecordService corporateAccountCollectionRecordService;
	@Autowired
	private CorporateAccountWithdrawalOrderService corporateAccountWithdrawalOrderService;
	@Autowired
	private CompanyCorporateAccountService companyCorporateAccountService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("对公户提现订单结果轮询处理任务启动");
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setOrderType(OrderTypeEnum.CORPORATE_WITHDRAW.getValue());
		orderEntity.setOrderStatus(RACWStatusEnum.PAYING.getValue());
		List<OrderEntity> lists = orderService.select(orderEntity);
		if (CollectionUtil.isEmpty(lists)) {
			XxlJobLogger.log("无提现中的对公户订单，任务结束");
		}
		OemParamsEntity paramsEntity = null;
		for (OrderEntity entity : lists) {
			try {
				// 读取渠道代付相关配置 paramsType=18
				paramsEntity = this.oemParamsService.getParams(entity.getOemCode(),18);
				if(null == paramsEntity){
					XxlJobLogger.log("未配置渠道代付相关信息");
					break;
				}
				CorporateAccountWithdrawalOrderEntity corporateAccountWithdrawalOrderEntity=new CorporateAccountWithdrawalOrderEntity();
				corporateAccountWithdrawalOrderEntity.setOrderNo(entity.getOrderNo());
				CorporateAccountWithdrawalOrderEntity cor=corporateAccountWithdrawalOrderService.selectOne(corporateAccountWithdrawalOrderEntity);
				CompanyCorporateAccountEntity companyCorporateAccountEntity=companyCorporateAccountService.findById(cor.getCorporateAccountId());
				JSONObject jsonObj = this.daifuApiService.queryPaidOrder(paramsEntity,entity.getOrderNo(),companyCorporateAccountEntity.getVoucherMemberCode());
				if(null != jsonObj && "00".equals(jsonObj.getString("bizCode"))){
					String orderStatus = jsonObj.getString("status");// 0 初始化 1 支付中 3 超时 4 关闭 5 成功 -1 失败
					if ("5".equals(orderStatus)) {
						// 提现成功，修改订单状态和流水状态
						XxlJobLogger.log("提现成功，修改订单状态:{}", entity.getOrderNo());
						this.orderService.updateOrderStatusAndExternalOrderNo(jsonObj.getString("tradeNo"),entity.getOrderNo(),RACWStatusEnum.PAYED.getValue());
					} else if ("-1".equals(orderStatus) || "4".equals(orderStatus)){
						XxlJobLogger.log("对公户提现失败，需要将开票对应的提现金额和收款记录对应的可提现金额进行回退并更新订单状态：{}", entity.getOrderNo());
						// 对公户提现失败，需要将开票对应的提现金额和收款记录对应的可提现金额进行回退并更新订单状态
						this.corporateAccountCollectionRecordService.returnWithdrawAmount(entity.getOrderNo(),jsonObj.getString("tradeNo"),jsonObj.getString("bizCode"),jsonObj.getString("bizCodeMsg"));

						//保存对公户提现错误信息 add ni.jiang
						if(jsonObj.containsKey("chResultMsg")){
							try {
								String chResultMsg = jsonObj.getString("chResultMsg");
								if (StringUtils.isNoneBlank(chResultMsg) && chResultMsg.length() > 127) {
									chResultMsg = chResultMsg.substring(chResultMsg.indexOf("业务异常"));
								}
								cor.setRemark(chResultMsg);
								cor.setUpdateTime(new Date());
								cor.setUpdateUser("xxljob");
								corporateAccountWithdrawalOrderService.editByIdSelective(cor);
							}catch (Exception e){
								XxlJobLogger.log("====保存对公户提现订单错误信息失败,订单号{}:错误信息{}", entity.getOrderNo(),e.getMessage());
							}
						}
					}
				} else if(null != jsonObj && "2022".equals(jsonObj.getString("bizCode"))){
					XxlJobLogger.log("对公户提现失败，需要将开票对应的提现金额和收款记录对应的可提现金额进行回退并更新订单状态:{}", entity.getOrderNo());
					// 对公户提现失败，需要将开票对应的提现金额和收款记录对应的可提现金额进行回退并更新订单状态
					this.corporateAccountCollectionRecordService.returnWithdrawAmount(entity.getOrderNo(),jsonObj.getString("tradeNo"),jsonObj.getString("bizCode"),jsonObj.getString("bizCodeMsg"));
				}
			} catch (BusinessException e) {
				XxlJobLogger.log("对公户提现订单结果处理失败，订单号：{}，错误原因：{}", entity.getOrderNo(), e.getMessage());
			} catch (Exception e) {
				XxlJobLogger.log("对公户提现订单结果处理失败，订单号：{}，错误原因：{}", entity.getOrderNo(), e.getMessage());
				XxlJobLogger.log(e);
			}
		}
		XxlJobLogger.log("对公户提现订单结果轮询处理任务结束");
		return SUCCESS;
	}
}
