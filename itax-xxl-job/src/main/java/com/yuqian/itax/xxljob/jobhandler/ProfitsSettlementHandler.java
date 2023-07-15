package com.yuqian.itax.xxljob.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.enums.OemSettlementTypeEnum;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.profits.entity.ProfitsDetailEntity;
import com.yuqian.itax.profits.enums.ProfitsDetailStatusEnum;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.user.enums.MemberLevelEnum;
import com.yuqian.itax.util.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


/**
 *  分润结算 每天凌晨00:30执行
 * @author：jiangni
 * @Date：219-12-26
 * @version：1.0
 */
@JobHandler(value="profitsSettlementHandler")
@Component
public class ProfitsSettlementHandler extends IJobHandler {

	@Autowired
	private OemService oemService;

	@Autowired
	private ProfitsDetailService profitsDetailService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		long t1 = System.currentTimeMillis(); // 代码执行前时间
		XxlJobLogger.log("=======分润结算任务启动");
		//获取当前需要结算的机构
		List<OemEntity> list = oemService.findOemInfosBySettlementCycle();
		for(OemEntity vo : list){
			XxlJobLogger.log("oemCode-->"+vo.getOemCode());
			profitsSettlement(vo);
		}
		XxlJobLogger.log("=======分润结算任务结束");
		long t2 = System.currentTimeMillis(); // 代码执行后时间
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t2 - t1);
		XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
		return SUCCESS;
	}

	/**
	 * 分润结算
	 * @param vo
	 */
	private void profitsSettlement(OemEntity vo){
		Date startTime = null;
		Date endTime = null;
		//获取机构配置的结算周期
		if(OemSettlementTypeEnum.BY_WEEK.getValue().equals(vo.getSettlementType())){
            int week = DateUtil.getWeek(new Date());
            if(week != vo.getSettlementCycle()){
                return ;
            }
			//startTime = new Date(DateUtil.getLastWeekStartTime());//上周开始时间
			endTime = new Date(DateUtil.getLastWeekEndTime()*1000); //上周结束时间
		}else if(OemSettlementTypeEnum.BY_MONTH.getValue().equals(vo.getSettlementType())){
		    int day = DateUtil.getDay(new Date());
		    if(day != vo.getSettlementCycle()){
		        return ;
            }
			//startTime = new Date(DateUtil.getLastMonStartTime()); //上月开始时间
			endTime = new Date(DateUtil.getLastMonEndTime()*1000); //上月结束时间
		}
		//根据周期获取分润明细表中分润金额和分润用户
		Map<String,Object> params = new HashMap<>();
		params.put("startTime",startTime);
		params.put("endTime",endTime);
		params.put("oemCode",vo.getOemCode());
		params.put("profitsStatus",ProfitsDetailStatusEnum.PROFITS_SETTLEMENT_WAIT.getValue());
		List<ProfitsDetailEntity> profitsDetailList = profitsDetailService.findProfitsDetailByParams(params);
		if(profitsDetailList == null || profitsDetailList.size()==0){
			return ;
		}
		//根据用户类型和用户id进行分组
		Map<Integer,Map<Long,List<ProfitsDetailEntity>>> userTypeUserIdGroup = profitsDetailList.parallelStream().collect(Collectors.groupingBy(ProfitsDetailEntity::getUserType,Collectors.groupingBy(ProfitsDetailEntity::getUserId)));
		//将分润用户的资金账号待分润金额划分到可用金额
		userTypeUserIdGroup.forEach((type,map)->{
			map.forEach((userId,list)->{
				for(ProfitsDetailEntity entity : list){
					if(entity.getUserType()==4 || (entity.getUserType()==1 && entity.getUserLevel().intValue() != MemberLevelEnum.DIAMOND.getValue().intValue())){
						continue;
					}
					profitsDetailService.updateProfitsDetailStatus(entity,"admin");
				};
			});
		});
	}
}
