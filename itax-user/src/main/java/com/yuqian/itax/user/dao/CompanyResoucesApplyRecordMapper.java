package com.yuqian.itax.user.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.CompanyResoucesApplyRecordEntity;
import com.yuqian.itax.user.entity.query.CompanyResoucesApplyRecordQuery;
import com.yuqian.itax.user.entity.vo.ComResApplyRecordDetailVO;
import com.yuqian.itax.user.entity.vo.CompanyCertListApiVO;
import com.yuqian.itax.user.entity.vo.CompanyResoucesApplyRecordExportVO;
import com.yuqian.itax.user.entity.vo.CompanyResoucesApplyRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 企业资源申请记录dao
 * 
 * @Date: 2020年03月25日 09:30:46 
 * @author 蒋匿
 */
@Mapper
public interface CompanyResoucesApplyRecordMapper extends BaseMapper<CompanyResoucesApplyRecordEntity> {

    /**
     * @Description 根据订单号查询订单
     * @Param  orderNo
     * @Return CompanyResoucesApplyRecordEntity
     */
    CompanyResoucesApplyRecordEntity queryByOrderNo(String orderNo);

    List<CompanyResoucesApplyRecordVO> queryCompanyResoucesApplyRecord(CompanyResoucesApplyRecordQuery query);

    List<CompanyResoucesApplyRecordExportVO> queryExportCompanyResoucesApplyRecord(CompanyResoucesApplyRecordQuery query);

    Map<String, Integer> sumCompanyResoucesApplyRecord(CompanyResoucesApplyRecordQuery query);

    void batchUpdateStatus (@Param("ids") List<Long> ids, @Param("status") Integer status,@Param("updateUser")String updateUser, @Param("updateTime") Date updateTime);

    /**
     * 查询企业资源申请列表
     * @param memberId
     * @param oemCode
     * @param companyId
     * @return
     */
    List<CompanyResoucesApplyRecordEntity> listCompResApplyRecord(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("companyId")Long companyId);

    /**
     * 查询企业资源申请待收货列表
     * @return
     */
    List<CompanyResoucesApplyRecordEntity> listCompResApplyRecordToRec();

    /**
     * 查询证件是否在申请中
     * @param oemCode
     * @param companyId
     * @return
     */
    int checkCertOrder(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("companyId")Long companyId, @Param("applyType")Integer applyType, @Param("applyResouces")String applyResouces);

    /**
     * 查询证件是否在园区
     * @param oemCode
     * @param companyId
     * @return
     */
    int checkCertIsInPark(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("companyId")Long companyId, @Param("applyResouces")String applyResouces);

    /**
     * 查询企业资源申请详情
     * @param orderNo
     * @return
     */
    ComResApplyRecordDetailVO queryCompResApplyRecordDetail(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("orderNo")String orderNo);

    /**
     * 查询10分钟未支付的证件领用订单
     * @return
     */
    List<CompanyResoucesApplyRecordEntity> certApplyOrderListByType();

    /**
     * 企业证件申请、归还列表查询
     * @param oemCode
     * @param companyName
     * @param regPhone
     * @param orderNo
     * @return
     */
    List<CompanyCertListApiVO> getCertListByQuery(@Param("oemCode")String oemCode, @Param("companyName")String companyName,
                                                  @Param("regPhone")String regPhone, @Param("orderNo")String orderNo);
}

