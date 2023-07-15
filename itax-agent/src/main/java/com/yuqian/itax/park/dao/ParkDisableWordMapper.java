package com.yuqian.itax.park.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.ParkDisableWordEntity;
import com.yuqian.itax.park.entity.query.ParkDisableWordQuery;
import com.yuqian.itax.park.entity.vo.ParkDisableWordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 园区禁用字dao
 * 
 * @Date: 2021年10月19日 14:31:08 
 * @author 蒋匿
 */
@Mapper
public interface ParkDisableWordMapper extends BaseMapper<ParkDisableWordEntity> {

    /**
     * 按条件查询禁用字号
     * @param query
     * @return
     */
    List<ParkDisableWordVO> queryParkDisableWord(ParkDisableWordQuery query);

    /**
     * 根据园区id查询禁用字号
     * @param parkId
     * @return
     */
    List<ParkDisableWordEntity> getDisableWordByParkId(Long parkId);

    /**
     * 根据园区id和禁用字号名称查询数据
     * @param parkId
     * @param disableWord
     * @return
     */
    ParkDisableWordEntity getDisableWordByParkIdAndDisableWord(@Param("parkId") Long parkId, @Param("disableWord") String disableWord);

    /**
     * 批量插入
     * @param list
     */
    void batchAddDisableWord(@Param("list") List<ParkDisableWordEntity> list);

    /**
     * 根据字号查询禁用字号
     * @param parkId
     * @param shopName
     * @return
     */
    List<String> queryByShopName(@Param("parkId") Long parkId, @Param("shopName") String shopName, @Param("shopNameOne") String shopNameOne, @Param("shopNameTwo") String shopNameTwo);
}

