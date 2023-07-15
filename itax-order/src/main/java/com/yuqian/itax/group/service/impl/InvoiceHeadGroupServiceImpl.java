package com.yuqian.itax.group.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.group.dao.InvoiceHeadGroupMapper;
import com.yuqian.itax.group.entity.InvoiceHeadGroupEntity;
import com.yuqian.itax.group.entity.po.InvoiceHeadGroupPO;
import com.yuqian.itax.group.entity.query.InvoiceHeadGroupQuery;
import com.yuqian.itax.group.service.InvoiceHeadGroupService;
import com.yuqian.itax.system.entity.CityEntity;
import com.yuqian.itax.system.entity.DistrictEntity;
import com.yuqian.itax.system.entity.ProvinceEntity;
import com.yuqian.itax.system.service.CityService;
import com.yuqian.itax.system.service.DistrictService;
import com.yuqian.itax.system.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service("invoiceHeadGroupService")
public class InvoiceHeadGroupServiceImpl extends BaseServiceImpl<InvoiceHeadGroupEntity,InvoiceHeadGroupMapper> implements InvoiceHeadGroupService {

    @Autowired
    ProvinceService provinceService;
    @Autowired
    CityService cityService;
    @Autowired
    DistrictService districtService;


    @Override
    public List<InvoiceHeadGroupEntity> getInvoiceHeadGroup(InvoiceHeadGroupQuery query) {
        return mapper.getInvoiceHeadGroup(query);
    }

    @Override
    public InvoiceHeadGroupEntity addInvoiceHeadGroup(InvoiceHeadGroupPO po,String account) {
        InvoiceHeadGroupEntity entity1=new InvoiceHeadGroupEntity();
        InvoiceHeadGroupEntity entity2=new InvoiceHeadGroupEntity();
        InvoiceHeadGroupEntity entity3=new InvoiceHeadGroupEntity();
        entity1.setOemCode(po.getOemCode());
        entity1.setCompanyName(po.getCompanyName());
        if(mapper.select(entity1).size()>0){
            throw  new BusinessException("发票抬头中的公司名或者税号已存在。");
        }
        entity2.setOemCode(po.getOemCode());
        entity2.setEin(po.getEin());
        if(mapper.select(entity2).size()>0){
            throw  new BusinessException("发票抬头中的公司名或者税号已存在。");
        }
        entity3.setOemCode(po.getOemCode());
        entity3.setCompanyName(po.getCompanyName());
        entity3.setEin(po.getEin());
        if(mapper.select(entity3).size()>0){
            throw  new BusinessException("发票抬头中的公司名或者税号已存在。");
        }
        InvoiceHeadGroupEntity invoiceHeadGroupEntity=new InvoiceHeadGroupEntity();
        invoiceHeadGroupEntity.setOemCode(po.getOemCode());
        invoiceHeadGroupEntity.setCompanyName(po.getCompanyName());
        invoiceHeadGroupEntity.setCompanyAddress(po.getCompanyAddress());
        invoiceHeadGroupEntity.setEin(po.getEin());
        invoiceHeadGroupEntity.setPhone(po.getPhone());
        invoiceHeadGroupEntity.setRegistAddress(po.getCompanyAddress());
        invoiceHeadGroupEntity.setBankName(po.getBankName());
        invoiceHeadGroupEntity.setBankNumber(po.getBankNumber());
        invoiceHeadGroupEntity.setRecipient(po.getRecipient());
        invoiceHeadGroupEntity.setRecipientPhone(po.getRecipientPhone());
        invoiceHeadGroupEntity.setRecipientAddress(po.getRecipientAddress());
        invoiceHeadGroupEntity.setStatus(1);
        invoiceHeadGroupEntity.setProvinceCode(po.getProvinceCode());
        ProvinceEntity provinceEntity=provinceService.getByCode(po.getProvinceCode());
        invoiceHeadGroupEntity.setProvinceName(provinceEntity.getName());
        invoiceHeadGroupEntity.setCityCode(po.getCityCode());
        CityEntity cityEntity=cityService.getByCode(po.getCityCode());
        invoiceHeadGroupEntity.setCityName(cityEntity.getName());
        invoiceHeadGroupEntity.setDistrictCode(po.getDistrictCode());
        invoiceHeadGroupEntity.setEmail(po.getEmail());
        DistrictEntity districtEntity=districtService.getByCode(po.getDistrictCode());
        invoiceHeadGroupEntity.setDistrictName(districtEntity.getName());
        invoiceHeadGroupEntity.setAddTime(new Date());
        invoiceHeadGroupEntity.setAddUser(account);


        mapper.insert(invoiceHeadGroupEntity);
        return invoiceHeadGroupEntity;
    }

