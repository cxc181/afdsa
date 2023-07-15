package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.UserOrderStatisticsDayEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayAgentQuery;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayEmployeeQuery;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayMemberQuery;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayAgentVO;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayEmployeeVO;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayMemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户订单日统计dao
 * 
 * @Date: 2020年06月03日 09:13:20 
 * @author 蒋匿
 */
@Mapper
public interface UserOrderStatisticsDayMapper extends BaseMapper<UserOrderStatisticsDayEntity> {

    /**
     * @Author yejian
     * @Description 查询城市服务商下所有日统计
     * @param memberId 会员id
     * @param day 哪天
     * @param month 哪月
     * @Date 2020/06/04 10:18
     **/
	List<UserOrderStatisticsDayEntity> getOrderStatByDate(@Param("memberId")Long memberId, @Param("day")String day, @Param("month")String month,
                                                          @Param("startDate")String startDate, @Param("endDate")String endDate);

    /**
     * 查询当日用户订单统计数据
     */
    UserOrderStatisticsDayEntity queryrOrderStatisticsDayByToday(@Param("oemCode")String oemCode,@Param("userId")Long userId);

    /**
     *会员业绩统计
     */
    List<UserOrderStatisticsDayMemberVO> queryUserOrderStatisticsDayMember(UserOrderStatisticsDayMemberQuery query);
    /**
     * 员工业绩统计
     */
    List<UserOrderStatisticsDayEmployeeVO> queryUserOrderStatisticsDayEmployee(UserOrderStatisticsDayEmployeeQuery query);
    /**
     * 合伙人业绩统计
     */
    List<UserOrderStatisticsDayAgentVO> queryUserOrderStatisticsDayAgent(UserOrderStatisticsDayAgentQuery query);



}

