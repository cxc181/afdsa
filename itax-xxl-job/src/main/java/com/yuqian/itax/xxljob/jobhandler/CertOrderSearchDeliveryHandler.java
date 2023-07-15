package com.yuqian.itax.xxljob.jobhandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.order.entity.LogisticsInfoEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.CompResApplyRecordOrderStatusEnum;
import com.yuqian.itax.order.service.LogisticsInfoService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.system.entity.LogisCompanyEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.LogisCompanyService;
import com.yuqian.itax.user.entity.CompanyResoucesApplyRecordEntity;
import com.yuqian.itax.user.service.CompanyResoucesApplyRecordService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.DeliveryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 证件领用待收货订单调用快递100接口获取快递信息，将待收货改为已签收任务启动 每30分钟执行一次任务
 * @Author yejian
 * @Date 2020/03/27 14:28
 * @return
 */
@JobHandler(value="certOrderSearchDeliveryHandler")
@Component
public class CertOrderSearchDeliveryHandler extends IJobHandler {
	@Autowired
	private DictionaryService sysDictionaryService;
	@Autowired
	private CompanyResoucesApplyRecordService companyResoucesApplyRecordService;
	@Autowired
	private LogisCompanyService logisCompanyService;
	@Autowired
	private LogisticsInfoService logisticsInfoService;
	@Autowired
	private OrderService orderService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		long t1 = System.currentTimeMillis(); // 代码执行前时间
		XxlJobLogger.log("===========>证件领用待收货订单调用快递100接口获取快递信息，将待收货改为已签收任务启动");
		//查询待收货的证件领用订单和快递表未保存数据的已签收的证件领用订单
		List<CompanyResoucesApplyRecordEntity> certOrderList = companyResoucesApplyRecordService.listCompResApplyRecordToRec();
		if(certOrderList== null || certOrderList.size()<1){
			return SUCCESS;
		}
		certOrderList.stream().forEach(certOrder ->{
			XxlJobLogger.log("orderNo-->"+certOrder.getOrderNo());
			getExpress(certOrder);
		});
		XxlJobLogger.log("===========>证件领用待收货订单调用快递100接口获取快递信息，将待收货改为已签收任务结束");
		long t2 = System.currentTimeMillis(); // 代码执行后时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t2 - t1);
		XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
		return SUCCESS;
	}

	/**
	 * 调用快递100接口获取快递信息
	 * @param certOrder
	 */
	private void getExpress(CompanyResoucesApplyRecordEntity certOrder){
		//获取快递编码
		LogisCompanyEntity logis = new LogisCompanyEntity();
		logis.setCompanyName(certOrder.getCourierCompanyName());
		logis = logisCompanyService.selectOne(logis);
		if (null == logis) {
			XxlJobLogger.log("快递公司名称有误-->"+certOrder.getCourierCompanyName());
			return;
		}

		//快递100查询快递信息
		String result = DeliveryUtils.synQueryData(sysDictionaryService.getByCode("kuaidi100_key").getDictValue(),
				sysDictionaryService.getByCode("kuaidi100_companyno").getDictValue(), logis.getCompanyCode(),
				certOrder.getCourierNumber(), null,null,null, 1);

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

				//如果数据库状态为待收货则确认收货
				if(Objects.equals(certOrder.getStatus(), CompResApplyRecordOrderStatusEnum.TO_BE_RECEIVED.getValue())){
					// 查询主表订单信息的userId
					OrderEntity order = orderService.queryByOrderNo(certOrder.getOrderNo());
					orderService.certUseConfirm(order.getUserId(), certOrder.getOemCode(), certOrder.getOrderNo());
				}

				//依次取出json对象中data数组并保存到数据库
				JSONArray array = resultObj.getJSONArray("data");
				for (int i=0; i<array.size(); i++) {
					JSONObject object = array.getJSONObject(i);
					LogisticsInfoEntity logisInfo = new LogisticsInfoEntity();
					logisInfo.setOrderNo(certOrder.getOrderNo());
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
			XxlJobLogger.log("查询无结果-->"+ certOrder.getCourierNumber()+","+certOrder.getCourierCompanyName());
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
