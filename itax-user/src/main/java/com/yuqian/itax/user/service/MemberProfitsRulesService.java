package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.MemberProfitsRulesMapper;
import com.yuqian.itax.user.entity.MemberProfitsRulesEntity;
import com.yuqian.itax.user.entity.po.MemberProfitsRulesPO;

/**
 * 会员分润规则service
 * 
 * @Date: 2019年12月12日 17:12:37 
 * @author 蒋匿
 */
public interface MemberProfitsRulesService extends IBaseService<MemberProfitsRulesEntity, MemberProfitsRulesMapper> {
    /**
     * 会员政策编辑
     * @author hz
     * @date 2019/12/16
     */
    MemberProfitsRulesEntity updateMemberProfitsRulesEntity(MemberProfitsRulesPO memberProfitsRulesPO,Long userId);


    /**
     * @Description 计算会员产品折扣价
     * @Author  Kaven
     * @Date   2019/12/27 16:11
     * @Param  currUserId productId oemCode
     * @Return Long
     * @Exception BusinessException
    */
    Long queryMemberDiscount(Long currUserId,Long productId,String oemCode, Long parkId) throws BusinessException;

    /**
     * 机构会员政策初始化
     */
    void   initMemberProfitsRulesByOem (String oemCode,String account);
}

