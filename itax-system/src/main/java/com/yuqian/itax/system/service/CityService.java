package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.CityEntity;
import com.yuqian.itax.system.dao.CityMapper;
import com.yuqian.itax.system.entity.vo.SysCityVO;

import java.util.List;

/**
 * 市service
 * 
 * @Date: 2019年12月08日 20:33:09 
 * @author Kaven
 */
public interface CityService extends IBaseService<CityEntity,CityMapper> {
    /**
     * @Description 根据省级编码获取市级信息
     * @Author  Kaven
     * @Date   2019/12/9 17:17
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
     * @Date   2019/12/10 10:34
     * @Param  cityCode
     * @Return CityEntity
    */
    CityEntity getByCode(String cityCode);
    /**
     * @Description 根据编码查询城市信息
     * @Author  Kaven
     * @Date   2019/12/10 10:34
     * @Param  cityCode
     * @Return CityEntity
     */
    CityEntity getByName(String name, String provinceCode);
}

