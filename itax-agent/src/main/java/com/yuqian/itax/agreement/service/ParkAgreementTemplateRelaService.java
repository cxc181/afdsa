package com.yuqian.itax.agreement.service;

import com.yuqian.itax.agreement.dao.ParkAgreementTemplateRelaMapper;
import com.yuqian.itax.agreement.entity.ParkAgreementTemplateRelaEntity;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import com.yuqian.itax.common.base.service.IBaseService;

import java.util.List;

/**
 * 园区与协议模板的关系表service
 * 
 * @Date: 2022年02月11日 17:13:34 
 * @author 蒋匿
 */
public interface ParkAgreementTemplateRelaService extends IBaseService<ParkAgreementTemplateRelaEntity,ParkAgreementTemplateRelaMapper> {

    /**
     * 批量插入
     * @param list
     */
    void batchInsert(List<ParkAgreementTemplateRelaEntity> list);

    /**
     * 删除园区绑定的办理协议
     * @param parkId
     */
    void deleteByParkId(Long parkId);


    /**
     * 根据园区id和产品id删除数据
     * @param parkId
     * @param product
     */
    void deleteByParkIdAndProductId(Long parkId,Long product);

    /**
     * 根据园区id获取园区对应模板信息
     * @param parkId
     * @return
     */
    List<AgreementTemplateInfoVO> getTemplateInfo(Long parkId,Integer templateType,Long productId);

    /**
     * 根据园区id和产品id查询模板信息
     * @param parkId
     * @param productId
     * @return
     */
    List<AgreementTemplateInfoVO> getTemplateInfoByParkIdAndProductId(Long parkId,Long productId);

    /**
     * 根据协议模板id获取园区与协议模板的关系表id
     * @param agreementTemplateId
     * @return
     */
    List<Long> getParkAgreementIdByAgreementTemplateId(Long agreementTemplateId);

}

