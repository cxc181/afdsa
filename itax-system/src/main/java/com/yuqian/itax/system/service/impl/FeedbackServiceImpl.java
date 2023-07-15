package com.yuqian.itax.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.FeedbackMapper;
import com.yuqian.itax.system.entity.FeedbackEntity;
import com.yuqian.itax.system.entity.query.FeedbackQuery;
import com.yuqian.itax.system.entity.vo.FeedbackVO;
import com.yuqian.itax.system.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("feedbackService")
public class FeedbackServiceImpl extends BaseServiceImpl<FeedbackEntity,FeedbackMapper> implements FeedbackService {

    @Override
    public PageInfo<FeedbackVO> listPage(FeedbackQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(listFeedback(query));
    }

    @Override
    public List<FeedbackVO> listFeedback(FeedbackQuery query) {
        return mapper.listFeedback(query);
    }
}

