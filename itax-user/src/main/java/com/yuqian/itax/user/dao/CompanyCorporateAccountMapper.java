package com.yuqian.itax.user.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;
import com.yuqian.itax.user.entity.query.CompanyCorporateAccountQuery;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;
import com.yuqian.itax.user.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 企业对公户表dao
 *
 * @Date: 2020年09月07日 09:13:58 
 * @author 蒋匿
 */
@Mapper
public interface CompanyCorporateAccountMapper extends BaseMapper<CompanyCorporateAccountEntity> {

    List<CompanyCorporateAccountVO> querCompanyCorporateAccountServiceList(CompanyCorporateAccountQuery query);

    CompanyCorporateAccountVO queryCompanyCorporateAccountDetail(Long id);

    /**
     * @Description 分页查询企业对公户列表
     * @Author  Kaven
     * @Date   2020/9/7 11:25
     * @Param   ComCorpAccQuery
     * @Return  List<CompanyCorpAccountVO>
     * @Exception
     */
    List<CompanyCorpAccountVO> listComCorpAccountPage(ComCorpAccQuery query);

    /**
     * @Description 查询对公户详情
     * @Author  Kaven
     * @Date   2020/9/7 14:56
     * @Param   id
     * @Return  CompanyCorpAccountDetailVO
     * @Exception
     */
    CompanyCorpAccountDetailVO getDetail(Long id);

    /**
     * @Description 对公户提现-选择开票记录列表
     * @Author Kaven
     * @Date 2020/9/8 14:41
     * @Param CorporateAccountCollectionRecordQuery
     * @Return List<CorporateInvoiceOrderVO>
     * @Exception
     */
    List<CorporateInvoiceOrderVO> listInvoiceOrderForCorp(CorporateAccountCollectionRecordQuery query);

    /**
     * 查询企业对公户列表（xxljob使用）
     */
    List<CompanyCorporateAccountHandlerVO> queryAccountHandlerList();

    /**
     * 根据companyID查询企业对公户
     * @param companyId
     * @return
     */
    CompanyCorporateAccountEntity queryCorpByCompanyId(Long companyId);

    /**
     * 查询对公户续费详情信息
     * @param companyCorpAccId
     * @return
     */
    CompanyCorpAccRenewDetailVO companyCorpAccRenewDetail(Long companyCorpAccId);

    /**
     * 根据对公户id更新对公户过期状态
     * @param id
     * @param expirationTime
     * @param surplusDays
     */
    void updateOverdueStatus(@Param("id") Long id, @Param("expirationTime") Date expirationTime, @Param("surplusDays") Integer surplusDays);

    /**
     * 查询已过期的企业对公户信息
     * @param overdueDays
     * @return
     */
    List<OverdueCompanyCropAccInfoVO> queryOverdueCompanyCropAcc(Integer overdueDays);

    /**
     * 查询即将过期的对公户信息
     * @param surplusDays
     * @return
     */
    List<OverdueCompanyCropAccInfoVO> queryWillExpireCompanyCropAcc(Integer surplusDays);

    /**
     * 更新对公户过期状态
     * @param surplusDays
     */
    void updateCompanyCorpAccOverdueStatus(Integer surplusDays);

    /**
     * 已过期对公户状态修改，包括是否发送通知标识
     */
    void updateOverdueCorpAccStatus();
}

