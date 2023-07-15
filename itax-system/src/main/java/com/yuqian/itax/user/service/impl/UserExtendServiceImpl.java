package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.UserExtendMapper;
import com.yuqian.itax.user.entity.UserExtendEntity;
import com.yuqian.itax.user.service.UserExtendService;
import org.springframework.stereotype.Service;


@Service("userExtendService")
public class UserExtendServiceImpl extends BaseServiceImpl<UserExtendEntity,UserExtendMapper> implements UserExtendService {

    @Override
    public UserExtendEntity getUserExtendByUserId(Long userId) {
        return mapper.getUserExtendByUserId(userId);
    }
}

