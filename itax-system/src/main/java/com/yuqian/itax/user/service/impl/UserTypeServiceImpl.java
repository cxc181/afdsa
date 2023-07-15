package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.UserTypeMapper;
import com.yuqian.itax.user.entity.UserTypeEntity;
import com.yuqian.itax.user.service.UserTypeService;
import org.springframework.stereotype.Service;


@Service("userTypeService")
public class UserTypeServiceImpl extends BaseServiceImpl<UserTypeEntity,UserTypeMapper> implements UserTypeService {
	
}

