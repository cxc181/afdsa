package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.UserTypeEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户类型管理dao
 * 
 * @Date: 2019年12月08日 20:51:13 
 * @author 蒋匿
 */
@Mapper
public interface UserTypeMapper extends BaseMapper<UserTypeEntity> {
	
}

