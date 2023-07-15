package com.yuqian.itax.message.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.message.dao.MessageNoticeMapper;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.entity.query.MessageNoticeQuery;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("messageNoticeService")
public class MessageNoticeServiceImpl extends BaseServiceImpl<MessageNoticeEntity,MessageNoticeMapper> implements MessageNoticeService {
    @Resource
    private MessageNoticeMapper messageNoticeMapper;
    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 保存通知消息
     * @param entity
     * @return
     */
    @Override
    public int saveMessageNotice(MessageNoticeEntity entity){
        if(entity.getUserId() == null || entity.getUserType() == null
                || entity.getNoticeType() == null || StringUtils.isEmpty(entity.getNoticeContent())){
            throw new BusinessException("通知人或通知内容为空");
        }
        return mapper.insertSelective(entity);
    }

     /**
     * 更新通知消息
     * @param entity
     * @return
     */
     @Override
     public int updateNoticeById(MessageNoticeEntity entity){
         return mapper.updateNoticeById(entity);
     }

    @Override
    public int getUnReadCount(Long userId, String oemCode) {
        return this.messageNoticeMapper.getUnReadCount(userId,oemCode);
    }

    @Override
    public PageInfo<MessageNoticeEntity> listMessage(MessageNoticeQuery query) {
         PageHelper.startPage(query.getPageNumber(), query.getPageSize());
         List<MessageNoticeEntity> list = this.messageNoticeMapper.selectList(query);
         return new PageInfo<MessageNoticeEntity>(list);
    }

    @Override
    public void readAll(Long userId,String memberAccount,String oemCode) {
         this.messageNoticeMapper.updateNoticeAllRead(userId,oemCode,memberAccount,new Date());
    }

    @Override
    public List<MessageNoticeEntity> selectUnReadList(Integer days) {
        return this.messageNoticeMapper.selectUnReadList(days);
    }

    @Override
    public List<MessageNoticeEntity> findAllHomeNotAlertMessageByUserId(@Param("userId") Long userId, @Param("oemCode") String oemCode){
        return this.messageNoticeMapper.findAllHomeNotAlertMessageByUserId(userId,oemCode);
    }

    @Override
    public List<MessageNoticeEntity> findAllHomeNotAlertMessageByUserIdAndBusinessType(Long userId, String oemCode, Integer businessType) {
        return this.messageNoticeMapper.findAllHomeNotAlertMessageByUserIdAndBusinessType(userId,oemCode,businessType);
    }

    @Override
    public void updateStatusByNoticeId(Long noticeId, Integer status) {
        mapper.updateStatusByNoticeId(noticeId,status);
    }

    @Override
    public void addNoticeIndex(Long userId, String oemCode,String orderNo) throws BusinessException {
        log.info("新增首页弹窗消息：{},{}",userId,oemCode);
        // 新增新的通知记录
        MessageNoticeEntity entity = new MessageNoticeEntity();
        entity.setIsAlert(0);//是否已弹窗 0-未弹窗 1-已弹窗
        entity.setNoticeTitle("订单未完成通知");
        // 查询通知内容
        DictionaryEntity dict = this.dictionaryService.getByCode("index_notice_content");
        entity.setNoticeContent(null == dict ? "您有一个企业注册订单未完成经营者身份验证，是否继续？" : dict.getDictValue());
        entity.setOrderNo(orderNo);
        entity.setUserId(userId);
        entity.setUserType(1);// 用户类型 1-会员 2-后端用户
        entity.setNoticeType(2);// 通知类型  1-短信通知 2-站内通知
        entity.setNoticePosition("2");//通知位置(多个通知位置之间用逗号分割)  1-消息中心 2-首页弹窗
        entity.setOpenMode(3);// 打开方式 1-通知详情 2-h5地址链接 3-小程序功能
        entity.setAddUser("admin");
        entity.setAddTime(new Date());
        entity.setBusinessType(6);// 待身份验证
        entity.setStatus(0);// 状态 0-未读 1-已读 2-已下线 3-已取消
        entity.setOemCode(oemCode);
        this.insertSelective(entity);

        log.info("新增首页弹窗消息成功。");
    }

    @Override
    public void deleteByOrderNo(String orderNo, Integer isAlertFlag) throws BusinessException {
        log.info("根据订单号和通知状态删除通知消息service：{}",orderNo);
        MessageNoticeEntity t = new MessageNoticeEntity();
        t.setOrderNo(orderNo);
        t.setIsAlert(isAlertFlag);
        t.setBusinessType(6);// 待身份验证
        this.messageNoticeMapper.delete(t);
    }

    @Override
    public List<MessageNoticeEntity> findNoticeByType(Long userId, String oemCode, Integer businessType) {
        return mapper.queryNoticeByType(userId,oemCode,businessType);
    }

    @Override
    public void updateStatusById(Long id, Integer status) {
        mapper.updateStatusById(id,status);
    }

    @Override
    public MessageNoticeEntity getBaseNotice(String dictCode, String defaultNoticeContent, String oemCode, Long userId) {
        MessageNoticeEntity entity = new MessageNoticeEntity();
        // 查询通知模板
        String content;
        if (StringUtils.isBlank(defaultNoticeContent)) {
            content = dictionaryService.getValueByCode(dictCode, true, "未配置通知模板", null);
        } else {
            content = dictionaryService.getValueByCode(dictCode, defaultNoticeContent);
        }
        entity.setNoticeContent(content);
        entity.setOemCode(oemCode);
        entity.setUserId(userId);
        entity.setAddTime(new Date());
        // 用户类型 1-会员 2-后端用户
        entity.setUserType(1);
        //是否已弹窗 0-未弹窗 1-已弹窗
        entity.setIsAlert(0);
        // 状态 0-未读 1-已读 2-已下线 3-已取消
        entity.setStatus(0);
        return entity;
    }

    @Override
    public void addNotice(String dictCode, String oemCode, String orderNo, Long userId, String noticeTitle, Integer noticeType, String noticePosition, Integer openMode, Integer businessType, String addUser) {

        MessageNoticeEntity entity = getBaseNotice(dictCode, null, oemCode, userId);
        entity.setNoticeTitle(noticeTitle);
        entity.setOrderNo(orderNo);
        entity.setNoticeType(noticeType);
        entity.setNoticePosition(noticePosition);
        entity.setOpenMode(openMode);
        entity.setBusinessType(businessType);
        entity.setAddUser(addUser);

        this.insertSelective(entity);
    }

}

