package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.UserOrderStatisticsDayEntity;
import com.yuqian.itax.user.dao.UserOrderStatisticsDayMapper;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayAgentQuery;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayEmployeeQuery;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayMemberQuery;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayAgentVO;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayEmployeeVO;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayMemberVO;

import java.util.List;

/**
 * 用户订单日统计service
 * 
 * @Date: 2020年06月03日 09:13:20 
 * @author 蒋匿
 */
public interface UserOrderStatisticsDayService extends IBaseService<UserOrderStatisticsDayEntity,UserOrderStatisticsDayMapper> {

    /**
     * @Author yejian
     * @Description 查询城市服务商下所有日统计
     * @param memberId 会员id
     * @param day 哪天
     * @param month 哪月
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @Date 2020/06/04 10:18
     **/
    List<UserOrderStatisticsDayEntity> getOrderStatByDate(Long memberId, String day, String month, String startDate, String endDate);

    UserOrderStatisticsDayEntity queryrOrderStatisticsDayByToday(String oemCode,Long userId);

    /**
     * 会员业绩统计(分页)
     */

    PageInfo<UserOrderStatisticsDayMemberVO> queryUserOrderStatisticsDayMemberPage(UserOrderStatisticsDayMemberQuery query);

    /**
     * 会员业绩统计
     */

    List<UserOrderStatisticsDayMemberVO> queryUserOrderStatisticsDayMember(UserOrderStatisticsDayMemberQuery query);

    /**
     * 员工业绩统计(分页)
     */

    PageInfo<UserOrderStatisticsDayEmployeeVO> queryUserOrderStatisticsDayEmployeePage(UserOrderStatisticsDayEmployeeQuery query);


    /**
     * 员工业绩统计
     */

    List<UserOrderStatisticsDayEmployeeVO> queryUserOrderStatisticsDayEmployee(UserOrderStatisticsDayEmployeeQuery query);


    /**
     * 合伙人业绩统计(分页)
     */

    PageInfo<UserOrderStatisticsDayAgentVO> queryUserOrderStatisticsDayAgentPage(UserOrderStatisticsDayAgentQuery query);


    /**
     * 合伙人业绩统计
     */

    List<UserOrderStatisticsDayAgentVO> queryUserOrderStatisticsDayAgent(UserOrderStatisticsDayAgentQuery query);
}

