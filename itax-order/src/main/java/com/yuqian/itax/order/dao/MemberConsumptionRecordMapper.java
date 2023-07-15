package com.yuqian.itax.order.dao;

import com.yuqian.itax.order.entity.MemberConsumptionRecordEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.query.MemberConsumptionRecordQuery;
import com.yuqian.itax.order.entity.vo.MemberConsumptionRecordVO;
import com.yuqian.itax.order.service.MemberConsumptionRecordService;
import com.yuqian.itax.order.entity.query.ConsumptionRecordQuery;
import com.yuqian.itax.order.entity.vo.ConsumptionRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员消费记录表dao
 * 
 * @Date: 2020年09月27日 11:22:19 
 * @author 蒋匿
 */
@Mapper
public interface MemberConsumptionRecordMapper extends BaseMapper<MemberConsumptionRecordEntity> {

    List<MemberConsumptionRecordVO> queryMemberConsumptionRecordList(MemberConsumptionRecordQuery query);
    /**
     * @Description 获取可开票的订单列表数据
     * @Author  Kaven
     * @Date   2020/9/27 11:39
     * @Param   ConsumptionRecordQuery
     * @Return  List<ConsumptionRecordVO>
     * @Exception
    */
    List<ConsumptionRecordVO> listConsumptionRecord(ConsumptionRecordQuery query);

    /**
     * @Description 根据订单号批量更新会员消费记录（是否已开票）
     * @Author  Kaven
     * @Date   2020/9/29 10:08
     * @Param  orderNos
     * @Return
     * @Exception
    */
    void updateRecordByOrderNo(@Param("orderNos") String orderNos);
}

