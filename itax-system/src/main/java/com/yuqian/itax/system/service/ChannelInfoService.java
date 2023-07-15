package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.ChannelInfoEntity;
import com.yuqian.itax.system.dao.ChannelInfoMapper;

/**
 * 渠道信息表service
 * 
 * @Date: 2021年04月27日 16:38:21 
 * @author 蒋匿
 */
public interface ChannelInfoService extends IBaseService<ChannelInfoEntity,ChannelInfoMapper> {

    /**
     * 根据渠道编码查询渠道信息
     * @param channelCode
     * @return
     */
    ChannelInfoEntity findByChannelCode(String channelCode);
}

