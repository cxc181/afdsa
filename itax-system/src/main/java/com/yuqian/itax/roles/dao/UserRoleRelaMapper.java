package com.yuqian.itax.roles.dao;

import com.yuqian.itax.roles.entity.UserRoleRelaEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户与角色的关系表dao
 * 
 * @Date: 2019年12月08日 20:59:38 
 * @author 蒋匿
 */
@Mapper
public interface UserRoleRelaMapper extends BaseMapper<UserRoleRelaEntity> {
    List<UserRoleRelaEntity> queryUserRoleRelaEntityByRoleId(@Param("roleId")Long roleId);

    UserRoleRelaEntity queryUserRoleRelaEntityByUserId(@Param("userId")Long userId);

}

