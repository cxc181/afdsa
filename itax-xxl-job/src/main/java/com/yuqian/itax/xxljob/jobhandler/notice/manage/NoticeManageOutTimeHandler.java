package com.yuqian.itax.xxljob.jobhandler.notice.manage;


import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.message.entity.NoticeManageEntity;
import com.yuqian.itax.message.entity.query.NoticeManageQuery;
import com.yuqian.itax.message.service.NoticeManageService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 消息管理(悬浮公告)定时下线 每分钟执行任务
 * @author：hz
 * @Date：2020/10/21
 * @version：1.0
 */
@JobHandler(value="noticeManageOutTimeHandler")
@Component
public class NoticeManageOutTimeHandler extends IJobHandler {

    @Autowired
    NoticeManageService noticeManageService;
    @Autowired
    MemberAccountService memberAccountService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long t1 = System.currentTimeMillis(); // 代码执行前时间
        XxlJobLogger.log("=========消息管理(悬浮公告)定时下线发送任务启动");
        List<Map<String ,Object>> mapList=new ArrayList<>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Example example=new Example(NoticeManageEntity.class);
        example.createCriteria().andLessThanOrEqualTo("outTime",sdf.format(new Date()));
        List<NoticeManageEntity> list =noticeManageService.selectByExample(example);
        for(int i=0;i<list.size();i++){
            NoticeManageEntity noticeManageEntity=list.get(i);
            noticeManageEntity.setSendStatus(4);
            noticeManageEntity.setUpdateTime(new Date());
            noticeManageService.editByIdSelective(noticeManageEntity);
        }
        XxlJobLogger.log("=========消息管理(悬浮公告)定时下线任务结束");
        long t2 = System.currentTimeMillis(); // 代码执行后时间
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t2 - t1);
        XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
        return SUCCESS;
    }
}