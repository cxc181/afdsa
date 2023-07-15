package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.ProvinceEntity;
import com.yuqian.itax.system.dao.ProvinceMapper;
import com.yuqian.itax.system.entity.vo.SysProvinceVO;

import java.util.List;

/**
 * 省service
 * 
 * @Date: 2019年12月08日 20:33:23 
 * @author 蒋匿
 */
public interface ProvinceService extends IBaseService<ProvinceEntity,ProvinceMapper> {
    /**
     * @Description 获取省级信息列表
     * @Author  Kaven
     * @Date   2019/12/9 17:17
     * @Return List
    */
    List<SysProvinceVO> getProvinceList();

    /**
     * @Description 根据编码查询省级信息
     * @Author  Kaven
     * @Date   2019/12/10 10:37
     * @Param  provinceCode
     * @Return ProvinceEntity
    */
    ProvinceEntity getByCode(String provinceCode);
    /**
     * @Description 根据省名称查询省级信息
     * @Author  Kaven
     * @Date   2019/12/10 10:37
     * @Param  provinceCode
     * @Return ProvinceEntity
     */
    ProvinceEntity getByName(String provinceName);
}

