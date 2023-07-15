package com.yuqian.itax.snapshot.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.snapshot.entity.InvoiceOrderSnapshotEntity;
import com.yuqian.itax.snapshot.dao.InvoiceOrderSnapshotMapper;
import com.yuqian.itax.snapshot.entity.query.InvoiceOrderSnapshotParkQuery;
import com.yuqian.itax.snapshot.entity.query.InvoiceSnapshotQuery;
import com.yuqian.itax.snapshot.entity.vo.InvoiceOrderSnapshotParkVO;
import com.yuqian.itax.snapshot.entity.vo.InvoiceSnapshotVO;

import java.util.List;

/**
 * 开票订单快照(已完成)service
 * 
 * @Date: 2020年10月26日 11:25:23 
 * @author 蒋匿
 */
public interface InvoiceOrderSnapshotService extends IBaseService<InvoiceOrderSnapshotEntity,InvoiceOrderSnapshotMapper> {


    /**
     * 园区交易量统计
     */
    List<InvoiceOrderSnapshotParkVO> queryInvoiceOrderSnapshotPark(InvoiceOrderSnapshotParkQuery query);

    /**
     * 开票数据统计
     * @param query
     * @return
     */
    List<InvoiceSnapshotVO> invoiceCount(InvoiceSnapshotQuery query);

    /**
     * 新增或修改开票订单快照表记录
     * @param startDate
     * @param endDate
     * @param userId
     * @param oemCode
     */
    void updateOrInsertInvoiceOrderSnapshot(String startDate,String endDate,Long userId,String oemCode);

    /**
     * 删除指定日期快照
     * @param startDate
     * @param endDate
     * @param userId
     * @param oemCode
     */
    void deleteInvoiceOrderSnapshotByDate(String startDate,String endDate,Long userId,String oemCode);
}

