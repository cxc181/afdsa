package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.order.dao.CompanyCancelOrderMapper;
import com.yuqian.itax.order.entity.CompanyCancelOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.ComCancelOrderVO;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.ComCancelExtendDetailVO;
import com.yuqian.itax.user.entity.vo.ExtendMemberVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业注销订单表service
 * 
 * @Date: 2020年02月13日 15:33:29 
 * @author 蒋匿
 */
public interface CompanyCancelOrderService extends IBaseService<CompanyCancelOrderEntity,CompanyCancelOrderMapper> {

    /**
     * 企业注销取消
     * @param entity
     * @param companyCancelOrderEntity
     */
    void cancelOrder(OrderEntity entity, CompanyCancelOrderEntity companyCancelOrderEntity);

    /**
     * 企业注销确认
     * @param entity
     * @param companyCancelEntity
     * @param memberCompanyEntity
     */
    void cancelConfirm(OrderEntity entity, CompanyCancelOrderEntity companyCancelEntity, MemberCompanyEntity memberCompanyEntity,String account);

    /**
     * @Description 查询企业注销订单-拓展宝
     * @Author  Kaven
     * @Date   2020/3/25 15:51
     * @Param query
     * @Return PageResultVo<ComCancelOrderVO>
     * @Exception
    */
    PageResultVo<ComCancelOrderVO> queryComCancelOrder(TZBOrderQuery query);

    /**
     * @Description 查询企业注销订单统计信息（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:57 下午
     * @Param   MemberExtendQuery
     * @Return  ComCancelExtendDetailVO
     * @Exception  BusinessException
    */
    ComCancelExtendDetailVO queryComCancelStat(MemberExtendQuery query) throws BusinessException;
    /**
     * @Description 查询企业注销订单统计信息（推广明细）
     * @Param   MemberExtendQuery
     * @Return  ComCancelExtendDetailVO
     * @Exception  BusinessException
     */
    ComCancelExtendDetailVO queryComCancelStatByChannelServiceId(MemberExtendQuery query) throws BusinessException;

    /**
     * @Description 根据状态查询企业注销订单列表（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:58 下午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception  BusinessException
    */
    List<ExtendMemberVO> queryCancelOrderListByStatus(MemberExtendQuery query) throws BusinessException;
    /**
     * @Description 根据状态查询企业注销订单列表（推广明细）
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception  BusinessException
     */
    List<ExtendMemberVO> queryCancelOrderListByStatusByChannelServiceId(MemberExtendQuery query) throws BusinessException;

    /**
     * @Description 根据状态查询总注销服务费（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:59 下午
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception  BusinessException
    */
    Long queryTotalCancelFee(MemberExtendQuery query) throws BusinessException;
    /**
     * @Description 根据状态查询总注销服务费（推广明细）
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception  BusinessException
     */
    Long queryTotalCancelFeeByChannelServiceId(MemberExtendQuery query) throws BusinessException;

    /**
     * 根据企业id查询企业注销订单
     * @param companyId
     * @return
     */
    List<ComCancelOrderVO> queryByCompanyId(Long companyId);
}

