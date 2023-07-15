package com.yuqian.itax.system.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.DistrictEntity;
import com.yuqian.itax.system.entity.vo.DistrictVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 区dao
 * 
 * @Date: 2019年12月14日 14:13:57 
 * @author 蒋匿
 */
@Mapper
public interface DistrictMapper extends BaseMapper<DistrictEntity> {
    /**
     * @Description 根据市编码查询区信息
     * @Author  yejian
     * @Date   2019/12/23 17:19
     * @Param  cityCode
     * @Return List
     */
    List<DistrictVO> getDistrictList(String cityCode);

    DistrictEntity getByCode(@Param("code") String code);


    DistrictEntity getByName(@Param("name") String name, @Param("cityCode") String cityCode);
}

