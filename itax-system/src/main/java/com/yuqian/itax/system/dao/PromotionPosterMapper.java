package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.PromotionPosterEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.query.PromotionPosterQuery;
import com.yuqian.itax.system.entity.vo.PromotionPosterVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推广海报dao
 *
 * @Date: 2020年08月07日 10:38:14 
 * @author 蒋匿
 */
@Mapper
public interface PromotionPosterMapper extends BaseMapper<PromotionPosterEntity> {

    List<PromotionPosterVO> queryPromotionPosterList(PromotionPosterQuery query);

    Integer getMaxPosterSn();

    List<PromotionPosterEntity> selectPromotionPosterByNotId(Integer sn,Long id);

    /**
     * 根据oemCode查询海报列表
     *
     * @param oemCode
     * @return
     */
    List<PromotionPosterEntity> getPromotionPosterList(@Param("oemCode") String oemCode);
}

