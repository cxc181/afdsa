package com.yuqian.itax.message.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.message.entity.NoticeManageEntity;
import com.yuqian.itax.message.entity.query.NoticeManageQuery;
import com.yuqian.itax.message.entity.vo.NoticeManageListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知管理dao
 *
 * @author 蒋匿
 * @Date: 2020年03月04日 09:32:20
 */
@Mapper
public interface NoticeManageMapper extends BaseMapper<NoticeManageEntity> {

    List<NoticeManageEntity> getNoticeManage(NoticeManageQuery query);

    /**
     * 查询已发布的悬浮公告列表
     *
     * @param oemCode
     */
    List<NoticeManageListVO> getPublishedList(@Param("oemCode") String oemCode);

    /**
     * 查询悬浮公告详情
     *
     * @param oemCode
     * @param id      通知主键id
     */
    NoticeManageEntity getDetail(@Param("oemCode") String oemCode, @Param("id") Long id);
}

