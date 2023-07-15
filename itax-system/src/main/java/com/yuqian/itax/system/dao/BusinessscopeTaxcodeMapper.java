package com.yuqian.itax.system.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.BusinessscopeTaxcodeEntity;
import com.yuqian.itax.system.entity.query.BusinessScopeTaxCodeQuery;
import com.yuqian.itax.system.entity.vo.BusinessScopeTaxCodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 经营范围税费分类编码表dao
 * 
 * @Date: 2021年11月09日 17:23:33 
 * @author 蒋匿
 */
@Mapper
public interface BusinessscopeTaxcodeMapper extends BaseMapper<BusinessscopeTaxcodeEntity> {

    /**
     * 经营范围基础库列表页查询
     * @param query
     * @return
     */
    List<BusinessScopeTaxCodeVO> queryPageList(BusinessScopeTaxCodeQuery query);

    /**
     * 根据经营范围和税收分类编码查询数据
     * @param businessScopName
     * @param taxClassificationCode
     * @return
     */
    BusinessscopeTaxcodeEntity getVBusinessScopeByScopNameAndCode(@Param("businessScopName") String businessScopName, @Param("taxClassificationCode")String taxClassificationCode);

    /**
     * 批量新增
     */
    void batchAddBusinessScopeTaxCode(@Param("list") List<BusinessscopeTaxcodeEntity> list);


    /**
     * 校验
     * @param list
     * @return
     */
    List<String> checkByBusiness(List<String> list);

    /**
     * 根据经营范围名称查找经营范围
     * @param scopName
     * @return
     */
    List<BusinessScopeTaxCodeVO> getBusinessScopeByScopName(@Param("scopName") String scopName);

    /**
     * 根据经营范围和税收分类编码查询数据
     * @param list
     * @return
     */
    List<BusinessscopeTaxcodeEntity> getBusinessScopeByConcatNameAndCode(@Param("list") List<String> list);

    /**
     * 根据经营范围查询数据
     * @param list
     * @return
     */
    List<BusinessscopeTaxcodeEntity> getBusinessScopeByConcatName(@Param("list") List<String> list);
}

