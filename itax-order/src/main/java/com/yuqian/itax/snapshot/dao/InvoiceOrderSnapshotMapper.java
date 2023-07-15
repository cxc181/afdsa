package com.yuqian.itax.snapshot.dao;

import com.yuqian.itax.snapshot.entity.InvoiceOrderSnapshotEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.snapshot.entity.query.InvoiceOrderSnapshotParkQuery;
import com.yuqian.itax.snapshot.entity.query.InvoiceSnapshotQuery;
import com.yuqian.itax.snapshot.entity.vo.InvoiceOrderSnapshotParkVO;
import com.yuqian.itax.snapshot.entity.vo.InvoiceSnapshotVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 开票订单快照(已完成)dao
 * 
 * @Date: 2020年10月26日 11:25:23 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceOrderSnapshotMapper extends BaseMapper<InvoiceOrderSnapshotEntity> {

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
     *新增或修改开票快照表
     */
    void updateOrInsertInvoiceOrderSnapshot(@Param("startDate") String startDate,@Param("endDate") String endDate, @Param("userId") Long userId,@Param("oemCode") String oemCode);
    /**
     * 删除快照
     */
    void deleteInvoiceOrderSnapshotByDate(@Param("startDate")String startDate,@Param("endDate") String endDate,@Param("userId") Long userId,@Param("oemCode") String oemCode);
}

