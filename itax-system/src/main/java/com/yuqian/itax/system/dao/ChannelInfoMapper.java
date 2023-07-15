package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.ChannelInfoEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 渠道信息表dao
 * 
 * @Date: 2021年04月27日 16:38:21 
 * @author 蒋匿
 */
@Mapper
public interface ChannelInfoMapper extends BaseMapper<ChannelInfoEntity> {

    /**
     * @param channelCode
     * @return
     */
    ChannelInfoEntity queryByChannelCode(@Param("channelCode") String channelCode);
}

