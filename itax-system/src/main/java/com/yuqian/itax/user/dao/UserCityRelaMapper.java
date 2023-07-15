package com.yuqian.itax.user.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.UserCityRelaEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户与城市的关系dao
 * 
 * @Date: 2019年12月08日 21:08:59 
 * @author 蒋匿
 */
@Mapper
public interface UserCityRelaMapper extends BaseMapper<UserCityRelaEntity> {
    /**
     * 根据USERID查询用户与城市的关系
     */
    UserCityRelaEntity queryUserCityRelaEntityByUserId(Long userId);
}

