package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.MemberAccountChangeMapper;
import com.yuqian.itax.user.entity.MemberAccountChangeEntity;
import com.yuqian.itax.user.service.MemberAccountChangeService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("memberAccountChangeService")
public class MemberAccountChangeServiceImpl extends BaseServiceImpl<MemberAccountChangeEntity,MemberAccountChangeMapper> implements MemberAccountChangeService {

    @Override
    public List<MemberAccountChangeEntity> queryMemberPhoneChange(Long id) {
        return mapper.queryMemberPhoneChange(id);
    }
}

