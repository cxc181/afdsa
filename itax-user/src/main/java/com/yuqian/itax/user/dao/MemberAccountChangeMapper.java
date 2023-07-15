package com.yuqian.itax.user.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.MemberAccountChangeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员账号变动表dao
 * 
 * @Date: 2021年02月03日 11:25:34 
 * @author 蒋匿
 */
@Mapper
public interface MemberAccountChangeMapper extends BaseMapper<MemberAccountChangeEntity> {

    /**
     * 查询手机号码修改记录
     */
    List<MemberAccountChangeEntity> queryMemberPhoneChange(@Param("id")Long id);
}

