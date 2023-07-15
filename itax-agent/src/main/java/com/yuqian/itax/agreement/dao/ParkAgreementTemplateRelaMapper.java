package com.yuqian.itax.agreement.dao;

import com.yuqian.itax.agreement.entity.ParkAgreementTemplateRelaEntity;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 园区与协议模板的关系表dao
 * 
 * @Date: 2022年02月11日 17:13:34 
 * @author 蒋匿
 */
@Mapper
public interface ParkAgreementTemplateRelaMapper extends BaseMapper<ParkAgreementTemplateRelaEntity> {

    /**
     * 批量插入
     * @param list
     */
    void batchInsert(@Param("list") List<ParkAgreementTemplateRelaEntity> list);

    /**
     * 删除园区绑定的办理协议
     * @param parkId
     */
    void deleteByParkId(@Param("parkId") Long parkId);

    /**
     * 根据园区id和产品id删除数据
     * @param parkId
     * @param product
     */
    void deleteByParkIdAndProductId(@Param("parkId") Long parkId,@Param("product") Long product);

    /**
     * 根据园区id获取园区对应模板信息
     * @param parkId
     * @return
     */
    List<AgreementTemplateInfoVO> getTemplateInfo(@Param("parkId") Long parkId,@Param("templateType") Integer templateType,@Param("productId") Long productId);

    /**
     * 根据园区id和产品id查询模板信息
     * @param parkId
     * @param productId
     * @return
     */
    List<AgreementTemplateInfoVO> getTemplateInfoByParkIdAndProductId(@Param("parkId") Long parkId,@Param("productId") Long productId);

    /**
     * 根据协议模板id获取园区与协议模板的关系表id
     * @param agreementTemplateId
     * @return
     */
    List<Long> getParkAgreementIdByAgreementTemplateId(@Param("agreementTemplateId") Long agreementTemplateId);
	
}

