package com.yuqian.itax.message.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.entity.query.MessageNoticeQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 消息通知表dao
 * 
 * @Date: 2019年12月24日 15:57:07 
 * @author 蒋匿
 */
@Mapper
public interface MessageNoticeMapper extends BaseMapper<MessageNoticeEntity> {
    /**
     * 更新通知消息
     * @param entity
     * @return
     */
    int updateNoticeById(MessageNoticeEntity entity);

    /**
     * @Description 查询未读消息数量
     * @Author  Kaven
     * @Date   2020/3/4 11:13
     * @Param  userId oemCode
     * @Return int
     * @Exception
    */
    int getUnReadCount(@Param("userId") Long userId, @Param("oemCode") String oemCode);

    /**
     * @Description 根据用户id和机构编码将用户未读消息全部设置为已读
     * @Author  Kaven
     * @Date   2020/3/4 11:29
     * @Param  userId  oemCode
     * @Return 
     * @Exception 
    */
    void updateNoticeAllRead(@Param("userId") Long userId, @Param("oemCode") String oemCode,
                             @Param("updateUser") String updateUser,@Param("updateTime") Date updateTime);

    /**
     * @Description 查询所有消息列表（未读和已读）
     * @Author  Kaven
     * @Date   2020/3/4 11:50
     * @Param  MessageNoticeQuery
     * @Return List
     * @Exception
    */
    List<MessageNoticeEntity> selectList(MessageNoticeQuery query);

    /**
     * @Description 查询超过days天的未读消息列表
     * @Author  Kaven
     * @Date   2020/3/10 10:22
     * @Param   days
     * @Return  List<MessageNoticeEntity>
     * @Exception
    */
    List<MessageNoticeEntity> selectUnReadList(@Param(value="days") Integer days);

    /**
     * 根据用户id和机构编码获取全部首页未弹窗的消息
     * @param userId
     * @param oemCode
     * @return
     */
    List<MessageNoticeEntity> findAllHomeNotAlertMessageByUserId(@Param("userId") Long userId, @Param("oemCode") String oemCode);

    /**
     * 根据用户id和机构编码业务类型获取全部首页未弹窗的消息
     * @param userId
     * @param oemCode
     * @param businessType
     * @return
     */
    List<MessageNoticeEntity> findAllHomeNotAlertMessageByUserIdAndBusinessType(@Param("userId") Long userId, @Param("oemCode") String oemCode,@Param("businessType") Integer businessType);


    void addBatch(List<MessageNoticeEntity> list);

    void updateStatusByNoticeId(@Param("noticeId")Long noticeId, @Param("status")Integer status);

    /**
     * 按类型及用户查询是否有未读消息
     * @param userId
     * @param oemCode
     * @param businessType
     * @return
     */
    List<MessageNoticeEntity> queryNoticeByType(@Param("userId")Long userId, @Param("oemCode")String oemCode, @Param("businessType")Integer businessType);

    /**
     * 更新未读托管费续费
     * @param id
     * @param status
     */
    void updateStatusById(Long id, Integer status);
}

