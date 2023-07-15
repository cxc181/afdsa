package com.yuqian.itax.order.dao;

import com.yuqian.itax.order.entity.MemberOrderRelaEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员订单关系表dao
 * 
 * @Date: 2019年12月10日 12:19:24 
 * @author 蒋匿
 */
@Mapper
public interface MemberOrderRelaMapper extends BaseMapper<MemberOrderRelaEntity> {
    /**
     * @Description 根据用户ID查询会员订单关系
     * @Author  Kaven
     * @Date   2019/12/10 16:21
     * @Param  userId
     * @Return MemberOrderRelaEntity
    */
    MemberOrderRelaEntity findByMemberId(Long userId);
    /**
     * 根据一级推广人修改会员一级推广人账号
     */
    void updateMemberAccountByFirstMemberId(@Param("memberAccount") String memberAccount, @Param("remark")  String remark, @Param("memberId") Long memberId);

    /**
     * 根据二级推广人修改会员二级推广人账号
     */
    void updateMemberAccountByTwoMemberId(@Param("memberAccount") String memberAccount, @Param("remark")  String remark, @Param("memberId") Long memberId);

    /**
     * 根据三级推广人修改会员三级推广人账号
     */
    void updateMemberAccountByThreeMemberId(@Param("memberAccount") String memberAccount, @Param("remark")  String remark, @Param("memberId") Long memberId);

    /**
     * 根据四级推广人修改会员四级推广人账号
     */
    void updateMemberAccountByFourMemberId(@Param("memberAccount") String memberAccount, @Param("remark")  String remark, @Param("memberId") Long memberId);

    /**
     * 根据上级钻石id修改上级钻石账号
     */
    void updateOrderRelaUpDiamondAccountByUpDiamondAccountId(@Param("memberAccount") String memberAccount, @Param("remark")  String remark, @Param("memberId") Long memberId);

    /**
     * 根据上上级钻石id修改上上级钻石账号
     */
    void updateOrderRelaSuperDiamondAccountBySuperDiamondId(@Param("memberAccount") String memberAccount, @Param("remark")  String remark, @Param("memberId") Long memberId);

    /**
     * 根据所属员工id修改所属员工账号
     */
    void updateOrderRelaEmployeesAccountByEmployeesId(@Param("memberAccount") String memberAccount, @Param("remark")  String remark, @Param("memberId") Long memberId);

    /**
     * 根据上上级员工id修改上上级员工账号
     */
    void updateOrderRelaSuperEmployeesAccountBySuperDiamondId(@Param("memberAccount") String memberAccount, @Param("remark")  String remark, @Param("memberId") Long memberId);

    /**
     * 根据邀请人id修改邀请人账号
     */
    void updateOrderRelaParentMemberAccountByParentMemberId(@Param("memberAccount") String memberAccount, @Param("remark")  String remark, @Param("memberId") Long memberId);
}

