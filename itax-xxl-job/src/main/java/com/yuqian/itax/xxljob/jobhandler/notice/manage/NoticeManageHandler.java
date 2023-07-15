package com.yuqian.itax.xxljob.jobhandler.notice.manage;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.message.entity.NoticeManageEntity;
import com.yuqian.itax.message.service.NoticeManageService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 消息管理定时发送 每分钟执行任务
 * @author：hz
 * @Date：2020/03/09
 * @version：1.0
 */
@JobHandler(value="noticeManageHandler")
@Component
public class NoticeManageHandler extends IJobHandler {

    @Autowired
    NoticeManageService noticeManageService;
    @Autowired
    MemberAccountService memberAccountService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long t1 = System.currentTimeMillis(); // 代码执行前时间
        XxlJobLogger.log("=========消息管理定时发送任务启动");
        List<Map<String ,Object>> mapList=new ArrayList<>();

//        NoticeManageEntity entity=new NoticeManageEntity();
//        entity.setSendStatus(0);
//         List<NoticeManageEntity> list =noticeManageService.select(entity);

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Example example=new Example(NoticeManageEntity.class);
        example.createCriteria().andLessThanOrEqualTo("sendTime",sdf.format(new Date()))
                .andEqualTo("sendStatus",0)
                .andEqualTo("releaseWay",2);
        List<NoticeManageEntity> list =noticeManageService.selectByExample(example);
        NoticeManageEntity noticeManageEntity = null;
        for(int i=0;i<list.size();i++){
            noticeManageEntity=list.get(i);
            if(noticeManageEntity.getNoticeType().intValue() == 3){
                noticeManageEntity.setSendStatus(1);
                noticeManageEntity.setUpdateTime(new Date());
                noticeManageService.editByIdSelective(noticeManageEntity);
            }else {
                mapList=new ArrayList<>();
                String[] users = null;
                if (noticeManageEntity.getNoticeObj() == 2) {
                    users = noticeManageEntity.getUserPhones().trim().split(",");
                    for (int j = 0; j < users.length; j++) {
                        String phone = users[j];
                        MemberAccountEntity memberAccountEntity = memberAccountService.queryByAccount(phone, noticeManageEntity.getOemCode());
                        if (null == memberAccountEntity) {
                            continue;
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put("phone", memberAccountEntity.getMemberPhone());
                        map.put("userId", memberAccountEntity.getId());
                        mapList.add(map);
                    }
                }
                if (noticeManageEntity.getNoticeObj() == 1) {
                    List<MemberAccountEntity> memberAccountEntityList = memberAccountService.queryMemberByStatus(noticeManageEntity.getOemCode());

                    for (MemberAccountEntity memberAccountEntity : memberAccountEntityList) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("phone", memberAccountEntity.getMemberPhone());
                        map.put("userId", memberAccountEntity.getId());
                        mapList.add(map);
                    }
                }

                //发送
                if(mapList != null && mapList.size() > 0) {
                    noticeManageService.sendNotice(noticeManageEntity, noticeManageEntity.getAddUser(), mapList);
                }
            }
        }
        XxlJobLogger.log("=========消息管理定时发送任务结束");
        long t2 = System.currentTimeMillis(); // 代码执行后时间
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t2 - t1);
        XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
        return SUCCESS;
    }
}
