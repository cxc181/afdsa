package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.CompanyCorporateAccountMapper;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.po.CompanyCorporateAccountPO;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;
import com.yuqian.itax.user.entity.query.CompanyCorporateAccountDetailQuery;
import com.yuqian.itax.user.entity.query.CompanyCorporateAccountQuery;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;
import com.yuqian.itax.user.entity.vo.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 企业对公户表service
 * 
 * @Date: 2020年09月07日 09:13:58 
 * @author 蒋匿
 */
public interface CompanyCorporateAccountService extends IBaseService<CompanyCorporateAccountEntity,CompanyCorporateAccountMapper> {

    PageInfo<CompanyCorporateAccountVO> querCompanyCorporateAccountServicePageInfo(CompanyCorporateAccountQuery query);

    void updateStatus(Long id,Integer status,String photoUrl);

    /**
     * 修改对公户账号
     * @return
     */
    void update(CompanyCorporateAccountPO po,String account);


    CompanyCorporateAccountVO queryCompanyCorporateAccountDetail(Long id);

    /**
     * @Description 分页查询企业对公户列表
     * @Author  Kaven
     * @Date   2020/9/7 11:22
     * @Param   ComCorpAccQuery
     * @Return  List<CompanyCorpAccountVO>
     * @Exception  BusinessException
    */
    List<CompanyCorpAccountVO> listComCorpAccountPage(ComCorpAccQuery query) throws BusinessException;

    /**
     * @Description 查询对公户详情
     * @Author  Kaven
     * @Date   2020/9/7 14:53
     * @Param   id
     * @Return  CompanyCorpAccountDetailVO
     * @Exception
    */
    CompanyCorpAccountDetailVO getDetail(Long id) throws BusinessException;

    /**
     * @Description 对公户新增
     * @Author  HZ
     * @Date   2020/9/7 14:53
     * @Param   id
     * @Exception
     */
    CompanyCorporateAccountEntity  addCompanyCorporateAccount(CompanyCorporateAccountPO po,String account);

    /**
     * 开户账号明细
     *  @Description 对公户新增
     * @Author  HZ
     * @Date   2020/9/7 14:53
     */
    PageInfo<CompanyCorporateAccountDetailVO> queryCompanyCorporateAccountDetailList(CompanyCorporateAccountDetailQuery query);

    /**
     * @Description 对公户提现-获取收款个人银行卡信息
     * @Author  Kaven
     * @Date   2020/9/8 13:54
     * @Param   corporateAccountId oemCode
     * @Return  CorporateAccountBankCardVO
     * @Exception  BusinessException
    */
    CorporateAccountBankCardVO queryCorpBankCardInfo(Long corporateAccountId,String oemCode) throws BusinessException;

    /**
     * @Description 对公户提现-选择开票记录列表
     * @Author Kaven
     * @Date 2020/9/8 14:28
     * @Param CorporateAccountCollectionRecordQuery
     * @Return List<CorporateInvoiceOrderVO>
     * @Exception BusinessException
     */
    List<CorporateInvoiceOrderVO> listInvoiceOrderForCorp(CorporateAccountCollectionRecordQuery query) throws BusinessException;

    /**
     * 对公户申请-申请详情
     *
     * @return CompanyCorpAccApplyDetailVO
     * @Author yejian
     * @Date 2020/9/8 15:28
     * @Param oemCode
     */
    CompanyCorpAccApplyDetailVO applyDetail(String oemCode, Long memberId, Long parkId) throws BusinessException;

    /**
     * 查询企业对公户列表（xxljob使用）
     */
    List<CompanyCorporateAccountHandlerVO> queryAccountHandlerList();

    /**
     * 根据companyID查询企业对公户
     */
    CompanyCorporateAccountEntity queryCorpByCompanyId(Long companyId);

    /**
     * 申请条件校验
     * @param memberId
     * @return 0-不符合申请条件 1-符合申请条件
     */
    int applicationConditionCheck(Long memberId);

    /**
     * 根据用户id查询用户对公户列表
     * @param memberId
     * @return
     */
    List<CompanyCorpAccountVO> queryByMemberId(Long memberId);

    /**
     * 查询对公户续费详情信息
     * @param companyCorpAccId
     * @param currUserId
     * @return
     */
    CompanyCorpAccRenewDetailVO companyCorpAccRenewDetail(Long companyCorpAccId, Long currUserId);

    /**
     * 根据对公户id更新对公户过期状态
     * @param id
     * @param expirationTime
     */
    void updateOverdueStatus(Long id, Date expirationTime);

    /**
     * 获取已过期的对公户信息
     * @param overdueDays
     * @return
     */
    List<OverdueCompanyCropAccInfoVO> getOverdueCompanyCropAcc(Integer overdueDays);

    /**
     * 获取即将过期的对公户信息
     * @param surplusDays
     * @return
     */
    List<OverdueCompanyCropAccInfoVO> getWillExpireCompanyCropAcc(Integer surplusDays);

    /**
     * 更新对公户过期状态
     * @param surplusDays
     */
    void updateCompanyCorpAccOverdueStatus(Integer surplusDays);

    /**
     * 已过期对公户状态修改，包括是否发送短信通知标识
     */
    void updateOverdueCorpAccStatus();

    /**
     * 对公户申请-可申请园区名称
     * @param oemCode
     * @return
     */
    Map<String, String> applyUsablePark(String oemCode);
}

