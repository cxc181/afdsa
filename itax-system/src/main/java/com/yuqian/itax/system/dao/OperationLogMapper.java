package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.OperationLogEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志dao
 * 
 * @Date: 2019年12月08日 20:36:31 
 * @author 蒋匿
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLogEntity> {
	
}

