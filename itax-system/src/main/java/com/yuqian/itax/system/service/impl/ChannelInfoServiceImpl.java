package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.ChannelInfoMapper;
import com.yuqian.itax.system.entity.ChannelInfoEntity;
import com.yuqian.itax.system.service.ChannelInfoService;
import org.springframework.stereotype.Service;


@Service("channelInfoService")
public class ChannelInfoServiceImpl extends BaseServiceImpl<ChannelInfoEntity,ChannelInfoMapper> implements ChannelInfoService {

    @Override
    public ChannelInfoEntity findByChannelCode(String channelCode) {
        return mapper.queryByChannelCode(channelCode);
    }
}

