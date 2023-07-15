package com.yuqian.itax.park.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.query.ParkListOfMenuQuery;
import com.yuqian.itax.park.entity.query.ParkQuery;
import com.yuqian.itax.park.entity.query.SearchParkQuery;
import com.yuqian.itax.park.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 园区管理dao
 * 
 * @Date: 2019年12月07日 20:44:24 
 * @author 蒋匿
 */
@Mapper
public interface ParkMapper extends BaseMapper<ParkEntity> {
    /**
     * @Description 查询当前产品下的所有园区列表
     * @Author  Kaven
     * @Date   2019/12/10 11:29
     * @Param  productId
     * @Return List
     */
    List<ParkVO> queryParkList(Long productId, Integer companyType);
    /**
     * 查询当前机构下所有产品
     * @param query
     * @return
     */
    List<ParkEntity> queryOemParkList(ParkQuery query);

    /**
     * @Description 后台查询所有园区列表
     * @Author  HZ
     * @Date   2019/12/17 11:29
     */
    List<ParkListVO> getParkList(ParkQuery parkQuery);

    /**
     * @Description 根据园区编码查询园区信息
     * @Author  HZ
     * @param id （不包括的id）
     * @Date   2019/12/17 11:29
     */
    List<ParkEntity> getParkByParkCode(@Param("parkCode") String parkCode,@Param("id") Long id);
    /**
     * @Description 后台查询机构所有园区列表
     * @Author  HZ
     * @Date   2019/12/17 11:29
     */
    List<ParkListVO> getOemParkList(ParkQuery parkQuery);

    /**
     * 查询当前产品下的所有园区列表(简版)
     * @param productId
     * @return
     */
    List<ParkVO> queryProductParkList(Long productId);

    /**
     * 查询所有园区列表
     * @param
     * @return
     */
    List<ParkEntity> getAllPark();

    /**
     * 查询所有园区列表和园区税费政策(计算器使用)
     *
     * @param companyType
     * @param oemCode
     * @return
     */
    List<ParkVO> getAllParkAndPolicy(@Param("companyType") Integer companyType, @Param("oemCode") String oemCode);

    /**
     * 搜索园区列表
     * @param query
     * @return
     */
    List<SearchParkListVO> queryParkListByParam(SearchParkQuery query);

    /**
     * 根据税收分类编码匹配园区经营范围
     *
     * @return
     */
    List<UsableParkBusinessScopeVO> getUsableParkBusinessScope(@Param("oemCode") String oemCode, @Param("taxCodeList") List<String> taxCodeList,
                                                               @Param("parkList") List<Long> parkList, @Param("taxCodeNum") Integer taxCodeNum,
                                                               @Param("taxpayerType") int taxpayerType);

    /**
     * 查询园区可选经营范围列表
     *
     * @return
     */
    List<UsableParkBusinessScopeVO> getUsableParkIndustry(@Param("oemCode") String oemCode, @Param("parkList") List<Long> parkList,
                                                          @Param("responseType") int responseType, @Param("taxpayerType") int taxpayerType);

    /**
     * 获取园区流程标记
     * @param parkId
     * @return
     */
    ParkProcessMarkVO queryParkProcessMark(@Param("parkId") Long parkId);

    /**
     * 查询所有园区列表
     *
     * @param companyType
     * @param oemCode
     * @return
     */
    List<TaxCalculatorParkVO> getAllParkList(@Param("companyType") Integer companyType, @Param("oemCode") String oemCode);

    /**
     * 小程序园区菜单——园区列表
     * @param query
     * @return
     */
    List<ParkListOfMenuVO> getParkListOfMenu(ParkListOfMenuQuery query);

    /**
     * 小程序园区菜单——园区详情
     * @param parkId
     * @param oemCode
     * @return
     */
    ParkDetailOfMenuVO getParkDetailOfMenu(@Param("parkId") Long parkId,@Param("oemCode") String oemCode);
}

