package com.yuqian.itax.park.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.dao.ParkAgreementMapper;
import com.yuqian.itax.park.entity.ParkAgreementEntity;

import java.util.List;

/**
 * 园区协议表service
 * 
 * @Date: 2020年07月14日 16:50:43 
 * @author 蒋匿
 */
public interface ParkAgreementService extends IBaseService<ParkAgreementEntity,ParkAgreementMapper> {

    /**
     * @Description 根据企业ID查询协议列表
     * @Author  Kaven
     * @Date   2020/9/7 09:54
     * @Param  companyId   oemCode
     * @Return  List<ParkAgreementEntity>
     * @Exception
    */
    List<ParkAgreementEntity> listParkAgreement(Long companyId, String oemCode) throws BusinessException;
}

