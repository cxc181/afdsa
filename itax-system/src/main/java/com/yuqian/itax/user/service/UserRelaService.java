package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.UserRelaEntity;
import com.yuqian.itax.user.dao.UserRelaMapper;

/**
 * 用户关系表service
 * 
 * @Date: 2019年12月18日 14:04:55 
 * @author 蒋匿
 */
public interface UserRelaService extends IBaseService<UserRelaEntity,UserRelaMapper> {
    /**
     * 根据userID查询用户关系表
     */
    UserRelaEntity queryUserRelaEntityByUserId(Long userId,Integer userClass);

    /**
     * 根据userID，和用户树查询用户关系表
     */
    UserRelaEntity queryUserRelaEntity(Long userId, String userTree, Integer userClass);

    /**
     * 查询系统用户关系表
     * @param userId
     * @param userTree
     * @return
     */
    UserRelaEntity querySystemUserRelaEntity(Long userId, String userTree);
}

