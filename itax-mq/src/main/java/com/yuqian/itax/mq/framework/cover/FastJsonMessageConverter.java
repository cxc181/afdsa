package com.yuqian.itax.mq.framework.cover;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import java.io.UnsupportedEncodingException;

@SuppressWarnings("unchecked")
public class FastJsonMessageConverter extends AbstractMessageConverter {

	private static Log logger = LogFactory.getLog(FastJsonMessageConverter.class);

	public static final String DEFAULT_CHARSET = "UTF-8";

	private static volatile String defaultCharset = DEFAULT_CHARSET;
	
	public FastJsonMessageConverter() {
		super();
	}
	
	public void setDefaultCharset(String defaultCharset) {
		FastJsonMessageConverter.defaultCharset = (defaultCharset != null) ? defaultCharset : DEFAULT_CHARSET;
	}

	@Override
	public Object fromMessage(Message message)
			throws MessageConversionException {
		return null;
	}
	
	public <T> T fromMessage(Message message, T t) {
		String json = "";
		try {
			json = new String(message.getBody(),defaultCharset);
		} catch (UnsupportedEncodingException e) {
			logger.error("",e);
		}
		return (T) JSON.parseObject(json, t.getClass());
	}	
	
	public static <T> T fromMessage(byte[] body,T t) {
		String json = "";
		try {
			json = new String(body,defaultCharset);
		} catch (UnsupportedEncodingException e) {
			logger.error("",e);
		}
		return (T) JSON.parseObject(json, t.getClass());
	}	
	
	public static <T> T fromMessage(byte[] body,Class<?> clazz) {
		String json = "";
		try {
			json = new String(body,defaultCharset);
		} catch (UnsupportedEncodingException e) {
			logger.error("",e);
		}
		return (T) JSON.parseObject(json, clazz);
	}	
	

	@SuppressWarnings("static-access")
	@Override
	protected Message createMessage(Object objectToConvert,
                                    MessageProperties messageProperties)
			throws MessageConversionException {
		byte[] bytes = null;
		try {
			String jsonString = JSON.toJSONString(objectToConvert);
			bytes = jsonString.getBytes(defaultCharset);
		} catch (UnsupportedEncodingException e) {
			throw new MessageConversionException(
					"Failed to convert Message content", e);
		} 
		messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		messageProperties.setContentEncoding(defaultCharset);
		if (bytes != null) {
			messageProperties.setContentLength(bytes.length);
		}
		return new Message(bytes, messageProperties);

	}

}
