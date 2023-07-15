package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.UserExtendEntity;
import com.yuqian.itax.user.dao.UserExtendMapper;

/**
 * 用户扩展表service
 * 
 * @Date: 2019年12月08日 20:51:30 
 * @author 蒋匿
 */
public interface UserExtendService extends IBaseService<UserExtendEntity,UserExtendMapper> {

    public  UserExtendEntity getUserExtendByUserId(Long userId);
}

