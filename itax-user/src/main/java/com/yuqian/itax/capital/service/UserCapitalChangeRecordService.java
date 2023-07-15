package com.yuqian.itax.capital.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.capital.dao.UserCapitalChangeRecordMapper;
import com.yuqian.itax.capital.entity.UserCapitalChangeRecordEntity;
import com.yuqian.itax.capital.entity.query.UserCapitalChangeRecordQuery;
import com.yuqian.itax.capital.entity.vo.UserCapitalChangeRecordVO;
import com.yuqian.itax.common.base.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * 用户资金变动记录service
 * 
 * @Date: 2019年12月07日 20:54:31 
 * @author 蒋匿
 */
public interface UserCapitalChangeRecordService extends IBaseService<UserCapitalChangeRecordEntity,UserCapitalChangeRecordMapper> {
    /**
     * @Description 保存微信支付资金变动记录
     * @Author  Kaven
     * @Date   2019/12/13 9:51
     * @Param  params
    */
    void saveChangeRecord(Map<String,Object> params);

    /**
     * 根据UserId分页查询资金变动明细
     */
    PageInfo<UserCapitalChangeRecordVO> queryUserCapitalChangeRecordEntityPageInfo(UserCapitalChangeRecordQuery userCapitalChangeRecordQuery);

    /**
     * 根据UserId分页查询资金变动明细
     */
    List<UserCapitalChangeRecordVO> queryUserCapitalChangeRecordEntityList(UserCapitalChangeRecordQuery userCapitalChangeRecordQuery);
}

