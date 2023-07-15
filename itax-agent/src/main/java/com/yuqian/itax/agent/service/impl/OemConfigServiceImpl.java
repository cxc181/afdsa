package com.yuqian.itax.agent.service.impl;

import com.yuqian.itax.agent.entity.vo.OemConfigVO;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.agent.dao.OemConfigMapper;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.service.OemConfigService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("oemConfigService")
public class OemConfigServiceImpl extends BaseServiceImpl<OemConfigEntity,OemConfigMapper> implements OemConfigService {

    @Override
    public List<OemConfigVO> queryOemRate(String oemCode, String parentParamsCode, Integer taxType) {
        return mapper.queryOemRate(oemCode, parentParamsCode, taxType);
    }

    /**
     * 查询机构配置对象
     * @param oemCode
     * @param parentParamsCode
     */
    public OemConfigEntity queryOemConfigByCode(String oemCode, String parentParamsCode){
        OemConfigEntity oemConfigEntity = new OemConfigEntity();
        oemConfigEntity.setOemCode(oemCode);
        oemConfigEntity.setParamsCode(parentParamsCode);
        List<OemConfigEntity> list= mapper.select(oemConfigEntity);
        if(list!=null && list.size() == 1){
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    public OemConfigEntity queryOemConfigByOemCodeAndParamsCode(String oemCode, String paramsCode, String paramsValue) {
        return mapper.queryOemConfigByOemCodeAndParamsCode(oemCode,paramsCode,paramsValue);
    }

}

