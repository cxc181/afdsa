package com.yuqian.itax.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.capital.enums.UserCapOrderTypeEnum;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.CompanyResoucesApplyRecordMapper;
import com.yuqian.itax.user.entity.CompanyResoucesApplyRecordEntity;
import com.yuqian.itax.user.entity.query.CompResApplyRecordQuery;
import com.yuqian.itax.user.entity.query.CompanyCertListApiQuery;
import com.yuqian.itax.user.entity.query.CompanyResoucesApplyRecordQuery;
import com.yuqian.itax.user.entity.vo.ComResApplyRecordDetailVO;
import com.yuqian.itax.user.entity.vo.CompanyCertListApiVO;
import com.yuqian.itax.user.entity.vo.CompanyResoucesApplyRecordExportVO;
import com.yuqian.itax.user.entity.vo.CompanyResoucesApplyRecordVO;
import com.yuqian.itax.user.enums.CompanyResoucesApplyRecordStatusEnum;
import com.yuqian.itax.user.service.CompanyResoucesApplyRecordService;
import com.yuqian.itax.user.service.MemberCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("companyResoucesApplyRecordService")
public class CompanyResoucesApplyRecordServiceImpl extends BaseServiceImpl<CompanyResoucesApplyRecordEntity,CompanyResoucesApplyRecordMapper> implements CompanyResoucesApplyRecordService {

    @Autowired
    UserCapitalAccountService userCapitalAccountService;
    @Autowired
    MemberCompanyService memberCompanyService;


    @Override
    public PageInfo<CompanyResoucesApplyRecordVO> companyResoucesApplyRecordPageInfo(CompanyResoucesApplyRecordQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<CompanyResoucesApplyRecordVO>(mapper.queryCompanyResoucesApplyRecord(query));
    }

    @Override
    public List<CompanyResoucesApplyRecordVO> companyResoucesApplyRecordList(CompanyResoucesApplyRecordQuery query) {
        return mapper.queryCompanyResoucesApplyRecord(query);
    }

    @Override
    public List<CompanyResoucesApplyRecordExportVO> companyResoucesApplyRecordExportList(CompanyResoucesApplyRecordQuery query) {
        return mapper.queryExportCompanyResoucesApplyRecord(query);
    }

    @Override
    public Map<String, Integer> sumCompanyResoucesApplyRecordList(CompanyResoucesApplyRecordQuery query) {
        return mapper.sumCompanyResoucesApplyRecord(query);
    }

    @Override
    @Transactional
    public void companyResoucesApplyRecordCancel(Long id,String account) {
        //获取企业资源申请信息
        CompanyResoucesApplyRecordEntity companyResoucesApplyRecordEntity=mapper.selectByPrimaryKey(id);
        if(companyResoucesApplyRecordEntity.getStatus()==4||companyResoucesApplyRecordEntity.getStatus()==5){
            throw  new BusinessException("已签收、已取消订单不允许取消。如有疑问请联系客服");
        }
        //判断是否要退还邮费
        if(companyResoucesApplyRecordEntity.getPostageFees()>0){
            Long memberId=memberCompanyService.findById(companyResoucesApplyRecordEntity.getCompanyId()).getMemberId();
            userCapitalAccountService.addBalance(companyResoucesApplyRecordEntity.getOemCode(),companyResoucesApplyRecordEntity.getOrderNo(), UserCapOrderTypeEnum.COMPANY_APPLY_RECORD.getValue(),memberId,1,companyResoucesApplyRecordEntity.getPostageFees(),"企业资源申请取消退返邮费",account,new Date(),1);
        }
        //更新状态
        companyResoucesApplyRecordEntity.setStatus(CompanyResoucesApplyRecordStatusEnum.CANCELED.getValue());
        companyResoucesApplyRecordEntity.setUpdateTime(new Date());
        companyResoucesApplyRecordEntity.setUpdateUser(account);
        mapper.updateByPrimaryKey(companyResoucesApplyRecordEntity);
    }

    @Override
    public List<CompanyResoucesApplyRecordEntity> listCompResApplyRecord(Long memberId, String oemCode, CompResApplyRecordQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return mapper.listCompResApplyRecord(memberId, oemCode, query.getCompanyId());
    }

    @Override
    public List<CompanyResoucesApplyRecordEntity> listCompResApplyRecordToRec() {
        return mapper.listCompResApplyRecordToRec();
    }

    @Override
    public ComResApplyRecordDetailVO queryCompResApplyRecordDetail(Long memberId, String oemCode, String orderNo) {
        return mapper.queryCompResApplyRecordDetail(memberId, oemCode, orderNo);
    }

    @Override
    public List<CompanyResoucesApplyRecordEntity> certApplyOrderListByType() {
        return mapper.certApplyOrderListByType();
    }

    @Override
    public List<CompanyCertListApiVO> getCertListByQuery(String oemCode, CompanyCertListApiQuery query) {
        return mapper.getCertListByQuery(oemCode, query.getCompanyName(), query.getRegPhone(), query.getOrderNo());
    }
}

