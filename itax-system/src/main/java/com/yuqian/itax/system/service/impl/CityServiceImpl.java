package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.CityMapper;
import com.yuqian.itax.system.entity.CityEntity;
import com.yuqian.itax.system.entity.vo.SysCityVO;
import com.yuqian.itax.system.service.CityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.ws.RequestWrapper;
import java.util.List;


@Service("cityService")
public class CityServiceImpl extends BaseServiceImpl<CityEntity,CityMapper> implements CityService {
    @Resource
    private CityMapper cityMapper;

    @Override
    public List<SysCityVO> getCityList(String provinceCode) {
        return this.cityMapper.getCityList(provinceCode);
    }

    @Override
    public SysCityVO getCityByName(String cityName) {
        return this.cityMapper.getCityByName(cityName);
    }

    @Override
    public CityEntity getByCode(String cityCode) {
        return this.cityMapper.getByCode(cityCode);
    }
    @Override
    public CityEntity getByName(String name, String provinceCode) {
        return this.cityMapper.getByName(name, provinceCode);
    }
}

