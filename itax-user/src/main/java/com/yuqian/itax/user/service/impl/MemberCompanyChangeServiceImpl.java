package com.yuqian.itax.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.MemberCompanyChangeMapper;
import com.yuqian.itax.user.entity.MemberCompanyChangeEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.query.MemberCompanyQuery;
import com.yuqian.itax.user.entity.vo.CompanyChangeVo;
import com.yuqian.itax.user.service.MemberCompanyChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service("memberCompanyChangeService")
public class MemberCompanyChangeServiceImpl extends BaseServiceImpl<MemberCompanyChangeEntity,MemberCompanyChangeMapper> implements MemberCompanyChangeService {

    @Autowired
    private MemberCompanyChangeMapper memberCompanyChangeMapper;

    @Override
    public PageInfo<CompanyChangeVo> queryMemberCompanyChangeByCompanyId(MemberCompanyQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(memberCompanyChangeMapper.queryMemberCompanyChangeByCompanyId(query.getCompanyId()));
    }

    @Override
    public void insertChangeInfo(MemberCompanyEntity memberCompanyEntity, String addUser,String remark) {
        MemberCompanyChangeEntity memberCompanyChangeEntity = new MemberCompanyChangeEntity();
        memberCompanyChangeEntity.setCompanyId(memberCompanyEntity.getId());
        memberCompanyChangeEntity.setMemberId(memberCompanyEntity.getMemberId());
        memberCompanyChangeEntity.setCompanyName(memberCompanyEntity.getCompanyName());
        if (memberCompanyEntity.getEin() != null){
            memberCompanyChangeEntity.setEin(memberCompanyEntity.getEin());
        }
        memberCompanyChangeEntity.setBusinessScope(memberCompanyEntity.getBusinessScope());
        memberCompanyChangeEntity.setIndustryId(memberCompanyEntity.getIndustryId());
        memberCompanyChangeEntity.setIndustry(memberCompanyEntity.getIndustry());
        if (memberCompanyEntity.getBusinessLicense() != null){
            memberCompanyChangeEntity.setBusinessLicense(memberCompanyEntity.getBusinessLicense());
        }
        if (memberCompanyEntity.getBusinessLicenseCopy() != null){
            memberCompanyChangeEntity.setBusinessLicenseCopy(memberCompanyEntity.getBusinessLicenseCopy());
        }
        memberCompanyChangeEntity.setEndTime(memberCompanyEntity.getEndTime());
        memberCompanyChangeEntity.setStatus(memberCompanyEntity.getStatus());
        memberCompanyChangeEntity.setAnnualFee(memberCompanyEntity.getAnnualFee());
        memberCompanyChangeEntity.setOemCode(memberCompanyEntity.getOemCode());
        memberCompanyChangeEntity.setParkId(memberCompanyEntity.getParkId());
        memberCompanyChangeEntity.setIsTopUp(memberCompanyEntity.getIsTopUp());
        memberCompanyChangeEntity.setAddTime(new Date());
        memberCompanyChangeEntity.setAddUser(addUser);
        memberCompanyChangeEntity.setRemark(remark);
        memberCompanyChangeEntity.setOperatorName(memberCompanyEntity.getOperatorName());
        memberCompanyChangeEntity.setCompanyType(memberCompanyEntity.getCompanyType());
        memberCompanyChangeEntity.setOperatorTel(memberCompanyEntity.getOperatorTel());
        if (memberCompanyEntity.getAgentAccount() != null){
            memberCompanyChangeEntity.setAgentAccount(memberCompanyEntity.getAgentAccount());
        }
        memberCompanyChangeEntity.setIdCardNumber(memberCompanyEntity.getIdCardNumber());
        memberCompanyChangeEntity.setIdCardFront(memberCompanyEntity.getIdCardFront());
        memberCompanyChangeEntity.setIdCardReverse(memberCompanyEntity.getIdCardReverse());
        memberCompanyChangeEntity.setIsOther(memberCompanyEntity.getIsOther());
        memberCompanyChangeEntity.setCommissionInvoiceDefault(memberCompanyEntity.getCommissionInvoiceDefault());
        if (memberCompanyEntity.getOrderNo() != null){
            memberCompanyChangeEntity.setOrderNo(memberCompanyEntity.getOrderNo());
        }
        memberCompanyChangeEntity.setOverdueStatus(memberCompanyEntity.getOverdueStatus());
        memberCompanyChangeEntity.setIsSendNotice(memberCompanyEntity.getIsSendNotice());
        if (memberCompanyEntity.getCancelCredentials() != null){
            memberCompanyChangeEntity.setCancelCredentials(memberCompanyEntity.getCancelCredentials());
        }
        mapper.insert(memberCompanyChangeEntity);
    }
}

