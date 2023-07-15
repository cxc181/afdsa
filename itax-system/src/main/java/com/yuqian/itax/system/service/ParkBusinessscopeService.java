package com.yuqian.itax.system.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeBatchVO;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeVO;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeWithTaxCodeVO;
import com.yuqian.itax.system.dao.ParkBusinessscopeMapper;
import com.yuqian.itax.system.entity.ParkBusinessscopeEntity;
import com.yuqian.itax.system.entity.query.ParkBusinessScopeQuery;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 园区经营范围表service
 * 
 * @Date: 2022年12月29日 13:52:58 
 * @author 蒋匿
 */
public interface ParkBusinessscopeService extends IBaseService<ParkBusinessscopeEntity,ParkBusinessscopeMapper> {

    /**
     * 根据园区id查询园区经营范围（分页）
     * @param query
     * @return
     */
    PageResultVo<String> findByParkId(ParkBusinessScopeQuery query);

    /**
     * 根据园区id 和经营范围分页查询
     * @param query
     * @return
     */
    PageInfo<ParkBusinessScopeVO> queryByfindByParkIdAndbusinessScopeName(ParkBusinessScopeQuery query);

    /**
     * 经营范围导出
     * @param query
     * @return
     */
    List<ParkBusinessScopeVO> parkBusinessScopeExport(ParkBusinessScopeQuery query);

    /**
     * 根据园区id、经营范围查找数据
     * @param parkId
     * @param scopeName
     * @return
     */
    ParkBusinessscopeEntity getParkBusinessscopeByParkIdAndName(Long parkId,String scopeName);

    /**
     * 校验保存的数据
     * @param list
     * @param userName
     * @return
     */
    Map<String,Object> checkParkBusinessScope(List<ParkBusinessScopeBatchVO> list, Long parkId,String userName);

    /**
     * 根据税收编码查询园区经营范围（商品编码匹配园区经营范围）
     * @param taxCodeSet
     * @param parkId
     */
    List<ParkBusinessScopeWithTaxCodeVO> queryByTaxCode(Set<String> taxCodeSet, Long parkId);

    /**
     * 添加经营范围
     * @param entity
     * @return
     */
    Void addParkBusinessScope(ParkBusinessscopeEntity entity);

    /**
     * 删除园区经营范围
     * @param entity
     */
    void deleteParkBusinessScope(ParkBusinessscopeEntity entity,String userName);
}

