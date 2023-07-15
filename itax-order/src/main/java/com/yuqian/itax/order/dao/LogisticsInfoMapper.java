package com.yuqian.itax.order.dao;

import com.yuqian.itax.order.entity.LogisticsInfoEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 开票快递信息记录dao
 * 
 * @Date: 2020年02月13日 15:33:18 
 * @author 蒋匿
 */
@Mapper
public interface LogisticsInfoMapper extends BaseMapper<LogisticsInfoEntity> {

    /**
     * 根据orderNo获取开票快递信息
     * @param orderNo
     * @return
     */
    List<LogisticsInfoEntity> queryByOrderNo(String orderNo);



    /**
     * 根据orderNo删除快递信息
     * @param orderNo
     */
    void deleteByOrderNo(String orderNo);
	
}

