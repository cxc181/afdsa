package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.MemberAccountChangeEntity;
import com.yuqian.itax.user.dao.MemberAccountChangeMapper;

import java.util.List;

/**
 * 会员账号变动表service
 * 
 * @Date: 2021年02月03日 11:25:34 
 * @author 蒋匿
 */
public interface MemberAccountChangeService extends IBaseService<MemberAccountChangeEntity,MemberAccountChangeMapper> {

    /**
     * 查询手机号码修改记录
     */
    List<MemberAccountChangeEntity> queryMemberPhoneChange(Long id);
}

