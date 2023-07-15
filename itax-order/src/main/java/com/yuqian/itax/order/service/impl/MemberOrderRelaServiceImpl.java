package com.yuqian.itax.order.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.MemberOrderRelaMapper;
import com.yuqian.itax.order.entity.MemberOrderRelaEntity;
import com.yuqian.itax.order.service.MemberOrderRelaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("memberOrderRelaService")
public class MemberOrderRelaServiceImpl extends BaseServiceImpl<MemberOrderRelaEntity,MemberOrderRelaMapper> implements MemberOrderRelaService {
    @Resource
    private MemberOrderRelaMapper memberOrderRelaMapper;

    @Override
    public MemberOrderRelaEntity findByMemberId(Long userId) {
        return this.memberOrderRelaMapper.findByMemberId(userId);
    }

    @Override
    public void updateMemberAccountByFirstMemberId(String memberAccount, String remark, Long memberId) {
        mapper.updateMemberAccountByFirstMemberId(memberAccount,remark,memberId);
    }

    @Override
    public void updateMemberAccountByTwoMemberId(String memberAccount, String remark, Long memberId) {
        mapper.updateMemberAccountByTwoMemberId(memberAccount,remark,memberId);
    }

    @Override
    public void updateMemberAccountByThreeMemberId(String memberAccount, String remark, Long memberId) {
        mapper.updateMemberAccountByThreeMemberId(memberAccount,remark,memberId);
    }

    @Override
    public void updateMemberAccountByFourMemberId(String memberAccount, String remark, Long memberId) {
        mapper.updateMemberAccountByFourMemberId(memberAccount,remark,memberId);
    }

    @Override
    public void updateOrderRelaUpDiamondAccountByUpDiamondAccountId(String memberAccount, String remark, Long memberId) {
        mapper.updateOrderRelaUpDiamondAccountByUpDiamondAccountId(memberAccount,remark,memberId);
    }

    @Override
    public void updateOrderRelaSuperDiamondAccountBySuperDiamondId(String memberAccount, String remark, Long memberId) {
        mapper.updateOrderRelaSuperDiamondAccountBySuperDiamondId(memberAccount,remark,memberId);
    }

    @Override
    public void updateOrderRelaEmployeesAccountByEmployeesId(String memberAccount, String remark, Long memberId) {
        mapper.updateOrderRelaEmployeesAccountByEmployeesId(memberAccount,remark,memberId);
    }

    @Override
    public void updateOrderRelaSuperEmployeesAccountBySuperDiamondId(String memberAccount, String remark, Long memberId) {
        mapper.updateOrderRelaSuperEmployeesAccountBySuperDiamondId(memberAccount,remark,memberId);
    }

    @Override
    public void updateOrderRelaParentMemberAccountByParentMemberId(String memberAccount, String remark, Long memberId) {
        mapper.updateOrderRelaParentMemberAccountByParentMemberId(memberAccount,remark,memberId);
    }
}

