package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.DistrictMapper;
import com.yuqian.itax.system.entity.DistrictEntity;
import com.yuqian.itax.system.entity.vo.DistrictVO;
import com.yuqian.itax.system.service.DistrictService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("districtService")
public class DistrictServiceImpl extends BaseServiceImpl<DistrictEntity,DistrictMapper> implements DistrictService {

    @Resource
    private DistrictMapper districtMapper;

    @Override
    public List<DistrictVO> getDistrictList(String cityCode) {
        return districtMapper.getDistrictList(cityCode);
    }

    @Override
    public DistrictEntity getByCode(String code) {
        return mapper.getByCode(code);
    }
    @Override
    public DistrictEntity getByName(String name, String cityCode) {
        return mapper.getByName(name, cityCode);
    }
}

