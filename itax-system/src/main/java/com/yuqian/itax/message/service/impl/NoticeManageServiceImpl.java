package com.yuqian.itax.message.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.message.dao.MessageNoticeMapper;
import com.yuqian.itax.message.dao.NoticeManageMapper;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.entity.NoticeManageEntity;
import com.yuqian.itax.message.entity.SmsEntity;
import com.yuqian.itax.message.entity.po.NoticeManagePO;
import com.yuqian.itax.message.entity.query.NoticeManageQuery;
import com.yuqian.itax.message.entity.vo.NoticeManageListVO;
import com.yuqian.itax.message.service.NoticeManageService;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.service.UserExtendService;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.util.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service("noticeManageService")
@Slf4j
public class NoticeManageServiceImpl extends BaseServiceImpl<NoticeManageEntity,NoticeManageMapper> implements NoticeManageService {

    @Autowired
    UserService userService;
    @Autowired
    UserExtendService userExtendService;
    @Resource
    MessageNoticeMapper messageNoticeMapper;
    @Autowired
    SmsService smsService;
    @Autowired
    private OemService oemService;
    @Autowired
    private OssService ossService;
    @Autowired
    private DictionaryService dictionaryService;


    @Override
    public PageInfo<NoticeManageEntity> getNoticeManage(NoticeManageQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.getNoticeManage(query));
    }

    @Override
    public NoticeManageEntity addNoticeManage(NoticeManagePO po, String account, List<Map<String,Object>> mapList, String oemCode) {
        if(po.getSendTime()!=null &&po.getSendTime().getTime() < System.currentTimeMillis()){
            throw new BusinessException("发送时间不能小于当前时间");
        }
        if(po.getOutTime()!=null &&po.getSendTime()!=null && (po.getOutTime().getTime() < po.getSendTime().getTime())){
            throw new BusinessException("下线时间小于发布时间");
        }
        if(po.getOutTime()!=null &&po.getOutTime().getTime() < System.currentTimeMillis()){
            throw new BusinessException("下线时间不能小于当前时间");
        }
        if(po.getNoticeType()==1){
            if(null == po.getNoticeContent() || "".equals(po.getNoticeContent()) ){
                throw new BusinessException("请输入通知内容");
            }
        }

        if(po.getNoticeType()==2){
            if(null == po.getNoticePosition()|| "".equals(po.getNoticePosition()) ){
                throw new BusinessException("请选择通知位置");
            }
            if(null == po.getOpenMode() || Objects.equals("",po.getOpenMode())){
                throw new BusinessException("请选择打开方式");
            }
            if(po.getOpenMode()==2){
                if(null == po.getJumpUrl() || "".equals(po.getJumpUrl())){
                    throw new BusinessException("请输入H5链接配置");
                }
                if(null == po.getNoticeSubtitle() || "".equals(po.getNoticeSubtitle())){
                    throw new BusinessException("请输入副标题");
                }
            }
            if(po.getOpenMode()==1){
                if(null == po.getNoticeContent() || "".equals(po.getNoticeContent())){
                    throw new BusinessException("请输入通知内容");
                }
                if(null == po.getNoticeSubtitle() || "".equals(po.getNoticeSubtitle())){
                    throw new BusinessException("请输入副标题");
                }
            }
        }
        if(po.getNoticeType()==3){
            if(null == po.getNoticeTitle() || "".equals(po.getNoticeTitle()) ){
                throw new BusinessException("请输入通知标题");
            }
            if(null == po.getOpenMode() || Objects.equals("",po.getOpenMode())){
                throw new BusinessException("请选择打开方式");
            }
            if(null == po.getNoticeSubtitle() || Objects.equals("",po.getNoticeSubtitle())){
                throw new BusinessException("请输入通知副标题");
            }
            if(po.getOpenMode()==1){
                if(null == po.getNoticeContent() || "".equals(po.getNoticeContent())){
                    throw new BusinessException("请输入通知内容");
                }
            }
            if(po.getOpenMode()==2){
                if(null == po.getJumpUrl() || "".equals(po.getJumpUrl())){
                    throw new BusinessException("请输入H5链接配置");
                }
            }
        }

        NoticeManageEntity noticeManageEntity=new NoticeManageEntity();
        noticeManageEntity.setOemCode(oemCode);
        noticeManageEntity.setNoticeType(po.getNoticeType());
        noticeManageEntity.setNoticePosition(po.getNoticePosition());
        noticeManageEntity.setOpenMode(po.getOpenMode());
        noticeManageEntity.setNoticeTitle(po.getNoticeTitle());
        noticeManageEntity.setNoticeContent(po.getNoticeContent());
        noticeManageEntity.setNoticeSubtitle(po.getNoticeSubtitle());
        noticeManageEntity.setJumpUrl(po.getJumpUrl());
        if(po.getNoticeType()==3){
            noticeManageEntity.setNoticeObj(1);
            noticeManageEntity.setOutTime(po.getOutTime());
        }else{
            noticeManageEntity.setNoticeObj(po.getNoticeObj());
        }
        if (po.getNoticeObj() == 2) {
            noticeManageEntity.setUserPhones(po.getUserPhones());
        }
        noticeManageEntity.setReleaseWay(po.getReleaseWay());
        if(Objects.equals(po.getReleaseWay(),1)){
            noticeManageEntity.setSendStatus(1);
            noticeManageEntity.setSendTime(new Date());
        }else{
            noticeManageEntity.setSendStatus(0);
            noticeManageEntity.setSendTime(po.getSendTime());
        }
        noticeManageEntity.setUserListUrl(po.getFileName());
        noticeManageEntity.setAddTime(new Date());
        noticeManageEntity.setAddUser(account);

        mapper.insert(noticeManageEntity);

        //发送通知
        if(Objects.equals(noticeManageEntity.getReleaseWay(),1)) {
            sendNotice(noticeManageEntity, account, mapList);
        }

        return noticeManageEntity;
    }

    @Override
    public boolean sendNotice(NoticeManageEntity noticeManageEntity,String account ,List<Map<String,Object>> mapList){
        //String [] users=noticeManageEntity.getUserPhones().trim().split(",");
        OemEntity oemEntity = null;
        String sign = "";
        List<MessageNoticeEntity> messageNoticeEntities=new ArrayList<>();
        for(int i=0;i<mapList.size();i++){
            if(Objects.equals(noticeManageEntity.getNoticeType(),1)){
                if(oemEntity == null){
                    oemEntity = oemService.getOem(noticeManageEntity.getOemCode());
                    sign = "【"+oemEntity.getOemName()+"】";
                }
                String userPhone=(String)mapList.get(i).get("phone");
                //发短信
                SmsEntity smsEntity=new SmsEntity();
                smsEntity.setUserType(1);
                smsEntity.setOemCode(noticeManageEntity.getOemCode());
                smsEntity.setUserPhone(userPhone);
                smsEntity.setSmsContent(sign+noticeManageEntity.getNoticeContent());
                smsService.sendMessge(smsEntity);
            }else if(Objects.equals(noticeManageEntity.getNoticeType(),2)){
                //站内消息
                String userPhone=(String)mapList.get(i).get("phone");
                Long userId=(Long)mapList.get(i).get("userId");
                MessageNoticeEntity messageNoticeEntity=new MessageNoticeEntity();
                messageNoticeEntity.setNoticeId(noticeManageEntity.getId());
                messageNoticeEntity.setOemCode(noticeManageEntity.getOemCode());
                messageNoticeEntity.setNoticeType(noticeManageEntity.getNoticeType());
                messageNoticeEntity.setNoticePosition(noticeManageEntity.getNoticePosition());
                messageNoticeEntity.setOpenMode(noticeManageEntity.getOpenMode());
                //messageNoticeEntity.setBusinessType();//
                messageNoticeEntity.setNoticeTitle(noticeManageEntity.getNoticeTitle());
                if(StringUtils.isNotBlank(noticeManageEntity.getNoticeContent())){
                    messageNoticeEntity.setNoticeContent(noticeManageEntity.getNoticeContent());
                }
                if(StringUtils.isNotBlank(noticeManageEntity.getNoticeSubtitle())){
                    messageNoticeEntity.setNoticeSubtitle(noticeManageEntity.getNoticeSubtitle());
                }
                messageNoticeEntity.setJumpUrl(noticeManageEntity.getJumpUrl());
                messageNoticeEntity.setUserPhones(userPhone);
                messageNoticeEntity.setStatus(0);
                // messageNoticeEntity.setSourceId();//
                // messageNoticeEntity.setOrderNo();//
                messageNoticeEntity.setUserId(userId);
                messageNoticeEntity.setUserType(1);//
                messageNoticeEntity.setAddTime(new Date());
                messageNoticeEntity.setAddUser(account);
                if(noticeManageEntity.getNoticePosition().contains("2")){
                    messageNoticeEntity.setIsAlert(0);
                }
                messageNoticeEntities.add(messageNoticeEntity);
                //messageNoticeMapper.insert(messageNoticeEntity);
            }
        }

        if (Objects.equals(2, noticeManageEntity.getNoticeType())) {
            if (messageNoticeEntities.size() > 0) {
                messageNoticeMapper.addBatch(messageNoticeEntities);
            }
        }
        noticeManageEntity.setSendStatus(1);
        mapper.updateByPrimaryKey(noticeManageEntity);
        return true;
    }

    @Override
    public List<NoticeManageListVO> getPublishedList(String oemCode) {
        return mapper.getPublishedList(oemCode);
    }

    @Override
    public NoticeManageEntity getDetail(String oemCode, Long id) {
        return mapper.getDetail(oemCode, id);
    }

    @Override
    public Workbook getUserFile(String fileName) {
        if (StringUtil.isEmpty(fileName)) {
            throw new BusinessException("文件名不能为空");
        }
        DictionaryEntity endPoint = dictionaryService.getByCode("oss_endpoint");
        DictionaryEntity accessKeyId = dictionaryService.getByCode("oss_accessKeyId");
        DictionaryEntity accessKeySecret = dictionaryService.getByCode("oss_accessKeySecret");
        DictionaryEntity bucketName = dictionaryService.getByCode("oss_privateBucketName");

        Workbook wb = OssUtil.downloudExcel(endPoint.getDictValue(), accessKeyId.getDictValue(), accessKeySecret.getDictValue(), bucketName.getDictValue(), fileName);
        if (null == wb) {
            throw new BusinessException("获取文件失败");
        }
        return wb;
    }
}

