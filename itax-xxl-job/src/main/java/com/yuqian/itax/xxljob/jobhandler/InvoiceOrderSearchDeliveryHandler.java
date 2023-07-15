package com.yuqian.itax.xxljob.jobhandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderChangeEntity;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderEntity;
import com.yuqian.itax.order.entity.LogisticsInfoEntity;
import com.yuqian.itax.order.entity.vo.ConsumptionInvoiceOrderJobVO;
import com.yuqian.itax.order.entity.vo.InvoiceOrderDetailVO;
import com.yuqian.itax.order.enums.InvoiceOrderStatusEnum;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.system.entity.LogisCompanyEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.LogisCompanyService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.DeliveryUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 开票待收货订单调用快递100接口获取快递信息，将已签收改为已完成任务启动 每30分钟执行一次任务
 * @Author yejian
 * @Date 2020/02/13 11:34
 * @return
 */
@JobHandler(value="invoiceOrderSearchDeliveryHandler")
@Component
public class InvoiceOrderSearchDeliveryHandler extends IJobHandler {
	@Autowired
	private DictionaryService sysDictionaryService;
	@Autowired
	private InvoiceOrderService invoiceOrderService;
	@Autowired
	private LogisCompanyService logisCompanyService;
	@Autowired
	private LogisticsInfoService logisticsInfoService;
	@Autowired
	private SmsService smsService;
	@Autowired
	private MemberAccountService memberAccountService;
	@Autowired
	private ConsumptionInvoiceOrderService consumptionInvoiceOrderService;
	@Autowired
	private ConsumptionInvoiceOrderChangeService consumptionInvoiceOrderChangeService;
	@Autowired
	private OrderService orderService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		long t1 = System.currentTimeMillis(); // 代码执行前时间
		XxlJobLogger.log("===========>开票待收货订单调用快递100接口获取快递信息，将已签收改为已完成任务启动");
		//查询待收货的开票订单和快递表未保存数据的已收货开票订单
		List<InvoiceOrderDetailVO>  invorderList = invoiceOrderService.listInvoiceOrderDetailByStatus();
		//待签收的消费订单
		List<ConsumptionInvoiceOrderJobVO> consumptionInvoiceOrderList = consumptionInvoiceOrderService.listConsumptionInvoiceByStatus();
		if((invorderList== null || invorderList.size()<1) &&(consumptionInvoiceOrderList== null || consumptionInvoiceOrderList.size()<1)){
			return SUCCESS;
		}
		if (invorderList != null && invorderList.size()>0){
			invorderList.stream().forEach(invOrder ->{
				XxlJobLogger.log("orderNo-->"+invOrder.getOrderNo());
				getExpress(invOrder);
			});
		}

