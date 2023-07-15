package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.BannerEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.query.BannerQuery;
import com.yuqian.itax.system.entity.vo.BannerListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * banner图管理dao
 * 
 * @Date: 2019年12月08日 20:37:18 
 * @author 蒋匿
 */
@Mapper
public interface BannerMapper extends BaseMapper<BannerEntity> {

    /**
     * 查询banner列表
     * @param query
     * @return
     */
    List<BannerListVO> listBanner(BannerQuery query);

    /**
     * 查询排序是否存在
     * @param oemCode
     * @param orderNum
     * @param notId
     * @return
     */
    boolean orderNumIsExist(@Param("oemCode")String oemCode, @Param("orderNum")Integer orderNum, @Param("notId")Long notId);

    /**
     *
     */
    void addBatch(@Param("list") List<BannerEntity> list, @Param("oemCode")String oemCode, @Param("addTime") Date addTime,@Param("account") String account);
}

