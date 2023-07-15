package com.yuqian.itax.snapshot.dao;

import com.yuqian.itax.snapshot.entity.OrderSnapshotEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.snapshot.entity.query.OemCapitalQuery;
import com.yuqian.itax.snapshot.entity.query.OrderSnapshotQuery;
import com.yuqian.itax.snapshot.entity.vo.OemCapitalVO;
import com.yuqian.itax.snapshot.entity.vo.RechargeWithdrawSnapshotVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单快照(已完成)dao
 * 
 * @Date: 2020年10月26日 11:25:17 
 * @author 蒋匿
 */
@Mapper
public interface OrderSnapshotMapper extends BaseMapper<OrderSnapshotEntity> {

    /**
     * 充值提现统计
     * @param query
     * @return
     */
    List<RechargeWithdrawSnapshotVO> rechargeWithdraw(OrderSnapshotQuery query);

    /**
     * oem机构资金流统计
     * @param query
     * @return
     */
    List<OemCapitalVO> oemCapital(OemCapitalQuery query);

    /**
     *
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    Integer updateOrInsertOrderSnapshot(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("userId") Long userId,@Param("oemCode") String oemCode);

    /**
     * 删除
     * @param startDate
     * @param endDate
     * @param userId
     */
    void deleteOrderSnapshotByDate(@Param("startDate") String startDate,@Param("endDate") String endDate, @Param("userId") Long userId,@Param("oemCode") String oemCode);
}

