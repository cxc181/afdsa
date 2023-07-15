package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.ProvinceMapper;
import com.yuqian.itax.system.entity.ProvinceEntity;
import com.yuqian.itax.system.entity.vo.SysProvinceVO;
import com.yuqian.itax.system.service.ProvinceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("provinceService")
public class ProvinceServiceImpl extends BaseServiceImpl<ProvinceEntity,ProvinceMapper> implements ProvinceService {
    @Resource
    private ProvinceMapper provinceMapper;

    @Override
    public List<SysProvinceVO> getProvinceList() {
        return this.provinceMapper.getProvinceList();
    }

    @Override
    public ProvinceEntity getByCode(String provinceCode) {
        return this.provinceMapper.getByCode(provinceCode);
    }

    @Override
    public ProvinceEntity getByName(String provinceName) {
        return this.provinceMapper.getByName(provinceName);
    }
}

