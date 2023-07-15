package com.yuqian.itax.user.service.impl;

import com.google.common.collect.Maps;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.user.dao.CompanyResourcesAddressMapper;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.po.CompanyResourcesAddressPO;
import com.yuqian.itax.user.entity.vo.CompanyResourcesAddressVO;
import com.yuqian.itax.user.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("companyResourcesAddressService")
public class CompanyResourcesAddressServiceImpl extends BaseServiceImpl<CompanyResourcesAddressEntity,CompanyResourcesAddressMapper> implements CompanyResourcesAddressService {

    @Autowired
    CompanyResourcesAddressHistoryService companyResourcesAddressHistoryService;
    @Autowired
    MemberCompanyService memberCompanyService;
    @Autowired
    ParkService parkService;
    @Autowired
    CompanyResoucesApplyRecordService companyResoucesApplyRecordService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    SmsService smsService;
    @Autowired
    private MemberCompanyChangeService memberCompanyChangeService;

    @Override
    public List<CompanyResourcesAddressEntity> listCompanyResourcesAddress(Long companyId, String oemCode) {
        return mapper.listCompanyResourcesAddress(companyId, oemCode);
    }

    @Override
    public CompanyResourcesAddressVO queryCompanyResourcesAddressDetail(Long id) {
        List<Map<String ,Object>> list=new ArrayList<>();
        Map<String ,Object> map =new HashMap<>();
        CompanyResourcesAddressVO companyResourcesAddressVO=mapper.queryCompanyResourcesAddressDetail(id);
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(companyResourcesAddressVO.getCompanyId());
        ParkEntity parkEntity=parkService.findById(memberCompanyEntity.getParkId());
        if(companyResourcesAddressVO.getAddress().equals(parkEntity.getParkName())){
            map.put("label","已移交给用户");
            map.put("name","已移交给用户");
            list.add(map);
            companyResourcesAddressVO.setAddressSelect(list);
        }
        if("已移交给用户".equals(companyResourcesAddressVO.getAddress())){
            map.put("label",parkEntity.getParkName());
            map.put("name",parkEntity.getParkName());
            list.add(map);
            companyResourcesAddressVO.setAddressSelect(list);
        }
        return companyResourcesAddressVO;
    }

    @Override
    public void updateCompanyResourcesAddress(CompanyResourcesAddressPO po,String account) {
        //查询
        CompanyResourcesAddressEntity companyResourcesAddressEntity=mapper.selectByPrimaryKey(po.getId());
        //添加变更记录
        CompanyResourcesAddressHistoryEntity companyResourcesAddressHistoryEntity=new CompanyResourcesAddressHistoryEntity();
        companyResourcesAddressHistoryEntity.setCompanyId(companyResourcesAddressEntity.getCompanyId());
        companyResourcesAddressHistoryEntity.setResourcesType(companyResourcesAddressEntity.getResourcesType());
        companyResourcesAddressHistoryEntity.setUpdateBefore(companyResourcesAddressEntity.getAddress());
        companyResourcesAddressHistoryEntity.setUpdateAfter(po.getAddress());
        companyResourcesAddressHistoryEntity.setOemCode(companyResourcesAddressEntity.getOemCode());
        companyResourcesAddressHistoryEntity.setAddTime(new Date());
        companyResourcesAddressHistoryEntity.setAddUser(account);
        companyResourcesAddressHistoryEntity.setRemark(po.getRemark());
        companyResourcesAddressHistoryService.insertSelective(companyResourcesAddressHistoryEntity);

        //添加企业变更记录
        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(companyResourcesAddressEntity.getCompanyId());
        memberCompanyChangeService.insertChangeInfo(memberCompanyEntity,account,"修改证件归属地");

        //修改
        if("已移交给用户".equals(po.getAddress())){
            companyResourcesAddressEntity.setIsInPark(0);
        }else{
            companyResourcesAddressEntity.setIsInPark(1);
        }
        companyResourcesAddressEntity.setAddress(po.getAddress());
        companyResourcesAddressEntity.setRemark(po.getRemark());
        companyResourcesAddressEntity.setUpdateTime(new Date());
        companyResourcesAddressEntity.setUpdateUser(account);
        mapper.updateByPrimaryKey(companyResourcesAddressEntity);

    }

    @Override
    public String updateBatchCompanyResourcesAddress(String applyResouces,Long id,String account) {
        CompanyResoucesApplyRecordEntity companyResoucesApplyRecordEntity=companyResoucesApplyRecordService.findById(id);
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(companyResoucesApplyRecordEntity.getCompanyId());
        MemberAccountEntity memberAccountEntity=memberAccountService.findById(memberCompanyEntity.getMemberId());
        ParkEntity parkEntity=parkService.findById(memberCompanyEntity.getParkId());

        String [] applyResoucess=applyResouces.split(",");
        for (int i=0;i<applyResoucess.length;i++ ) {
            String applyResouce=applyResoucess[i];
            CompanyResourcesAddressEntity companyResourcesAddressEntity=new CompanyResourcesAddressEntity();
            companyResourcesAddressEntity.setCompanyId(companyResoucesApplyRecordEntity.getCompanyId());
            companyResourcesAddressEntity.setResourcesType(Integer.parseInt(applyResouce));
            CompanyResourcesAddressEntity resourcesAddressEntity=mapper.selectOne(companyResourcesAddressEntity);
            CompanyResourcesAddressPO companyResourcesAddressPO=new CompanyResourcesAddressPO();
            companyResourcesAddressPO.setId(resourcesAddressEntity.getId());
            companyResourcesAddressPO.setAddress(parkEntity.getParkName());
            companyResourcesAddressPO.setRemark("企业资源申请归还证件。");
            updateCompanyResourcesAddress(companyResourcesAddressPO,account);

        }

        //发送短信
        Map<String, Object> map = Maps.newHashMap();
        map.put("companyName",memberCompanyEntity.getCompanyName());
        smsService.sendTemplateSms(memberAccountEntity.getMemberPhone(), memberAccountEntity.getOemCode(), VerifyCodeTypeEnum.COMPANY_APPLY_RECORD_RETURN.getValue(), map, 1);

        return null;
    }
}

