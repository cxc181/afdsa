package com.yuqian.itax.roles.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.roles.entity.UserRoleRelaEntity;
import com.yuqian.itax.roles.dao.UserRoleRelaMapper;

import java.util.List;

/**
 * 用户与角色的关系表service
 * 
 * @Date: 2019年12月08日 20:59:38 
 * @author 蒋匿
 */
public interface UserRoleRelaService extends IBaseService<UserRoleRelaEntity,UserRoleRelaMapper> {

    /**
     * 更具roleId查询未注销的系统用户账号
     */
    List<UserRoleRelaEntity> queryUserRoleRelaEntityByRoleId(Long roleId);
}

