package com.yuqian.itax.tax.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.entity.vo.PeriodPaidTaxVo;
import com.yuqian.itax.tax.dao.CompanyTaxBillMapper;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.dto.FillCostDTO;
import com.yuqian.itax.tax.entity.dto.TaxAuditDTO;
import com.yuqian.itax.tax.entity.query.*;
import com.yuqian.itax.tax.entity.vo.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 企业税单表service
 *
 * @Date: 2020年12月03日 10:36:23
 * @author 蒋匿
 */
public interface CompanyTaxBillService extends IBaseService<CompanyTaxBillEntity,CompanyTaxBillMapper> {

    /**
     * 查询指定时间内得企业税单初始话数据
     */
    List<CompanyTaxBillXXJOBVO> queryCompanyTaxBillByTime(CompanyTaxBillQuery query);

    /**
     * 新增(指定税期)企业税单初始话数据
     */
    void insertCompanyTaxBillByTaxBillYearAndTaxBillSeasonal(CompanyTaxBillQuery query);
    /**
     * 根据税号查询企业税单
     */
    CompanyTaxBillEntity queryCompanyTaxBillByEin(String ein, int taxBillSeasonal, int taxBillYear, Long companyId);

    /**
     * 根据税号和园区税单id查企业税单信息
     * @param ein
     * @param parkTaxBillId
     * @return
     */
    CompanyTaxBillEntity queryCompanyTaxBillByEinAndParkTaxBillId(String ein ,Long parkTaxBillId);
    /**
     * 分页查询企业税单
     * @param query
     * @return
     */
    PageInfo<CompanyTaxBillListVO> listPageCompanyTaxBill(CompanyTaxBillQuery query);

    /**
     * 查询企业税单
     * @param query
     * @return
     */
    List<CompanyTaxBillListVO> listCompanyTaxBill(CompanyTaxBillQuery query);

    /**
     * 根据园区税单id查询同一期的企业税单
     */
    List<CompanyTaxBillEntity> queryCompanyTaxBillByParkTaxBillId(Long parkTaxBillId);

    /**
     * 企业税单列表（分页）
     */
    PageInfo<CompanyTaxBillListVOAdmin> queryCompanyTaxBillPageInfo(CompanyTaxBillQueryAdmin query);

    /**
     * 企业税单列表（分页）
     */
    List<CompanyTaxBillListVOAdmin> queryCompanyTaxBillList(CompanyTaxBillQueryAdmin query);
    /**
     * 下载税单
     */
    List<DownloadCompanyTaxBillVOAdmin> queryDownloadCompanyTaxBillList(CompanyTaxBillQueryAdmin query);

    /**
     * 下载查账征收税单
     * @param query
     * @return
     */
    List<DownloadCompanyTaxBillByAccountsVO> queryDownloadCompanyTaxBillListByAccounts(CompanyTaxBillQueryAdmin query);

    /**
     * 查询企业税单每个用户的应缴补交企业情况
     */
    List<TaxBillInfoToMemberVO> queryCompanyTaxBillInfoToMember(TaxBillInfoToMemberQuery query);
    /**
     * 根据超时时间查询未补交的企业税单
     */
    List<CompanyTaxBillEntity> queryCompanyTaxByOverTime(Integer overTime, Long companyId);

    /**
     * 根据企业id查询未补交的企业税单
     * @param companyId
     * @return
     */
    List<CompanyTaxBillEntity> queryCompanyTaxByCompanyId(Long companyId);
    /**
     * 根据园区税单id查询所有企业税单应缴
     */
    Long queryShouldCompanyByParkTaxBillId(Long parkTaxBillId);
    /**
     * 封装XXJOB
     */
    void xxjob();

    /**
     * 统计指定税期年和月得增值税，所得税，附加税之和
     */
    Map<String,Object> queryCompanyTaxBillTotalVatIiTfJByTime(CompanyTaxBillQueryAdmin query);

