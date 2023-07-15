package com.yuqian.itax.point.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.point.entity.BuriedPointEntity;
import com.yuqian.itax.point.dao.BuriedPointMapper;

/**
 * 埋点表service
 * 
 * @Date: 2021年04月08日 10:46:03 
 * @author 蒋匿
 */
public interface BuriedPointService extends IBaseService<BuriedPointEntity,BuriedPointMapper> {

    /**
     * 添加
     * @param buriedPointEntity
     */
    void add(BuriedPointEntity buriedPointEntity, CurrUser currUser);
	
}

