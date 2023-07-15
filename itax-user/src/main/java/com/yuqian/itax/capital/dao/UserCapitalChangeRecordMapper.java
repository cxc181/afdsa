package com.yuqian.itax.capital.dao;

import com.yuqian.itax.capital.entity.UserCapitalChangeRecordEntity;
import com.yuqian.itax.capital.entity.query.UserCapitalChangeRecordQuery;
import com.yuqian.itax.capital.entity.vo.UserCapitalChangeRecordVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户资金变动记录dao
 * 
 * @Date: 2019年12月07日 20:54:31 
 * @author 蒋匿
 */
@Mapper
public interface UserCapitalChangeRecordMapper extends BaseMapper<UserCapitalChangeRecordEntity> {

    /**
     * 分页查询资金变动明细表
     */
    List<UserCapitalChangeRecordVO> queryUserCapitalChangeRecordEntityPageInfo(UserCapitalChangeRecordQuery userCapitalChangeRecordQuery);

}

