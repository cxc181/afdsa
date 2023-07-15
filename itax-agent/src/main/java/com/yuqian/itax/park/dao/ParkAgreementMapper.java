package com.yuqian.itax.park.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.ParkAgreementEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 园区协议表dao
 * 
 * @Date: 2020年07月14日 16:50:43 
 * @author 蒋匿
 */
@Mapper
public interface ParkAgreementMapper extends BaseMapper<ParkAgreementEntity> {

    /**
     * @Description 根据企业ID查询协议列表
     * @Author  Kaven
     * @Date   2020/9/7 09:56
     * @Param
     * @Return
     * @Exception
    */
    List<ParkAgreementEntity> listParkAgreement(@Param("companyId") Long companyId, @Param("oemCode") String oemCode);
}

