package com.yuqian.itax.group.dao;

import com.yuqian.itax.group.entity.GroupPaymentAnalysisRecordEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 集团打款流水解析记录表dao
 * 
 * @Date: 2020年03月04日 09:26:23 
 * @author 蒋匿
 */
@Mapper
public interface GroupPaymentAnalysisRecordMapper extends BaseMapper<GroupPaymentAnalysisRecordEntity> {

    /**
     * 根据集团订单编号获取解析记录集合
     * @param groupOrderNo
     * @param oemCode
     * @return
     */
    List<GroupPaymentAnalysisRecordEntity> queryByOrderNo(@Param("groupOrderNo")String groupOrderNo, @Param("oemCode")String oemCode);

    /**
     * 根据集团订单编号统计成功，失败，总计解析信息（开票金额，记录条数）
     * @param groupOrderNo
     * @param oemCode
     */
    Map<String, Object> sumByGroupOrderNo(@Param("groupOrderNo")String groupOrderNo, @Param("oemCode")String oemCode);

    /**
     * 批量添加解析记录
     * @param list
     */
    void batchAdd(@Param("list")List<GroupPaymentAnalysisRecordEntity> list);

    /**
     * 根据状态统计记录条数
     * @param analysisResult
     * @return
     */
    int countByStatus(@Param("groupOrderNo")String groupOrderNo, @Param("oemCode")String oemCode, @Param("analysisResult")Integer analysisResult);
}

