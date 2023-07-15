package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.UserRelaMapper;
import com.yuqian.itax.user.entity.UserRelaEntity;
import com.yuqian.itax.user.enums.UserLevelEnum;
import com.yuqian.itax.user.service.UserRelaService;
import org.springframework.stereotype.Service;


@Service("userRelaService")
public class UserRelaServiceImpl extends BaseServiceImpl<UserRelaEntity,UserRelaMapper> implements UserRelaService {

    @Override
    public UserRelaEntity queryUserRelaEntityByUserId(Long userId,Integer userClass) {
        return mapper.queryUserRelaEntityByUserId(userId,userClass);
    }

    @Override
    public UserRelaEntity queryUserRelaEntity(Long userId, String userTree, Integer userClass) {
        return mapper.queryUserRelaEntity(userId, userTree, userClass);
    }

    @Override
    public UserRelaEntity querySystemUserRelaEntity(Long userId, String userTree) {
        return mapper.querySystemUserRelaEntity(userId, userTree, UserLevelEnum.MEMBER.getValue());
    }
}

