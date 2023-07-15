package com.yuqian.itax.group.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.group.entity.GroupPaymentAnalysisRecordEntity;
import com.yuqian.itax.group.dao.GroupPaymentAnalysisRecordMapper;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 集团打款流水解析记录表service
 * 
 * @Date: 2020年03月04日 09:26:23 
 * @author 蒋匿
 */
public interface GroupPaymentAnalysisRecordService extends IBaseService<GroupPaymentAnalysisRecordEntity,GroupPaymentAnalysisRecordMapper> {

    /**
     * 根据集团订单编号获取解析记录集合
     * @param groupOrderNo
     * @param oemCode
     * @return
     */
    List<GroupPaymentAnalysisRecordEntity> queryByOrderNo(String groupOrderNo, String oemCode);

    /**
     * 根据集团订单编号统计成功，失败，总计解析信息（开票金额，记录条数）
     * @param groupOrderNo
     * @param oemCode
     */
    Map<String, Object> sumByGroupOrderNo(String groupOrderNo, String oemCode);

    /**
     * 批量添加解析记录
     * @param list
     */
    void batchAdd(List<GroupPaymentAnalysisRecordEntity> list);
}

