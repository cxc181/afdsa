package com.yuqian.itax.park.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.query.IndustryPolicyQuery;
import com.yuqian.itax.park.entity.vo.IndustryPolicyVO;
import com.yuqian.itax.park.entity.vo.TaxPolicySelectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 税费政策dao
 * 
 * @Date: 2019年12月12日 18:38:59 
 * @author 蒋匿
 */
@Mapper
public interface TaxPolicyMapper extends BaseMapper<TaxPolicyEntity> {

    /**
     * 根据园区id获取园区政策配置企业类型
     * @param parkId
     * @return
     */
    List<Map<String,Object>> queryTaxPolicyCompanyTypeByParkId(@Param("parkId") Long parkId);
    /**
     *
     * 根据园区ID和企业类型查询税费政策信息
     */
    List<TaxPolicyEntity> queryTaxPolicyByParkIdAndCompanyType(@Param("parkId") Long parkId,@Param("companyType") Integer companyType,@Param("taxpayerType") Integer taxpayerType);

    List<TaxPolicySelectVO> queryTaxPolicySelectByParkId(@Param("parkId") Long parkId);

    TaxPolicyEntity queryTaxPolicy(@Param("parkId") Long parkId, @Param("companyType") Integer companyType, @Param("taxType") Integer taxType, @Param("calType") Integer calType);

    /**
     * 查询列外行业税率
     * @param query
     * @return
     */
    List<IndustryPolicyVO> listIndustryRules(IndustryPolicyQuery query);

    /**
     * 根据园区id和企业类型查询税费政策
     * @param parkId
     * @param companyType
     * @return
     */
    TaxPolicyEntity queryTaxPolicyByParkId(@Param("parkId") Long parkId, @Param("companyType") Integer companyType,@Param("taxpayerType") Integer taxpayerType);

    /**
     * 根据征收方式查询税费政策
     * @param oemCode
     * @param incomeLevyType
     * @return
     */
    List<TaxPolicyEntity> queryByIncomeLevyType(@Param("oemCode") String oemCode, @Param("incomeLevyType") Integer incomeLevyType);
}

