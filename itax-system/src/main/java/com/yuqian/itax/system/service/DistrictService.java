package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.DistrictEntity;
import com.yuqian.itax.system.dao.DistrictMapper;
import com.yuqian.itax.system.entity.vo.DistrictVO;

import java.util.List;

/**
 * 区service
 * 
 * @Date: 2019年12月14日 14:13:57 
 * @author 蒋匿
 */
public interface DistrictService extends IBaseService<DistrictEntity,DistrictMapper> {
    /**
     * @Description 根据市编码查询区信息
     * @Author  yejian
     * @Date   2019/12/23 17:19
     * @Param  cityCode
     * @Return List
     */
    List<DistrictVO> getDistrictList(String cityCode);

    DistrictEntity getByCode(String code);

    DistrictEntity getByName(String name, String cityCode);
}

