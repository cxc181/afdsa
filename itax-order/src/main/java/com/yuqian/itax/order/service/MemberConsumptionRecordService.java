package com.yuqian.itax.order.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.entity.MemberConsumptionRecordEntity;
import com.yuqian.itax.order.dao.MemberConsumptionRecordMapper;
import com.yuqian.itax.order.entity.query.MemberConsumptionRecordQuery;

import java.util.List;
import com.yuqian.itax.order.entity.query.ConsumptionRecordQuery;
import com.yuqian.itax.order.entity.vo.ConsumptionRecordVO;
import com.yuqian.itax.order.entity.vo.MemberConsumptionRecordVO;

import java.util.List;

/**
 * 会员消费记录表service
 * 
 * @Date: 2020年09月27日 11:22:19 
 * @author 蒋匿
 */
public interface MemberConsumptionRecordService extends IBaseService<MemberConsumptionRecordEntity,MemberConsumptionRecordMapper> {

    /**
     * 查看消费订单(分页)
     * @param query
     * @return
     */
    public PageInfo<MemberConsumptionRecordVO> queryMemberConsumptionRecordPage(MemberConsumptionRecordQuery query);

    /**
     * 查看消费订单
     * @param query
     * @return
     */
    public List<MemberConsumptionRecordVO> queryMemberConsumptionRecordList(MemberConsumptionRecordQuery query);


    /**
     * @Description 获取可开票的订单列表数据
     * @Author  Kaven
     * @Date   2020/9/27 11:32
     * @Param currUserId oemCode query
     * @Return List<ConsumptionRecordVO>
     * @Exception BusinessException
    */
    List<ConsumptionRecordVO> listConsumptionRecord(Long currUserId, String oemCode, ConsumptionRecordQuery query) throws BusinessException;

    /**
     * 新增消费记录
     * @param oemCode 机构编码
     * @param orderNo 订单编号
     * @param orderType 订单类型  3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现
     * @param memberId 用户id
     * @param consumptionAmount 消费金额（分）
     * @param addUser 添加人
     * @param remark 备注
     */
    void insertSelective(String oemCode, String orderNo, Integer orderType, Long memberId, Long consumptionAmount, String addUser, String remark);

    /**
     * @Description 根据订单号批量更新是否已开票状态
     * @Author  Kaven
     * @Date   2020/9/29 10:03
     * @Param   orderNos
     * @Return
     * @Exception  BusinessException
    */
    void updateRecordByOrderNo(String orderNos) throws BusinessException;
}