    /**
     * 根据parkId给企业税单赋值园区税单ID
     */
    void updateCompanyTaxBillByParkId(Long parkId,Long parkTaxBillId,int taxBillSeasonal,  int taxBillYear);

    /**
     * 预税单列表
     * @param query
     * @return
     */
    PageInfo<CompanyTaxBillPrepareListVO> listPagePrepareCompanyTaxBill(PrepareCompanyTaxBillQuery query);

    /**
     * 预税单详情
     *
     * @param companyId
     * @param memberId
     * @return
     */
    CompanyTaxBillPrepareVO prePareDetail(Long companyId, Long memberId);

    /**
     * 查询企业应缴税
     * @param companyId
     * @param year
     * @return
     */
    PeriodPaidTaxVo queryPayableTaxFee(Long companyId, Integer year, Integer seasonal);



    /**
     * 校验批量报税数据
     * @param excelList
     * @return
     */
    Map<String,Object>  handleBill( List<TaxDeclarationVO> excelList,Long parkTaxBillId,String userName);

    /**
     * 添加扣除金额
     * @param excelList
     * @param parkTaxBillId
     * @param userName
     * @return
     */
    Map<String, Object> batchAddAmount(List<DeductionAmountVO> excelList, Long parkTaxBillId, String userName);

    /**
     * 待处理税单
     */
    List<PendingTaxBillVO> pendingTaxBill(PendingTaxBillQuery query);

    /**
     * 根据企业id和时间查询该企业再该时间之后的为申报中/待补税的税单
     * @return
     */
    List<CompanyTaxBillEntity> getCompanyTaxBillByCompanyIdAndAddTime(Long companyId, Date addTime);

    /**
     * 重置企业税单
     * @param entity
     * @param userName
     */
    void rechargeCompanyBill(CompanyTaxBillEntity entity,String userName,String remark);

    /**
     * 企业报税
     * @param entity
     * @param userName
     */
    void declareTax(CompanyTaxBillEntity entity,String userName);

    /**
     * 根据园区税单id查询待填报成本的税单
     * @param
     * @return
     */
    List<CompanyTaxBillEntity> getCompanyTaxBillByStatus(Long companyId);

    /**
     * 查账征收方式税单详情数据查询
     */
    void detailOfAuditCollection(CompanyTaxBillVO companyTaxBillVO);

    /**
     * 更新税单并生成企业税单历史记录
     * @param entity
     */
    void updateBillAndInsertChange(CompanyTaxBillEntity entity,String descrip);

    /**
     * 获取核对税单
     * @param parkTaxBillId
     * @return
     */
    List<ApprovedTaxBillVO> getApprovedTaxBill(Long parkTaxBillId);

    /**
     * 获取填报成本详情
     * @param companyTaxBillId
     * @return
     */
    TaxBillFillCostDetailVO getFillCostDetail(Long companyTaxBillId);

    /**
     * 填报成本
     * @param dto
     */
    CompanyTaxBillEntity fillCost(FillCostDTO dto);

    /**
     * 批量填报成本
     * @param excelList
     * @param parkTaxBillId
     * @param operator
     * @return
     */
    Map<String, Object> batchFillCost(List<TaxFillCostVO> excelList, Long parkTaxBillId, String operator);

    /**
     * 税单（财务）审核
     * @param
     */
    void taxAudit(TaxAuditDTO dto);

    /**
     * 批量税单（财务）审核
     * @param excelList
     * @param parkTaxBillId
     * @param operator
     * @return
     */
    Map<String, Object> batchTaxAudit(List<TaxAuditVO> excelList, Long parkTaxBillId, String operator);

    /**
     * 重新提交税单
     * @param companyTaxBillId
     * @param incomeTaxRefundAmount
     * @param operator
     */
    void resubmitTaxBill(Long companyTaxBillId, Long incomeTaxRefundAmount, String operator);

    /**
     * 根据税单年获取历史成本汇总
     * @param companyTaxBillId
     * @return
     */
    TaxBilHistoryCostItemVO getCostItemByCompanyBillsYear(Long companyTaxBillId);
}

