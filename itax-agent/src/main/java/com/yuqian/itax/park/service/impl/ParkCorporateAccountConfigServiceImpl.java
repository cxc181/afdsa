package com.yuqian.itax.park.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.park.dao.ParkCorporateAccountConfigMapper;
import com.yuqian.itax.park.entity.ParkCorporateAccountConfigEntity;
import com.yuqian.itax.park.service.ParkCorporateAccountConfigService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("parkCorporateAccountConfigService")
public class ParkCorporateAccountConfigServiceImpl extends BaseServiceImpl<ParkCorporateAccountConfigEntity,ParkCorporateAccountConfigMapper> implements ParkCorporateAccountConfigService {

    @Override
    public List<ParkCorporateAccountConfigEntity> selectByMemberId(Long userId) {
        return this.mapper.selectByMemberId(userId);
    }

    @Override
    public List<ParkCorporateAccountConfigEntity> getConfigByParkId(Long parkId) {
        return this.mapper.queryConfigByParkId(parkId);
    }

}

