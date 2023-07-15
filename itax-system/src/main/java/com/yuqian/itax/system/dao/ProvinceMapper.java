package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.ProvinceEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.vo.SysProvinceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 省dao
 * 
 * @Date: 2019年12月08日 20:33:23 
 * @author 蒋匿
 */
@Mapper
public interface ProvinceMapper extends BaseMapper<ProvinceEntity> {
    /**
     * @Description 获取所有省份信息列表
     * @Author  Kaven
     * @Date   2019/12/9 17:23
     * @Return List
    */
    List<SysProvinceVO> getProvinceList();

    /**
     * @Description 根据编码查询省级信息
     * @Author  Kaven
     * @Date   2019/12/10 10:38
     * @Param  provinceCode
     * @Return ProvinceEntity
    */
    ProvinceEntity getByCode(String provinceCode);

    /**
     * @Description 根据编码查询省级信息
     * @Author  Kaven
     * @Date   2019/12/10 10:38
     * @Param  provinceCode
     * @Return ProvinceEntity
     */
    ProvinceEntity getByName(String provinceName);
}

