package com.yuqian.itax.system.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.FeedbackMapper;
import com.yuqian.itax.system.entity.FeedbackEntity;
import com.yuqian.itax.system.entity.query.FeedbackQuery;
import com.yuqian.itax.system.entity.vo.FeedbackVO;

import java.util.List;

/**
 * 意见反馈service
 * 
 * @Date: 2019年12月08日 20:38:54 
 * @author 蒋匿
 */
public interface FeedbackService extends IBaseService<FeedbackEntity,FeedbackMapper> {

    /**
     * 分页查询意见反馈
     * @param query
     * @return
     */
    PageInfo<FeedbackVO> listPage(FeedbackQuery query);

    /**
     * 查询意见反馈列表
     * @param query
     * @return
     */
    List<FeedbackVO> listFeedback(FeedbackQuery query);
}

