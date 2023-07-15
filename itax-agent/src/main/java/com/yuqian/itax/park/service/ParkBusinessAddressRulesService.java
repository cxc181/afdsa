package com.yuqian.itax.park.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.entity.ParkBusinessAddressRulesEntity;
import com.yuqian.itax.park.dao.ParkBusinessAddressRulesMapper;
import com.yuqian.itax.park.entity.ParkBusinessAddressVO;

/**
 * 园区经营地址生成规则service
 * 
 * @Date: 2020年05月18日 11:33:58 
 * @author 蒋匿
 */
public interface ParkBusinessAddressRulesService extends IBaseService<ParkBusinessAddressRulesEntity,ParkBusinessAddressRulesMapper> {

    /**
     * 根据园区生成经营地址
     * @param parkId
     * @return
     */
    String builderBusinessAddressByPark(Long parkId);

    /**
     * 根据园区id查询经营地址
     * @param parkId
     * @return
     */
    ParkBusinessAddressVO queryByParkId(Long parkId);

    /**
     * 校验经营地址参数
     * @param parkBusinessAddressVO
     * @return
     */
    ParkBusinessAddressRulesEntity checkAddress(ParkBusinessAddressVO parkBusinessAddressVO);

    /**
     * 获取最新使用地址
     * @param parkBusinessAddressVO
     * @return
     */
    String getUseAddress(ParkBusinessAddressVO parkBusinessAddressVO);

    /**
     * 获取区域注册数最小值 位数不足前面补0
     * @param min
     * @param max
     * @return
     */
    String getAreaRegistNumMin(String min,String max);
}

