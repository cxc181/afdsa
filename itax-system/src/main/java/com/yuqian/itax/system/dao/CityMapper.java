package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.CityEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.vo.SysCityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 市dao
 * 
 * @Date: 2019年12月08日 20:33:09 
 * @author 蒋匿
 */
@Mapper
public interface CityMapper extends BaseMapper<CityEntity> {
    /**
     * @Description 根据省级编码查询市级信息
     * @Author  Kaven
     * @Date   2019/12/9 17:19
     * @Param  provinceCode
     * @Return List
    */
    List<SysCityVO> getCityList(String provinceCode);

    /**
     * @Description 根据城市名称查询省市信息
     * @Author  Kaven
     * @Date   2019/12/10 10:31
     * @Param  cityName
     * @Return SysCityVO
    */
    SysCityVO getCityByName(String cityName);

    /**
     * @Description 根据编码查询城市信息
     * @Author  Kaven
     * @Date   2019/12/10 10:35
     * @Param  cityCode
     * @Return CityEntity
     */
    CityEntity getByCode(String cityCode);
    /**
     * @Description 根据编码查询城市信息
     * @Author  Kaven
     * @Date   2019/12/10 10:35
     * @Param  cityCode
     * @Return CityEntity
     */
    CityEntity getByName(@Param("name") String name, @Param("provinceCode") String provinceCode);
}

