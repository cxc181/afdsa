package com.yuqian.itax.common.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {	
	
	
//	/**
//	 * 交易记录入库队列
//	 */
//	@Bean
//	public Queue upSuccessTradeOrderQueue(){
//		return createQueue("UP_SUCCESS_TRADE_ORDER_QUEUE");
//	}

	/**
	 * 测试
	 */
	@Bean
	public Queue test(){
		return createQueue("test");
	}

	@Bean
	public Queue GroupPaymentAnalysisRecordReceiver(){
		return createQueue("createGroupPaymentAnalysisRecord");
	}

	@Bean
	public Queue InvoiceOrderReceiver(){
		return createQueue("createInvoiceOrder");
	}
	@Bean
	public Queue MemberAutoUpdateReceiver(){
		return createQueue("memberAutoUpdate");
	}

	@Bean
	public Queue StatisticsMemberGeneralizeReceiver(){
		return createQueue("statisticsMemberGeneralize");
	}

	@Bean
	public Queue UploadVouchersReceiver(){
		return createQueue("uploadVouchers");
	}

	@Bean
	public Queue ParkTaxBillReceiver(){
		return createQueue("parkTaxBill");
	}
	@Bean
	public Queue RebuildParkTaxBillReceiver(){
		return createQueue("rebuildParkTaxBill");
	}
	@Bean
	public Queue ParkSendReceiver(){
		return createQueue("parkSend");
	}

	@Bean
	public Queue InvoicePdf2ImgReceiver(){
		return createQueue("invoicePdf2Img");
	}

	@Bean
	public Queue OpenInvoicingReceiver(){
		return createQueue("openInvoicing");
	}

	@Bean
	public Queue BwInvoiceIssueReceiver(){return createQueue("bwInvoiceIssue");}

	@Bean
	public Queue OrderPushReceiver(){return createQueue("orderPush");}

	@Bean
	public Queue CompanyAuthPushReceiver(){return createQueue("companyAuthPush");}

	@Bean
	public Queue ThirdPartyPushReceiver(){return createQueue("thirdPartyPush");}

	@Bean
	public Queue withdrawOrderYiShuiAuditQueue() {return createQueue("withdrawOrderYiShuiAuditQueue");}

	/**
	 * 创建新的队列
	 * 
	 * @param queueName
	 * @return
	 */
	private Queue createQueue(String queueName) {
		return new Queue(queueName);
	}
	
	
	/**
	 * 消费者数量
	 */
	public static final int DEFAULT_CONCURRENT = 20;
	
	/**
	 * 每个消费者获取最大投递数量 
	 */
	public static final int DEFAULT_PREFETCH_COUNT = 1;

	
	@Bean("pointTaskContainerFactory")
	public SimpleRabbitListenerContainerFactory pointTaskContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setPrefetchCount(DEFAULT_PREFETCH_COUNT);
		factory.setConcurrentConsumers(DEFAULT_CONCURRENT);
		factory.setMaxConcurrentConsumers(DEFAULT_CONCURRENT);
		configurer.configure(factory, connectionFactory);
		return factory;
	}
	
}
