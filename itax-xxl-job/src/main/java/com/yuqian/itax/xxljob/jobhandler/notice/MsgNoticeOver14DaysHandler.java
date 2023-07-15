package com.yuqian.itax.xxljob.jobhandler.notice;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/10 10:06
 *  @Description: 批量修改通知弹窗调度任务：超过14天未读的消息通知改为已弹窗
 */
@Slf4j
@JobHandler(value="msgNoticeOver14DaysHandler")
@Component
public class MsgNoticeOver14DaysHandler extends IJobHandler {

	@Autowired
	private MessageNoticeService messageNoticeService;

	@Autowired
	private DictionaryService dictionaryService;

	private static final Integer DEFAULT_OVER_DAYS = 14;// 默认超时天数

	@Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("批量修改通知弹窗调度任务启动");
		Integer overDays = DEFAULT_OVER_DAYS;

		// 查询超过配置天数的未读消息列表
		DictionaryEntity dict = this.dictionaryService.getByCode("notice_over_days");
		if(null == dict){
			XxlJobLogger.log("系统未配置超时弹框有效天数，取默认配置");
		}else{
		    // 取系统配置天数
		    overDays = Integer.parseInt(dict.getDictValue());
        }
		List<MessageNoticeEntity> unReadList = messageNoticeService.selectUnReadList(overDays);

		if (CollectionUtils.isEmpty(unReadList)) {
			XxlJobLogger.log("没有超过14天的未读消息记录，任务结束");
			return SUCCESS;
		}

		// 遍历集合，批量修改弹窗状态为“已弹窗”
		for(MessageNoticeEntity notice : unReadList){
			try {
				MessageNoticeEntity entity = new MessageNoticeEntity();
				entity.setId(notice.getId());
				entity.setUpdateTime(new Date());
				entity.setUpdateUser("admin");
				entity.setIsAlert(1);
				this.messageNoticeService.editByIdSelective(entity);
			} catch (Exception e) {
				XxlJobLogger.log("通知对象{} 更新失败:{} ", JSON.toJSONString(notice),e.getMessage());
				XxlJobLogger.log(e);
			}
		}
		XxlJobLogger.log("批量修改通知弹窗调度任务结束");
		return SUCCESS;
	}
}
