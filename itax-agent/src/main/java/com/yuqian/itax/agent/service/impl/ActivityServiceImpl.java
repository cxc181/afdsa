package com.yuqian.itax.agent.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.agent.dao.ActivityMapper;
import com.yuqian.itax.agent.entity.ActivityEntity;
import com.yuqian.itax.agent.service.ActivityService;
import org.springframework.stereotype.Service;


@Service("activityService")
public class ActivityServiceImpl extends BaseServiceImpl<ActivityEntity,ActivityMapper> implements ActivityService {
	
}

