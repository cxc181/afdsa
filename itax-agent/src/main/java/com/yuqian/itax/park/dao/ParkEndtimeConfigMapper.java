package com.yuqian.itax.park.dao;

import com.yuqian.itax.park.entity.ParkEndtimeConfigEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 园区操作截止时间配置dao
 * 
 * @Date: 2021年03月16日 14:49:30 
 * @author 蒋匿
 */
@Mapper
public interface ParkEndtimeConfigMapper extends BaseMapper<ParkEndtimeConfigEntity> {

    ParkEndtimeConfigEntity queryEndtimeConfig(@Param("parkId") Long parkId, @Param("operType") Integer operType, @Param("invoiceWay") Integer invoiceWay);


    /**
     * 根据园区id，操作类型，年，季度查询数据
     * @param parkId
     * @param OperType
     * @param year
     * @param quarter
     * @return
     */
    List<ParkEndtimeConfigEntity> queryByOperTypeAndParkIdAndYearAndQuarter(@Param("parkId") Long parkId, @Param("OperType")Integer OperType, @Param("year")Integer year,  @Param("quarter")Integer quarter);
}

