package com.yuqian.itax.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.BannerMapper;
import com.yuqian.itax.system.entity.BannerEntity;
import com.yuqian.itax.system.entity.query.BannerQuery;
import com.yuqian.itax.system.entity.vo.BannerListVO;
import com.yuqian.itax.system.entity.vo.BannerVO;
import com.yuqian.itax.system.service.BannerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service("bannerService")
public class BannerServiceImpl extends BaseServiceImpl<BannerEntity,BannerMapper> implements BannerService {
    @Override
    public BannerVO getDetail(Long bannerId, String oemCode) {
        BannerEntity t = new BannerEntity();
        t.setId(bannerId);
        t.setOemCode(oemCode);
        BannerEntity entity = this.selectOne(t);
        BannerVO banner = null;
        if(null != entity){
            banner = new BannerVO();
            banner.setId(entity.getId());
            banner.setShareTitle(entity.getShareTitle());
            banner.setShareImageAddress(entity.getShareImageAddress());
        }
        return banner;
    }

    @Override
    public PageInfo<BannerListVO> listPageBanner(BannerQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listBanner(query));
    }

    @Override
    public boolean orderNumIsExist(String oemCode, Integer orderNum, Long notId) {
        return mapper.orderNumIsExist(oemCode, orderNum, notId);
    }

    @Override
    public void edit(BannerEntity bannerEntity) {
        mapper.updateByPrimaryKey(bannerEntity);
    }

    @Override
    public void batchInsertBannerEntity(List<BannerEntity> list, String oemCode, String account) {
        mapper.addBatch(list,oemCode,new Date(),account);
    }
}

