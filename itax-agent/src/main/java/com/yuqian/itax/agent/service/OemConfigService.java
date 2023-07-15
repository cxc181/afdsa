package com.yuqian.itax.agent.service;

import com.yuqian.itax.agent.entity.vo.OemConfigVO;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.dao.OemConfigMapper;

import java.util.List;

/**
 * oem机构参数配置表service
 * 
 * @Date: 2020年07月21日 11:19:36 
 * @author 蒋匿
 */
public interface OemConfigService extends IBaseService<OemConfigEntity,OemConfigMapper> {

    /**
     * 查询机构配置税率
     * @param oemCode
     * @param parentParamsCode
     * @param taxType
     */
    List<OemConfigVO> queryOemRate(String oemCode, String parentParamsCode, Integer taxType);

    /**
     * 查询机构配置对象
     * @param oemCode
     * @param parentParamsCode
     */
    OemConfigEntity queryOemConfigByCode(String oemCode, String parentParamsCode);

    /**
     * 根据oem机构查询是否接入国金
     * @param oemCode
     * @param paramsCode
     * @param paramsValue
     * @return
     */
    OemConfigEntity queryOemConfigByOemCodeAndParamsCode(String oemCode,String paramsCode,String paramsValue);
}

