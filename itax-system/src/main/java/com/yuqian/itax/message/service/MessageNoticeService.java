package com.yuqian.itax.message.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.message.dao.MessageNoticeMapper;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.entity.query.MessageNoticeQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息通知表service
 * 
 * @Date: 2019年12月24日 15:57:07 
 * @author 蒋匿
 */
public interface MessageNoticeService extends IBaseService<MessageNoticeEntity,MessageNoticeMapper> {


    /**
     * 保存通知消息
     * @param entity
     * @return
     */
    int saveMessageNotice(MessageNoticeEntity entity);

    /**
     * 更新通知消息
     * @param entity
     * @return
     */
    int updateNoticeById(MessageNoticeEntity entity);

    /**
     * @Description 根据用户id和机构编码获取未读消息的数量
     * @Author  Kaven
     * @Date   2020/3/4 11:12
     * @Param  userId oemCode
     * @Return int
     * @Exception 
    */
    int getUnReadCount(Long userId, String oemCode);

    /**
     * @Description 根据消息状态查询消息列表
     * @Author  Kaven
     * @Date   2020/3/4 11:23
     * @Param  MessageNoticeQuery
     * @Return List
     * @Exception
    */
    PageInfo<MessageNoticeEntity> listMessage(MessageNoticeQuery query);

    /**
     * @Description 根据用户id和机构编码将用户未读消息全部设置为已读
     * @Author  Kaven
     * @Date   2020/3/4 11:28
     * @Param  userId oemCode
     * @Return 
     * @Exception 
    */
    void readAll(Long userId,String memberAccount, String oemCode);

    /**
     * @Description 查询超过days天的未读消息列表
     * @Author  Kaven
     * @Date   2020/3/10 10:20
     * @Param   days
     * @Return  List<MessageNoticeEntity>
     * @Exception
    */
    List<MessageNoticeEntity> selectUnReadList(Integer days);

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


    void updateStatusByNoticeId(Long noticeId,Integer status);

    /**
     * @Description 新增首页弹窗消息（目前仅长沙园区业务用到）
     * @Author  Kaven
     * @Date   2020/6/15 10:00 上午
     * @Param   userId oemCode orderNo
     * @Return
     * @Exception  BusinessException
    */
    void addNoticeIndex(Long userId, String oemCode,String orderNo) throws BusinessException;

    /**
     * @Description 根据订单号和是否已弹窗字段删除当前通知数据
     * @Author  Kaven
     * @Date   2020/6/15 10:17 上午
     * @Param   orderNo isAlertFlag:0-未弹窗 1-已弹窗
     * @Return
     * @Exception  BusinessException
    */
    void deleteByOrderNo(String orderNo, Integer isAlertFlag) throws BusinessException;

    /**
     * 按类型及用户查询是否有未读消息
     */
    List<MessageNoticeEntity> findNoticeByType(Long userId , String oemCode , Integer BusinessType);

    void updateStatusById(Long id,Integer status);

    /**
     * 获取基础通知详情
     * @param dictCode 字典表通知配置key
     * @param defaultNoticeContent 默认通知模板，没有传参的话如果查询不到模板会抛出自定义异常
     * @param oemCode 机构编码
     * @param userId 通知用户
     * @return
     * @throws BusinessException
     */
    MessageNoticeEntity getBaseNotice(String dictCode, String defaultNoticeContent, String oemCode, Long userId);

    /**
     * 添加通知
     * @param dictCode 字典表通知配置key
     * @param oemCode 机构编码
     * @param orderNo 订单编号
     * @param userId 通知用户
     * @param noticeTitle 通知标题
     * @param noticeType 通知类型  1-短信通知 2-站内通知
     * @param noticePosition 通知位置(多个通知位置之间用逗号分割)  1-消息中心 2-首页弹窗
     * @param openMode 打开方式 1-通知详情 2-h5地址链接 3-小程序功能
     * @param businessType 业务类型 1-开户待支付 2-开户已完成 3-开票 4-用章 5-开票流水审核 6-待身份验证 7-托管费到期提醒 8-工商注册确认登记 9-工商注册用户未提交签名
     * @throws BusinessException
     */
    void addNotice(String dictCode, String oemCode, String orderNo, Long userId, String noticeTitle, Integer noticeType, String noticePosition, Integer openMode, Integer businessType, String addUser);
}

