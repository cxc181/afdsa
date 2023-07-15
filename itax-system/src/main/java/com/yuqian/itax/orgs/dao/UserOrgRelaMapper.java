package com.yuqian.itax.orgs.dao;

import com.yuqian.itax.orgs.entity.UserOrgRelaEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户组织关系表dao
 * 
 * @Date: 2019年12月08日 20:57:22 
 * @author 蒋匿
 */
@Mapper
public interface UserOrgRelaMapper extends BaseMapper<UserOrgRelaEntity> {
    /**
     * 更具UserId获取
     */
    UserOrgRelaEntity getUserOrgRelaByUserId(@Param("userId") Long userId);
}

