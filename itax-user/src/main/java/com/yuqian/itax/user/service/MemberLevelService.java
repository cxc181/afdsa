package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.product.entity.vo.MemberVipVO;
import com.yuqian.itax.user.dao.MemberLevelMapper;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.entity.vo.MemberLevelVO;

import java.util.List;

/**
 * 会员等级管理service
 * 
 * @Date: 2019年12月07日 20:48:00 
 * @author 蒋匿
 */
public interface MemberLevelService extends IBaseService<MemberLevelEntity,MemberLevelMapper> {

    /**
     * @Description 根据机构编码和等级编号查询
     * @Author  Kaven
     * @Date   2019/12/9 11:43
     * @Param oemCode levelNo
     * @Return
    */
    MemberLevelEntity queryMemberLevel(String oemCode, int levelNo);

    /**
     * @Description 获取会员VIP信息
     * @Author  Kaven
     * @Date   2019/12/14 16:14
     * @Param  oemCode prodType
     * @Return MemberVipVO
     * @Exception BusinessException
     */
    MemberVipVO getMemberVipInfo(String oemCode, Integer prodType) throws BusinessException;

    /**
     * 批量插入
     */
    void addBatchMemberLevelEntity(List<MemberLevelEntity> list,String oemCode,String account);

    /**
     * 获取当前用户可供升级的等级集合
     * @param memberAccountEntity
     */
    List<MemberLevelVO> getLevelUpList(MemberAccountEntity memberAccountEntity,String orderBy);

}

