package com.yuqian.itax.system.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.ClassificationCodeVatEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;


/**
 * 税收分类编码与增值税率的关系表dao
 *
 * @Date: 2022年08月19日 11:38:14
 * @author cxz
 */
@Mapper
public interface ClassificationCodeVatMapper extends BaseMapper<ClassificationCodeVatEntity> {

    /**
     * 根据税收分类编码查询对应的  增值税率, 用逗号拼接
     * @param taxClassificationCode
     * @return
     */
    String queryVatFeeRate(@Param("taxClassificationCode") String taxClassificationCode);

    /**
     * 根据税收分类编码查询对应的  增值税率
     * @param taxClassificationCode
     * @return
     */
    List<ClassificationCodeVatEntity> queryVatFeeRateList(@Param("taxClassificationCode") String taxClassificationCode);

    /**
     * 根据企业开票类目查询增值税率
     * @param companyCategoryId
     * @return
     */
    List<BigDecimal> queryVatRateByCompanyCategoryId(Long companyCategoryId);
}
