package com.yuqian.itax.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.user.dao.CompanyResourcesUseRecordMapper;
import com.yuqian.itax.user.entity.CompanyResourcesUseRecordEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.query.CompanyResourcesUseRecordAdminQuery;
import com.yuqian.itax.user.entity.query.CompanyResourcesUseRecordQuery;
import com.yuqian.itax.user.entity.vo.ComRscTimeoutRecordVO;
import com.yuqian.itax.user.entity.vo.CompanyResourcesUseRecordAdminVO;
import com.yuqian.itax.user.entity.vo.CompanyResourcesUseRecordVO;
import com.yuqian.itax.user.enums.AuditStateEnum;
import com.yuqian.itax.user.service.CompanyResourcesUseRecordService;
import com.yuqian.itax.user.service.MemberAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("companyResourcesUseRecordService")
public class CompanyResourcesUseRecordServiceImpl extends BaseServiceImpl<CompanyResourcesUseRecordEntity,CompanyResourcesUseRecordMapper> implements CompanyResourcesUseRecordService {
    @Autowired
    private MemberAccountService memberAccountService;

    @Resource
    private CompanyResourcesUseRecordMapper companyResourcesUseRecordMapper;

    @Override
    public PageInfo<CompanyResourcesUseRecordVO> listPage(CompanyResourcesUseRecordQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<CompanyResourcesUseRecordVO> list = this.companyResourcesUseRecordMapper.listRecordPage(query);

        return new PageInfo<CompanyResourcesUseRecordVO>(list);
    }

    @Override
    public PageInfo<CompanyResourcesUseRecordAdminVO> companyResourcesUseRecordPage(CompanyResourcesUseRecordAdminQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return  new PageInfo<>(mapper.getCompanyResourcesUseRecordList(query));
    }

    @Override
    public void approval(Long userId, Long id, Integer auditStatus, String auditDesc, String imgAddr) throws BusinessException {
        // 判断用户是否存在
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if(null == member){
            throw new BusinessException("审批失败，" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        // 查询审批记录是否存在
        CompanyResourcesUseRecordEntity entity = this.companyResourcesUseRecordMapper.selectByPrimaryKey(id);
        if(null == entity){
            throw new BusinessException("用章申请记录不存在，请检查");
        }

        // 状态判断
        if(!entity.getAuditStatus().equals(AuditStateEnum.TO_APPROVE.getValue())){
            throw new BusinessException("只有待审批状态的记录才能审批");
        }

        entity.setAuditUser(userId);
        entity.setUpdateUser(member.getMemberAccount());
        entity.setUpdateTime(new Date());

        if(StringUtils.isNotBlank(imgAddr)){
            entity.setImgsAddr(imgAddr);
        }

        if(AuditStateEnum.APPROVE_NO_PASS.getValue().equals(auditStatus)){
            // 审核不通过时
            entity.setAuditStatus(AuditStateEnum.APPROVE_NO_PASS.getValue());
            entity.setAuditDesc(auditDesc);
        }else if(AuditStateEnum.APPROVED.getValue().equals(auditStatus)){
            // 审核通过时
            entity.setAuditStatus(AuditStateEnum.APPROVED.getValue());
        }else{
            throw new BusinessException("未知的审核状态，请检查");
        }
        this.companyResourcesUseRecordMapper.updateByPrimaryKey(entity);
    }

    @Override
    public List<ComRscTimeoutRecordVO> selectTimeOutList(Integer queryType) {
        return this.companyResourcesUseRecordMapper.selectTimeOutList(queryType);
    }

    /**
     * 根据用户账号修改通知状态
     * @param auditUser
     * @param queryType
     */
    @Override
    public void updateResourcesNoticeStatusByAccount(Long auditUser,Integer queryType){
         this.companyResourcesUseRecordMapper.updateResourcesNoticeStatusByAccount(auditUser,queryType);
    }
}

