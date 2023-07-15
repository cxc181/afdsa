package com.yuqian.itax.agent.service;

import com.yuqian.itax.agent.dao.OemParkRelaMapper;
import com.yuqian.itax.agent.entity.OemParkRelaEntity;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import com.yuqian.itax.common.base.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * 机构与园区的关系service
 *
 * @Date: 2019年12月07日 20:35:24 
 * @author 蒋匿
 */
public interface OemParkRelaService extends IBaseService<OemParkRelaEntity, OemParkRelaMapper> {

    /**
     * 通过oemCode获得关联园区ID
     */
    List<Long> queryOemParkIdList(String oemCode);

    /**
     * 通过oemCode查询已配置对公户提现的园区
     */
    Map<String, String> queryOemParkCorporate(String oemCode);

    /**
     * 根据oemCode和园区id获取数据
     * @param oemCode
     * @param parkId
     * @return
     */
    OemParkRelaEntity queryOemParkByOemCodeAndParkId(String oemCode,Long parkId);

    /**
     * 根据oemCode 和园区id获取模板数据
     * @param oemCode
     * @param parkId
     * @return
     */
    AgreementTemplateInfoVO getAgreementTemplateByOemCodeAndParkId(String oemCode, Long parkId);

    /**
     * 根据协议模板id查询机构与园区的关系表id
     * @param agreementTemplateId
     * @return
     */
    List<Long> getOemParkIdByAgreementTemplateId(Long agreementTemplateId);

}

