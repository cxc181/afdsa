package com.yuqian.itax.agent.dao;

import com.yuqian.itax.agent.entity.ApiRequestMessageRecordEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * api接口请求报文记录dao
 * 
 * @Date: 2020年07月20日 17:44:57 
 * @author 蒋匿
 */
@Mapper
public interface ApiRequestMessageRecordMapper extends BaseMapper<ApiRequestMessageRecordEntity> {
	
}

