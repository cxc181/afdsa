package com.yuqian.itax.park.dao;

import com.yuqian.itax.park.entity.ParkCorporateAccountConfigEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 园区对公户提现配置dao
 * 
 * @Date: 2020年09月07日 09:14:49 
 * @author 蒋匿
 */
@Mapper
public interface ParkCorporateAccountConfigMapper extends BaseMapper<ParkCorporateAccountConfigEntity> {

    /**
     * @Description 根据用户ID查询支持对公户园区列表
     * @Author  Kaven
     * @Date   2020/9/7 10:49
     * @Param   userId
     * @Return  List<ParkCorporateAccountConfigEntity>
     * @Exception
    */
    List<ParkCorporateAccountConfigEntity> selectByMemberId(@Param("userId") Long userId);

    /**
     * 根据园区id查询配置
     * @param parkId
     * @return
     */
    List<ParkCorporateAccountConfigEntity> queryConfigByParkId(@Param("parkId") Long parkId);
}

