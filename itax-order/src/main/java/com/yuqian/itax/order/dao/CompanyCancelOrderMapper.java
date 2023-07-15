package com.yuqian.itax.order.dao;

import com.yuqian.itax.order.entity.CompanyCancelOrderEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.ComCancelOrderVO;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.ComCancelExtendDetailVO;
import com.yuqian.itax.user.entity.vo.ExtendMemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业注销订单表dao
 * 
 * @Date: 2020年02月13日 15:33:29 
 * @author 蒋匿
 */
@Mapper
public interface CompanyCancelOrderMapper extends BaseMapper<CompanyCancelOrderEntity> {
    /**
     * @Description 企业注销订单查询-拓展宝
     * @Author  Kaven
     * @Date   2020/3/25 16:36
     * @Param   TZBOrderQuery
     * @Return  List<ComCancelOrderVO>
     * @Exception
     */
    List<ComCancelOrderVO> queryComCancelOrderList(TZBOrderQuery query);

    /**
     * @Description 查询企业注销订单统计信息（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 3:04 下午
     * @Param   MemberExtendQuery
     * @Return  ComCancelExtendDetailVO
     * @Exception
    */
    ComCancelExtendDetailVO queryComCancelStat(MemberExtendQuery query);
    /**
     * @Description 查询企业注销订单统计信息（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 3:04 下午
     * @Param   MemberExtendQuery
     * @Return  ComCancelExtendDetailVO
     * @Exception
     */
    ComCancelExtendDetailVO queryComCancelStatByChannelServiceId(MemberExtendQuery query);

    /**
     * @Description 根据订单状态查询企业注销订单列表（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 3:48 下午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception
    */
    List<ExtendMemberVO> queryCancelOrderListByStatus(MemberExtendQuery query);
    /**
     * @Description 根据订单状态查询企业注销订单列表（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 3:48 下午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception
     */
    List<ExtendMemberVO> queryCancelOrderListByStatusByChannelServiceId(MemberExtendQuery query);

    /**
     * @Description 根据订单状态查询总注销服务费
     * @Author  Kaven
     * @Date   2020/6/8 3:49 下午
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception
    */
    Long queryTotalCancelFee(MemberExtendQuery query);
    /**
     * @Description 根据订单状态查询总注销服务费
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception
     */
    Long queryTotalCancelFeeByChannelServiceId(MemberExtendQuery query);

    /**
     * 根据orderNo获取企业注销信息
     * @param orderNo
     * @return
     */
    CompanyCancelOrderEntity queryByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据企业id查询企业注销订单
     * @param companyId
     * @return
     */
    List<ComCancelOrderVO> queryByCompanyId(@Param("companyId") Long companyId);
}

