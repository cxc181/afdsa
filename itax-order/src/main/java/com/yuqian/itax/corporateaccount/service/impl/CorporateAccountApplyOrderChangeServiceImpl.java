package com.yuqian.itax.corporateaccount.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.corporateaccount.dao.CorporateAccountApplyOrderChangeMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountApplyOrderChangeEntity;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountApplyOrderEntity;
import com.yuqian.itax.corporateaccount.service.CorporateAccountApplyOrderChangeService;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service("corporateAccountApplyOrderChangeService")
public class CorporateAccountApplyOrderChangeServiceImpl extends BaseServiceImpl<CorporateAccountApplyOrderChangeEntity,CorporateAccountApplyOrderChangeMapper> implements CorporateAccountApplyOrderChangeService {

    @Override
    public void addCorporateAccountApplyOrderChange(CorporateAccountApplyOrderEntity corporateAccountApplyOrderEntity,Integer status,String account) {
        CorporateAccountApplyOrderChangeEntity corporateAccountApplyOrderChangeEntity=new CorporateAccountApplyOrderChangeEntity();
        corporateAccountApplyOrderChangeEntity.setOrderNo(corporateAccountApplyOrderEntity.getOrderNo());
        corporateAccountApplyOrderChangeEntity.setMemberId(corporateAccountApplyOrderEntity.getMemberId());
        corporateAccountApplyOrderChangeEntity.setCompanyId(corporateAccountApplyOrderEntity.getCompanyId());
        corporateAccountApplyOrderChangeEntity.setApplyBankName(corporateAccountApplyOrderEntity.getApplyBankName());
        corporateAccountApplyOrderChangeEntity.setOemCode(corporateAccountApplyOrderEntity.getOemCode());
        corporateAccountApplyOrderChangeEntity.setHandleFee(corporateAccountApplyOrderEntity.getHandleFee());
        corporateAccountApplyOrderChangeEntity.setEscrowFee(corporateAccountApplyOrderEntity.getEscrowFee());
        corporateAccountApplyOrderChangeEntity.setParkId(corporateAccountApplyOrderEntity.getParkId());
        corporateAccountApplyOrderChangeEntity.setParkCode(corporateAccountApplyOrderEntity.getParkCode());
        corporateAccountApplyOrderChangeEntity.setCorporateAccountId(corporateAccountApplyOrderEntity.getCorporateAccountId());
        corporateAccountApplyOrderChangeEntity.setStatus(status);
        corporateAccountApplyOrderChangeEntity.setAddTime(new Date());
        corporateAccountApplyOrderChangeEntity.setAddUser(account);
        corporateAccountApplyOrderChangeEntity.setRemark(corporateAccountApplyOrderEntity.getRemark());
        mapper.insertSelective(corporateAccountApplyOrderChangeEntity);
    }
}