    @Override
    public InvoiceHeadGroupEntity updateInvoiceHeadGroup(InvoiceHeadGroupPO po, String account) {
        InvoiceHeadGroupEntity entity1=new InvoiceHeadGroupEntity();
        InvoiceHeadGroupEntity entity2=new InvoiceHeadGroupEntity();
        InvoiceHeadGroupEntity entity3=new InvoiceHeadGroupEntity();
        entity1.setOemCode(po.getOemCode());
        entity1.setCompanyName(po.getCompanyName());
        List<InvoiceHeadGroupEntity> list1 =mapper.select(entity1);
        if(list1.size()>0){
            for(InvoiceHeadGroupEntity e:list1){
                if(!e.getId().equals(po.getId())){
                    throw  new BusinessException("发票抬头中的公司名或者税号已存在。");
                }
            }
        }
        entity2.setOemCode(po.getOemCode());
        entity2.setEin(po.getEin());
        List<InvoiceHeadGroupEntity> list2 =mapper.select(entity2);
        if(list2.size()>0){
            for(InvoiceHeadGroupEntity e:list2){
                if(!e.getId().equals(po.getId())){
                    throw  new BusinessException("发票抬头中的公司名或者税号已存在。");
                }
            }
        }
        entity3.setOemCode(po.getOemCode());
        entity3.setCompanyName(po.getCompanyName());
        entity3.setEin(po.getEin());
        List<InvoiceHeadGroupEntity> list3 =mapper.select(entity3);
        if(list3.size()>0){
            for(InvoiceHeadGroupEntity e:list3){
                if(!e.getId().equals(po.getId())){
                    throw  new BusinessException("发票抬头中的公司名或者税号已存在。");
                }
            }
        }
        InvoiceHeadGroupEntity invoiceHeadGroupEntity=mapper.selectByPrimaryKey(po.getId());
        invoiceHeadGroupEntity.setOemCode(po.getOemCode());
        invoiceHeadGroupEntity.setCompanyName(po.getCompanyName());
        invoiceHeadGroupEntity.setCompanyAddress(po.getCompanyAddress());
        invoiceHeadGroupEntity.setEin(po.getEin());
        invoiceHeadGroupEntity.setPhone(po.getPhone());
        invoiceHeadGroupEntity.setRegistAddress(po.getCompanyAddress());
        invoiceHeadGroupEntity.setBankName(po.getBankName());
        invoiceHeadGroupEntity.setBankNumber(po.getBankNumber());
        invoiceHeadGroupEntity.setRecipient(po.getRecipient());
        invoiceHeadGroupEntity.setRecipientPhone(po.getRecipientPhone());
        invoiceHeadGroupEntity.setRecipientAddress(po.getRecipientAddress());
        invoiceHeadGroupEntity.setStatus(1);
        invoiceHeadGroupEntity.setProvinceCode(po.getProvinceCode());
        ProvinceEntity provinceEntity=provinceService.getByCode(po.getProvinceCode());
        invoiceHeadGroupEntity.setProvinceName(provinceEntity.getName());
        invoiceHeadGroupEntity.setCityCode(po.getCityCode());
        CityEntity cityEntity=cityService.getByCode(po.getCityCode());
        invoiceHeadGroupEntity.setCityName(cityEntity.getName());
        invoiceHeadGroupEntity.setDistrictCode(po.getDistrictCode());
        DistrictEntity districtEntity=districtService.getByCode(po.getDistrictCode());
        invoiceHeadGroupEntity.setDistrictName(districtEntity.getName());
        invoiceHeadGroupEntity.setUpdateTime(new Date());
        invoiceHeadGroupEntity.setUpdateUser(account);
        invoiceHeadGroupEntity.setEmail(po.getEmail());
        mapper.updateByPrimaryKey(invoiceHeadGroupEntity);
        return invoiceHeadGroupEntity;
    }
}

