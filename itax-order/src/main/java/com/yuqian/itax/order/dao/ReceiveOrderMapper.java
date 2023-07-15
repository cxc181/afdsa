package com.yuqian.itax.order.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.ReceiveOrderEntity;
import com.yuqian.itax.order.entity.vo.ReceiveOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 接单表dao
 * 
 * @Date: 2019年12月16日 20:20:33 
 * @author 蒋匿
 */
@Mapper
public interface ReceiveOrderMapper extends BaseMapper<ReceiveOrderEntity> {

    /**
     * @Description 查询最小接单数的客服数量
     * @Author  Kaven
     * @Date   2019/12/24 16:08
     * @Param  oemCode
     * @Return ReceiveOrderVO
     * @Exception
    */
    ReceiveOrderVO getMinReceiveNum(String oemCode);

    /**
     * @Description 查询总客服坐席数和总接单数
     * @Author Kaven
     * @Date 2020/1/7 14:51
     * @Param oemCode
     * @Return ReceiveOrderVO
     * @Exception
     */
    ReceiveOrderVO getTotalNums(@Param("oemCode") String oemCode);

    /**
     * @Description 查询接单客服列表
     * @Author  Kaven
     * @Date   2020/1/7 14:51
     * @Return List
     * @Exception
     */
    List<ReceiveOrderEntity> getReceiveList(@Param("oemCode") String oemCode);

    /**
     * 根据userId查询信息
     * @param userId
     * @return
     */
    ReceiveOrderEntity queryByUserId(@Param("userId") Long userId);
}

