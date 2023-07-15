package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.product.dao.ProductMapper;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.vo.MemberVipVO;
import com.yuqian.itax.user.dao.MemberAccountMapper;
import com.yuqian.itax.user.dao.MemberLevelMapper;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.entity.MemberProfitsRulesEntity;
import com.yuqian.itax.user.entity.vo.MemberLevelVO;
import com.yuqian.itax.user.enums.ExtendTypeEnum;
import com.yuqian.itax.user.enums.MemberLevelEnum;
import com.yuqian.itax.user.enums.MemberStateEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberLevelService;
import com.yuqian.itax.user.service.MemberProfitsRulesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service("memberLevelService")
@Slf4j
public class MemberLevelServiceImpl extends BaseServiceImpl<MemberLevelEntity,MemberLevelMapper> implements MemberLevelService {
    @Resource
    private MemberLevelMapper memberLevelMapper;
    @Autowired
    private MemberProfitsRulesService memberProfitsRulesService;
    @Resource
    private ProductMapper productMapper;

    @Override
    public MemberLevelEntity queryMemberLevel(String oemCode, int levelNo) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("oemCode",oemCode);
        params.put("levelNo",levelNo);
        return this.memberLevelMapper.queryMemberLevel(params);
    }

    @Override
    public MemberVipVO getMemberVipInfo(String oemCode, Integer prodType) throws BusinessException {
        if(!(prodType == 9 || prodType == 10)){
            throw new BusinessException("未知的会员类型，请检查");
        }

        ProductEntity t = new ProductEntity();
        t.setProdType(prodType);
        t.setOemCode(oemCode);
        ProductEntity entity = this.productMapper.selectOne(t);

        if(null == entity){
            throw new BusinessException("未查询到会员VIP信息");
        }

        MemberVipVO result = new MemberVipVO();
        BeanUtils.copyProperties(entity,result);

        // 查询折扣、奖励比例以及分润比例
        MemberProfitsRulesEntity mpre = new MemberProfitsRulesEntity();
        mpre.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());
        mpre.setOemCode(oemCode);
        if(prodType == 9){
            mpre.setUserLevel(MemberLevelEnum.GOLD.getValue());
        }else if(prodType == 10){
            mpre.setUserLevel(MemberLevelEnum.DIAMOND.getValue());
        }
        MemberProfitsRulesEntity profit = this.memberProfitsRulesService.selectOne(mpre);
        if(null == profit){
            throw new BusinessException("未找到会员分润规则信息");
        }
        result.setProductId(entity.getId());
//        result.setProfitsFirst(profit.getServiceFeeRate());
        result.setMembershipFee(profit.getMembershipFee());
//        result.setProfitsSecond(profit.getProfitsTwo());
//        result.setProfitsDiamondSecond(profit.getProfitsDiamondTwo());
        result.setDiscount(profit.getConsumptionDiscount());
        return result;
    }

    @Override
    public void addBatchMemberLevelEntity(List<MemberLevelEntity> list, String oemCode, String account) {
        memberLevelMapper.addBatch(list,oemCode,new Date(),account);
    }

    @Override
    public List<MemberLevelVO> getLevelUpList(MemberAccountEntity memberAccountEntity,String orderBy) {
        List<MemberLevelVO> list = mapper.selectCanUpgradeList(memberAccountEntity.getMemberLevel(), memberAccountEntity.getOemCode(),orderBy);
        if (CollectionUtil.isEmpty(list)) {
            throw new BusinessException("暂无可升级列表");
        }
/*        if (memberAccountEntity.getExtendType() == null || Objects.equals(memberAccountEntity.getExtendType(), ExtendTypeEnum.INDEPENDENT_CUSTOMER.getValue())) {
            //散客（只有VIP）
            list = list.stream().filter(e-> Objects.equals(e.getLevelNo(), MemberLevelEnum.BRONZE.getValue())).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(list)) {
                throw new BusinessException("暂无可升级列表");
            }
            return list;
        }*/
        // V3.0  只有vip
        list = list.stream().filter(e-> Objects.equals(e.getLevelNo(), MemberLevelEnum.BRONZE.getValue())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(list)) {
            throw new BusinessException("暂无可升级列表");
        }
        return list;

    }
}

