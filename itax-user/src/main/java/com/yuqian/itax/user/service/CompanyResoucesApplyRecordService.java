package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.CompanyResoucesApplyRecordEntity;
import com.yuqian.itax.user.dao.CompanyResoucesApplyRecordMapper;
import com.yuqian.itax.user.entity.query.CompResApplyRecordQuery;
import com.yuqian.itax.user.entity.query.CompanyCertListApiQuery;
import com.yuqian.itax.user.entity.query.CompanyResoucesApplyRecordQuery;
import com.yuqian.itax.user.entity.vo.ComResApplyRecordDetailVO;
import com.yuqian.itax.user.entity.vo.CompanyCertListApiVO;
import com.yuqian.itax.user.entity.vo.CompanyResoucesApplyRecordExportVO;
import com.yuqian.itax.user.entity.vo.CompanyResoucesApplyRecordVO;

import java.util.List;
import java.util.Map;

/**
 * 企业资源申请记录service
 * 
 * @Date: 2020年03月25日 09:30:46 
 * @author 蒋匿
 */
public interface CompanyResoucesApplyRecordService extends IBaseService<CompanyResoucesApplyRecordEntity,CompanyResoucesApplyRecordMapper> {
    /**
     * 企业资源申请分页
     * @param query
     * @return
     */

    PageInfo<CompanyResoucesApplyRecordVO> companyResoucesApplyRecordPageInfo(CompanyResoucesApplyRecordQuery query);
    /**
     * 企业资源申请出库列表
     * @param query
     * @return
     */
    List<CompanyResoucesApplyRecordVO> companyResoucesApplyRecordList(CompanyResoucesApplyRecordQuery query);
    /**
     * 企业资源申请出库导出列表
     * @param query
     * @return
     */
    List<CompanyResoucesApplyRecordExportVO> companyResoucesApplyRecordExportList(CompanyResoucesApplyRecordQuery query);

    /**
     * 企业资源申请出库弹窗
     * @param query
     * @return
     */
    Map<String, Integer> sumCompanyResoucesApplyRecordList(CompanyResoucesApplyRecordQuery query);

    /**
     * 企业资源申请取消
     */
    void companyResoucesApplyRecordCancel(Long id,String account);

    /**
     * 分页查询企业资源申请列表
     * @param memberId
     * @param oemCode
     * @param query
     * @return
     */
    List<CompanyResoucesApplyRecordEntity> listCompResApplyRecord(Long memberId, String oemCode, CompResApplyRecordQuery query);

    /**
     * 查询企业资源申请待收货列表
     * @return
     */
    List<CompanyResoucesApplyRecordEntity> listCompResApplyRecordToRec();

    /**
     * 查询企业资源申请详情
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @return
     */
    ComResApplyRecordDetailVO queryCompResApplyRecordDetail(Long memberId, String oemCode, String orderNo);

    /**
     * 查询10分钟未支付的证件领用订单
     * @return
     */
    List<CompanyResoucesApplyRecordEntity> certApplyOrderListByType();

    /**
     * 企业证件申请、归还列表查询
     * @param oemCode
     * @param query
     * @return
     */
    List<CompanyCertListApiVO> getCertListByQuery(String oemCode, CompanyCertListApiQuery query);
}

