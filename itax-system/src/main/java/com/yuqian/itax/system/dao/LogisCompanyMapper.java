package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.LogisCompanyEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物流公司信息dao
 * 
 * @Date: 2020年02月13日 13:40:54 
 * @author 蒋匿
 */
@Mapper
public interface LogisCompanyMapper extends BaseMapper<LogisCompanyEntity> {
    /**
     * 查询快递公司列表
     * @return
     */
    List<LogisCompanyEntity> logisCompanyList();

    /**
     * 根据快递公司名称查询快递公司
     * @param companyName
     * @return
     */
    LogisCompanyEntity queryByCompanyName(@Param("companyName") String companyName);


    /**
     * 根据快递公司名称模糊查询快递公司
     * @param companyName
     * @return
     */
    List<LogisCompanyEntity> queryByLikeCompanyName(@Param("companyName") String companyName);
}

