package com.yuqian.itax.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.UserOrderStatisticsDayMapper;
import com.yuqian.itax.user.entity.UserOrderStatisticsDayEntity;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayAgentQuery;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayEmployeeQuery;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayMemberQuery;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayAgentVO;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayEmployeeVO;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayMemberVO;
import com.yuqian.itax.user.service.UserOrderStatisticsDayService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("userOrderStatisticsDayService")
public class UserOrderStatisticsDayServiceImpl extends BaseServiceImpl<UserOrderStatisticsDayEntity,UserOrderStatisticsDayMapper> implements UserOrderStatisticsDayService {

    @Override
    public List<UserOrderStatisticsDayEntity> getOrderStatByDate(Long memberId, String day, String month, String startDate, String endDate) {
        return mapper.getOrderStatByDate(memberId, day, month, startDate, endDate);
    }

    @Override
    public UserOrderStatisticsDayEntity queryrOrderStatisticsDayByToday(String oemCode, Long userId) {
        return mapper.queryrOrderStatisticsDayByToday(oemCode,userId);
    }

    @Override
    public PageInfo<UserOrderStatisticsDayMemberVO> queryUserOrderStatisticsDayMemberPage(UserOrderStatisticsDayMemberQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.queryUserOrderStatisticsDayMember(query));    }

    @Override
    public List<UserOrderStatisticsDayMemberVO> queryUserOrderStatisticsDayMember(UserOrderStatisticsDayMemberQuery query) {
        return mapper.queryUserOrderStatisticsDayMember(query);
    }

    @Override
    public PageInfo<UserOrderStatisticsDayEmployeeVO> queryUserOrderStatisticsDayEmployeePage(UserOrderStatisticsDayEmployeeQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.queryUserOrderStatisticsDayEmployee(query));
    }

    @Override
    public List<UserOrderStatisticsDayEmployeeVO> queryUserOrderStatisticsDayEmployee(UserOrderStatisticsDayEmployeeQuery query) {
        return mapper.queryUserOrderStatisticsDayEmployee(query);
    }

    @Override
    public PageInfo<UserOrderStatisticsDayAgentVO> queryUserOrderStatisticsDayAgentPage(UserOrderStatisticsDayAgentQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.queryUserOrderStatisticsDayAgent(query));
    }

    @Override
    public List<UserOrderStatisticsDayAgentVO> queryUserOrderStatisticsDayAgent(UserOrderStatisticsDayAgentQuery query) {
        return mapper.queryUserOrderStatisticsDayAgent(query);
    }
}

