package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.FeedbackEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.query.FeedbackQuery;
import com.yuqian.itax.system.entity.vo.FeedbackVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 意见反馈dao
 * 
 * @Date: 2019年12月08日 20:38:54 
 * @author 蒋匿
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<FeedbackEntity> {

    /**
     * 查询意见反馈列表
     * @param query
     * @return
     */
    List<FeedbackVO> listFeedback(FeedbackQuery query);
}

