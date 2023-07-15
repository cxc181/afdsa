package com.yuqian.itax.agent.dao;

import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.vo.OemConfigVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * oem机构参数配置表dao
 * 
 * @Date: 2020年07月21日 11:19:36 
 * @author 蒋匿
 */
@Mapper
public interface OemConfigMapper extends BaseMapper<OemConfigEntity> {

    List<OemConfigVO> queryOemRate(@Param("oemCode")String oemCode, @Param("parentParamsCode")String parentParamsCode, @Param("taxType")Integer taxType);


    /**
     * 根据oem机构查询是否接入国金
     * @param oemCode
     * @param paramsCode
     * @param paramsValue
     * @return
     */
    OemConfigEntity queryOemConfigByOemCodeAndParamsCode(@Param("oemCode") String oemCode,@Param("paramsCode") String paramsCode,@Param("paramsValue") String paramsValue);
}

