package com.yuqian.itax.agent.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.po.OemParamsPO;
import com.yuqian.itax.agent.entity.query.OemParamsQuery;
import com.yuqian.itax.agent.entity.vo.OemParamsVO;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.dao.OemParamsMapper;

import java.util.List;

/**
 * 机构参数配置service
 * 
 * @Date: 2019年12月06日 17:27:05 
 * @author Kaven
 */
public interface OemParamsService extends IBaseService<OemParamsEntity,OemParamsMapper> {

    /**
     * @Description 根据机构编码和参数类型获取配置
     * @Author  Kaven
     * @Date   2019/12/9 9:33
     * @Param  oemCode paramsType
     * @Return OemParamsEntity
    */
    OemParamsEntity getParams(String oemCode, int paramsType);

    PageInfo<OemParamsVO> querySysOemParamsPageInfo(OemParamsQuery query);

    void addSysOemParams(OemParamsPO po,String account);

    void updateSysDictionaryList(OemParamsPO po,String account);

    /**
     * 根据商户号获取易税接入参数
     * @param merchantCode
     * @return
     */
    OemParamsEntity getYishuiParam(String merchantCode);
}

