package com.yuqian.itax.system.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.product.entity.vo.ProductOemVO;
import com.yuqian.itax.system.dao.BannerMapper;
import com.yuqian.itax.system.entity.BannerEntity;
import com.yuqian.itax.system.entity.query.BannerQuery;
import com.yuqian.itax.system.entity.vo.BannerListVO;
import com.yuqian.itax.system.entity.vo.BannerVO;

import java.util.List;

/**
 * banner图管理service
 * 
 * @Date: 2019年12月08日 20:37:18 
 * @author 蒋匿
 */
public interface BannerService extends IBaseService<BannerEntity,BannerMapper> {
    /**
     * @Description 根据bannerId和机构编码获取分享标题和分享图标
     * @Author  Kaven
     * @Date   2020/3/4 10:07
     * @Param bannerId oemCode
     * @Return BannerVO
     * @Exception
    */
    BannerVO getDetail(Long bannerId, String oemCode);

    /**
     * 分页查询广告位
     * @param query
     * @return
     */
    PageInfo<BannerListVO> listPageBanner(BannerQuery query);

    /**
     * 查询排序是否存在
     * @param oemCode
     * @param orderNum
     * @param notId
     * @return
     */
    boolean orderNumIsExist(String oemCode, Integer orderNum, Long notId);

    /**
     * 编辑
     * @param bannerEntity
     */
    void edit(BannerEntity bannerEntity);

    /**
     * 批量插入
     */
    void batchInsertBannerEntity(List<BannerEntity> list,String oemCode,String account);
}

