package com.yuqian.itax.park.service.impl;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.park.dao.ParkAgreementMapper;
import com.yuqian.itax.park.entity.ParkAgreementEntity;
import com.yuqian.itax.park.service.ParkAgreementService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("parkAgreementService")
public class ParkAgreementServiceImpl extends BaseServiceImpl<ParkAgreementEntity,ParkAgreementMapper> implements ParkAgreementService {

    @Override
    public List<ParkAgreementEntity> listParkAgreement(Long companyId, String oemCode) throws BusinessException {
        List<ParkAgreementEntity> list = this.mapper.listParkAgreement(companyId,oemCode);
        return list;
    }
}

