package com.yuqian.itax.order.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.RegisterOrderChangeRecordMapper;
import com.yuqian.itax.order.entity.RegisterOrderChangeRecordEntity;
import com.yuqian.itax.order.service.RegisterOrderChangeRecordService;
import org.springframework.stereotype.Service;


@Service("registerOrderChangeRecordService")
public class RegisterOrderChangeRecordServiceImpl extends BaseServiceImpl<RegisterOrderChangeRecordEntity,RegisterOrderChangeRecordMapper> implements RegisterOrderChangeRecordService {

}

