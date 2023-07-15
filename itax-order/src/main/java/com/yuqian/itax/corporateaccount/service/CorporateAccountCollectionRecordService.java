package com.yuqian.itax.corporateaccount.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.corporateaccount.dao.CorporateAccountCollectionRecordMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountCollectionRecordEntity;
import com.yuqian.itax.user.entity.query.CompanyCorporateAccountVerificationQuery;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;
import com.yuqian.itax.user.entity.vo.CorporateAccountCollectionRecordVO;
import com.yuqian.itax.user.entity.vo.CorporateAccountVO;
import com.yuqian.itax.user.entity.vo.CorporateAccountVOAdmin;
import com.yuqian.itax.user.entity.vo.PublicAccountDetailVO;

import java.util.List;

/**
 *  对公户银行收款记录表service
 * 
 * @Date: 2020年09月07日 09:12:46 
 * @author 蒋匿
 */
public interface CorporateAccountCollectionRecordService extends IBaseService<CorporateAccountCollectionRecordEntity,CorporateAccountCollectionRecordMapper> {


    /**
     * @Description 查询对公户账户明细信息
     * @Author  Kaven
     * @Date   2020/9/7 16:16
     * @Param   CorporateAccountCollectionRecordQuery
     * @Return PublicAccountDetailVO
     * @Exception BusinessException
    */
    PublicAccountDetailVO getCorpAccountCollectionRecords(CorporateAccountCollectionRecordQuery query) throws BusinessException;


    /**
     * @Description 查询对公户账户明细信息（后台）
     * @Author  HZ
     * @Date   2020/9/10 16:16
     * @Param   CorporateAccountCollectionRecordQuery
     * @Return PublicAccountDetailVO
     * @Exception BusinessException
     */
    PageInfo<CorporateAccountVOAdmin> queryCorpAccountCollectionRecordsAdmin(CorporateAccountCollectionRecordQuery query) throws BusinessException;

    /**
     * @Description 开票提现金额和收款记录可提现金额回退
     * @Author  Kaven
     * @Date   2020/9/9 14:19
     * @Param   orderNo externalOrderNo upStatusCode upResultMsg
     * @Return
     * @Exception  BusinessException
    */
    void returnWithdrawAmount(String orderNo,String externalOrderNo,String upStatusCode,String upResultMsg) throws BusinessException;

    /**
     * @Description 更新剩余提现额度
     * @Author  Kaven
     * @Date   2020/9/9 14:44
     * @Param recordId withdrawalAmount flag:0-扣减 1-添加
     * @Return
     * @Exception BusinessException
    */
    void updateRemainingWithdrawAmount(Long recordId, Long withdrawalAmount, int flag) throws BusinessException;

    /**
     * 额度核销
     * @param query
     */
    void companyCorporateAccountVerification(CompanyCorporateAccountVerificationQuery query,String account);

    /**
     * 查询明细列表
     */
    List<CorporateAccountCollectionRecordVO> queryCorpAccountCollectionRecords(CorporateAccountCollectionRecordQuery query);

    /**
     * 查询明细列表
     */
    List<CorporateAccountVOAdmin> getCorpAccountCollectionRecordsAdmin(CorporateAccountCollectionRecordQuery query);
}

