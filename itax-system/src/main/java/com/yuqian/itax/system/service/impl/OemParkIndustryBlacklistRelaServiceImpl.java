package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.system.dao.OemParkIndustryBlacklistRelaMapper;
import com.yuqian.itax.system.entity.IndustryEntity;
import com.yuqian.itax.system.entity.OemParkIndustryBlacklistRelaEntity;
import com.yuqian.itax.system.service.IndustryService;
import com.yuqian.itax.system.service.OemParkIndustryBlacklistRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service("oemParkIndustryBlacklistRelaService")
public class OemParkIndustryBlacklistRelaServiceImpl extends BaseServiceImpl<OemParkIndustryBlacklistRelaEntity,OemParkIndustryBlacklistRelaMapper> implements OemParkIndustryBlacklistRelaService {

    @Autowired
    private IndustryService industryService;

    @Autowired
    private ParkService parkService;

    @Override
    @Transactional
    public void addBatch(String oemCode, Long parkId, List<Long> ids, String addUser) {
        OemParkIndustryBlacklistRelaEntity entity = new OemParkIndustryBlacklistRelaEntity();
        entity.setOemCode(oemCode);
        entity.setParkId(parkId);
        mapper.delete(entity);
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        List<IndustryEntity> list = industryService.selectByIndustryIds(parkId, ids);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        ParkEntity parkEntity = parkService.findById(parkId);
        String parkCode = Optional.ofNullable(parkEntity).map(ParkEntity::getParkCode).orElse(null);
        ids = list.stream().map(IndustryEntity::getId).collect(Collectors.toList());
        entity.setParkCode(parkCode);
        entity.setAddTime(new Date());
        entity.setAddUser(addUser);
        mapper.addBatch(entity, ids);
    }

    @Override
    public void deleteByParkIds(String oemCode, List<Long> parkIdList) {
        mapper.deleteByParkIds(oemCode, parkIdList);
    }
}

