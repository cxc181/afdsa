package com.yuqian.itax.xxljob.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 定时刷新机构信息 10分钟执行一次
 * @author：jiangni
 * @Date：219-12-26
 * @version：1.0
 */
@JobHandler(value="oemInfoCheckHandler")
@Component
public class OEMInfoCheckHandler extends IJobHandler {

	@Autowired
	private OemService oemService;

	@Autowired
	private RedisService redisService;

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("=========定时刷新机构信息任务启动");
		OemEntity entity = new OemEntity();
		entity.setOemStatus(1);
		List<OemEntity> list =  oemService.select(entity);
		list.forEach(vo -> redisService.set(RedisKey.OEM_CODE_KEY + vo.getOemCode(),vo,10*60));
		XxlJobLogger.log("=========定时刷新机构信息任务结束");
		return SUCCESS;
	}
	
}
