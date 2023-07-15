package com.yuqian.itax.agent.dao;

import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.entity.query.OemParamsQuery;
import com.yuqian.itax.agent.entity.vo.OemParamsVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 机构参数配置dao
 * 
 * @Date: 2019年12月06日 17:27:05 
 * @author Kaven
 */
@Mapper
public interface OemParamsMapper extends BaseMapper<OemParamsEntity> {

    /**
     * @Description 根据机构编码和参数类型查询配置记录
     * @Author  Kaven
     * @Date   2019/12/9 9:38
     * @Param  params
     * @Return OemParamsEntity
    */
    OemParamsEntity getParams(Map<String, Object> params);

    List<OemParamsVO> querySysOemParamsList(OemParamsQuery query);
}

