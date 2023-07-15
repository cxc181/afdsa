package com.yuqian.itax.mq.framework.common;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基础Sender 所有的sender继承与它
 * 
 * @author 刘献廷
 *
 */
public class BaseSender {
	
	@Autowired
	protected AmqpTemplate rabbitTemplate;
}
