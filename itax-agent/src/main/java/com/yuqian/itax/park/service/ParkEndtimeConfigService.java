package com.yuqian.itax.park.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.entity.ParkEndtimeConfigEntity;
import com.yuqian.itax.park.dao.ParkEndtimeConfigMapper;
import com.yuqian.itax.park.entity.po.ParkEndtimeConfigPO;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * 园区操作截止时间配置service
 * 
 * @Date: 2021年03月16日 14:49:30 
 * @author 蒋匿
 */
public interface ParkEndtimeConfigService extends IBaseService<ParkEndtimeConfigEntity,ParkEndtimeConfigMapper> {

    ParkEndtimeConfigEntity findEndtimeConfig(Long parkId, Integer operType, Integer invoiceWay);

    /**
     * 保存开票截至日期和成本确认日期
     * @param po
     */
    void insertParkEndtimeConfigInfo(ParkEndtimeConfigPO po,String userName);

    /**
     * 根据园区id，操作类型，年，季度查询数据
     * @param parkId
     * @param OperType
     * @param year
     * @param quarter
     * @return
     */
    ParkEndtimeConfigEntity queryByOperTypeAndParkIdAndYearAndQuarter(Long parkId, Integer OperType,Integer year,Integer quarter);
}

