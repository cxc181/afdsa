package com.yuqian.itax.xxljob.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderChangeEntity;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderEntity;
import com.yuqian.itax.order.entity.LogisticsInfoEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.vo.ConsumptionInvoiceOrderJobVO;
import com.yuqian.itax.order.enums.InvoiceOrderStatusEnum;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.util.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 开票订单7天自动签收 每天凌晨00:01执行任务
 * @Author ni.jiang
 * @Date 2019/12/28 15:34
 * @return
 */
@JobHandler(value="invoiceOrderAutoReceivedHandler")
@Component
public class InvoiceOrderAutoReceivedHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;
	@Autowired
	private InvoiceOrderService invoiceOrderService;
	@Autowired
	private ConsumptionInvoiceOrderService consumptionInvoiceOrderService;
	@Autowired
	private ConsumptionInvoiceOrderChangeService consumptionInvoiceOrderChangeService;
	@Autowired
	private LogisticsInfoService logisticsInfoService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		long t1 = System.currentTimeMillis(); // 代码执行前时间
		XxlJobLogger.log("=========开票订单7天自动签收任务启动");
		OrderEntity mainOrder = new OrderEntity();
		mainOrder.setOrderType(OrderTypeEnum.INVOICE.getValue());
		mainOrder.setOrderStatus(InvoiceOrderStatusEnum.TO_BE_RECEIVED.getValue()); //查询待收货的开票订单
		List<OrderEntity> orderList = orderService.select(mainOrder);
		//待签收的消费订单
		List<ConsumptionInvoiceOrderJobVO> consumptionInvoiceOrderList = consumptionInvoiceOrderService.listConsumptionInvoiceByStatus();
		if((orderList== null || orderList.size()<1) && (consumptionInvoiceOrderList== null || consumptionInvoiceOrderList.size()<1)){
			return SUCCESS;
		}
		Date currentDate = DateUtil.getCurrentDate();
		if (orderList != null && orderList.size()>0){
			orderList.stream().forEach(order ->{
				if(DateUtil.diffDate(currentDate,order.getUpdateTime()) >=7 ){
					XxlJobLogger.log("orderNo-->"+order.getOrderNo());
					invoiceOrderService.confirmReceipt(order.getUserId(), order.getOemCode(), order.getOrderNo(),"xxJob");

				}
			});
		}
		XxlJobLogger.log("=========开票订单7天自动签收任务结束");
 		if (consumptionInvoiceOrderList != null && consumptionInvoiceOrderList.size()>0){
			consumptionInvoiceOrderList.stream().forEach(consumptionInvoiceOrder ->{
				if(DateUtil.diffDate(currentDate,consumptionInvoiceOrder.getUpdateTime()) >=10 ){
					XxlJobLogger.log("orderNo-->"+consumptionInvoiceOrder.getOrderNo());
					ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity = new ConsumptionInvoiceOrderEntity();
					consumptionInvoiceOrderEntity.setOrderNo(consumptionInvoiceOrder.getOrderNo());
					consumptionInvoiceOrderEntity = consumptionInvoiceOrderService.selectOne(consumptionInvoiceOrderEntity);
					Date date =  new Date();
					consumptionInvoiceOrderEntity.setUpdateTime(date);
					consumptionInvoiceOrderEntity.setUpdateUser("xxJob");
					consumptionInvoiceOrderEntity.setCompleteTime(date);
					consumptionInvoiceOrderEntity.setRemark("物流信息超时自动签收");
					ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity=new ConsumptionInvoiceOrderChangeEntity();
					BeanUtils.copyProperties(consumptionInvoiceOrderEntity,consumptionInvoiceOrderChangeEntity);
					consumptionInvoiceOrderChangeEntity.setStatus(2);
					consumptionInvoiceOrderChangeEntity.setId(null);
					consumptionInvoiceOrderChangeEntity.setAddUser("xxJob");
					consumptionInvoiceOrderChangeEntity.setAddTime(new Date());
					consumptionInvoiceOrderChangeEntity.setUpdateTime(null);
					consumptionInvoiceOrderChangeEntity.setUpdateUser(null);
					consumptionInvoiceOrderChangeEntity.setRemark("物流信息超时自动签收");
					consumptionInvoiceOrderChangeService.add(consumptionInvoiceOrderChangeEntity);
					//修改订单状态为已完成
					orderService.updateOrderStatus("xxJob", consumptionInvoiceOrderEntity.getOrderNo(), 2);
					consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);
					//保存超时自动完成的物流信息
					LogisticsInfoEntity logisInfo = new LogisticsInfoEntity();
					logisInfo.setOrderNo(consumptionInvoiceOrder.getOrderNo());
					logisInfo.setCourierNumber(consumptionInvoiceOrder.getCourierNumber());
					logisInfo.setCourierCompanyName(consumptionInvoiceOrder.getCourierCompanyName());
					logisInfo.setLogisticsInfo("物流信息超时自动签收");
					logisInfo.setLogisticsTime(new Date());
					logisInfo.setLogisticsStatus(6);
					logisInfo.setAddTime(new Date());
					logisInfo.setAddUser("xxljob");
					logisInfo.setRemark("物流信息超时自动签收");
					logisticsInfoService.insertSelective(logisInfo);
				}
			});
		}
		XxlJobLogger.log("=========消费开票订单10天自动签收任务结束");
		long t2 = System.currentTimeMillis(); // 代码执行后时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t2 - t1);
		XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
		return SUCCESS;
	}
	
}
