package com.yuqian.itax.system.dao;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeVO;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeWithTaxCodeVO;
import com.yuqian.itax.system.entity.ParkBusinessscopeEntity;
import com.yuqian.itax.system.entity.query.ParkBusinessScopeQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 园区经营范围表dao
 * 
 * @Date: 2022年12月29日 13:52:58 
 * @author 蒋匿
 */
@Mapper
public interface ParkBusinessscopeMapper extends BaseMapper<ParkBusinessscopeEntity> {

    /**
     * 根据园区id查询园区经营范围
     * @param query
     * @return
     */
    List<String> queryByParkId(ParkBusinessScopeQuery query);

    /**
     * 根据园区id 和经营范围分页查询
     * @param query
     * @return
     */
    List<ParkBusinessScopeVO> queryByFindByParkIdAndBusinessScopeName(ParkBusinessScopeQuery query);

    /**
     * 根据园区id、经营范围查找数据
     * @param parkId
     * @param scopeName
     * @return
     */
    ParkBusinessscopeEntity getParkBusinessscopeByParkIdAndName(@Param("parkId") Long parkId,@Param("scopeName") String scopeName);

    /**
     * 根据经营范围名称查找数据
     * @param list
     * @return
     */
    List<ParkBusinessscopeEntity> getParkBusinessscopeByScopeName(@Param("list") List<String> list,@Param("parkId") Long parkId);

    /**
     * 根据税收编码查询园区经营范围（商品编码匹配园区经营范围）
     * @param taxCodeSet
     * @param parkId
     */
    List<ParkBusinessScopeWithTaxCodeVO> queryByTaxCode(@Param("taxCodeSet") Set<String> taxCodeSet, @Param("parkId") Long parkId);
}

