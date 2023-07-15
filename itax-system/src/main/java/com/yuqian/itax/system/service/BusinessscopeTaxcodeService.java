package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.system.dao.BusinessscopeTaxcodeMapper;
import com.yuqian.itax.system.entity.BusinessScopeEntity;
import com.yuqian.itax.system.entity.BusinessscopeTaxcodeEntity;
import com.yuqian.itax.system.entity.query.BusinessScopeTaxCodeQuery;
import com.yuqian.itax.system.entity.vo.BusinessScopeBatchVO;
import com.yuqian.itax.system.entity.vo.BusinessScopeTaxCodeVO;

import java.util.List;
import java.util.Map;

/**
 * 经营范围税费分类编码表service
 * 
 * @Date: 2021年11月09日 17:23:33 
 * @author 蒋匿
 */
public interface BusinessscopeTaxcodeService extends IBaseService<BusinessscopeTaxcodeEntity,BusinessscopeTaxcodeMapper> {

    /**
     * 经营范围基础库列表页查询
     * @param query
     * @return
     */
    PageResultVo<BusinessScopeTaxCodeVO> list(BusinessScopeTaxCodeQuery query);

    /**
     * 查询数据
     * @param query
     * @return
     */
    List<BusinessScopeTaxCodeVO> queryBusinessScopeTaxcode(BusinessScopeTaxCodeQuery query);

    /**
     * 根据经营范围和税收分类编码查询数据
     * @param businessScopName
     * @param taxClassificationCode
     * @return
     */
    BusinessscopeTaxcodeEntity getVBusinessScopeByScopNameAndCode(String businessScopName,String taxClassificationCode);

    /**
     *校验保存的数据
     * @param list
     * @param userName
     * @return
     */
    Map<String,Object> checkScopeTaxCode(List<BusinessScopeBatchVO> list,String userName);

    /**
     * 批量新增
     */
    void batchAddBusinessScopeTaxCode(List<BusinessscopeTaxcodeEntity> list);

    /**
     * 校验经营范围是否存在基础库中
     * @param business
     * @return
     */
    List<String> checkByBusiness(String business);

    /**
     * 根据经营范围名称查找经营范围
     * @param scopName
     * @return
     */
    List<BusinessScopeTaxCodeVO> getBusinessScopeByScopeName(String scopName);
}

