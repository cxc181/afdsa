package com.yuqian.itax.message.dao;

import com.yuqian.itax.message.entity.SmsEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信记录表dao
 * 
 * @Date: 2019年12月08日 20:44:18 
 * @author 蒋匿
 */
@Mapper
public interface SmsMapper extends BaseMapper<SmsEntity> {
	
}

