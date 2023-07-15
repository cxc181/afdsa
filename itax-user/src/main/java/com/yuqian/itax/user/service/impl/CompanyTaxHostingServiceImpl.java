package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.user.dao.CompanyTaxHostingMapper;
import com.yuqian.itax.user.entity.CompanyTaxHostingEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.po.CompanyTaxHostingPO;
import com.yuqian.itax.user.service.CompanyTaxHostingService;
import com.yuqian.itax.user.service.MemberCompanyChangeService;
import com.yuqian.itax.user.service.MemberCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service("companyTaxHostingService")
public class CompanyTaxHostingServiceImpl extends BaseServiceImpl<CompanyTaxHostingEntity,CompanyTaxHostingMapper> implements CompanyTaxHostingService {

    @Autowired
    private MemberCompanyChangeService memberCompanyChangeService;
    @Autowired
    MemberCompanyService memberCompanyService;

    @Override
    public CompanyTaxHostingEntity getCompanyTaxHostingByCompanyId(Long companyId,Integer channel){
        if(channel == null){
            channel = 1;
        }
        CompanyTaxHostingEntity companyTaxHostingEntity = new CompanyTaxHostingEntity();
        companyTaxHostingEntity.setCompanyId(companyId);
        companyTaxHostingEntity.setStatus(1); //已托管
        companyTaxHostingEntity.setChannel(channel); //通道-百旺
        List<CompanyTaxHostingEntity> hostingEntityList = select(companyTaxHostingEntity);
        if(hostingEntityList == null || hostingEntityList.size()!=1){
            companyTaxHostingEntity = null;
        }else{
            companyTaxHostingEntity = hostingEntityList.get(0);
        }
        return companyTaxHostingEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdate(CompanyTaxHostingPO po, String account) {


        CompanyTaxHostingEntity entity=new CompanyTaxHostingEntity();
        entity.setCompanyId(po.getCompanyId());
        List<CompanyTaxHostingEntity> list =mapper.select(entity);
        CompanyTaxHostingEntity companyTaxHostingEntity;
        if(CollectionUtil.isNotEmpty(list)){
            //更新
            companyTaxHostingEntity=list.get(0);
            //companyTaxHostingEntity.setTaxDiscType(po.getTaxDiscType());
            //companyTaxHostingEntity.setTaxDiscCode(po.getTaxDiscCode());
            companyTaxHostingEntity.setChannel(po.getChannel());
            //companyTaxHostingEntity.setFaceAmountType(po.getFaceAmountType());
            //companyTaxHostingEntity.setFaceAmount(po.getFaceAmount());
            companyTaxHostingEntity.setChannel(po.getChannel());
            companyTaxHostingEntity.setUpdateTime(new Date());
            companyTaxHostingEntity.setUpdateUser(account);
            companyTaxHostingEntity.setStatus(po.getHostingStatus());
            mapper.updateByPrimaryKeySelective(companyTaxHostingEntity);
        }else{
            //新增
            companyTaxHostingEntity=new CompanyTaxHostingEntity();
            companyTaxHostingEntity.setCompanyId(po.getCompanyId());
//            companyTaxHostingEntity.setTaxDiscType(po.getTaxDiscType());
//            companyTaxHostingEntity.setTaxDiscCode(po.getTaxDiscCode());
//            companyTaxHostingEntity.setFaceAmountType(po.getFaceAmountType());
//            companyTaxHostingEntity.setFaceAmount(po.getFaceAmount());
            companyTaxHostingEntity.setChannel(po.getChannel());
            companyTaxHostingEntity.setStatus(po.getHostingStatus());
            companyTaxHostingEntity.setAddTime(new Date());
            companyTaxHostingEntity.setAddUser(account);
            mapper.insertSelective(companyTaxHostingEntity);
        }
        //添加企业变更记录
        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(po.getCompanyId());
        memberCompanyChangeService.insertChangeInfo(memberCompanyEntity,account,"托管信息修改");
    }
}

