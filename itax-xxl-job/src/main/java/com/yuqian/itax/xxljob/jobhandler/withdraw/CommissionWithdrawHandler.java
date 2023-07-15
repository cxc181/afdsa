package com.yuqian.itax.xxljob.jobhandler.withdraw;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.nabei.entity.SinglePayQueryRespVo;
import com.yuqian.itax.nabei.service.NabeiApiService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.enums.RACWStatusEnum;
import com.yuqian.itax.order.enums.WalletTypeEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.enums.PayChannelEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @Description 佣金钱包提现订单跑批处理
 * @Author  Kaven
 * @Date   2020/6/29 12:01 下午
*/
@JobHandler(value="commissionWithdrawHandler")
@Component
public class CommissionWithdrawHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;
	@Autowired
	private NabeiApiService nabeiApiService;
	@Autowired
	private OemParamsService oemParamsService;
	@Autowired
	private UserCapitalAccountService userCapitalAccountService;
	@Autowired
	private PayWaterService  payWaterService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("佣金钱包提现任务启动");
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setOrderType(OrderTypeEnum.WITHDRAW.getValue());
		orderEntity.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());
		orderEntity.setOrderStatus(RACWStatusEnum.PAYING.getValue());
		List<OrderEntity> lists = this.orderService.select(orderEntity);
		if (CollectionUtil.isEmpty(lists)) {
			XxlJobLogger.log("无提现中订单，任务结束");
		}

		// 遍历集合，调用纳呗单笔出款查询接口，根据结果处理订单状态
		for(OrderEntity order : lists) {
			try {
				PayWaterEntity payWaterEntity = new PayWaterEntity();
				payWaterEntity.setOrderNo(order.getOrderNo());
				payWaterEntity.setOemCode(order.getOemCode());
				payWaterEntity = payWaterService.selectOne(payWaterEntity);
				if (payWaterEntity == null) {
					throw new BusinessException("提现流水不存在");
				}
				// 判断如果不是纳呗通道 则直接退出本次查询
				if(payWaterEntity.getPayChannels().intValue() != PayChannelEnum.NABEIPAY.getValue().intValue()){
					continue;
				}
				UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(order.getUserId(),order.getUserType(), order.getOemCode(), order.getWalletType());
				if (accEntity == null) {
					throw new BusinessException("资金账户不存在");
				}

				// 读取纳呗平台API相关配置 paramsType=10
				OemParamsEntity paramsEntity = this.oemParamsService.getParams(order.getOemCode(), 10);
				if (null == paramsEntity) {
					throw new BusinessException("未配置纳呗平台API相关信息！");
				}
				SinglePayQueryRespVo singlePayQueryRespVo = this.nabeiApiService.singlePayQuery(paramsEntity,order.getOrderNo());
				if(null != singlePayQueryRespVo){
					XxlJobLogger.log("纳呗单笔出款查询接口返回：{}",JSON.toJSONString(singlePayQueryRespVo));
					if(Objects.equals(singlePayQueryRespVo.getP1_resCode(), "0000")) {
						// 代发处理状态 0-等待业务处理，1-业务系统出款中，2-出款成功，3-出款失败,9-待签约
						if("2".equals(singlePayQueryRespVo.getP8_status())){
							this.orderService.handleCommissionWithdrawResult(order,accEntity,0, "");
						} else if("3".equals(singlePayQueryRespVo.getP8_status())) {
							this.orderService.handleCommissionWithdrawResult(order,accEntity,1, singlePayQueryRespVo.getP9_message());
						}
					}else if(Objects.equals(singlePayQueryRespVo.getP1_resCode(), "0014")) { //在纳呗未查询到订单
						this.orderService.handleCommissionWithdrawResult(order,accEntity,1, singlePayQueryRespVo.getP2_resMsg());
					}
				}else{
					XxlJobLogger.log("纳呗单笔出款查询接口异常");
				}
			} catch (BusinessException e) {
				XxlJobLogger.log("佣金钱包提现失败，订单号：{}，错误原因：{}", order.getOrderNo(), e.getMessage());
			} catch (Exception e) {
				XxlJobLogger.log("佣金钱包提现失败，订单号：{}，错误原因：{}", order.getOrderNo(), e.getMessage());
				XxlJobLogger.log(e);
			}
		}
		XxlJobLogger.log("佣金钱包提现任务结束");
		return SUCCESS;
	}
}
