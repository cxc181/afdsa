package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.entity.MemberOrderRelaEntity;
import com.yuqian.itax.order.dao.MemberOrderRelaMapper;

/**
 * 会员订单关系表service
 * 
 * @Date: 2019年12月10日 12:19:24 
 * @author 蒋匿
 */
public interface MemberOrderRelaService extends IBaseService<MemberOrderRelaEntity,MemberOrderRelaMapper> {
    /**
     * @Description 根据用户ID查询会员订单关系
     * @Author  Kaven
     * @Date   2019/12/10 16:20
     * @Param  userId
     * @Return MemberOrderRelaEntity
    */
    MemberOrderRelaEntity findByMemberId(Long userId);
    /**
     * 根据一级推广人修改会员一级推广人账号
     */
    void updateMemberAccountByFirstMemberId(String memberAccount,String remark,Long memberId);
    /**
     * 根据二级推广人修改会员二级推广人账号
     */
    void updateMemberAccountByTwoMemberId(String memberAccount,String remark,Long memberId);
    /**
     * 根据三级推广人修改会员三级推广人账号
     */
    void updateMemberAccountByThreeMemberId(String memberAccount,String remark,Long memberId);
    /**
     * 根据四级推广人修改会员四级推广人账号
     */
    void updateMemberAccountByFourMemberId(String memberAccount,String remark,Long memberId);

    /**
     * 根据上级钻石id修改上级钻石账号
     */
    void updateOrderRelaUpDiamondAccountByUpDiamondAccountId(String memberAccount,String remark,Long memberId);

    /**
     * 根据上上级钻石id修改上上级钻石账号
     */
    void updateOrderRelaSuperDiamondAccountBySuperDiamondId(String memberAccount,String remark,Long memberId);

    /**
     * 根据所属员工id修改所属员工账号
     */
    void updateOrderRelaEmployeesAccountByEmployeesId(String memberAccount,String remark,Long memberId);

    /**
     * 根据上上级员工id修改上上级员工账号
     */
    void updateOrderRelaSuperEmployeesAccountBySuperDiamondId(String memberAccount,String remark,Long memberId);

    /**
     * 根据邀请人id修改邀请人账号
     */
    void updateOrderRelaParentMemberAccountByParentMemberId(String memberAccount,String remark,Long memberId);

}

