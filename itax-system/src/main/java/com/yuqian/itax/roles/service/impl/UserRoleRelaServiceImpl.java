package com.yuqian.itax.roles.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.roles.dao.UserRoleRelaMapper;
import com.yuqian.itax.roles.entity.UserRoleRelaEntity;
import com.yuqian.itax.roles.service.UserRoleRelaService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("userRoleRelaService")
public class UserRoleRelaServiceImpl extends BaseServiceImpl<UserRoleRelaEntity,UserRoleRelaMapper> implements UserRoleRelaService {

    @Override
    public List<UserRoleRelaEntity> queryUserRoleRelaEntityByRoleId(Long roleId) {
        return mapper.queryUserRoleRelaEntityByRoleId(roleId);
    }
}

