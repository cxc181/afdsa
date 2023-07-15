package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.IndustryEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.query.IndustryQuery;
import com.yuqian.itax.system.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 行业dao
 * 
 * @Date: 2019年12月08日 20:37:33 
 * @author 蒋匿
 */
@Mapper
public interface IndustryMapper extends BaseMapper<IndustryEntity> {

    /**
     * 查询行业信息
     * @param query
     * @return
     */
    List<IndustryInfoVO> listIndustryInfo(IndustryQuery query);

    /**
     * 查询行业列表
     * @param parkCode  园区code
     * @param companyType 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
     * @return
     */
    List<IndustryApiVO> getIndustryList(@Param("parkCode")String parkCode, @Param("companyType")Integer companyType,
                                        @Param("oemCode")String oemCode, @Param("industryName")String industryName);

    /**
     * 查询黑名单例外行业列表
     * @param oemCode
     * @param parkId
     * @return
     */
    List<IndustryAdminVO> queryBlackListByParkId(@Param("oemCode")String oemCode, @Param("parkId")Long parkId);

    /**
     * 根据行业id查询行业信息
     *
     * @param parkId
     * @param ids
     * @return
     */
    List<IndustryEntity> selectByIndustryIds(@Param("parkId")Long parkId, @Param("ids") List<Long> ids);

    /**
     * 查询园区行业介绍信息
     * @param industryId
     * @return
     */
    ParkIndustryPresentationVO queryIndustryPresentation(@Param("industryId") Long industryId);

    /**
     * 根据园区id获取行业
     * @param parkIds
     * @return
     */
    List<IndustryAndParkInfoVo> queryIndustryByParkIds(@Param("parkIds") List<String> parkIds);
}

