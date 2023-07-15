package com.yuqian.itax.snapshot.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.snapshot.entity.OrderSnapshotEntity;
import com.yuqian.itax.snapshot.dao.OrderSnapshotMapper;
import com.yuqian.itax.snapshot.entity.query.OemCapitalQuery;
import com.yuqian.itax.snapshot.entity.query.OrderSnapshotQuery;
import com.yuqian.itax.snapshot.entity.vo.OemCapitalVO;
import com.yuqian.itax.snapshot.entity.vo.RechargeWithdrawSnapshotVO;

import java.util.List;

/**
 * 订单快照(已完成)service
 * 
 * @Date: 2020年10月26日 11:25:17 
 * @author 蒋匿
 */
public interface OrderSnapshotService extends IBaseService<OrderSnapshotEntity,OrderSnapshotMapper> {

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
     * 新增或修复订单快照
     * @param startDate
     * @param endDate
     * @param userId
     * @param oemCode
     */
    void updateOrInsertOrderSnapshot(String startDate,String endDate,Long userId,String oemCode);

    /**
     * 删除订单快照
     * @param startDate
     * @param endDate
     * @param userId
     * @param oemCode
     */
    void deleteOrderSnapshotByDate (String startDate,String endDate,Long userId,String oemCode);
}

