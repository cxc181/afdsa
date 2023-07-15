package com.yuqian.itax.order.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.OrderAttachmentMapper;
import com.yuqian.itax.order.entity.OrderAttachmentEntity;
import com.yuqian.itax.order.service.OrderAttachmentService;
import org.springframework.stereotype.Service;


@Service("orderAttachmentService")
public class OrderAttachmentServiceImpl extends BaseServiceImpl<OrderAttachmentEntity,OrderAttachmentMapper> implements OrderAttachmentService {
	
}

