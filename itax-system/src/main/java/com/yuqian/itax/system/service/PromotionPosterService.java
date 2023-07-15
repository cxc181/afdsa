package com.yuqian.itax.system.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.PromotionPosterMapper;
import com.yuqian.itax.system.entity.PromotionPosterEntity;
import com.yuqian.itax.system.entity.dto.PromotionPosterPO;
import com.yuqian.itax.system.entity.query.PromotionPosterQuery;
import com.yuqian.itax.system.entity.vo.PromotionPosterVO;

import java.util.List;

/**
 * 推广海报service
 *
 * @author 蒋匿
 * @Date: 2020年08月07日 10:38:14
 */
public interface PromotionPosterService extends IBaseService<PromotionPosterEntity, PromotionPosterMapper> {

    PageInfo<PromotionPosterVO> queryPromotionPosterPage(PromotionPosterQuery query);

    PromotionPosterEntity addPromotionPosterPage(PromotionPosterPO po, String account);

    PromotionPosterEntity updatePromotionPoster(PromotionPosterPO po, String account);

    PromotionPosterVO queryPromotionPosterDetail(Long id);

    Integer getMaxPosterSn();

    /**
     * 根据oemCode查询海报列表
     *
     * @param oemCode
     * @return
     */
    List<PromotionPosterEntity> getPromotionPosterList(String oemCode);

}

