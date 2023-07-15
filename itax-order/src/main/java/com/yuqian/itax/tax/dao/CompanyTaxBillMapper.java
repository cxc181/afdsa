package com.yuqian.itax.tax.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.vo.PeriodPaidTaxVo;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.query.*;
import com.yuqian.itax.tax.entity.vo.*;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 企业税单表dao
 *
 * @Date: 2020年12月03日 10:36:23
 * @author 蒋匿
 */
@Mapper
public interface CompanyTaxBillMapper extends BaseMapper<CompanyTaxBillEntity> {

    /**
     * 查询指定时间内得企业税单初始话数据
     * @param query
     * @return
     */
    public List<CompanyTaxBillXXJOBVO> queryCompanyTaxBillByTime(CompanyTaxBillQuery query);

    /**
     * 新增指定税期得企业税单得初始化数据
     * @param query
     */
    void insertCompanyTaxBillByTaxBillYearAndTaxBillSeasonal(CompanyTaxBillQuery query);
    /**
     * 根据税号查询企业税单
     * @param ein
     * @return
     */
    CompanyTaxBillEntity queryCompanyTaxBillByEin(@Param("ein") String ein,@Param("taxBillSeasonal")int taxBillSeasonal,@Param("taxBillYear")int taxBillYear,@Param("companyId")Long comapnyId);

    /**
     * 根据税号和园区税单id查企业税单信息
     * @param ein
     * @param parkTaxBillId
     * @return
     */

    CompanyTaxBillEntity queryCompanyTaxBillByEinAndParkTaxBillId(@Param("ein") String ein,@Param("parkTaxBillId")Long  parkTaxBillId);

    /**
     * 根据园区税单id查询同一期的企业税单
     */
    List<CompanyTaxBillEntity> queryCompanyTaxBillByParkTaxBillId(@Param("parkTaxBillId")Long parkTaxBillId);
    /**
     * 查询企业税单
     * @param query
     * @return
     */
    List<CompanyTaxBillListVO> listCompanyTaxBill(CompanyTaxBillQuery query);

    /**
     * 企业税单（分页）后台
     * @param query
     * @return
     */
    List<CompanyTaxBillListVOAdmin> queryCompanyTaxBillList(CompanyTaxBillQueryAdmin query);
    /**
     * 下载税单
     * @param query
     * @return
     */
    List<DownloadCompanyTaxBillVOAdmin> queryDownloadCompanyTaxBillList(CompanyTaxBillQueryAdmin query);


    /**
     *
     */
    List<TaxBillInfoToMemberVO> queryCompanyTaxBillInfoToMember(TaxBillInfoToMemberQuery query);

    /**
     * 根据超时时间来查询超时未补交的企业税单
     */
    List<CompanyTaxBillEntity> queryCompanyTaxByOverTime(@Param("overTime") Integer overTime, @Param("companyId") Long companyId);

    /**
     * 根据企业id查询未补交的企业税单
     * @param companyId
     * @return
     */
    List<CompanyTaxBillEntity> queryCompanyTaxByCompanyId(@Param("companyId") Long companyId);

    /**
     *
     * @param parkTaxBillId
     * @return
     */
    Long queryShouldCompanyByParkTaxBillId(@Param("parkTaxBillId") Long parkTaxBillId);

    /**
     * 统计指定税期年和月得增值税，所得税，附加税之和
     */
    Map<String, Object> queryCompanyTaxBillTotalVatIiTfJByTime(CompanyTaxBillQueryAdmin query);
    /**
     * 批量更新园区税单ID
     */
    void updateCompanyTaxBillByParkId(@Param("parkId")Long parkId,@Param("parkTaxBillId") Long parkTaxBillId,@Param("taxBillSeasonal") int taxBillSeasonal,@Param("taxBillYear") int taxBillYear);

    /**
     * 预税单列表
     * @param query
     * @return
     */
    List<CompanyTaxBillPrepareListVO> listPagePrepareCompanyTaxBill(PrepareCompanyTaxBillQuery query);

    /**
     * 预税单详情
     *
     * @param companyId
     * @param memberId
     * @return
     */
    CompanyTaxBillPrepareVO prePareDetail(@Param("companyId") Long companyId, @Param("memberId") Long memberId);

    /**
     * 查询企业应缴增值税
     * @param companyId
     * @param year
     * @return
     */
    PeriodPaidTaxVo queryPayableTaxFee(@Param("companyId") Long companyId, @Param("year") Integer year, @Param("seasonal") Integer seasonal);

    /**
     * 根据园区税单id查询企业税单状态为非为正常的企业税单
     * @param parkTaxBillId
     * @return
     */
    List<Long> getCompanyTaxBillByTaxBillStatus(@Param("parkTaxBillId") Long parkTaxBillId);

    /**
     * 查询企业待处理税单
     * @param query
     * @return
     */
    List<PendingTaxBillVO> queryPendingTaxBill(PendingTaxBillQuery query);

    /**
     * 下载查账征收税单
     * @param query
     * @return
     */
    List<DownloadCompanyTaxBillByAccountsVO> queryDownloadCompanyTaxBillListByAccounts(CompanyTaxBillQueryAdmin query);

    /**
     * 根据企业id和时间查询该企业再该时间之后的为申报中/待补税的税单
     * @return
     */
    List<CompanyTaxBillEntity> getCompanyTaxBillByCompanyIdAndAddTime(@Param("companyId")Long companyId,@Param("addTime") Date addTime);

    /**
     * 获取园区的作废/红冲企业数
     * @param parkTaxBillId
     * @return
     */
    Integer getCancellationCompanyByParkTaxBillId(@Param("parkTaxBillId") Long parkTaxBillId);

    /**
     * 根据园区税单id查询待填报成本的税单
     * @param
     * @return
     */
    List<CompanyTaxBillEntity> getCompanyTaxBillByStatus(@Param("companyId") Long companyId);

    /**
     * 获取核对税单
     * @param parkTaxBillId
     * @return
     */
    List<ApprovedTaxBillVO> getApprovedTaxBill(@Param("parkTaxBillId") Long parkTaxBillId);

    /**
     * 根据税单年获取历史成本汇总
     * @param taxBillYear
     * @param companyId
     * @param ein
     * @return
     */
    List<Map<String,Long>> getCostItemByCompanyBillsYear(@Param("taxBillYear") Integer taxBillYear, @Param("taxBillSeasonal") Integer taxBillSeasonal,
                                                         @Param("companyId") Long companyId, @Param("ein") String ein, @Param("costItemName") String costItemName);
}

