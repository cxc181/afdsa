package com.yuqian.itax.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.user.dao.MemberProfitsRulesMapper;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.entity.MemberProfitsRulesEntity;
import com.yuqian.itax.user.entity.po.MemberProfitsRulesPO;
import com.yuqian.itax.user.enums.MemberStateEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberLevelService;
import com.yuqian.itax.user.service.MemberProfitsRulesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("memberProfitsRulesService")
public class MemberProfitsRulesServiceImpl extends BaseServiceImpl<MemberProfitsRulesEntity, MemberProfitsRulesMapper> implements MemberProfitsRulesService {
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private ProductService productService;

    @Override
    public MemberProfitsRulesEntity updateMemberProfitsRulesEntity(MemberProfitsRulesPO memberProfitsRulesPO,Long userId) {
        MemberProfitsRulesEntity memberProfitsRulesEntity =mapper.selectByPrimaryKey(memberProfitsRulesPO.getId());
        memberProfitsRulesEntity.setMembershipFee(memberProfitsRulesPO.getMembershipFee());
        memberProfitsRulesEntity.setProfitsEntrustFeeRate(memberProfitsRulesPO.getProfitsEntrustFeeRate());
        memberProfitsRulesEntity.setProfitsPeersTwoEntrustFeeRate(memberProfitsRulesPO.getProfitsPeersTwoEntrustFeeRate());
        memberProfitsRulesEntity.setServiceFeeRate(memberProfitsRulesPO.getServiceFeeRate());
        memberProfitsRulesEntity.setProfitsPeersTwoServiceFeeRate(memberProfitsRulesPO.getProfitsPeersTwoServiceFeeRate());
        memberProfitsRulesEntity.setProfitsPeersTwoMembershipFee(memberProfitsRulesPO.getProfitsPeersTwoMembershipFee());
        memberProfitsRulesEntity.setConsumptionDiscount(memberProfitsRulesPO.getConsumptionDiscount());
        memberProfitsRulesEntity.setUpdateTime(new Date());
        memberProfitsRulesEntity.setUpdateUser(String.valueOf(userId));
        mapper.updateByPrimaryKey(memberProfitsRulesEntity);
        return memberProfitsRulesEntity;
    }

    @Override
    public Long queryMemberDiscount(Long currUserId, Long productId,String oemCode, Long parkId) throws BusinessException {
        if(null == currUserId || null == productId){
            throw new BusinessException("参数不正确");
        }
        // 查询产品信息
        ProductEntity product = this.productService.findById(productId);
        if(null == product){
            throw new BusinessException("产品信息不存在");
        }
        product = productService.queryProductByProdType(product.getProdType(), product.getOemCode(), parkId);

        // 查询当前会员等级
        MemberAccountEntity member = this.memberAccountService.findById(currUserId);
        if(null == member){
            throw new BusinessException(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());
        if(null == level){
            throw new BusinessException("未找到用户会员等级信息");
        }

        Long discount = product.getProdAmount();// 折后价，默认取产品原价

        MemberProfitsRulesEntity t = new MemberProfitsRulesEntity();
        t.setOemCode(oemCode);
        t.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());
        t.setUserLevel(level.getLevelNo());
        MemberProfitsRulesEntity mpre = this.selectOne(t);
        if(null != mpre){
            // 计算折后价格
            BigDecimal vipAmount = new BigDecimal(product.getProdAmount()).multiply(mpre.getConsumptionDiscount().divide(new BigDecimal(100)));
            discount = discount - vipAmount.longValue();
        }
        return discount;
    }

    @Override
    public void initMemberProfitsRulesByOem(String oemCode,String account) {
        MemberProfitsRulesEntity entity;
        MemberProfitsRulesEntity memberProfitsRulesEntity = new MemberProfitsRulesEntity();
        memberProfitsRulesEntity.setOemCode("YCS");
        memberProfitsRulesEntity.setStatus(1);
        List<MemberProfitsRulesEntity> list = this.select(memberProfitsRulesEntity);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (MemberProfitsRulesEntity vo : list) {
            entity = new MemberProfitsRulesEntity();
            entity.setOemCode(oemCode);
            entity.setUserLevel(vo.getUserLevel());
            entity.setStatus(1);
            entity.setAddTime(new Date());
            entity.setAddUser(account);
            mapper.insertSelective(entity);
        }
    }
}

