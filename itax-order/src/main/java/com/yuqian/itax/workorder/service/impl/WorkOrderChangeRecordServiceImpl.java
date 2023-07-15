package com.yuqian.itax.workorder.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.workorder.dao.WorkOrderChangeRecordMapper;
import com.yuqian.itax.workorder.entity.WorkOrderChangeRecordEntity;
import com.yuqian.itax.workorder.service.WorkOrderChangeRecordService;
import org.springframework.stereotype.Service;


@Service("workOrderChangeRecordService")
public class WorkOrderChangeRecordServiceImpl extends BaseServiceImpl<WorkOrderChangeRecordEntity,WorkOrderChangeRecordMapper> implements WorkOrderChangeRecordService {
	
}

