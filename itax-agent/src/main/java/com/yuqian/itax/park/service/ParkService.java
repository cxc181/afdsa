package com.yuqian.itax.park.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.dao.ParkMapper;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.ParkPO;
import com.yuqian.itax.park.entity.query.GetUsableParkIndustryQuery;
import com.yuqian.itax.park.entity.query.ParkListOfMenuQuery;
import com.yuqian.itax.park.entity.query.ParkQuery;
import com.yuqian.itax.park.entity.query.SearchParkQuery;
import com.yuqian.itax.park.entity.vo.*;

import java.util.List;
import java.util.Map;


/**
 * 园区管理service
 *
 * @Date: 2019年12月07日 20:44:24 
 * @author 蒋匿
 */
public interface ParkService extends IBaseService<ParkEntity,ParkMapper> {
    /**
     * 后台查询园区列表
     * @param parkQuery
     * @return
     */
    List<ParkListVO> queryParkList(ParkQuery parkQuery);

    /**
     * 后台查询机构的园区列表
     * @param parkQuery
     * @return
     */
    public List<ParkListVO> getOemParkList(ParkQuery parkQuery) ;

    PageInfo<ParkListVO> queryOemPageInfo(ParkQuery parkQuery);

    /**
     * 新增园区
     */
    ParkEntity addParkEntity(ParkPO parkPO, String userAccount);
    /**
     * 园区状态修改
     */
    ParkEntity updateParkStatus(ParkPO parkPO,String userAccount);

    /**
     * 查询当前机构下所有产品
     * @param query
     * @return
     */
    List<ParkEntity> queryOemParkList(ParkQuery query);

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
    List<ParkVO> getAllParkAndPolicy(Integer companyType, String oemCode);

    ParkDetailVO getParkDteatailById(Long id);

    List<ParkEntity> getParkByParkCode(String parkCode,Long id);

    /**
     * @Description 园区列表查询（纯API）
     * @Author  Kaven
     * @Date   2020/7/31 10:10
     * @Param   companyType:企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任，默认1
     * @Return List<Park4OutVO>
     * @Exception BusinessException
    */
    List<Park4OutVO> listParks(String oemCode,Integer companyType) throws BusinessException;

    /**
     * 搜索园区列表
     * @param query
     * @return
     */
    List<SearchParkListVO> searchPark(SearchParkQuery query);

    /**
     * 根据园区id查询园区特殊事项说明
     * @param parkId
     * @param oemCode
     * @return
     */
    Map<String, Object> getSpecialConsiderations(Long parkId, String oemCode, Integer companyType);

    /**
     * 接入方根据经营范围查询可选园区行业
     * @param oemCode
     * @param query
     * @return
     */
    Map<String, Object> getUsableParkBusinessScope(String oemCode, String accessPartyCode, GetUsableParkIndustryQuery query);

    /**
     * 获取园区流程标记
     * @param parkId
     * @return
     */
    ParkProcessMarkVO getParkProcessMark(Long parkId);

    List<TaxCalculatorParkVO> getAllParkList(Integer companyType, String oemCode);

    /**
     * 获取园区政策标签
     * @return
     */
    List<String> getParkPolicyLabel(String oemCode);

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
    ParkDetailOfMenuVO getParkDetailOfMenu(Long parkId,String oemCode);
}