		XxlJobLogger.log("===========>消费开票待收货订单调用快递100接口获取快递信息，将已签收改为已完成任务结束");
		if (consumptionInvoiceOrderList != null && consumptionInvoiceOrderList.size()>0){
			consumptionInvoiceOrderList.stream().forEach(consumptionInvoiceOrder ->{
				XxlJobLogger.log("orderNo-->"+consumptionInvoiceOrder.getOrderNo());
				getExpressInConsumptionInvoiceOrder(consumptionInvoiceOrder);
			});
		}
		XxlJobLogger.log("===========>消费开票待收货订单调用快递100接口获取快递信息，将待签收改为已完成任务结束");
		long t2 = System.currentTimeMillis(); // 代码执行后时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t2 - t1);
		XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
		return SUCCESS;
	}

	private void getExpressInConsumptionInvoiceOrder(ConsumptionInvoiceOrderJobVO consumptionInvoiceOrder ){
		if(StringUtils.isBlank(consumptionInvoiceOrder.getCourierCompanyName()) || StringUtils.isBlank(consumptionInvoiceOrder.getCourierNumber())){
			XxlJobLogger.log("订单号【"+consumptionInvoiceOrder.getOrderNo()+"】快递公司名称或快递订单不存在");
			return ;
		}
		//获取快递编码
		LogisCompanyEntity logis = new LogisCompanyEntity();
		logis.setCompanyName(consumptionInvoiceOrder.getCourierCompanyName());
		logis = logisCompanyService.selectOne(logis);
		if (null == logis) {
			XxlJobLogger.log("快递公司名称有误-->"+consumptionInvoiceOrder.getCourierCompanyName());
			return ;
		}

		//快递100查询快递信息
		String result = DeliveryUtils.synQueryData(sysDictionaryService.getByCode("kuaidi100_key").getDictValue(),
				sysDictionaryService.getByCode("kuaidi100_companyno").getDictValue(), logis.getCompanyCode(),
				consumptionInvoiceOrder.getCourierNumber(), null,null,null, 1);

		//解析快递100返回结果
		JSONObject resultObj = JSONObject.parseObject(result);
		if("ok".equals(resultObj.getString("message")) && "200".equals(resultObj.getString("status"))){

			//state	快递单当前状态，包括0在途，1揽收，2疑难，3签收，4退签，5派件，6退回等7个状态
			//如果是签收状态则修改订单状态为已完成
			if("3".equals(resultObj.getString("state"))){

				// 查询快递公司名称
				logis = new LogisCompanyEntity();
				logis.setCompanyCode(resultObj.getString("com"));
				logis = logisCompanyService.selectOne(logis);

				// 如果数据库状态为待签收则确认收货
				if(Objects.equals(consumptionInvoiceOrder.getOrderStatus(), 5)){
					ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity = new ConsumptionInvoiceOrderEntity();
					consumptionInvoiceOrderEntity.setOrderNo(consumptionInvoiceOrder.getOrderNo());
					consumptionInvoiceOrderEntity = consumptionInvoiceOrderService.selectOne(consumptionInvoiceOrderEntity);
					Date date =  new Date();
					consumptionInvoiceOrderEntity.setUpdateTime(date);
					consumptionInvoiceOrderEntity.setUpdateUser("xxJob");
					consumptionInvoiceOrderEntity.setCompleteTime(date);
					ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity=new ConsumptionInvoiceOrderChangeEntity();
					BeanUtils.copyProperties(consumptionInvoiceOrderEntity,consumptionInvoiceOrderChangeEntity);
					consumptionInvoiceOrderChangeEntity.setStatus(2);
					consumptionInvoiceOrderChangeEntity.setId(null);
					consumptionInvoiceOrderChangeEntity.setAddUser("xxJob");
					consumptionInvoiceOrderChangeEntity.setAddTime(new Date());
					consumptionInvoiceOrderChangeEntity.setUpdateTime(null);
					consumptionInvoiceOrderChangeEntity.setUpdateUser(null);
					consumptionInvoiceOrderChangeService.add(consumptionInvoiceOrderChangeEntity);
					//修改订单状态为已完成
					orderService.updateOrderStatus("xxJob", consumptionInvoiceOrderEntity.getOrderNo(), 2);
					consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);
				}

				//依次取出json对象中data数组并保存到数据库
				JSONArray array = resultObj.getJSONArray("data");
				for (int i=0; i<array.size(); i++) {
					JSONObject object = array.getJSONObject(i);
					LogisticsInfoEntity logisInfo = new LogisticsInfoEntity();
					logisInfo.setOrderNo(consumptionInvoiceOrder.getOrderNo());
					logisInfo.setCourierNumber(resultObj.getString("nu"));
					logisInfo.setCourierCompanyName(logis.getCompanyName());
					logisInfo.setLogisticsInfo(object.getString("context"));
					logisInfo.setLogisticsTime(DateUtil.parseTimesTampDate(object.getString("ftime")));
					logisInfo.setLogisticsStatus(tranStatus(object.getString("status")));
					logisInfo.setAddTime(new Date());
					logisInfo.setAddUser("xxljob");
					logisInfo.setRemark("快递100接口获取快递信息为已签收");
					logisticsInfoService.insertSelective(logisInfo);
				}
			}
		}else{
			XxlJobLogger.log("查询无结果-->"+ consumptionInvoiceOrder.getCourierNumber()+","+consumptionInvoiceOrder.getCourierCompanyName());
		}
	}

	/**
	 * 调用快递100接口获取快递信息
	 * @param invOrder
	 */
	private void getExpress(InvoiceOrderDetailVO invOrder){
		if(StringUtils.isBlank(invOrder.getCourierCompanyName()) || StringUtils.isBlank(invOrder.getCourierNumber())){
			XxlJobLogger.log("订单号【"+invOrder.getOrderNo()+"】快递公司名称或快递订单不存在");
			return ;
		}
		//获取快递编码
		LogisCompanyEntity logis = new LogisCompanyEntity();
		logis.setCompanyName(invOrder.getCourierCompanyName());
		logis = logisCompanyService.selectOne(logis);
		if (null == logis) {
			XxlJobLogger.log("快递公司名称有误-->"+invOrder.getCourierCompanyName());
			return ;
		}

		//快递100查询快递信息
		String result = DeliveryUtils.synQueryData(sysDictionaryService.getByCode("kuaidi100_key").getDictValue(),
				sysDictionaryService.getByCode("kuaidi100_companyno").getDictValue(), logis.getCompanyCode(),
				invOrder.getCourierNumber(), null,null,null, 1);

		//解析快递100返回结果
		JSONObject resultObj = JSONObject.parseObject(result);
		if("ok".equals(resultObj.getString("message")) && "200".equals(resultObj.getString("status"))){

			//state	快递单当前状态，包括0在途，1揽收，2疑难，3签收，4退签，5派件，6退回等7个状态
			//如果是签收状态则修改订单状态为已完成
			if("3".equals(resultObj.getString("state"))){

				// 查询快递公司名称
				logis = new LogisCompanyEntity();
				logis.setCompanyCode(resultObj.getString("com"));
				logis = logisCompanyService.selectOne(logis);

				// 如果数据库状态为待收货则确认收货
				if(Objects.equals(invOrder.getOrderStatus(), InvoiceOrderStatusEnum.TO_BE_RECEIVED.getValue())){
					invoiceOrderService.confirmReceipt(invOrder.getUserId(), invOrder.getOemCode(), invOrder.getOrderNo(),"xxJob");
				}

				//依次取出json对象中data数组并保存到数据库
				JSONArray array = resultObj.getJSONArray("data");
				for (int i=0; i<array.size(); i++) {
					JSONObject object = array.getJSONObject(i);
					LogisticsInfoEntity logisInfo = new LogisticsInfoEntity();
					logisInfo.setOrderNo(invOrder.getOrderNo());
					logisInfo.setCourierNumber(resultObj.getString("nu"));
					logisInfo.setCourierCompanyName(logis.getCompanyName());
					logisInfo.setLogisticsInfo(object.getString("context"));
					logisInfo.setLogisticsTime(DateUtil.parseTimesTampDate(object.getString("ftime")));
					logisInfo.setLogisticsStatus(tranStatus(object.getString("status")));
					logisInfo.setAddTime(new Date());
					logisInfo.setAddUser("xxljob");
					logisInfo.setRemark("快递100接口获取快递信息为已签收");
					logisticsInfoService.insertSelective(logisInfo);
				}

				//发送短信通知
				MemberAccountEntity member = new MemberAccountEntity();
				member.setId(invOrder.getUserId());
				member = memberAccountService.selectOne(member);
				Map<String, Object> map = Maps.newHashMap();
				map.put("name", invOrder.getCourierCompanyName());
				map.put("number", invOrder.getCourierNumber());
				smsService.sendTemplateSms(member.getMemberPhone(), invOrder.getOemCode(), VerifyCodeTypeEnum.INVOICE_SIGN.getValue(), map, 1);
				//是否为他人办理 0-本人办理 1-为他人办理
				if(Objects.equals(invOrder.getIsOther(),1)){
					smsService.sendTemplateSms(invOrder.getOperatorTel(), invOrder.getOemCode(), VerifyCodeTypeEnum.INVOICE_SIGN.getValue(), map, 1);
				}


				// 推送回调数据
				if (null != member.getAccessPartyId()) {
					// 回调参数
					HashMap<String, Object> push = new HashMap<>();
					push.put("callbackType", 4); //回调类型 1-取消 2-出票 3-发货 4-完成
					push.put("orderNo", invOrder.getOrderNo());
					push.put("orderStatus", 7);
					// 发送推送消息
					invoiceOrderService.accessPartyPush(invOrder.getOrderNo(), member.getOemCode(), member.getAccessPartyId(), push);
				}
			}
		}else{
			XxlJobLogger.log("查询无结果-->"+ invOrder.getCourierNumber()+","+invOrder.getCourierCompanyName());
		}
	}

	/**
	 * 快递100明细状态转换
	 * @param status
	 * 0-待发货 1-已揽货 2-运输中 3-派送中 4-待取件  5-已签收 6-已收货 7-退货
	 */
	public Integer tranStatus(String status){
		if("在途".equals(status)){
			return 2;
		}else if("揽收".equals(status)){
			return 1;
		}else if("疑难".equals(status)){
			return 0;
		}else if("签收".equals(status)){
			return 5;
		}else if("退签".equals(status)){
			return 7;
		}else if("派件".equals(status)){
			return 3;
		}else if("退回".equals(status)){
			return 7;
		}else{
			return 1;
		}
	}

}
